package org.mossmc.mosscg.MossFrp;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.mossmc.mosscg.MossFrp.Command.CommandVelocity;

import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(
        id = "mossfrp",
        name = BasicInfo.getName,
        version = BasicInfo.getVersion,
        description = "A plugin which can run Frp with server.",
        authors = {BasicInfo.getAuthor}
)
public class PathVelocity {
    public static ProxyServer server;
    public static Logger logger;
    public static Path dataDirectory;

    @Inject
    public PathVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        PathVelocity.server = server;
        PathVelocity.logger = logger;
        PathVelocity.dataDirectory = dataDirectory;
        CommandManager commandManager = server.getCommandManager();
        CommandMeta meta = commandManager.metaBuilder("mossfrp").build();
        commandManager.register(meta, new CommandVelocity());
        BasicInfo.getVelocityInstance = this;
        BasicInfo.getDataFolder = dataDirectory;
        BasicInfo.getRunMode = BasicInfo.runMode.velocity;
        StartGuide.runGuide(null);
    }

    public static Logger getLogger() {
        return logger;
    }
}
