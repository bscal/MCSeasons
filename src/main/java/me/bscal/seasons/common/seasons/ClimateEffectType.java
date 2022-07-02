package me.bscal.seasons.common.seasons;

public enum ClimateEffectType
{

    Mild(ClimateEffectCategory.Temperature),
    Dry(ClimateEffectCategory.Temperature),
    Rainy(ClimateEffectCategory.Temperature);

    public final ClimateEffectCategory Category;

    ClimateEffectType(ClimateEffectCategory category)
    {
        Category = category;
    }

}
