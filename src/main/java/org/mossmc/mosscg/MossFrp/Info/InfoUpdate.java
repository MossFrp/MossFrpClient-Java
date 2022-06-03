package org.mossmc.mosscg.MossFrp.Info;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrp.BasicVoid.*;
import static org.mossmc.mosscg.MossFrp.BasicInfo.*;

public class InfoUpdate {
    public static void updateThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread updateThread = new Thread(InfoUpdate::update);
        updateThread.setName("infoUpdateThread");
        singleThreadExecutor.execute(updateThread);
    }

    public static boolean firstUpdate = true;

    @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
    public static void update() {
        while (true) {
            try {
                updateDomains();
                checkNotice();
                checkUpdate();
                firstUpdate = false;
                Thread.sleep(1000*60*60);
            } catch (Exception e) {
                sendException(e);
            }
        }
    }

    public static void updateDomains() {
        listDomains.add("mossfrp.cn");
        if (getConfig("checkDomains").equals("false")) {
            return;
        }
        try {
            JSONObject jsonObject = getJSONFromURL(getDomainsAPIAddress);
            if (jsonObject == null) {
                return;
            }
            listDomains.clear();
            int count = jsonObject.getInteger("count");
            int i = 1;
            while (i <= count) {
                listDomains.add(jsonObject.getString(String.valueOf(i)));
                i++;
            }
            sendInfo("#lang#Start_UpdateDomainsComplete");
        } catch (Exception e) {
            if (firstUpdate) {
                sendException(e);
            }
            listDomains.add("mossfrp.cn");
            sendWarn("#lang#Start_UpdateDomainsFailed");
        }
    }

    public static void checkUpdate() {
        if (getConfig("checkUpdate").equals("false")) {
            return;
        }
        try {
            JSONObject jsonObject = getJSONFromURL(getCheckUpdateAddress);
            if (jsonObject == null) {
                return;
            }
            String latest = jsonObject.getString("latest");
            if (!getVersion.equals(latest)) {
                sendInfo(getLanguage("Start_NeedUpdate").replace("[version]",latest));
                int count = jsonObject.getInteger("count");
                int i = 1;
                while (i <= count) {
                    sendInfo(jsonObject.getString(String.valueOf(i)));
                    i++;
                }
                return;
            }
            sendInfo("#lang#Start_CheckUpdateComplete");
        } catch (Exception e) {
            if (firstUpdate) {
                sendException(e);
            }
            sendWarn("#lang#Start_CheckUpdateFailed");
        }
    }

    public static void checkNotice() {
        if (getConfig("checkNotice").equals("false")) {
            return;
        }
        try {
            JSONObject jsonObject = getJSONFromURL(getCheckNoticeAddress);
            if (jsonObject == null) {
                return;
            }
            int count = jsonObject.getInteger("count");
            int i = 1;
            while (i <= count) {
                sendInfo(jsonObject.getString(String.valueOf(i)));
                i++;
            }
        } catch (Exception e) {
            if (firstUpdate) {
                sendException(e);
            }
            sendWarn("#lang#Start_CheckNoticeFailed");
        }
    }

    public static JSONObject getJSONFromURL(String address) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            URL target = new URL(address);
            URLConnection targetConnection = target.openConnection();
            targetConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            BufferedReader input = new BufferedReader(new InputStreamReader(targetConnection.getInputStream(), StandardCharsets.UTF_8));
            String inputLine;
            while ((inputLine = input.readLine()) != null) {
                stringBuilder.append(inputLine);
            }
            input.close();
            return JSONObject.parseObject(stringBuilder.toString());
        } catch (Exception e) {
            if (firstUpdate) {
                sendException(e);
            }
            return null;
        }
    }
}
