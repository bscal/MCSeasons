package me.bscal.seasons.client.biome;

import me.bscal.seasons.client.SeasonsClient;
import me.bscal.seasons.common.Config;
import me.bscal.seasons.common.seasons.Season;
import me.bscal.seasons.common.seasons.SeasonTimer;
import me.bscal.seasons.common.utils.Color;
import me.bscal.seasons.mixin.client.BiomeInvoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;

@Environment(EnvType.CLIENT)
public class BiomeChanger
{
    public static final Color WINTER_DEAD_BLUE = Color.fromHex("#827866");
    public static final Color AUTUMN_DEEP_YELLOW = Color.fromHex("#ffcc00");
    public static final Color DEEP_BROWN = Color.fromHex("#976b1d");
    public static final Color DEEP_RED = Color.fromHex("#bd4615");
    public static final Color DEEP_YELLOW = Color.fromHex("#d6b11c");
    public static final Color DEEP_ORANAGE = Color.fromHex("#874a0c");

    public int DefaultColor;
    public int[] GrassColors;
    public int[] FoliageColor;
    public int[] FallLeaves;

    public BiomeChanger(int defaultColor, int[] grassColors, int[] foliageColors, int[] fallLeavesColors)
    {
        assert grassColors.length == 4 : "grassColors length must be 4";
        assert foliageColors.length == 4 : "grassColors length must be 4";
        assert fallLeavesColors.length > 0 : "grassColors must be greater than 0";
        DefaultColor = defaultColor;
        GrassColors = grassColors;
        FoliageColor = foliageColors;
        FallLeaves = fallLeavesColors;
    }

    public int getGrassColor()
    {
        return GrassColors[SeasonTimer.get().getInternalSeasonId()];
    }

    public int getFoliageColor()
    {
        return FoliageColor[SeasonTimer.get().getInternalSeasonId()];
    }

    public int getFallLeavesColor(int x, int y)
    {
        if (SeasonsClient.ClientConfig.Settings.EnableFallColors && SeasonTimer.get().getSeason() == Season.Autumn)
            return getRandomFallColor(x, y);
        return getFoliageColor();
    }

    public int getRandomFallColor(int x, int y)
    {
        int index;
        if (SeasonsClient.ClientConfig.Settings.GraphicsLevel == Config.SeasonsGraphicsLevel.Fancy)
        {
            float val = SeasonsClient.NOISE.GetNoise(x, y);
            float normalized = (val - -1) / (1 - -1);
            index = (int) (normalized * FallLeaves.length);
        }
        else if (SeasonsClient.ClientConfig.Settings.GraphicsLevel == Config.SeasonsGraphicsLevel.Fast)
            index = ((x + y) / 16) % FallLeaves.length;
        else
            index = 0;

        return FallLeaves[MathHelper.clamp(index, 0, FallLeaves.length - 1)];
    }

    public static BiomeChanger createDefaultChanger(Biome biome)
    {
        int defaultColor = biome.getEffects().getGrassColor().orElse(((BiomeInvoker) (Object) biome).invokeGetDefaultGrassColor());

        Color spring = new Color(defaultColor);
        spring.saturate(40);

        Color fall = new Color(defaultColor);
        fall.blend(AUTUMN_DEEP_YELLOW, .25f);

        Color winter = new Color(defaultColor);
        winter.blend(WINTER_DEAD_BLUE, .65f);

        int[] colors = new int[]{spring.toInt(), defaultColor, fall.toInt(), winter.toInt()};
        int[] fallColors = new int[]{fall.toInt(), DEEP_YELLOW.toInt(), DEEP_BROWN.toInt(), DEEP_RED.toInt(), DEEP_ORANAGE.toInt()};
        return new BiomeChanger(defaultColor, colors, colors, fallColors);
    }
}