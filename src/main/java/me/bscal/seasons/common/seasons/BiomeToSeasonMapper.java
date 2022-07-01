package me.bscal.seasons.common.seasons;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.bscal.seasons.Seasons;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public final class BiomeToSeasonMapper
{

	public static final Object2ObjectOpenHashMap<Biome, SeasonBiomeClimate> BiomesToSeason = new Object2ObjectOpenHashMap<>();

	private BiomeToSeasonMapper() {}

	public static void Init(MinecraftServer server)
	{
		if (!BiomesToSeason.isEmpty())
			BiomesToSeason.clear();

		var biomesRegister = server.getRegistryManager().get(Registry.BIOME_KEY);
		register(biomesRegister.get(BiomeKeys.JUNGLE), SeasonBiomeClimates.JUNGLE);

		Seasons.LOGGER.info("Registering " + BiomesToSeason.size() + " SeasonBiomes.");
	}

	public static void register(Biome biome, SeasonBiomeClimate seasonBiome)
	{
		BiomesToSeason.put(biome, seasonBiome);
	}

	public static SeasonTypes getSeasonType(Biome biome)
	{
		var seasonBiome = BiomesToSeason.get(biome);
		if (seasonBiome == null) return SeasonTypes.forGenericSeason();
		return seasonBiome.SeasonTypePerSeason.get(Season.getSeason());
	}
}
