package me.bscal.seasons.common.seasons;

public enum SeasonTypes
{
    Spring("Spring", SeasonStatsGlobals.SPRING),
    Summer("Summer", SeasonStatsGlobals.SUMMER),
    Autumn("Autumn", SeasonStatsGlobals.AUTUMN),
    Winter("Winter", SeasonStatsGlobals.WINTER),
    Wet("Wet", SeasonStatsGlobals.WET),
    Dry("Dry", SeasonStatsGlobals.DRY),
    ExtremeSummer("Extreme Summer", SeasonStatsGlobals.EXTREME_SUMMER),
    ExtremeWinter("Extreme Winter", SeasonStatsGlobals.EXTREME_WINTER);

    public final String Name;
    public final SeasonStats SeasonStats;

    SeasonTypes(String name, SeasonStats stats)
    {
        Name = name;
        SeasonStats = stats;
    }
}
