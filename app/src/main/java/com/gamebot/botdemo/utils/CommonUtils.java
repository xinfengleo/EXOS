package com.gamebot.botdemo.utils;

import android.content.Context;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2018/9/3.
 */

public class CommonUtils {
    /**
     * convert px to its equivalent dp
     *
     * 将px转换为与之相等的dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale =  context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * convert dp to its equivalent px
     *
     * 将dp转换为与之相等的px
     */
    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    /**
     * convert px to its equivalent sp
     *
     * 将px转换为sp
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }


    /**
     * convert sp to its equivalent px
     *
     * 将sp转换为px
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    public static Integer getInteger(JsonObject json, String key){
        return CommonUtils.getInteger(json,key,null);
    }

    public static Integer getInteger(JsonObject json, String key, Integer defaultVal){
        if(json!=null){
            JsonElement obj=json.get(key);
            if(obj!=null){
                return obj.getAsInt();
            }
        }
        return defaultVal;
    }

    public static Long getLong(JsonObject json, String key){
        return CommonUtils.getLong(json,key,null);
    }

    public static Long getLong(JsonObject json, String key, Long defaultVal){
        if(json!=null){
            JsonElement obj=json.get(key);
            if(obj!=null){
                return obj.getAsLong();
            }
        }
        return defaultVal;
    }

    public static Integer getInteger(Integer i, Integer defaultVal){
        if(i==null){
            return defaultVal;
        }
        return i;
    }

    public static Integer getIntegerToPx(Context context,Integer i,Integer defaultVal){
        if(i==null){
            return CommonUtils.dp2px(context,defaultVal);
        }
        return CommonUtils.dp2px(context,i);
    }

    public static String getString(JsonObject json, String key){
        return CommonUtils.getString(json,key,null);
    }

    public static String getString(JsonObject json, String key, String defaultVal){
        if(json!=null){
            JsonElement obj=json.get(key);
            if(obj!=null){
                return obj.getAsString();
            }
        }
        return defaultVal;
    }
    public static String getWeekStr() {
        return CommonUtils.getWeekOfDateStr(new Date());
    }

    public static int getWeekInt() {
        return CommonUtils.getWeekOfDateInt(new Date());
    }

    public static String getWeekOfDateStr(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    public static int getWeekOfDateInt(Date dt) {
        int[] weekDays = {0, 1, 2, 3, 4, 5, 6};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    public static int getHourInt() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    public static int getMinuteInt() {
        Calendar cal = Calendar.getInstance();
        int minute = cal.get(Calendar.MINUTE);
        return minute;
    }
    public static int getSecondInt() {
        Calendar cal = Calendar.getInstance();
        int second = cal.get(Calendar.SECOND);
        return second;
    }
}
