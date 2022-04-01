package me.bscal.seasons.common.seasons;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectImmutableList;

/**
 * Enum that contains data on how a biome should handle itself depending on what the internal seasonal id it is.
 * For example: 0 = Spring, 1 = Summer, 2 = Fall
 */
public enum SeasonalType
{
	FourSeasonPerYear(ImmutableList.of(Season.Spring, Season.Summer, Season.Autumn, Season.Winter)),
	TropicalSeason(ImmutableList.of(Season.Wet, Season.Wet, Season.Dry, Season.Dry)),
	HarshWinter(ImmutableList.of(Season.Autumn, Season.Winter, Season.IceAge, Season.Winter)),
	SummerOnly(ImmutableList.of(Season.Summer, Season.Summer, Season.Summer, Season.Summer));

	public final ImmutableList<Season> Seasons;

	SeasonalType(ImmutableList<Season> seasonalSections)
	{

		Seasons = seasonalSections;
	}

	public Season getSeason(int internalId)
	{
		int clampedSection = Math.max(0, Math.min(Seasons.size(), internalId));
		return Seasons.get(clampedSection);
	}
}
