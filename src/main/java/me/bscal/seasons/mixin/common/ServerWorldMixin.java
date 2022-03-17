package me.bscal.seasons.mixin.common;

import me.bscal.seasons.Seasons;
import me.bscal.seasons.common.seasons.SeasonTimer;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldMixin
{

	@Inject(method = "setTimeOfDay(J)V", at = @At("HEAD"))
	public void onSetTimeOfDay(long timeOfDay, CallbackInfo ci)
	{
		var world = (ServerWorld) (Object) this;
		// TODO current seasons and time only support 1 world
		// not sure if I want to support multiple worlds
		if (world == Seasons.Instance.getServer().getOverworld())
			SeasonTimer.GetOrCreate().updateTime();
	}


}
