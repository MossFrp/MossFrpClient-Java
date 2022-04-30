package org.mossmc.mosscg.MossFrpProcess;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpProcess.Request.RequestJSON;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger {
    //发送信息方法
    public static void sendInfo(JSONObject jsonObject) {
        System.out.println(jsonObject.toString());
    }

    //发送错误方法
    public static void sendException(Exception e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter= new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        JSONObject jsonObject = RequestJSON.createNewRequest("exception");
        jsonObject.put("exception",stringWriter.toString());
        sendInfo(jsonObject);
        try {
            printWriter.close();
            stringWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
