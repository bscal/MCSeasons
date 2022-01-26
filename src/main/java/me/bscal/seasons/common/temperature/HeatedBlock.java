package me.bscal.seasons.common.temperature;

import me.bscal.seasons.Seasons;
import net.minecraft.block.BlockState;

import java.util.function.Predicate;

public class HeatedBlock implements TemperatureSource
{

	protected float m_Temperature;
	protected float m_Radius;
	protected Predicate<BlockState> m_Condition;

	public HeatedBlock(float temp, float radius, Predicate<BlockState> stateConditions)
	{
		if (temp <= 0)
			Seasons.LOGGER.warn("TemperatureSource is a heated block but temp is <= 0");
		if (radius <= 0)
			Seasons.LOGGER.warn("TemperatureSource radius is <= 0");

		m_Temperature = temp;
		m_Radius = radius;
		m_Condition = stateConditions;
	}

	@Override
	public boolean isStateValid(BlockState state)
	{
		return m_Condition == null || m_Condition.test(state);
	}

	@Override
	public float getTemperature()
	{
		return m_Temperature;
	}

	@Override
	public float getRadius()
	{
		return m_Radius;
	}
}