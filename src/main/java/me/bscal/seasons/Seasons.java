package me.bscal.seasons;

import me.bscal.seasons.common.SeasonTimer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Seasons implements ModInitializer
{
	public static Seasons Instance;

	public static final String MOD_ID = "seasons";
	public static final String MOD_NAME = "Seasons";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	private MinecraftServer Server;

	@Override
	public void onInitialize()
	{
		ServerLifecycleEvents.SERVER_STARTED.register((server) ->
		{
			Server = server;
			SeasonTimer.GetOrCreate(server.getOverworld());
		});
	}

	public MinecraftServer getServer() { return Server; }
}
