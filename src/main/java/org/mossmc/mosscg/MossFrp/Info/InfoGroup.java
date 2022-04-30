package org.mossmc.mosscg.MossFrp.Info;

import org.mossmc.mosscg.MossFrp.BasicInfo;

import static org.mossmc.mosscg.MossFrp.BasicVoid.*;

public class InfoGroup {
    public static void sendStartGroup() {
        sendInfo(getLanguage("Start_Welcome").replace("[version]", BasicInfo.getVersion));
        sendInfo("#lang#Start_Copyright");
        sendInfo(getLanguage("Start_SystemFull").replace("[system]",BasicInfo.getSystemName));
        sendInfo(getLanguage("Start_SystemRead").replace("[system]",BasicInfo.getSystemType.name()));
    }

    public static void sendEndGroup() {
        sendInfo("#lang#Exit_Start");
        sendInfo("#lang#Exit_Finished");
    }
}
