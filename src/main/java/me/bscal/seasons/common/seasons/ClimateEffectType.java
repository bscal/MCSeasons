package me.bscal.seasons.common.seasons;

import me.bscal.seasons.common.seasons.effects.TemperatureEffects;
import static me.bscal.seasons.common.seasons.ClimateEffectCategory.*;

public enum ClimateEffectType
{

    Mild(Temperature, new TemperatureEffects()),
    Cool(Temperature, new TemperatureEffects()),
    Dry(Rain, new TemperatureEffects()),
    Rainy(Rain, new TemperatureEffects());

    public final ClimateEffectCategory Category;
    public final ClimateEffect Effect;

    ClimateEffectType(ClimateEffectCategory category, ClimateEffect effect)
    {
        Category = category;
        Effect = effect;
    }

}
