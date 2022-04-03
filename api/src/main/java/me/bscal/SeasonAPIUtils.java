package me.bscal;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Objects;
import java.util.Optional;

public final class SeasonAPIUtils
{

	public static Optional<Biome> getBiomeFromId(Identifier id, World world)
	{
		Objects.requireNonNull(id, "id must not be null");
		Objects.requireNonNull(world, "world must not be null");
		var biome = world.getRegistryManager().get(Registry.BIOME_KEY).get(id);
		return Optional.ofNullable(biome);
	}

	public static Optional<Identifier> getBiomeIdFromBiome(Biome biome, World world)
	{
		Objects.requireNonNull(biome, "biome must not be null");
		Objects.requireNonNull(world, "world must not be null");
		var id = world.getRegistryManager().get(Registry.BIOME_KEY).getId(biome);
		return Optional.ofNullable(id);
	}

	public static Biome getBiomeFromEntity(Entity entity)
	{
		Objects.requireNonNull(entity, "entity must not be null");
		return entity.world.getBiome(entity.getBlockPos()).value();
	}

}
