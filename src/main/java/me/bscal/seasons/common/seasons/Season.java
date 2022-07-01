package me.bscal.seasons.common.seasons;

public enum Season
{

    Spring,
    Summer,
    Autumn,
    Winter;

    public static final int MAX_SEASONS = 4;
    public static final int MAX_SEASON_ID = MAX_SEASONS - 1;

    public static Season getSeason()
    {
        return Season.values()[SeasonTimer.getOrCreate().getInternalSeasonId()];
    }
}
