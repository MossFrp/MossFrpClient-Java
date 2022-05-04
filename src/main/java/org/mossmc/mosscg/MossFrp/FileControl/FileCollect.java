package org.mossmc.mosscg.MossFrp.FileControl;

import org.mossmc.mosscg.MossFrp.BasicInfo;
import org.mossmc.mosscg.MossFrp.Config.ConfigGet;
import org.mossmc.mosscg.MossFrp.FrpControl.FrpCache;
import org.mossmc.mosscg.MossFrp.Language.LanguageLoad;
import org.mossmc.mosscg.MossFrp.Time.TimeDate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import static org.mossmc.mosscg.MossFrp.BasicVoid.*;

public class FileCollect {
    public static void createCollect() {
        try {
            sendInfo("#lang#File_CollectStart");
            String fileName = "MossFrp_Report_"+TimeDate.getNowTimeFull()+".txt";
            String reportPath = BasicInfo.getDataFolder +"/report/"+fileName;
            StringBuilder info;
            info = new StringBuilder();
            info.append("--=MossFrp Client Info Collection=--");
            info.append("\r\n").append("Create time: ").append(TimeDate.getNowTimeFull());

            info.append("\r\n");
            info.append("\r\n").append(">>>System Info<<<");
            info.append("\r\n").append("System name: ").append(BasicInfo.getSystemName);
            info.append("\r\n").append("System type: ").append(BasicInfo.getSystemType.name());
            info.append("\r\n").append("System arch: ").append(System.getProperty("os.arch"));
            info.append("\r\n").append("System version: ").append(System.getProperty("os.version"));
            info.append("\r\n").append("System user name: ").append(System.getProperty("user.name"));
            info.append("\r\n").append("System user home: ").append(System.getProperty("user.home"));

            info.append("\r\n").append("Java version: ").append(System.getProperty("java.version"));
            info.append("\r\n").append("Java provider: ").append(System.getProperty("java.vendor"));
            info.append("\r\n").append("Java path: ").append(System.getProperty("java.home"));
            info.append("\r\n").append("Java class path: ").append(System.getProperty("java.class.path"));
            info.append("\r\n").append("Java execute dir: ").append(System.getProperty("java.ext.dirs"));

            info.append("\r\n");
            info.append("\r\n").append(">>>Client Info<<<");
            info.append("\r\n").append("Client version: ").append(BasicInfo.getVersion);
            info.append("\r\n").append("Client run mode: ").append(BasicInfo.getRunMode.name());
            info.append("\r\n").append("Client language: ").append(LanguageLoad.languageName);
            info.append("\r\n").append("Client dir: ").append(System.getProperty("user.dir"));

            info.append("\r\n");
            info.append("\r\n").append(">>>Runtime Info<<<");
            info.append("\r\n").append("Domain list: ").append(BasicInfo.listDomains.toString());
            info.append("\r\n").append("Config cache: ").append(ConfigGet.configMap.toString());
            info.append("\r\n").append("Frp list: ").append(FrpCache.frpList.toString());
            info.append("\r\n").append("Frp cache: ").append(FrpCache.frpCache.toString());
            info.append("\r\n").append("Frp status cache: ").append(FrpCache.frpStatusCache.toString());
            info.append("\r\n").append("Frp config cache: ").append(FrpCache.configCache.toString());

            info.append("\r\n");
            info.append("\r\n").append(">>>Client logs<<<");
            inputAllFrpLog(info);
            inputAllClientLog(info);

            BufferedWriter getWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(reportPath, false), StandardCharsets.UTF_8));
            getWriter.write(info.toString());
            getWriter.flush();
            getWriter.close();
            sendInfo(getLanguage("File_CollectComplete").replace("[name]",fileName));
        } catch (Exception e) {
            sendException(e);
            sendWarn("#lang#File_CollectFailed");
        }
    }

    public static void inputAllFrpLog(StringBuilder info) {
        File[] tempList = FileGet.getLogDirFile.listFiles();
        if (tempList == null) {
            return;
        }
        for (File value : tempList) {
            if (value.isDirectory()) {
                try {

                    String fileName = value.getName();
                    String[] cut = fileName.split("\\.");
                    String name = cut[0];


                } catch (Exception e) {
                    sendException(e);

                }
            }
        }
    }

    public static void inputAllClientLog(StringBuilder info) {

    }
}
