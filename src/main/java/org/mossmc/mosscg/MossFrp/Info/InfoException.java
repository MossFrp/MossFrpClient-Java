package org.mossmc.mosscg.MossFrp.Info;

import java.io.PrintWriter;
import java.io.StringWriter;

public class InfoException {
    public static void exception(Exception e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter= new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        InfoSender.send(stringWriter.toString(),"exception");
        try {
            printWriter.close();
            stringWriter.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
