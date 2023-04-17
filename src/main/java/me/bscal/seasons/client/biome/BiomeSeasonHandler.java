package me.bscal.seasons.client.biome;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

@Environment(EnvType.CLIENT) public class BiomeSeasonHandler
{
	public Object2ObjectOpenHashMap<Identifier, BiomeChanger> ChangerMap;

	/**
	 * This is loaded when the world is loaded to access the dynamic registry for biomes.
	 */
	public BiomeSeasonHandler()
	{
		ChangerMap = new Object2ObjectOpenHashMap<>();
	}

	public void reload(ClientWorld world)
	{
		ChangerMap.clear();
		initChangers(world);
		MinecraftClient.getInstance().worldRenderer.reload();
	}

	private void initChangers(ClientWorld world)
	{
		//MinecraftClient.getInstance().player.networkHandler.getRegistryManager()
		world.getRegistryManager()
				.get(RegistryKeys.BIOME)
				.streamEntries()
				.forEach((pair) -> register(pair.registryKey(), BiomeChanger.createDefaultChanger(pair.value())));
	}

	public BiomeChanger getChanger(Biome biome)
	{
		if (MinecraftClient.getInstance().world == null)
			return null;
		var id = MinecraftClient.getInstance().world.getRegistryManager().get(RegistryKeys.BIOME).getId(biome);
		return ChangerMap.get(id);
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
		register(MinecraftClient.getInstance().world.getRegistryManager().get(RegistryKeys.BIOME).getId(biome), changer);
	}
}
