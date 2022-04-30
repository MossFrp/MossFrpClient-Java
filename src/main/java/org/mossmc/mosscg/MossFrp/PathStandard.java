package org.mossmc.mosscg.MossFrp;

import java.io.File;

public class PathStandard {
    //独立端引导类
    public static void main(String[] args) {
        BasicInfo.getRunMode = BasicInfo.runMode.standard;
        BasicInfo.getDataFolder = new File("./MossFrp").toPath();
        StartGuide.runGuide(args);
    }
}
