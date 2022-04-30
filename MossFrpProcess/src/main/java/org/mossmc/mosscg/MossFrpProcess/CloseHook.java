package org.mossmc.mosscg.MossFrpProcess;

import org.mossmc.mosscg.MossFrpProcess.FrpControl.FrpProcess;
import org.mossmc.mosscg.MossFrpProcess.Request.Create.CreateDebug;

import static org.mossmc.mosscg.MossFrpProcess.Logger.*;

public class CloseHook {
    //注册守护进程
    @SuppressWarnings("BusyWait")
    public static void registerDaemonThread() {
        Thread thread = new Thread(() -> {
            try {
                registerCloseHook();
                while (true) {
                    Thread.sleep(1000);
                    System.gc();
                }
            } catch (InterruptedException e) {
                sendException(e);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    //注册关闭钩子
    public static void registerCloseHook() {
        Runtime.getRuntime().addShutdownHook((new Thread(() -> {
            sendInfo(CreateDebug.create("close"));
            try{
                for (String path : FrpProcess.frpProcessMap.keySet()) {
                    FrpProcess.frpProcessMap.get(path).destroy();
                }
            }catch (Exception e) {
                sendException(e);
            }
            sendInfo(CreateDebug.create("exit"));
        })));
    }
}
