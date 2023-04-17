package me.bscal.seasons.client.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.bscal.seasons.common.seasons.SeasonClimateManager;
import me.bscal.seasons.common.seasons.SeasonTimer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

@Environment(EnvType.CLIENT)
public class GetSeasonCommand implements Command<FabricClientCommandSource>, ClientCommandRegistration
{
    @Override
    public int run(CommandContext<FabricClientCommandSource> context)
    {
        var sender = context.getSource().getClient().player;
        var world = context.getSource().getWorld();
        var biome = (sender == null) ? null : world.getBiome(sender.getBlockPos()).value();

        var seasonTimer = SeasonTimer.get();
        var season = seasonTimer.CurrentSeason;
        var currentTick = seasonTimer.CurrentTicks;
        var date = seasonTimer.getDate();
        var seasonType = SeasonClimateManager.getSeasonType(biome);
        String msg = String.format("Season : %s (%s), TickTime: %d, Date: %s", season, seasonType, currentTick, date);
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
