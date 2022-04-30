package org.mossmc.mosscg.MossFrp.Language;

import java.util.Map;

public class LanguageGet {
    public static Map<?,?> languageMap;

    public static String getLanguage(String key) {
        return languageMap.get(key).toString();
    }
}
