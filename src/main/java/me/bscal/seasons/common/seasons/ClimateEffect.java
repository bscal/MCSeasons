package me.bscal.seasons.common.seasons;

public interface ClimateEffect
{

    boolean canApply(SeasonWorld seasonWorld, Season season);

    void applyEffect(SeasonWorld seasonWorld);

    void removeEffect(SeasonWorld seasonWorld);

    boolean updateEffect(SeasonWorld seasonWorld, int duration, int daysLeftInSeason);

    int getDurationInDays();

    ClimateEffectType getType();

    ClimateEffectCategory getCategory();

    default boolean isOverDuration(int duration)
    {
        return duration >= getDurationInDays();
    }

    default boolean sameTypeOrCategory(SeasonWorld seasonWorld)
    {
        return seasonWorld.ActiveEffects.containsKey(getType()) || seasonWorld.ActiveCategories.contains(getCategory());
    }

}
