package me.bscal.seasons.common.seasons;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.io.Serializable;

@ConfigSerializable
public class SeasonStats implements Serializable
{

    public float Temperature;
    public TemperateType TemperatureType;

    public SeasonStats()
    {
    }

    public SeasonStats(float temp, TemperateType temperatureType)
    {
        Temperature = temp;
        TemperatureType = temperatureType;
    }

    public enum TemperateType
    {
        NORMAL(0),
        EXTREMELY_COLD(-1),
        VERY_COLD(-.75f),
        COLD(-.5f),
        HOT(.5f),
        VERY_HOT(.75f),
        EXTREMELY_HOT(1);

        public final float TemperatureChange;

        TemperateType(float temperatureChange)
        {
            TemperatureChange = temperatureChange;
        }
    }

}
