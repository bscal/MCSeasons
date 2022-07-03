package me.bscal.seasons.common.seasons;

import me.bscal.seasons.Seasons;
import me.bscal.seasons.common.Config;
import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.configurate.ConfigurationOptions;

import java.io.File;

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

    public static void tryLoadFromConfig()
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
    }

}
