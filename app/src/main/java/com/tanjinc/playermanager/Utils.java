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
}
