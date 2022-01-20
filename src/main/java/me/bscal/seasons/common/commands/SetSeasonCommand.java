package me.bscal.seasons.common.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.bscal.seasons.common.seasons.SeasonTimer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SetSeasonCommand implements Command<ServerCommandSource>, CommandRegistrationCallback
{
	private static final String SEASON_ARG = "season";

	@Override
	public int run(CommandContext<ServerCommandSource> ctx)
	{
		SeasonTimer.GetOrCreate().setSeason(ctx.getArgument(SEASON_ARG, int.class));
		return 0;
	}

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated)
	{
		dispatcher.register(literal("season").then(literal("set").then(argument(SEASON_ARG, IntegerArgumentType.integer(0, 3)).executes(this))));
	}
}
