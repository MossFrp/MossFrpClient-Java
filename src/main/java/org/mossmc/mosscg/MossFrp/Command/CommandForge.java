package org.mossmc.mosscg.MossFrp.Command;

import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.mossmc.mosscg.MossFrp.PathForge;

@Mod.EventBusSubscriber(modid = "mossfrp")
public class CommandForge {
    @SubscribeEvent
    public static void chatCommand(ClientChatEvent event) {
        if (event.getOriginalMessage().startsWith("/mossfrp")) {
            String[] cut = event.getOriginalMessage().replace("/mossfrp ","").split("\\s+");
            CommandRead.read(cut);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        PathForge.player = event.player;
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        PathForge.player = null;
    }
}
