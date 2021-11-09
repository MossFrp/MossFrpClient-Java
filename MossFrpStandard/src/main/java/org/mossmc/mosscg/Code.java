package org.mossmc.mosscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static org.mossmc.mosscg.MossFrp.*;

public class Code {
    public static Map<String,String> codeMap = new HashMap<>();
    public static Map<String,String> tunnelMap = new HashMap<>();
    public static String readInput() {
        BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(System.in, Charset.defaultCharset()));
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            sendException(e);
            return "null";
        }
    }
    public static void sendErrorWarn(String warn) {
        sendWarn(warn);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            sendException(e);
        }
    }

    public static void printTunnelInfo(String code,String frpName) {
        String prefix = code+"-"+frpName+"-";
        sendInfo("");
        sendInfo(getLanguage("CodeGuide_PrintLine"));
        sendInfo(getLanguage("CodeGuide_PrintFrpName")+frpName);
        sendInfo(getLanguage("CodeGuide_PrintToken")+tunnelMap.get(prefix+"token"));
        sendInfo(getLanguage("CodeGuide_PrintProtocol")+tunnelMap.get(prefix+"frpType"));
        sendInfo(getLanguage("CodeGuide_PrintLocalIP")+tunnelMap.get(prefix+"localIP")+":"+tunnelMap.get(prefix+"portLocal"));
        sendInfo(getLanguage("CodeGuide_PrintRemoteIP")+tunnelMap.get(prefix+"node")+".mossfrp.cn:"+tunnelMap.get(prefix+"portOpen"));
        sendInfo(getLanguage("CodeGuide_PrintLine"));
        sendInfo(getLanguage("CodeGuide_PrintExit"));
    }

    public static void codeGuide(String code,String frpName) {
        if (!decode(code,true)) {
            return;
        }
        String prefix = code+"-"+frpName+"-";
        tunnelMap.put(prefix+"token",code);
        tunnelMap.put(prefix+"frpType","");
        tunnelMap.put(prefix+"localIP","");
        tunnelMap.put(prefix+"portOpen","");
        tunnelMap.put(prefix+"portLocal","");
        tunnelMap.put(prefix+"portServer",codeMap.get(code+"-portServer"));
        tunnelMap.put(prefix+"portStart",codeMap.get(code+"-portStart"));
        tunnelMap.put(prefix+"portEnd",codeMap.get(code+"-portEnd"));
        tunnelMap.put(prefix+"node",codeMap.get(code+"-node"));
        while (true) {
            printTunnelInfo(code,frpName);
            sendInfo(getLanguage("CodeGuide_ProtocolInfo"));
            String read = readInput();
            if (read.equals("exit")) {
                sendInfo(getLanguage("CodeGuide_Exit"));
                return;
            }
            if (read.equals("tcp") || read.equals("udp")) {
                tunnelMap.put(code+"-"+frpName+"-"+"frpType",read);
                break;
            }
            sendErrorWarn(getLanguage("CodeGuide_ProtocolWarn"));
        }
        while (true) {
            printTunnelInfo(code,frpName);
            sendInfo(getLanguage("CodeGuide_LocalIPInfo"));
            String read = readInput();
            if (read.equals("exit")) {
                sendInfo(getLanguage("CodeGuide_Exit"));
                return;
            }
            if (read.contains(":") || read.contains("：")) {
                sendErrorWarn(getLanguage("CodeGuide_LocalIPWarnPort"));
                continue;
            }
            if (read.length() > 0) {
                tunnelMap.put(code+"-"+frpName+"-"+"localIP",read);
                break;
            }
            sendErrorWarn(getLanguage("CodeGuide_LocalIPWarn"));
        }
        while (true) {
            printTunnelInfo(code,frpName);
            sendInfo(getLanguage("CodeGuide_LocalPortInfo"));
            String read = readInput();
            if (read.equals("exit")) {
                sendInfo(getLanguage("CodeGuide_Exit"));
                return;
            }
            int port;
            try {
                port = Integer.parseInt(read);
            } catch (NumberFormatException e) {
                sendErrorWarn(getLanguage("CodeGuide_LocalPortWarn"));
                continue;
            }
            if (port > 0 && port <= 65535) {
                tunnelMap.put(code+"-"+frpName+"-"+"portLocal",read);
                break;
            }
            sendErrorWarn(getLanguage("CodeGuide_LocalPortWarn"));
        }
        while (true) {
            printTunnelInfo(code,frpName);
            sendInfo(getLanguage("CodeGuide_RemotePortInfo").replace("[remotePortRange]",tunnelMap.get(prefix+"portStart")+"-"+tunnelMap.get(prefix+"portEnd")));
            String read = readInput();
            if (read.equals("exit")) {
                sendInfo(getLanguage("CodeGuide_Exit"));
                return;
            }
            int start = Integer.parseInt(tunnelMap.get(prefix+"portStart"));
            int end = Integer.parseInt(tunnelMap.get(prefix+"portEnd"));
            int select = Integer.parseInt(read);
            if (select >= start && select <= end) {
                tunnelMap.put(code+"-"+frpName+"-"+"portOpen",read);
                break;
            }
            sendErrorWarn(getLanguage("CodeGuide_RemotePortWarn").replace("[remotePortRange]",tunnelMap.get(prefix+"portStart")+"-"+tunnelMap.get(prefix+"portEnd")));
        }
        while (true) {
            printTunnelInfo(code,frpName);
            sendInfo(getLanguage("CodeGuide_AdvancedInfo"));
            String read = readInput();
            if (read.equals("exit")) {
                sendInfo(getLanguage("CodeGuide_Exit"));
                return;
            }
            if (read.length() < 1) {
                tunnelMap.put(code+"-"+frpName+"-"+"advancedSettings","0");
                break;
            }
            try {
                Integer.parseInt(read);
                tunnelMap.put(code+"-"+frpName+"-"+"advancedSettings",read);
                break;
            } catch (NumberFormatException e) {
                sendErrorWarn(getLanguage("CodeGuide_AdvancedWarn"));
            }
        }
        printTunnelInfo(code,frpName);
        sendInfo(getLanguage("CodeGuide_Complete"));
    }

    public static Boolean decode(String code, boolean cache) {
        try {
            int nodeNameLength = Integer.parseInt(code.substring(0,1));
            String nodeName = code.substring(1,nodeNameLength+1);
            int auth = Integer.parseInt(code.substring(nodeNameLength+1,nodeNameLength+6));
            int portServer = Integer.parseInt(code.substring(nodeNameLength+6,nodeNameLength+11));
            portServer = portServer - auth;
            int portStart = portServer + 1;
            int portEnd = portServer + 9;
            int number = Integer.parseInt(code.substring(nodeNameLength+11,nodeNameLength+18));
            number = number - auth;
            sendInfo(getLanguage("Code_DecodeSuccess"));
            sendInfo(getLanguage("Code_DecodeNode")+nodeName);
            sendInfo(getLanguage("Code_DecodeNumber")+number);
            sendInfo(getLanguage("Code_DecodePortServer")+portServer);
            sendInfo(getLanguage("Code_DecodePortRange")+portStart+"-"+portEnd);
            if (cache) {
                codeMap.put(code+"-code", code);
                codeMap.put(code+"-node", nodeName);
                codeMap.put(code+"-number", String.valueOf(number));
                codeMap.put(code+"-portServer", String.valueOf(portServer));
                codeMap.put(code+"-portStart", String.valueOf(portStart));
                codeMap.put(code+"-portEnd", String.valueOf(portEnd));
            }
            return true;
        } catch (Exception e) {
            sendException(e);
            sendWarn(getLanguage("Code_DecodeFailed"));
            return false;
        }
    }
}
