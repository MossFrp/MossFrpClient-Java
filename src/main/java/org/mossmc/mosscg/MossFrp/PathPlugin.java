package org.mossmc.mosscg.MossFrp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.mossmc.mosscg.MossFrp.Command.CommandRead;

public class PathPlugin extends JavaPlugin {
    //插件端引导类
    @Override
    public void onEnable() {
        BasicInfo.getPluginInstance = this;
        BasicInfo.getDataFolder = this.getDataFolder().toPath();
        BasicInfo.getRunMode = BasicInfo.runMode.plugin;
        StartGuide.runGuide(null);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!isConsole(sender)) {
            sender.sendMessage(BasicVoid.getLanguage("Command_ConsoleOnly"));
            return false;
        }
        CommandRead.read(args);
        return true;
    }

    public static boolean isConsole(CommandSender sender) {
        return sender instanceof ConsoleCommandSender;
    }
}
