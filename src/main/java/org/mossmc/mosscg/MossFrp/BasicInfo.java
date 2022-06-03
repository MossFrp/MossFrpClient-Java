package org.mossmc.mosscg.MossFrp;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.mossmc.mosscg.MossFrp.BasicVoid.getConfig;
import static org.mossmc.mosscg.MossFrp.BasicVoid.getLanguage;

public class BasicInfo {
    public enum runMode{
        plugin,standard,bungee,velocity,forge,fabric
    }

    public enum systemType{
        linux,windows
    }

    public enum frpStatus {
        starting,online,offline
    }

    public static runMode getRunMode;
    public static systemType getSystemType;

    public static PathPlugin getPluginInstance;
    public static PathBungee getBungeeInstance;
    public static PathVelocity getVelocityInstance;
    public static PathForge getForgeInstance;
    public static PathFabric getFabricInstance;

    public static Path getDataFolder;

    public static final String getVersion = "V1.5.1.1.1213 Beta";
    public static final String getAuthor = "MossCG";
    public static final String getName = "MossFrp";

    public static String getDomainsAPIAddress = "https://www.mossfrp.top/api/domains.json";
    public static String getCheckUpdateAddress = "https://www.mossfrp.top/api/update.json";
    public static String getCheckNoticeAddress = "https://www.mossfrp.top/api/notice.json";

    public static String getSystemName;

    public static List<String> listDomains = new ArrayList<>();

    public static String getInfoPrefix(String type) {
        switch (type) {
            case "info":
                return getLanguage("Prefix_Info");
            case "warn":
                return getLanguage("Prefix_Warn");
            case "error":
                return getLanguage("Prefix_Error");
            case "exception":
                return getLanguage("Prefix_Exception");
            case "debug":
                return getLanguage("Prefix_Debug");
            case "command":
                return getLanguage("Prefix_Command");
            default:
                return getLanguage("Prefix_Unknown");
        }
    }

    public static String getFrpStatusName(frpStatus status) {
        switch (status) {
            case online:
                return getLanguage("List_Online");
            case offline:
                return getLanguage("List_Offline");
            case starting:
                return getLanguage("List_Starting");
            default:
                return null;
        }
    }

    public static void loadSystemType() {
        getSystemName = System.getProperty("os.name");
        if (getConfig("systemType").equals("auto")) {
            if (getSystemName.toLowerCase().startsWith("windows")) {
                getSystemType = systemType.windows;
                return;
            }
            if (getSystemName.toLowerCase().startsWith("linux")) {
                getSystemType = systemType.linux;
                return;
            }
        }
        if (getConfig("systemType").equals("windows")) {
            getSystemType = systemType.windows;
            return;
        }
        if (getConfig("systemType").equals("linux")) {
            getSystemType = systemType.linux;
            return;
        }
        getSystemType = systemType.windows;
    }
}
