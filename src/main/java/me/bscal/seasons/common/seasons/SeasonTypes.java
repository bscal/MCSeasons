package me.bscal.seasons.common;

public enum SeasonTypes
{
	FourSeasonPerYear(new SeasonState[]{ SeasonState.Spring, SeasonState.Summer, SeasonState.Autumn, SeasonState.Winter }),
	TropicalSeason(new SeasonState[]{ SeasonState.Wet, SeasonState.Wet, SeasonState.Dry, SeasonState.Dry }),
	HarshWinter(new SeasonState[]{ SeasonState.Autumn, SeasonState.Winter, SeasonState.IceAge, SeasonState.Winter }),
	SummerOnly(new SeasonState[]{ SeasonState.Summer, SeasonState.Summer, SeasonState.Summer, SeasonState.Summer });

	public final SeasonState[] Seasons;

	SeasonTypes(SeasonState[] seasonalSections)
	{
		Seasons = seasonalSections;
	}

	public SeasonState getSeason(int seasonSection)
	{
		int clampedSection = Math.max(0, Math.min(Seasons.length, seasonSection));
		return Seasons[clampedSection];
	}

}
