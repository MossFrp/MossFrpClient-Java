package org.mossmc.mosscg.MossFrp.Code;

import java.util.HashMap;
import java.util.Map;

public class CodeCache {
    public static Map<String,Map<String,String>> codeCacheMap = new HashMap<>();

    public static void inputCache(String code,String key,String value) {
        if (!codeCacheMap.containsKey(code)) {
            codeCacheMap.put(code,new HashMap<>());
        }
        codeCacheMap.get(code).put(key,value);
    }

    public static String getCache(String code,String key) {
        return codeCacheMap.get(code).get(key);
    }
}
