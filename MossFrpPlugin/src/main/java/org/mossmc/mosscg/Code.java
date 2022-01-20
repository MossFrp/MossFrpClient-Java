package org.mossmc.mosscg;

import org.bukkit.command.CommandSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static org.mossmc.mosscg.MossFrp.*;

public class Code {
    //俩缓存用的Map
    public static Map<String,String> codeMap = new HashMap<>();
    public static Map<String,String> tunnelMap = new HashMap<>();
    //一个通过阻塞线程来读取用户输入的方法
    //将用户输入内容转为String输出
    public static String readInput() {
        BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(System.in, Charset.defaultCharset()));
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            sendException(e);
            return "null";
        }
    }
    //发送错误信息
    //主要是用户输入错了内容发送错误没有sleep信息会被刷上去
    //导致用户看不到
    //所以为了方便好看就把sleep和send写一起单独写了一个方法
    public static void sendErrorWarn(String warn,CommandSender sender) {
        sendWarn(warn,sender);
    }

    //显示隧道信息的方法
    //每次输入设置都会调用一次
    public static void printTunnelInfo(String frpName,CommandSender sender) {
        String prefix = frpName+"-";
        sendInfo("",sender);
        sendInfo(getLanguage("CodeGuide_PrintLine"),sender);
        sendInfo(getLanguage("CodeGuide_PrintFrpName") + frpName,sender);
        sendInfo(getLanguage("CodeGuide_PrintToken") + autoHide(tunnelMap.get(prefix + "token")),sender);
        sendInfo(getLanguage("CodeGuide_PrintProtocol") + tunnelMap.get(prefix + "frpType"),sender);
        sendInfo(getLanguage("CodeGuide_PrintLocalIP") + tunnelMap.get(prefix + "localIP") + ":" + tunnelMap.get(prefix + "portLocal"),sender);
        if (tunnelMap.containsKey(prefix + "custom")) {
            sendInfo(getLanguage("CodeGuide_PrintRemoteIP") + tunnelMap.get(prefix + "remoteIP") + ":" + tunnelMap.get(prefix + "portOpen"),sender);
        } else {
            sendInfo(getLanguage("CodeGuide_PrintRemoteIP") + tunnelMap.get(prefix + "node") + ".mossfrp.cn:" + tunnelMap.get(prefix + "portOpen"),sender);
        }
        sendInfo(getLanguage("CodeGuide_PrintLine"),sender);
    }

    public static String autoHide(String input) {
        int length = input.length();
        StringBuilder builder = new StringBuilder();
        if (length <= 6) {
            int i = 0;
            while (i<length) {
                i++;
                builder.append("*");
            }
        } else {
            builder.append(input, 0, 2);
            length = length - 4;
            int i = 0;
            while (i<length) {
                i++;
                builder.append("*");
            }
            builder.append(input.substring(input.length()-2));
        }
        return builder.toString();
    }
    //激活码设置向导（插件暂时不启用）
    //方便一些萌新不会用做了一个向导
    //不然我挺想做全指令的其实
    //插件版本输入的话是使用/mossfrp set <参数>
    public static void codeGuide(String code, String frpName, CommandSender sender) {
        //进行激活码解码然后存入激活码缓存
        if (!decode(code,true,sender)) {
            return;
        }
        if (FrpManager.frpStatusMap.containsKey(codeMap.get(code+"-node")+"-"+frpName)) {
            sendInfo(getLanguage("Command_TunnelAlreadyExist"),sender);
            return;
        }
        //将数据存入隧道缓存
        //方便后续调用
        String prefix = frpName+"-";
        tunnelMap.put(prefix+"token",code);
        tunnelMap.put(prefix+"frpType","");
        tunnelMap.put(prefix+"localIP","");
        tunnelMap.put(prefix+"portOpen","");
        tunnelMap.put(prefix+"portLocal","");
        tunnelMap.put(prefix+"portServer",codeMap.get(code+"-portServer"));
        tunnelMap.put(prefix+"portStart",codeMap.get(code+"-portStart"));
        tunnelMap.put(prefix+"portEnd",codeMap.get(code+"-portEnd"));
        tunnelMap.put(prefix+"node",codeMap.get(code+"-node"));
        tunnelMap.put(prefix+"new","true");
        tunnelMap.put(prefix+"tunnelExtraSettings","false");
        tunnelMap.put(prefix+"commonExtraSettings","false");
        //设置隧道协议
        while (true) {
            printTunnelInfo(frpName,sender);
            sendInfo(getLanguage("CodeGuide_PrintExit"),sender);
            sendInfo(getLanguage("CodeGuide_ProtocolInfo"),sender);
            String read = readInput();
            if (read.equals("exit")) {
                sendInfo(getLanguage("CodeGuide_Exit"),sender);
                return;
            }
            if (read.equals("tcp") || read.equals("udp")) {
                tunnelMap.put(prefix+"frpType",read);
                break;
            }
            sendErrorWarn(getLanguage("CodeGuide_ProtocolWarn"),sender);
        }
        //设置本地IP地址
        while (true) {
            printTunnelInfo(frpName,sender);
            sendInfo(getLanguage("CodeGuide_PrintExit"),sender);
            sendInfo(getLanguage("CodeGuide_LocalIPInfo"),sender);
            String read = readInput();
            if (read.equals("exit")) {
                sendInfo(getLanguage("CodeGuide_Exit"),sender);
                return;
            }
            if (read.contains(":") || read.contains("：")) {
                sendErrorWarn(getLanguage("CodeGuide_LocalIPWarnPort"),sender);
                continue;
            }
            if (read.length() > 0) {
                tunnelMap.put(prefix+"localIP",read);
                break;
            }
            sendErrorWarn(getLanguage("CodeGuide_LocalIPWarn"),sender);
        }
        //设置本地端口
        while (true) {
            printTunnelInfo(frpName,sender);
            sendInfo(getLanguage("CodeGuide_PrintExit"),sender);
            sendInfo(getLanguage("CodeGuide_LocalPortInfo"),sender);
            String read = readInput();
            if (read.equals("exit")) {
                sendInfo(getLanguage("CodeGuide_Exit"),sender);
                return;
            }
            int port;
            try {
                port = Integer.parseInt(read);
            } catch (NumberFormatException e) {
                sendErrorWarn(getLanguage("CodeGuide_LocalPortWarn"),sender);
                continue;
            }
            if (port > 0 && port <= 65535) {
                tunnelMap.put(prefix+"portLocal",read);
                break;
            }
            sendErrorWarn(getLanguage("CodeGuide_LocalPortWarn"),sender);
        }
        //设置远程端口
        while (true) {
            printTunnelInfo(frpName,sender);
            sendInfo(getLanguage("CodeGuide_PrintExit"),sender);
            sendInfo(getLanguage("CodeGuide_RemotePortInfo").replace("[remotePortRange]",tunnelMap.get(prefix+"portStart")+"-"+tunnelMap.get(prefix+"portEnd")),sender);
            String read = readInput();
            if (read.equals("exit")) {
                sendInfo(getLanguage("CodeGuide_Exit"),sender);
                return;
            }
            int start = Integer.parseInt(tunnelMap.get(prefix+"portStart"));
            int end = Integer.parseInt(tunnelMap.get(prefix+"portEnd"));
            int select = Integer.parseInt(read);
            if (select >= start && select <= end) {
                tunnelMap.put(prefix+"portOpen",read);
                break;
            }
            sendErrorWarn(getLanguage("CodeGuide_RemotePortWarn").replace("[remotePortRange]",tunnelMap.get(prefix+"portStart")+"-"+tunnelMap.get(prefix+"portEnd")),sender);
        }
        //设置高级选项
        while (true) {
            printTunnelInfo(frpName,sender);
            sendInfo(getLanguage("CodeGuide_PrintExit"),sender);
            sendInfo(getLanguage("CodeGuide_AdvancedInfo"),sender);
            String read = readInput();
            if (read.equals("exit")) {
                sendInfo(getLanguage("CodeGuide_Exit"),sender);
                return;
            }
            if (read.length() < 1) {
                tunnelMap.put(prefix+"advancedSettings","0");
                break;
            }
            try {
                Integer.parseInt(read);
                tunnelMap.put(prefix+"advancedSettings",read);
                break;
            } catch (NumberFormatException e) {
                sendErrorWarn(getLanguage("CodeGuide_AdvancedWarn"),sender);
            }
        }
        printTunnelInfo(frpName,sender);
        sendInfo(getLanguage("CodeGuide_Complete"),sender);
        //新建独立线程运行frp
        //保证运行不把主线程玩炸了
        FrpManager.runFrp(code,frpName,sender);
    }
    //激活码解码方法
    //cache选项为是否存入缓存
    public static Boolean decode(String code, boolean cache,CommandSender sender) {
        try {
            //解码部分
            int nodeNameLength = Integer.parseInt(code.substring(0,1));
            String nodeName = code.substring(1,nodeNameLength+1);
            int auth = Integer.parseInt(code.substring(nodeNameLength+1,nodeNameLength+6));
            int portServer = Integer.parseInt(code.substring(nodeNameLength+6,nodeNameLength+11));
            portServer = portServer - auth;
            int portStart = portServer + 1;
            int portEnd = portServer + 9;
            int number = Integer.parseInt(code.substring(nodeNameLength+11,nodeNameLength+18));
            number = number - auth;
            //显示部分
            sendInfo(getLanguage("Code_DecodeSuccess"),sender);
            sendInfo(getLanguage("Code_DecodeNode")+nodeName,sender);
            sendInfo(getLanguage("Code_DecodeNumber")+number,sender);
            sendInfo(getLanguage("Code_DecodePortServer")+portServer,sender);
            sendInfo(getLanguage("Code_DecodePortRange")+portStart+"-"+portEnd,sender);
            //缓存部分
            if (cache) {
                codeMap.put(code+"-code", code);
                codeMap.put(code+"-node", nodeName);
                codeMap.put(code+"-number", String.valueOf(number));
                codeMap.put(code+"-portServer", String.valueOf(portServer));
                codeMap.put(code+"-portStart", String.valueOf(portStart));
                codeMap.put(code+"-portEnd", String.valueOf(portEnd));
            }
            return true;
        } catch (StringIndexOutOfBoundsException | NumberFormatException ex) {
            sendWarn(getLanguage("Code_DecodeFailed"),sender);
            return false;
        } catch (Exception e) {
            sendException(e);
            sendWarn(getLanguage("Code_DecodeFailed"),sender);
            return false;
        }
    }
}
