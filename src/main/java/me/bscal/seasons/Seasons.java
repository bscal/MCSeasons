package me.bscal.seasons;

import me.bscal.seasons.common.Config;
import me.bscal.seasons.common.commands.DebugCommand;
import me.bscal.seasons.common.commands.SetSeasonCommand;
import me.bscal.seasons.common.seasons.SeasonClimateManager;
import me.bscal.seasons.common.seasons.SeasonStatsGlobals;
import me.bscal.seasons.common.seasons.SeasonTimer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.util.Objects;
import java.util.Optional;

public class Seasons implements ModInitializer
{
    public static Seasons Instance;

    public static final String MOD_ID = "seasons";
    public static final org.apache.logging.log4j.core.Logger LOGGER = (Logger) LogManager.getLogger("Seasons");
    public static Config<Config.ServerSettings> ServerConfig;
    public static Random Random;
    private static MinecraftServer m_Server;

    @Override
    public void onInitialize()
    {
        Instance = this;
        LOGGER.setLevel(Level.INFO);
        ServerConfig = Config.initServerConfig();
        SeasonStatsGlobals.tryLoadFromConfig();
        Random = net.minecraft.util.math.random.Random.create();

        CommandRegistrationCallback.EVENT.register(new SetSeasonCommand());
        CommandRegistrationCallback.EVENT.register(new DebugCommand());

        ServerLifecycleEvents.SERVER_STARTED.register(server ->
        {
            m_Server = server;
            ServerConfig.load();
            SeasonClimateManager.init(server);
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> SeasonTimer.getOrCreate());
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> ServerConfig.save());
    }

    public MinecraftServer getServer()
    {
        assert m_Server != null: "m_Server must not be null. Are you calling getServer() from client?";
        return m_Server;
    }
}
