package me.bscal.seasons.mixin.client;

import me.bscal.seasons.client.SeasonsClient;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class) public class BiomeMixin
{

	@Inject(method = "getGrassColorAt(DD)I", at = @At(value = "HEAD"), cancellable = true)
	public void OnGetGrassColorAt(double x, double z, CallbackInfoReturnable<Integer> cir)
	{
		Biome biome = (Biome) (Object) this;
		cir.setReturnValue(SeasonsClient.SeasonHandler.getChanger(biome).getGrassColor());
	}

	@Inject(method = "getFoliageColor", at = @At(value = "HEAD"), cancellable = true)
	public void OnGetFoliageColor(CallbackInfoReturnable<Integer> cir)
	{
		Biome biome = (Biome) (Object) this;
		cir.setReturnValue(SeasonsClient.SeasonHandler.getChanger(biome).getFoliageColor());
	}

	//@Inject(method = "getTemperature()F", at = @At(value = "RETURN"))
	//public void OnGetTemperature(CallbackInfoReturnable<Float> cir)
	//{
	//
	//}

}
