package com.gamebot.botdemo.script;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.fauth.library.utils.Preference;
import com.gamebot.botdemo.R;
import com.gamebot.botdemo.entity.Goods;
import com.gamebot.botdemo.entity.NPCBean;
import com.gamebot.botdemo.entity.TaskAction;
import com.gamebot.botdemo.entity.UnitAction;
import com.gamebot.botdemo.entity.UnitCallback;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import com.gamebot.botdemo.utils.DateUtil;
import com.gamebot.botdemo.utils.HUDManage;
import com.gamebot.botdemo.utils.ProcessUtils;
import com.gamebot.botdemo.utils.TimingUtil;
import com.gamebot.sdk.preference.*;
import com.google.gson.Gson;
import com.topjohnwu.superuser.Shell;

import static android.os.Looper.getMainLooper;


public class ScriptThread extends SuperScriptThread {

    private final static String gamePkg="com.linegames.exos";
    private static Preference Setting;

    private TaskAction gongGongAction;

    private boolean 領取郵件_開關 = false;
    private boolean 探索任務_開關 = SettingPreference.getBoolean("探索任務",false);
    private boolean 成就領取_開關 = false;
    private boolean 八小時一抽_開關 = false;
    private boolean 七日獎勵_開關 = SettingPreference.getBoolean("七日獎勵",false);
    private boolean 工會簽到_開關 = SettingPreference.getBoolean("工會簽到",false);
    private boolean 在線時長獎勵_開關 = SettingPreference.getBoolean("在線時長獎勵",false);
    private boolean 每日任務_開關 = false;
    private boolean 主线任務_開關 = false;
    private boolean 英雄經驗本_開關 = SettingPreference.getBoolean("英雄經驗本",false);
    private boolean 英雄狗糧本_開關 = SettingPreference.getBoolean("英雄狗糧本",false);
    private boolean 低級金幣本_開關 = SettingPreference.getBoolean("低級金幣本",false);
    private boolean 裝備升級本_開關 = SettingPreference.getBoolean("裝備升級本",false);
    private boolean 裝備升星本_開關 = SettingPreference.getBoolean("裝備升星本",false);
    private boolean 英雄升星本_開關 = SettingPreference.getBoolean("英雄升星本",false);
    private boolean 高級金幣本_開關 = SettingPreference.getBoolean("高級金幣本",false);

    private String 英雄經驗本_等級 = SettingPreference.getString("英雄經驗本等級","");
    private String 英雄狗糧本_等級 = SettingPreference.getString("英雄狗糧本等級","");
    private String 低級金幣本_等級 = SettingPreference.getString("低級金幣本等級","");
    private String 裝備升級本_等級 = SettingPreference.getString("裝備升級本等級","");
    private String 裝備升星本_等級 = SettingPreference.getString("裝備升星本等級","");
    private String 英雄升星本_等級 = SettingPreference.getString("英雄升星本等級","");
    private String 高級金幣本_等級 = SettingPreference.getString("高級金幣本等級","");

    private int 等待體力回復時長 = 0,七日獎勵等待 = 3,等待次數 = 3;

    private long 每日任務計時 = 0,在線獎勵計時 = 0;

    private boolean 連續刷圖選中 = false,自動編隊 = false,副本選中 = false,切換等待 = false;


    public ScriptThread(Context context, ScriptServiceListener scriptServiceListener) {
        super(context, scriptServiceListener);
        init(context);
        mContext = context;
    }
    @Override
    protected String getTag() {
        return "qdzbot";
    }
    @Override
    protected String getAppName() {
        return context.getResources().getString(R.string.app_name);
    }

    public static void init(Context context){
        Setting = new Preference(context,"dayData");
    }

    private void initTaskAction() {
        initColorAction(true);
        initGongGong();
    }

    /**
     * 初始化原始數據
     */
    private void initData(){
        if (!SettingPreference.getString("等待體力回復時長","").equals("")){
            if (!SettingPreference.getString("等待體力回復時長","").equals("0")){
                等待體力回復時長 = Integer.parseInt(SettingPreference.getString("等待體力回復時長",""));
            }else{
                等待體力回復時長 = 0;
            }
        }else{
            等待體力回復時長 = 0;
        }
    }

    /**
     * 郵件領取
     * @return
     */
    private TaskAction initYouJianLingQu(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("世界頁面","打開郵件");
        taskAction.addLayerAction("飛船頁面","打開郵件");
        taskAction.addLayerAction("郵件頁面","一鍵領取","領取","換類型",new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                領取郵件_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        return taskAction;
    }

    /**
     * 探索任务
     */
    private TaskAction initTanSuo(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("飛船頁面","回到世界");
        taskAction.addLayerAction("世界頁面","探索");
        taskAction.addLayerAction("探索開始頁面","開始");
        taskAction.addLayerAction("探索中頁面",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                delay(2000);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("停止探索頁面","取消");
        taskAction.addLayerAction("通知道具不足頁面",new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                探索任務_開關 = false;
                每日任務計時 = System.currentTimeMillis();
                return true;
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("確認", new UnitCallback() {
            @Override
            public boolean before() {
                探索任務_開關 = false;
                每日任務計時 = System.currentTimeMillis();
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("探索結果頁面",new UnitAction("確認", new UnitCallback() {
            @Override
            public boolean before() {
                探索任務_開關 = false;
                每日任務計時 = System.currentTimeMillis();
                return true;
            }

            @Override
            public void after() {

            }
        }));
        return taskAction;
    }

    /**
     * 每日任务
     */
    private TaskAction initMeiRi(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("飛船頁面","回到世界");
        taskAction.addLayerAction("世界頁面",new UnitAction("每日任務", new UnitCallback() {
            @Override
            public boolean before() {
                if (System.currentTimeMillis() - 每日任務計時 > 5000){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                每日任務計時 = System.currentTimeMillis();
            }
        }),"等待");
        taskAction.addLayerAction("城內頁面","七日禮物",new UnitAction("每日任務", new UnitCallback() {
            @Override
            public boolean before() {
                if (System.currentTimeMillis() - 每日任務計時 > 15000){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                每日任務計時 = System.currentTimeMillis();
            }
        }));
        return taskAction;
    }

    /**
     * 主線任務
     */
    private TaskAction initZhuXian(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("飛船頁面","回到世界");
        taskAction.addLayerAction("世界頁面","主線任務1","主線任務2");
        taskAction.addLayerAction("主線詳情頁面","故事進行");
        taskAction.addLayerAction("主線勝利頁面","確定");
        return taskAction;
    }

    /**
     * 英雄經驗本
     */
    private TaskAction initYingXiongJingYanBen(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("飛船頁面","回到世界");
        taskAction.addLayerAction("世界頁面","副本","等待");
        taskAction.addLayerAction("副本選擇頁面",new UnitAction("英雄經驗本",new UnitCallback() {
                    @Override
                    public boolean before() {
                        副本選中 = true;
                        return true;
                    }

                    @Override
                    public void after() {
                    }
                }),
                new UnitAction("前往", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (副本選中){
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        副本選中 = false;
                    }
                }));
        taskAction.addLayerAction("英雄經驗頁面",英雄經驗本_等級,
                new UnitAction("連續刷圖", new UnitCallback() {
                    @Override
                    public boolean before() {
                        連續刷圖選中 = true;
                        return false;
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("連續刷", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (連續刷圖選中){
                            return false;
                        }else {
                            return true;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("自動編隊", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (自動編隊){
                            return false;
                        }else{
                            return true;
                        }
                    }

                    @Override
                    public void after() {
                        自動編隊 = true;
                    }
                }),
                new UnitAction("開始", new UnitCallback() {
                    @Override
                    public boolean before() {
                        連續刷圖選中 = false;
                        自動編隊 = false;
                        return true;
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("道具不足頁面",new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                Setting.putBoolean(DateUtil.getNowDateStr() + "英雄經驗本",true);
                連續刷圖選中 = false;
                自動編隊 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("道具用光頁面",new UnitAction("確認", new UnitCallback() {
            @Override
            public boolean before() {
                Setting.putBoolean(DateUtil.getNowDateStr() + "英雄經驗本",true);
                連續刷圖選中 = false;
                自動編隊 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("英雄經驗結算頁面",new UnitAction("確認", new UnitCallback() {
            @Override
            public boolean before() {
                Setting.putBoolean(DateUtil.getNowDateStr() + "英雄經驗本",true);
                連續刷圖選中 = false;
                自動編隊 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        return taskAction;
    }

    /**
     * 英雄狗糧本
     */
    private TaskAction initYingXiongGouLiangBen(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("飛船頁面","回到世界");
        taskAction.addLayerAction("世界頁面","副本","等待");
        taskAction.addLayerAction("副本選擇頁面",new UnitAction("英雄狗糧本",new UnitCallback() {
                    @Override
                    public boolean before() {
                        副本選中 = true;
                        return true;
                    }

                    @Override
                    public void after() {
                    }
                }),
                new UnitAction("前往", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (副本選中){
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        副本選中 = false;
                    }
                }));
        taskAction.addLayerAction("英雄狗糧頁面",英雄狗糧本_等級,
                new UnitAction("連續刷圖", new UnitCallback() {
                    @Override
                    public boolean before() {
                        連續刷圖選中 = true;
                        return false;
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("連續刷", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (連續刷圖選中){
                            return false;
                        }else {
                            return true;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("自動編隊", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (自動編隊){
                            return false;
                        }else{
                            return true;
                        }
                    }

                    @Override
                    public void after() {
                        自動編隊 = true;
                    }
                }),
                new UnitAction("開始", new UnitCallback() {
                    @Override
                    public boolean before() {
                        連續刷圖選中 = false;
                        自動編隊 = false;
                        return true;
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("道具不足頁面",new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                Setting.putBoolean(DateUtil.getNowDateStr() + "英雄狗糧本",true);
                連續刷圖選中 = false;
                自動編隊 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("道具用光頁面",new UnitAction("確認", new UnitCallback() {
            @Override
            public boolean before() {
                Setting.putBoolean(DateUtil.getNowDateStr() + "英雄狗糧本",true);
                連續刷圖選中 = false;
                自動編隊 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("英雄狗糧結算頁面",new UnitAction("確認", new UnitCallback() {
            @Override
            public boolean before() {
                Setting.putBoolean(DateUtil.getNowDateStr() + "英雄狗糧本",true);
                連續刷圖選中 = false;
                自動編隊 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        return taskAction;
    }

    /**
     * 英雄狗糧本
     */
    private TaskAction initDiJiJingBiBen(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("飛船頁面","回到世界");
        taskAction.addLayerAction("世界頁面","副本","等待");
        taskAction.addLayerAction("副本選擇頁面",new UnitAction("低級金幣本",new UnitCallback() {
                    @Override
                    public boolean before() {
                        副本選中 = true;
                        return true;
                    }

                    @Override
                    public void after() {
                    }
                }),
                new UnitAction("前往", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (副本選中){
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        副本選中 = false;
                    }
                }));
        taskAction.addLayerAction("低級金幣頁面",低級金幣本_等級,
                new UnitAction("連續刷圖", new UnitCallback() {
                    @Override
                    public boolean before() {
                        連續刷圖選中 = true;
                        return false;
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("連續刷", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (連續刷圖選中){
                            return false;
                        }else {
                            return true;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("自動編隊", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (自動編隊){
                            return false;
                        }else{
                            return true;
                        }
                    }

                    @Override
                    public void after() {
                        自動編隊 = true;
                    }
                }),
                new UnitAction("開始", new UnitCallback() {
                    @Override
                    public boolean before() {
                        連續刷圖選中 = false;
                        自動編隊 = false;
                        return true;
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("道具不足頁面",new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                Setting.putBoolean(DateUtil.getNowDateStr() + "低級金幣本",true);
                連續刷圖選中 = false;
                自動編隊 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("道具用光頁面",new UnitAction("確認", new UnitCallback() {
            @Override
            public boolean before() {
                Setting.putBoolean(DateUtil.getNowDateStr() + "低級金幣本",true);
                連續刷圖選中 = false;
                自動編隊 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("低級金幣結算頁面",new UnitAction("確認", new UnitCallback() {
            @Override
            public boolean before() {
                Setting.putBoolean(DateUtil.getNowDateStr() + "低級金幣本",true);
                連續刷圖選中 = false;
                自動編隊 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        return taskAction;
    }

    /**
     * 太陽裝備本
     */
    private TaskAction initZhuangBeiShengJiBen(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("飛船頁面","回到世界");
        taskAction.addLayerAction("世界頁面","副本","等待");
        taskAction.addLayerAction("副本選擇頁面",new UnitAction("裝備升級本", new UnitCallback() {
            @Override
            public boolean before() {
                副本選中 = true;
                return true;
            }

            @Override
            public void after() {
            }
        }),
                new UnitAction("前往", new UnitCallback() {
            @Override
            public boolean before() {
                if (副本選中){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                副本選中 = false;
            }
        }));
        taskAction.addLayerAction("裝備升級頁面",裝備升級本_等級,
                new UnitAction("連續刷圖", new UnitCallback() {
            @Override
            public boolean before() {
                連續刷圖選中 = true;
                return false;
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("連續刷", new UnitCallback() {
            @Override
            public boolean before() {
                if (連續刷圖選中){
                    return false;
                }else {
                    return true;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("自動編隊", new UnitCallback() {
            @Override
            public boolean before() {
                if (自動編隊){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {
                自動編隊 = true;
            }
        }),new UnitAction("開始", new UnitCallback() {
                    @Override
                    public boolean before() {
                        連續刷圖選中 = false;
                        自動編隊 = false;
                        return true;
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("道具不足頁面",new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                連續刷圖選中 = false;
                自動編隊 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + "裝備升級本",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("道具用光頁面",new UnitAction("確認", new UnitCallback() {
            @Override
            public boolean before() {
                連續刷圖選中 = false;
                自動編隊 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + "裝備升級本",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("裝備升級結算頁面",new UnitAction("確認", new UnitCallback() {
            @Override
            public boolean before() {
                Setting.putBoolean(DateUtil.getNowDateStr() + "裝備升級本",true);
                連續刷圖選中 = false;
                自動編隊 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        return taskAction;
    }

    /**
     * 月亮裝備本
     */
    private TaskAction initZhuangBeiShengXingBen(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("飛船頁面","回到世界");
        taskAction.addLayerAction("世界頁面","副本","等待");
        taskAction.addLayerAction("副本選擇頁面",new UnitAction("裝備升星本", new UnitCallback() {
                    @Override
                    public boolean before() {
                        副本選中 = true;
                        return true;
                    }

                    @Override
                    public void after() {
                    }
                }),
                new UnitAction("前往", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (副本選中){
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        副本選中 = false;
                    }
                }));
        taskAction.addLayerAction("裝備升星頁面",裝備升星本_等級,
                new UnitAction("連續刷圖", new UnitCallback() {
                    @Override
                    public boolean before() {
                        連續刷圖選中 = true;
                        return false;
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("連續刷", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (連續刷圖選中){
                            return false;
                        }else {
                            return true;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("自動編隊", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (自動編隊){
                            return false;
                        }else{
                            return true;
                        }
                    }

                    @Override
                    public void after() {
                        自動編隊 = true;
                    }
                }),new UnitAction("開始", new UnitCallback() {
                    @Override
                    public boolean before() {
                        連續刷圖選中 = false;
                        自動編隊 = false;
                        return true;
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("道具不足頁面",new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                連續刷圖選中 = false;
                自動編隊 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + "裝備升星本",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("道具用光頁面",new UnitAction("確認", new UnitCallback() {
            @Override
            public boolean before() {
                連續刷圖選中 = false;
                自動編隊 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + "裝備升星本",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("裝備升星結算頁面",new UnitAction("確認", new UnitCallback() {
            @Override
            public boolean before() {
                Setting.putBoolean(DateUtil.getNowDateStr() + "裝備升星本",true);
                連續刷圖選中 = false;
                自動編隊 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        return taskAction;
    }

    /**
     * 星星裝備本
     */
    private TaskAction initYingXiongShengXingBen(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("飛船頁面","回到世界");
        taskAction.addLayerAction("世界頁面","副本","等待");
        taskAction.addLayerAction("副本選擇頁面",new UnitAction("英雄升星本", new UnitCallback() {
                    @Override
                    public boolean before() {
                        副本選中 = true;
                        等待次數 = 3;
                        return true;
                    }

                    @Override
                    public void after() {
                    }
                }),new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (等待次數 == 0 || 副本選中){
                            return false;
                        }else{
                            return true;
                        }
                    }

                    @Override
                    public void after() {
                        touchDown(200,964);
                        delay(2000);
                        touchMove(400,967);
                        touchUp();
                        等待次數 --;
                    }
                }),
                new UnitAction("前往", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (副本選中){
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        副本選中 = false;
                    }
                }));
        taskAction.addLayerAction("英雄升星頁面",英雄升星本_等級,
                new UnitAction("連續刷圖", new UnitCallback() {
                    @Override
                    public boolean before() {
                        連續刷圖選中 = true;
                        return false;
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("連續刷", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (連續刷圖選中){
                            return false;
                        }else {
                            return true;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("自動編隊", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (自動編隊){
                            return false;
                        }else{
                            return true;
                        }
                    }

                    @Override
                    public void after() {
                        自動編隊 = true;
                    }
                }),new UnitAction("開始", new UnitCallback() {
                    @Override
                    public boolean before() {
                        連續刷圖選中 = false;
                        自動編隊 = false;
                        return true;
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("道具不足頁面",new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                連續刷圖選中 = false;
                自動編隊 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + "英雄升星本",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("道具用光頁面",new UnitAction("確認", new UnitCallback() {
            @Override
            public boolean before() {
                連續刷圖選中 = false;
                自動編隊 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + "英雄升星本",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("英雄升星結算頁面",new UnitAction("確認", new UnitCallback() {
            @Override
            public boolean before() {
                Setting.putBoolean(DateUtil.getNowDateStr() + "英雄升星本",true);
                連續刷圖選中 = false;
                自動編隊 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        return taskAction;
    }

    /**
     * 高級金幣本
     */
    private TaskAction initGaoJiJinBiBen(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("飛船頁面","回到世界");
        taskAction.addLayerAction("世界頁面","副本","等待");
        taskAction.addLayerAction("副本選擇頁面",new UnitAction("高級金幣本", new UnitCallback() {
                    @Override
                    public boolean before() {
                        等待次數 = 3;
                        副本選中 = true;
                        return true;
                    }

                    @Override
                    public void after() {
                    }
                }),
                new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (等待次數 == 0 || 副本選中){
                            return false;
                        }else{
                            return true;
                        }
                    }

                    @Override
                    public void after() {
                        touchDown(200,964);
                        delay(2000);
                        touchMove(400,967);
                        touchUp();
                        等待次數 --;
                    }
                }),
                new UnitAction("前往", new UnitCallback(){
                    @Override
                    public boolean before() {
                        if (副本選中){
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        副本選中 = false;
                    }
                }));
        taskAction.addLayerAction("高級金幣頁面",高級金幣本_等級,
                new UnitAction("連續刷圖", new UnitCallback() {
                    @Override
                    public boolean before() {
                        連續刷圖選中 = true;
                        return false;
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("連續刷", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (連續刷圖選中){
                            return false;
                        }else {
                            return true;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("自動編隊", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (自動編隊){
                            return false;
                        }else{
                            return true;
                        }
                    }

                    @Override
                    public void after() {
                        自動編隊 = true;
                    }
                }),new UnitAction("開始", new UnitCallback() {
                    @Override
                    public boolean before() {
                        連續刷圖選中 = false;
                        自動編隊 = false;
                        return true;
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("道具不足頁面",new UnitAction("確認", new UnitCallback() {
            @Override
            public boolean before() {
                連續刷圖選中 = false;
                自動編隊 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + "高級金幣本",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("道具用光頁面",new UnitAction("確認", new UnitCallback() {
            @Override
            public boolean before() {
                連續刷圖選中 = false;
                自動編隊 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + "高級金幣本",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        return taskAction;
    }

    /**
     * 自動派遣
     * @return
     */
    private TaskAction initZiDongPaiQian(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("飛船頁面","進入派遣");
        taskAction.addLayerAction("派遣完成確認頁面","確認");
        return taskAction;
    }

    /**
     * 領取成就
     * @return
     */
    private TaskAction initChengJiuLingQu(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("飛船頁面","成就領取",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                成就領取_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("成就頁面","一鍵領取","領取","切換類型",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                成就領取_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        return taskAction;
    }

    /**
     * 工會簽到
     * @return
     */
    private TaskAction initGongHuiQianDao(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("飛船頁面","工會簽到",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                Setting.putBoolean(DateUtil.getNowDateStr() + "工會簽到",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("工會信息頁面","簽到",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                Setting.putBoolean(DateUtil.getNowDateStr() + "工會簽到",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("工會確認頁面","確認");
        return taskAction;
    }

    /**
     * 八小時一抽
     * @return
     */
    private TaskAction initBaXiaoShiYiChou(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("飛船頁面","招募",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                八小時一抽_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("世界頁面","返回飛船");
        taskAction.addLayerAction("招募頁面","招募","單抽",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                八小時一抽_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("招募結果頁面","確認");
        return taskAction;
    }

    /**
     * 七日獎勵
     * @return
     */
    private TaskAction initQiRiJiangLi(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("飛船頁面","回到世界");
        taskAction.addLayerAction("世界頁面","進入城內");
        taskAction.addLayerAction("城內頁面","七日禮物",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                if(七日獎勵等待 == 0){
                    Setting.putBoolean(DateUtil.getNowDateStr() + "七日獎勵",true);
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {
                七日獎勵等待 --;
            }
        }));
        taskAction.addLayerAction("七日獎勵頁面",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                Setting.putBoolean(DateUtil.getNowDateStr() + "七日獎勵",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        return taskAction;
    }

    /**
     * 領取在線獎勵
     * @return
     */
    private TaskAction initLingQuZaiXianJiangLi(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("飛船頁面","回到世界");
        taskAction.addLayerAction("世界頁面","進入城內");
        taskAction.addLayerAction("城內頁面","在線獎勵",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                在線時長獎勵_開關 = false;
                在線獎勵計時 = System.currentTimeMillis();
                return true;

            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("活動領獎頁面","領取","切換類型",new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before(){
                在線時長獎勵_開關 = false;
                在線獎勵計時 = System.currentTimeMillis();
                return true;
            }

            @Override
            public void after() {

            }
        }));
        return taskAction;
    }

    /**
     * 公共操作
     */
    private void initGongGong(){
        gongGongAction = new TaskAction();
        gongGongAction.addLayerAction("飛船頁面",new UnitAction("成就領取", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getBoolean("成就領取",false)) {
                    成就領取_開關 = true;
                }
                return false;
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("招募", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (SettingPreference.getBoolean("8小時一抽",false)) {
                            八小時一抽_開關 = true;
                        }
                        return false;
                    }

                    @Override
                    public void after() {

                    }
                }));
        gongGongAction.addLayerAction("世界頁面",new UnitAction("返回飛船", new UnitCallback() {
            @Override
            public boolean before() {
                if (切換等待){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                切換等待 = false;
            }
        }),
                new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                切換等待 = true;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        gongGongAction.addLayerAction("探索開始頁面","關閉");
        gongGongAction.addLayerAction("道具用光頁面","確認");
        gongGongAction.addLayerAction("城內頁面","回到世界");
        gongGongAction.addLayerAction("道具不足頁面","取消");
        gongGongAction.addLayerAction("登錄選擇頁面","谷歌登錄");
        gongGongAction.addLayerAction("登錄頁面","點擊開始");
        gongGongAction.addLayerAction("每日獎勵頁面","確定");
        gongGongAction.addLayerAction("廣告頁面","關閉");
        gongGongAction.addLayerAction("戰鬥勝利頁面","確認");
        gongGongAction.addLayerAction("戰鬥失敗頁面","確認");
        gongGongAction.addLayerAction("探索現狀頁面","關閉");
        gongGongAction.addLayerAction("探索終止頁面","確認","取消");
        gongGongAction.addLayerAction("探索結果頁面","確認");
        gongGongAction.addLayerAction("斷線重連頁面1","重試");
        gongGongAction.addLayerAction("斷線重連頁面2","重試");
        gongGongAction.addLayerAction("斷線重連頁面3","重試");
        gongGongAction.addLayerAction("SKIP頁面","skip");
        gongGongAction.addLayerAction("探索中頁面","取消探索");
        gongGongAction.addLayerAction("戰鬥頁面","x2","auto");
        gongGongAction.addLayerAction("玩家升級頁面","確定");
        gongGongAction.addLayerAction("英雄經驗頁面","返回");
        gongGongAction.addLayerAction("英雄狗糧頁面","返回");
        gongGongAction.addLayerAction("低級金幣頁面","返回");
        gongGongAction.addLayerAction("裝備升級頁面","返回");
        gongGongAction.addLayerAction("裝備升星頁面","返回");
        gongGongAction.addLayerAction("英雄升星頁面","返回");
        gongGongAction.addLayerAction("英雄經驗結算頁面","確認");
        gongGongAction.addLayerAction("英雄狗糧結算頁面","確認");
        gongGongAction.addLayerAction("低級金幣結算頁面","確認");
        gongGongAction.addLayerAction("裝備升級結算頁面","確認");
        gongGongAction.addLayerAction("裝備升星結算頁面","確認");
        gongGongAction.addLayerAction("英雄升星結算頁面","確認");
        gongGongAction.addLayerAction("單選頁面","確認");
        gongGongAction.addLayerAction("副本選擇頁面","關閉");
        gongGongAction.addLayerAction("未知頁面","確認","點擊");
        gongGongAction.addLayerAction("郵件頁面","關閉");
        gongGongAction.addLayerAction("每週結算頁面","關閉");
        gongGongAction.addLayerAction("通訊不暢頁面","確認");
        gongGongAction.addLayerAction("高級金幣頁面","返回");
        gongGongAction.addLayerAction("高級金幣結算頁面","確認");
        gongGongAction.addLayerAction("招募頁面","返回");
        gongGongAction.addLayerAction("錯誤提示頁面",new UnitAction("確認", new UnitCallback() {
            @Override
            public boolean before() {
                killApp(gamePkg);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        gongGongAction.addLayerAction("背包不足頁面",new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getBoolean("背包不足騰空間",false)){
//                    背包騰空間 = true;
                }else{
//                    暫停腳本 = true;
                }
                return true;
            }

            @Override
            public void after() {

            }
        }));
        gongGongAction.addLayerAction("成就頁面","返回");
        gongGongAction.addLayerAction("活動領獎頁面","關閉");
    }


    @Override
    public void run() {
        scriptStart();
        delay(2000);
        initData();
        initTaskAction();
        File file = new File("/sdcard/Pictures/Screenshots/ehsData.txt");
        在線獎勵計時 = System.currentTimeMillis();
        try{
            file.delete();
            file.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }
        while (!isInterrupted()) {
            if (isPaused()) {
                String lastHUDText = hudManage.getHUDText("info");
                showMessage("暫停");
                showHUDInfo("暫停中");
                while (!isInterrupted() && isPaused()) {

                    delay(1000);
                }
                initTaskAction();
                showHUDInfo(lastHUDText);
            }
            if(!gamePkg.equals(getFront())){
                runApp(gamePkg);
                try{
                    fileWriter("啟動遊戲"+"**************************************",file);
                }catch (IOException e){
                    e.printStackTrace();
                }
                delay(1000);
            }else{
                keepCapture();
                String curLayerName = null;
                TaskAction curTaskAction=null;
                curLayerName = getLayerName();
                /************************************判斷執行任務順序******************************/
                if (工會簽到_開關 && !Setting.getBoolean(DateUtil.getNowDateStr() + "工會簽到",false)){
                    curTaskAction = initGongHuiQianDao();
                    showHUDInfo("工會簽到");
                }else if (七日獎勵_開關 && !Setting.getBoolean(DateUtil.getNowDateStr() + "七日獎勵",false)){
                    curTaskAction = initQiRiJiangLi();
                    showHUDInfo("七日獎勵");
                }else if (在線時長獎勵_開關 && System.currentTimeMillis() - 在線獎勵計時 > 65*60*1000){
                    curTaskAction = initLingQuZaiXianJiangLi();
                    showHUDInfo("領取在線獎勵");
                }else if (領取郵件_開關){
                    curTaskAction = initYouJianLingQu();
                    showHUDInfo("郵件領取");
                }else if (成就領取_開關){
                    curTaskAction = initChengJiuLingQu();
                    showHUDInfo("領取成就");
                }else if (八小時一抽_開關){
                    curTaskAction = initBaXiaoShiYiChou();
                    showHUDInfo("8小時一抽");
                }else if (探索任務_開關){
                    curTaskAction = initTanSuo();
                    showHUDInfo("探索任務");
                }else if (主线任務_開關){
                    curTaskAction = initZhuXian();
                    showHUDInfo("主線任務");
                }else if (每日任務_開關){
                    curTaskAction = initMeiRi();
                    showHUDInfo("每日任務");
                }else if (英雄經驗本_開關 && !Setting.getBoolean(DateUtil.getNowDateStr() + "英雄經驗本",false)){
                    curTaskAction = initYingXiongJingYanBen();
                    showHUDInfo("英雄經驗本");
                }else if (英雄狗糧本_開關 && !Setting.getBoolean(DateUtil.getNowDateStr() + "英雄狗糧本",false)){
                    curTaskAction = initYingXiongGouLiangBen();
                    showHUDInfo("英雄狗糧本");
                }else if (低級金幣本_開關 && !Setting.getBoolean(DateUtil.getNowDateStr() + "低級金幣本",false)){
                    curTaskAction = initDiJiJingBiBen();
                    showHUDInfo("金幣本");
                }else if (裝備升級本_開關 && !Setting.getBoolean(DateUtil.getNowDateStr() + "裝備升級本",false)){
                    curTaskAction = initZhuangBeiShengJiBen();
                    showHUDInfo("裝備升級本");
                }else if (裝備升星本_開關 && !Setting.getBoolean(DateUtil.getNowDateStr() + "裝備升星本",false)){
                    curTaskAction = initZhuangBeiShengXingBen();
                    showHUDInfo("裝備升星本");
                }else if (英雄升星本_開關 && !Setting.getBoolean(DateUtil.getNowDateStr() + "英雄升星本",false)){
                    curTaskAction = initYingXiongShengXingBen();
                    showHUDInfo("英雄升星本");
                }else if (高級金幣本_開關 && !Setting.getBoolean(DateUtil.getNowDateStr() + "高級金幣本",false)){
                    curTaskAction = initGaoJiJinBiBen();
                    showHUDInfo("高級金幣本");
                }else{
                    showHUDInfo("暫無任務");
                }
                if (SettingPreference.getBoolean("探索任務",false) && !探索任務_開關 && System.currentTimeMillis() - 每日任務計時 > 等待體力回復時長 * 60 * 1000){
                    探索任務_開關 = true;
                }
                if (SettingPreference.getBoolean("領取郵件",false)){
                    Point[] points = null;
                    points = findMultiColors(646, 1091, 719, 1167,"fffff7-101010","14|1|fffff7-101010,14|-15|fffff7-101010,14|12|fff7f7-101010,-8|16|fffff7-101010,-8|-15|fffff7-101010,3|-16|fffff7-101010,-2|0|fffff7-101010,18|23|ef4d6b-101010",0.95f);
                    if (points != null && points.length > 0){
                        領取郵件_開關 = true;
                    }
                }
                /*************************************任務重置*************************************/

                /*****************************過零點任務重置開關重置為true*************************/

                /**********************************畫面超時不動重啟遊戲****************************/

                /*************************************斷線重連等待*********************************/

                /***************************************執行操作***********************************/
                if (execTask(curLayerName,curTaskAction)) {
                    delay(500);
                }else{
                    execTask(curLayerName,gongGongAction);
                }
                /***************************************打印日誌***********************************/
                Log.e(getTag(), "當前頁面:" + curLayerName);
                showMOVEInfo(curLayerName + "-" + getUnitName());
                try{
                    fileWriter(curLayerName + "-" + getUnitName(),file);
                }catch (IOException e){
                    e.printStackTrace();
                }
                delay(500);
                releaseCapture();
            }
        }
        scriptEnd();
    }

    @Override
    protected void scriptEnd() {
        super.scriptEnd();
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                hudManage.hideHUD("move");
                hudManage.hideHUD("info");
            }
        });
    }

    public void fileWriter(String str, File file) throws IOException{
        FileWriter writer = new FileWriter(file,true);
        // 向文件写入内容
        writer.write(DateUtil.getNowTimestampStr() + "  " + str + "\r\n");
        writer.flush();
        writer.close();
    }

    private void sendLineMessage(String userName,String notifCodes,String notifStr){

    }

}
