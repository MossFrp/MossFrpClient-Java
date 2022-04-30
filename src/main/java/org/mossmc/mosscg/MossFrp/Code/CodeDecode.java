package org.mossmc.mosscg.MossFrp.Code;

import static org.mossmc.mosscg.MossFrp.BasicVoid.*;

public class CodeDecode {
    public static Boolean decode(String code) {
        try {
            //解码部分
            int nodeNameLength = Integer.parseInt(code.substring(0,1));
            String nodeName = code.substring(1,nodeNameLength+1);
            int auth = Integer.parseInt(code.substring(nodeNameLength+1,nodeNameLength+6));
            int portServer = Integer.parseInt(code.substring(nodeNameLength+6,nodeNameLength+11));
            portServer = portServer - auth;
            int portStart = portServer + 1;
            int portEnd = portServer + 9;
            int number = Integer.parseInt(code.substring(nodeNameLength+11,nodeNameLength+18));
            number = number - auth;
            //显示部分
            sendInfo("#lang#Code_DecodeSuccess");
            sendInfo(getLanguage("Code_DecodeNode")+nodeName);
            sendInfo(getLanguage("Code_DecodeNumber")+number);
            sendInfo(getLanguage("Code_DecodePortServer")+portServer);
            sendInfo(getLanguage("Code_DecodePortRange")+portStart+"-"+portEnd);
            //缓存部分
            CodeCache.inputCache(code,"node",nodeName);
            CodeCache.inputCache(code,"number",String.valueOf(number));
            CodeCache.inputCache(code,"portServer",String.valueOf(portServer));
            CodeCache.inputCache(code,"portStart",String.valueOf(portStart));
            CodeCache.inputCache(code,"portEnd",String.valueOf(portEnd));
            return true;
        } catch (StringIndexOutOfBoundsException | NumberFormatException ex) {
            sendWarn("#lang#Code_DecodeFailed");
            return false;
        } catch (Exception e) {
            sendException(e);
            sendWarn("#lang#Code_DecodeFailed");
            return false;
        }
    }

    public static String getNode(String code) {
        int nodeNameLength = Integer.parseInt(code.substring(0,1));
        return code.substring(1,nodeNameLength+1);
    }
}
