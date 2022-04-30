package org.mossmc.mosscg.MossFrp.Config;

import java.util.Map;

public class ConfigGet {
    public static Map<?,?> configMap;

    public static String getConfig(String key) {
        return configMap.get(key).toString();
    }
}
