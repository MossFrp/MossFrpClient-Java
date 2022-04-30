package org.mossmc.mosscg.MossFrp.Config;

import org.mossmc.mosscg.MossFrp.BasicInfo;
import org.mossmc.mosscg.MossFrp.Code.CodeCache;
import org.mossmc.mosscg.MossFrp.FileControl.FileCheck;
import org.mossmc.mosscg.MossFrp.FileControl.FileGet;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.mossmc.mosscg.MossFrp.BasicVoid.*;

public class ConfigCodeSave {
    public static void saveCodeConfig(String code,String name) {
        Map<?,?> codeMap = CodeCache.codeCacheMap.get(code);
        String template = getTemplate();
        if (template == null) {
            return;
        }
        template = template
                .replace("[remoteIP]",codeMap.get("node").toString()+"."+BasicInfo.listDomains.get(0))
                .replace("[remotePort]",codeMap.get("portServer").toString())
                .replace("[portStart]",codeMap.get("portStart").toString())
                .replace("[portEnd]",codeMap.get("portEnd").toString())
                .replace("[openPort]",codeMap.get("portStart").toString())
                .replace("[token]",code);
        writeIntoConfig(name,template);
        sendInfo(getLanguage("Save_SaveSucceed").replace("[name]",name));
    }

    public static String getTemplate() {
        try {
            InputStream inputStream = FileCheck.class.getClassLoader().getResourceAsStream("save.yml");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int length;
            byte[] buffer = new byte[1024];
            assert inputStream != null;
            while((length = inputStream.read(buffer))!=-1){
                byteArrayOutputStream.write(buffer,0,length);
            }
            byteArrayOutputStream.close();
            inputStream.close();
            byte[] result = byteArrayOutputStream.toByteArray();
            return new String(result,StandardCharsets.UTF_8);
        } catch (Exception e) {
            sendException(e);
            sendWarn("#lang#Save_SaveReadFailed");
            return null;
        }
    }

    public static void writeIntoConfig(String name,String template) {
        try {
            String path = FileGet.getFrpConfigDirFile.toPath() +"/"+name+".yml";
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, true), StandardCharsets.UTF_8));
            writer.write(template);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            sendException(e);
            sendWarn("#lang#Save_SaveWriteFailed");
        }
    }
}
