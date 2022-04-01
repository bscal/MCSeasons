package me.bscal.seasons.client;

import me.bscal.seasons.Seasons;
import me.bscal.seasons.client.biome.BiomeSeasonHandler;
import me.bscal.seasons.client.commands.GetSeasonCommand;
import me.bscal.seasons.client.particles.FallingLeavesParticle;
import me.bscal.seasons.common.Config;
import me.bscal.seasons.common.seasons.SeasonTimer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT) public class SeasonsClient implements ClientModInitializer
{

	public static Config<Config.ClientSettings> ClientConfig;
	public static BiomeSeasonHandler SeasonHandler;

	@Override
	public void onInitializeClient()
	{
		ClientConfig = Config.initClientConfig();

		SeasonHandler = new BiomeSeasonHandler();

		Registry.register(Registry.PARTICLE_TYPE, Seasons.MOD_ID + ":falling_leaves", FallingLeavesParticle.FALLING_LEAVES);
		ParticleFactoryRegistry.getInstance().register(FallingLeavesParticle.FALLING_LEAVES, FallingLeavesParticle.Factory::new);

		ClientCommandManager.DISPATCHER.register(new GetSeasonCommand().register());

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> SeasonHandler.reload(client.world));
		ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> ClientConfig.save()));

		ClientPlayNetworking.registerGlobalReceiver(SeasonTimer.CHANNEL_NAME, (client, handler, buf, responseSender) -> {
			long totalTicks = buf.readLong();
			long currentTicks = buf.readLong();
			int days = buf.readInt();
			int daysInCurrentSeasons = buf.readShort();
			int seasonalSectionTracker = buf.readByte();
			boolean seasonChanged = buf.readBoolean();
			client.execute(() ->  {
				SeasonTimer.getOrCreate().readFromServer(totalTicks, currentTicks, days, daysInCurrentSeasons, seasonalSectionTracker);
				if (seasonChanged)
				{
					MinecraftClient.getInstance().worldRenderer.reload();
				}
			});
		});
	}

}
