package me.bscal.seasons.client.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.bscal.seasons.common.seasons.SeasonTimer;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

public class GetSeasonCommand implements Command<FabricClientCommandSource>, ClientCommandRegistration
{
	@Override
	public int run(CommandContext<FabricClientCommandSource> context)
	{
		var season = SeasonTimer.getOrCreate().getGenericSeason();
		var seasonId = SeasonTimer.getOrCreate().getInternalSeasonId();
		var date = SeasonTimer.getOrCreate().getDate();
		MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(String.format("Season: %s, SeasonId: %d, Date: %s", season, seasonId, date)));
		return 0;
	}

	@Override
	public LiteralArgumentBuilder<FabricClientCommandSource> register()
	{
		return literal("seasoninfo")
				.requires(src -> src.hasPermissionLevel(4))
				.executes(this);
	}
}
