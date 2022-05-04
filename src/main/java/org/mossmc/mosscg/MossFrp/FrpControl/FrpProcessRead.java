package org.mossmc.mosscg.MossFrp.FrpControl;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrp.BasicInfo;
import org.mossmc.mosscg.MossFrp.Info.InfoLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrp.BasicVoid.*;
import static org.mossmc.mosscg.MossFrp.BasicVoid.getLanguage;

/*
常见Request格式
{
"type": "debug",
"debug": "Heartbeat timeout"
}
{
"type": "failed",
"name": "abc-xxx"
}
{
"type": "success",
"name": "abc-xxx"
}
{
"type": "run",
"name": "abc-xxx"
}
{
"type": "stop",
"name": "abc-xxx"
}
{
"type": "info",
"name": "abc-xxx",
"info": "I don't know."
}
 */
public class FrpProcessRead {
    public static InputStream inputStream;

    public static void loadReadThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread read = new Thread(FrpProcessRead::read);
        singleThreadExecutor.execute(read::start);
    }

    public static void read() {
        while (true) {
            try {
                BufferedReader frpOut = new BufferedReader(new InputStreamReader(inputStream));
                String frpInfo = frpOut.readLine();
                if (getConfig("processDebug").equals("true")) {
                    sendDebug(frpInfo);
                }
                readInfo(frpInfo);
            } catch (IOException e) {
                sendWarn(getLanguage("Frp_ReadError"));
                break;
            }
        }
    }

    public static void readInfo(String info) {
        JSONObject jsonObject = FrpProcessJSON.readRequestToJSON(info);
        switch (jsonObject.getString("type")) {
            case "debug":
                switch (jsonObject.getString("debug")) {
                    case "timeout":
                        sendInfo("#lang#Core_HeartbeatTimeout");
                        break;
                    case "start":
                        sendInfo("#lang#Core_ProcessStart");
                        break;
                    case "close":
                        sendInfo("#lang#Core_ProcessClose");
                        break;
                    case "exit":
                        sendInfo("#lang#Core_ProcessExit");
                        break;
                    default:
                        break;
                }
                break;
            case "exception":
                sendWarn(jsonObject.getString("exception"));
                break;
            case "failed":
                FrpCache.frpStatusCache.put(jsonObject.getString("name"), BasicInfo.frpStatus.offline);
                sendWarn(getLanguage("Core_FrpStartFailed").replace("[name]",jsonObject.getString("name")));
                break;
            case "info":
                infoTranslate(jsonObject.getString("name"),jsonObject.getString("info"));
                break;
            case "run":
                sendInfo(getLanguage("Core_FrpStartRun").replace("[name]",jsonObject.getString("name")));
                break;
            case "stop":
                FrpCache.frpStatusCache.put(jsonObject.getString("name"), BasicInfo.frpStatus.offline);
                sendInfo(getLanguage("Core_FrpStop").replace("[name]",jsonObject.getString("name")));
                break;
            case "success":
                FrpCache.frpStatusCache.put(jsonObject.getString("name"), BasicInfo.frpStatus.online);
                sendInfo(getLanguage("Core_FrpStartSucceed").replace("[name]",jsonObject.getString("name"))+FrpCache.getFrpRemoteString(jsonObject.getString("name")));
                break;
            default:
                break;
        }
    }

    public static void infoTranslate(String name,String info) {
        String prefix = "["+name+"] ";
        InfoLogger.fileInput(prefix+info);
        if (info.contains("login to server success")) {
            sendInfo(prefix+getLanguage("Frp_InfoLogin"));
            return;
        }
        if (info.contains("proxy added")) {
            sendInfo(prefix+getLanguage("Frp_InfoStarting"));
            return;
        }
        if (info.contains("start proxy success")) {
            sendInfo(prefix+getLanguage("Frp_InfoStartSuccess"));
            return;
        }
        if (info.contains("port already used")) {
            sendInfo(prefix+getLanguage("Frp_InfoPortUsed"));
            return;
        }
        if (info.contains("work connection closed before response StartWorkConn message: EOF")) {
            sendInfo(prefix+getLanguage("Frp_InfoErrorEOF"));
            return;
        }
        if (info.contains("try to reconnect to server...")) {
            sendInfo(prefix+getLanguage("Frp_InfoReconnecting"));
            return;
        }
        if (info.contains("A connection attempt failed because the connected party did not properly respond after a period of time")) {
            sendInfo(prefix+getLanguage("Frp_InfoConnectNoRespond"));
            return;
        }
        if (info.contains("No connection could be made because the target machine actively refused it")) {
            if (info.contains("connect to local service")) {
                sendInfo(prefix + getLanguage("Frp_InfoConnectRejectLocal"));
            } else {
                sendInfo(prefix + getLanguage("Frp_InfoConnectRejectRemote"));
            }
            return;
        }
        if (info.contains("token in login doesn't match token from configuration")) {
            sendInfo(prefix+getLanguage("Frp_InfoConnectTokenWrong"));
            return;
        }
        if (info.contains("no such host")) {
            sendInfo(prefix+getLanguage("Frp_InfoNoSuchHost"));
            return;
        }
        if (info.contains("login to server failed: EOF")) {
            sendInfo(prefix+getLanguage("Frp_InfoLoginEOF"));
            return;
        }
        if (info.contains("incoming a new work connection for udp proxy")) {
            String[] cut = info.split(",");
            sendInfo(prefix+getLanguage("Frp_InfoUDPConnection").replace("[address]",cut[1]));
            return;
        }
        if (info.contains("read from workConn for udp error: EOF")) {
            sendInfo(prefix+getLanguage("Frp_InfoUDPConnectionEOF"));
            return;
        }
        if (info.contains("control writer is closing")) {
            sendInfo(prefix+getLanguage("Frp_InfoWriterClosing"));
            return;
        }
        sendInfo(prefix+info);
    }
}
