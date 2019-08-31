package com.gamebot.botdemo.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

/**
 * Created by dzy on 2017/4/26.
 */

public class ScreenMetrics {

    private static int screenHeight;
    private static int screenWidth;
    private static int deviceScreenDensity;
    private static int deviceScreenHeight;
    private static int deviceScreenWidth;
    private static int ori;
    private static boolean initialized = false;
    private static int actionBarHeight;

    public static void initIfNeeded(Context context) {
        if (initialized)
            return;
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration mConfiguration = resources.getConfiguration(); //获取设置的配置信息
        ori = mConfiguration.orientation; //获取屏幕方向
        deviceScreenDensity = dm.densityDpi;
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        DisplayMetrics metrics =new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getRealMetrics(metrics);
        deviceScreenWidth = metrics.widthPixels;
        deviceScreenHeight = metrics.heightPixels;

        setActionBarHeight(context,dm);
        initialized = true;
    }

    public static int getActionBarHeight() {
        return actionBarHeight;
    }

    /**
     * 获取虚拟按钮ActionBar的高度
     */
    private static void setActionBarHeight(Context context, DisplayMetrics dm) {
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight=TypedValue.complexToDimensionPixelSize(tv.data, dm);
        }else{
            actionBarHeight=0;
        }
    }

    public static int getOri() {
        return ori;
    }

    public static void setOri(int ori) {
        ScreenMetrics.ori = ori;
    }

    public static int getDeviceScreenHeight() {
        return deviceScreenHeight;
    }

    public static int getDeviceScreenWidth() {
        return deviceScreenWidth;
    }

    public static int getDeviceScreenDensity() {
        return deviceScreenDensity;
    }

    public static int getScreenWidth() {
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            if(getDeviceScreenWidth()>getDeviceScreenHeight())
            {
                return screenWidth;
            }else{
                return screenHeight;
            }
        } else {
            if(getDeviceScreenWidth()<getDeviceScreenHeight())
            {
                return screenWidth;
            }else{
                return screenHeight;
            }
        }
    }

    public static int getScreenHeight() {
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            if(getDeviceScreenWidth()<getDeviceScreenHeight())
            {
                return screenWidth;
            }else{
                return screenHeight;
            }
        } else {
            if(getDeviceScreenWidth()>getDeviceScreenHeight())
            {
                return screenWidth;
            }else{
                return screenHeight;
            }
        }
    }

    public static int scaleX(int x, int width) {
        if (width == 0 || !initialized)
            return x;
        return x * screenWidth / width;
    }

    public static int scaleY(int y, int height) {
        if (height == 0 || !initialized)
            return y;
        return y * screenHeight / height;
    }

    public static int rescaleX(int x, int width) {
        if (width == 0 || !initialized)
            return x;
        return x * width / screenWidth;
    }

    public static int rescaleY(int y, int height) {
        if (height == 0 || !initialized)
            return y;
        return y * height / screenHeight;
    }


    private int mDesignWidth;
    private int mDesignHeight;

    public ScreenMetrics(int designWidth, int designHeight) {
        mDesignWidth = designWidth;
        mDesignHeight = designHeight;
    }

    public ScreenMetrics() {
    }

    public void setDesignWidth(int designWidth) {
        mDesignWidth = designWidth;
    }

    public void setDesignHeight(int designHeight) {
        mDesignHeight = designHeight;
    }

    public int scaleX(int x) {
        return scaleX(x, mDesignWidth);
    }

    public int scaleY(int y) {
        return scaleY(y, mDesignHeight);
    }


    public void setScreenMetrics(int width, int height) {
        mDesignWidth = width;
        mDesignHeight = height;
    }

    public int rescaleX(int x) {
        return rescaleX(x, mDesignWidth);
    }


    public int rescaleY(int y) {
        return rescaleY(y, mDesignHeight);
    }
}
