package me.bscal.seasons.common.seasons.effects;

import me.bscal.seasons.Seasons;
import me.bscal.seasons.common.seasons.*;

public class TemperatureEffects implements ClimateEffect
{


    @Override
    public boolean canApply(SeasonWorld seasonWorld, Season season)
    {
        return sameTypeOrCategory(seasonWorld);
    }

    @Override
    public void applyEffect(SeasonWorld seasonWorld)
    {
        seasonWorld.WorldStats.Temperature += 3f;
    }

    @Override
    public void removeEffect(SeasonWorld seasonWorld)
    {
        seasonWorld.WorldStats.Temperature -= 3f;
    }

    @Override
    public boolean updateEffect(SeasonWorld seasonWorld, int duration, int daysLeftInSeason)
    {
        return duration >= getDurationInDays() && Seasons.Random.nextInt(6) == 0;
    }

    @Override
    public int getDurationInDays()
    {
        return 12;
    }

    @Override
    public ClimateEffectType getType()
    {
        return ClimateEffectType.Mild;
    }

    @Override
    public ClimateEffectCategory getCategory()
    {
        return ClimateEffectCategory.Temperature;
    }
}
