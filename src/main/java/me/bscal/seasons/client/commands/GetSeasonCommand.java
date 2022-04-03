package me.bscal.seasons.client.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.bscal.seasons.common.seasons.BiomeToSeasonMapper;
import me.bscal.seasons.common.seasons.SeasonTimer;
import me.bscal.seasons.common.seasons.SeasonType;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

public class GetSeasonCommand implements Command<FabricClientCommandSource>, ClientCommandRegistration
{
	@Override
	public int run(CommandContext<FabricClientCommandSource> context)
	{
		var sender = context.getSource().getClient().player;
		var world = context.getSource().getWorld();
		var biome = (sender == null) ? null : world.getBiome(sender.getBlockPos()).value();
		var seasonTime = SeasonTimer.getOrCreate();

		var season = seasonTime.getSeason(biome);
		var date = seasonTime.getDate();
		var seasonType = BiomeToSeasonMapper.getSeasonalType(biome);

		String msg = String.format("Season : %s (%s), Date: %s", season, seasonType, date);
		MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(msg));
		return 0;
	}

	@Override
	public LiteralArgumentBuilder<FabricClientCommandSource> register()
	{
		return literal("seasonsc")
				.then(literal("info")
						.requires(src -> src.hasPermissionLevel(4))
						.executes(this));
	}
}
