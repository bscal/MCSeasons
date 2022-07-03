package me.bscal.seasons.client.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

@Environment(EnvType.CLIENT)
public interface ClientCommandRegistration
{

	LiteralArgumentBuilder<FabricClientCommandSource> register();

}
