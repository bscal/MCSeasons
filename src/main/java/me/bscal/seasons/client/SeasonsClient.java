package me.bscal.seasons.client;

import me.bscal.seasons.common.SeasonTimer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT) public class SeasonsClient implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		ClientPlayNetworking.registerGlobalReceiver(SeasonTimer.CHANNEL_NAME, (client, handler, buf, responseSender) -> {
			long totalTicks = buf.readLong();
			long currentTicks = buf.readLong();
			int day = buf.readShort();
			int month = buf.readShort();
			int year = buf.readInt();
			int seasonalSectionTracker = buf.readByte();
			client.execute(() -> SeasonTimer.GetOrCreate(client.world).readFromServer(totalTicks, currentTicks, day, month, year, seasonalSectionTracker));
		});
	}
}
