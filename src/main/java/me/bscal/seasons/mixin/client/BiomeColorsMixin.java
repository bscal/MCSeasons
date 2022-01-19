package me.bscal.seasons.mixin.client;

import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.ColorResolver;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

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
	static void setFoliageColor(ColorResolver resolver) {}

	static
	{
		FOLIAGE_COLOR = BiomeColorsMixin::FoliageColorOverride;
	}

	private static int FoliageColorOverride(Biome biome, double x, double y)
	{
		if (SeasonSettings.Root.fallLeavesGraphics.getValue() != SeasonSettings.FallLeavesSettings.DISABLED && BetterFarmingClient.GetBiomeSeasonHandler().seasonClock.currentSeason == Seasons.AUTUMN)
		{
			return BetterFarmingClient.GetBiomeSeasonHandler().GetChangers().get(biome).GetRandomFallColor((int)x, (int)y);
		}
		return biome.getFoliageColor();
	}
}
