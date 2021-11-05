package org.mossmc.mosscg;

import java.util.HashMap;
import java.util.Map;

import static org.mossmc.mosscg.MossFrp.*;

public class Code {
    public static Map<String,Map<String,String>> codeMap = new HashMap<>();
    public static void decode(String code) {
        try {
            int nodeNameLength = Integer.parseInt(code.substring(0,1));
            String nodeName = code.substring(1,nodeNameLength+1);
            int auth = Integer.parseInt(code.substring(nodeNameLength+1,nodeNameLength+6));
            int portServer = Integer.parseInt(code.substring(nodeNameLength+6,nodeNameLength+11));
            portServer = portServer - auth;
            int portStart = portServer + 1;
            int portEnd = portServer + 9;
            int number = Integer.parseInt(code.substring(nodeNameLength+11,nodeNameLength+18));
            number = number - auth;
            sendInfo(getLanguage("Code_DecodeSuccess"));
            sendInfo(getLanguage("Code_DecodeNode")+nodeName);
            sendInfo(getLanguage("Code_DecodeNumber")+number);
            sendInfo(getLanguage("Code_DecodePortServer")+portServer);
            sendInfo(getLanguage("Code_DecodePortRange")+portStart+"-"+portEnd);
            
        } catch (Exception e) {
            sendException(e);
            sendWarn(getLanguage("Code_DecodeFailed"));
        }
    }
}
