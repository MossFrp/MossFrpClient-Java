package org.mossmc.mosscg;

import static org.mossmc.mosscg.MossFrp.*;

public class StartGuide {
    //启动完成参数
    //启动完成之后会被设置为true
    public static boolean processStarted = false;
    //启动引导
    //我咋感觉我写啥都要单独写个引导
    public static void start() {
        sendInfo(getLanguage("Guide_ProcessStart"),null);
        FrpManager.loadProcessThread();
        while (!processStarted) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                sendException(e);
            }
        }
        sendInfo(getLanguage("Guide_StartComplete").replace("help","mossfrp help"),null);
    }
}
