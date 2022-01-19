package me.bscal.seasons.client.biome;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

@Environment(EnvType.CLIENT) public class BiomeSeasonHandler
{
	public Object2ObjectOpenHashMap<Identifier, BiomeChanger> ChangerMap;

	/**
	 * This is loaded when the world is loaded to access the dynamic registry for biomes.
	 */
	public BiomeSeasonHandler(ClientWorld world)
	{
		ChangerMap = new Object2ObjectOpenHashMap<>();
	}

	public void initChangers(ClientWorld world)
	{
		world.getRegistryManager()
				.get(Registry.BIOME_KEY)
				.getEntries()
				.forEach((pair) -> register(pair.getValue(), BiomeChanger.createDefaultChanger(pair.getValue())));
	}

	public void register(Identifier id, BiomeChanger changer)
	{
		ChangerMap.putIfAbsent(id, changer);
	}

	public void register(RegistryKey<Biome> biome, BiomeChanger changer)
	{
		register(biome.getValue(), changer);
	}

	public void register(Biome biome, BiomeChanger changer)
	{
		if (MinecraftClient.getInstance().world == null)
			return;
		register(MinecraftClient.getInstance().world.getRegistryManager().get(Registry.BIOME_KEY).getId(biome), changer);
	}

	public void reload(ClientWorld world)
	{
		ChangerMap.clear();
		initChangers(world);
		MinecraftClient.getInstance().worldRenderer.reload();
	}
}
