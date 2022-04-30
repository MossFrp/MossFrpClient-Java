package org.mossmc.mosscg.MossFrp.Info;

import org.mossmc.mosscg.MossFrp.BasicInfo;
import org.mossmc.mosscg.MossFrp.Language.LanguageGet;
import org.mossmc.mosscg.MossFrp.PathVelocity;
import org.mossmc.mosscg.MossFrp.Time.TimeDate;

public class InfoSender {
    public static void send(String info,String type) {
        if (info.startsWith("#lang#")) {
            info = info.replace("#lang#","");
            info = LanguageGet.getLanguage(info);
        }
        InfoLogger.logMessage(BasicInfo.getInfoPrefix(type)+info);
        if (BasicInfo.getRunMode.equals(BasicInfo.runMode.standard)) {
            System.out.println(TimeDate.getNowTime()+BasicInfo.getInfoPrefix(type)+info);
        }
        if (BasicInfo.getRunMode.equals(BasicInfo.runMode.plugin)) {
            if (type.equals("warn")||type.equals("exception")) {
                BasicInfo.getPluginInstance.getLogger().warning(info);
                return;
            }
            BasicInfo.getPluginInstance.getLogger().info(info);
        }
        if (BasicInfo.getRunMode.equals(BasicInfo.runMode.bungee)) {
            if (type.equals("warn")||type.equals("exception")) {
                BasicInfo.getBungeeInstance.getLogger().warning(info);
                return;
            }
            BasicInfo.getBungeeInstance.getLogger().info(info);
        }
        if (BasicInfo.getRunMode.equals(BasicInfo.runMode.velocity)) {
            if (type.equals("warn")||type.equals("exception")) {
                PathVelocity.getLogger().warning(info);
                return;
            }
            PathVelocity.getLogger().info(info);
        }
    }
}
