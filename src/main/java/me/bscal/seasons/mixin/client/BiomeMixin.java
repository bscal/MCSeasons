package me.bscal.seasons.mixin.client;

import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class) public class BiomeMixin
{

	@Inject(method = "Lnet/minecraft/world/biome/Biome;getGrassColorAt(DD)I", at = @At(value = "HEAD"), cancellable = true)
	public void OnGetGrassColorAt(double x, double z, CallbackInfoReturnable<Integer> cir)
	{
		Biome biome = (Biome) (Object) this;
		cir.setReturnValue(BetterFarmingClient.GetBiomeSeasonHandler().GetChangers().get(biome).GetColor(Seasons.GetSeasonForBiome(biome)));
	}

	@Inject(method = "getFoliageColor", at = @At(value = "HEAD"), cancellable = true)
	public void OnGetFoliageColor(CallbackInfoReturnable<Integer> cir)
	{
		Biome biome = (Biome) (Object) this;
		cir.setReturnValue(
				BetterFarmingClient.GetBiomeSeasonHandler().GetChangers().get(biome).GetFoliageColor(Seasons.GetSeasonForBiome(biome)));
	}

	//@Inject(method = "getTemperature()F", at = @At(value = "RETURN"))
	//public void OnGetTemperature(CallbackInfoReturnable<Float> cir)
	//{
	//
	//}

}
