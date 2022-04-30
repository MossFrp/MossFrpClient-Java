package org.mossmc.mosscg.MossFrp.Language;

import org.mossmc.mosscg.MossFrp.BasicInfo;
import org.mossmc.mosscg.MossFrp.BasicVoid;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

public class LanguageLoad {
    public static void load(String language) {
        Yaml yaml = new Yaml();
        FileInputStream input;
        String languageFile = BasicInfo.getDataFolder +"/languages/"+language+".yml";
        try {
            input = new FileInputStream(languageFile);
        } catch (FileNotFoundException e) {
            BasicVoid.sendException(e);
            BasicVoid.sendWarn("#lang#Language_Unknown");
            return;
        }
        LanguageGet.languageMap = yaml.loadAs(input, Map.class);
        BasicVoid.sendInfo("#lang#Language_Changed");
    }
}
