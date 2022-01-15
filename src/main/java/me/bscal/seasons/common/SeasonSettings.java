package me.bscal.seasons.common;

import io.leangen.geantyref.TypeToken;
import me.bscal.seasons.common.utils.IdentifierSerializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SeasonSettings
{
	private static YamlConfigurationLoader Loader;

	public static boolean DebugMode;
	public static int TicksPerDay;
	public static int DaysPerMonth;
	public static int MonthsPerYear;
	public static int MonthsPerSeason;
	public static int MaxSeasons;

	public static Map<Identifier, SeasonTypes> BiomeToSeasonType;

	public static SeasonTypes getSeasonType(Identifier biomeId)
	{
		return BiomeToSeasonType.getOrDefault(biomeId, SeasonTypes.FourSeasonPerYear);
	}

	public static SeasonTypes getSeasonType(RegistryKey<Biome> biomeKey)
	{
		return getSeasonType(biomeKey.getValue());
	}

	public static Map<Identifier, SeasonTypes> biomeToSeasonsSupplier()
	{
		Map<Identifier, SeasonTypes> biomeToSeasonType = new HashMap<>();
		biomeToSeasonType.put(BiomeKeys.BAMBOO_JUNGLE.getValue(), SeasonTypes.TropicalSeason);
		biomeToSeasonType.put(new Identifier("plains"), SeasonTypes.FourSeasonPerYear);
		return biomeToSeasonType;
	}

	public static void load()
	{
		if (Loader == null)
			init();

		CommentedConfigurationNode root;
		try
		{
			root = Loader.load();
			DebugMode = root.node("DebugMode").getBoolean(true);
			TicksPerDay = root.node("TicksPerDay").getInt(24000);
			DaysPerMonth = root.node("DaysPerMonth").getInt(30);
			MonthsPerYear = root.node("MonthsPerYear").getInt(12);
			MonthsPerSeason = root.node("MonthsPerSeason").getInt(4);
			MaxSeasons = root.node("MaxSeasons").getInt(3);

			var type = new TypeToken<Map<Identifier, SeasonTypes>>()
			{
			};
			var mapNode = root.node("BiomeSeasonTypes");
			if (mapNode.empty())
			{
				BiomeToSeasonType = biomeToSeasonsSupplier();
				root.node("BiomeSeasonTypes").set(type, BiomeToSeasonType);
			}
			else
				BiomeToSeasonType = root.node("BiomeSeasonTypes").get(type);
			Loader.save(root);
		}
		catch (ConfigurateException e)
		{
			if (e.getCause() != null)
			{
				e.getCause().printStackTrace();
			}
			System.exit(1);
		}
	}

	private static void init()
	{
		File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "seasons.yml");

		var configurationOptions = ConfigurationOptions.defaults()
				.serializers(builder -> builder.register(Identifier.class, IdentifierSerializer.Instance))
				.shouldCopyDefaults(true);

		Loader = YamlConfigurationLoader.builder().defaultOptions(configurationOptions).file(configFile).build();
		load();
	}
}
