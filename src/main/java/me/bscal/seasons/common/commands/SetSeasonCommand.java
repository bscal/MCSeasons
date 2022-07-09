package me.bscal.seasons.common.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.bscal.seasons.common.seasons.Season;
import me.bscal.seasons.common.seasons.SeasonTimer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SetSeasonCommand implements Command<ServerCommandSource>, CommandRegistrationCallback
{

    private static final String SEASON_ARG = "season";

    @Override
    public int run(CommandContext<ServerCommandSource> ctx)
    {
        var season = Season.values()[ctx.getArgument(SEASON_ARG, int.class)];
        SeasonTimer.get().setSeason(season, 0);
        return 0;
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment)
    {
        dispatcher.register(literal("seasons")
                .then(literal("set")
                        .requires(src -> src.hasPermissionLevel(4))
                        .then(argument(SEASON_ARG, IntegerArgumentType.integer(0, 3))
                                .executes(this))));
    }
}
