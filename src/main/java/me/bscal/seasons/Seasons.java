package me.bscal.seasons;

import me.bscal.seasons.common.SeasonSettings;
import me.bscal.seasons.common.SeasonTimer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class Seasons implements ModInitializer
{
	public static Seasons Instance;

	public static final String MOD_ID = "seasons";
	public static final String MOD_NAME = "Seasons";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	public static final String SETTINGS_FILE = "seasons.conf";
	private static SeasonSettings Settings;
	private MinecraftServer Server;

	@Override
	public void onInitialize()
	{
		Instance = this;

		Settings = new SeasonSettings(SETTINGS_FILE);

		ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
			Server = server;
			Settings.load(SETTINGS_FILE);
			SeasonTimer.GetOrCreate();
		});

		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			Settings.save(SETTINGS_FILE);
		});
	}

	public static SeasonSettings getSettings()
	{
		return Settings;
	}

	public MinecraftServer getServer()
	{
		return Server;
	}

	public Optional<ServerWorld> getOverWorld()
	{
		if (Server == null) return Optional.empty();
		return Optional.of(Server.getOverworld());
	}
}
