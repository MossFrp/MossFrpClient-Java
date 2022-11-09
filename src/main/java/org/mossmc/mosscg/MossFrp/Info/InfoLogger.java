package org.mossmc.mosscg.MossFrp.Info;

import org.mossmc.mosscg.MossFrp.BasicInfo;
import org.mossmc.mosscg.MossFrp.BasicVoid;
import org.mossmc.mosscg.MossFrp.Config.ConfigGet;
import org.mossmc.mosscg.MossFrp.Time.TimeDate;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class InfoLogger {
    public static BufferedWriter getWriter;
    public static String logPath;

    public static boolean logEnabled = true;
    public static boolean firstLoad = true;

    public static void loadWriter() throws IOException {
        logPath = BasicInfo.getDataFolder +"/logs/"+TimeDate.getNowTimeFull()+".yml";
        getWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logPath, true), StandardCharsets.UTF_8));
    }

    public static void setLogEnabled() {
        firstLoad = false;
        if (ConfigGet.configMap.containsKey("disableLog")) {
            if (BasicVoid.getConfig("disableLog").equals("true")) {
                logEnabled = false;
            }
        }
    }

    public static void logMessage(String message) {
        fileInput(message);
    }

    public static void fileInput(String info) {
        try {
            if (firstLoad) {
                setLogEnabled();
            }
            if (!logEnabled) {
                return;
            }
            if (getWriter == null) {
                loadWriter();
            }
            getWriter.write("["+ TimeDate.getNowTimeFull()+"] "+info+"\r\n");
            getWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
