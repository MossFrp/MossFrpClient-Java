package org.mossmc.mosscg.MossFrpProcess;

public class BasicInfo {
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
        plugin,standard,bungee,velocity,forge,fabric
    }

    //文件夹path
    public static String basicPath = "./MossFrp/frps/";
}
