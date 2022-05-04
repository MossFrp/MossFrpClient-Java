package org.mossmc.mosscg.MossFrp;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.mossmc.mosscg.MossFrp.Command.CommandForge;

import java.io.File;

@Mod(
        modid = "mossfrp",
        name = BasicInfo.getName,
        version = BasicInfo.getVersion
)
public class PathForge {
    public static Logger logger;
    public static EntityPlayer player;

    public static Logger getLogger() {
        return logger;
    }

    @Mod.EventHandler
    public void preLoad(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        BasicInfo.getForgeInstance = this;
        String configPath = event.getModConfigurationDirectory().toPath()+"/MossFrp";
        BasicInfo.getDataFolder = new File(configPath).toPath();
        BasicInfo.getRunMode = BasicInfo.runMode.forge;
        MinecraftForge.EVENT_BUS.register(CommandForge.class);
        StartGuide.runGuide(null);
    }

    //这是forge支持 但是还有bug
    //信息发送不出去 等待有缘人来修修
    public static void sendPlayer(String info) {
        if (player == null) {return;}
        try {
            ITextComponent component = new TextComponentBase() {
                @Override
                public @NotNull String getUnformattedComponentText() {
                    return info;
                }

                @Override
                public @NotNull ITextComponent createCopy() {
                    return this;
                }
            };
            //FMLClientHandler.instance().getClient().ingameGUI.addChatMessage(ChatType.GAME_INFO,component);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
