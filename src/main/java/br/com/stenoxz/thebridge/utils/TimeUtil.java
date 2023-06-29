package br.com.stenoxz.thebridge.utils;

public class TimeUtil {

    public static String toTime(int time) {
        int m = time / 60;
        int s = time % 60;

        if (s < 10){
            return m + ":0" + s;
        } else {
            return m + ":" + s + "";
        }
    }
}
