package me.bscal.seasons.api;

import me.bscal.seasons.Seasons;
import me.bscal.seasons.common.Config;
import me.bscal.seasons.common.seasons.Season;
import me.bscal.seasons.common.seasons.SeasonTimer;
import me.bscal.seasons.common.seasons.SeasonTypes;
import net.minecraft.server.world.ServerWorld;

/**
 * Small class containing util functions for seasons
 */
public final class SeasonAPI
{

    /**
     * Shortcut for SeasonTimer.getSeason()
     */
    public static Season getSeason()
    {
        return SeasonTimer.get().CurrentSeason;
    }

    /**
     * Works exactly like /time add [time].
     */
    public static void addTime(long time)
    {
        if (time < 1) throw new IllegalArgumentException("time must be greater than 0");

        var server = Seasons.Instance.getServer();
        if (server == null) return;
        for (ServerWorld serverWorld : server.getWorlds())
        {
            serverWorld.setTimeOfDay(serverWorld.getTimeOfDay() + time);
        }
    }

    public static SeasonTypes forGenericSeason()
    {
        switch (getSeason())
        {
            case Spring ->
            {
                return SeasonTypes.Spring;
            }
            case Summer ->
            {
                return SeasonTypes.Summer;
            }
            case Autumn ->
            {
                return SeasonTypes.Autumn;
            }
            default ->
            {
                return SeasonTypes.Winter;
            }
        }
    }
}
