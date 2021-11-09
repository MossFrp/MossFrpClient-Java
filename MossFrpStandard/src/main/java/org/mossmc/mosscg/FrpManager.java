package org.mossmc.mosscg;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FrpManager {
    public static void newFrpThread(String code,String frpName) {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread frpCheck = new Thread(() -> runFrp(code,frpName));
        singleThreadExecutor.execute(frpCheck::start);
    }
    public static void runFrp(String code,String frpName) {

    }
}
