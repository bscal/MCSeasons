package me.bscal.seasons.common.seasons;

public interface ClimateEffect
{

    boolean processEffect(SeasonWorld seasonWorld, Season season);

    ClimateEffectType getType();

    default boolean ofCategory(ClimateEffectCategory category)
    {
        return getType().Category == category;
    }

    default boolean sameTypeOrCategory(SeasonWorld seasonWorld)
    {
        var type = getType();
        return seasonWorld.ActiveEffects.contains(type) || seasonWorld.ActiveCategories.contains(type.Category);
    }

}
