package org.mossmc.mosscg.MossFrp.FileControl;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

import static org.mossmc.mosscg.MossFrp.BasicVoid.*;

public class FileCheck {
    public static void check() {
        try {
            checkDirExist(FileGet.getMainDirFile);
            checkDirExist(FileGet.getFrpConfigDirFile);
            checkDirExist(FileGet.getLanguageDirFile);
            checkDirExist(FileGet.getLogDirFile);
            checkDirExist(FileGet.getFrpDirFile);
            checkDirExist(FileGet.getReportDirFile);
            checkReplace(FileGet.getLanguageChineseFile,"languages/zh_cn.yml");
            checkReplace(FileGet.getLanguageEnglishFile,"languages/en_us.yml");
            checkReplace(FileGet.getProcessFile,"MossFrpProcess.jar");
            checkConfig(FileGet.getConfigFile);
        } catch (Exception e) {
            sendException(e);
            System.exit(1);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void checkDirExist(File file){
        if (!file.exists()) {
            file.mkdir();
        }
    }

    /* 可能暂时没用的方法
    public static void checkFileExist(File file,String resourcePath) throws Exception {
        if (file.exists()) {
            return;
        }
        InputStream input = FileCheck.class.getClassLoader().getResourceAsStream(resourcePath);
        assert input != null;
        Files.copy(input, file.toPath());
    }
     */

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void checkReplace(File file,String resourcePath) throws Exception {
        if (file.exists()) {
            file.delete();
        }
        InputStream input = FileCheck.class.getClassLoader().getResourceAsStream(resourcePath);
        assert input != null;
        Files.copy(input, file.toPath());
    }

    public static void checkConfig(File file) throws Exception{
        if (file.exists()) {
            return;
        }
        InputStream input = FileCheck.class.getClassLoader().getResourceAsStream("config.yml");
        assert input != null;
        Files.copy(input, file.toPath());
        input = FileCheck.class.getClassLoader().getResourceAsStream("example.yml");
        assert input != null;
        Files.copy(input, FileGet.getExampleFile.toPath());
    }
}
