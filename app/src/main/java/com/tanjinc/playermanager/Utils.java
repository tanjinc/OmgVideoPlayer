package com.tanjinc.playermanager;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

/**
 * Created by tanjincheng on 17/7/2.
 */
public class Utils {
    /**
     * Get activity from context object
     *
     * @param context context
     * @return object of Activity or null if it is not Activity
     */
    public static Activity scanForActivity(Context context) {
        if (context == null) return null;

        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return scanForActivity(((ContextWrapper) context).getBaseContext());
        }

        return null;
    }

    public static String stringForTime(int millis) {
        int totalSeconds = millis / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        String textString = null;
        if (hours > 0) {
            textString = String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            textString = String.format("%02d:%02d", minutes, seconds);
        }
        return textString;

    }
}
