package org.mossmc.mosscg.MossFrpProcess.Request.Create;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpProcess.Request.RequestJSON;

public class CreateDebug {
    public static JSONObject create(String debug) {
        JSONObject jsonObject = RequestJSON.createNewRequest("debug");
        jsonObject.put("debug",debug);
        return jsonObject;
    }
}
