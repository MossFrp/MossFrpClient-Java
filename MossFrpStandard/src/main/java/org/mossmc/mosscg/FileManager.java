package org.mossmc.mosscg;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

import static org.mossmc.mosscg.Code.codeMap;
import static org.mossmc.mosscg.MossFrp.*;
import static org.mossmc.mosscg.Code.tunnelMap;

public class FileManager {
    //读取配置文件的方法
    public static void loadSaveTunnel(String name) {
        File file = new File("./MossFrp/configs/"+name+".yml");
        if (!file.exists()) {
            sendWarn(getLanguage("File_ReadNotExist"));
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
                Code.printTunnelInfo(name);
                FileManager.writeFrpSettings(code,name);
                FrpManager.runFrpProcess(name);
                return;
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
                Code.printTunnelInfo(name);
                FileManager.writeFrpSettings(token,name);
                FrpManager.runFrpProcess(name);
                return;
            }
            sendWarn(getLanguage("File_ReadUnknownMode"));
        } catch (Exception e) {
            sendException(e);
            sendWarn(getLanguage("File_ReadError"));
        }
    }
    //保存隧道配置文件为yml，用于下次启动时读取
    public static void writeSaveTunnel(String code,String frpName) {
        sendInfo(getLanguage("File_WriteSaveStart"));
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
            File saveFile = new File("./MossFrp/configs/"+tunnelMap.get(prefix+"node")+"-"+frpName+".yml");
            if (!saveFile.exists()) {
                if (!saveFile.createNewFile()) {
                    sendWarn(getLanguage("File_WriteSaveFailed"));
                    return;
                }
            }
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFile), StandardCharsets.UTF_8));
            writer.write(fileInput);
            writer.flush();
            writer.close();
            sendInfo(getLanguage("File_WriteSaveSuccess"));
        } catch (Exception e) {
            e.printStackTrace();
            sendInfo(getLanguage("File_WriteSaveFailed"));
        }
    }
    //生成frpc.ini以及复制frpc.exe
    public static void writeFrpSettings(String code,String frpName) {
        sendInfo(getLanguage("File_WriteConfigStart"));
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
        dirPath = "./MossFrp/frps/" + frpFileName;
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
                    sendWarn(getLanguage("File_WriteConfigFailed"));
                    return;
                }
            }
            if (!cfgFile.exists()) {
                if (!cfgFile.createNewFile()) {
                    sendWarn(getLanguage("File_WriteConfigFailed"));
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
                    sendWarn(getLanguage("File_WriteConfigFailed"));
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
            fileWriter.write("log_file = ./MossFrp/frps/"+frpName+"/frps.log"+"\r\n");
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
                writeSaveTunnel(code,frpName);
            }
            fileWriter.flush();
            fileWriter.close();
            sendInfo(getLanguage("File_WriteConfigSuccess"));
        }catch(IOException e){
            sendException(e);
            sendWarn(getLanguage("File_WriteConfigFailed"));
        }
    }
}

