package com.gamebot.botdemo.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by Administrator on 2018/10/25.
 */

public class Utils {
    public static Integer objToInt(Object obj) {
        return Integer.valueOf(obj.toString().replace(".0", ""));
    }


    public static int pointXConver(int y) {
        return 719 - y;
    }

    public static Point pointConver(int x, int y) {
        return new Point(719 - y,x);
    }

    public static String pointColorsConver(String str,boolean converXY) {
        String[] strp1 = StringUtils.split(str, ",");
        StringBuffer result = new StringBuffer();
        boolean relative=StringUtils.contains(str,"0|0|");
        for (String str1 : strp1) {
            if (StringUtils.contains(str1, "|")) {
                String[] strp2 = StringUtils.split(str1, "|");
                if (StringUtils.isNotEmpty(result)) {
                    result.append(",");
                }
                if(converXY){
                    if(relative){
                        int x = -Integer.valueOf(strp2[1]);
                        result.append(String.format("%d|%s|%s", x, strp2[0], strp2[2]));
                    }else{
                        int x = pointXConver(Integer.valueOf(strp2[1]));
                        result.append(String.format("%d|%s|%s", x, strp2[0], strp2[2]));
                    }
                }else{
                    result.append(String.format("%s|%s|%s", strp2[0], strp2[1], strp2[2]));
                }
            }
        }
        return result.toString();
    }

    public static void colorsConver(List<String[]> list) {
        for (String[] strs : list) {
            strs[1] = pointColorsConver(strs[1],true);
        }
    }

    public static int getTime(){
        return (int)(System.currentTimeMillis()/1000);
    }

    public static Integer getInt(Integer v1,Integer v2){
        if(v1==null){
            return v2;
        }
        return v1;
    }

    public static int getLocalVersion(Context ctx) {
        int localVersion = 0;
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionCode;
            //LogUtil.d("TAG", "本软件的版本号。。" + localVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    /**
     * 获取本地软件版本号名称
     */
    public static String getLocalVersionName(Context ctx) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;
            //LogUtil.d("TAG", "本软件的版本号。。" + localVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }
}
