package org.mossmc.mosscg.MossFrp.Config;

import org.mossmc.mosscg.MossFrp.BasicInfo;
import org.mossmc.mosscg.MossFrp.BasicVoid;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

public class ConfigLoad {
    public static void load() {
        Yaml yaml = new Yaml();
        FileInputStream input = null;
        String configFile = BasicInfo.getDataFolder +"/config.yml";
        try {
            input = new FileInputStream(configFile);
        } catch (FileNotFoundException e) {
            BasicVoid.sendException(e);
        }
        ConfigGet.configMap = yaml.loadAs(input, Map.class);
    }
}
