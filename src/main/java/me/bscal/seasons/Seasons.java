package me.bscal.seasons;

import me.bscal.seasons.common.Config;
import me.bscal.seasons.common.commands.DebugCommand;
import me.bscal.seasons.common.commands.SetSeasonCommand;
import me.bscal.seasons.common.seasons.SeasonTimer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.util.Optional;

public class Seasons implements ModInitializer
{
	public static Seasons Instance;

	public static final String MOD_ID = "seasons";
	public static final org.apache.logging.log4j.core.Logger LOGGER = (Logger) LogManager.getLogger("Seasons");
	public static Config<Config.ServerSettings> ServerConfig;

	private static MinecraftServer m_Server;

	@Override
	public void onInitialize()
	{
		Instance = this;
		LOGGER.setLevel(Level.OFF);
		ServerConfig = Config.initServerConfig();

		CommandRegistrationCallback.EVENT.register(new SetSeasonCommand());
		CommandRegistrationCallback.EVENT.register(new DebugCommand());

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			m_Server = server;
			ServerConfig.load();
		});

		ServerLifecycleEvents.SERVER_STARTED.register(server -> SeasonTimer.getOrCreate());
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> ServerConfig.save());
	}

	public MinecraftServer getServer()
	{
		return m_Server;
	}

	public Optional<ServerWorld> getOverWorld()
	{
		if (m_Server == null)
			return Optional.empty();
		return Optional.of(m_Server.getOverworld());
	}
}
