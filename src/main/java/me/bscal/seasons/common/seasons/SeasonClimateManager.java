package me.bscal.seasons.common.seasons;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.bscal.seasons.Seasons;
import me.bscal.seasons.api.SeasonAPI;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import java.util.Objects;

public final class SeasonClimateManager
{

	public static final Object2ObjectOpenHashMap<Biome, SeasonBiomeClimate> BIOME_TO_CLIMATE = new Object2ObjectOpenHashMap<>();

	private SeasonClimateManager() {}

	public static void init(MinecraftServer server)
	{
		Objects.requireNonNull(server, "MinecraftServer cannot be null.");

		if (!BIOME_TO_CLIMATE.isEmpty())
			BIOME_TO_CLIMATE.clear();

		var biomesRegister = server.getRegistryManager().get(Registry.BIOME_KEY);
		register(biomesRegister.get(BiomeKeys.JUNGLE), SeasonBiomeClimates.JUNGLE, true);

		Seasons.LOGGER.info("Registering " + BIOME_TO_CLIMATE.size() + " SeasonBiomes.");
	}

	public static void register(Biome biome, SeasonBiomeClimate seasonBiome, boolean override)
	{
		Objects.requireNonNull(biome, "biome cannot be null.");
		Objects.requireNonNull(seasonBiome, "seasonBiome cannot be null.");

		if (override)
			BIOME_TO_CLIMATE.put(biome, seasonBiome);
		else
			BIOME_TO_CLIMATE.putIfAbsent(biome, seasonBiome);
	}

	public static SeasonBiomeClimate getOrGeneric(Biome biome)
	{
		return BIOME_TO_CLIMATE.getOrDefault(biome, SeasonBiomeClimates.GENERIC);
	}

	public static SeasonTypes getSeasonType(Biome biome)
	{
		var seasonBiome = BIOME_TO_CLIMATE.get(biome);
		return (seasonBiome == null) ? SeasonAPI.forGenericSeason() : seasonBiome.SeasonTypePerSeason.get(SeasonAPI.getSeason());
	}
}
