package me.bscal.seasons.mixin.client;

import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Biome.class)
public interface BiomeInvoker
{

	@Invoker
	int invokeGetDefaultGrassColor();

	@Invoker
	int invokeGetDefaultFoliageColor();

}
