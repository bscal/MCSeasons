package me.bscal.seasons.common.seasons;

import io.netty.buffer.Unpooled;
import me.bscal.seasons.api.SeasonAPI;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import org.apache.commons.lang3.SerializationUtils;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class SeasonWorld
{

    public SeasonStats WorldStats;
    public transient List<ClimateEffect> SeasonalEffects;
    public EnumSet<ClimateEffectType> ActiveEffects;
    public EnumSet<ClimateEffectCategory> ActiveCategories;
    public int GlobalWarmingLevel;

    public SeasonWorld(ClimateEffect... effects)
    {
        WorldStats = new SeasonStats();
        SeasonalEffects = List.of(effects);
        ActiveEffects = EnumSet.allOf(ClimateEffectType.class);
        ActiveCategories = EnumSet.allOf(ClimateEffectCategory.class);
    }

    public SeasonWorld()
    {
    }

/*    public static SeasonWorld getOrCreate(ServerWorld world)
    {
        world.getPersistentStateManager().getOrCreate((nbtCompound -> {
            SeasonWorld seasonWorld = new SeasonWorld();
            seasonWorld.WorldStats = SerializationUtils.deserialize()
        }))
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(64);
        SerializationUtils.serialize(WorldStats, baos);
        SerializationUtils.serialize(ActiveEffects, baos);
        SerializationUtils.serialize(ActiveCategories, baos);
        SerializationUtils.serialize(GlobalWarmingLevel, baos);
        nbt.putByteArray("SeasonWorld_Data", baos.toByteArray());
        return nbt;
    }*/

    public void updateSeasonalEffects()
    {
        var season = SeasonAPI.getSeason();

        Collections.shuffle(SeasonalEffects);
        for (var modifier : SeasonalEffects)
        {
            if (modifier.processEffect(this, season))
            {

            }
        }
    }

    public void removeEffect(ClimateEffect effect)
    {
        var type = effect.getType();
        if (!ActiveEffects.remove(type)) return;
        boolean containsCategory = false;
        for (var activeType : ActiveEffects)
        {
            if (activeType.Category == type.Category)
            {
                containsCategory = true;
                break;
            }
        }
        if (containsCategory) return;
        ActiveCategories.remove(type.Category);
    }


    public void resetEffects()
    {
        WorldStats.zero();
        ActiveEffects.clear();
        ActiveCategories.clear();
    }

/*    public NbtCompound Serialize()
    {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        buffer.writeBitSet();
    }

    public static SeasonWorld Deserialize(NbtCompound nbt)
    {
        SeasonWorld world = new SeasonWorld();
    }*/

}
