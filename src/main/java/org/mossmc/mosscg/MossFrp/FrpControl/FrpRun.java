package org.mossmc.mosscg.MossFrp.FrpControl;

import com.alibaba.fastjson.JSONObject;

public class FrpRun {
    public static void runFrp(String name) throws Exception {
        JSONObject request = FrpProcessJSON.createNewRequest("run");
        request.put("name",name);
        FrpProcessControl.input(request);
    }
}
