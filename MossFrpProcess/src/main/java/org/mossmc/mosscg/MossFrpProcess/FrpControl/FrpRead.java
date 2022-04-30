package org.mossmc.mosscg.MossFrpProcess.FrpControl;

import org.mossmc.mosscg.MossFrpProcess.Request.Create.CreateInfo;

import java.io.BufferedReader;
import java.io.IOException;

import static org.mossmc.mosscg.MossFrpProcess.FrpControl.FrpStop.stopFrp;
import static org.mossmc.mosscg.MossFrpProcess.Logger.sendInfo;

public class FrpRead {
    public static void readFrp(String path, Process frp, BufferedReader frpOut) {
        while (true) {
            if (!frp.isAlive()) {
                stopFrp(path);
                return;
            }
            try {
                String frpInfo = frpOut.readLine();
                if (frpInfo == null) {
                    continue;
                }
                sendInfo(CreateInfo.create(path,frpInfo));
            } catch (Exception e) {
                sendInfo(CreateInfo.create(path,"error"));
            }
        }
    }
}
