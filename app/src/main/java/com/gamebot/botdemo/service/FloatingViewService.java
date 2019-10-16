package com.gamebot.botdemo.service;

import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.renderscript.Script;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import com.fauth.library.core.AuthService;
import com.fauth.library.entity.AuthResult;
import com.fauth.library.utils.Preference;
import com.gamebot.botdemo.R;
import com.gamebot.botdemo.entity.MsgEvent;
import com.gamebot.botdemo.script.ScriptThread;
import com.gamebot.botdemo.script.SocketClient;
import com.gamebot.botdemo.utils.ConsoleHelper;
import com.gamebot.botdemo.utils.FileUtils;
import com.gamebot.botdemo.utils.HUDManage;
import com.gamebot.botdemo.view.RadioGroupExd;
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
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


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
    private View tap1View,tap2View,tap3View,tabMainView;
    private RadioGroupExd selectShuatu,xuankashu;
    private LinearLayout qianghuaben,jinhuaben,zhuangbeirenwu,bosszhan,llCailiaoben,llJinbiben,llBanzidongzhuxian,llShuahaogandu,llZidongjianmiezhan;
    private LinearLayout yizhangka,liangzhangka,sanzhangka,llMeirirenwu;
    private CheckBoxEx cbCailiaoben,cbJinbiben,cbShuahaogandu,cbBanzidongzhuxian,cbZidongjianmiezhan,cbMeirirenuw;
    private ScrollView svMain;

    @Override
    protected void createSettingView(LayoutInflater layoutInflater) {
        tabMainView = layoutInflater.inflate(R.layout.setting_main, null);
        tap1View = layoutInflater.inflate(R.layout.setting_tab_one, null);
        tap2View = layoutInflater.inflate(R.layout.setting_tab_two, null);
        tap3View = layoutInflater.inflate(R.layout.setting_tab_three, null);
        addView("使用說明", tabMainView);
        addView("刷初始", tap1View);
        addView("日常", tap2View);
        addView("掛機",tap3View);
        selectShuatu = tap3View.findViewById(R.id.select_shuatu);
        qianghuaben = tap3View.findViewById(R.id.qianghuaben);
        jinhuaben = tap3View.findViewById(R.id.jinhuaben);
        zhuangbeirenwu = tap3View.findViewById(R.id.zhuangbeirenwu);
        bosszhan = tap3View.findViewById(R.id.BOSSzhan);
        xuankashu = tap1View.findViewById(R.id.shuachushi_xuankashu);
        yizhangka = tap1View.findViewById(R.id.shuachushi_yizhangka);
        liangzhangka = tap1View.findViewById(R.id.shuachushi_liangzhangka);
        sanzhangka = tap1View.findViewById(R.id.shuachushi_sanzhangka);
        llCailiaoben = tap2View.findViewById(R.id.ll_cailiaoben);
        llJinbiben = tap2View.findViewById(R.id.ll_jinbiben);
        llShuahaogandu = tap2View.findViewById(R.id.ll_shuahaogandu);
        llBanzidongzhuxian = tap2View.findViewById(R.id.ll_banzidongzhuxian);
        llZidongjianmiezhan = tap3View.findViewById(R.id.ll_zidongjianmiezhan);
        llMeirirenwu = tap2View.findViewById(R.id.ll_meirirenwu);
        cbCailiaoben = tap2View.findViewById(R.id.cb_cailiaoben);
        cbJinbiben = tap2View.findViewById(R.id.cb_jinbiben);
        cbShuahaogandu = tap2View.findViewById(R.id.cb_shuahaogandu);
        cbBanzidongzhuxian = tap2View.findViewById(R.id.cb_banzidongzhuxian);
        cbZidongjianmiezhan = tap3View.findViewById(R.id.cb_zidongjianmiezhan);
        cbMeirirenuw = tap2View.findViewById(R.id.cb_meirirenuw);
        svMain = tap2View.findViewById(R.id.sv_main);
        initEven();
    }


    @Override
    protected int getViewPosition() {
        return 350;
    }


    private void initEven(){
        if (cbCailiaoben.isChecked()){
            llCailiaoben.setVisibility(VISIBLE);
        }else{
            llCailiaoben.setVisibility(GONE);
        }

        if (cbJinbiben.isChecked()){
            llJinbiben.setVisibility(VISIBLE);
        }else{
            llJinbiben.setVisibility(GONE);
        }

        if (cbShuahaogandu.isChecked()){
            llShuahaogandu.setVisibility(VISIBLE);
        }else{
            llShuahaogandu.setVisibility(GONE);
        }

        if (cbBanzidongzhuxian.isChecked()){
            llBanzidongzhuxian.setVisibility(VISIBLE);
        }else{
            llBanzidongzhuxian.setVisibility(GONE);
        }

        if (cbMeirirenuw.isChecked()){
            llMeirirenwu.setVisibility(VISIBLE);
        }else{
            llMeirirenwu.setVisibility(GONE);
        }
        if (cbZidongjianmiezhan.isChecked()){
            llZidongjianmiezhan.setVisibility(VISIBLE);
        }else{
            llZidongjianmiezhan.setVisibility(GONE);
        }

        switch (selectShuatu.getIndex()){
            case 0:
                qianghuaben.setVisibility(VISIBLE);
                jinhuaben.setVisibility(GONE);
                zhuangbeirenwu.setVisibility(GONE);
                bosszhan.setVisibility(GONE);
                break;
            case 1:
                qianghuaben.setVisibility(GONE);
                jinhuaben.setVisibility(VISIBLE);
                zhuangbeirenwu.setVisibility(GONE);
                bosszhan.setVisibility(GONE);
                break;
            case 2:
                qianghuaben.setVisibility(GONE);
                jinhuaben.setVisibility(GONE);
                zhuangbeirenwu.setVisibility(VISIBLE);
                bosszhan.setVisibility(GONE);
                break;
            case 3:
                qianghuaben.setVisibility(GONE);
                jinhuaben.setVisibility(GONE);
                zhuangbeirenwu.setVisibility(GONE);
                bosszhan.setVisibility(VISIBLE);
                break;
            default:
                qianghuaben.setVisibility(VISIBLE);
                jinhuaben.setVisibility(GONE);
                zhuangbeirenwu.setVisibility(GONE);
                bosszhan.setVisibility(GONE);
                break;
        }
        selectShuatu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case 0:
                        qianghuaben.setVisibility(VISIBLE);
                        jinhuaben.setVisibility(GONE);
                        zhuangbeirenwu.setVisibility(GONE);
                        bosszhan.setVisibility(GONE);
                        break;
                    case 1:
                        qianghuaben.setVisibility(GONE);
                        jinhuaben.setVisibility(VISIBLE);
                        zhuangbeirenwu.setVisibility(GONE);
                        bosszhan.setVisibility(GONE);
                        break;
                    case 2:
                        qianghuaben.setVisibility(GONE);
                        jinhuaben.setVisibility(GONE);
                        zhuangbeirenwu.setVisibility(VISIBLE);
                        bosszhan.setVisibility(GONE);
                        break;
                    case 3:
                        qianghuaben.setVisibility(GONE);
                        jinhuaben.setVisibility(GONE);
                        zhuangbeirenwu.setVisibility(GONE);
                        bosszhan.setVisibility(VISIBLE);
                        break;
                }
            }
        });
        switch (xuankashu.getIndex()){
            case 0:
                yizhangka.setVisibility(VISIBLE);
                liangzhangka.setVisibility(GONE);
                sanzhangka.setVisibility(GONE);
                break;
            case 1:
                yizhangka.setVisibility(VISIBLE);
                liangzhangka.setVisibility(VISIBLE);
                sanzhangka.setVisibility(GONE);
                break;
            case 2:
                yizhangka.setVisibility(VISIBLE);
                liangzhangka.setVisibility(VISIBLE);
                sanzhangka.setVisibility(VISIBLE);
                break;
            default:
                yizhangka.setVisibility(VISIBLE);
                liangzhangka.setVisibility(GONE);
                sanzhangka.setVisibility(GONE);
                break;
        }
        xuankashu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case 0:
                        yizhangka.setVisibility(VISIBLE);
                        liangzhangka.setVisibility(GONE);
                        sanzhangka.setVisibility(GONE);
                        break;
                    case 1:
                        yizhangka.setVisibility(VISIBLE);
                        liangzhangka.setVisibility(VISIBLE);
                        sanzhangka.setVisibility(GONE);
                        break;
                    case 2:
                        yizhangka.setVisibility(VISIBLE);
                        liangzhangka.setVisibility(VISIBLE);
                        sanzhangka.setVisibility(VISIBLE);
                        break;
                }
            }
        });
        cbCailiaoben.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    llCailiaoben.setVisibility(VISIBLE);
                }else{
                    llCailiaoben.setVisibility(GONE);
                }
            }
        });
        cbJinbiben.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    llJinbiben.setVisibility(VISIBLE);
                }else{
                    llJinbiben.setVisibility(GONE);
                }
            }
        });
        cbMeirirenuw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    llMeirirenwu.setVisibility(VISIBLE);
                    svMain.post(new Runnable() {
                        public void run() {
                            svMain.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }else{
                    llMeirirenwu.setVisibility(GONE);
                }
            }
        });

        cbShuahaogandu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    llShuahaogandu.setVisibility(VISIBLE);
                }else{
                    llShuahaogandu.setVisibility(GONE);
                }
            }
        });

        cbBanzidongzhuxian.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    llBanzidongzhuxian.setVisibility(VISIBLE);
                }else{
                    llBanzidongzhuxian.setVisibility(GONE);
                }
            }
        });

        cbZidongjianmiezhan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    llZidongjianmiezhan.setVisibility(VISIBLE);
                }else{
                    llZidongjianmiezhan.setVisibility(GONE);
                }
            }
        });
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
        Preference preference = new Preference(getApplicationContext(),"dayData");
//        AuthService  authService = AuthService.getInstance();
//        initConsole();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
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
//                        hudManage.showHUD("careEndDate", "腳本剩餘時長:" + String.valueOf(l) + "分鐘", 13, 200, 20, 1079, 649, "#ffffff", "#80000000", 0);
//                        }else{
//                            hudManage.hideHUD("careEndDate");
//                        }
                    }
                });
            }
        });
    }

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
                        String text = "";
                        if(o==null){
                            text="驗證返回數據為空!";
                        }else if(o instanceof AuthResult){
                            text=authService.getErrorMsg(((AuthResult)o).getStatus());
                            if(StringUtils.isEmpty(text)){
                                text=((AuthResult)o).getMsg();
                            }
                        }else if(o instanceof String){
                            text=(String) o;
                        }
                        showMsgBox(getString(R.string.app_name),text);
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
        authService.stopHearbeat();
        killServer();
//        sendRunStatusJson(2);
    }

    @Override
    protected void onScriptPause() {
        _scriptThread.setPaused(true);
//        sendRunStatusJson(3);
    }

    @Override
    protected void onScriptResume() {
        _scriptThread.setPaused(false);
//        sendRunStatusJson(1);
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
        }
        authService.stopHearbeat();
        killServer();
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
                                        showMsgBox(getString(R.string.app_name),"脚本异常退出，请重新启动");
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
                                        settingWindow.setVisibility(VISIBLE);
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
