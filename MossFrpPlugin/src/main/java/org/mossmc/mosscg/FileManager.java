package org.mossmc.mosscg;

import org.bukkit.command.CommandSender;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Random;

import static org.mossmc.mosscg.Code.*;
import static org.mossmc.mosscg.MossFrp.*;

public class FileManager {
    //读取配置文件的方法
    public static void loadSaveTunnel(String name,CommandSender sender) {
        File file = new File(dataFolder+"/configs/"+name+".yml");
        if (!file.exists()) {
            sendWarn(getLanguage("File_ReadNotExist"),sender);
            return;
        }
        //yaml格式读取
        Yaml yaml = new Yaml();
        FileInputStream input;
        Map cacheMap;
        try {
            input = new FileInputStream(file);
            cacheMap = yaml.loadAs(input, Map.class);
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
                Code.decode(code,true,sender);
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
                Code.printTunnelInfo(name,sender);
                FileManager.writeFrpSettings(code,name,sender);
                FrpManager.runFrpProcess(name);
                return;
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
                Code.printTunnelInfo(name,sender);
                FileManager.writeFrpSettings(token,name,sender);
                FrpManager.runFrpProcess(name);
                return;
            }
            sendWarn(getLanguage("File_ReadUnknownMode"),sender);
        } catch (Exception e) {
            sendException(e);
            sendWarn(getLanguage("File_ReadError"),sender);
        }
    }
    //创建空的保存配置文件，给用户填写
    public static void writeEmptySaveTunnel(String name,String code,CommandSender sender) {
        sendInfo(getLanguage("File_WriteSaveStart"),sender);
        //初始化参数
        String remoteIP = "null";
        int remotePort = 0;
        if (!code.equals("")) {
            decode(code,true,sender);
            remoteIP = codeMap.get(code+"-node")+".mossfrp.cn";
            Random random = new Random();
            remotePort = Integer.parseInt(codeMap.get(code+"-portServer"))+random.nextInt(8)+1;
        }
        //替换标识符
        String fileInput = getLanguage("SaveFile");
        fileInput = fileInput.replace("[runMode]","1")
                .replace("[code]",code)
                .replace("[token]",code)
                .replace("[protocol]","tcp")
                .replace("[localIP]","127.0.0.1")
                .replace("[localPort]",String.valueOf(getInstance().getServer().getPort()))
                .replace("[remoteIP]",remoteIP)
                .replace("[remotePort]",String.valueOf(remotePort))
                .replace("[compression]","false")
                .replace("[encryption]","false")
                .replace("[proxyProtocol]","false");
        //写入文件
        try {
            File saveFile = new File(dataFolder+"/configs/"+name+".yml");
            if (!saveFile.exists()) {
                if (!saveFile.createNewFile()) {
                    sendWarn(getLanguage("File_WriteSaveFailed"),sender);
                    return;
                }
            }
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFile), StandardCharsets.UTF_8));
            writer.write(fileInput);
            writer.flush();
            writer.close();
            sendInfo(getLanguage("File_WriteSaveSuccess"),sender);
        } catch (Exception e) {
            e.printStackTrace();
            sendInfo(getLanguage("File_WriteSaveFailed"),sender);
        }
    }
    //保存隧道配置文件为yml，用于下次启动时读取
    public static void writeSaveTunnel(String code, String frpName, CommandSender sender) {
        sendInfo(getLanguage("File_WriteSaveStart"),sender);
        //读取高级选项
        String prefix = frpName+"-";
        String fileInput = getLanguage("SaveFile");
        String compression = "false";
        String encryption = "false";
        String proxyProtocol = "false";
        String advanced = tunnelMap.get(prefix+"advancedSettings");
        if (advanced.contains("1")) {
            compression = "true";
        }
        if (advanced.contains("2")) {
            encryption = "true";
        }
        if (advanced.contains("3")) {
            proxyProtocol = "v1";
        }
        if (advanced.contains("4")) {
            proxyProtocol = "v2";
        }
        //内容标识符替换
        fileInput = fileInput.replace("[runMode]","1")
                .replace("[code]",tunnelMap.get(prefix+"token"))
                .replace("[token]",tunnelMap.get(prefix+"token"))
                .replace("[protocol]",tunnelMap.get(prefix+"frpType"))
                .replace("[localIP]",tunnelMap.get(prefix+"localIP"))
                .replace("[localPort]",tunnelMap.get(prefix+"portLocal"))
                .replace("[remoteIP]","null")
                .replace("[remotePort]",tunnelMap.get(prefix+"portOpen"))
                .replace("[compression]",compression)
                .replace("[encryption]",encryption)
                .replace("[proxyProtocol]",proxyProtocol);
        //写入文件部分
        try {
            File saveFile = new File(dataFolder+"/configs/"+tunnelMap.get(prefix+"node")+"-"+frpName+".yml");
            if (!saveFile.exists()) {
                if (!saveFile.createNewFile()) {
                    sendWarn(getLanguage("File_WriteSaveFailed"),sender);
                    return;
                }
            }
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFile), StandardCharsets.UTF_8));
            writer.write(fileInput);
            writer.flush();
            writer.close();
            sendInfo(getLanguage("File_WriteSaveSuccess"),sender);
        } catch (Exception e) {
            e.printStackTrace();
            sendInfo(getLanguage("File_WriteSaveFailed"),sender);
        }
    }
    //生成frpc.ini以及复制frpc.exe
    public static void writeFrpSettings(String code,String frpName,CommandSender sender) {
        sendInfo(getLanguage("File_WriteConfigStart"),sender);
        String prefix = frpName+"-";
        String frpFileName;
        if (tunnelMap.containsKey(prefix+"new")) {
            frpFileName = tunnelMap.get(prefix+"node") + "-" + frpName;
        } else {
            frpFileName = frpName;
        }
        //检查文件是否存在
        //以及创建文件
        String dirPath;
        dirPath = dataFolder+"/frps/" + frpFileName;
        try{
            File dirFile = new File(dirPath);
            File cfgFile = new File(dirPath+"/frpc.ini");
            File frpFile = null;
            if (getSystemType == systemType.windows) {
                frpFile = new File(dirPath + "/frpc-" + frpFileName + ".exe");
            }
            if (getSystemType == systemType.linux) {
                frpFile = new File(dirPath + "/frpc-" + frpFileName);
            }
            assert frpFile != null;
            if (!dirFile.exists()) {
                if (!dirFile.mkdir()) {
                    sendWarn(getLanguage("File_WriteConfigFailed"),sender);
                    return;
                }
            }
            if (!cfgFile.exists()) {
                if (!cfgFile.createNewFile()) {
                    sendWarn(getLanguage("File_WriteConfigFailed"),sender);
                    return;
                }
            }
            if (!frpFile.exists()) {
                InputStream in = null;
                if (getSystemType == systemType.windows) {
                    in = MossFrp.class.getClassLoader().getResourceAsStream("frpc.exe");
                }
                if (getSystemType == systemType.linux) {
                    in = MossFrp.class.getClassLoader().getResourceAsStream("frpc");
                }
                try {
                    assert in != null;
                    Files.copy(in, frpFile.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                    sendWarn(getLanguage("File_WriteConfigFailed"),sender);
                    return;
                }
                frpFile.setReadable(true);
                frpFile.setExecutable(true);
                frpFile.setWritable(true);
            }
            //写入frpc.ini
            FileWriter fileWriter = new FileWriter(cfgFile,false);
            fileWriter.write("[common]"+"\r\n");
            if (tunnelMap.containsKey(prefix+"custom")) {
                fileWriter.write("server_addr = "+tunnelMap.get(prefix+"remoteIP")+"\r\n");
            } else {
                fileWriter.write("server_addr = "+tunnelMap.get(prefix+"node")+".mossfrp.cn\r\n");
            }
            fileWriter.write("server_port = "+tunnelMap.get(prefix+"portServer")+"\r\n");
            fileWriter.write("log_file = "+dataFolder.toString().replace("\\","/")+"/frps/"+frpName+"/frps.log"+"\r\n");
            fileWriter.write("log_level = info"+"\r\n");
            fileWriter.write("log_max_days = 7"+"\r\n");
            fileWriter.write("token = "+tunnelMap.get(prefix+"token"));
            fileWriter.write("\r\n");
            fileWriter.write("["+frpName+"]"+"\r\n");
            fileWriter.write("type = "+tunnelMap.get(prefix+"frpType")+"\r\n");
            fileWriter.write("local_ip = "+tunnelMap.get(prefix+"localIP")+"\r\n");
            fileWriter.write("local_port = "+tunnelMap.get(prefix+"portLocal")+"\r\n");
            fileWriter.write("remote_port = "+tunnelMap.get(prefix+"portOpen")+"\r\n");
            fileWriter.flush();
            String advanced = tunnelMap.get(prefix+"advancedSettings");
            if (advanced.contains("1")) {
                fileWriter.write("use_compression = true\r\n");
            }
            if (advanced.contains("2")) {
                fileWriter.write("tls_enable = true\r\n");
            }
            if (advanced.contains("3")) {
                fileWriter.write("proxy_protocol_version = v1\r\n");
            }
            if (advanced.contains("4")) {
                fileWriter.write("proxy_protocol_version = v2\r\n");
            }
            if (advanced.contains("5")) {
                writeSaveTunnel(code,frpName,sender);
            }
            fileWriter.flush();
            fileWriter.close();
            sendInfo(getLanguage("File_WriteConfigSuccess"),sender);
        }catch(IOException e){
            sendException(e);
            sendWarn(getLanguage("File_WriteConfigFailed"),sender);
        }
    }
}

