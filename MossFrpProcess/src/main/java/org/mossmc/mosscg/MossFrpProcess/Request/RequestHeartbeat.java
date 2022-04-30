package org.mossmc.mosscg.MossFrpProcess.Request;

import org.mossmc.mosscg.MossFrpProcess.Request.Create.CreateDebug;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrpProcess.Logger.sendException;
import static org.mossmc.mosscg.MossFrpProcess.Logger.sendInfo;

public class RequestHeartbeat {
    //创建心跳线程
    public static void heartbeatThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread heartbeat = new Thread(RequestHeartbeat::heartbeat);
        singleThreadExecutor.execute(heartbeat::start);
    }

    //心跳方法部分
    public static long beatTime;
    public static long timeout = 5000;
    public static void heartbeat() {
        beatTime = System.currentTimeMillis();
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                sendException(e);
            }
            if (beatTime < System.currentTimeMillis()-timeout) {
                sendInfo(CreateDebug.create("timeout"));
                System.exit(0);
            }
        }
    }
}
