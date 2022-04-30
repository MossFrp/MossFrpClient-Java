package org.mossmc.mosscg.MossFrpProcess.Request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class RequestJSON {
    //创建新的请求
    //type参数：exception debug
    public static JSONObject createNewRequest(String type) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type",type);
        return jsonObject;
    }

    public static JSONObject readRequestToJSON(String request) {
        return JSON.parseObject(request);
    }
}
