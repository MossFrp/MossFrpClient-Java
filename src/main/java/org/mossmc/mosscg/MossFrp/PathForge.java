package org.mossmc.mosscg.MossFrp;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import org.mossmc.mosscg.MossFrpForgeSuport.MossFrpForgeSupport;

import java.io.File;
@Mod(
        value = "mossfrp",
        modid = "mossfrp",
        name = BasicInfo.getName,
        version = BasicInfo.getVersion,
        dependencies = "before:mossfrpforgesupport@[1.0,);"
)
public class PathForge {
    public static Logger logger;

    public static Logger getLogger() {
        return logger;
    }

    @Mod.EventHandler
    public void preLoad(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        BasicInfo.getForgeInstance = this;
        String configPath = event.getModConfigurationDirectory().toPath()+"/MossFrp";
        BasicInfo.getDataFolder = new File(configPath).toPath();
        BasicInfo.getRunMode = BasicInfo.runMode.forge;
        StartGuide.runGuide(null);
    }
    public static void sendPlayer(String info) {
        MossFrpForgeSupport.sendPlayer(info);
    }
}
