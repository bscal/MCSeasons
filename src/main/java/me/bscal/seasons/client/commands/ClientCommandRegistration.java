package me.bscal.seasons.client.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

public interface ClientCommandRegistration
{

	LiteralArgumentBuilder<FabricClientCommandSource> register();

}