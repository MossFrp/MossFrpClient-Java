package org.mossmc.mosscg.MossFrpProcess.FrpControl;

import org.mossmc.mosscg.MossFrpProcess.BasicInfo;
import org.mossmc.mosscg.MossFrpProcess.Request.Create.CreateFailed;
import org.mossmc.mosscg.MossFrpProcess.Request.Create.CreateRun;
import org.mossmc.mosscg.MossFrpProcess.Request.Create.CreateSuccess;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrpProcess.BasicInfo.getSystemType;
import static org.mossmc.mosscg.MossFrpProcess.FrpControl.FrpThread.frpThreadMap;
import static org.mossmc.mosscg.MossFrpProcess.Logger.sendException;
import static org.mossmc.mosscg.MossFrpProcess.Logger.sendInfo;

public class FrpRun {
    //新建frp线程
    //单独线程因为readLine会堵塞线程
    //所以不能放主线程
    public static void newFrpThread(String path) {
        sendInfo(CreateRun.create(path));
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread frp = new Thread(() -> newFrp(path));
        singleThreadExecutor.execute(frp::start);
        frpThreadMap.put(path,frp);
    }

    //新建frp方法
    public static void newFrp(String path) {
        Runtime run = Runtime.getRuntime();
        try {
            Process frp;
            if (getSystemType == BasicInfo.systemType.windows) {
                frp = run.exec("taskkill /im frpc-"+path+".exe /f");
                BufferedReader output = new BufferedReader(new InputStreamReader(frp.getInputStream()));
                output.readLine();
                output.close();
                frp = run.exec(BasicInfo.basicPath+path+"/frpc-"+path+".exe -c "+ BasicInfo.basicPath+path+"/frpc.ini");
                BufferedReader frpOut = new BufferedReader(new InputStreamReader(frp.getInputStream()));
                FrpProcess.frpProcessMap.put(path,frp);
                sendInfo(CreateSuccess.create(path));
                FrpRead.readFrp(path,frp,frpOut);
            }
            if (getSystemType == BasicInfo.systemType.linux) {
                frp = run.exec(BasicInfo.basicPath+path+"/frpc-"+path+" -c "+ BasicInfo.basicPath+path+"/frpc.ini");
                BufferedReader frpOut = new BufferedReader(new InputStreamReader(frp.getInputStream()));
                FrpProcess.frpProcessMap.put(path,frp);
                sendInfo(CreateSuccess.create(path));
                FrpRead.readFrp(path,frp,frpOut);
            }
        }catch (Exception e){
            sendException(e);
            sendInfo(CreateFailed.create(path));
        }
    }
}
