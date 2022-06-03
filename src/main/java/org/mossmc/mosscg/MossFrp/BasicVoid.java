package org.mossmc.mosscg.MossFrp;

import org.bukkit.Bukkit;
import org.mossmc.mosscg.MossFrp.Config.ConfigCodeLoad;
import org.mossmc.mosscg.MossFrp.Config.ConfigGet;
import org.mossmc.mosscg.MossFrp.Config.ConfigLoad;
import org.mossmc.mosscg.MossFrp.FrpControl.FrpCache;
import org.mossmc.mosscg.MossFrp.FrpControl.FrpStop;
import org.mossmc.mosscg.MossFrp.Info.InfoException;
import org.mossmc.mosscg.MossFrp.Info.InfoSender;
import org.mossmc.mosscg.MossFrp.Language.LanguageGet;
import org.mossmc.mosscg.MossFrp.Language.LanguageLoad;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasicVoid {
    //消息发送部分
    //直接调用就可以发送消息
    //别用System.out.printIn()了！！！
    //包括后边try catch都是用这里的sendException
    //不然log日志不记录
    public static void sendCommand(String command) {
        InfoSender.send(command,"command");
    }

    public static void sendDebug(String debug) {
        InfoSender.send(debug,"debug");
    }

    public static void sendInfo(String info) {
        InfoSender.send(info,"info");
    }

    public static void sendWarn(String warn) {
        InfoSender.send(warn,"warn");
    }

    public static void sendError(String error) {
        InfoSender.send(error,"error");
    }

    public static void sendException(Exception exception) {
        InfoException.exception(exception);
    }

    public static String getConfig(String key) {
        return ConfigGet.getConfig(key);
    }

    public static String getLanguage(String key) {
        return LanguageGet.getLanguage(key);
    }

    public static void reloadMossFrp() {
        sendInfo("#lang#Command_ReloadStart");
        FrpStop.stopAll();
        FrpCache.frpCache.clear();
        FrpCache.configCache.clear();
        FrpCache.frpStatusCache.clear();
        FrpCache.frpList.clear();
        System.gc();
        ConfigLoad.load();
        LanguageLoad.load(BasicVoid.getConfig("language"));
        ConfigCodeLoad.loadAll();
        sendInfo("#lang#Command_ReloadComplete");
    }

    public static boolean isContainChinese(String str) {
        Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    public static int getLocalPort() {
        try {
            switch (BasicInfo.getRunMode) {
                case plugin:
                    return Bukkit.getPort();
                default:
                    return 25565;
            }
        } catch (Exception e) {
            sendException(e);
        }
        return 25565;
    }
}
