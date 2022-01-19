package me.bscal.seasons.client;

import com.oroarmor.config.Config;
import com.oroarmor.config.ConfigItem;
import com.oroarmor.config.ConfigItemGroup;
import me.bscal.seasons.Seasons;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.util.List;

import static java.util.List.of;

@Environment(EnvType.CLIENT)
public class ClientConfig extends Config
{
	public static final List<ConfigItemGroup> configs = of(new Root());

	/**
	 * Creates a new config
	 */
	public ClientConfig()
	{
		super(configs, new File(FabricLoader.getInstance().getConfigDir().toFile(), "seasons_client_cfg.json"), Seasons.MOD_ID);
	}

	public static class Root extends ConfigItemGroup
	{

		public static final ConfigItem<SeasonsGraphicsLevel> GraphicsLevel = new ConfigItem<>("seasons_graphics_level", SeasonsGraphicsLevel.Fancy, "seasons_graphics_level");
		public static final ConfigItem<Boolean> EnableFallColors = new ConfigItem<>("enable_fall_colors", true, "enable_fall_leaves");

		/**
		 * Creates a new {@link ConfigItemGroup} with the list of configs and the name
		 */
		public Root()
		{
			super(of(GraphicsLevel, EnableFallColors), "root");
		}
	}


	public enum SeasonsGraphicsLevel
	{
		Disabled,
		Fast,
		Fancy
	}

}
