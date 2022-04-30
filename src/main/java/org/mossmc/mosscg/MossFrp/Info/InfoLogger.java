package org.mossmc.mosscg.MossFrp.Info;

import org.mossmc.mosscg.MossFrp.BasicInfo;
import org.mossmc.mosscg.MossFrp.Time.TimeDate;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class InfoLogger {
    public static BufferedWriter getWriter;
    public static String logPath;

    public static void loadWriter() throws IOException {
        logPath = BasicInfo.getDataFolder +"/logs/"+TimeDate.getNowTimeFull()+".yml";
        getWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logPath, true), StandardCharsets.UTF_8));
    }

    public static void logMessage(String message) {
        fileInput(message);
    }

    public static void fileInput(String info) {
        try {
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
