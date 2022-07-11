package org.mossmc.mosscg.MossFrp;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrp.Command.CommandThread;
import org.mossmc.mosscg.MossFrp.Config.ConfigCodeLoad;
import org.mossmc.mosscg.MossFrp.Config.ConfigLoad;
import org.mossmc.mosscg.MossFrp.FileControl.FileCheck;
import org.mossmc.mosscg.MossFrp.FileControl.FileLoad;
import org.mossmc.mosscg.MossFrp.FrpControl.FrpProcessControl;
import org.mossmc.mosscg.MossFrp.FrpControl.FrpProcessCore;
import org.mossmc.mosscg.MossFrp.FrpControl.FrpProcessJSON;
import org.mossmc.mosscg.MossFrp.Info.InfoGroup;
import org.mossmc.mosscg.MossFrp.Info.InfoUpdate;
import org.mossmc.mosscg.MossFrp.Language.LanguageLoad;
import org.mossmc.mosscg.MossFrp.Time.TimeCount;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrp.BasicVoid.*;

public class StartGuide {
    public static void runGuide(String[] args) {
        TimeCount.setStartTime();
        FileLoad.load();
        FileCheck.check();
        ConfigLoad.load();
        BasicInfo.loadSystemType();
        LanguageLoad.load(BasicVoid.getConfig("language"));
        registerDaemonThread();
        checkStart(args);
        InfoUpdate.updateThread();
        InfoGroup.sendStartGroup();
        FrpProcessCore.loadProcess();
        ConfigCodeLoad.loadAll();
        CommandThread.runThread();
        TimeCount.printStartUse();
    }

    public static void checkStart(String[] args) {
        if (BasicInfo.getRunMode != BasicInfo.runMode.standard) {
            return;
        }
        if (!Arrays.toString(args).contains("-MossFrp=nb")) {
            try {
                //
                FileWriter fileWriter = new FileWriter("./run.bat");//如果我用的是Linux呢 (
                //fileWriter.write("@echo off \r\n");
                //fileWriter.write("title MossFrp Standard Client \r\n");
                //fileWriter.write("java -Xmx50m -jar MossFrpJava.jar -MossFrp=nb \r\n");
                //fileWriter.write("pause \r\n");
                //fileWriter.flush();
                //fileWriter.close();
                switch (System.getProperty(" os.name").toLowerCase().contains("windows"))
                {
                    case true:
                        FileWriter fileWriter = new FileWriter("./run.bat");
                        fileWriter.write("java -Xmx50m -jar MossFrpJava.jar -MossFrp=nb \r\n");
                        fileWriter.flush();
                        fileWriter.close();
                        break;
                    case false:
                        FileWriter fileWriter = new FileWriter("./run.sh");
                        fileWriter.write("java -Xmx50m -jar MossFrpJava.jar -MossFrp=nb \r\n");
                        fileWriter.flush();
                        fileWriter.close();
                        break;
                }
                
            } catch (IOException e) {
                sendException(e);
            }
            BasicVoid.sendInfo("#lang#Start_CheckFailed");
            System.exit(1);
        }
    }

    public static void registerDaemonThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread gcThread = new Thread(StartGuide::gcThread);
        gcThread.setName("gcThread");
        gcThread.setDaemon(true);
        singleThreadExecutor.execute(gcThread);
    }

    @SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
    public static void gcThread() {
        registerCloseHook();
        while (true){
            try {
                Thread.sleep(2000);
                System.gc();
            } catch (InterruptedException e) {
                sendException(e);
            }
        }
    }

    //注册关闭钩子
    public static void registerCloseHook() {
        Runtime.getRuntime().addShutdownHook((new Thread(StartGuide::shutdownThread)));
    }

    public static void shutdownThread() {
        try {
            JSONObject request = FrpProcessJSON.createNewRequest("exit");
            FrpProcessControl.input(request);
            InfoGroup.sendEndGroup();
        } catch (Exception e) {
            sendException(e);
        }
    }
}
