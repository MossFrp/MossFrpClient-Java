package org.mossmc.mosscg.MossFrp.Command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.command.ConsoleCommandSender;
import org.mossmc.mosscg.MossFrp.BasicVoid;

public class CommandBungee extends Command {
    public CommandBungee() {
        super("mossfrp");
    }

    public void execute(CommandSender sender, String[] strings) {
        if (!isConsole(sender)) {
            sender.sendMessage(new TextComponent(BasicVoid.getLanguage("Command_ConsoleOnly")));
        }
        CommandRead.read(strings);
    }

    public static boolean isConsole(CommandSender sender) {
        return sender instanceof ConsoleCommandSender;
    }
}
