package org.mossmc.mosscg.MossFrp.FileControl;

import org.mossmc.mosscg.MossFrp.BasicInfo;

import java.io.File;

public class FileLoad {
    public static void load() {
        FileGet.getMainDirFile = new File(BasicInfo.getDataFolder.toString());
        FileGet.getLogDirFile = new File(BasicInfo.getDataFolder+"/logs");
        FileGet.getLanguageDirFile = new File(BasicInfo.getDataFolder+"/languages");
        FileGet.getFrpConfigDirFile = new File(BasicInfo.getDataFolder+"/configs");
        FileGet.getFrpDirFile = new File(BasicInfo.getDataFolder+"/frps");
        FileGet.getReportDirFile = new File(BasicInfo.getDataFolder+"/report");

        FileGet.getLanguageChineseFile = new File(BasicInfo.getDataFolder+"/languages/zh_cn.yml");
        FileGet.getLanguageEnglishFile = new File(BasicInfo.getDataFolder+"/languages/en_us.yml");

        FileGet.getConfigFile = new File(BasicInfo.getDataFolder+"/config.yml");
        FileGet.getExampleFile = new File(BasicInfo.getDataFolder+"/configs/example.yml");
        FileGet.getProcessFile = new File(BasicInfo.getDataFolder+"/frps/MossFrpProcess.jar");
    }
}
