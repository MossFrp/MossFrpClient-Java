package org.mossmc.mosscg.MossFrp.FrpControl;

import com.alibaba.fastjson.JSONObject;

import static org.mossmc.mosscg.MossFrp.BasicVoid.sendException;

public class FrpStop {
    public static void stopFrp(String name) throws Exception {
        JSONObject request = FrpProcessJSON.createNewRequest("stop");
        request.put("name",name);
        FrpProcessControl.input(request);
    }

    public static void stopAll() {
        int i = 0;
        while (i < FrpCache.frpList.size()) {
            try {
                FrpStop.stopFrp(FrpCache.frpList.get(i));
            } catch (Exception e) {
                sendException(e);
            }
            i++;
        }
    }
}
