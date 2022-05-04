package org.mossmc.mosscg.MossFrpProcess;

import org.mossmc.mosscg.MossFrpProcess.Request.Create.CreateDebug;
import org.mossmc.mosscg.MossFrpProcess.Request.RequestHeartbeat;
import org.mossmc.mosscg.MossFrpProcess.Request.RequestRead;

import java.io.*;
import java.nio.charset.Charset;

import static org.mossmc.mosscg.MossFrpProcess.Logger.*;
import static org.mossmc.mosscg.MossFrpProcess.CloseHook.*;
import static org.mossmc.mosscg.MossFrpProcess.BasicInfo.*;

public class ProcessMain {
    //主类
    //简单初始化一下
    //反正只是一个拿来调用process的工具jar
    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) {
        checkStart(args);
        sendInfo(CreateDebug.create("start"));
        registerDaemonThread();
        RequestHeartbeat.heartbeatThread();
        BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(System.in, Charset.defaultCharset()));
        //死循环监听
        while (true){
            try {
                String request = bufferedReader.readLine();
                RequestRead.readRequest(request);
            } catch (Exception e) {
                sendException(e);
            }
        }
    }

    //检查是否用参数启动
    //避免有用户直接点软件导致出bug
    //以及初始化系统设定
    public static void checkStart(String[] args) {
        boolean pass = false;
        for (String arg : args) {
            switch (arg) {
                case "-MossFrp=nb":
                    pass = true;
                    break;
                case "-systemType=linux":
                    getSystemType = systemType.linux;
                    break;
                case "-systemType=windows":
                    getSystemType = systemType.windows;
                    break;
                case "-mode=plugin":
                    getRunMode = runMode.plugin;
                    break;
                case "-mode=standard":
                    getRunMode = runMode.standard;
                    break;
                case "-mode=bungee":
                    getRunMode = runMode.bungee;
                    break;
                case "-mode=velocity":
                    getRunMode = runMode.velocity;
                    break;
                case "-mode=forge":
                    getRunMode = runMode.forge;
                    break;
                case "-mode=fabric":
                    getRunMode = runMode.fabric;
                    break;
                default:
                    break;
            }
        }
        if (!pass||getSystemType == null||getRunMode == null) {
            System.exit(1);
        }
    }
}
