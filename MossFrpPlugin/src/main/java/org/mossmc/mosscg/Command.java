package org.mossmc.mosscg;

import org.bukkit.command.CommandSender;

import static org.mossmc.mosscg.MossFrp.*;

public class Command {
    //无情的指令读取工具方法
    //说白就是分割指令然后读取
    public static Boolean readCommand(String[] command, CommandSender sender) {
        if (command == null) {
            return true;
        }
        //依据空格分割指令
        String part1 = "";
        String part2 = "";
        String part3 = "";
        String part4 = "";
        String part5 = "";
        for (int i = 0; i < command.length; i++) {
            switch (i) {
                case 0:
                    part1 = command[i];
                    break;
                case 1:
                    part2 = command[i];
                    break;
                case 2:
                    part3 = command[i];
                    break;
                case 3:
                    part4 = command[i];
                    break;
                case 4:
                    part5 = command[i];
                    break;
                default:
                    break;
            }
        }
        //读取指令部分
        //空指令判断
        if (command.length < 1) {
            sendInfo(getLanguage("Command_Help_Plugin"),sender);
            return false;
        }
        switch (part1) {
            //帮助指令
            case "help":
                if (!sender.hasPermission("mossfrp.help") && !sender.isOp()) {
                    sendInfo(getLanguage("Permission_Reject"),sender);
                    return false;
                }
                sendInfo(getLanguage("Command_Help_Plugin"),sender);
                return true;
            //语言指令
            case "lang":
                if (!sender.hasPermission("mossfrp.lang") && !sender.isOp()) {
                    sendInfo(getLanguage("Permission_Reject"),sender);
                    return false;
                }
                if (!part2.equals("")) {
                    loadLanguage(part2,sender);
                    return true;
                }
                sendInfo(getLanguage("Command_HelpLangMain_Plugin"),sender);
                return false;
            //列表指令
            case "list":
                if (!sender.hasPermission("mossfrp.list") && !sender.isOp()) {
                    sendInfo(getLanguage("Permission_Reject"),sender);
                    return false;
                }
                for (String name : FrpManager.frpStatusMap.keySet()) {
                    sendInfo(getLanguage("List_Send").replace("[tunnelName]",name).replace("[tunnelStatus]",FrpManager.frpStatusMap.get(name).toString()),sender);
                }
                return true;
            //隧道相关指令
            case "tunnel":
                if (!sender.hasPermission("mossfrp.tunnel") && !sender.isOp()) {
                    sendInfo(getLanguage("Permission_Reject"),sender);
                    return false;
                }
                if (part2.equals("run")) {
                    if (!part3.equals("")) {
                        FileManager.loadSaveTunnel(part3,sender);
                        return true;
                    }
                    sendInfo(getLanguage("Command_HelpTunnelRun_Plugin"),sender);
                    return false;
                }
                if (part2.equals("new")) {
                    if (!part3.equals("")) {
                        if (FrpManager.frpStatusMap.containsKey(part3)) {
                            sendInfo(getLanguage("Command_TunnelAlreadyExist"),sender);
                            return false;
                        }
                        FileManager.writeEmptySaveTunnel(part3,part4,sender);
                        sendInfo(getLanguage("Command_HelpTunnelFile"),sender);
                        //sendInfo(getLanguage("CodeGuide_Start"),sender);
                        //Code.codeGuide(part4,part3,sender);
                        return true;
                    }
                    sendInfo(getLanguage("Command_HelpTunnelNew_Plugin"),sender);
                    return false;
                }
                if (part2.equals("remove")) {
                    if (!part3.equals("")) {
                        if (FrpManager.frpStatusMap.containsKey(part3)) {
                            FrpManager.stopFrp(part3);
                            sendInfo(getLanguage("Command_TunnelStopping"),sender);
                            return true;
                        }
                        sendInfo(getLanguage("Command_TunnelNotExist_Plugin"),sender);
                        return false;
                    }
                    sendInfo(getLanguage("Command_HelpTunnelRemove_Plugin"),sender);
                    return false;
                }
                sendInfo(getLanguage("Command_HelpTunnelMain_Plugin"),sender);
                return false;
            //未知指令判断
            default:
                sendInfo(getLanguage("Command_Unknown"),sender);
                return false;
        }
    }
}
