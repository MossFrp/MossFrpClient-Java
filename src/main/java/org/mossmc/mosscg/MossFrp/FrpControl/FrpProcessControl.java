package org.mossmc.mosscg.MossFrp.FrpControl;

import com.alibaba.fastjson.JSONObject;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static org.mossmc.mosscg.MossFrp.BasicVoid.*;

public class FrpProcessControl {
    public static OutputStream outputStream;

    public static void input(JSONObject jsonObject) throws Exception{
        if (getConfig("processDebug").equals("true")) {
            sendDebug(jsonObject.toString());
        }
        String input = jsonObject.toString() + "\r\n";
        outputStream.write(input.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
