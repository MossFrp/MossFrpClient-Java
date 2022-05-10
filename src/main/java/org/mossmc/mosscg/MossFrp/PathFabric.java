package org.mossmc.mosscg.MossFrp;

import net.fabricmc.api.ModInitializer;
import org.mossmc.mosscg.MossFrpFabricSupport.MossFrpFabricSupport;

import java.io.File;

public class PathFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        BasicInfo.getFabricInstance = this;
        String configPath = "./config/MossFrp";
        BasicInfo.getDataFolder = new File(configPath).toPath();
        BasicInfo.getRunMode = BasicInfo.runMode.fabric;
        StartGuide.runGuide(null);
    }

    public static void sendPlayer(String info) {
        MossFrpFabricSupport.sendPlayer(info);
    }
}
