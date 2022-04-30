package org.mossmc.mosscg.MossFrp.FrpControl;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrp.FileControl.FileGet;

import static org.mossmc.mosscg.MossFrp.BasicInfo.*;
import static org.mossmc.mosscg.MossFrp.BasicVoid.*;

public class FrpProcessCore {
    public static Process coreProcess;

    public static void loadProcess() {
        try {
            if (coreProcess != null) {
                coreProcess.destroy();
            }
            String runCommand = "java -Xmx30M -jar "+FileGet.getProcessFile.toPath()+" -MossFrp=nb -systemType="+getSystemType.name()+" -mode="+getRunMode.name();
            coreProcess = Runtime.getRuntime().exec(runCommand);
            FrpProcessRead.inputStream = coreProcess.getInputStream();
            FrpProcessControl.outputStream = coreProcess.getOutputStream();

            JSONObject request = FrpProcessJSON.createNewRequest("path");
            request.put("path", getDataFolder.toString());
            FrpProcessControl.input(request);

            request = FrpProcessJSON.createNewRequest("timeout");
            request.put("time", getConfig("processTimeout"));
            FrpProcessControl.input(request);

            FrpProcessRead.loadReadThread();
            FrpProcessHeartbeat.loadHeartbeatThread();
        } catch (Exception e) {
            sendException(e);
            sendError("#lang#Core_RunError");
            System.exit(1);
        }
    }
}
