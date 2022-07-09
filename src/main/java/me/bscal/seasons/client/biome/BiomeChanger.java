package me.bscal.seasons.client.biome;

import me.bscal.seasons.api.SeasonAPI;
import me.bscal.seasons.client.SeasonsClient;
import me.bscal.seasons.common.Config;
import me.bscal.seasons.common.seasons.Season;
import me.bscal.seasons.common.seasons.SeasonTimer;
import me.bscal.seasons.common.utils.Color;
import me.bscal.seasons.common.utils.FastNoiseLite;
import me.bscal.seasons.mixin.client.BiomeInvoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.biome.Biome;

@Environment(EnvType.CLIENT)
public class BiomeChanger
{
    public static final Color WINTER_DEAD_BLUE = Color.fromHex("#665538");
    public static final Color AUTUMN_DEEP_YELLOW = Color.fromHex("#ffcc00");
    public static final Color DEEP_BROWN = Color.fromHex("#976b1d");
    public static final Color DEEP_RED = Color.fromHex("#bd4615");
    public static final Color DEEP_YELLOW = Color.fromHex("#d6b11c");
    public static final Color DEEP_ORANAGE = Color.fromHex("#874a0c");
    public static final int ERROR_PINK = Color.fromHex("#ff1493").toInt();

    public static final FastNoiseLite Noise = new FastNoiseLite();

    protected int DefaultColor;
    protected int[] GrassColors;
    protected int[] FoliageColor;
    protected int[] FallLeaves;

    public BiomeChanger(Biome biome)
    {
        Noise.SetFrequency(0.02f);
        Noise.SetFractalOctaves(3);
        Noise.SetFractalLacunarity(3.0f);
        DefaultColor = biome.getEffects().getGrassColor().orElse(((BiomeInvoker) (Object) biome).invokeGetDefaultGrassColor());
        GrassColors = new int[0];
        FoliageColor = new int[0];
        FallLeaves = new int[0];
    }

    public void setGrassColors(int spring, int summer, int fall, int winter)
    {
        GrassColors = new int[]{spring, summer, fall, winter};
    }

    public void setFoliageColor(int spring, int summer, int fall, int winter)
    {
        FoliageColor = new int[]{spring, summer, fall, winter};
    }

    public void setFallLeavesColor(int... c3)
    {
        FallLeaves = c3;
    }

    public int getGrassColor()
    {
        if (GrassColors == null)
            return ERROR_PINK; // Safety check because these arrays should never be null
        if (SeasonTimer.get().getInternalSeasonId() >= GrassColors.length)
            return DefaultColor; // If the array doesnt contain the season id use the default color
        return GrassColors[SeasonTimer.get().getInternalSeasonId()];
    }

    public int getFoliageColor()
    {
        if (FoliageColor == null)
            return ERROR_PINK; // Safety check because these arrays should never be null
        if (SeasonTimer.get().getInternalSeasonId() >= FoliageColor.length)
            return DefaultColor; // If the array doesnt contain the season id use the default color
        return FoliageColor[SeasonTimer.get().getInternalSeasonId()];
    }

    public int getFallLeavesColor(int x, int y)
    {
        if (SeasonsClient.ClientConfig.Settings.EnableFallColors && SeasonAPI.getSeason() == Season.Autumn)
            return getRandomFallColor(x, y);
        return getFoliageColor();
    }

    public int getRandomFallColor(int x, int y)
    {
        int index;
        if (SeasonsClient.ClientConfig.Settings.GraphicsLevel == Config.SeasonsGraphicsLevel.Fancy)
        {
            float val = Noise.GetNoise(x, y);
            float normalized = (val - -1) / (1 - -1);
            index = (int)(normalized * FallLeaves.length);
        }
        else if (SeasonsClient.ClientConfig.Settings.GraphicsLevel == Config.SeasonsGraphicsLevel.Fast)
            index = ((x + y) / 16) % FallLeaves.length;
        else
            index = 0;

        if (FallLeaves == null)
            return ERROR_PINK; // Safety check because these arrays should never be null
        if (index < 0 || index >= FallLeaves.length)
            return DefaultColor; // If the array doesnt contain the season id use the default color
        return FallLeaves[index];
    }

    public static BiomeChanger createDefaultChanger(Biome biome)
    {
        BiomeChanger changer = new BiomeChanger(biome);

        Color spring = new Color(changer.DefaultColor);
        spring.saturate(40);

        Color fall = new Color(changer.DefaultColor);
        fall.blend(AUTUMN_DEEP_YELLOW, .25f);

        Color winter = new Color(changer.DefaultColor);
        winter.blend(WINTER_DEAD_BLUE, .6f);

        changer.setGrassColors(spring.toInt(), changer.DefaultColor, fall.toInt(), winter.toInt());
        changer.setFoliageColor(spring.toInt(), changer.DefaultColor, fall.toInt(), winter.toInt());
        changer.setFallLeavesColor(fall.toInt(), DEEP_YELLOW.toInt(), DEEP_BROWN.toInt(), DEEP_RED.toInt(), DEEP_ORANAGE.toInt());

        return changer;
    }
}