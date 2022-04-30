package org.mossmc.mosscg.MossFrp.Time;

import java.util.Calendar;

public class TimeDate {
    //输出示例：2021-10-30-21-6-55
    public static String getNowTimeFull() {
        Calendar rightNow = Calendar.getInstance();
        int year = rightNow.get(Calendar.YEAR);
        int month = rightNow.get(Calendar.MONTH)+1;
        int day = rightNow.get(Calendar.DAY_OF_MONTH);
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        int minute = rightNow.get(Calendar.MINUTE);
        int second = rightNow.get(Calendar.SECOND);
        return year + "-" + month + "-" + day + "-" + hour + "-" + minute + "-" + second;
    }

    //输出示例：2021-10-30-21-6-55
    public static String getNowTime() {
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        int minute = rightNow.get(Calendar.MINUTE);
        int second = rightNow.get(Calendar.SECOND);
        return "[" + hour + ":" + minute + ":" + second + "]";
    }

    //输出示例：2021-10-30-21-6-55
    public static String getNowDate() {
        Calendar rightNow = Calendar.getInstance();
        int year = rightNow.get(Calendar.YEAR);
        int month = rightNow.get(Calendar.MONTH)+1;
        int day = rightNow.get(Calendar.DAY_OF_MONTH);
        return "[" + year + "-" + month + "-" + day + "]";
    }
}
