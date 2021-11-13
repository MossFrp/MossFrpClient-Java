package org.mossmc.mosscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import static org.mossmc.mosscg.MossFrp.*;

public class Command {
    //通过System.in读取指令
    //然后交给readCommand方法解析
    public static void listenCommand() {
        BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(System.in, Charset.defaultCharset()));
        //死循环监听
        while (true){
            try {
                String command = bufferedReader.readLine();
                sendCommand(command);
                readCommand(command);
            } catch (Exception e) {
                sendException(e);
            }
        }
    }

    //无情的指令读取工具方法
    //说白就是分割指令然后读取
    public static Boolean readCommand(String command) {
        if (command == null) {
            return true;
        }
        //依据空格分割指令
        String[] cut = command.split("\\s+");
        String part1 = "";
        String part2 = "";
        String part3 = "";
        String part4 = "";
        String part5 = "";
        for (int i = 0; i < cut.length; i++) {
            switch (i) {
                case 0:
                    part1 = cut[i];
                    break;
                case 1:
                    part2 = cut[i];
                    break;
                case 2:
                    part3 = cut[i];
                    break;
                case 3:
                    part4 = cut[i];
                    break;
                case 4:
                    part5 = cut[i];
                    break;
                default:
                    break;
            }
        }
        //读取指令部分
        //空指令判断
        if (command.length() < 1) {
            sendWarn(getLanguage("Command_Unknown"));
            return false;
        }
        switch (part1) {
            //退出指令
            case "stop":
                System.exit(0);
                return true;
            //帮助指令
            case "help":
                sendInfo(getLanguage("Command_Help"));
                return true;
            //隧道相关指令
            case "tunnel":
                if (part2.equals("new")) {
                    if (!part3.equals("") && !part4.equals("")) {
                        if (FrpManager.frpStatusMap.containsKey(part3)) {
                            sendInfo(getLanguage("Command_TunnelAlreadyExist"));
                            return false;
                        }
                        sendInfo(getLanguage("CodeGuide_Start"));
                        Code.codeGuide(part4,part3);
                        return true;
                    }
                    sendInfo(getLanguage("Command_HelpTunnelNew"));
                    return false;
                }
                //if (part2.equals("remove")) {
                //    return false;
                //}
                sendInfo(getLanguage("Command_HelpTunnelMain"));
                return false;
            //激活码相关指令
            case "code":
                if (part2.equals("decode")) {
                    if (!part3.equals("")) {
                        Code.decode(part3,false);
                        return true;
                    }
                    sendInfo(getLanguage("Command_HelpCodeDecode"));
                    return false;
                }
                sendInfo(getLanguage("Command_HelpCodeMain"));
                return false;
            //未知指令判断
            default:
                sendWarn(getLanguage("Command_Unknown"));
                return false;
        }
    }
}
