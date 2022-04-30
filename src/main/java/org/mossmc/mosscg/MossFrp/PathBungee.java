package org.mossmc.mosscg.MossFrp;

import net.md_5.bungee.api.plugin.Plugin;

import net.md_5.bungee.api.plugin.PluginManager;
import org.mossmc.mosscg.MossFrp.Command.CommandBungee;

public class PathBungee extends Plugin {
    @Override
    public void onEnable() {
        BasicInfo.getBungeeInstance = this;
        BasicInfo.getDataFolder = this.getDataFolder().toPath();
        BasicInfo.getRunMode = BasicInfo.runMode.bungee;
        PluginManager pluginManager = this.getProxy().getPluginManager();
        pluginManager.registerCommand(this, new CommandBungee());
        StartGuide.runGuide(null);
    }
}
