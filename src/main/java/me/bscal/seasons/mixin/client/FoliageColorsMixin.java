package me.bscal.seasons.mixin.client;

import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.world.biome.BiomeKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FoliageColors.class) public class FoliageColorsMixin
{

	private static final int FALL_BIRCH_COLOR = Color.fromHex("#e2b914").toInt();

	@Inject(method = "getBirchColor", at = @At(value = "RETURN"), cancellable = true)
	private static void OnGetBirchColor(CallbackInfoReturnable<Integer> cir)
	{
		BiomeSeasonHandler seasonHandler = BetterFarmingClient.GetBiomeSeasonHandler();
		BiomeChanger changer = seasonHandler.GetChangers().getFromRegistryKey(BiomeKeys.BIRCH_FOREST);
		if (changer != null)
		{
			if (SeasonSettings.Root.fallLeavesGraphics.getValue() != SeasonSettings.FallLeavesSettings.DISABLED && Seasons.GetSeason() == Seasons.AUTUMN)
				cir.setReturnValue(FALL_BIRCH_COLOR);
			else
				cir.setReturnValue(changer.GetColor(Seasons.GetSeason()));
		}
	}

}
