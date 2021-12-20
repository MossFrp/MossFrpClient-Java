package org.mossmc.mosscg;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MossFrpProcess {
    //主类
    //简单初始化一下
    //反正只是一个拿来调用process的工具
    public static void main(String[] args) {
        checkStart(args);
        sendInfo("debug main start");
        registerDaemonThread();
        heartbeatThread();
        sendInfo("debug main request");
        BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(System.in, Charset.defaultCharset()));
        //死循环监听
        while (true){
            try {
                String request = bufferedReader.readLine();
                readRequest(request);
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
                default:
                    if (getRunMode == runMode.plugin && arg.contains("-path")) {
                        basicPath = "./"+arg.split("=")[1]+"/frps/";
                    }
                    break;
            }
        }
        if (!pass||getSystemType == null||getRunMode == null) {
            sendInfo("please not start this without enough arguments!");
            System.exit(1);
        }
    }

    //系统类型
    //没加参数就默认win
    public static systemType getSystemType;

    public enum systemType {
        windows,linux
    }

    //运行模式
    //区分调用版本
    public static runMode getRunMode;

    public enum runMode {
        plugin,standard
    }

    //文件夹path
    public static String basicPath = "./MossFrp/frps/";

    //读取请求
    public static void readRequest(String request) {
        if (request == null) {
            return;
        }
        //依据空格分割指令
        String[] cut = request.split("\\s+");
        String part1 = "";
        String part2 = "";
        for (int i = 0; i < cut.length; i++) {
            switch (i) {
                case 0:
                    part1 = cut[i];
                    break;
                case 1:
                    part2 = cut[i];
                    break;
                default:
                    break;
            }
        }
        if (part1.equals("run")) {
            if (!part2.equals("")) {
                newFrpThread(part2);
                return;
            }
        }
        if (part1.equals("stop")) {
            if (!part2.equals("")) {
                stopFrp(part2);
                return;
            }
        }
        if (part1.equals("hb")) {
            beatTime = System.currentTimeMillis();
            return;
        }
        sendInfo("Unknown request");
    }

    //frp列表Map
    public static Map<String,Process> frpProcessMap = new HashMap<>();
    public static Map<String,Thread> frpThreadMap = new HashMap<>();

    //新建frp线程
    //单独线程因为readLine会堵塞线程
    //所以不能放主线程
    public static void newFrpThread(String path) {
        sendInfo("debug frp new");
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread frp = new Thread(() -> newFrp(path));
        singleThreadExecutor.execute(frp::start);
        frpThreadMap.put(path,frp);
    }

    //新建frp方法
    public static void newFrp(String path) {
        sendInfo("debug frp run");
        Runtime run = Runtime.getRuntime();
        try {
            if (getSystemType == systemType.windows) {
                sendInfo("send Start "+path);
                Process frp;
                frp = run.exec("taskkill /im frpc-"+path+".exe /f");
                BufferedReader output = new BufferedReader(new InputStreamReader(frp.getInputStream()));
                output.readLine();
                output.close();
                frp = run.exec(basicPath+path+"/frpc-"+path+".exe -c "+basicPath+path+"/frpc.ini");
                frpProcessMap.put(path,frp);
                sendInfo("send Success "+path);
                readFrp(path);
            }
            if (getSystemType == systemType.linux) {
                sendInfo("send Start "+path);
                Process frp;
                frp = run.exec(basicPath+path+"/frpc-"+path+" -c "+basicPath+path+"/frpc.ini");
                frpProcessMap.put(path,frp);
                sendInfo("send Success "+path);
                readFrp(path);
            }
        }catch (Exception e){
            sendException(e);
            sendInfo("send Failed "+path);
        }
    }

    //停止frp方法
    public static void stopFrp(String path) {
        try {
            frpThreadMap.get(path).interrupt();
            frpProcessMap.get(path).destroy();
            sendInfo("send Stop "+path);
        } catch (Exception e) {
            sendException(e);
        }
    }

    //读取frp返回流
    //然后输出信息
    public static void readFrp(String path) {
        Process frp = frpProcessMap.get(path);
        String prefix = path+" ";
        BufferedReader frpOut = new BufferedReader(new InputStreamReader(frp.getInputStream()));
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
                sendInfo("msg "+prefix+frpInfo);
            } catch (IOException e) {
                sendInfo("msg "+prefix+"error");
            }
        }
    }

    //发送信息方法
    public static void sendInfo(String info) {
        System.out.println(info);
    }

    //发送错误方法
    public static void sendException(Exception e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter= new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        sendInfo(stringWriter.toString());
        try {
            printWriter.close();
            stringWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //创建心跳线程
    public static void heartbeatThread() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Thread heartbeat = new Thread(MossFrpProcess::heartbeat);
        singleThreadExecutor.execute(heartbeat::start);
    }

    //心跳方法部分
    public static long beatTime;
    public static void heartbeat() {
        beatTime = System.currentTimeMillis();
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                sendException(e);
            }
            if (beatTime < System.currentTimeMillis()-5000) {
                sendInfo("Heartbeat timeout");
                System.exit(0);
            }
        }
    }

    //注册守护进程
    public static void registerDaemonThread() {
        Thread thread = new Thread(() -> {
            try {
                registerCloseHook();
                while (true) {
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                sendException(e);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    //注册关闭钩子
    public static void registerCloseHook() {
        Runtime.getRuntime().addShutdownHook((new Thread(() -> {
            sendInfo("debug close start");
            try{
                Iterator<String> iterator = frpProcessMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String path = iterator.next();
                    frpProcessMap.get(path).destroy();
                }
            }catch (Exception e) {
                sendException(e);
            }
            sendInfo("debug close complete");
        })));
    }
}
