package org.mossmc.mosscg.MossFrp.FrpControl;

import org.mossmc.mosscg.MossFrp.BasicVoid;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrp.BasicVoid.*;

public class FrpProcessHeartbeat {
    public static void loadHeartbeatThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread heartbeat = new Thread(FrpProcessHeartbeat::heartbeat);
        singleThreadExecutor.execute(heartbeat::start);
    }

    @SuppressWarnings("BusyWait")
    public static void heartbeat() {
        while (true) {
            try {
                FrpProcessControl.input(FrpProcessJSON.createNewRequest("heartbeat"));
                Thread.sleep(200);
            } catch (Exception e) {
                sendException(e);
                sendError("#lang#Core_HeartbeatError");
                FrpProcessCore.loadProcess();
                BasicVoid.reloadMossFrp();
                //这里可能有一个无限循环，可能会产生报错
                //暂时没有想到好的解决方法，先将就着用吧
                //有大佬看到可以修一下下www
                break;
            }
        }
    }
}
