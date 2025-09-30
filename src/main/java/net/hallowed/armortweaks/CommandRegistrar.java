package net.hallowed.armortweaks;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.minecraft.commands.Commands.literal;

@Mod.EventBusSubscriber(modid = ArmorTweaks.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CommandRegistrar {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent e) {
        LiteralArgumentBuilder<CommandSourceStack> root = literal("armortweaks")
                .requires(src -> src.hasPermission(2))
                .then(literal("reload")
                        .executes(ctx -> {
                            ArmorOverrides.load();

                            int n = ArmorOverrides.size();
                            ctx.getSource().sendSuccess(
                                    () -> Component.literal("[ArmorTweaks] Reloaded overrides (" + n + " armor overrides)"),
                                    true
                            );
                            return Command.SINGLE_SUCCESS;
                        })
                );

        e.getDispatcher().register(root);
    }

    private CommandRegistrar() {}
}
