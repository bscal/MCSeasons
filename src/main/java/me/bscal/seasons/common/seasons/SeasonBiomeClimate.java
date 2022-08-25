package me.bscal.seasons.common.seasons;

import me.bscal.seasons.Seasons;

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

        assert Seasons.Instance.getServer().getOverworld() != null: "Overworld cannot be found!";
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
