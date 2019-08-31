package com.gamebot.botdemo;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.gamebot.botdemo.utils.ScreenMetrics;
import com.gamebot.sdk.GameBotApp;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;


public class ApplicationEx extends GameBotApp {
    @Override
    public void onCreate() {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process: manager.getRunningAppProcesses()) {
            if(process.pid == pid)
            {
                processName = process.processName;
            }
        }

        Log.e("TAG","application start,processName:"+processName);
        super.onCreate();
        ScreenMetrics.initIfNeeded(getApplicationContext());
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "fdc07a1fb195ad6835a1bf6f31deb66c");
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }
}
