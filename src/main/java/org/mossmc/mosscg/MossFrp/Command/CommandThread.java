package org.mossmc.mosscg.MossFrp.Command;

import org.mossmc.mosscg.MossFrp.BasicInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrp.BasicVoid.*;

public class CommandThread {
    public static void runThread() {
        if (BasicInfo.getRunMode.equals(BasicInfo.runMode.standard)) {
            ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
            Thread commandListener = new Thread(CommandThread::readThread);
            commandListener.setName("commandReadThread");
            singleThreadExecutor.execute(commandListener);
        }
        sendInfo("#lang#Start_CommandComplete");
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public static void readThread() {
        BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(System.in, Charset.defaultCharset()));
        while (true){
            try {
                String command = bufferedReader.readLine();
                if (command == null) {
                    sendWarn("#lang#Command_Unknown");
                    continue;
                }
                CommandRead.read(command.split("\\s+"));
            } catch (Exception e) {
                sendException(e);
            }
        }
    }
}
