package me.bscal.seasons.client.particles;

import me.bscal.seasons.client.SeasonsClient;
import me.bscal.seasons.common.utils.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT) public class FallingLeavesParticle extends SpriteBillboardParticle
{

	public static final ParticleType<BlockStateParticleEffect> FALLING_LEAVES = FabricParticleTypes.complex(BlockStateParticleEffect.PARAMETERS_FACTORY);

	// TODO This doesnt need a complex factory prob

	private final float angleVelocity;

	protected FallingLeavesParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, SpriteProvider sprites,
			BlockState state)
	{
		super(clientWorld, d, e, f, g, h, i);
		this.setSprite(sprites.getSprite(clientWorld.random));
		this.maxAge = 60 - clientWorld.random.nextInt(10);
		this.scale *= .75f;

		float vModifier = 0.1f;
		vModifier += clientWorld.isRaining() ? .025f : 0;
		vModifier += clientWorld.isThundering() ? .05f : 0;
		float vModifierOffset = vModifier / 2;
		this.velocityX = clientWorld.random.nextFloat() * vModifier - vModifierOffset;
		this.velocityY = 0;
		this.velocityZ = clientWorld.random.nextFloat() * vModifier - vModifierOffset;
		this.angleVelocity = clientWorld.random.nextFloat() - 0.5f;

		Biome b = clientWorld.getBiome(new BlockPos(d, e, f));
		var id = clientWorld.getRegistryManager().get(Registry.BIOME_KEY).getId(b);
		var changer = SeasonsClient.SeasonHandler.ChangerMap.get(id);
		Color color = new Color(changer.getFallLeavesColor((int) d, (int) f));
		float[] rgba = color.toFloats();
		this.setColor(rgba[0], rgba[1], rgba[2]);
	}

	@Override
	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public void tick()
	{
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge)
		{
			this.markDead();
		}
		else
		{
			this.prevAngle = this.angle;
			this.angle += this.angleVelocity;

			if (this.onGround)
			{
				this.prevAngle = this.angle = 0.0F;
			}
			else
			{
				this.move(this.velocityX, this.velocityY, this.velocityZ);
				this.velocityY -= 0.003000000026077032D;
				this.velocityY = Math.max(this.velocityY, -0.14000000059604645D);
			}
		}
	}

	@Environment(EnvType.CLIENT) public static class Factory implements ParticleFactory<BlockStateParticleEffect>
	{
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}

		@Nullable
		@Override
		public Particle createParticle(BlockStateParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX,
				double velocityY,
				double velocityZ)
		{
			BlockState blockState = parameters.getBlockState();
			return new FallingLeavesParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider, blockState);
		}
	}
}
