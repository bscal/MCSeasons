package me.bscal.seasons.common.seasons;

public class SeasonStatsGlobals
{

    public static final SeasonStats SPRING = new SeasonStats();
    public static final SeasonStats SUMMER = new SeasonStats();
    public static final SeasonStats AUTUMN = new SeasonStats();
    public static final SeasonStats WINTER = new SeasonStats();
    public static final SeasonStats WET = new SeasonStats();
    public static final SeasonStats DRY = new SeasonStats();
    public static final SeasonStats EXTREME_SUMMER = new SeasonStats();
    public static final SeasonStats EXTREME_WINTER = new SeasonStats();

    static
    {
        SPRING.Temperature = 15.0f;
        SUMMER.Temperature = 26.0f;
        AUTUMN.Temperature = 15.0f;
        WINTER.Temperature = 1.0f;

        WET.Temperature = 27.0f;
        DRY.Temperature = 27.0f;

        EXTREME_SUMMER.Temperature = 34.0f;
        EXTREME_WINTER.Temperature = -3.0f;
    }

/*    public static void tryLoadFromConfig()
    {
        int updated = 0;
        updated += getSeasonStatsConfig(SPRING, SeasonTypes.Spring.Name);
        updated += getSeasonStatsConfig(SUMMER, SeasonTypes.Summer.Name);
        updated += getSeasonStatsConfig(AUTUMN, SeasonTypes.Autumn.Name);
        updated += getSeasonStatsConfig(WINTER, SeasonTypes.Winter.Name);
        updated += getSeasonStatsConfig(WET, SeasonTypes.Wet.Name);
        updated += getSeasonStatsConfig(DRY, SeasonTypes.Dry.Name);
        updated += getSeasonStatsConfig(EXTREME_SUMMER, SeasonTypes.ExtremeSummer.Name);
        updated += getSeasonStatsConfig(EXTREME_WINTER, SeasonTypes.ExtremeWinter.Name);
        Seasons.LOGGER.info("Loaded " + updated + " season stat files");
    }

    public static int getSeasonStatsConfig(SeasonStats outStats, String filename)
    {
        var file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "/seasons/" + filename + ".conf");
        if (!file.exists()) return 0;
        var options = ConfigurationOptions.defaults()
                .shouldCopyDefaults(true);
        var config = new Config<>(outStats, file, options);
        config.load();
        return 1;
    }*/

}
