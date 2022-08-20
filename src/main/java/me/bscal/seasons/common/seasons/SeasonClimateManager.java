package me.bscal.seasons.common.seasons;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.bscal.seasons.Seasons;
import me.bscal.seasons.api.SeasonAPI;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import java.util.Objects;

public final class SeasonClimateManager
{

	public static final Object2ObjectOpenHashMap<Biome, SeasonBiomeClimate> BIOME_TO_CLIMATE = new Object2ObjectOpenHashMap<>(48);

	private SeasonClimateManager() {}

	public static SeasonBiomeClimate getOrGeneric(Biome biome)
	{
		return BIOME_TO_CLIMATE.getOrDefault(biome, SeasonBiomeClimates.GENERIC);
	}

	public static SeasonTypes getSeasonType(Biome biome)
	{
		var seasonBiome = BIOME_TO_CLIMATE.get(biome);
		return (seasonBiome == null) ? SeasonAPI.forGenericSeason() : seasonBiome.SeasonTypePerSeason.get(SeasonAPI.getSeason());
	}

	public static void put(World world, Identifier id, SeasonBiomeClimate climate)
	{
		var biome = world.getRegistryManager().get(Registry.BIOME_KEY).get(id);
		if (biome == null) return;
		BIOME_TO_CLIMATE.put(biome, climate);
	}

	public static void init(MinecraftServer server)
	{
		Objects.requireNonNull(server, "MinecraftServer cannot be null.");

		if (!BIOME_TO_CLIMATE.isEmpty())
			BIOME_TO_CLIMATE.clear();

		BIOME_TO_CLIMATE.defaultReturnValue(SeasonBiomeClimates.GENERIC);

		var world = server.getOverworld();
		put(world, BiomeKeys.SNOWY_PLAINS.getValue(), SeasonBiomeClimates.ARTIC);
		put(world, BiomeKeys.ICE_SPIKES.getValue(), SeasonBiomeClimates.ARTIC);

		put(world, BiomeKeys.DESERT.getValue(), SeasonBiomeClimates.DESERT);

		put(world, BiomeKeys.TAIGA.getValue(), SeasonBiomeClimates.TUNDRA);
		put(world, BiomeKeys.SNOWY_TAIGA.getValue(), SeasonBiomeClimates.TUNDRA);

		put(world, BiomeKeys.SAVANNA.getValue(), SeasonBiomeClimates.SAVANNAH);
		put(world, BiomeKeys.SAVANNA_PLATEAU.getValue(), SeasonBiomeClimates.SAVANNAH);

		put(world, BiomeKeys.JUNGLE.getValue(), SeasonBiomeClimates.JUNGLE);
		put(world, BiomeKeys.SPARSE_JUNGLE.getValue(), SeasonBiomeClimates.JUNGLE);
		put(world, BiomeKeys.BAMBOO_JUNGLE.getValue(), SeasonBiomeClimates.JUNGLE);

		put(world, BiomeKeys.BADLANDS.getValue(), SeasonBiomeClimates.SAVANNAH);
		put(world, BiomeKeys.ERODED_BADLANDS.getValue(), SeasonBiomeClimates.SAVANNAH);
		put(world, BiomeKeys.WOODED_BADLANDS.getValue(), SeasonBiomeClimates.SAVANNAH);

		put(world, BiomeKeys.SNOWY_SLOPES.getValue(), SeasonBiomeClimates.TUNDRA);
		put(world, BiomeKeys.FROZEN_PEAKS.getValue(), SeasonBiomeClimates.ARTIC);
		put(world, BiomeKeys.JAGGED_PEAKS.getValue(), SeasonBiomeClimates.ARTIC);
		put(world, BiomeKeys.STONY_PEAKS.getValue(), SeasonBiomeClimates.ARTIC);
		put(world, BiomeKeys.FROZEN_OCEAN.getValue(), SeasonBiomeClimates.ARTIC);
		put(world, BiomeKeys.DEEP_FROZEN_OCEAN.getValue(), SeasonBiomeClimates.ARTIC);

		// TODO
		put(world, BiomeKeys.NETHER_WASTES.getValue(), SeasonBiomeClimates.GENERIC);
		put(world, BiomeKeys.WARPED_FOREST.getValue(), SeasonBiomeClimates.GENERIC);
		put(world, BiomeKeys.CRIMSON_FOREST.getValue(), SeasonBiomeClimates.GENERIC);
		put(world, BiomeKeys.SOUL_SAND_VALLEY.getValue(), SeasonBiomeClimates.GENERIC);
		put(world, BiomeKeys.BASALT_DELTAS.getValue(), SeasonBiomeClimates.GENERIC);

		// TODO
		put(world, BiomeKeys.THE_END.getValue(), SeasonBiomeClimates.GENERIC);
		put(world, BiomeKeys.END_HIGHLANDS.getValue(), SeasonBiomeClimates.GENERIC);
		put(world, BiomeKeys.END_MIDLANDS.getValue(), SeasonBiomeClimates.GENERIC);
		put(world, BiomeKeys.SMALL_END_ISLANDS.getValue(), SeasonBiomeClimates.GENERIC);
		put(world, BiomeKeys.END_BARRENS.getValue(), SeasonBiomeClimates.GENERIC);

		Seasons.LOGGER.info("Registering " + BIOME_TO_CLIMATE.size() + " SeasonBiomes.");
	}
}
