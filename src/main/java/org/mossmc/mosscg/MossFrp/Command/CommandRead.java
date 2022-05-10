package org.mossmc.mosscg.MossFrp.Command;

import org.mossmc.mosscg.MossFrp.BasicInfo;
import org.mossmc.mosscg.MossFrp.BasicVoid;
import org.mossmc.mosscg.MossFrp.Code.CodeDecode;
import org.mossmc.mosscg.MossFrp.Config.ConfigCodeSave;
import org.mossmc.mosscg.MossFrp.FileControl.FileCollect;
import org.mossmc.mosscg.MossFrp.FrpControl.FrpCache;
import org.mossmc.mosscg.MossFrp.Language.LanguageLoad;
import org.mossmc.mosscg.MossFrpForgeSuport.MossFrpForgeSupport;

import java.util.Arrays;

import static org.mossmc.mosscg.MossFrp.BasicVoid.*;

public class CommandRead {
    public static void read(String[] args) {
        sendCommand(Arrays.toString(args));
        if (args.length == 0) {
            sendWarn("#lang#Command_Unknown");
            return;
        }
        switch (args[0]) {
            case "exit":
                if (BasicInfo.getRunMode.equals(BasicInfo.runMode.forge)) {
                    break;
                    //MossFrpForgeSupport.forgeExit();
                    //不会有人会用/mossfrp exit来退出游戏吧
                }
                System.exit(0);
                break;
            case "help":
                sendInfo("#lang#Command_Help");
                break;
            case "save":
                if (args.length <= 2) {
                    sendWarn("#lang#Command_HelpSave");
                    return;
                }
                if (isContainChinese(args[1])) {
                    sendWarn("#lang#Command_NameChinese");
                    return;
                }
                boolean decode = CodeDecode.decode(args[2]);
                if (!decode) {
                    return;
                }
                ConfigCodeSave.saveCodeConfig(args[2],args[1]);
                break;
            case "lang":
                if (args.length <= 1) {
                    sendWarn("#lang#Command_HelpLang");
                    return;
                }
                LanguageLoad.load(args[1]);
                break;
            case "list":
                sendInfo(FrpCache.getFrpListString());
                break;
            case "reload":
                BasicVoid.reloadMossFrp();
                break;
            case "report":
                FileCollect.createCollect();
                break;
            default:
                sendWarn("#lang#Command_Unknown");
                break;
        }
    }
}
