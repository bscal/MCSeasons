package me.bscal.seasons.common.seasons;

import me.bscal.seasons.Seasons;
import me.bscal.seasons.api.SeasonAPI;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import org.apache.commons.lang3.SerializationUtils;

import java.io.ByteArrayOutputStream;
import java.util.*;

public class SeasonWorld extends PersistentState
{

    public static final String STATE_NAME = Seasons.MOD_ID + ":season_world";

    public SeasonStats WorldStats;
    public transient List<ClimateEffectType> SeasonalEffects;
    public EnumMap<ClimateEffectType, Integer> ActiveEffects;
    public EnumSet<ClimateEffectCategory> ActiveCategories;
    public int GlobalWarmingLevel;
    private boolean m_SkipNextEffects;

    public SeasonWorld(ClimateEffectType... effects)
    {
        WorldStats = new SeasonStats();
        SeasonalEffects = List.of(effects);
        ActiveEffects = new EnumMap<>(ClimateEffectType.class);
        ActiveCategories = EnumSet.allOf(ClimateEffectCategory.class);
        m_SkipNextEffects = true;
    }

    private SeasonWorld()
    {
    }

    public void updateSeasonalEffects()
    {
        var season = SeasonAPI.getSeason();
        for (var biomeClimate : SeasonClimateManager.BIOME_TO_CLIMATE.values())
        {
            biomeClimate.updateSeason(season);
        }
    }

    // TODO
    public void updateDailyEffects(int daysLeftInSeason)
    {
        var iter = ActiveEffects.entrySet().iterator();
        while (iter.hasNext())
        {
            var pair = iter.next();
            var type = pair.getKey();
            if (type.Effect.updateEffect(this, pair.getValue(), daysLeftInSeason))
            {
                type.Effect.removeEffect(this);
                iter.remove();
            }
        }

        var season = SeasonAPI.getSeason();
        Collections.shuffle(SeasonalEffects);
        for (var effect : SeasonalEffects)
        {
            if (effect.Effect.canApply(this, season))
            {
                effect.Effect.applyEffect(this);
            }
        }
    }

    public void addEffect(ClimateEffectType type, boolean force)
    {
        if (force || type.Effect.canApply(this, SeasonAPI.getSeason()))
        {
            type.Effect.applyEffect(this);
            ActiveEffects.put(type, 0);
            ActiveCategories.add(type.Effect.getCategory());
        }
    }

    public void removeEffect(ClimateEffectType type)
    {
        if (ActiveEffects.remove(type) == null) return;
        type.Effect.removeEffect(this);
        boolean containsCategory = false;
        for (var activeType : ActiveEffects.keySet())
        {
            if (activeType.Effect.getCategory() == type.Effect.getCategory())
            {
                containsCategory = true;
                break;
            }
        }
        if (containsCategory) return;
        ActiveCategories.remove(type.Effect.getCategory());
    }

    public void resetEffects()
    {
        WorldStats.zero();
        ActiveEffects.clear();
        ActiveCategories.clear();
    }

    public static SeasonWorld getOrCreate(ServerWorld world)
    {
        return world.getPersistentStateManager().getOrCreate((nbt) ->
        {
            byte[] data = nbt.getByteArray("SeasonWorldData");
            Objects.requireNonNull(data);
            assert data.length > 0: "data is null or empty!";

            SeasonWorld seasonWorld = new SeasonWorld();
            seasonWorld.WorldStats = SerializationUtils.deserialize(data);
            seasonWorld.ActiveEffects = SerializationUtils.deserialize(data);
            seasonWorld.ActiveCategories = SerializationUtils.deserialize(data);
            seasonWorld.GlobalWarmingLevel = SerializationUtils.deserialize(data);
            seasonWorld.m_SkipNextEffects = SerializationUtils.deserialize(data);

            assert seasonWorld.WorldStats != null: "SeasonWorld is null after deserialization!";
            assert seasonWorld.ActiveEffects != null: "ActiveEffects is null after deserialization!";
            assert seasonWorld.ActiveCategories != null: "ActiveCategories is null after deserialization!";

            return seasonWorld;
        }, () -> new SeasonWorld(ClimateEffectType.Mild), STATE_NAME);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(64);
        SerializationUtils.serialize(WorldStats, baos);
        SerializationUtils.serialize(ActiveEffects, baos);
        SerializationUtils.serialize(ActiveCategories, baos);
        SerializationUtils.serialize(GlobalWarmingLevel, baos);
        SerializationUtils.serialize(m_SkipNextEffects, baos);
        nbt.putByteArray("SeasonWorldData", baos.toByteArray());
        return nbt;
    }

}
