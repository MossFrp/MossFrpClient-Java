package org.mossmc.mosscg.MossFrpProcess.Request;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpProcess.BasicInfo;
import org.mossmc.mosscg.MossFrpProcess.FrpControl.FrpRun;
import org.mossmc.mosscg.MossFrpProcess.FrpControl.FrpStop;
import org.mossmc.mosscg.MossFrpProcess.Logger;
import org.mossmc.mosscg.MossFrpProcess.Request.Create.CreateInfo;

/*
常见Request参数及其内容
{
"type": "run",
"name": "abc-xxx"
}
{
"type": "stop",
"name": "abc-xxx"
}
{
"type": "heartbeat"
}
{
"type": "path",
"path": "./MossFrp"
}
{
"type": "timeout",
"time": "5000"
}
*/

public class RequestRead {
    public static void readRequest(String request) {
        if (request == null) {
            return;
        }
        JSONObject jsonObject = RequestJSON.readRequestToJSON(request);
        switch (jsonObject.getString("type")) {
            case "run":
                FrpRun.newFrpThread(jsonObject.getString("name"));
                break;
            case "stop":
                FrpStop.stopFrp(jsonObject.getString("name"));
                break;
            case "heartbeat":
                RequestHeartbeat.beatTime = System.currentTimeMillis();
                break;
            case "path":
                BasicInfo.basicPath = jsonObject.getString("path")+"/frps/";
                break;
            case "timeout":
                RequestHeartbeat.timeout = jsonObject.getLong("time");
                break;
            case "exit":
                System.exit(0);
                break;
            default:
                break;
        }
    }
}
