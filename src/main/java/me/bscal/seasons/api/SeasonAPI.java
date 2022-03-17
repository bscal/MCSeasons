package me.bscal.seasons.api;

import me.bscal.seasons.Seasons;
import me.bscal.seasons.common.seasons.SeasonDate;
import me.bscal.seasons.common.seasons.Season;
import me.bscal.seasons.common.seasons.SeasonTimer;
import me.bscal.seasons.common.seasons.SeasonalType;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public final class SeasonAPI
{
	public static Identifier getBiomeId(Biome biome, World world)
	{
		return world.getRegistryManager().get(Registry.BIOME_KEY).getId(biome);
	}

	public static Season getSeason()
	{
		return SeasonTimer.GetOrCreate().getGenericSeason();
	}

	public static Season getSeasonByBiome(Entity entity)
	{
		return getSeasonByBiome(getBiomeId(entity.world.getBiome(entity.getBlockPos()).value(), entity.world));
	}

	public static Season getSeasonByBiome(Identifier biomeId)
	{
		return SeasonTimer.GetOrCreate().getSeason(biomeId);
	}

	public static Season getSeasonByBiome(RegistryKey<Biome> biomeKey)
	{
		return getSeasonByBiome(biomeKey.getValue());
	}

	public static Season getSeasonByBiome(Biome biome, World world)
	{
		var biomeKey = world.getRegistryManager().get(Registry.BIOME_KEY).getKey(biome);
		return biomeKey.isPresent() ? getSeasonByBiome(biomeKey.get().getValue()) : getSeason();
	}

	public static SeasonalType getSeasonalType(Identifier biomeId)
	{
		return Seasons.getSettings().getSeasonType(biomeId);
	}

	public static SeasonalType getSeasonalType(Biome biome, World world)
	{
		var biomeKey = world.getRegistryManager().get(Registry.BIOME_KEY).getKey(biome);
		return biomeKey.isPresent() ? Seasons.getSettings().getSeasonType(biomeKey.get().getValue()) : SeasonalType.FourSeasonPerYear;
	}

	public static SeasonDate getDate()
	{
		return SeasonTimer.GetOrCreate().getDate();
	}

	public static long getTimeOfDay()
	{
		return SeasonTimer.GetOrCreate().getCurrentTicks();
	}

	public static long getTotalTicks()
	{
		return SeasonTimer.GetOrCreate().getTotalTicks();
	}

	public static void addDays(int days)
	{
		SeasonTimer.GetOrCreate().addDays(days);
	}

	public static void setSeason(int seasonTrackerId)
	{
		SeasonTimer.GetOrCreate().setSeason(seasonTrackerId);
	}

	public static int getInternalSeasonId()
	{
		return SeasonTimer.GetOrCreate().getInternalSeasonId();
	}

}
