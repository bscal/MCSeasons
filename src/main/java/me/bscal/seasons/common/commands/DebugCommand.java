package me.bscal.seasons.common.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.bscal.seasons.Seasons;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.logging.log4j.Level;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DebugCommand implements Command<ServerCommandSource>, CommandRegistrationCallback
{
	@Override
	public int run(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException
	{
		int level = ctx.getArgument("level", int.class);

		Level logLevel = Level.OFF;
		if (level == 1)
			logLevel = Level.INFO;
		else if (level == 2)
			logLevel = Level.ALL;

		Seasons.LOGGER.setLevel(logLevel);

		return 0;
	}

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated)
	{
		dispatcher.register(literal("seasons")
				.then(literal("debug")
						.requires(src -> src.hasPermissionLevel(4))
						.then(literal("setlevel")
								.then(argument("level", IntegerArgumentType.integer(0, 2))
										.executes(this)))));
	}
}
