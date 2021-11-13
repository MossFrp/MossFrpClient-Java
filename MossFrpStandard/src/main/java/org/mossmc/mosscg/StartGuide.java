package org.mossmc.mosscg;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrp.*;

public class StartGuide {
    //启动引导
    //我咋感觉我写啥都要单独写个引导
    public static void start() {
        sendInfo(getLanguage("Guide_ProcessStart"));
        FrpManager.loadProcessThread();
        sendInfo(getLanguage("Guide_CommandStart"));
        commandThread();
        sendInfo(getLanguage("Guide_StartComplete"));
    }

    //指令系统线程
    //单独一个线程免得把主线程炸了
    public static void commandThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread commandListener = new Thread(Command::listenCommand);
        commandListener.setName("commandListenerThread");
        singleThreadExecutor.execute(commandListener);
        sendInfo(getLanguage("Guide_CommandStartComplete"));
    }
}
