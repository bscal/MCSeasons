package me.bscal.seasons.common.seasons;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class SeasonStats
{

    public float Temperature;
    public float DayTemperature;
    public float NightTemperature;
    public float Humidity;
    public float Rainfall;
    public float Sunlight;
    public float WindRate;

    public SeasonStats()
    {
    }

    public SeasonStats(float temp, float dayTemp, float nightTemp, float humidity, float rainfall, float sunlight, float windRate)
    {
        Temperature = temp;
        DayTemperature = dayTemp;
        NightTemperature = nightTemp;
        Humidity = humidity;
        Rainfall = rainfall;
        Sunlight = sunlight;
        WindRate = windRate;
    }

    public SeasonStats(SeasonStats stats0, SeasonStats stats1)
    {
        Combine(stats0, stats1);
    }

    public void Combine(SeasonStats stats0, SeasonStats stats1)
    {
        Temperature = stats0.Temperature + stats1.Temperature;
        DayTemperature = stats0.DayTemperature + stats1.DayTemperature;
        NightTemperature = stats0.NightTemperature + stats1.NightTemperature;
        Humidity = stats0.Humidity + stats1.Humidity;
        Rainfall = stats0.Rainfall + stats1.Rainfall;
        Sunlight = stats0.Sunlight + stats1.Sunlight;
        WindRate = stats0.WindRate + stats1.WindRate;
    }

    public void zero()
    {
        Temperature = 0;
        DayTemperature = 0;
        NightTemperature = 0;
        Humidity = 0;
        Rainfall = 0;
        Sunlight = 0;
        WindRate = 0;
    }
}
