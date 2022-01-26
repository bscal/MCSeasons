package me.bscal.seasons.common.temperature;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface TemperatureSource
{

	default boolean isStateValid(BlockState state)
	{
		return true;
	}

	default float getFinalTemperature(BlockState state)
	{
		return isStateValid(state) ? getTemperature() : 0f;
	}

	default boolean isClose(BlockPos targetPos, BlockPos srcPos)
	{
		return targetPos.isWithinDistance(srcPos, getRadius());
	}

	float getTemperature();

	float getRadius();

}
