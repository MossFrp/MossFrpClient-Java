package org.mossmc.mosscg.MossFrp.Command;

public class CommandFabric {
    @SuppressWarnings("unused")
    public static void commandFabric(boolean backend,String[] args) {
        CommandRead.read(args);
    }
}
