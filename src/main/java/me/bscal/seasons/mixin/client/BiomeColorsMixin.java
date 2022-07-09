package me.bscal.seasons.mixin.client;

import me.bscal.seasons.api.SeasonAPI;
import me.bscal.seasons.client.SeasonsClient;
import me.bscal.seasons.common.Config;
import me.bscal.seasons.common.seasons.Season;
import me.bscal.seasons.common.seasons.SeasonTimer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.ColorResolver;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(BiomeColors.class)
public class BiomeColorsMixin
{

	@Mutable @Shadow @Final public static ColorResolver FOLIAGE_COLOR;

	@Mutable
	@Accessor("FOLIAGE_COLOR")
	static ColorResolver getFoliageColor()
	{
		return null;
	}

	@Mutable
	@Accessor("FOLIAGE_COLOR")
	static void setFoliageColor(ColorResolver resolver)
	{
	}

	static
	{
		FOLIAGE_COLOR = BiomeColorsMixin::FoliageColorOverride;
	}

	private static int FoliageColorOverride(Biome biome, double x, double y)
	{
		if (SeasonsClient.ClientConfig.Settings.EnableFallColors && SeasonAPI.getSeason() == Season.Autumn)
		{
			return SeasonsClient.SeasonHandler.getChanger(biome).getRandomFallColor((int) x, (int) y);
		}
		return biome.getFoliageColor();
	}
}
