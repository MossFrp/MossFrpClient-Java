package org.mossmc.mosscg;

import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Logger;

import static org.mossmc.mosscg.Code.codeMap;
import static org.mossmc.mosscg.Code.tunnelMap;

public class MossFrp extends JavaPlugin {
    //主类，用于调用另一个主类
    @Override
    public void onEnable() {
        main();
    }

    @Getter
    private static MossFrp instance;
    public static Path dataFolder;

    @Override
    public void onLoad() {
        instance = this;
        dataFolder = instance.getDataFolder().toPath();
    }

    //运行主类
    //完成一堆文件检查与初始化
    public static void main() {
        checkFile();
        loadConfig();
        loadSystemType();
        loadLanguage(getConfig("language"),null);
        loadData();
        checkStart();
        sendInfo(getLanguage("Start_Welcome")+getData("version"),null);
        sendInfo(getLanguage("Start_Copyright"),null);
        sendInfo(getLanguage("Start_SystemFull").replace("[system]",System.getProperty("os.name")),null);
        sendInfo(getLanguage("Start_SystemRead").replace("[system]", getSystemType.name()),null);
        sendInfo(getLanguage("Start_ToGuide"),null);
        StartGuide.start();
    }

    //指令监听
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return org.mossmc.mosscg.Command.readCommand(args,sender);
    }

    //检查启动，或许会有用
    //直接copy过来的懒得删了2333
    public static void checkStart() {
    }

    //启动时读取已保存的配置文件
    public static void readSave() {
        //列出文件列表
        File file = new File(dataFolder+"/configs");
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
                        String prefix = name+"-";
                        Code.decode(code,true,null);
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
                        Code.printTunnelInfo(name,null);
                        FileManager.writeFrpSettings(code,name,null);
                        FrpManager.runFrpProcess(name);
                        continue;
                    }
                    //自定义模式读取
                    if (cacheMap.get("mode").toString().equals("2")) {
                        String token = cacheMap.get("token").toString();
                        String protocol = cacheMap.get("protocol").toString();
                        String localIP = cacheMap.get("localIP").toString();
                        String localPort = cacheMap.get("localPort").toString();
                        String remoteIP = cacheMap.get("remoteIP").toString();
                        String remotePort = cacheMap.get("remotePort").toString();
                        String use_compression = cacheMap.get("use_compression").toString();
                        String use_encryption = cacheMap.get("use_encryption").toString();
                        String proxy_protocol_version = cacheMap.get("proxy_protocol_version").toString();
                        String prefix = name+"-";
                        tunnelMap.put(prefix+"custom","true");
                        tunnelMap.put(prefix+"token",token);
                        tunnelMap.put(prefix+"frpType",protocol);
                        tunnelMap.put(prefix+"localIP",localIP);
                        tunnelMap.put(prefix+"portOpen",remotePort);
                        tunnelMap.put(prefix+"portLocal",localPort);
                        tunnelMap.put(prefix+"portServer",remotePort);
                        tunnelMap.put(prefix+"remoteIP",remoteIP);
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
                        Code.printTunnelInfo(name,null);
                        FileManager.writeFrpSettings(token,name,null);
                        FrpManager.runFrpProcess(name);
                    }
                } catch (Exception e) {
                    sendException(e);
                }
            }
        }
        sendInfo(getLanguage("Guide_LoadSaveComplete"),null);
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
    public static void sendCommand(String command,CommandSender sender) {
        send(command,"command",sender);
    }

    public static void sendInfo(String info,CommandSender sender) {
        send(info,"info",sender);
    }

    public static void sendWarn(String warn,CommandSender sender) {
        send(warn,"warn",sender);
    }

    public static void sendError(String error,CommandSender sender) {
        send(error,"error",sender);
    }

    public static void sendException(Exception exception) {
        exception(exception);
    }

    public static void send(String info,String type,CommandSender sender) {
        String prefix = getLanguage("Prefix_Unknown");
        if (sender == null) {
            Logger logger = instance.getLogger();
            if (type.equals("info")) {
                prefix = getLanguage("Prefix_Info");
                logger.info(info);
            }
            if (type.equals("warn")) {
                prefix = getLanguage("Prefix_Warn");
                logger.warning(info);
            }
            if (type.equals("error")) {
                prefix = getLanguage("Prefix_Error");
                logger.warning(info);
            }
            if (type.equals("exception")) {
                prefix = getLanguage("Prefix_Exception");
                logger.warning(info);
            }
            if (type.equals("command")) {
                prefix = getLanguage("Prefix_Command");
            }
        } else {
            sender.sendMessage("MossFrp >>> "+info);
        }
        logMessage(prefix+info);
    }

    public static void exception(Exception e) {
        if (getConfig("exceptionMode").equals("light")) {
            send(e.getMessage(),"exception",null);
        } else {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter= new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            send(stringWriter.toString(),"exception",null);
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
    public enum systemType {
        windows,linux
    }
    public static systemType getSystemType = systemType.linux;

    public static void loadSystemType() {
        String getSystemName = System.getProperty("os.name");
        if (getConfig("systemType").equals("auto")) {
            if (getSystemName.toLowerCase().startsWith("windows")) {
                getSystemType = systemType.windows;
                return;
            }
            if (getSystemName.toLowerCase().startsWith("linux")) {
                getSystemType = systemType.linux;
                return;
            }
        }
        if (getConfig("systemType").equals("windows")) {
            getSystemType = systemType.windows;
            return;
        }
        if (getConfig("systemType").equals("linux")) {
            getSystemType = systemType.linux;
            return;
        }
        getSystemType = systemType.windows;
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
        getWriter = new FileWriter(dataFolder+"/logs/latest.yml",true);
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
            input = new FileInputStream(dataFolder+"/config.yml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        configMap = yaml.loadAs(input, Map.class);
    }

    public static void loadLanguage(String language,CommandSender sender) {
        Yaml yaml = new Yaml();
        FileInputStream input = null;
        String languageFile = dataFolder+"/languages/"+language+".yml";
        try {
            input = new FileInputStream(languageFile);
        } catch (FileNotFoundException e) {
            if (languageMap == null) {
                e.printStackTrace();
                System.exit(1);
            } else {
                sendInfo(getLanguage("Language_Unknown"),sender);
                return;
            }
        }
        if (languageMap != null) {
            languageMap = yaml.loadAs(input, Map.class);
            sendInfo(getLanguage("Language_Changed"),sender);
            return;
        }
        languageMap = yaml.loadAs(input, Map.class);
    }

    public static void loadData() {
        Yaml yaml = new Yaml();
        FileInputStream input = null;
        try {
            input = new FileInputStream(dataFolder+"/languages/data.yml");
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
        File basicDir = new File(dataFolder.toString());
        if (!basicDir.exists()) {
            basicDir.mkdir();
        }
        //log文件夹检查
        File logDir = new File(dataFolder+"/logs");
        if (!logDir.exists()) {
            logDir.mkdir();
        }
        //copy旧的log
        File latestLogFile = new File(dataFolder+"/logs/latest.yml");
        File saveLogFile = new File(dataFolder+"/logs/"+getNowTime()+".yml");
        if (latestLogFile.exists()) {
            try {
                Files.copy(latestLogFile.toPath(), saveLogFile.toPath());
                Files.delete(latestLogFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //config文件检查
        instance.saveDefaultConfig();
        instance.getConfig().options().copyDefaults(true);
        //语言文件夹检查
        File languageDir = new File(dataFolder+"/languages");
        if (!languageDir.exists()) {
            languageDir.mkdir();
        }
        //frp文件夹检查
        File frpDir = new File(dataFolder+"/frps");
        if (!frpDir.exists()) {
            frpDir.mkdir();
        }
        //frp进程核心检查
        File frpProcessFile = new File(dataFolder+"/frps/MossFrpProcess.jar");
        if (!frpProcessFile.exists()) {
            InputStream in = MossFrp.class.getClassLoader().getResourceAsStream("MossFrpProcess.jar");
            try {
                assert in != null;
                Files.copy(in, frpProcessFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            frpProcessFile.delete();
            InputStream in = MossFrp.class.getClassLoader().getResourceAsStream("MossFrpProcess.jar");
            try {
                assert in != null;
                Files.copy(in, frpProcessFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        //frp配置文件夹检查
        File frpConfigDir = new File(dataFolder+"/configs");
        if (!frpConfigDir.exists()) {
            frpConfigDir.mkdir();
        }
        //语言文件检查
        File languageFile = new File(dataFolder+"/languages/zh_cn.yml");
        if (languageFile.exists()) {
            languageFile.delete();
        }
        InputStream langIn = MossFrp.class.getClassLoader().getResourceAsStream("languages/zh_cn.yml");
        try {
            assert langIn != null;
            Files.copy(langIn, languageFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        languageFile = new File(dataFolder+"/languages/en_us.yml");
        if (languageFile.exists()) {
            languageFile.delete();
        }
        langIn = MossFrp.class.getClassLoader().getResourceAsStream("languages/en_us.yml");
        try {
            assert langIn != null;
            Files.copy(langIn, languageFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //数据文件更新
        File dataFile = new File(dataFolder+"/languages/data.yml");
        if (!dataFile.exists()) {
            InputStream in = MossFrp.class.getClassLoader().getResourceAsStream("data.yml");
            try {
                assert in != null;
                Files.copy(in, dataFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            InputStream in = MossFrp.class.getClassLoader().getResourceAsStream("data.yml");
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
