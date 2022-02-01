package me.bscal.seasons.common.temperature;

import me.bscal.seasons.Seasons;
import me.bscal.seasons.api.SeasonAPI;
import me.bscal.seasons.common.seasons.SeasonState;
import me.bscal.seasons.common.seasons.SeasonType;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;

import java.util.Set;

public final class TemperatureAPI
{
	public static final BlockApiLookup<TemperatureSource, Void> TEMPERATURE_CONTAINER = BlockApiLookup.get(
			new Identifier(Seasons.MOD_ID, "temperature_container"), TemperatureSource.class, null);

	public static final int TEMPERATURE_RADIUS = 12;
	public static final int TEMPERATURE_RADIUS_Y = TEMPERATURE_RADIUS / 2;

	public static final Block[] HEATED_BLOCKS = new Block[]
			{
					Blocks.LAVA, Blocks.FIRE, Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE

			};

	public final Set<Block> ALL_TEMPERATURE_BLOCKS;

	TemperatureAPI()
	{
		registerBlocks();
		ALL_TEMPERATURE_BLOCKS = Set.of(HEATED_BLOCKS);
	}

	public void updatePlayerTemperatures(ServerPlayerEntity sPlayer)
	{
		BlockPos pos = sPlayer.getBlockPos();
		Biome biome = sPlayer.world.getBiome(pos);
		Identifier biomeId = SeasonAPI.getBiomeId(biome, sPlayer.world);

		// Seasonal Temperatures
		// Season / Air
		SeasonType seasonType = SeasonAPI.getSeasonType(biomeId);
		SeasonState seasonState = SeasonAPI.getSeason(biomeId);

		boolean isNight = sPlayer.world.getTimeOfDay() > 20000 || sPlayer.world.getTimeOfDay() < 6000;
		
		// If outside / wind
		sPlayer.world.getLightLevel(LightType.SKY, pos);

		// Positional Temperature
		// Y Level
		float yPosModifier = 0f;
		if (pos.getY() > 127) yPosModifier = pos.getY() * -0.1f;
		else if (pos.getY() < 0) yPosModifier = pos.getY() * 0.1f;

		// Biome Temp

		// Wetness Modifier

		// Block Sources/Sinks
		// TODO looking into caching iterated blocks?
		float totalNearbyTemperature = 0f;
		for (BlockPos nextPos: BlockPos.iterateOutwards(sPlayer.getBlockPos(), TEMPERATURE_RADIUS, TEMPERATURE_RADIUS_Y, TEMPERATURE_RADIUS))
		{
			BlockState state = sPlayer.world.getBlockState(nextPos);
			if (ALL_TEMPERATURE_BLOCKS.contains(state.getBlock()))
			{
				BlockEntity blockEntity = sPlayer.world.getBlockEntity(nextPos);
				TemperatureSource tempSrc = TemperatureAPI.TEMPERATURE_CONTAINER.find(sPlayer.world, nextPos, state, blockEntity, null);
				int dist = nextPos.getManhattanDistance(sPlayer.getBlockPos());
				if (tempSrc != null && dist <= tempSrc.getRadius() && tempSrc.isStateValid(state))
				{
					// Handle block temperature
					float temp = tempSrc.getTemperature() / dist;
				}
			}
		}

		// Process and apply temperature
		// Insulation and clothing
	}

	public void registerBlocks()
	{
		TEMPERATURE_CONTAINER.registerForBlocks((world, pos, state, blockEntity, context) ->
		{
			Block block = state.getBlock();
			if (Blocks.LAVA.equals(block))
			{
				return new HeatedBlock(400, 10, null);
			}
			else if (Blocks.FIRE.equals(block))
			{
				return new HeatedBlock(200, 6, null);
			}
			else if ((Blocks.CAMPFIRE.equals(block) || Blocks.SOUL_CAMPFIRE.equals(block)))
			{
				return new HeatedBlock(350, 4, (blockState) -> blockState.get(CampfireBlock.LIT));
			}
			else if (Blocks.FURNACE.equals(block))
			{
				return new HeatedBlock(350, 4, (blockState) -> blockState.get(FurnaceBlock.LIT));
			}
			return null;

		}, HEATED_BLOCKS);
	}

}
