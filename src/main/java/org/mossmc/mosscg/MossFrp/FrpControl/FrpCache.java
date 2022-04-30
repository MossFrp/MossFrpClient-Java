package org.mossmc.mosscg.MossFrp.FrpControl;

import org.mossmc.mosscg.MossFrp.BasicInfo;
import org.mossmc.mosscg.MossFrp.Code.CodeDecode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mossmc.mosscg.MossFrp.BasicVoid.*;

public class FrpCache {
    //以下是Frp必须要有的参数（写给自己看的）
    //协议|protocol，参数：tcp/udp
    //服务器地址|remoteAddress
    //服务器端口|remotePort
    //本地地址|localAddress
    //本地端口|localPort
    //开放端口|openPort
    //连接密钥|token
    //类型|type，参数：mossfrp/custom

    //节点|node，只有在mossfrp模式下才使用
    //开启指针压缩|useCompression，参数：true
    //开启链接加密|useEncryption，参数：true
    //开启protocol协议|proxyProtocolVersion，参数：v1/v2
    //隧道额外参数|tunnelExtraSettings
    //常规额外参数|commonExtraSettings
    public static Map<String,Map<String,String>> frpCache = new HashMap<>();
    public static Map<String,Map<?,?>> configCache = new HashMap<>();
    public static Map<String,BasicInfo.frpStatus> frpStatusCache = new HashMap<>();
    public static List<String> frpList = new ArrayList<>();

    public static void inputFrpCache(String name,String key,String value) {
        if (!frpCache.containsKey(name)) {
            frpCache.put(name,new HashMap<>());
        }
        frpCache.get(name).put(key,value);
    }

    public static String getFrpCache(String name,String key) {
        return frpCache.get(name).get(key);
    }

    public static void inputConfigCache(String name,Map<?,?> map) {
        configCache.put(name,map);
    }

    public static String getConfigCache(String name,String key) {
        return configCache.get(name).get(key).toString();
    }

    public static String getFrpListString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getLanguage("List_Prefix"));
        int i = 0;
        while (i < frpList.size()) {
            String name = frpList.get(i);
            stringBuilder.append("\r\n").append(BasicInfo.getFrpStatusName(frpStatusCache.get(name)));
            stringBuilder.append(name).append("\r\n");
            stringBuilder.append(getLanguage("List_Local")).append(getFrpCache(name,"localAddress"));
            stringBuilder.append(":").append(getFrpCache(name,"localPort")).append("\r\n");
            stringBuilder.append(getFrpRemoteString(name));
            i++;
        }
        return stringBuilder.toString();
    }

    public static String getFrpRemoteString(String name) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getLanguage("List_Remote"));
        int i2 = 0;
        if (getFrpCache(name,"type").equals("mossfrp")) {
            String node = CodeDecode.getNode(getFrpCache(name,"token"));
            while (i2 < BasicInfo.listDomains.size()) {
                if (i2>=1) {
                    stringBuilder.append(getLanguage("List_Or"));
                }
                String ip = BasicInfo.listDomains.get(i2)+":"+getFrpCache(name,"openPort");
                stringBuilder.append(node).append(".").append(ip);
                i2++;
            }
        } else {
            stringBuilder.append(getFrpCache(name,"remoteAddress")).append(":").append(getFrpCache(name,"openPort"));
        }
        return stringBuilder.toString();
    }
}
