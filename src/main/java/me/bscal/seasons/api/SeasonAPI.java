package me.bscal.seasons.api;

import me.bscal.seasons.common.seasons.SeasonDate;
import me.bscal.seasons.common.seasons.SeasonState;
import me.bscal.seasons.common.seasons.SeasonTimer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public final class SeasonAPI
{
	private SeasonAPI() {}

	public static SeasonState getSeason()
	{
		return SeasonTimer.GetOrCreate().getGenericSeason();
	}

	public static SeasonState getSeason(Identifier biomeId)
	{
		return SeasonTimer.GetOrCreate().getSeason(biomeId);
	}

	public static SeasonState getSeason(RegistryKey<Biome> biomeKey)
	{
		return getSeason(biomeKey.getValue());
	}

	public static SeasonState getSeason(Biome biome, World world)
	{
		var biomeKey = world.getRegistryManager().get(Registry.BIOME_KEY).getKey(biome);
		return biomeKey.isPresent() ? getSeason(biomeKey.get().getValue()) : getSeason();
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

	public static void addDays(int days) { SeasonTimer.GetOrCreate().addDays(days); }

	public static void addTicks(long ticks)
	{
		SeasonTimer.GetOrCreate().addTicks(ticks);
	}

	public static void setSeason(int seasonTrackerId)
	{
		SeasonTimer.GetOrCreate().setSeason(seasonTrackerId);
	}

}
