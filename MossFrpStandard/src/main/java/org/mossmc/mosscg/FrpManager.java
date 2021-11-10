package org.mossmc.mosscg;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FrpManager {
    //新建frp线程
    //单独线程因为readLine会堵塞线程
    //所以不能放主线程
    public static void newFrpThread(String code,String frpName) {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread frpCheck = new Thread(() -> runFrp(code,frpName));
        singleThreadExecutor.execute(frpCheck::start);
    }
    //frp线程运行方法
    public static void runFrp(String code,String frpName) {
        String prefix = code+"-"+frpName+"-";
        FileManager.writeFrpSettings(code,frpName);
    }
}
