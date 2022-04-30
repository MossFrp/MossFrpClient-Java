package org.mossmc.mosscg.MossFrp.Time;

import org.mossmc.mosscg.MossFrp.BasicVoid;
import org.mossmc.mosscg.MossFrp.Language.LanguageGet;

public class TimeCount {
    public static long startTime;

    public static void setStartTime() {
        startTime = System.currentTimeMillis();
    }

    public static double getRunTime() {
        long runTime = System.currentTimeMillis() - startTime;
        return (double) runTime/1000;
    }

    public static void printStartUse() {
        BasicVoid.sendInfo(LanguageGet.getLanguage("Start_Time").replace("[time]",String.valueOf(getRunTime())));
    }
}
