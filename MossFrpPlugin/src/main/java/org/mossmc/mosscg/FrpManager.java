package org.mossmc.mosscg;

import org.bukkit.command.CommandSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrp.*;

public class FrpManager {
    //frp状态类型枚举
    public enum frpStatus {
        START,RUN,STOP
    }

    //frp缓存Map
    //保存frp状态
    public static Map<String,Enum<frpStatus>> frpStatusMap = new HashMap<>();

    //frp管理核心process
    //输入方法示例：
    //启动一个frp：run sq1-2333
    //停止一个frp：stop sq1-2333
    //心跳包：hb
    //输出内容示例
    //frp输出信息：msg sq1-2333 moss nb
    //frp停止运行：stop sq1-2333 moss nb
    public static Process frpProcess;

    //frp管理核心线程
    public static void loadProcessThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread read = new Thread(FrpManager::readProcess);
        singleThreadExecutor.execute(read::start);
        sendInfo(getLanguage("Guide_ProcessStartComplete"),null);
    }

    //frp管理核心心跳包线程
    public static void loadHeartbeatThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread beat = new Thread(FrpManager::heartbeat);
        singleThreadExecutor.execute(beat::start);
        loadSave();
    }

    //加载保存的配置文件
    public static void loadSave() {
        sendInfo(getLanguage("Guide_LoadSaveStart"),null);
        MossFrp.readSave();
        StartGuide.processStarted = true;
    }

    //frp管理核心心跳包功能
    public static void heartbeat() {
        OutputStream out = frpProcess.getOutputStream();
        String beatInfo = "hb \r\n";
        while (true) {
            try {
                Thread.sleep(200);
                out.write(beatInfo.getBytes(StandardCharsets.UTF_8));
                out.flush();
            } catch (Exception e) {
                sendException(e);
            }
        }
    }

    //启动frp调用
    public static void runFrpProcess(String path) {
        frpStatusMap.put(path, frpStatus.START);
        sendProcess("run "+path+"\r\n");
    }

    //关闭frp调用
    public static void stopFrpProcess(String path) {
        sendProcess("stop "+path+"\r\n");
    }

    //frp线程运行方法
    public static void runFrp(String code, String frpName, CommandSender sender) {
        //写入配置文件frpc.ini
        //生成frpc.exe文件
        String prefix = code+"-"+frpName+"-";
        FileManager.writeFrpSettings(code,frpName,sender);
        //运行frp主方法部分
        runFrpProcess(Code.tunnelMap.get(prefix+"node")+"-"+frpName);
    }

    public static void stopFrp(String path) {
        stopFrpProcess(path);
    }

    //给process输入信息
    public static void sendProcess(String info) {
        try {
            frpProcess.getOutputStream().write(info.getBytes(StandardCharsets.UTF_8));
            frpProcess.getOutputStream().flush();
        } catch (IOException e) {
            sendException(e);
        }
    }

    //frp管理核心读取方法
    //以及启动frp管理核心
    public static void readProcess() {
        try {
            if (getSystemType == systemType.windows) {
                frpProcess = Runtime.getRuntime().exec("java -server -Xmx30M -jar "+dataFolder+"/frps/MossFrpProcess.jar -MossFrp=nb -systemType=windows -mode=plugin -path="+dataFolder.toString().replace("\\","/"));
            }
            if (getSystemType == systemType.linux) {
                frpProcess = Runtime.getRuntime().exec("java -server -Xmx50M -jar "+dataFolder+"/frps/MossFrpProcess.jar -MossFrp=nb -systemType=linux -mode=plugin -path="+dataFolder.toString().replace("\\","/"));
            }
            BufferedReader frpOut = new BufferedReader(new InputStreamReader(frpProcess.getInputStream()));
            loadHeartbeatThread();
            while (true) {
                try {
                    String frpInfo = frpOut.readLine();
                    //System.out.println(frpInfo);
                    readProcessInfo(frpInfo);
                } catch (IOException e) {
                    sendWarn(getLanguage("Frp_ReadError"),null);
                    try {
                        frpProcess.destroy();
                    } catch (Exception exception) {
                        sendException(e);
                    }
                    break;
                }
            }
            loadProcessThread();
        } catch (IOException e) {
            sendException(e);
            System.exit(1);
        }
    }

    //解析Process返回信息
    //有亿点类似Command模块
    public static void readProcessInfo(String processSend) {
        if (processSend == null) {
            return;
        }
        //依据空格分割指令
        String[] cut = processSend.split("\\s+");
        String part1 = "";
        String part2 = "";
        String part3 = "";
        for (int i = 0; i < cut.length; i++) {
            switch (i) {
                case 0:
                    part1 = cut[i];
                    break;
                case 1:
                    part2 = cut[i];
                    break;
                case 2:
                    part3 = cut[i];
                    break;
                default:
                    break;
            }
        }
        String msg = "";
        if (processSend.length() >= part1.length()+part2.length()+2) {
            msg = processSend.substring(part1.length()+part2.length()+2);
        }
        if (part1.equals("msg")) {
            logMessage("[Process] "+processSend);
            if (!part2.equals("")) {
                readInfo(msg,part2);
                return;
            }
        }
        if (part1.equals("send")) {
            logMessage("[Process] "+processSend);
            if (part2.equals("")) {
                return;
            }
            String prefix = "["+msg+"] ";
            if (part2.equals("Start")) {
                frpStatusMap.put(msg, frpStatus.RUN);
            }
            if (part2.equals("Stop")) {
                frpStatusMap.put(msg, frpStatus.STOP);
            }
            sendInfo(prefix+getLanguage("Process_"+part2).replace("[tunnelName]",msg),null);
            return;
        }
        if (part1.equals("debug")) {
            return;
        }
        sendInfo(processSend,null);
    }

    //解析读取到的信息
    //说白就是翻译
    public static void readInfo(String info,String prefix) {
        prefix = "["+prefix+"] ";
        logMessage(prefix+info);
        if (info.contains("login to server success")) {
            sendInfo(prefix+getLanguage("Frp_InfoLogin"),null);
            return;
        }
        if (info.contains("proxy added")) {
            sendInfo(prefix+getLanguage("Frp_InfoStarting"),null);
            return;
        }
        if (info.contains("start proxy success")) {
            sendInfo(prefix+getLanguage("Frp_InfoStartSuccess"),null);
            return;
        }
        if (info.contains("port already used")) {
            sendInfo(prefix+getLanguage("Frp_InfoPortUsed"),null);
            return;
        }
        if (info.contains("work connection closed before response StartWorkConn message: EOF")) {
            sendInfo(prefix+getLanguage("Frp_InfoErrorEOF"),null);
            return;
        }
        if (info.contains("try to reconnect to server...")) {
            sendInfo(prefix+getLanguage("Frp_InfoReconnecting"),null);
            return;
        }
        if (info.contains("A connection attempt failed because the connected party did not properly respond after a period of time")) {
            sendInfo(prefix+getLanguage("Frp_InfoConnectNoRespond"),null);
            return;
        }
        if (info.contains("No connection could be made because the target machine actively refused it")) {
            if (info.contains("connect to local service")) {
                sendInfo(prefix + getLanguage("Frp_InfoConnectRejectLocal"),null);
            } else {
                sendInfo(prefix + getLanguage("Frp_InfoConnectRejectRemote"),null);
            }
            return;
        }
        if (info.contains("token in login doesn't match token from configuration")) {
            sendInfo(prefix+getLanguage("Frp_InfoConnectTokenWrong"),null);
            return;
        }
        if (info.contains("no such host")) {
            sendInfo(prefix+getLanguage("Frp_InfoNoSuchHost"),null);
            return;
        }
        if (info.contains("login to server failed: EOF")) {
            sendInfo(prefix+getLanguage("Frp_InfoLoginEOF"),null);
            return;
        }
        sendInfo(prefix+info,null);
    }
}
