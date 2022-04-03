import me.bscal.seasons.Seasons;
import me.bscal.seasons.common.seasons.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public final class SeasonAPI
{

	public static Identifier getBiomeId(Biome biome, World world)
	{
		return world.getRegistryManager().get(Registry.BIOME_KEY).getId(biome);
	}

	public static Season getSeason()
	{
		return SeasonTimer.getOrCreate().getGenericSeason();
	}

	public static Season getSeasonByBiome(Entity entity)
	{
		return getSeasonByBiome(entity.world.getBiome(entity.getBlockPos()).value());
	}

	public static Season getSeasonByBiome(Biome biome)
	{
		return SeasonTimer.getOrCreate().getSeason(biome);
	}

	public static SeasonType getSeasonType(Biome biome)
	{
		return BiomeToSeasonMapper.getSeasonalType(biome);
	}


	public static SeasonDate getDate()
	{
		return SeasonTimer.getOrCreate().getDate();
	}

	public static long getTimeOfDay()
	{
		return SeasonTimer.getOrCreate().getCurrentTicks() % Seasons.ServerConfig.Settings.TicksPerDay;
	}

	public static long getCurrentTick()
	{
		return SeasonTimer.getOrCreate().getCurrentTicks();
	}

	public static long getTotalTicks()
	{
		return SeasonTimer.getOrCreate().getTotalTicks();
	}

	public static void addDays(int days)
	{
		SeasonTimer.getOrCreate().addDays(days);
	}

	public static void setSeason(int seasonTrackerId)
	{
		SeasonTimer.getOrCreate().setSeason(seasonTrackerId);
	}

	public static int getInternalSeasonId()
	{
		return SeasonTimer.getOrCreate().getInternalSeasonId();
	}

}
