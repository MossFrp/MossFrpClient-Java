package org.mossmc.mosscg.MossFrp.Config;

import org.mossmc.mosscg.MossFrp.FrpControl.FrpCache;

import java.io.File;
import java.io.FileWriter;

public class ConfigFrpSave {
    public static void writeFile(File cfgFile, String frpName, String dirPath) throws Exception{
        //写入frpc.ini
        FileWriter fileWriter = new FileWriter(cfgFile,false);
        fileWriter.write("[common]"+"\r\n");fileWriter.write("server_addr = "+ FrpCache.frpCache.get(frpName).get("remoteAddress")+"\r\n");
        fileWriter.write("server_port = "+FrpCache.frpCache.get(frpName).get("remotePort")+"\r\n");
        //fileWriter.write("log_file = "+dirPath.replaceAll("\\\\","/")+"frps.log"+"\r\n");
        //fileWriter.write("log_level = info"+"\r\n");
        //fileWriter.write("log_max_days = 7"+"\r\n");
        fileWriter.write("token = "+FrpCache.frpCache.get(frpName).get("token")+"\r\n");
        fileWriter.write(FrpCache.frpCache.get(frpName).get("commonExtraSettings") + "\r\n");
        fileWriter.write("\r\n");
        fileWriter.write("["+frpName+"]"+"\r\n");
        fileWriter.write("type = "+FrpCache.frpCache.get(frpName).get("protocol")+"\r\n");
        fileWriter.write("local_ip = "+FrpCache.frpCache.get(frpName).get("localAddress")+"\r\n");
        fileWriter.write("local_port = "+FrpCache.frpCache.get(frpName).get("localPort")+"\r\n");
        fileWriter.write("remote_port = "+FrpCache.frpCache.get(frpName).get("openPort")+"\r\n");
        fileWriter.write(FrpCache.frpCache.get(frpName).get("tunnelExtraSettings") + "\r\n");
        if (FrpCache.frpCache.get(frpName).get("useCompression").equals("true")) {
            fileWriter.write("use_compression = true\r\n");
        }
        if (FrpCache.frpCache.get(frpName).get("useEncryption").equals("true")) {
            fileWriter.write("tls_enable = true\r\n");
        }
        if (!FrpCache.frpCache.get(frpName).get("proxyProtocolVersion").equals("false")) {
            fileWriter.write("proxy_protocol_version = "+FrpCache.frpCache.get(frpName).get("proxyProtocolVersion")+"\r\n");
        }
        fileWriter.flush();
        fileWriter.close();
    }
}
