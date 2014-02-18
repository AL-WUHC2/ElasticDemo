package org.n3r.elastic.utils;

import java.util.Date;

public class TimeLagUtils {

    private static int[] units = { 1000, 60, 60, 24 };

    private static String[] unitNames = { "ms", "s", "m", "h" };

    public static String formatLagBetween(Date begin, Date end) {
        long lag = end.getTime() - begin.getTime();
        if (lag == 0L) return "0ms";

        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < units.length; i++) {
            sb.insert(0, unitNames[i]).insert(0, lag % units[i]);
            lag /= units[i];
            if (lag == 0) break;
            else sb.insert(0, " ");
        }
        if (lag != 0) sb.insert(0, "d").insert(0, lag);

        return sb.toString();
    }

}
