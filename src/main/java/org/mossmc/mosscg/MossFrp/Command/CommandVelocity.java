package org.mossmc.mosscg.MossFrp.Command;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import net.kyori.adventure.text.Component;
import org.mossmc.mosscg.MossFrp.BasicVoid;

public class CommandVelocity implements SimpleCommand {
    @Override
    public void execute(final Invocation invocation) {
        if (!isConsole(invocation)) {
            invocation.source().sendMessage(Component.text(BasicVoid.getLanguage("Command_ConsoleOnly")));
        }
        String[] args = invocation.arguments();
        CommandRead.read(args);
    }

    public static boolean isConsole(Invocation invocation) {
        return invocation.source() instanceof ConsoleCommandSource;
    }
}
