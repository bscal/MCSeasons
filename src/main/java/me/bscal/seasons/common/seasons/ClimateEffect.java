package me.bscal.seasons.common.seasons;

public interface ClimateEffect
{

    void processEffect(SeasonalModifiers modifiers, SeasonBiomeClimate climate, Season season);

    public static final ClimateEffect MILD = new ClimateEffect()
    {
        @Override
        public void processEffect(SeasonalModifiers modifiers, SeasonBiomeClimate climate, Season season)
        {

        }
    };



}
