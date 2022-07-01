package me.bscal.seasons.api;

import me.bscal.seasons.Seasons;
import me.bscal.seasons.common.seasons.*;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Objects;

/**
 * A small and basic API for Seasons mod. This should allow you to integrate into Seasons without having to rely on Seasons itself.
 * Mostly just a wrapper around Seasons
 */
public final class SeasonAPI
{

	/**
	 * Returns the current <code>Season</code>. Spring, Summer, Autumn, or Winter. <code>Season</code> is the global state, and can be used to find a
	 * biomes current <code>SeasonType</code>. ie. jungle's Spring = Wet, plain's Winter = Winter
	 */
	public static Season getSeason()
	{
		return Season.getSeason();
	}

	/**
	 *	Returns a SeasonBiome containing info on climate for a biome. Will use <code>SeasonBiomeClimate.GENERIC</code> by default.
	 */
	public static SeasonBiomeClimate getSeasonBiome(Biome biome)
	{
		return BiomeToSeasonMapper.BiomesToSeason.getOrDefault(biome, SeasonBiomeClimates.GENERIC);
	}

	/**
	 * Returns the <code>SeasonType</code> for a biome. All biomes represent a <code>Season</code> but can handle seasons different.
	 * Jungles for instance could have: Wet, Wet, Dry, Dry as their seasons. Will use <code>SeasonBiomeClimate.GENERIC</code> by default.
	 */
	public static SeasonTypes getSeasonByBiome(Biome biome)
	{
		Objects.requireNonNull(biome, "biome was null");
		return BiomeToSeasonMapper.getSeasonType(biome);
	}

	public static SeasonTypes getSeasonByEntity(Entity entity)
	{
		Objects.requireNonNull(entity, "entity must not be null");
		return getSeasonByBiome(SeasonAPIUtils.getBiomeFromEntity(entity));
	}

	public static SeasonTypes getSeasonById(Identifier id, World world)
	{
		Objects.requireNonNull(id, "id must not be null");
		Objects.requireNonNull(world, "world must not be null");
		var biome = SeasonAPIUtils.getBiomeFromId(id, world);
		if (biome.isEmpty()) return SeasonTypes.forGenericSeason();
		return getSeasonByBiome(biome.get());
	}

	public static SeasonDate getDate()
	{
		return SeasonTimer.getOrCreate().getDate();
	}

	/**
	 * Returns the time of day based upon season time.
	 */
	public static long getTimeOfDay()
	{
		return SeasonTimer.getOrCreate().getCurrentTicks() % Seasons.ServerConfig.Settings.TicksPerDay;
	}

	public static long getCurrentTick()
	{
		return SeasonTimer.getOrCreate().getCurrentTicks();
	}

	public static long getTotalTicks()
	{
		return SeasonTimer.getOrCreate().getTotalTicks();
	}

	/**
	 * Adds a number of days. This does not progress the time of worlds like /time command.
	 */
	public static void addDays(int days)
	{
		if (days < 1) throw new IllegalArgumentException("days must be greater than 0");

		SeasonTimer.getOrCreate().addDays(days);
	}

	/**
	 * Works exactly like /time add [time].
	 */
	public static void addTime(long time)
	{
		if (time < 1) throw new IllegalArgumentException("time must be greater than 0");

		var server = Seasons.Instance.getServer();
		if (server == null) return;
		for (ServerWorld serverWorld : server.getWorlds())
		{
			serverWorld.setTimeOfDay(serverWorld.getTimeOfDay() + time);
		}
	}

	/**
	 * A naive approach to settings the season based on Season. Only Spring, Summer, Autumn, and Winter will change the season
	 */
	public static void setSeason(Season season)
	{
		Objects.requireNonNull(season, "season is null");
		SeasonTimer.getOrCreate().setSeason(season.ordinal());
	}

	/**
	 * Adds a Biome to SeasonBiome entry into BiomeToSeasonMapper
	 * @param shouldOverride - Should entry replace an existing entry.
	 * @return - true if added
	 */
	public static boolean registerBiomeWithSeasons(Biome biome, SeasonBiomeClimate seasonBiome, boolean shouldOverride)
	{
		Objects.requireNonNull(biome, "biome cannot be null.");
		Objects.requireNonNull(seasonBiome, "seasonBiome cannot be null.");

		if (!shouldOverride)
			return BiomeToSeasonMapper.BiomesToSeason.putIfAbsent(biome, seasonBiome) == null;

		BiomeToSeasonMapper.register(biome, seasonBiome);
		return true;
	}
}
