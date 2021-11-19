package org.mossmc.mosscg;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import static org.mossmc.mosscg.Code.codeMap;
import static org.mossmc.mosscg.Code.tunnelMap;

public class MossFrp {
    //主类
    //完成一堆文件检查与初始化
    public static void main(String[] args) {
        checkFile();
        loadConfig();
        loadLanguage(getConfig("language"));
        loadData();
        checkStart(Arrays.toString(args));
        sendInfo(getLanguage("Start_Welcome")+getData("version"));
        sendInfo(getLanguage("Start_Copyright"));
        sendInfo(getLanguage("Start_ToGuide"));
        StartGuide.start();
    }

    //检查是否用CMD启动
    //避免有用户直接点软件导致命令行界面出不来
    //linux直接忽略此方法
    public static void checkStart(String args) {
        if (getSystemType().equals("linux")) {
            return;
        }
        if (!args.contains("-MossFrp=nb")) {
            try {
                FileWriter fileWriter = new FileWriter("./run.bat");
                fileWriter.write("@echo off \r\n");
                fileWriter.write("title MossFrp Standard Client \r\n");
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

    //启动时读取已保存的配置文件
    public static void readSave() {
        //列出文件列表
        File file = new File("./MossFrp/configs");
        File[] tempList = file.listFiles();
        if (tempList == null) {
            return;
        }
        Map cacheMap;
        //对文件列表进行遍历
        for (File value : tempList) {
            if (value.isFile()) {
                //yaml格式读取
                Yaml yaml = new Yaml();
                FileInputStream input;
                try {
                    input = new FileInputStream(value);
                    cacheMap = yaml.loadAs(input, Map.class);
                    String fileName = value.getName();
                    String[] cut = fileName.split("\\.");
                    String name = cut[0];
                    //未知模式判定
                    if (!cacheMap.get("mode").toString().equals("1") && !cacheMap.get("mode").toString().equals("2")) {
                        continue;
                    }
                    //MossFrp模式读取
                    if (cacheMap.get("mode").toString().equals("1")) {
                        String code = cacheMap.get("code").toString();
                        String protocol = cacheMap.get("protocol").toString();
                        String localIP = cacheMap.get("localIP").toString();
                        String localPort = cacheMap.get("localPort").toString();
                        String remotePort = cacheMap.get("remotePort").toString();
                        String use_compression = cacheMap.get("use_compression").toString();
                        String use_encryption = cacheMap.get("use_encryption").toString();
                        String proxy_protocol_version = cacheMap.get("proxy_protocol_version").toString();
                        String prefix = code+"-"+name+"-";
                        Code.decode(code,true);
                        tunnelMap.put(prefix+"token",code);
                        tunnelMap.put(prefix+"frpType",protocol);
                        tunnelMap.put(prefix+"localIP",localIP);
                        tunnelMap.put(prefix+"portOpen",remotePort);
                        tunnelMap.put(prefix+"portLocal",localPort);
                        tunnelMap.put(prefix+"portServer",codeMap.get(code+"-portServer"));
                        tunnelMap.put(prefix+"node",codeMap.get(code+"-node"));
                        tunnelMap.put(prefix+"advancedSettings","");
                        if (use_compression.equals("true")) {
                            tunnelMap.put(prefix+"advancedSettings",tunnelMap.get(prefix+"advancedSettings")+"1");
                        }
                        if (use_encryption.equals("true")) {
                            tunnelMap.put(prefix+"advancedSettings",tunnelMap.get(prefix+"advancedSettings")+"2");
                        }
                        if (proxy_protocol_version.equals("v1")) {
                            tunnelMap.put(prefix+"advancedSettings",tunnelMap.get(prefix+"advancedSettings")+"3");
                        }
                        if (proxy_protocol_version.equals("v2")) {
                            tunnelMap.put(prefix+"advancedSettings",tunnelMap.get(prefix+"advancedSettings")+"4");
                        }
                        cacheMap.clear();
                        Code.printTunnelInfo(code,name);
                        FileManager.writeFrpSettings(code,name);
                        FrpManager.runFrpProcess(name);
                        continue;
                    }
                    //自定义模式读取
                    if (cacheMap.get("mode").equals("2")) {
                        String token = cacheMap.get("token").toString();
                        String protocol = cacheMap.get("protocol").toString();
                        String localIP = cacheMap.get("localIP").toString();
                        String localPort = cacheMap.get("localPort").toString();
                        String remoteIP = cacheMap.get("remoteIP").toString();
                        String remotePort = cacheMap.get("remotePort").toString();
                        String use_compression = cacheMap.get("use_compression").toString();
                        String use_encryption = cacheMap.get("use_encryption").toString();
                        String proxy_protocol_version = cacheMap.get("proxy_protocol_version").toString();
                        String prefix = token+"-"+name+"-";
                        tunnelMap.put(prefix+"custom","true");
                        tunnelMap.put(prefix+"token",token);
                        tunnelMap.put(prefix+"frpType",protocol);
                        tunnelMap.put(prefix+"localIP",localIP);
                        tunnelMap.put(prefix+"portOpen",remotePort);
                        tunnelMap.put(prefix+"portLocal",localPort);
                        tunnelMap.put(prefix+"portServer",codeMap.get(remotePort));
                        tunnelMap.put(prefix+"remoteIP",codeMap.get(remoteIP));
                        tunnelMap.put(prefix+"advancedSettings","");
                        if (use_compression.equals("true")) {
                            tunnelMap.put(prefix+"advancedSettings",tunnelMap.get(prefix+"advancedSettings")+"1");
                        }
                        if (use_encryption.equals("true")) {
                            tunnelMap.put(prefix+"advancedSettings",tunnelMap.get(prefix+"advancedSettings")+"2");
                        }
                        if (proxy_protocol_version.equals("v1")) {
                            tunnelMap.put(prefix+"advancedSettings",tunnelMap.get(prefix+"advancedSettings")+"3");
                        }
                        if (proxy_protocol_version.equals("v2")) {
                            tunnelMap.put(prefix+"advancedSettings",tunnelMap.get(prefix+"advancedSettings")+"4");
                        }
                        cacheMap.clear();
                        Code.printTunnelInfo(token,name);
                        FileManager.writeFrpSettings(token,name);
                        FrpManager.runFrpProcess(name);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        sendInfo(getLanguage("Guide_LoadSaveComplete"));
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

    //获取系统类型方法
    //简单但是确实有效
    public static String getSystemType() {
        String getSystemName = System.getProperty("os.name");
        if (getConfig("systemType").equals("auto")) {
            if (getSystemName.toLowerCase().startsWith("win")) {
                return "windows";
            }
            return "linux";
        }
        if (getConfig("systemType").equals("windows")) {
            return "windows";
        }
        if (getConfig("systemType").equals("linux")) {
            return "linux";
        }
        return "windows";
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

    public static void loadLanguage(String language) {
        Yaml yaml = new Yaml();
        FileInputStream input = null;
        String languageFile = "./MossFrp/languages/"+language+".yml";
        try {
            input = new FileInputStream(languageFile);
        } catch (FileNotFoundException e) {
            if (languageMap == null) {
                e.printStackTrace();
                System.exit(1);
            } else {
                sendInfo(getLanguage("Language_Unknown"));
                return;
            }
        }
        if (languageMap != null) {
            languageMap = yaml.loadAs(input, Map.class);
            sendInfo(getLanguage("Language_Changed"));
            return;
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
        //frp文件夹检查
        File frpDir = new File("./MossFrp/frps");
        if (!frpDir.exists()) {
            frpDir.mkdir();
        }
        //frp进程核心检查
        File frpProcessFile = new File("./MossFrp/frps/MossFrpProcess.jar");
        if (!frpProcessFile.exists()) {
            InputStream in = InputStream.class.getResourceAsStream("/MossFrpProcess.jar");
            try {
                assert in != null;
                Files.copy(in, frpProcessFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        //frp配置文件夹检查
        File frpConfigDir = new File("./MossFrp/configs");
        if (!frpConfigDir.exists()) {
            frpConfigDir.mkdir();
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
