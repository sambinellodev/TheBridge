package br.com.stenoxz.thebridge.utils;

public class ProgressBar {

    public static String progressBar(double current, int max, int totalBars, String symbol, String completedColor, String notCompletedColor) {
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);
        int leftOver = (totalBars - progressBars);
        StringBuilder sb = new StringBuilder();

        sb.append(completedColor);

        for (int i = 0; i < progressBars; i++) {
            sb.append(symbol);
        }

        sb.append(notCompletedColor);

        for (int i = 0; i < leftOver; i++) {
            sb.append(symbol);
        }
        return sb.toString();
    }
}
