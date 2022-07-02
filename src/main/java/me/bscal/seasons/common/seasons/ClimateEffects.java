package me.bscal.seasons.common.seasons;

import me.bscal.seasons.Seasons;

public class ClimateEffects
{

    public static final ClimateEffect MILD = new ClimateEffect()
    {
        @Override
        public boolean processEffect(SeasonWorld seasonWorld, Season season)
        {
            if (sameTypeOrCategory(seasonWorld)) return false;
            if (Seasons.Random.nextInt(6) < 6) return false;

            seasonWorld.WorldStats.Temperature += Seasons.Random.nextFloat() * 3f + 3f;
            return true;
        }

        @Override
        public ClimateEffectType getType()
        {
            return ClimateEffectType.Mild;
        }
    };

    public static final ClimateEffect DRY = new ClimateEffect()
    {
        @Override
        public boolean processEffect(SeasonWorld seasonWorld, Season season)
        {
            if (sameTypeOrCategory(seasonWorld)) return false;
            if (Seasons.Random.nextInt(6) < 6) return false;

            seasonWorld.WorldStats.Rainfall -= Seasons.Random.nextFloat() * 3f + .5f;
            return true;
        }

        @Override
        public ClimateEffectType getType()
        {
            return ClimateEffectType.Dry;
        }
    };

    private ClimateEffects() {}

}
