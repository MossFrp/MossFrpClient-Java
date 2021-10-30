package org.mossmc.mosscg;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Map;

public class MossFrp {
    public static void main(String[] args) {
        checkFile();
        loadConfig();
        loadLanguage();
        sendInfo("检查完成！");
    }

    public static Map languageMap;

    public static Map configMap;

    public static String getLanguage(String languagePath) {
        return languageMap.get(languagePath).toString();
    }
    public static String getConfig(String configPath) {
        return configMap.get(configPath).toString();
    }

    public static void sendCommand(String command) {
        send(command,"command");
    }

    public static void sendInfo(String info) {
        send(info,"info");
    }

    public static void sendWarn(String warn) {
        send(warn,"warn");
    }

    public static void sendError(String error) {
        send(error,"error");
    }

    public static void sendException(Exception exception) {
        exception(exception);
    }

    public static String getNowTime() {
        Calendar rightNow = Calendar.getInstance();
        int year = rightNow.get(Calendar.YEAR);
        int month = rightNow.get(Calendar.MONTH)+1;
        int day = rightNow.get(Calendar.DAY_OF_MONTH);
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        int minute = rightNow.get(Calendar.MINUTE);
        int second = rightNow.get(Calendar.SECOND);
        return year + "-" + month + "-" + day + "-" + hour + "-" + minute + "-" + second;
    }

    public static void send(String info,String type) {
        String prefix = getLanguage("Prefix_Unknown");
        if (type.equals("info")) {
            prefix = getLanguage("Prefix_Info");
        }
        if (type.equals("warn")) {
            prefix = getLanguage("Prefix_Warn");
        }
        if (type.equals("error")) {
            prefix = getLanguage("Prefix_Error");
        }
        if (type.equals("exception")) {
            prefix = getLanguage("Prefix_Exception");
        }
        if (type.equals("command")) {
            prefix = getLanguage("Prefix_Command");
        }
        System.out.println("["+getNowTime()+"]"+prefix+info);
        logMessage(prefix+info);
    }

    public static void logMessage(String message) {
        fileInput(message);
    }

    public static FileWriter getWriter;

    public static void loadWriter() throws IOException {
        getWriter = new FileWriter("MossFrp/logs/latest.yml",true);
    }
    public static void fileInput(String info) {
        try {
            if (getWriter == null) {
                loadWriter();
            }
            getWriter.write("["+getNowTime()+"] "+info+"\r\n");
            getWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exception(Exception e) {
        if (getConfig("exceptionMode").equals("light")) {
            send(e.getMessage(),"exception");
        } else {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter= new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            send(stringWriter.toString(),"exception");
            try {
                printWriter.close();
                stringWriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void loadConfig() {
        Yaml yaml = new Yaml();
        FileInputStream input = null;
        try {
            input = new FileInputStream("./MossFrp/config.yml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        configMap = yaml.loadAs(input, Map.class);
    }

    public static void loadLanguage() {
        Yaml yaml = new Yaml();
        FileInputStream input = null;
        String languageFile = "./MossFrp/languages/"+getConfig("language")+".yml";
        try {
            input = new FileInputStream(languageFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        languageMap = yaml.loadAs(input, Map.class);
    }

    public static void checkFile() {
        File basicDir = new File("./MossFrp");
        if (!basicDir.exists()) {
            basicDir.mkdir();
        }
        File logDir = new File("./MossFrp/logs");
        if (!logDir.exists()) {
            logDir.mkdir();
        }
        File latestLogFile = new File("./MossFrp/logs/latest.yml");
        File saveLogFile = new File("./MossFrp/logs/"+getNowTime()+".yml");
        if (latestLogFile.exists()) {
            try {
                Files.copy(latestLogFile.toPath(), saveLogFile.toPath());
                Files.delete(latestLogFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File languageDir = new File("./MossFrp/languages");
        if (!languageDir.exists()) {
            languageDir.mkdir();
        }
        File configFile = new File("./MossFrp/config.yml");
        if (!configFile.exists()) {
            InputStream in = InputStream.class.getResourceAsStream("/config.yml");
            try {
                assert in != null;
                Files.copy(in, configFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        File languageFile = new File("./MossFrp/languages/zh_cn.yml");
        if (!languageFile.exists()) {
            InputStream in = InputStream.class.getResourceAsStream("/languages/zh_cn.yml");
            try {
                assert in != null;
                Files.copy(in, languageFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
