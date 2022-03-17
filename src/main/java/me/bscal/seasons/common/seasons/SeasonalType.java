package me.bscal.seasons.common.seasons;

/**
 * Enum that contains data on how a biome should handle itself depending on what the internal seasonal id it is.
 * For example: 0 = Spring, 1 = Summer, 2 = Fall
 */
public enum SeasonalType
{
	FourSeasonPerYear(new Season[]{ Season.Spring, Season.Summer, Season.Autumn, Season.Winter }),
	TropicalSeason(new Season[]{ Season.Wet, Season.Wet, Season.Dry, Season.Dry }),
	HarshWinter(new Season[]{ Season.Autumn, Season.Winter, Season.IceAge, Season.Winter }),
	SummerOnly(new Season[]{ Season.Summer, Season.Summer, Season.Summer, Season.Summer });

	public final Season[] Seasons;

	SeasonalType(Season[] seasonalSections)
	{
		Seasons = seasonalSections;
	}

	public Season getSeason(int seasonSection)
	{
		int clampedSection = Math.max(0, Math.min(Seasons.length, seasonSection));
		return Seasons[clampedSection];
	}
}
