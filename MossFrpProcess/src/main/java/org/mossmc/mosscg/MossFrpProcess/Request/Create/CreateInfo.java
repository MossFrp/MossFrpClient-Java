package org.mossmc.mosscg.MossFrpProcess.Request.Create;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpProcess.Request.RequestJSON;

public class CreateInfo {
    public static JSONObject create(String name,String info) {
        JSONObject jsonObject = RequestJSON.createNewRequest("info");
        jsonObject.put("name",name);
        jsonObject.put("info",info);
        return jsonObject;
    }
}
