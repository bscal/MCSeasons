package me.bscal.seasons.common;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public final class SeasonSettings
{
	public static int TicksPerDay;
	public static int DaysPerMonth;
	public static int MonthsPerYear;
	public static int MonthsPerSeason;
	public static int MaxSeasons;

	public static Object2ObjectOpenHashMap<Identifier, SeasonTypes> BiomeToSeasonType;

	public static SeasonTypes getSeasonType(Identifier biomeId)
	{
		return BiomeToSeasonType.getOrDefault(biomeId, SeasonTypes.FourSeasonPerYear);
	}

	public static SeasonTypes getSeasonType(RegistryKey<Biome> biomeKey)
	{
		return getSeasonType(biomeKey.getValue());
	}

	public static void setDefaults()
	{
		TicksPerDay = 24000;
		DaysPerMonth = 30;
		MonthsPerYear = 12;
		MonthsPerSeason = 3;
		MaxSeasons = 4;
		BiomeToSeasonType = new Object2ObjectOpenHashMap<>();
		BiomeToSeasonType.put(BiomeKeys.BAMBOO_JUNGLE.getValue(), SeasonTypes.TropicalSeason);
	}

	private SeasonSettings()
	{
	}
}
