package com.tanjinc.omgvideoplayer.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by tanjinc on 16-8-19.
 */
public class ScreenUtils {
    private static final String TAG = "ScreenUtils";

    public static final int MAX_SYS_BRIGHTNESS = 255;

    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public static void setWinBrightness(Window win, int brightness) {
        if (win != null) {
            if (brightness > MAX_SYS_BRIGHTNESS) {
                brightness = MAX_SYS_BRIGHTNESS;
            }
            if (brightness < 0) {
                brightness = 0;
            }
            float brightnessRatio = brightness / (float) MAX_SYS_BRIGHTNESS;
            WindowManager.LayoutParams winParams = win.getAttributes();
            winParams.screenBrightness = brightnessRatio;
            win.setAttributes(winParams);
        }
    }

    public static int getCurScreenBrightness(Context context) {
        int value = 0;
        ContentResolver cr = context.getContentResolver();
        try {
            value = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {

        }
        return value;
    }

    public static int getMaxScreenBrightness() {
        final Resources res = Resources.getSystem();
        int id = res.getIdentifier("config_screenBrightnessSettingMaximum", "integer", "android");// API17+
        if (id != 0) {
            try {
                return res.getInteger(id);
            } catch (Exception e) {
                Log.e(TAG, "video getMaxScreenBrightness fail " + e);
            }
        }
        return 255;
    }

    public static int getMinScreenBrightness() {
        final Resources res = Resources.getSystem();
        int id = res.getIdentifier("config_screenBrightnessSettingMinimum", "integer", "android");// API17+
        if (id == 0) {
            id = res.getIdentifier("config_screenBrightnessDim", "integer", "android"); // lowerAPI
        }
        if (id != 0) {
            try {
                return res.getInteger(id);
            } catch (Exception e) {
                Log.e(TAG, "video getMinScreenBrightness fail " + e);
            }
        }
        return 0;
    }
}
