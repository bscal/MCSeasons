package me.bscal.seasons.common.seasons;

import static me.bscal.seasons.common.seasons.SeasonTypes.*;

public final class SeasonBiomeClimates
{


    public static final SeasonBiomeClimate GENERIC = new SeasonBiomeClimate(Spring, Summer, Autumn, Winter);
    public static final SeasonBiomeClimate JUNGLE = new SeasonBiomeClimate(Wet, Wet, Dry, Dry);
    public static final SeasonBiomeClimate DESERT = new SeasonBiomeClimate(ExtremeSummer, ExtremeSummer, Summer, Summer);
    public static final SeasonBiomeClimate ARTIC = new SeasonBiomeClimate(Winter, Winter, ExtremeWinter, ExtremeWinter);
    public static final SeasonBiomeClimate TUNDRA = new SeasonBiomeClimate(Spring, Summer, Autumn, ExtremeWinter);
    public static final SeasonBiomeClimate SAVANNAH = new SeasonBiomeClimate(Spring, ExtremeSummer, Autumn, Winter);

}
