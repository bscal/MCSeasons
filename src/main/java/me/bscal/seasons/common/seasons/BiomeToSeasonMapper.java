package me.bscal.seasons.common.seasons;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;

public final class BiomeToSeasonMapper
{

	public static final Reference2ObjectOpenHashMap<Biome, SeasonType> BiomesToSeason = new Reference2ObjectOpenHashMap<>();

	static
	{
		register(BuiltinRegistries.BIOME.get(new Identifier("jungle")), SeasonType.TropicalSeason);
	}

	public static void register(Biome biome, SeasonType seasonType)
	{
		BiomesToSeason.put(biome, seasonType);
	}

	public static SeasonType getSeasonalType(Biome biome)
	{
		return BiomesToSeason.getOrDefault(biome, SeasonType.FourSeasonPerYear);
	}

}
