package me.bscal.seasons.common.seasons;

import me.bscal.seasons.Seasons;

import java.util.EnumMap;

public class SeasonBiomeClimate
{
    public final SeasonStats BaseStats;
    public SeasonStats FinalStats;
    public final EnumMap<Season, SeasonTypes> SeasonTypePerSeason;
    private final SeasonStats m_WorldStatsReference;

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
        m_WorldStatsReference = SeasonWorld.getOrCreate(Seasons.Instance.getServer().getOverworld()).WorldStats;
    }

    public void updateSeason(Season season)
    {
        var seasonalStats = SeasonTypePerSeason.get(season);
        FinalStats.zero();
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

    public float getTemperature()
    {
        return FinalStats.Temperature + m_WorldStatsReference.Temperature;
    }

    public float getRainfall() { return FinalStats.Rainfall + m_WorldStatsReference.Rainfall; }

}
