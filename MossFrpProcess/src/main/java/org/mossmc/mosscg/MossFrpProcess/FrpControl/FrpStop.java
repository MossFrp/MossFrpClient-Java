package org.mossmc.mosscg.MossFrpProcess.FrpControl;

import org.mossmc.mosscg.MossFrpProcess.Request.Create.CreateStop;

import static org.mossmc.mosscg.MossFrpProcess.Logger.sendException;
import static org.mossmc.mosscg.MossFrpProcess.Logger.sendInfo;

public class FrpStop {
    //停止frp方法
    public static void stopFrp(String path) {
        try {
            if (!FrpThread.frpThreadMap.containsKey(path)) {
                return;
            }
            FrpThread.frpThreadMap.get(path).interrupt();
            FrpProcess.frpProcessMap.get(path).destroy();
            sendInfo(CreateStop.create(path));
        } catch (Exception e) {
            sendException(e);
        }
    }
}
