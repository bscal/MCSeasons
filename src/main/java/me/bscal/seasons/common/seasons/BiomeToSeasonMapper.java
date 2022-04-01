package me.bscal.seasons.common.seasons;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.Identifier;

public class BiomeToSeasonMapper
{

	public static final Object2ObjectOpenHashMap<Identifier, SeasonalType> BiomesToSeason;

	static
	{
		BiomesToSeason = new Object2ObjectOpenHashMap<>();
	}

	public static SeasonalType getSeasonalType(Identifier id)
	{
		return BiomesToSeason.getOrDefault(id, SeasonalType.FourSeasonPerYear);
	}

}
