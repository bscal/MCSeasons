package me.bscal.seasons.mixin.client;

import me.bscal.seasons.client.SeasonsClient;
import me.bscal.seasons.client.biome.BiomeChanger;
import me.bscal.seasons.common.seasons.Season;
import me.bscal.seasons.common.seasons.SeasonTimer;
import me.bscal.seasons.common.utils.Color;
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
	private static void onGetBirchColor(CallbackInfoReturnable<Integer> cir)
	{
		BiomeChanger changer = SeasonsClient.SeasonHandler.ChangerMap.get(BiomeKeys.BIRCH_FOREST.getValue());
		if (changer != null)
		{
			if (SeasonsClient.ClientConfig.Settings.EnableFallColors && SeasonTimer.getOrCreate().getGenericSeason() == Season.Autumn)
				cir.setReturnValue(FALL_BIRCH_COLOR);
			else
				cir.setReturnValue(changer.getFoliageColor());
		}
	}

}
