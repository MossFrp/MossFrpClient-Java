package org.mossmc.mosscg.MossFrp.Command;

import org.mossmc.mosscg.MossFrp.BasicVoid;
import org.mossmc.mosscg.MossFrp.Code.CodeDecode;
import org.mossmc.mosscg.MossFrp.Config.ConfigCodeLoad;
import org.mossmc.mosscg.MossFrp.Config.ConfigCodeSave;
import org.mossmc.mosscg.MossFrp.Config.ConfigLoad;
import org.mossmc.mosscg.MossFrp.FrpControl.FrpCache;
import org.mossmc.mosscg.MossFrp.FrpControl.FrpStop;
import org.mossmc.mosscg.MossFrp.Language.LanguageLoad;

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
            default:
                sendWarn("#lang#Command_Unknown");
                break;
        }
    }
}
