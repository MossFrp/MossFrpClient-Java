package org.mossmc.mosscg.MossFrp.Code;

public class CodeAutoHide {
    public static String autoHide(String input) {
        int length = input.length();
        StringBuilder builder = new StringBuilder();
        if (length <= 6) {
            int i = 0;
            while (i<length) {
                i++;
                builder.append("*");
            }
        } else {
            builder.append(input, 0, 2);
            length = length - 4;
            int i = 0;
            while (i<length) {
                i++;
                builder.append("*");
            }
            builder.append(input.substring(input.length()-2));
        }
        return builder.toString();
    }
}
