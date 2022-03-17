package me.bscal.seasons.mixin.client.block;

import me.bscal.seasons.client.ClientConfig;
import me.bscal.seasons.client.particles.FallingLeavesParticle;
import me.bscal.seasons.common.seasons.Season;
import me.bscal.seasons.common.seasons.SeasonTimer;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(LeavesBlock.class) public class LeavesBlockMixin
{

	@Inject(method = "randomDisplayTick", at = @At(value = "HEAD"))
	public void OnRandomDisplayTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci)
	{
		int leafFallDist = ClientConfig.Root.LeafFallDistance.getValue();
		if (leafFallDist > 0 && pos.isWithinDistance(MinecraftClient.getInstance().player.getPos(), leafFallDist))
		{
			int bound = SeasonTimer.GetOrCreate().getGenericSeason() == Season.Autumn ? 32 : 96;

			if (world.isRaining())
				bound -= 8;
			if (world.isThundering())
				bound -= 8;

			if (random.nextInt(bound) == 0)
			{
				BlockPos blockPos = pos.down();
				if (CanFallThrough(world.getBlockState(blockPos)))
				{
					double d = (double) pos.getX() + random.nextDouble();
					double e = (double) pos.getY() - 0.05D;
					double f = (double) pos.getZ() + random.nextDouble();
					world.addParticle(FallingLeavesParticle.FALLING_LEAVES, d, e, f, 0.0D, 0.0D, 0.0D);
				}
			}
		}
	}

	private static boolean CanFallThrough(BlockState state)
	{
		return state.isAir() || state.isIn(BlockTags.FIRE) || state.getMaterial().isReplaceable();
	}

}
