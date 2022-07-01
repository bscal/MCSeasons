package me.bscal.seasons.common.seasons;

import me.bscal.seasons.Seasons;
import net.minecraft.util.math.random.Random;

import java.util.Objects;

public class SeasonalModifiers
{

    public float Temperature;
    public float Rainfall;

    public void applyModifiers(SeasonStats stat)
    {
        Objects.requireNonNull(stat, "stat cannot be null.");

        Random rand = Seasons.Random;
        //float tempModifier = rand.nextFloat() * (MaxTemperature - MinTemperature) + MinTemperature;

    }

}
