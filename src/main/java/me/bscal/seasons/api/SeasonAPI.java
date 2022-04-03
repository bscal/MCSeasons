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
	 * Returns the global default season. Will always be Spring, Summer, Autumn, or Summer
	 */
	public static Season getSeason()
	{
		return SeasonTimer.getOrCreate().getGenericSeason();
	}

	/**
	 * Returns the seasons based on the biome.
	 */
	public static Season getSeasonByBiome(Biome biome)
	{
		Objects.requireNonNull(biome, "biome was null");
		return SeasonTimer.getOrCreate().getSeason(biome);
	}

	public static Season getSeasonByEntity(Entity entity)
	{
		Objects.requireNonNull(entity, "entity must not be null");
		return getSeasonByBiome(SeasonAPIUtils.getBiomeFromEntity(entity));
	}

	public static Season getSeasonById(Identifier id, World world)
	{
		Objects.requireNonNull(id, "id must not be null");
		Objects.requireNonNull(world, "world must not be null");
		var biome = SeasonAPIUtils.getBiomeFromId(id, world);
		if (biome.isEmpty()) return getSeason();
		return getSeasonByBiome(biome.get());
	}

	/**
	 * Returns the SeasonType for the Biome. SeasonType specifies what seasons the biome has.
	 * The default SeasonType is FourSeasonsPerYear
	 */
	public static SeasonType getSeasonType(Biome biome)
	{
		Objects.requireNonNull(biome, "biome was null");
		return BiomeToSeasonMapper.getSeasonalType(biome);
	}

	/**
	 * Returns the current date
	 */
	public static SeasonDate getDate()
	{
		return SeasonTimer.getOrCreate().getDate();
	}

/*	public static void setDate(SeasonDate date)
	{
		Objects.requireNonNull(date, "date must not be null");
		SeasonTimer.getOrCreate().setDate(data);
	}*/

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
	 * seasonTrackerId will be clamped
	 */
	public static void setSeason(int seasonTrackerId)
	{
		SeasonTimer.getOrCreate().setSeason(seasonTrackerId);
	}

	/**
	 * A naive approach to settings the season based on Season. Only Spring, Summer, Autumn, and Winter will change the season
	 */
	public static void setSeason(Season season)
	{
		Objects.requireNonNull(season, "season is null");

		int newSeasonId;
		switch (season)
		{
		case Spring -> newSeasonId = 0;
		case Summer -> newSeasonId = 1;
		case Autumn -> newSeasonId = 2;
		case Winter -> newSeasonId = 3;
		default -> {
			return;
		}
		}
		setSeason(newSeasonId);
	}

	public static int getInternalSeasonId()
	{
		return SeasonTimer.getOrCreate().getInternalSeasonId();
	}

}
