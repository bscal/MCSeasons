package me.bscal.seasons.common.seasons;

import java.util.EnumMap;

public class SeasonBiomeClimate
{
    public final SeasonStats BaseStats;
    public SeasonStats FinalStats;
    public final EnumMap<Season, SeasonTypes> SeasonTypePerSeason;

    public SeasonBiomeClimate(SeasonTypes spring, SeasonTypes summer, SeasonTypes fall, SeasonTypes winter)
    {
        BaseStats = new SeasonStats();
        FinalStats = new SeasonStats();
        SeasonTypePerSeason = new EnumMap<>(Season.class);
        SeasonTypePerSeason.put(Season.Spring, spring);
        SeasonTypePerSeason.put(Season.Summer, summer);
        SeasonTypePerSeason.put(Season.Autumn, fall);
        SeasonTypePerSeason.put(Season.Winter, winter);
    }

    public void updateSeason(Season season)
    {
        var seasonalStats = SeasonTypePerSeason.get(season);
        FinalStats.Combine(BaseStats, seasonalStats.SeasonStats);
    }

    public boolean isTropical()
    {
        for (var seasonType : SeasonTypePerSeason.values())
        {
            if (seasonType == SeasonTypes.Dry || seasonType == SeasonTypes.Wet)
                return true;
        }
        return false;
    }

}
