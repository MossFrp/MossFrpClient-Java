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
        //指令部分（写注释只是为了方便看）
        //空指令判断
        if (command.length() < 1) {
            sendWarn(getLanguage("Command_Unknown"));
            return false;
        }
        switch (part1) {
            case "help":
                sendInfo(getLanguage("Command_Help"));
                return true;
            case "code":
                sendInfo(getLanguage("Command_helpCodeMain"));
                return true;
            default:
                sendWarn(getLanguage("Command_Unknown"));
                return false;
        }
    }
}
