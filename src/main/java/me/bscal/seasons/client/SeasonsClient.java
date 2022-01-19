package me.bscal.seasons.client;

import me.bscal.seasons.common.seasons.SeasonTimer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT) public class SeasonsClient implements ClientModInitializer
{

	public static ClientConfig ClientConfig;

	@Override
	public void onInitializeClient()
	{
		ClientConfig.readConfigFromFile();

		ClientPlayNetworking.registerGlobalReceiver(SeasonTimer.CHANNEL_NAME, (client, handler, buf, responseSender) -> {
			long totalTicks = buf.readLong();
			long currentTicks = buf.readLong();
			int day = buf.readShort();
			int month = buf.readShort();
			int year = buf.readInt();
			int daysInCurrentSeasons = buf.readShort();
			int seasonalSectionTracker = buf.readByte();
			client.execute(() -> SeasonTimer.GetOrCreate().readFromServer(totalTicks, currentTicks, day, month, year, daysInCurrentSeasons, seasonalSectionTracker));
		});
	}

	public static ClientConfig getConfig()
	{
		return ClientConfig;
	}

}
