package org.mossmc.mosscg.MossFrpProcess.FrpControl;

import org.mossmc.mosscg.MossFrpProcess.Request.Create.CreateInfo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrpProcess.Logger.sendException;
import static org.mossmc.mosscg.MossFrpProcess.Logger.sendInfo;

public class FrpRead {
    public static void newReadThread(String path, Process frp) {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread read = new Thread(() -> readFrp(path,frp));
        singleThreadExecutor.execute(read::start);
    }

    public static void readFrp(String path, Process frp) {
        InputStream inputStream = frp.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader frpOut = new BufferedReader(inputStreamReader);
        String frpInfo;
        while (true) {
            if (!frp.isAlive()) {return;}
            try {
                frpInfo = frpOut.readLine();
                if (frpInfo == null) {continue;}
                sendInfo(CreateInfo.create(path,frpInfo));
            } catch (Exception e) {
                sendException(e);
                sendInfo(CreateInfo.create(path,"error"));
            }
        }
    }
}
