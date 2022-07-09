package me.bscal.seasons.mixin.client;

import me.bscal.seasons.client.SeasonsClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(Biome.class) public class BiomeMixin
{

	@Inject(method = "getGrassColorAt(DD)I", at = @At(value = "HEAD"), cancellable = true)
	public void OnGetGrassColorAt(double x, double z, CallbackInfoReturnable<Integer> cir)
	{
		if (SeasonsClient.ClientConfig.Settings.EnableSeasonalColors)
		{
			Biome biome = (Biome) (Object) this;
			cir.setReturnValue(SeasonsClient.SeasonHandler.getChanger(biome).getGrassColor());
		}

	}

	@Inject(method = "getFoliageColor", at = @At(value = "HEAD"), cancellable = true)
	public void OnGetFoliageColor(CallbackInfoReturnable<Integer> cir)
	{
		if (SeasonsClient.ClientConfig.Settings.EnableSeasonalColors)
		{
			Biome biome = (Biome) (Object) this;
			cir.setReturnValue(SeasonsClient.SeasonHandler.getChanger(biome).getFoliageColor());
		}
	}

}
