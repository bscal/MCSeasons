package me.bscal.seasons.common.seasons;

public enum SeasonTypes
{
    Spring("Spring", SeasonStatsGlobals.SPRING, new SeasonalModifiers()),
    Summer("Summer", SeasonStatsGlobals.SUMMER, new SeasonalModifiers()),
    Autumn("Autumn", SeasonStatsGlobals.AUTUMN, new SeasonalModifiers()),
    Winter("Winter", SeasonStatsGlobals.WINTER, new SeasonalModifiers()),
    Wet("Wet", SeasonStatsGlobals.WET, new SeasonalModifiers()),
    Dry("Dry", SeasonStatsGlobals.DRY, new SeasonalModifiers()),
    ExtremeSummer("Extreme Summer", SeasonStatsGlobals.EXTREME_SUMMER, new SeasonalModifiers()),
    ExtremeWinter("Extreme Winter", SeasonStatsGlobals.EXTREME_WINTER, new SeasonalModifiers());

    public final String Name;
    public final SeasonStats SeasonStats;
    public final SeasonalModifiers SeasonModifiers;

    SeasonTypes(String name, SeasonStats stats, SeasonalModifiers seasonalModifiers)
    {
        Name = name;
        SeasonStats = stats;
        SeasonModifiers = seasonalModifiers;
    }

    public static SeasonTypes forGenericSeason()
    {
        switch (Season.getSeason())
        {
            case Spring ->
            {
                return Spring;
            }
            case Summer ->
            {
                return Summer;
            }
            case Autumn ->
            {
                return Autumn;
            }
            default ->
            {
                return Winter;
            }
        }
    }
}
