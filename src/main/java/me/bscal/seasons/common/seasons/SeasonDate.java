package me.bscal.seasons.common.seasons;

public record SeasonDate(int Day, int Month, int Year)
{

	@Override
	public String toString()
	{
		return String.format("%d/%d/%d", Day, Month, Year);
	}
}
