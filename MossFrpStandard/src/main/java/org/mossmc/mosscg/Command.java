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
    public static void readCommand(String command) {

    }
}
