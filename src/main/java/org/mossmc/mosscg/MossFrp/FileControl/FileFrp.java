package org.mossmc.mosscg.MossFrp.FileControl;

import org.mossmc.mosscg.MossFrp.Config.ConfigFrpSave;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static org.mossmc.mosscg.MossFrp.BasicVoid.*;
import static org.mossmc.mosscg.MossFrp.BasicInfo.*;

public class FileFrp {
    //生成frpc.ini以及复制frpc.exe
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void writeFrpSettings(String frpName) {
        sendInfo("#lang#File_WriteConfigStart");
        //检查文件是否存在
        //以及创建文件
        String dirPath = FileGet.getFrpDirFile.toPath()+"/"+frpName+"/";
        try{
            File dirFile = new File(dirPath);
            File cfgFile = new File(dirPath+"frpc.ini");
            File frpFile = null;
            if (getSystemType == systemType.windows) {
                frpFile = new File(dirPath + "frpc-" + frpName + ".exe");
            }
            if (getSystemType == systemType.linux) {
                frpFile = new File(dirPath + "frpc-" + frpName);
            }
            assert frpFile != null;
            if (!dirFile.exists()) {
                if (!dirFile.mkdir()) {
                    sendWarn("#lang#File_WriteConfigFailed");
                    return;
                }
            }
            if (!cfgFile.exists()) {
                if (!cfgFile.createNewFile()) {
                    sendWarn("#lang#File_WriteConfigFailed");
                    return;
                }
            }
            if (!frpFile.exists()) {
                InputStream in = null;
                if (getSystemType == systemType.windows) {
                    in = FileFrp.class.getClassLoader().getResourceAsStream("frpc.exe");
                }
                if (getSystemType == systemType.linux) {
                    in = FileFrp.class.getClassLoader().getResourceAsStream("frpc");
                }
                try {
                    assert in != null;
                    Files.copy(in, frpFile.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                    sendWarn("#lang#File_WriteConfigFailed");
                    return;
                }
                frpFile.setReadable(true);
                frpFile.setExecutable(true);
                frpFile.setWritable(true);
            }
            ConfigFrpSave.writeFile(cfgFile,frpName,dirPath);
            sendInfo("#lang#File_WriteConfigSuccess");
        }catch(Exception e){
            sendException(e);
            sendWarn("#lang#File_WriteConfigFailed");
        }
    }
}
