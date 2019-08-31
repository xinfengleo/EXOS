package com.gamebot.botdemo.service;

import android.app.Notification;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fauth.library.core.AuthService;
import com.fauth.library.entity.AuthResult;
import com.gamebot.botdemo.R;
import com.gamebot.botdemo.entity.MsgEvent;
import com.gamebot.botdemo.script.ScriptThread;
import com.gamebot.botdemo.utils.ConsoleHelper;
import com.gamebot.botdemo.utils.FileUtils;
import com.gamebot.botdemo.utils.HUDManage;
import com.gamebot.botdemo.view.SpinnerExd;
import com.gamebot.sdk.client.BaseScriptThread;
import com.gamebot.sdk.preference.SettingPreference;
import com.gamebot.sdk.service.BaseFloatingViewService;
import com.gamebot.sdk.service.ViewStyle;
import com.gamebot.sdk.service.ViewTheme;
import com.gamebot.sdk.view.BotControl;
import com.gamebot.sdk.view.CheckBoxEx;
import com.gamebot.sdk.view.EditTextEx;
import com.github.johnpersano.supertoasts.library.SuperToast;
import com.google.gson.Gson;
import com.topjohnwu.superuser.Shell;
import com.umeng.analytics.MobclickAgent;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Map;


public class FloatingViewService extends BaseFloatingViewService implements BaseScriptThread.ScriptServiceListener,View.OnClickListener{

    private static final String AUTH_BASE_URL = "http://wmsj.ai.igcps.com";
    public static String sKey="1234567890123456";
    private static String ivParameter="fcfaccbadbcaddaf";
    private static final int HEARTBEAT_TIME = 900;
    private ScriptThread _scriptThread;
    private HUDManage hudManage;
    private ConsoleHelper consoleHelper;
    private ImageView pauseView;
    private LinearLayout settingWindow;
    private SharedPreferences sharedPreferences;
    private final static String CONFIG_PATH="/sdcard/wmsj";
    private AuthService authService;
    private String 使用卡號 = "";
    private FloatingViewService self;
    private View tap1View,tab2View,tap3View,tabMainView;

    @Override
    protected void createSettingView(LayoutInflater layoutInflater) {
        tabMainView = layoutInflater.inflate(R.layout.setting_main, null);
        tap1View = layoutInflater.inflate(R.layout.setting_tab_one, null);
        tab2View = layoutInflater.inflate(R.layout.setting_tab_two, null);
//        tap3View = layoutInflater.inflate(R.layout.setting_tab4, null);
        addView("使用說明", tabMainView);
        addView("刷初始", tap1View);
        addView("日常", tab2View);
//        addView("全局設定", tap3View);
//        tap1View.findViewById(R.id.btn_new_setting).setOnClickListener(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        self=this;
        MobclickAgent.onResume(this);
        hudManage=HUDManage.getInstance();
        authService= AuthService.getInstance();
        authService.init(getApplicationContext(),"","",AUTH_BASE_URL,900,"0619",sKey, ivParameter);
        sharedPreferences= getApplicationContext().getSharedPreferences("PREF_SETTING", 0);

        Notification notification=new NotificationCompat.Builder(this,"mzbot")
                .setContentText("夢幻模擬戰助手")
                .setContentText("夢幻模擬戰助手運行中")
                .setSmallIcon(R.mipmap.ic_launcher).build();
        int NOTIFICATION_ID=14545;
        startForeground(NOTIFICATION_ID,notification);
//        AuthService  authService = AuthService.getInstance();
//        initConsole();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_new_setting:
                refreshConfig();
                break;
//            case R.id.btn_daily_setting:
//
//                break;
            default:
                break;
        }
    }

    private  Map<String,?> configMap;
    private void refreshConfig(){
        String jsonStr = FileUtils.readTextFromAESRaw(this, R.raw.newwmsj);
        configMap=new Gson().fromJson(jsonStr,Map.class);
        setControlConfig(getSettingWindow());
    }

    private void setControlConfig(ViewGroup viewGroup){
        int count = viewGroup.getChildCount();
        for(int i = 0; i < count; ++i) {
            View view = viewGroup.getChildAt(i);
            if(view instanceof BotControl) {
                String key=((BotControl) view).getUniqueKey();
                Object val=configMap.get(key);
                if(StringUtils.isNotEmpty(key) && val!=null){
                    if(view instanceof SpinnerExd){
                        SpinnerExd spinnerExd=((SpinnerExd) view);
                        spinnerExd.setSelection(val);
                    }
                    if(view instanceof EditTextEx){
                        ((EditTextEx) view).setText((String)val);
                    }
                    if(view instanceof CheckBoxEx){
                        ((CheckBoxEx) view).setChecked((Boolean) val);
                    }
                }
            } else if(view instanceof ViewGroup) {
                setControlConfig((ViewGroup) view);
            }
        }
    }

    private void startHeartbeat(){
        authService.startHeartbeat(new AuthService.AuthHeartbeatCallBack() {
            @Override
            public void onExpire(String s, AuthResult authResult, int i) {
                onScriptStop();

                if (i > HEARTBEAT_TIME) {
                    showMsgBox(getString(R.string.app_name), "授權驗證失效！");
                } else {
                    if (StringUtils.isEmpty(s)) {
                        showMsgBox(getString(R.string.app_name), "體驗时间到期");
                    } else {
                        showMsgBox(getString(R.string.app_name), "使用期限到期");
                    }
                }
//                        Intent dialogIntent = newwmsj Intent(getBaseContext(), MainActivity.class);
//                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        getApplication().startActivity(dialogIntent);
            }

            @Override
            public void onPoll(long l) {
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
//                        if(min<60){
                        hudManage.showHUD("careEndDate", "腳本剩餘時長:" + String.valueOf(l) + "分鐘", 13, 200, 20, 1079, 649, "#ffffff", "#80000000", 0);
//                        }else{
//                            hudManage.hideHUD("careEndDate");
//                        }
                    }
                });
            }

            @Override
            public void plusAuthNum() {

            }
        });
    }
//        AuthService authService= AuthService.getInstance();
//        authService.startHeartbeat(cardNum,new AuthService.AuthHeartbeatCallBack() {
//            @Override
//            public void onExpire(String crad, AuthResult result,int t) {
//                new Handler(getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        onScriptStop();
//
//                        if(t > AuthService.HEARTBEAT_TIME){
//                            showMsgBox(getString(R.string.app_name), "授權驗證失效！");
//                        }else{
//                            if (StringUtils.isEmpty(crad)) {
//                                showMsgBox(getString(R.string.app_name), "體驗时间到期");
//                            } else {
//                                showMsgBox(getString(R.string.app_name), "使用期限到期");
//                            }
//                        }
//
////                        Intent dialogIntent = newwmsj Intent(getBaseContext(), MainActivity.class);
////                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                        getApplication().startActivity(dialogIntent);
//                    }
//                });
//            }
//
//            @Override
//            public void onPoll(long min) {
//                new Handler(getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
////                        if(min<60){
//                            hudManage.showHUD("careEndDate","腳本剩餘時長:"+String.valueOf(min)+"分鐘",13,200,20,1079,649,"#ffffff","#80000000",0);
////                        }else{
////                            hudManage.hideHUD("careEndDate");
////                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onEnd(AuthResult result) {
//                new Handler(getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        stopScript();
//                        if(result.getStatus() == 2001){
//                            showMsgBox(getString(R.string.app_name), "版本號錯誤！");
//                        }else if(result.getStatus() == 2002){
//                            showMsgBox(getString(R.string.app_name), "免費登入超過次數！");
//                        }else if(result.getStatus() == 2003){
//                            showMsgBox(getString(R.string.app_name), "卡號不存在！");
//                        }else if(result.getStatus() == 2004){
//                            showMsgBox(getString(R.string.app_name), "請輸入密碼！");
//                        }else if(result.getStatus() == 2005){
//                            showMsgBox(getString(R.string.app_name), "卡號過期！");
//                        }else if(result.getStatus() == 2006){
//                            showMsgBox(getString(R.string.app_name), "已經在其他設備登入！");
//                        }else if(result.getStatus() == 2007){
//                            showMsgBox(getString(R.string.app_name), "當前卡號已達上限值！");
//                        }else if(result.getStatus() == 2008){
//                            showMsgBox(getString(R.string.app_name), "啟動碼重複登入！");
//                        }else if(result.getStatus() == 2009){
//                            showMsgBox(getString(R.string.app_name), "密碼錯誤！");
//                        }else if(result.getStatus() == 2010){
//                            showMsgBox(getString(R.string.app_name), "簽名驗證失敗！");
//                        }else if(result.getStatus() == 2011){
//                            showMsgBox(getString(R.string.app_name), "參數錯誤！");
//                        }else if(result.getStatus() == 2012){
//                            showMsgBox(getString(R.string.app_name), "token驗證失敗！");
//                        }else if(result.getStatus() == 2013){
//                            showMsgBox(getString(R.string.app_name), "設備信息與token不符！");
//                        }else if(result.getStatus() == 2014){
//                            showMsgBox(getString(R.string.app_name), "卡號還未激活！");
//                        }else if(result.getStatus() == 2015){
//                            showMsgBox(getString(R.string.app_name), "已達單日免費次數上限！");
//                        }else if(result.getStatus() == 2016){
//                            showMsgBox(getString(R.string.app_name), "免費體驗模式已關閉！");
//                        }else if(result.getStatus() == 2017){
//                            showMsgBox(getString(R.string.app_name), "解碼失敗");
//                        }else{
//                            showMsgBox(getString(R.string.app_name),"伺服器連接失敗" + "\nMsg:" + result.getMsg() + "\ntoken:" + result.getToken() + "\nstatus:" + result.getStatus() + "\ncode:" + result.getCode() + "\nendTime:" + result.getEndtime() + "\nremainingtimes:" + result.getRemainingtimes() + "\nserverTime:" + result.getServertime());
//                        }
//                    }
//                });
//            }
//        });
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MobclickAgent.onPause(this);
        stopForeground(true);
    }

    @Override
    protected void onScriptStart() {
        使用卡號 = "";
        使用卡號 = SettingPreference.getString("使用卡號","").replace(" ","");
        Shell.Sync.su();
        if (使用卡號.equals("")){
            start("");
        }else{
            start(使用卡號);
        }
    }

    private void start(String cardNum){
        authService.sendAuth(cardNum, new AuthService.AuthCallBack() {
            @Override
            public void onResponse(AuthResult result) {
                new Handler(getApplicationContext().getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(!AuthService.getInstance().isAuth()){
                            showMsgBox(getString(R.string.app_name), "使用期限到期");
                            //            Intent dialogIntent = newwmsj Intent(getBaseContext(), MainActivity.class);
                            //            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            //            getApplication().startActivity(dialogIntent);
                            stopScript();
                        }else{
                            SuperToast st=SuperToast.create(getApplicationContext(), "剩餘時間:"+authService.getCardMinutesOfTwo()+"分鐘", 3000);
                            st.setGravity(Gravity.TOP);
                            st.show();
                            startService();
                            startHeartbeat();
                            _scriptThread = new ScriptThread(getApplicationContext(), self);
                            _scriptThread.start();
                        }
                        sendRunStatusJson(1);
                    }
                });
            }

            @Override
            public void onFailure(Object o) {
                new Handler(getApplicationContext().getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        showMsgBox(getString(R.string.app_name),"網絡請求失敗，請檢查網絡后重新嘗試");
                        stopScript();
                    }
                });
            }
        });
    }

    @Override
    protected void onScriptStop() {
        if (_scriptThread != null) {
            _scriptThread.interrupt();
            setRunning(false);
        }
        AuthService.getInstance().stopHearbeat();
        killServer();
        sendRunStatusJson(2);
    }

    @Override
    protected void onScriptPause() {
        _scriptThread.setPaused(true);
        sendRunStatusJson(3);
    }

    @Override
    protected void onScriptResume() {
        _scriptThread.setPaused(false);
        sendRunStatusJson(1);
    }

    @Override
    protected int getIconId() {
        return R.mipmap.ic_floating;
    }

    @Override
    protected int getRunningIconId() {
        return R.mipmap.ic_floating_running;
    }

    @Override
    protected int getPauseIconId() {
        return R.mipmap.btn_pause;
    }

    @Override
    protected int getResumeIconId() {
        return R.mipmap.btn_resume;
    }

    @Override
    public void onScriptEnd() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            stopScript();
            setRunning(false);
            _scriptThread = null;
        });
    }

    @Override
    protected void stopScript() {
        super.stopScript();
        if (_scriptThread != null) {
            _scriptThread.interrupt();
            setRunning(false);
        }
        hudManage.hideHUD("careEndDate");
        hudManage.hideHUD("move");
        hudManage.hideHUD("info");
        hudManage.hideHUD("time");
        使用卡號 = "";
        使用卡號 = SettingPreference.getString("使用卡號","").replace(" ","");
    }

    @Override
    public void onMsgBoxShow(String s, String s1) {
        showMsgBox(s, s1);
    }

    @Override
    protected ViewStyle getViewStyle() {
        return ViewStyle.TOP_RIGHT;
    }

    @Override
    protected ViewTheme getViewTheme() {
        return ViewTheme.Default;
    }



    private void sendRunStatusJson(int status) {
        JSONObject var1 = new JSONObject();
        try {
            if (status==1) {
                var1.put("e", 1);
            }
            else if (status==2) {
                var1.put("e", 2);
            } else if (status==3) {
                var1.put("e", 3);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("spe",var1.toString());
//        consoleHelper.sendMessage(var1);
    }
    /**
     * 初始化中控连接
     */
    private void initConsole() {
        Field field,field2 = null;
        try {
            field = BaseFloatingViewService.class.getDeclaredField("mPauseView");
            field.setAccessible(true);
            pauseView = (ImageView) field.get(this);
            field2 = BaseFloatingViewService.class.getDeclaredField("mSettingWindow");
            field2.setAccessible(true);
            settingWindow= (LinearLayout) field2.get(this);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        consoleHelper = ConsoleHelper.getInstance();
        if (!consoleHelper.consoleMode) {
            return;
        }

        consoleHelper.setMessageHandler(new ConsoleHelper.MessageHandler() {
            @Override
            public void handler(String msg) {
                //Log.v(TAG, msg);
                if (StringUtils.isNotEmpty(msg) && msg.startsWith("{") && msg.endsWith("}")) {
                    try {
                        MsgEvent obj = new Gson().fromJson(msg, MsgEvent.class);
                        switch (obj.getE()) {
                            case MsgEvent.EVENT.START:
                                new Handler(getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (_scriptThread == null || _scriptThread.isInterrupted()) {
                                            startScript();
                                        } else if (_scriptThread.isPaused()) {
                                            pauseView.setImageResource(getPauseIconId());
                                            _scriptThread.setPaused(false);
                                        }
                                    }
                                });
                                break;
                            case MsgEvent.EVENT.STOP:
                                new Handler(getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        stopScript();
                                        setRunning(false);
                                    }
                                });
                                break;
                            case MsgEvent.EVENT.PAUSE:
                                if (_scriptThread != null && !_scriptThread.isInterrupted() && !_scriptThread.isPaused()) {
                                    new Handler(getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            pauseView.setImageResource(getResumeIconId());
                                            _scriptThread.setPaused(true);
                                        }
                                    });
                                }
                                break;
                            case MsgEvent.EVENT.SHOW_SETTING:
                                new Handler(getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        settingWindow.setVisibility(View.VISIBLE);
                                    }
                                });
                                break;
                            case MsgEvent.EVENT.HIDE_SETTING:
                                new Handler(getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                    }
                                });
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        consoleHelper.sendMessage("{\"e\":8,\"data\":{\"msg\":\"\"}}");
    }

}
