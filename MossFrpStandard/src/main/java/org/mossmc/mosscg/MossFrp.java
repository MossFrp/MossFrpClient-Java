package org.mossmc.mosscg;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;

public class MossFrp {
    //主类
    //完成一堆文件检查与初始化
    public static void main(String[] args) {
        checkFile();
        loadConfig();
        loadLanguage();
        loadData();
        checkStart(Arrays.toString(args));
        sendInfo(getLanguage("Start_Welcome")+getData("version"));
        sendInfo(getLanguage("Start_Copyright"));
        sendInfo(getLanguage("Start_ToGuide"));
        StartGuide.start();
    }

    //检查是否用CMD启动
    //避免有用户直接点软件导致命令行界面出不来
    public static void checkStart(String args) {
        if (!args.contains("-MossFrp=nb")) {
            try {
                FileWriter fileWriter = new FileWriter("./run.bat");
                fileWriter.write("@echo off \r\n");
                fileWriter.write("java -server -Xmx50m -jar MossFrpStandard.jar -MossFrp=nb \r\n");
                fileWriter.write("pause \r\n");
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendInfo(getLanguage("Start_CheckFailed"));
            System.exit(1);
        }
    }

    //yml数据全部转为Map存储在这一块
    //方便调用所以写了get方法
    public static Map languageMap;

    public static Map configMap;

    public static Map dataMap;

    public static String getLanguage(String languagePath) {
        return languageMap.get(languagePath).toString();
    }

    public static String getConfig(String configPath) {
        return configMap.get(configPath).toString();
    }

    public static String getData(String dataPath) {
        return dataMap.get(dataPath).toString();
    }

    //消息发送部分
    //直接调用就可以发送消息
    //别用System.out.printIn()了！！！
    //包括后边try catch都是用这里的sendException
    //不然log日志不记录
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

    //简单的获取时间的一个方法
    //输出示例：2021-10-30-21-6-55
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

    //log记录部分
    //和send部分配套使用
    public static FileWriter getWriter;

    public static void loadWriter() throws IOException {
        getWriter = new FileWriter("MossFrp/logs/latest.yml",true);
    }

    public static void logMessage(String message) {
        fileInput(message);
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

    //yml数据文件加载模块
    //都是一个模子出来的，看的舒服
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

    public static void loadData() {
        Yaml yaml = new Yaml();
        FileInputStream input = null;
        try {
            input = new FileInputStream("./MossFrp/languages/data.yml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        dataMap = yaml.loadAs(input, Map.class);
    }

    //文件检查的方法
    //启动的时候调用一遍
    //用了一堆new不过问题不大
    //有些地方没简化，还有优化空间
    public static void checkFile() {
        //主文件夹检查
        File basicDir = new File("./MossFrp");
        if (!basicDir.exists()) {
            basicDir.mkdir();
        }
        //log文件夹检查
        File logDir = new File("./MossFrp/logs");
        if (!logDir.exists()) {
            logDir.mkdir();
        }
        //copy旧的log
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
        //config文件检查
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
        //语言文件夹检查
        File languageDir = new File("./MossFrp/languages");
        if (!languageDir.exists()) {
            languageDir.mkdir();
        }
        //语言文件检查
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
        languageFile = new File("./MossFrp/languages/en_us.yml");
        if (!languageFile.exists()) {
            InputStream in = InputStream.class.getResourceAsStream("/languages/en_us.yml");
            try {
                assert in != null;
                Files.copy(in, languageFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //数据文件更新
        File dataFile = new File("./MossFrp/languages/data.yml");
        if (!dataFile.exists()) {
            InputStream in = InputStream.class.getResourceAsStream("/data.yml");
            try {
                assert in != null;
                Files.copy(in, dataFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            InputStream in = InputStream.class.getResourceAsStream("/data.yml");
            try {
                assert in != null;
                Files.delete(dataFile.toPath());
                Files.copy(in, dataFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
