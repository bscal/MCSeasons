package me.bscal.seasons.mixin.client.block;

import me.bscal.seasons.api.SeasonAPI;
import me.bscal.seasons.client.SeasonsClient;
import me.bscal.seasons.client.particles.FallingLeavesParticle;
import me.bscal.seasons.common.seasons.Season;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LeavesBlock.class) public class LeavesBlockMixin
{

	@Inject(method = "randomDisplayTick", at = @At(value = "HEAD"))
	public void OnRandomDisplayTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci)
	{
		int leafFallDist = SeasonsClient.ClientConfig.Settings.LeafFallDistance;
		if (leafFallDist > 0 && pos.isWithinDistance(MinecraftClient.getInstance().player.getPos(), leafFallDist))
		{
			int bound = SeasonAPI.getSeason() == Season.Autumn ? 32 : 96;

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
