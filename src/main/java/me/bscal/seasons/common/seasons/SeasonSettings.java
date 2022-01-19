package me.bscal.seasons.common.seasons;

import me.bscal.seasons.common.utils.IdentifierSerializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.BiomeKeys;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SeasonSettings
{
	public boolean DebugMode;
	public Config Config;
	public final Map<Identifier, SeasonTypes> BiomeToSeasonType;
	private final ConfigurationOptions m_Options;

	public SeasonSettings(String filename)
	{
		DebugMode = FabricLoader.getInstance().isDevelopmentEnvironment();

		BiomeToSeasonType = new HashMap<>();

		m_Options = ConfigurationOptions.defaults()
				.serializers(builder -> builder.register(Identifier.class, IdentifierSerializer.Instance))
				.shouldCopyDefaults(true);

		load(filename);
	}

	public SeasonTypes getSeasonType(Identifier biomeId)
	{
		return BiomeToSeasonType.getOrDefault(biomeId, SeasonTypes.FourSeasonPerYear);
	}

	public Map<Identifier, SeasonTypes> biomeToSeasonsSupplier()
	{
		Map<Identifier, SeasonTypes> biomeToSeasonType = new HashMap<>();
		biomeToSeasonType.put(BiomeKeys.BAMBOO_JUNGLE.getValue(), SeasonTypes.TropicalSeason);
		biomeToSeasonType.put(new Identifier("plains"), SeasonTypes.FourSeasonPerYear);
		return biomeToSeasonType;
	}

	public void register(Identifier id, SeasonTypes type, boolean replace)
	{
		if (replace)
			BiomeToSeasonType.replace(id, type);
		else
			BiomeToSeasonType.putIfAbsent(id, type);
	}

	public void load(String filename)
	{
		File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), filename);
		var loader = HoconConfigurationLoader.builder().defaultOptions(m_Options).file(configFile).build();
		CommentedConfigurationNode root;
		try
		{
			root = loader.load();

			Config = root.get(Config.class);
			if (Config != null && Config.BiomeToSeasonTypeOverrides != null)
			{
				BiomeToSeasonType.putAll(Config.BiomeToSeasonTypeOverrides);
			}

			loader.save(root);
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

	public void save(String filename)
	{
		File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), filename);
		var loader = HoconConfigurationLoader.builder().defaultOptions(m_Options).file(configFile).build();
		CommentedConfigurationNode root;
		try
		{
			root = loader.load();
			root.set(Config.class, Config);
			loader.save(root);
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
}

@ConfigSerializable class Config
{
	@Setting("TicksPerDay") public int TicksPerDay = 24000;
	@Setting("DaysPerMonth") @Comment("Minecraft day is 20 minutes. Default is 7 (2.3 irl hours)") public int DaysPerMonth = 7;
	@Setting("MonthsPerYear") public int MonthsPerYear = 12;
	@Setting("DaysPerSeason") @Comment("Around 7 irl hours per season") public int DaysPerSeason = 7 * 4;
	@Setting("MaxSeasons") public int MaxSeasons = 4;
	@Setting("BiomeToSeasonTypeOverrides") public Map<Identifier, SeasonTypes> BiomeToSeasonTypeOverrides;
}
