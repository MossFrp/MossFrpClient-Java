package org.mossmc.mosscg.MossFrp.Config;

import org.mossmc.mosscg.MossFrp.BasicInfo;
import org.mossmc.mosscg.MossFrp.FileControl.FileFrp;
import org.mossmc.mosscg.MossFrp.FileControl.FileGet;
import org.mossmc.mosscg.MossFrp.FrpControl.FrpCache;
import org.mossmc.mosscg.MossFrp.FrpControl.FrpRun;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import static org.mossmc.mosscg.MossFrp.BasicVoid.*;

public class ConfigCodeLoad {
    public static void loadAll() {
        File[] tempList = FileGet.getFrpConfigDirFile.listFiles();
        if (tempList == null) {
            return;
        }
        for (File value : tempList) {
            if (value.isFile()) {
                try {
                    String fileName = value.getName();
                    String[] cut = fileName.split("\\.");
                    String name = cut[0];
                    sendInfo(getLanguage("Load_LoadFile").replace("[file]",value.getName()));
                    load(name);
                } catch (Exception e) {
                    sendException(e);
                    sendWarn(getLanguage("Load_LoadFileFailed").replace("[file]",value.getName()));
                }
            }
        }
    }

    public static void load(String name) throws Exception {
        Yaml yaml = new Yaml();
        FileInputStream input = null;
        String configFile = FileGet.getFrpConfigDirFile.toPath() +"/"+name+".yml";
        try {
            input = new FileInputStream(configFile);
        } catch (FileNotFoundException e) {
            sendException(e);
        }
        Map<?,?> config = yaml.loadAs(input, Map.class);
        if (!config.get("enable").toString().equals("true")) {
            sendInfo(getLanguage("Load_NotEnableFile").replace("[file]",name+".yml"));
            return;
        }
        FrpCache.inputConfigCache(name,config);
        FrpCache.frpList.add(name);
        FrpCache.frpStatusCache.put(name,BasicInfo.frpStatus.starting);
        read(name);
        FileFrp.writeFrpSettings(name);
        FrpRun.runFrp(name);
        sendInfo(getLanguage("Load_LoadFileSucceed").replace("[file]",name+".yml"));
    }

    public static void read(String name) {
        FrpCache.inputFrpCache(name,"protocol",FrpCache.getConfigCache(name,"protocol"));
        FrpCache.inputFrpCache(name,"remoteAddress",FrpCache.getConfigCache(name,"remoteIP"));
        FrpCache.inputFrpCache(name,"remotePort",FrpCache.getConfigCache(name,"remotePort"));
        FrpCache.inputFrpCache(name,"localAddress",FrpCache.getConfigCache(name,"localIP"));
        FrpCache.inputFrpCache(name,"localPort",FrpCache.getConfigCache(name,"localPort"));
        FrpCache.inputFrpCache(name,"openPort",FrpCache.getConfigCache(name,"openPort"));
        FrpCache.inputFrpCache(name,"token",FrpCache.getConfigCache(name,"token"));
        FrpCache.inputFrpCache(name,"type",FrpCache.getConfigCache(name,"type"));
        FrpCache.inputFrpCache(name,"useCompression",FrpCache.getConfigCache(name,"use_compression"));
        FrpCache.inputFrpCache(name,"useEncryption",FrpCache.getConfigCache(name,"use_encryption"));
        FrpCache.inputFrpCache(name,"proxyProtocolVersion",FrpCache.getConfigCache(name,"proxy_protocol_version"));
        FrpCache.inputFrpCache(name,"tunnelExtraSettings",FrpCache.getConfigCache(name,"tunnelExtraSettings"));
        FrpCache.inputFrpCache(name,"commonExtraSettings",FrpCache.getConfigCache(name,"commonExtraSettings"));

    }
}
