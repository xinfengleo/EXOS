package com.gamebot.botdemo.script;

import android.content.Context;
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
    private final static String gamePkg="com.netmarble.nanatsunotaizai";
    private SocketClient socketClient;
    private static Preference Setting;
    private Context mContext;


    private TaskAction g_taskAction,fenJieZhuangBeiTaskAction;
    private boolean 刷初始_開關 = SettingPreference.getBoolean("刷初始開關",false);
    private boolean 使用引繼碼_開關 = SettingPreference.getBoolean("使用引繼碼",false);
    private boolean 每日抽裝備_開關 = SettingPreference.getBoolean("每日抽裝備",false);
    private boolean 每日領寶箱_開關 = SettingPreference.getBoolean("每日領寶箱",false);
    private boolean 一鑽石抽獎_開關 = SettingPreference.getBoolean("1鑽石抽卡",false);
    private boolean 郵件領取_開關 = false;
    private boolean 成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
    private boolean 贈送友情點_開關 = SettingPreference.getBoolean("贈送友情點數",false);
    private boolean 友情點買藥_開關 = SettingPreference.getBoolean("友情點買體力",false);
    private boolean 消費友情點_開關 = false;
    private boolean 金幣本_開關 = SettingPreference.getBoolean("金幣本",false);
    private boolean 材料本_開關 = SettingPreference.getBoolean("材料本",false);
    private boolean 村莊好感度_開關 = SettingPreference.getBoolean("村莊好感度",false);
    private boolean 半自動主線_開關 = SettingPreference.getBoolean("半自動主線",false);
    private boolean BOSS戰_開關 = SettingPreference.getBoolean("BOSS戰",false);
    private boolean 每日任務_開關 = SettingPreference.getBoolean("每日任務",false);
    private boolean 騎士團祈禱 = SettingPreference.getBoolean("騎士團祈禱",false);
    private boolean 騎士團獎勵_開關 = SettingPreference.getBoolean("騎士團獎勵",false);
    private boolean 夢幻激戰 = SettingPreference.getBoolean("BOSS戰夢幻激戰",false);
    private boolean 無法者之岩 = SettingPreference.getBoolean("BOSS戰無法者之岩",false);
    private boolean 水晶洞窟 = SettingPreference.getBoolean("BOSS戰水晶洞窟",false);
    private boolean 紅色大地 = SettingPreference.getBoolean("BOSS戰紅色大地",false);
    private boolean 山神之森 = SettingPreference.getBoolean("BOSS戰山神之森",false);
    private boolean 墮落根源 = SettingPreference.getBoolean("BOSS戰墮落根源",false);
    private boolean 村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
    private boolean 酒館好感度_開關 = SettingPreference.getBoolean("酒館刷親密",false);
    private boolean 裝備分解_開關 = false,裝備選定 = false,出售金幣箱子_開關 = false;
    private boolean 萬聖節活動_開關 = false;
    private boolean 萬聖節活動主線_開關 = false,萬聖節活動殲滅戰_開關 = false,萬聖節活動普通本_開關 = false,每日任務領取 = false,裝備任務完成 = false;

    private boolean 抽獎完成 = false,检查个数完成 = false,密碼設置成功 = false,引繼碼賬號輸入完成 = false,引繼碼密碼輸入完成 = false
            ,引繼成功 = false,斷線等待 = false,任務重置_開關 = true,MAX選中 = false, 隊伍選中 = false,村莊好感度特訓任務 = false;
    private boolean 金幣本自動降級 = false,材料本自動降級 = false,村莊好感度切換村莊 = false,村莊刷好感失敗 = false;
    private boolean 等待體力回復 = false,通關完成 = false,好感度任務 = false,買食材選好村莊 = false,肉塊購買 = false,野菜購買 = false,購買成功 = false,料理完成 = false,主線自動編成 = false;
    private boolean 購買食材_開關 = false,無好感度任務 = false,體力回復中 = false,檢查過鑽石 = false,村莊好感度村莊0 = false,村莊好感度村莊1 = false;
    private boolean 日常材料本_開關 = false,日常普通本_開關 = false,日常強化任務_開關 = false,日常製作料理_開關 = false,日常BOSS戰_開關 = false,日常PVP戰鬥_開關 = false;
    private boolean 殲滅戰發生 = false,殲滅戰_開關 = false,少隊友 = true,無食材 = false,每日BOSS戰無法選擇 = false,每日普通本不能刷 = false;
    private boolean 巴尼亞村踢箱子 = true,達瑪麗鎮踢箱子 = false,塔拉踢箱子 = false,貝塞爾鎮踢箱子 = false,奧登村踢箱子 = false,里奧內斯城踢箱子 = false,競技場踢箱子 = true,村莊踢箱子中 = false,酒館踢箱子中 = true;
    private boolean 當前金幣本 = false,當前裝備本 = false,當前材料本 = false,當前進化本 = false,當前強化本 = false,當前BOSS戰 = false,列表縮回 = false,捐獻成功 = false,捐獻錢不夠 = false
                    ,材料本選中 = false,當前日常sp任務 = false,當前活動普通本 = false,檢查藥水數量 = false,缺強化石頭 = false;
    private boolean 出售完成 = false,UR寶箱選中 = false,SSR寶箱選中 = false,SR寶箱選中 = false,R寶箱選中 = false,UC寶箱選中 = false,C寶箱選中 = false,背包騰出空間 = false,鑽石買體力 = false;
    private boolean 殲滅戰邀請識別 = false,殲滅戰邀請成功 = false,殲滅戰邀請開始 = false;
    private boolean 殲滅戰邀請紅魔 = false,殲滅戰邀請紅魔normal = false,殲滅戰邀請紅魔hard = false,殲滅戰邀請紅魔extreme = false,殲滅戰邀請紅魔hell = false;
    private boolean 殲滅戰邀請灰魔 = false,殲滅戰邀請灰魔normal = false,殲滅戰邀請灰魔hard = false,殲滅戰邀請灰魔extreme = false,殲滅戰邀請灰魔hell = false;
    private boolean 殲滅戰邀請魔獸 = false,殲滅戰邀請魔獸normal = false,殲滅戰邀請魔獸hard = false,殲滅戰邀請魔獸extreme = false,殲滅戰邀請魔獸hell = false;
    private boolean 殲滅戰邀請螃蟹 = false,殲滅戰邀請螃蟹normal = false,殲滅戰邀請螃蟹hard = false,殲滅戰邀請螃蟹extreme = false,殲滅戰邀請螃蟹hell = false;

    private String 抽卡個數Str = String.valueOf((SettingPreference.getInt("抽卡個數", 0)));
    private String 抽卡一,抽卡二,抽卡三;
    private String 保存遊戲名 = SettingPreference.getString("保存遊戲名","").replace(" ","");
    private String 保存密碼 = SettingPreference.getString("保存密碼","").replace(" ","");
    private String 引繼賬號 = SettingPreference.getString("引繼碼賬號","").replace(" ","");
    private String 引繼密碼 = SettingPreference.getString("引繼碼密碼","").replace(" ","");
    private String 卡死重啟時長 = SettingPreference.getString("卡死重啟時長","").replace(" ","");
    private String 金幣本挑戰等級 = SettingPreference.getString("金幣本等級","").replace(" ","");
    private String 金幣本挑戰隊伍 = SettingPreference.getString("金幣本選隊伍","").replace(" ","");
    private String 材料本類型 = SettingPreference.getString("材料本類型","").replace(" ","");
    private String 材料本挑戰等級 = SettingPreference.getString("材料本等級","").replace(" ","");
    private String 材料本挑戰隊伍 = SettingPreference.getString("材料本選隊伍","").replace(" ","");
    private String 村莊好感度村莊 = SettingPreference.getString("村莊好感度村莊","").replace(" ","");
    private String 村莊好感度挑戰隊伍 = SettingPreference.getString("村莊好感度隊伍","").replace(" ","");
    private String 村莊好感度買藥 = SettingPreference.getString("村莊好感度體力使用","").replace(" ","");
    private String 主線停止模式 = String.valueOf((SettingPreference.getInt("主線停止", 0)));
    private String 掛機刷圖模式 = String.valueOf(SettingPreference.getInt("掛機刷圖",0));
    private String 強化本等級 = SettingPreference.getString("強化本等級","");
    private String 強化本類型 = SettingPreference.getString("強化本類型","");
    private String 強化本方向 = "下個副本";
    private String 進化本等級 = SettingPreference.getString("進化本等級","");
    private String 進化本類型 = SettingPreference.getString("進化本類型","");
    private String 進化本方向 = "下個副本";
    private String 刷裝備類型 = SettingPreference.getString("裝備任務類型","");
    private String BOSS戰等級 = SettingPreference.getString("BOSS戰選等級","");
    private String 殲滅戰等級 = SettingPreference.getString("殲滅戰等級","");
    private String 多日未登錄獎勵 = SettingPreference.getString("多日未登錄獎勵","");
    private String 日常BOSS戰關卡 = SettingPreference.getString("日常BOSS戰選關卡","");
    private String 日常BOSS戰等級 = SettingPreference.getString("日常BOSS戰選等級","");
    private String 日常普通本類型 = SettingPreference.getString("日常普通本選類型","");
    private String 日常SP副本選本 = SettingPreference.getString("日常SP副本選本","");
    private String 日常SP副本類型 = SettingPreference.getString("日常SP副本選類型","");
    private String 日常SP副本選等級 = SettingPreference.getString("日常SP副本選等級","");
    private String 萬聖節活動殲滅戰類型 = SettingPreference.getString("萬聖節活動殲滅戰類型","").replace(" ","");
    private String 萬聖節活動殲滅戰等級 = SettingPreference.getString("萬聖節活動殲滅戰等級","").replace(" ","");
    private String 萬聖節活動普通本等級 = SettingPreference.getString("萬聖節活動普通本等級","").replace(" ","");
    private String oldRGB = "",好感度食材 = "";

    private int 設置體力藥數量 = 0,設置鑽石數量 = 0,本次踢箱子的位置 = 0,踢箱子計數 = 0,金幣本計數 = 3,材料本計數 = 3,競技場計數 = 5,再來一次計數 = 3;
    private int 抽卡個數 = 0,指定抽卡個數 = 0,實際個數 = 0,鑽石數量 = -1,體力藥數量 = -1,料理購買計數 = 0,邀請殲滅站計數 = 5,刷裝備等待 = 2;
    private int 重置任務時間 = 0,斷線重連時長 = 0,騎士團獎勵計數 = 3,祈禱領取計數 = 3,團長料理計數 = 3,擦桌子計數 = 0,上酒計數 = 0,捐錢次數 = 2;

    private long 斷線等待時間 = System.currentTimeMillis(),t_卡死重啟 = 0,卡死重啟剩餘時間 = 0,t_體力回復 = 0,t_村莊踢箱子 = 0,城鎮待命 = 0,金幣本等待 = 0,材料本等待 = 0;


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
        initColorAction(false);
        initGongGong();
        initFenJieZhuangBei();
    }

    /**
     * 刷初始操作
     * @return
     */
    private TaskAction initShuaChuShi(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("劇情頁面","skip");
        taskAction.addLayerAction("戰鬥加血頁面","選擇目標");
        taskAction.addLayerAction("對話頁面","自動","等待");
        taskAction.addLayerAction("未知頁面","選目標","確認","開始任務","指引正上","指引左上","指引左上1","指引左下","指引右上1","指引右上2","指引右下","開始任務1","等待");
        taskAction.addLayerAction("引導合卡頁面","確認");
        taskAction.addLayerAction("技能介紹頁面","確認");
        taskAction.addLayerAction("引導TAP頁面","TAP");
        taskAction.addLayerAction("引導移卡頁面",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                touchDown(261,1175);
                touchMove(361,1175);
                touchUp();
            }
        }));
        taskAction.addLayerAction("引導屬性克制頁面","確認");
        taskAction.addLayerAction("任務完成頁面","確認");
        taskAction.addLayerAction("通關成功頁面","ok","確認");
        taskAction.addLayerAction("刷圖成功頁面","ok","確認");
        taskAction.addLayerAction("等級提升頁面","確認");
        taskAction.addLayerAction("任務信息頁面","確認");
        taskAction.addLayerAction("加載頁面","等待");
        taskAction.addLayerAction("抽獎動畫頁面","skip");
        taskAction.addLayerAction("抽獎動畫頁面1","skip");
        taskAction.addLayerAction("登錄頁面",new UnitAction("點擊繼續", new UnitCallback() {
            @Override
            public boolean before() {
                抽獎完成 = false;
                检查个数完成 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("廣告頁面","關閉");
        taskAction.addLayerAction("使用條款頁面","同意");
        taskAction.addLayerAction("通知頁面","同意");
        taskAction.addLayerAction("戰鬥頁面","第一張卡");
        taskAction.addLayerAction("新玩家獎勵頁面","領取");
        taskAction.addLayerAction("日常獎勵頁面","領取");
        taskAction.addLayerAction("一次獎勵頁面","領取");
        taskAction.addLayerAction("引導選隊頁面","選R");
        taskAction.addLayerAction("設置名字頁面","確認", new UnitAction("開始輸入", new UnitCallback() {
            @Override
            public boolean before() {
                抽獎完成 = false;
                return true;
            }

            @Override
            public void after() {
                inputText(保存遊戲名);
                delay(1000);
                tap(616,1205);
            }
        }));
        taskAction.addLayerAction("引導移動頁面",new UnitAction("移動", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                touchDown(363,828);
                touchMove(363,277);
                delay(5000);
                touchUp();
            }
        }));
        taskAction.addLayerAction("引導移動頁面1",new UnitAction("移動", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                touchDown(363,828);
                touchMove(363,277);
                delay(5000);
                touchUp();
            }
        }));
        taskAction.addLayerAction("城鎮頁面","進入抽獎");
        taskAction.addLayerAction("抽獎頁面",new UnitAction("打開卡包", new UnitCallback() {
            @Override
            public boolean before() {
                if (抽獎完成){
                    抽獎完成 = false;
                    return true;
                }else {
                    return false;
                }
            }

            @Override
            public void after() {
            }
        }),"11抽");
        taskAction.addLayerAction("更新頁面","確定");
        taskAction.addLayerAction("抽獎確認頁面","確認",new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                抽獎完成 = true;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("十抽結算頁面",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                抽獎完成 = true;
                return true;
            }

            @Override
            public void after() {
            }
        }));
        taskAction.addLayerAction("酒館頁面","打开设置","進入抽獎");
        taskAction.addLayerAction("卡列表頁面",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                if (检查个数完成){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {
                Point[] points = findMultiColors(23, 128, 710, 1065,"cd70bc-050505","-6|4|ffffff-050505,-3|10|f6c9f6-050505,6|12|6c42d5-050505,18|18|5b33d2-050505,12|26|5ea6ea-050505,28|17|fff5ff-050505,32|32|81d4f8-050505",0.95f);
                if (points.length != 0){
                    實際個數 = points.length;
                    检查个数完成 = true;
                }
            }
        }),new UnitAction(抽卡一, new UnitCallback() {
            @Override
            public boolean before() {
                指定抽卡個數 --;
                return false;
            }

            @Override
            public void after() {

            }
        }),new UnitAction(抽卡二, new UnitCallback() {
            @Override
            public boolean before() {
                指定抽卡個數 --;
                return false;
            }

            @Override
            public void after() {

            }
        }),new UnitAction(抽卡三, new UnitCallback() {
            @Override
            public boolean before() {
                指定抽卡個數 --;
                return false;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("打开设置", new UnitCallback() {
            @Override
            public boolean before() {
                if (检查个数完成) {
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                检查个数完成 = false;
            }
        }));
        taskAction.addLayerAction("設置頁面","打開信息");
        taskAction.addLayerAction("信息頁面", new UnitAction("重置數據", new UnitCallback() {
            @Override
            public boolean before() {
                if ((抽卡個數 > 實際個數) || (指定抽卡個數 >= 0)) {
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }), new UnitAction("保存數據", new UnitCallback() {
            @Override
            public boolean before() {
                if (((抽卡個數 <= 實際個數) && (指定抽卡個數 <= 0)) || (實際個數 >= 3 && SettingPreference.getBoolean("自動保存",false))){
                    return true;
                }else {
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("重置確認頁面","確認");
        taskAction.addLayerAction("設置繼承碼頁面",new UnitAction("設置密碼", new UnitCallback() {
            @Override
            public boolean before() {
                if (密碼設置成功){
                    return false;
                }else {
                    return true;
                }
            }

            @Override
            public void after() {
                密碼設置成功 = true;
                inputText(保存密碼);
                delay(1000);
                tap(616,1205);
                delay(1000);
                if (實際個數 >= 3 && SettingPreference.getBoolean("自動保存",false)){
                    Shell.Sync.su("screencap -p /sdcard/Pictures/Screenshots/" + new Date().getTime() + ".png");
                }
                實際個數 = 0;
            }
        }),new UnitAction("保存", new UnitCallback() {
            @Override
            public boolean before() {
                if (密碼設置成功){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {

            }
        }),new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                密碼設置成功 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("繼承碼成功頁面",new UnitAction("確認", new UnitCallback() {
            @Override
            public boolean before() {
//                刷初始_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("獎勵頁面","確定");
        return taskAction;
    }

    /**
     * 每日一抽裝備
     * @return
     */
    private TaskAction initMeiRiChouZhuangBei(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("關閉列表", new UnitCallback() {
            @Override
            public boolean before() {
                if (!列表縮回){
                    Point point = getMscdPos();
                    swipe(point.x,point.y,698,328,2000);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                列表縮回 = true;
            }
        }),new UnitAction("打開商店", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("城鎮頁面","打開商店");
        taskAction.addLayerAction("商店頁面1","裝備","上滑");
        taskAction.addLayerAction("商店頁面4","裝備","上滑");
        taskAction.addLayerAction("商店頁面2",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                每日抽裝備_開關 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "每日抽裝備",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("商店頁面3",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                每日抽裝備_開關 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "每日抽裝備",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("裝備抽取頁面","每日一抽",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                每日抽裝備_開關 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "每日抽裝備",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("鑰匙拉動頁面",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                touchDown(359,792);
                touchMove(357,1014);
                delay(1000);
                touchUp();
            }
        }));
        taskAction.addLayerAction("抽裝備結算頁面",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                每日抽裝備_開關 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "每日抽裝備",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        return taskAction;
    }

    /**
     * 每日領寶箱
     * @return
     */
    private TaskAction initMeiRiLingBaoXiang(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("關閉列表", new UnitCallback() {
            @Override
            public boolean before() {
                if (!列表縮回){
                    Point point = getMscdPos();
                    swipe(point.x,point.y,698,328,2000);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                列表縮回 = true;
            }
        }),new UnitAction("打開商店", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("城鎮頁面","打開商店");
        taskAction.addLayerAction("商店頁面1","上滑");
        taskAction.addLayerAction("商店頁面4","上滑");
        taskAction.addLayerAction("商店頁面2","兌換",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                每日領寶箱_開關 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "每日領寶箱",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("商店頁面3","兌換",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                每日領寶箱_開關 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "每日領寶箱",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("彩幣兌換頁面","可領",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                每日領寶箱_開關 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "每日領寶箱",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("金幣兌換頁面","可領",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                每日領寶箱_開關 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "每日領寶箱",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("銀幣兌換頁面","可領",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                每日領寶箱_開關 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "每日領寶箱",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("友情幣兌換頁面","可領",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                每日領寶箱_開關 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "每日領寶箱",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        return taskAction;
    }

    /**
     * 一鑽石抽卡
     * @return
     */
    private TaskAction initYiZuanShiChouKa(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("關閉列表", new UnitCallback() {
            @Override
            public boolean before() {
                if (!列表縮回){
                    Point point = getMscdPos();
                    swipe(point.x,point.y,698,328,2000);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                列表縮回 = true;
            }
        }),new UnitAction("進入抽獎", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("城鎮頁面","進入抽獎");
        taskAction.addLayerAction("抽獎頁面","每日一抽","下一頁",
                new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                一鑽石抽獎_開關 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "一鑽石抽卡",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("一抽結算頁面","返回");
        taskAction.addLayerAction("抽獎確認頁面","確認",new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                一鑽石抽獎_開關 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "一鑽石抽卡",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("抽獎動畫頁面","skip");
        taskAction.addLayerAction("抽獎動畫頁面1","skip");
        return taskAction;
    }

    /**
     * 郵件領取
     * @return
     */
    private TaskAction initYouJianLingQu(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("酒館頁面","清算营业",
                new UnitAction("關閉列表", new UnitCallback() {
            @Override
            public boolean before() {
                if (!列表縮回){
                    Point point = getMscdPos();
                    swipe(point.x,point.y,698,328,2000);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                列表縮回 = true;
            }
        }),
                new UnitAction("打開郵件", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                郵件領取_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("城鎮頁面","打開郵件",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                郵件領取_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("通知領獎頁面","領取","打開郵件","選擇類型",new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                郵件領取_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("郵件領獎頁面","一鍵領取","選擇類型",new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                郵件領取_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("禮物領獎頁面","領取","打開郵件","選擇類型",new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                郵件領取_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("友情領獎頁面","一鍵領取","選擇類型",new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                郵件領取_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("殲滅邀請頁面","一鍵取消","邀請切換","選擇類型",new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                郵件領取_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("比試邀請頁面","一鍵取消","邀請切換","選擇類型",new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                郵件領取_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        return taskAction;
    }

    /**
     * 領取成就獎勵
     * @return
     */
    private TaskAction initChengJiuJiangLi(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("關閉列表", new UnitCallback() {
            @Override
            public boolean before() {
                if (!列表縮回){
                    Point point = getMscdPos();
                    swipe(point.x,point.y,698,328,2000);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                列表縮回 = true;
            }
        }),new UnitAction("打開任務", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("城鎮頁面","打開任務");
        taskAction.addLayerAction("故事村莊頁面","打開功績","",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                成就領取_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("故事進行中頁面","打開功績","",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                成就領取_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("每日任務頁面","打開功績",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                成就領取_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("成長功績頁面","一鍵領取","選擇類型","選擇類型1","領取鑽石",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                成就領取_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("冒險功績頁面","一鍵領取","選擇類型","選擇類型1","領取鑽石",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                成就領取_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("挑戰功績頁面","一鍵領取","選擇類型","選擇類型1","領取鑽石",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                成就領取_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("其他功績頁面","一鍵領取","選擇類型","選擇類型1","領取鑽石",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                成就領取_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("活動功績頁面","一鍵領取","選擇類型","選擇類型1","領取鑽石",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                成就領取_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("獎勵頁面","確定");
        taskAction.addLayerAction("成就獎勵鑽石頁面",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        return taskAction;
    }

    /**
     * 酒館刷親密
     * @return
     */
    private TaskAction initJiuGuanShuaQinMi(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("關閉列表", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!列表縮回){
                            Point point = getMscdPos();
                            swipe(point.x,point.y,698,328,2000);
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        列表縮回 = true;
                    }
                }),
                new UnitAction("不讓裝酒", new UnitCallback() {
                    @Override
                    public boolean before() {
                        Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "好感度裝酒",true);
                        return false;
                    }

                    @Override
                    public void after() {
                    }
                }),
                new UnitAction("喇叭", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!Setting.getBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "好感度音樂",false)){
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("裝酒", new UnitCallback() {
            @Override
            public boolean before() {
                if (Setting.getBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "好感度音樂",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "好感度裝酒",true);
            }
        }),
                new UnitAction("擦桌子", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!Setting.getBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "好感度擦桌子",false) ){
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "好感度擦桌子",true);
                    }
                }),
                new UnitAction("上酒", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!Setting.getBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "好感度上酒",false)){
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "好感度上酒",true);
                    }
                }),
                new UnitAction("料理", new UnitCallback() {
            @Override
            public boolean before() {
                if (Setting.getBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "好感度料理",false)){
                    return false;
                }else{
                    return true;
                }

            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                擦桌子計數 = 0;
                if (!Setting.getBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "好感度上酒",false) ||
                        !Setting.getBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "好感度裝酒",false) ||
                        !Setting.getBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "好感度料理",false) ||
                        !Setting.getBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "好感度擦桌子",false) ||
                        !Setting.getBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "好感度音樂",false)){
                    return true;
                }else{
                    酒館好感度_開關 = false;
                    return false;
                }
            }

            @Override
            public void after() {
                if (socketClient.isConnected()) {
                    String tem = socketClient.sendMessageResult("{\"e\":1003}");
                    Log.e("TAG", "after1: " + tem);
                    if (tem != null && tem.length() > 30){
                        NPCBean obj = new Gson().fromJson(tem, NPCBean.class);
                        if (obj.getData().size() > 0) {
                            for (int i = 0; i < obj.getData().size(); i++) {
                                if (!Setting.getBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "好感度裝酒",false) && obj.getData().get(i).getRole() == 48) {
                                    socketClient.sendMessage("{\"e\":2002,\"x\":" + obj.getData().get(i).getX() + ",\"y\":" + obj.getData().get(i).getY() + ",\"z\":" + obj.getData().get(i).getZ() + "}");
                                    delay(3000);
                                    return;
                                }
                                if (!Setting.getBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "好感度擦桌子",false) && obj.getData().get(i).getRole() == 49) {
                                    擦桌子計數 ++;
                                    if (擦桌子計數 == 4 || 擦桌子計數 == 5){
                                        socketClient.sendMessage("{\"e\":2002,\"x\":" + obj.getData().get(i).getX() + ",\"y\":" + obj.getData().get(i).getY() + ",\"z\":" + obj.getData().get(i).getZ() + "}");
                                        delay(3000);
                                        擦桌子計數 = 0;
                                        return;
                                    }
                                    obj.getData().get(i).setRole(100);
                                }
                                if (!Setting.getBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "好感度上酒",false) && obj.getData().get(i).getRole() == 49) {
                                    上酒計數 ++;
                                    if (上酒計數 == 6){
                                        socketClient.sendMessage("{\"e\":2002,\"x\":" + obj.getData().get(i).getX() + ",\"y\":" + obj.getData().get(i).getY() + ",\"z\":" + obj.getData().get(i).getZ() + "}");
                                        delay(3000);
                                        上酒計數 = 0;
                                        return;
                                    }
                                    obj.getData().get(i).setRole(100);
                                }
                                if (!Setting.getBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "好感度音樂",false) && obj.getData().get(i).getRole() == 12) {
                                    socketClient.sendMessage("{\"e\":2002,\"x\":" + obj.getData().get(i).getX() + ",\"y\":" + obj.getData().get(i).getY() + ",\"z\":" + obj.getData().get(i).getZ() + "}");
                                    delay(3000);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }));
        taskAction.addLayerAction("城鎮頁面","進入酒館");
        taskAction.addLayerAction("班料理頁面","料理","切換團長");
        taskAction.addLayerAction("彈框接任務頁面","開始");
        taskAction.addLayerAction("對話接任務頁面","領取");
        taskAction.addLayerAction("團長料理頁面",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                if (Setting.getBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "好感度料理",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }), new UnitAction("料理", new UnitCallback() {
            @Override
            public boolean before() {
                Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "好感度料理",true);
                return true;
            }

            @Override
            public void after() {

            }
        }), "有機野菜","高級雞肉","小麥粉","熏肉乾","米","肉塊","鹽","牛奶","黃油","野菜","雞蛋","蜂蜜","白糖", new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                團長料理計數 --;
                if (團長料理計數 == 0){
                    return false;
                }else {
                    return true;
                }
            }

            @Override
            public void after() {

            }
        }), new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                團長料理計數 = 3;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("音樂播放頁面",new UnitAction("APPLY", new UnitCallback() {
            @Override
            public boolean before() {
                Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "好感度音樂",true);
                return true;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("選擇", new UnitCallback() {
            @Override
            public boolean before() {
                if (Setting.getBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "好感度音樂",false)){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {

            }
        }),"返回");
        return taskAction;
    }

    /**
     * 村莊踢箱子
     * @return
     */
    private TaskAction initCunZhuangTiXiangZi(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("酒館頁面","清算营业","查看1","查看2",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                if (酒館踢箱子中){
                    return true;
                }else {
                    return false;
                }
            }

            @Override
            public void after() {
                if (socketClient.isConnected()) {
                    String tem = socketClient.sendMessageResult("{\"e\":1003}");
                    Log.e("TAG", "after1: " + tem);
                    if (tem != null && tem.length() > 30){
                        NPCBean obj = new Gson().fromJson(tem, NPCBean.class);
                        if (obj.getData().size() > 0) {
                            for (int i = 0; i < obj.getData().size(); i++) {
                                if (obj.getData().get(i).getRole() == 37 && obj.getData().get(i).isActive()) {
                                    if (本次踢箱子的位置 == i){
                                        踢箱子計數 ++;
                                        if (踢箱子計數 >= 5){
                                            tap(69,1219);
                                        }
                                    }else{
                                        本次踢箱子的位置 = i;
                                        踢箱子計數 = 0;
                                    }
                                    socketClient.sendMessage("{\"e\":2002,\"x\":" + obj.getData().get(i).getX() + ",\"y\":" + obj.getData().get(i).getY() + ",\"z\":" + obj.getData().get(i).getZ() + "}");
                                    delay(5000);
                                    return;
                                }
                            }
                        }
                        酒館踢箱子中 = false;
                    }
                }
            }
        }),new UnitAction("打開世界", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("城鎮頁面",new UnitAction("進入酒館", new UnitCallback() {
            @Override
            public boolean before() {
                if (酒館踢箱子中) {
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),"查看1","查看2",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                if (村莊踢箱子中){
                    return true;
                }else {
                    return false;
                }
            }

            @Override
            public void after() {
                if (socketClient.isConnected()) {
                    String tem = socketClient.sendMessageResult("{\"e\":1003}");
                    Log.e("TAG", "after3: " + tem);
                    if (tem != null && tem.length() > 30) {
                        NPCBean obj = new Gson().fromJson(tem, NPCBean.class);
                        if (obj.getData().size() > 0) {
                            for (int i = 0; i < obj.getData().size(); i++) {
                                if (obj.getData().get(i).getRole() == 37 && obj.getData().get(i).isActive()) {
                                    if (本次踢箱子的位置 == i){
                                        踢箱子計數 ++;
                                        if (踢箱子計數 >= 5){
                                            tap(69,1219);
                                        }
                                    }else{
                                        本次踢箱子的位置 = i;
                                        踢箱子計數 = 0;
                                    }
                                    socketClient.sendMessage("{\"e\":2002,\"x\":" + obj.getData().get(i).getX() + ",\"y\":" + obj.getData().get(i).getY() + ",\"z\":" + obj.getData().get(i).getZ() + "}");
                                    delay(5000);
                                    return;
                                }
                                if (obj.getData().get(i).getRole() == 22 && obj.getData().get(i).isActive()) {
                                    if (本次踢箱子的位置 == i){
                                        踢箱子計數 ++;
                                        if (踢箱子計數 >= 5){
                                            tap(69,1219);
                                        }
                                    }else{
                                        本次踢箱子的位置 = i;
                                        踢箱子計數 = 0;
                                    }
                                    socketClient.sendMessage("{\"e\":2002,\"x\":" + obj.getData().get(i).getX() + ",\"y\":" + obj.getData().get(i).getY() + ",\"z\":" + obj.getData().get(i).getZ() + "}");
                                    delay(5000);
                                    return;
                                }
                            }
                        }
                        村莊踢箱子中 = false;
                    }
                }
            }
        }),"打開世界");
        taskAction.addLayerAction("世界頁面",new UnitAction("點豬", new UnitCallback() {
            @Override
            public boolean before() {
                if (村莊踢箱子中){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),"打開章節");
        taskAction.addLayerAction("章節故事頁面","打開村莊");
        taskAction.addLayerAction("章節村莊頁面","左滑",new UnitAction("巴尼亞村", new UnitCallback() {
            @Override
            public boolean before() {
                if (巴尼亞村踢箱子){
                    村莊踢箱子中 = true;
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                巴尼亞村踢箱子 = false;
                達瑪麗鎮踢箱子 = true;
            }
        }),new UnitAction("達瑪麗鎮", new UnitCallback() {
            @Override
            public boolean before() {
                if (達瑪麗鎮踢箱子){
                    村莊踢箱子中 = true;
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                達瑪麗鎮踢箱子 = false;
                塔拉踢箱子 = true;
            }
        }),new UnitAction("塔拉", new UnitCallback() {
            @Override
            public boolean before() {
                if (塔拉踢箱子){
                    村莊踢箱子中 = true;
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                塔拉踢箱子 = false;
                貝塞爾鎮踢箱子 = true;
            }
        }),new UnitAction("貝塞爾鎮", new UnitCallback() {
            @Override
            public boolean before() {
                if (貝塞爾鎮踢箱子){
                    村莊踢箱子中 = true;
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                貝塞爾鎮踢箱子 = false;
                奧登村踢箱子 = true;
            }
        }),new UnitAction("奧登村", new UnitCallback() {
            @Override
            public boolean before() {
                if (奧登村踢箱子){
                    村莊踢箱子中 = true;
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                奧登村踢箱子 = false;
                里奧內斯城踢箱子 = true;
            }
        }),new UnitAction("里奧內斯城", new UnitCallback() {
            @Override
            public boolean before() {
                if (里奧內斯城踢箱子){
                    村莊踢箱子中 = true;
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                里奧內斯城踢箱子 = false;
            }
        }),new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                村莊踢箱子_開關 = false;
                t_村莊踢箱子 = System.currentTimeMillis();
                return true;
            }

            @Override
            public void after() {

            }
        }));
        return taskAction;
    }

    /**
     * 贈送友情點
     * @return
     */
    private TaskAction initSongYouQingDian(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("關閉列表", new UnitCallback() {
            @Override
            public boolean before() {
                if (!列表縮回){
                    Point point = getMscdPos();
                    swipe(point.x,point.y,698,328,2000);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                列表縮回 = true;
            }
        }),"打開設置");
        taskAction.addLayerAction("城鎮頁面","打開設置");
        taskAction.addLayerAction("設置頁面","打開好友");
        taskAction.addLayerAction("好友頁面","全部贈送", new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                贈送友情點_開關 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "贈送友情點",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        return taskAction;
    }

    /**
     * 友情點換體力藥
     * @return
     */
    private TaskAction initYouQingDianMaiYao(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮頁面","打開商店");
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("關閉列表", new UnitCallback() {
            @Override
            public boolean before() {
                if (!列表縮回){
                    Point point = getMscdPos();
                    swipe(point.x,point.y,698,328,2000);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                列表縮回 = true;
            }
        }),"打開商店");
        taskAction.addLayerAction("商店頁面1","上滑");
        taskAction.addLayerAction("商店頁面4","上滑");
        taskAction.addLayerAction("商店頁面2","幣換物");
        taskAction.addLayerAction("商店頁面3","幣換物");
        taskAction.addLayerAction("彩幣兌換頁面","友情點兌換");
        taskAction.addLayerAction("金幣兌換頁面","友情點兌換");
        taskAction.addLayerAction("銀幣兌換頁面","友情點兌換");
        taskAction.addLayerAction("友情幣兌換頁面",new UnitAction("賣光", new UnitCallback() {
            @Override
            public boolean before() {

                return false;
            }

            @Override
            public void after() {

            }
        }),"選中體力藥",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                友情點買藥_開關 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "友情點買藥",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("友情點換物頁面",new UnitAction("交換", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                MAX選中 = false;
            }
        }), new UnitAction("MAX", new UnitCallback() {
            @Override
            public boolean before() {
                if(MAX選中){
                    return false;
                }else {
                    return true;
                }
            }

            @Override
            public void after() {
                MAX選中 = true;
            }
        }),"選友情點",new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                MAX選中 = false;
                友情點買藥_開關 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "友情點買藥",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        return taskAction;
    }

    /**
     * 友情點換體力藥
     * @return
     */
    private TaskAction initXiaoFeiYouQingDian(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮頁面","打開商店");
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("關閉列表", new UnitCallback() {
            @Override
            public boolean before() {
                if (!列表縮回){
                    Point point = getMscdPos();
                    swipe(point.x,point.y,698,328,2000);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                列表縮回 = true;
            }
        }),"打開商店");
        taskAction.addLayerAction("商店頁面1","上滑");
        taskAction.addLayerAction("商店頁面4","上滑");
        taskAction.addLayerAction("商店頁面2","幣換物");
        taskAction.addLayerAction("商店頁面3","幣換物");
        taskAction.addLayerAction("彩幣兌換頁面","友情點兌換");
        taskAction.addLayerAction("金幣兌換頁面","友情點兌換");
        taskAction.addLayerAction("銀幣兌換頁面","友情點兌換");
        taskAction.addLayerAction("友情幣兌換頁面","選中體力藥","材料本道具","金幣本道具",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                消費友情點_開關 = false;
                MAX選中 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "友情點買藥",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("友情點換物頁面",new UnitAction("交換", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                MAX選中 = false;
            }
        }), new UnitAction("MAX", new UnitCallback() {
            @Override
            public boolean before() {
                if(MAX選中){
                    return false;
                }else {
                    return true;
                }
            }

            @Override
            public void after() {
                MAX選中 = true;
            }
        }),"選友情點",new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                MAX選中 = false;
                消費友情點_開關 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "友情點買藥",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        return taskAction;
    }

    /**
     * 騎士團祈禱
     * @return
     */
    private TaskAction initQiShiTuanQiDao(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮頁面","打開騎士團",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                騎士團祈禱 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "騎士團祈禱",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("關閉列表", new UnitCallback() {
            @Override
            public boolean before() {
                if (!列表縮回){
                    Point point = getMscdPos();
                    swipe(point.x,point.y,698,328,2000);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                列表縮回 = true;
            }
        }),new UnitAction("打開騎士團", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                騎士團祈禱 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "騎士團祈禱",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("騎士團聊天頁面","進入騎士團");
        taskAction.addLayerAction("騎士團內頁面","祈禱",new UnitAction("返回酒館", new UnitCallback() {
            @Override
            public boolean before() {
                騎士團祈禱 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "騎士團祈禱",true);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("祈禱頁面",new UnitAction("領取", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                祈禱領取計數 = 0;
            }
        }),new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                if (祈禱領取計數 == 0){
                    騎士團祈禱 = false;
                    Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "騎士團祈禱",true);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                祈禱領取計數 = 3;
            }
        }),new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                祈禱領取計數 --;
            }
        }));
        return taskAction;
    }

    /**
     * 騎士團獎勵
     * @return
     */
    private TaskAction initQiShiTuanJiangLi(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮頁面","打開騎士團",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                騎士團獎勵_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("關閉列表", new UnitCallback() {
            @Override
            public boolean before() {
                if (!列表縮回){
                    Point point = getMscdPos();
                    swipe(point.x,point.y,698,328,2000);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                列表縮回 = true;
            }
        }),new UnitAction("打開騎士團", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                騎士團獎勵_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("騎士團聊天頁面","進入騎士團");
        taskAction.addLayerAction("騎士團內頁面",new UnitAction("管理", new UnitCallback() {
            @Override
            public boolean before() {

                return true;
            }

            @Override
            public void after() {
                騎士團獎勵計數 = 3;
            }
        }),new UnitAction("返回酒館", new UnitCallback() {
            @Override
            public boolean before() {
                if (騎士團獎勵計數 == 0){
                    騎士團獎勵_開關 = false;
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                騎士團獎勵計數 --;
            }
        }));
        taskAction.addLayerAction("騎士團信息頁面","騎士團任務",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                騎士團獎勵_開關 = false;
                return true;
            }

            @Override
            public void after() {
            }
        }));
        taskAction.addLayerAction("騎士團成員頁面","騎士團任務",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                騎士團獎勵_開關 = false;
                return true;
            }

            @Override
            public void after() {
            }
        }));
        taskAction.addLayerAction("騎士團任務頁面","一鍵領取",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                騎士團獎勵_開關 = false;
                return true;
            }

            @Override
            public void after() {
            }
        }));
        return taskAction;
    }

    /**
     * 金幣本操作
     * @return
     */
    private TaskAction initJinBiBen(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮頁面","打開戰鬥",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                金幣本_開關 = false;
                當前金幣本 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("關閉列表", new UnitCallback() {
            @Override
            public boolean before() {
                if (!列表縮回){
                    Point point = getMscdPos();
                    swipe(point.x,point.y,698,328,2000);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                列表縮回 = true;
            }
        }),new UnitAction("打開戰鬥", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                金幣本_開關 = false;
                當前金幣本 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥內頁面","打開副本");
        taskAction.addLayerAction("副本頁面",new UnitAction("金幣本", new UnitCallback() {
            @Override
            public boolean before() {
                當前金幣本 = true;
                當前裝備本 = false;
                當前材料本 = false;
                當前強化本 = false;
                當前進化本 = false;
                當前BOSS戰 = false;
                當前日常sp任務 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                if (金幣本計數 <= 0){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {
                金幣本計數--;
            }
        }),new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                金幣本_開關 = false;
                return true;
            }

            @Override
            public void after() {
                金幣本計數 = 3;
            }
        }));
        taskAction.addLayerAction("副本選關頁面",new UnitAction("金幣本" + 金幣本挑戰等級, new UnitCallback() {
            @Override
            public boolean before() {
                if (當前金幣本){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),"上滑");
        taskAction.addLayerAction("戰鬥準備頁面",new UnitAction(金幣本挑戰隊伍, new UnitCallback() {
            @Override
            public boolean before() {
                隊伍選中 = true;
                return false;
            }

            @Override
            public void after() {

            }
        }), new UnitAction("下一隊", new UnitCallback() {
            @Override
            public boolean before() {
                if (隊伍選中){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {

            }
        }),new UnitAction("start", new UnitCallback() {
            @Override
            public boolean before() {
                if (當前金幣本){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                金幣本等待 = System.currentTimeMillis();
                當前金幣本 = false;
                當前材料本 = false;
                當前強化本 = false;
                當前進化本 = false;
                當前BOSS戰 = false;
                當前裝備本 = false;
                當前日常sp任務 = false;
                當前活動普通本 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("道具不足頁面1",new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                當前金幣本 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("道具不足頁面2",new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                當前金幣本 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥頁面","AUTO",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                再來一次計數 = 3;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("刷圖成功頁面",new UnitAction("再來一次", new UnitCallback() {
            @Override
            public boolean before() {
                if (當前金幣本){
                    if (再來一次計數 <= 0){
                        金幣本等待 = System.currentTimeMillis();
                        當前金幣本 = false;
                        當前材料本 = false;
                        當前強化本 = false;
                        當前進化本 = false;
                        當前BOSS戰 = false;
                        當前裝備本 = false;
                        當前日常sp任務 = false;
                        當前活動普通本 = false;
                        return false;
                    }else{
                        return true;
                    }
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                再來一次計數 --;
            }
        }),"ok");
        taskAction.addLayerAction("通關失敗頁面",new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                if(金幣本自動降級) {
                    if (金幣本挑戰等級.equals("第六階")) {
                        金幣本挑戰等級 = "第五階";
                    } else if (金幣本挑戰等級.equals("第五階")) {
                        金幣本挑戰等級 = "第四階";
                    } else if (金幣本挑戰等級.equals("第四階")) {
                        金幣本挑戰等級 = "第三階";
                    } else if (金幣本挑戰等級.equals("第三階")) {
                        金幣本挑戰等級 = "第二階";
                    } else if (金幣本挑戰等級.equals("第二階")) {
                        金幣本挑戰等級 = "第一階";
                    }
                }
                return true;
            }

            @Override
            public void after() {
            }
        }),
                new UnitAction("ok1", new UnitCallback() {
            @Override
            public boolean before() {
                if(金幣本自動降級) {
                    if (金幣本挑戰等級.equals("第六階")) {
                        金幣本挑戰等級 = "第五階";
                    } else if (金幣本挑戰等級.equals("第五階")) {
                        金幣本挑戰等級 = "第四階";
                    } else if (金幣本挑戰等級.equals("第四階")) {
                        金幣本挑戰等級 = "第三階";
                    } else if (金幣本挑戰等級.equals("第三階")) {
                        金幣本挑戰等級 = "第二階";
                    } else if (金幣本挑戰等級.equals("第二階")) {
                        金幣本挑戰等級 = "第一階";
                    }
                }
                return true;
            }

            @Override
            public void after() {
            }
        }),
                new UnitAction("ok2", new UnitCallback() {
            @Override
            public boolean before() {
                if(金幣本自動降級) {
                    if (金幣本挑戰等級.equals("第六階")) {
                        金幣本挑戰等級 = "第五階";
                    } else if (金幣本挑戰等級.equals("第五階")) {
                        金幣本挑戰等級 = "第四階";
                    } else if (金幣本挑戰等級.equals("第四階")) {
                        金幣本挑戰等級 = "第三階";
                    } else if (金幣本挑戰等級.equals("第三階")) {
                        金幣本挑戰等級 = "第二階";
                    } else if (金幣本挑戰等級.equals("第二階")) {
                        金幣本挑戰等級 = "第一階";
                    }
                }
                return true;
            }

            @Override
            public void after() {
            }
        }),
                new UnitAction("ok3", new UnitCallback() {
            @Override
            public boolean before() {
                if(金幣本自動降級) {
                    if (金幣本挑戰等級.equals("第六階")) {
                        金幣本挑戰等級 = "第五階";
                    } else if (金幣本挑戰等級.equals("第五階")) {
                        金幣本挑戰等級 = "第四階";
                    } else if (金幣本挑戰等級.equals("第四階")) {
                        金幣本挑戰等級 = "第三階";
                    } else if (金幣本挑戰等級.equals("第三階")) {
                        金幣本挑戰等級 = "第二階";
                    } else if (金幣本挑戰等級.equals("第二階")) {
                        金幣本挑戰等級 = "第一階";
                    }
                }
                return true;
            }

            @Override
            public void after() {
            }
        }));
        taskAction.addLayerAction("已獲得獎勵頁面","確認");
        return taskAction;
    }

    /**
     * 材料本操作
     * @return
     */
    private TaskAction initCaiLiaoBen(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮頁面","打開戰鬥",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                材料本_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("關閉列表", new UnitCallback() {
            @Override
            public boolean before() {
                if (!列表縮回){
                    Point point = getMscdPos();
                    swipe(point.x,point.y,698,328,2000);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                列表縮回 = true;
            }
        }),new UnitAction("打開戰鬥", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                材料本_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥內頁面","打開副本");
        taskAction.addLayerAction("副本頁面",new UnitAction("材料本", new UnitCallback() {
            @Override
            public boolean before() {
                當前材料本 = true;
                當前金幣本 = false;
                當前裝備本 = false;
                當前強化本 = false;
                當前進化本 = false;
                當前BOSS戰 = false;
                當前日常sp任務 = false;
                當前活動普通本 = false;
                材料本選中 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                if (材料本計數 <= 0){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {
                材料本計數--;
            }
        }),new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                材料本_開關 = false;
                return true;
            }

            @Override
            public void after() {
                材料本計數 = 3;
            }
        }));
        taskAction.addLayerAction("副本選關頁面",new UnitAction(材料本類型 + 材料本挑戰等級, new UnitCallback() {
            @Override
            public boolean before() {
                if (當前材料本){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),new UnitAction(材料本類型, new UnitCallback() {
            @Override
            public boolean before() {
                if (材料本選中){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {
                if(材料本類型.equals("強化")){
                    swipe(292,497,311,379,2000);
                }else if (材料本類型.equals("金幣")){
                    swipe(306,670,318,386,2000);
                }else{
                    swipe(340,839,358,380,2000);
                }
                材料本選中 = true;
                delay(2000);
            }
        }));
        taskAction.addLayerAction("戰鬥準備頁面",new UnitAction(材料本挑戰隊伍, new UnitCallback() {
            @Override
            public boolean before() {
                隊伍選中 = true;
                return false;
            }

            @Override
            public void after() {

            }
        }), new UnitAction("下一隊", new UnitCallback() {
            @Override
            public boolean before() {
                if (隊伍選中){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {

            }
        }),new UnitAction("start", new UnitCallback() {
            @Override
            public boolean before() {
                if (當前材料本){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
            }
        }),new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                材料本等待 = System.currentTimeMillis();
                當前金幣本 = false;
                當前材料本 = false;
                當前強化本 = false;
                當前進化本 = false;
                當前BOSS戰 = false;
                當前裝備本 = false;
                當前日常sp任務 = false;
                當前活動普通本 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("道具不足頁面2",new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                當前材料本 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("道具不足頁面1",new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                當前材料本 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥頁面","AUTO",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                再來一次計數 = 3;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("刷圖成功頁面",new UnitAction("再來一次", new UnitCallback() {
            @Override
            public boolean before() {
                if (當前材料本){
                    if (再來一次計數 <= 0){
                        材料本等待 = System.currentTimeMillis();
                        當前金幣本 = false;
                        當前材料本 = false;
                        當前強化本 = false;
                        當前進化本 = false;
                        當前BOSS戰 = false;
                        當前裝備本 = false;
                        當前日常sp任務 = false;
                        當前活動普通本 = false;
                        return false;
                    }else{
                        return true;
                    }
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                再來一次計數 --;
            }
        }),"ok");
        taskAction.addLayerAction("通關失敗頁面",new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                if(材料本自動降級) {
                    if (材料本挑戰等級.equals("第三階")) {
                        材料本挑戰等級 = "第二階";
                    } else if (材料本挑戰等級.equals("第二階")) {
                        材料本挑戰等級 = "第一階";
                    }
                }
                return true;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("ok1", new UnitCallback() {
            @Override
            public boolean before() {
                if(材料本自動降級) {
                    if (材料本挑戰等級.equals("第三階")) {
                        材料本挑戰等級 = "第二階";
                    } else if (材料本挑戰等級.equals("第二階")) {
                        材料本挑戰等級 = "第一階";
                    }
                }
                return true;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("ok2", new UnitCallback() {
            @Override
            public boolean before() {
                if(材料本自動降級) {
                    if (材料本挑戰等級.equals("第三階")) {
                        材料本挑戰等級 = "第二階";
                    } else if (材料本挑戰等級.equals("第二階")) {
                        材料本挑戰等級 = "第一階";
                    }
                }
                return true;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("ok3", new UnitCallback() {
            @Override
            public boolean before() {
                if(材料本自動降級) {
                    if (材料本挑戰等級.equals("第三階")) {
                        材料本挑戰等級 = "第二階";
                    } else if (材料本挑戰等級.equals("第二階")) {
                        材料本挑戰等級 = "第一階";
                    }
                }
                return true;
            }

            @Override
            public void after() {
            }
        }));
        taskAction.addLayerAction("已獲得獎勵頁面","確認");
        return taskAction;
    }

    /**
     * 村莊刷好感度
     * @return
     */
    private TaskAction initCunZhuangHaoGanDu(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮對話頁面",new UnitAction("skip", new UnitCallback() {
            @Override
            public boolean before() {
                城鎮待命 = 0;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("城鎮頁面",new UnitAction("打開任務", new UnitCallback() {
            @Override
            public boolean before() {
                if (System.currentTimeMillis() - 城鎮待命 >= 40*1000){
                    return true;
                }else{
                    return false;
                }

            }

            @Override
            public void after() {
            }
        }));
        taskAction.addLayerAction("酒館頁面","清算营业",
                new UnitAction("打開任務", new UnitCallback() {
            @Override
            public boolean before() {
                好感度任務 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("故事進行中頁面", 村莊好感度村莊,
                "上滑",
                new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                if (村莊好感度切換村莊){
                    if (村莊好感度村莊.equals("巴尼亞村")){
                        村莊好感度村莊 = "達瑪麗鎮";
                    }else if (村莊好感度村莊.equals("達瑪麗鎮")){
                        村莊好感度村莊 = "塔拉";
                    }else if (村莊好感度村莊.equals("塔拉")){
                        村莊好感度村莊 = "貝塞爾鎮";
                    }else if (村莊好感度村莊.equals("貝塞爾鎮")){
                        村莊好感度村莊 = "奧登村";
                    }else if (村莊好感度村莊.equals("奧登村")){
                        村莊好感度村莊 = "里奧內斯城";
                    }else{
                        村莊好感度_開關 = false;
                    }
                }else{
                    村莊好感度_開關 = false;
                }
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("故事村莊頁面",new UnitAction("好感度滿", new UnitCallback() {
            @Override
            public boolean before() {
                return false;
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                if (無好感度任務){
                    if (村莊好感度切換村莊){
                        if (村莊好感度村莊.equals("巴尼亞村")){
                            村莊好感度村莊 = "達瑪麗鎮";
                        }else if (村莊好感度村莊.equals("達瑪麗鎮")){
                            村莊好感度村莊 = "塔拉";
                        }else if (村莊好感度村莊.equals("塔拉")){
                            村莊好感度村莊 = "貝塞爾鎮";
                        }else if (村莊好感度村莊.equals("貝塞爾鎮")){
                            村莊好感度村莊 = "奧登村";
                        }else if (村莊好感度村莊.equals("奧登村")){
                            村莊好感度村莊 = "里奧內斯城";
                        }else{
                            村莊好感度_開關 = false;
                        }
                    }else{
                        村莊好感度_開關 = false;
                    }
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                無好感度任務 = false;
            }
        }),
                new UnitAction("料理接取", new UnitCallback() {
                    @Override
                    public boolean before() {
                        捐錢次數 = 2;
                        城鎮待命 = System.currentTimeMillis();
                        通關完成 = false;
                        return true;
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("料理詳情", new UnitCallback() {
                    @Override
                    public boolean before() {
                        return true;
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("領取獎勵", new UnitCallback() {
            @Override
            public boolean before() {
                捐錢次數 = 2;
                城鎮待命 = System.currentTimeMillis();
                通關完成 = false;
                好感度任務 = true;
                return true;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("領取任務", new UnitCallback() {
                    @Override
                    public boolean before() {
                        捐錢次數 = 2;
                        城鎮待命 = System.currentTimeMillis();
                        通關完成 = false;
                        好感度任務 = true;
                        return true;
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("前往", new UnitCallback() {
                    @Override
                    public boolean before() {
                        捐錢次數 = 2;
                        城鎮待命 = System.currentTimeMillis();
                        通關完成 = false;
                        好感度任務 = true;
                        return true;
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("前往1", new UnitCallback() {
                    @Override
                    public boolean before() {
                        捐錢次數 = 2;
                        城鎮待命 = System.currentTimeMillis();
                        通關完成 = false;
                        好感度任務 = true;
                        return true;
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("無任務", new UnitCallback() {
            @Override
            public boolean before() {
                無好感度任務 = true;
                通關完成 = false;
                return false;
            }

            @Override
            public void after() {

            }
        }),
                "上滑",
                "等待");
        taskAction.addLayerAction("副本選關頁面",
                "任務需要",
                "任務關卡",
                new UnitAction("返回", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (通關完成){
                            好感度任務 = false;
                            return true;
                        }else {
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                "第一階",
                "上滑");
        taskAction.addLayerAction("料理詳情頁面",new UnitAction("蛋餅", new UnitCallback() {
            @Override
            public boolean before() {
                好感度食材 = "蛋餅";
                return false;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("烤肉串", new UnitCallback() {
            @Override
            public boolean before() {
                好感度食材 = "烤肉串";
                return false;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("海鮮鍋", new UnitCallback() {
            @Override
            public boolean before() {
                好感度食材 = "海鮮鍋";
                return false;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("葡萄乾", new UnitCallback() {
            @Override
            public boolean before() {
                好感度食材 = "葡萄乾";
                return false;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("麵包片", new UnitCallback() {
            @Override
            public boolean before() {
                好感度食材 = "麵包片";
                return false;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("蛋糕", new UnitCallback() {
            @Override
            public boolean before() {
                好感度食材 = "蛋糕";
                return false;
            }

            @Override
            public void after() {

            }
        }),"獲得場所");
        taskAction.addLayerAction("料理途徑頁面",new UnitAction("前往", new UnitCallback() {
            @Override
            public boolean before() {
                城鎮待命 = System.currentTimeMillis();
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥準備頁面",new UnitAction("特訓任務", new UnitCallback() {
            @Override
            public boolean before() {
                村莊好感度特訓任務 = true;
                return false;
            }

            @Override
            public void after() {

            }
        }),new UnitAction(村莊好感度挑戰隊伍, new UnitCallback() {
            @Override
            public boolean before() {
                隊伍選中 = true;
                return false;
            }

            @Override
            public void after() {

            }
        }), new UnitAction("下一隊", new UnitCallback() {
            @Override
            public boolean before() {
                if (隊伍選中){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {

            }
        }),"start");
        taskAction.addLayerAction("入場體力不足頁面",new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                if (村莊好感度特訓任務){
                    return false;
                }
                if (村莊好感度買藥.equals("等待回復體力")) {
                    等待體力回復 = true;
                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子", false);
                    if (村莊踢箱子_開關) {
                        巴尼亞村踢箱子 = true;
                        達瑪麗鎮踢箱子 = false;
                        塔拉踢箱子 = false;
                        貝塞爾鎮踢箱子 = false;
                        奧登村踢箱子 = false;
                        里奧內斯城踢箱子 = false;
                        競技場踢箱子 = true;
                        村莊踢箱子中 = false;
                        酒館踢箱子中 = true;
                    }
                    t_體力回復 = System.currentTimeMillis();
                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("確定", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("使用藥水頁面1",new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!檢查藥水數量){
                            檢查藥水數量 = true;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Point point = getMscdPos();
                        體力藥數量 = checkNum(point);
                        if (體力藥數量 == -1){
                            體力藥數量 = 0;
                        }
                    }
                }),
                new UnitAction("關閉", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        if (村莊好感度買藥.equals("等待回復體力")) {
                            等待體力回復 = true;
                            村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子", false);
                            if (村莊踢箱子_開關) {
                                巴尼亞村踢箱子 = true;
                                達瑪麗鎮踢箱子 = false;
                                塔拉踢箱子 = false;
                                貝塞爾鎮踢箱子 = false;
                                奧登村踢箱子 = false;
                                里奧內斯城踢箱子 = false;
                                競技場踢箱子 = true;
                                村莊踢箱子中 = false;
                                酒館踢箱子中 = true;
                            }
                            t_體力回復 = System.currentTimeMillis();
                            成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("使用", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (村莊好感度特訓任務){
                            村莊好感度特訓任務 = false;
                            return true;
                        }
                        檢查藥水數量 = false;
                        switch (村莊好感度買藥) {
                            case "用鑽石購買":
                                if (鑽石數量 <= 設置鑽石數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                }else{
                                    鑽石買體力 = true;
                                }
                                return false;
                            case "用體力藥":
                                if (體力藥數量 <= 設置體力藥數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                    return false;
                                } else {
                                    體力藥數量 --;
                                    return true;
                                }
                            default:
                                等待體力回復 = true;
                                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                if (村莊踢箱子_開關){
                                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                }
                                t_體力回復 = System.currentTimeMillis();
                                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("使用藥水頁面2",new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!檢查藥水數量){
                            檢查藥水數量 = true;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Point point = getMscdPos();
                        體力藥數量 = checkNum(point);
                        if (體力藥數量 == -1){
                            體力藥數量 = 0;
                        }
                    }
                }),
                new UnitAction("關閉", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        if (村莊好感度買藥.equals("等待回復體力")) {
                            等待體力回復 = true;
                            村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子", false);
                            if (村莊踢箱子_開關) {
                                巴尼亞村踢箱子 = true;
                                達瑪麗鎮踢箱子 = false;
                                塔拉踢箱子 = false;
                                貝塞爾鎮踢箱子 = false;
                                奧登村踢箱子 = false;
                                里奧內斯城踢箱子 = false;
                                競技場踢箱子 = true;
                                村莊踢箱子中 = false;
                                酒館踢箱子中 = true;
                            }
                            t_體力回復 = System.currentTimeMillis();
                            成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("使用", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (村莊好感度特訓任務){
                            村莊好感度特訓任務 = false;
                            return true;
                        }
                        檢查藥水數量 = false;
                        switch (村莊好感度買藥) {
                            case "用鑽石購買":
                                if (鑽石數量 <= 設置鑽石數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                }else{
                                    鑽石買體力 = true;
                                }
                                return false;
                            case "用體力藥":
                                if (體力藥數量 <= 設置體力藥數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                    return false;
                                } else {
                                    體力藥數量 --;
                                    return true;
                                }
                            default:
                                等待體力回復 = true;
                                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                if (村莊踢箱子_開關){
                                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                }
                                t_體力回復 = System.currentTimeMillis();
                                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("使用藥水頁面3",new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!檢查藥水數量){
                            檢查藥水數量 = true;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Point point = getMscdPos();
                        體力藥數量 = checkNum(point);
                        if (體力藥數量 == -1){
                            體力藥數量 = 0;
                        }
                    }
                }),
                new UnitAction("關閉", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        if (村莊好感度買藥.equals("等待回復體力")) {
                            等待體力回復 = true;
                            村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子", false);
                            if (村莊踢箱子_開關) {
                                巴尼亞村踢箱子 = true;
                                達瑪麗鎮踢箱子 = false;
                                塔拉踢箱子 = false;
                                貝塞爾鎮踢箱子 = false;
                                奧登村踢箱子 = false;
                                里奧內斯城踢箱子 = false;
                                競技場踢箱子 = true;
                                村莊踢箱子中 = false;
                                酒館踢箱子中 = true;
                            }
                            t_體力回復 = System.currentTimeMillis();
                            成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("使用", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (村莊好感度特訓任務){
                            村莊好感度特訓任務 = false;
                            return true;
                        }
                        檢查藥水數量 = false;
                        switch (村莊好感度買藥) {
                            case "用鑽石購買":
                                if (鑽石數量 <= 設置鑽石數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                }else{
                                    鑽石買體力 = true;
                                }
                                return false;
                            case "用體力藥":
                                if (體力藥數量 <= 設置體力藥數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                    return false;
                                } else {
                                    體力藥數量 --;
                                    return true;
                                }
                            default:
                                等待體力回復 = true;
                                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                if (村莊踢箱子_開關){
                                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                }
                                t_體力回復 = System.currentTimeMillis();
                                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("戰鬥頁面","AUTO",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                村莊好感度特訓任務 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("刷圖成功頁面",new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                通關完成 = true;
            }
        }),"ok1");
        taskAction.addLayerAction("通關失敗頁面",new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                if (村莊刷好感失敗){
                    if (村莊好感度切換村莊){
                        if (村莊好感度村莊.equals("巴尼亞村")){
                            村莊好感度村莊 = "達瑪麗鎮";
                        }else if (村莊好感度村莊.equals("達瑪麗鎮")){
                            村莊好感度村莊 = "塔拉";
                        }else if (村莊好感度村莊.equals("塔拉")){
                            村莊好感度村莊 = "貝塞爾鎮";
                        }else if (村莊好感度村莊.equals("貝塞爾鎮")){
                            村莊好感度村莊 = "奧登村";
                        }else if (村莊好感度村莊.equals("奧登村")){
                            村莊好感度村莊 = "里奧內斯城";
                        }else{
                            村莊好感度_開關 = false;
                        }
                    }else{
                        村莊好感度_開關 = false;
                    }
                }
                return true;
            }

            @Override
            public void after() {
                通關完成 = true;
            }
        }),new UnitAction("ok1", new UnitCallback() {
            @Override
            public boolean before() {
                if (村莊刷好感失敗){
                    if (村莊好感度切換村莊){
                        if (村莊好感度村莊.equals("巴尼亞村")){
                            村莊好感度村莊 = "達瑪麗鎮";
                        }else if (村莊好感度村莊.equals("達瑪麗鎮")){
                            村莊好感度村莊 = "塔拉";
                        }else if (村莊好感度村莊.equals("塔拉")){
                            村莊好感度村莊 = "貝塞爾鎮";
                        }else if (村莊好感度村莊.equals("貝塞爾鎮")){
                            村莊好感度村莊 = "奧登村";
                        }else if (村莊好感度村莊.equals("奧登村")){
                            村莊好感度村莊 = "里奧內斯城";
                        }else{
                            村莊好感度_開關 = false;
                        }
                    }else{
                        村莊好感度_開關 = false;
                    }
                }
                return true;
            }

            @Override
            public void after() {
                通關完成 = true;
            }
        }),new UnitAction("ok2", new UnitCallback() {
            @Override
            public boolean before() {
                if (村莊刷好感失敗){
                    if (村莊好感度切換村莊){
                        if (村莊好感度村莊.equals("巴尼亞村")){
                            村莊好感度村莊 = "達瑪麗鎮";
                        }else if (村莊好感度村莊.equals("達瑪麗鎮")){
                            村莊好感度村莊 = "塔拉";
                        }else if (村莊好感度村莊.equals("塔拉")){
                            村莊好感度村莊 = "貝塞爾鎮";
                        }else if (村莊好感度村莊.equals("貝塞爾鎮")){
                            村莊好感度村莊 = "奧登村";
                        }else if (村莊好感度村莊.equals("奧登村")){
                            村莊好感度村莊 = "里奧內斯城";
                        }else{
                            村莊好感度_開關 = false;
                        }
                    }else{
                        村莊好感度_開關 = false;
                    }
                }
                return true;
            }

            @Override
            public void after() {
                通關完成 = true;
            }
        }),new UnitAction("ok3", new UnitCallback() {
            @Override
            public boolean before() {
                if (村莊刷好感失敗){
                    if (村莊好感度切換村莊){
                        if (村莊好感度村莊.equals("巴尼亞村")){
                            村莊好感度村莊 = "達瑪麗鎮";
                        }else if (村莊好感度村莊.equals("達瑪麗鎮")){
                            村莊好感度村莊 = "塔拉";
                        }else if (村莊好感度村莊.equals("塔拉")){
                            村莊好感度村莊 = "貝塞爾鎮";
                        }else if (村莊好感度村莊.equals("貝塞爾鎮")){
                            村莊好感度村莊 = "奧登村";
                        }else if (村莊好感度村莊.equals("奧登村")){
                            村莊好感度村莊 = "里奧內斯城";
                        }else{
                            村莊好感度_開關 = false;
                        }
                    }else{
                        村莊好感度_開關 = false;
                    }
                }
                return true;
            }

            @Override
            public void after() {
                通關完成 = true;
            }
        }));
        taskAction.addLayerAction("班料理頁面","料理","切換團長");
        taskAction.addLayerAction("彈框接任務頁面","開始");
        taskAction.addLayerAction("對話接任務頁面",new UnitAction("領取", new UnitCallback() {
            @Override
            public boolean before() {
                城鎮待命 = 0;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("村莊捐錢頁面",new UnitAction("加錢", new UnitCallback() {
            @Override
            public boolean before() {
                if (捐錢次數 > 0){
                    城鎮待命 = 0;
                    return true;
                }else {
                    return false;
                }
            }

            @Override
            public void after() {
                捐錢次數 --;
                捐獻成功 = false;
            }
        }),new UnitAction("錢不夠", new UnitCallback() {
            @Override
            public boolean before() {
                捐獻錢不夠 = true;
                return false;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("捐獻", new UnitCallback() {
            @Override
            public boolean before() {
                if (捐獻錢不夠){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {
                捐獻成功 = true;
            }
        }),new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                if (捐獻成功){
                    城鎮待命 = 0;
                }else {
                    if (村莊好感度切換村莊){
                        if (村莊好感度村莊.equals("巴尼亞村")){
                            村莊好感度村莊 = "達瑪麗鎮";
                        }else if (村莊好感度村莊.equals("達瑪麗鎮")){
                            村莊好感度村莊 = "塔拉";
                        }else if (村莊好感度村莊.equals("塔拉")){
                            村莊好感度村莊 = "貝塞爾鎮";
                        }else if (村莊好感度村莊.equals("貝塞爾鎮")){
                            村莊好感度村莊 = "奧登村";
                        }else if (村莊好感度村莊.equals("奧登村")){
                            村莊好感度村莊 = "里奧內斯城";
                        }else{
                            村莊好感度_開關 = false;
                        }
                    }else{
                        村莊好感度_開關 = false;
                    }
                }
                return true;
            }

            @Override
            public void after() {
            }
        }));
        taskAction.addLayerAction("團長料理頁面",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                if (料理完成){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                料理完成 = false;
            }
        }),new UnitAction("料理", new UnitCallback() {
            @Override
            public boolean before() {
                Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "好感度料理",true);
                料理完成 = true;
                return true;
            }

            @Override
            public void after() {

            }
        }),"米","肉塊","胡椒","鹽","牛奶","黃油","野菜","雞蛋","蜂蜜","白糖",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                團長料理計數 --;
                if (團長料理計數 == 0){
                    return false;
                }else {
                    return true;
                }
            }

            @Override
            public void after() {

            }
        }),new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                團長料理計數 = 3;
                購買食材_開關 = true;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("副本頁面",new UnitAction("強化本", new UnitCallback() {
            @Override
            public boolean before() {
                if (好感度任務){
                    if (通關完成){
                        return false;
                    }else {
                        return true;
                    }
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                好感度任務 = false;
            }
        }),
                new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("雜貨商店頁面","LV2",new UnitAction(好感度食材, new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }),"上滑",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                if (購買成功){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                購買成功 = false;
            }
        }));
        taskAction.addLayerAction("買入食材頁面",new UnitAction("購入", new UnitCallback() {
            @Override
            public boolean before() {
                購買成功 = true;
                return true;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                if (村莊好感度村莊0){
                    村莊好感度村莊0 = false;
                }
                if (村莊好感度村莊1){
                    村莊好感度村莊1 = false;
                }
                return true;
            }

            @Override
            public void after() {

            }
        }));
        return taskAction;
    }

    /**
     * 萬聖節活動任務
     * @return
     */
    private TaskAction initWanShengJieHuoDongRenWu(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮對話頁面",new UnitAction("skip", new UnitCallback() {
            @Override
            public boolean before() {
                城鎮待命 = 0;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("世界頁面",new UnitAction("打開任務", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
            }
        }));
        taskAction.addLayerAction("城鎮頁面",new UnitAction("打開任務", new UnitCallback() {
            @Override
            public boolean before() {
                if (System.currentTimeMillis() - 城鎮待命 >= 30*1000){
                    return true;
                }else{
                    return false;
                }

            }

            @Override
            public void after() {
            }
        }));
        taskAction.addLayerAction("酒館頁面","清算营业","打開任務");
        taskAction.addLayerAction("故事進行中頁面", "萬聖節活動",
                new UnitAction("返回", new UnitCallback() {
                    @Override
                    public boolean before() {
                        萬聖節活動主線_開關 = false;
                        return true;
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("万圣节任务页面",
                new UnitAction("前往", new UnitCallback() {
                    @Override
                    public boolean before() {
                        城鎮待命 = System.currentTimeMillis();
                        通關完成 = false;
                        return true;
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("前往1", new UnitCallback() {
                    @Override
                    public boolean before() {
                        城鎮待命 = System.currentTimeMillis();
                        通關完成 = false;
                        return true;
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("無任務", new UnitCallback() {
                    @Override
                    public boolean before() {
                        萬聖節活動主線_開關 = false;
                        通關完成 = false;
                        return false;
                    }

                    @Override
                    public void after() {

                    }
                }),
                "上滑",
                "等待");
        taskAction.addLayerAction("副本選關頁面","選關黃",
                "任務需要",
                "任務關卡",
                new UnitAction("返回", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (通關完成){
                            return true;
                        }else {
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                "上滑");
        taskAction.addLayerAction("戰鬥準備頁面","start");
        taskAction.addLayerAction("入場體力不足頁面",new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getString("活動任務體力使用","").equals("等待回復體力")) {
                    等待體力回復 = true;
                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子", false);
                    if (村莊踢箱子_開關) {
                        巴尼亞村踢箱子 = true;
                        達瑪麗鎮踢箱子 = false;
                        塔拉踢箱子 = false;
                        貝塞爾鎮踢箱子 = false;
                        奧登村踢箱子 = false;
                        里奧內斯城踢箱子 = false;
                        競技場踢箱子 = true;
                        村莊踢箱子中 = false;
                        酒館踢箱子中 = true;
                    }
                    t_體力回復 = System.currentTimeMillis();
                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("確定", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("使用藥水頁面1",new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!檢查藥水數量){
                            檢查藥水數量 = true;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Point point = getMscdPos();
                        體力藥數量 = checkNum(point);
                        if (體力藥數量 == -1){
                            體力藥數量 = 0;
                        }
                    }
                }),
                new UnitAction("關閉", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        if (SettingPreference.getString("活動任務體力使用","").equals("等待回復體力")) {
                            等待體力回復 = true;
                            村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子", false);
                            if (村莊踢箱子_開關) {
                                巴尼亞村踢箱子 = true;
                                達瑪麗鎮踢箱子 = false;
                                塔拉踢箱子 = false;
                                貝塞爾鎮踢箱子 = false;
                                奧登村踢箱子 = false;
                                里奧內斯城踢箱子 = false;
                                競技場踢箱子 = true;
                                村莊踢箱子中 = false;
                                酒館踢箱子中 = true;
                            }
                            t_體力回復 = System.currentTimeMillis();
                            成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("使用", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        switch (SettingPreference.getString("活動任務體力使用","")) {
                            case "用鑽石購買":
                                if (鑽石數量 <= 設置鑽石數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                }else{
                                    鑽石買體力 = true;
                                }
                                return false;
                            case "用體力藥":
                                if (體力藥數量 <= 設置體力藥數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                    return false;
                                } else {
                                    體力藥數量 --;
                                    return true;
                                }
                            default:
                                等待體力回復 = true;
                                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                if (村莊踢箱子_開關){
                                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                }
                                t_體力回復 = System.currentTimeMillis();
                                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("使用藥水頁面2",new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!檢查藥水數量){
                            檢查藥水數量 = true;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Point point = getMscdPos();
                        體力藥數量 = checkNum(point);
                        if (體力藥數量 == -1){
                            體力藥數量 = 0;
                        }
                    }
                }),
                new UnitAction("關閉", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        if (SettingPreference.getString("活動任務體力使用","").equals("等待回復體力")) {
                            等待體力回復 = true;
                            村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子", false);
                            if (村莊踢箱子_開關) {
                                巴尼亞村踢箱子 = true;
                                達瑪麗鎮踢箱子 = false;
                                塔拉踢箱子 = false;
                                貝塞爾鎮踢箱子 = false;
                                奧登村踢箱子 = false;
                                里奧內斯城踢箱子 = false;
                                競技場踢箱子 = true;
                                村莊踢箱子中 = false;
                                酒館踢箱子中 = true;
                            }
                            t_體力回復 = System.currentTimeMillis();
                            成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("使用", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        switch (SettingPreference.getString("活動任務體力使用","")) {
                            case "用鑽石購買":
                                if (鑽石數量 <= 設置鑽石數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                }else{
                                    鑽石買體力 = true;
                                }
                                return false;
                            case "用體力藥":
                                if (體力藥數量 <= 設置體力藥數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                    return false;
                                } else {
                                    體力藥數量 --;
                                    return true;
                                }
                            default:
                                等待體力回復 = true;
                                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                if (村莊踢箱子_開關){
                                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                }
                                t_體力回復 = System.currentTimeMillis();
                                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("使用藥水頁面3",new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!檢查藥水數量){
                            檢查藥水數量 = true;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Point point = getMscdPos();
                        體力藥數量 = checkNum(point);
                        if (體力藥數量 == -1){
                            體力藥數量 = 0;
                        }
                    }
                }),
                new UnitAction("關閉", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        if (SettingPreference.getString("活動任務體力使用","").equals("等待回復體力")) {
                            等待體力回復 = true;
                            村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子", false);
                            if (村莊踢箱子_開關) {
                                巴尼亞村踢箱子 = true;
                                達瑪麗鎮踢箱子 = false;
                                塔拉踢箱子 = false;
                                貝塞爾鎮踢箱子 = false;
                                奧登村踢箱子 = false;
                                里奧內斯城踢箱子 = false;
                                競技場踢箱子 = true;
                                村莊踢箱子中 = false;
                                酒館踢箱子中 = true;
                            }
                            t_體力回復 = System.currentTimeMillis();
                            成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("使用", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        switch (SettingPreference.getString("活動任務體力使用","")) {
                            case "用鑽石購買":
                                if (鑽石數量 <= 設置鑽石數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                }else{
                                    鑽石買體力 = true;
                                }
                                return false;
                            case "用體力藥":
                                if (體力藥數量 <= 設置體力藥數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                    return false;
                                } else {
                                    體力藥數量 --;
                                    return true;
                                }
                            default:
                                等待體力回復 = true;
                                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                if (村莊踢箱子_開關){
                                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                }
                                t_體力回復 = System.currentTimeMillis();
                                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }));

        taskAction.addLayerAction("戰鬥頁面","AUTO",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥頁面1","大招","金卡","銀卡","銅卡","等待");
        taskAction.addLayerAction("刷圖成功頁面",new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                通關完成 = true;
            }
        }),"ok1");
        taskAction.addLayerAction("通關失敗頁面",new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                萬聖節活動主線_開關 = false;
                return true;
            }

            @Override
            public void after() {
                通關完成 = true;
            }
        }),new UnitAction("ok1", new UnitCallback() {
            @Override
            public boolean before() {
                萬聖節活動主線_開關 = false;
                return true;
            }

            @Override
            public void after() {
                通關完成 = true;
            }
        }),new UnitAction("ok2", new UnitCallback() {
            @Override
            public boolean before() {
                萬聖節活動主線_開關 = false;
                return true;
            }

            @Override
            public void after() {
                通關完成 = true;
            }
        }),new UnitAction("ok3", new UnitCallback() {
            @Override
            public boolean before() {
                萬聖節活動主線_開關 = false;
                return true;
            }

            @Override
            public void after() {
                通關完成 = true;
            }
        }));
        return taskAction;
    }

    /**
     * 萬聖節活動殲滅戰
     * @return
     */
    private TaskAction initWanShengJieHuoDongJianMieZhan(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮對話頁面",new UnitAction("skip", new UnitCallback() {
            @Override
            public boolean before() {
                城鎮待命 = 0;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("世界頁面","萬聖殲滅戰","打開任務");
        taskAction.addLayerAction("城鎮頁面","萬聖殲滅戰",new UnitAction("打開任務", new UnitCallback() {
            @Override
            public boolean before() {
                if (System.currentTimeMillis() - 城鎮待命 >= 30*1000){
                    return true;
                }else{
                    return false;
                }

            }

            @Override
            public void after() {
            }
        }));
        taskAction.addLayerAction("酒館頁面","清算营业","打開任務");
        taskAction.addLayerAction("故事進行中頁面", "萬聖節活動",
                new UnitAction("返回", new UnitCallback() {
                    @Override
                    public boolean before() {
                        萬聖節活動殲滅戰_開關 = false;
                        return true;
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("万圣节任务页面", "萬聖地圖");
        taskAction.addLayerAction("進入萬聖殲滅頁面","ok");
        taskAction.addLayerAction("萬聖紅魔頁面",new UnitAction("灰魔", new UnitCallback() {
            @Override
            public boolean before() {
                if (萬聖節活動殲滅戰類型.equals("灰魔") || 萬聖節活動殲滅戰類型.equals("魔獸")){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),萬聖節活動殲滅戰等級);
        taskAction.addLayerAction("萬聖灰魔頁面",new UnitAction("紅魔", new UnitCallback() {
            @Override
            public boolean before() {
                if (萬聖節活動殲滅戰類型.equals("紅魔")){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),new UnitAction("魔獸", new UnitCallback() {
            @Override
            public boolean before() {
                if (萬聖節活動殲滅戰類型.equals("魔獸")){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),萬聖節活動殲滅戰等級);
        taskAction.addLayerAction("萬聖魔獸頁面",new UnitAction("灰魔", new UnitCallback() {
            @Override
            public boolean before() {
                if (萬聖節活動殲滅戰類型.equals("紅魔") || 萬聖節活動殲滅戰類型.equals("灰魔")){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),萬聖節活動殲滅戰等級);
        taskAction.addLayerAction("萬聖殲滅準備頁面",new UnitAction(SettingPreference.getString("活動殲滅戰隊伍",""), new UnitCallback() {
            @Override
            public boolean before() {
                隊伍選中 = true;
                return false;
            }

            @Override
            public void after() {

            }
        }), new UnitAction("下一隊", new UnitCallback() {
            @Override
            public boolean before() {
                if (隊伍選中){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {

            }
        }),"start");
        taskAction.addLayerAction("戰鬥頁面","AUTO",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("萬聖任務完成頁面","ok");
        taskAction.addLayerAction("刷圖成功頁面",new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                通關完成 = true;
            }
        }),"ok1");
        taskAction.addLayerAction("通關失敗頁面",new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                萬聖節活動殲滅戰_開關 = false;
                return true;
            }

            @Override
            public void after() {
                通關完成 = true;
            }
        }),new UnitAction("ok1", new UnitCallback() {
            @Override
            public boolean before() {
                萬聖節活動殲滅戰_開關 = false;
                return true;
            }

            @Override
            public void after() {
                通關完成 = true;
            }
        }),new UnitAction("ok2", new UnitCallback() {
            @Override
            public boolean before() {
                萬聖節活動殲滅戰_開關 = false;
                return true;
            }

            @Override
            public void after() {
                通關完成 = true;
            }
        }),new UnitAction("ok3", new UnitCallback() {
            @Override
            public boolean before() {
                萬聖節活動殲滅戰_開關 = false;
                return true;
            }

            @Override
            public void after() {
                通關完成 = true;
            }
        }));
        taskAction.addLayerAction("萬聖次數限制頁面",new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                萬聖節活動殲滅戰_開關 = false;
                return true;
            }

            @Override
            public void after() {
                通關完成 = true;
            }
        }));
        return taskAction;
    }

    /**
     * 萬聖節活動普通本
     * @return
     */
    private TaskAction initWanShengJieHuoDongPuTongBen(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮對話頁面",new UnitAction("skip", new UnitCallback() {
            @Override
            public boolean before() {
                城鎮待命 = 0;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("世界頁面","萬聖殲滅戰","打開任務");
        taskAction.addLayerAction("城鎮頁面",new UnitAction(萬聖節活動普通本等級, new UnitCallback() {
            @Override
            public boolean before() {
                if (System.currentTimeMillis() - 城鎮待命 >= 30*1000){
                    再來一次計數 = 3;
                    return true;
                }else{
                    return false;
                }

            }

            @Override
            public void after() {
                城鎮待命 = System.currentTimeMillis();
            }
        }),new UnitAction("萬聖殲滅戰", new UnitCallback() {
            @Override
            public boolean before() {
                if (System.currentTimeMillis() - 城鎮待命 >= 30*1000){
                    return true;
                }else{
                    return false;
                }

            }

            @Override
            public void after() {
            }
        }),new UnitAction("打開任務", new UnitCallback() {
            @Override
            public boolean before() {
                if (System.currentTimeMillis() - 城鎮待命 >= 30*1000){
                    return true;
                }else{
                    return false;
                }

            }

            @Override
            public void after() {
            }
        }));
        taskAction.addLayerAction("酒館頁面","清算营业","打開任務");
        taskAction.addLayerAction("故事進行中頁面", "萬聖節活動",
                new UnitAction("返回", new UnitCallback() {
                    @Override
                    public boolean before() {
                        萬聖節活動殲滅戰_開關 = false;
                        return true;
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("万圣节任务页面", "萬聖地圖");
        taskAction.addLayerAction("未知頁面",new UnitAction("主線開戰", new UnitCallback() {
            @Override
            public boolean before() {
                if (再來一次計數 <= 0){
                    萬聖節活動普通本_開關 = false;
                    return false;
                }else{
                    當前活動普通本 = true;
                    當前日常sp任務 = false;
                    當前裝備本 = false;
                    當前BOSS戰 = false;
                    當前進化本 = false;
                    當前強化本 = false;
                    當前材料本 = false;
                    當前金幣本 = false;
                    return true;
                }
            }

            @Override
            public void after() {
                再來一次計數 --;
            }
        }),"以後再說");
        taskAction.addLayerAction("戰鬥準備頁面",new UnitAction(SettingPreference.getString("活動普通本隊伍",""), new UnitCallback() {
            @Override
            public boolean before() {
                隊伍選中 = true;
                return false;
            }

            @Override
            public void after() {

            }
        }), new UnitAction("下一隊", new UnitCallback() {
            @Override
            public boolean before() {
                if (隊伍選中){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {

            }
        }),"start");
        taskAction.addLayerAction("入場體力不足頁面",new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getString("活動普通本體力使用","").equals("等待回復體力")) {
                    等待體力回復 = true;
                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子", false);
                    if (村莊踢箱子_開關) {
                        巴尼亞村踢箱子 = true;
                        達瑪麗鎮踢箱子 = false;
                        塔拉踢箱子 = false;
                        貝塞爾鎮踢箱子 = false;
                        奧登村踢箱子 = false;
                        里奧內斯城踢箱子 = false;
                        競技場踢箱子 = true;
                        村莊踢箱子中 = false;
                        酒館踢箱子中 = true;
                    }
                    t_體力回復 = System.currentTimeMillis();
                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("確定", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("使用藥水頁面1",new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!檢查藥水數量){
                            檢查藥水數量 = true;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Point point = getMscdPos();
                        體力藥數量 = checkNum(point);
                        if (體力藥數量 == -1){
                            體力藥數量 = 0;
                        }
                    }
                }),
                new UnitAction("關閉", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        if (SettingPreference.getString("活動普通本體力使用","").equals("等待回復體力")) {
                            等待體力回復 = true;
                            村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子", false);
                            if (村莊踢箱子_開關) {
                                巴尼亞村踢箱子 = true;
                                達瑪麗鎮踢箱子 = false;
                                塔拉踢箱子 = false;
                                貝塞爾鎮踢箱子 = false;
                                奧登村踢箱子 = false;
                                里奧內斯城踢箱子 = false;
                                競技場踢箱子 = true;
                                村莊踢箱子中 = false;
                                酒館踢箱子中 = true;
                            }
                            t_體力回復 = System.currentTimeMillis();
                            成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("使用", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        switch (SettingPreference.getString("活動普通本體力使用","")) {
                            case "用鑽石購買":
                                if (鑽石數量 <= 設置鑽石數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                }else{
                                    鑽石買體力 = true;
                                }
                                return false;
                            case "用體力藥":
                                if (體力藥數量 <= 設置體力藥數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                    return false;
                                } else {
                                    體力藥數量 --;
                                    return true;
                                }
                            default:
                                等待體力回復 = true;
                                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                if (村莊踢箱子_開關){
                                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                }
                                t_體力回復 = System.currentTimeMillis();
                                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("使用藥水頁面2",new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!檢查藥水數量){
                            檢查藥水數量 = true;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Point point = getMscdPos();
                        體力藥數量 = checkNum(point);
                        if (體力藥數量 == -1){
                            體力藥數量 = 0;
                        }
                    }
                }),
                new UnitAction("關閉", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        if (SettingPreference.getString("活動普通本體力使用","").equals("等待回復體力")) {
                            等待體力回復 = true;
                            村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子", false);
                            if (村莊踢箱子_開關) {
                                巴尼亞村踢箱子 = true;
                                達瑪麗鎮踢箱子 = false;
                                塔拉踢箱子 = false;
                                貝塞爾鎮踢箱子 = false;
                                奧登村踢箱子 = false;
                                里奧內斯城踢箱子 = false;
                                競技場踢箱子 = true;
                                村莊踢箱子中 = false;
                                酒館踢箱子中 = true;
                            }
                            t_體力回復 = System.currentTimeMillis();
                            成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("使用", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        switch (SettingPreference.getString("活動普通本體力使用","")) {
                            case "用鑽石購買":
                                if (鑽石數量 <= 設置鑽石數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                }else{
                                    鑽石買體力 = true;
                                }
                                return false;
                            case "用體力藥":
                                if (體力藥數量 <= 設置體力藥數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                    return false;
                                } else {
                                    體力藥數量 --;
                                    return true;
                                }
                            default:
                                等待體力回復 = true;
                                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                if (村莊踢箱子_開關){
                                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                }
                                t_體力回復 = System.currentTimeMillis();
                                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("使用藥水頁面3",new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!檢查藥水數量){
                            檢查藥水數量 = true;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Point point = getMscdPos();
                        體力藥數量 = checkNum(point);
                        if (體力藥數量 == -1){
                            體力藥數量 = 0;
                        }
                    }
                }),
                new UnitAction("關閉", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        if (SettingPreference.getString("活動普通本體力使用","").equals("等待回復體力")) {
                            等待體力回復 = true;
                            村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子", false);
                            if (村莊踢箱子_開關) {
                                巴尼亞村踢箱子 = true;
                                達瑪麗鎮踢箱子 = false;
                                塔拉踢箱子 = false;
                                貝塞爾鎮踢箱子 = false;
                                奧登村踢箱子 = false;
                                里奧內斯城踢箱子 = false;
                                競技場踢箱子 = true;
                                村莊踢箱子中 = false;
                                酒館踢箱子中 = true;
                            }
                            t_體力回復 = System.currentTimeMillis();
                            成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("使用", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        switch (SettingPreference.getString("活動普通本體力使用","")) {
                            case "用鑽石購買":
                                if (鑽石數量 <= 設置鑽石數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                }else{
                                    鑽石買體力 = true;
                                }
                                return false;
                            case "用體力藥":
                                if (體力藥數量 <= 設置體力藥數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                    return false;
                                } else {
                                    體力藥數量 --;
                                    return true;
                                }
                            default:
                                等待體力回復 = true;
                                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                if (村莊踢箱子_開關){
                                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                }
                                t_體力回復 = System.currentTimeMillis();
                                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("戰鬥頁面","AUTO",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                再來一次計數 = 3;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("刷圖成功頁面",new UnitAction("再來一次", new UnitCallback() {
            @Override
            public boolean before() {
                if (當前活動普通本){
                    if (再來一次計數 <= 0){
                        萬聖節活動普通本_開關 = false;
                        當前活動普通本 = false;
                        return false;
                    }else {
                        return true;
                    }
                }else{
                    return false;
                }

            }

            @Override
            public void after() {
                再來一次計數 --;
            }
        }),new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                通關完成 = true;
            }
        }),new UnitAction("ok1", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                通關完成 = true;
            }
        }));
        taskAction.addLayerAction("通關失敗頁面",new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                萬聖節活動普通本_開關 = false;
                return true;
            }

            @Override
            public void after() {
                通關完成 = true;
            }
        }),new UnitAction("ok1", new UnitCallback() {
            @Override
            public boolean before() {
                萬聖節活動普通本_開關 = false;
                return true;
            }

            @Override
            public void after() {
                通關完成 = true;
            }
        }),new UnitAction("ok2", new UnitCallback() {
            @Override
            public boolean before() {
                萬聖節活動普通本_開關 = false;
                return true;
            }

            @Override
            public void after() {
                通關完成 = true;
            }
        }),new UnitAction("ok3", new UnitCallback() {
            @Override
            public boolean before() {
                萬聖節活動普通本_開關 = false;
                return true;
            }

            @Override
            public void after() {
                通關完成 = true;
            }
        }));
        return taskAction;
    }

    /**
     * 購買食材
     * @return
     */
    private TaskAction initGouMaiShiCai(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("關閉列表", new UnitCallback() {
            @Override
            public boolean before() {
                if (!列表縮回){
                    Point point = getMscdPos();
                    swipe(point.x,point.y,698,328,2000);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                列表縮回 = true;
            }
        }),new UnitAction("打開世界", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("城鎮頁面",new UnitAction("雜貨", new UnitCallback() {
            @Override
            public boolean before() {
                if (買食材選好村莊){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),"打開世界");
        taskAction.addLayerAction("世界頁面",new UnitAction("點豬", new UnitCallback() {
            @Override
            public boolean before() {
                if (買食材選好村莊){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),"打開章節");
        taskAction.addLayerAction("章節故事頁面","打開村莊");
        taskAction.addLayerAction("章節村莊頁面","左滑",new UnitAction("巴尼亞村", new UnitCallback() {
            @Override
            public boolean before() {
                買食材選好村莊 = true;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("雜貨商店頁面","LV1",new UnitAction("肉塊", new UnitCallback() {
            @Override
            public boolean before() {
                if (肉塊購買){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {
                肉塊購買 = true;
            }
        }), new UnitAction("野菜", new UnitCallback() {
            @Override
            public boolean before() {
                if (野菜購買){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {
                野菜購買 = true;
            }
        }), new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                肉塊購買 = false;
                野菜購買 = false;
                買食材選好村莊 = false;
                購買食材_開關 = false;
            }
        }));
        taskAction.addLayerAction("買入食材頁面","購入","關閉");
        return taskAction;
    }

    /**
     * 半自動主線
     * @return
     */
    private TaskAction initBanZiDongZhuXian(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮頁面","打開任務");
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("關閉列表", new UnitCallback() {
            @Override
            public boolean before() {
                if (!列表縮回){
                    Point point = getMscdPos();
                    swipe(point.x,point.y,698,328,2000);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                列表縮回 = true;
            }
        }),new UnitAction("打開任務", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("故事進行中頁面","主線");
        taskAction.addLayerAction("競技場頁面","打開任務");
        taskAction.addLayerAction("副本選關頁面","選關紅","選關黃","返回");
        taskAction.addLayerAction("主線戰鬥準備頁面","start");
        taskAction.addLayerAction("戰鬥準備頁面",new UnitAction("自動編成", new UnitCallback() {
            @Override
            public boolean before() {
                if (主線自動編成){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {
                主線自動編成 = true;
            }
        }),new UnitAction("戰力不足", new UnitCallback() {
            @Override
            public boolean before() {
                if (主線停止模式.equals("0")){
                    半自動主線_開關 = false;
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),new UnitAction("start", new UnitCallback() {
            @Override
            public boolean before() {
                主線自動編成 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥準備頁面1",new UnitAction("自動編成", new UnitCallback() {
            @Override
            public boolean before() {
                if (主線自動編成){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {
                主線自動編成 = true;
            }
        }),new UnitAction("戰力不足", new UnitCallback() {
            @Override
            public boolean before() {
                if (主線停止模式.equals("0")){
                    半自動主線_開關 = false;
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),new UnitAction("start", new UnitCallback() {
            @Override
            public boolean before() {
                主線自動編成 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("通關失敗頁面",new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                if (主線停止模式.equals("1")){
                    半自動主線_開關 = false;
                }
                return true;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("ok1", new UnitCallback() {
            @Override
            public boolean before() {
                if (主線停止模式.equals("1")){
                    半自動主線_開關 = false;
                }
                return true;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("ok2", new UnitCallback() {
            @Override
            public boolean before() {
                if (主線停止模式.equals("1")){
                    半自動主線_開關 = false;
                }
                return true;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("ok3", new UnitCallback() {
            @Override
            public boolean before() {
                if (主線停止模式.equals("1")){
                    半自動主線_開關 = false;
                }
                return true;
            }

            @Override
            public void after() {
            }
        }));
        taskAction.addLayerAction("入場體力不足頁面",new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getString("半主線體力使用","").equals("等待回復體力")){
                    等待體力回復 = true;
                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                    if (村莊踢箱子_開關){
                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                    }
                    t_體力回復 = System.currentTimeMillis();
                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }), new UnitAction("確定", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("使用藥水頁面2",new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!檢查藥水數量){
                            檢查藥水數量 = true;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Point point = getMscdPos();
                        體力藥數量 = checkNum(point);
                        if (體力藥數量 == -1){
                            體力藥數量 = 0;
                        }
                    }
                }),
                new UnitAction("關閉", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        if (SettingPreference.getString("半主線體力使用","").equals("等待回復體力")) {
                            等待體力回復 = true;
                            村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子", false);
                            if (村莊踢箱子_開關) {
                                巴尼亞村踢箱子 = true;
                                達瑪麗鎮踢箱子 = false;
                                塔拉踢箱子 = false;
                                貝塞爾鎮踢箱子 = false;
                                奧登村踢箱子 = false;
                                里奧內斯城踢箱子 = false;
                                競技場踢箱子 = true;
                                村莊踢箱子中 = false;
                                酒館踢箱子中 = true;
                            }
                            t_體力回復 = System.currentTimeMillis();
                            成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("使用", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        switch (SettingPreference.getString("半主線體力使用","")) {
                            case "用鑽石購買":
                                if (鑽石數量 <= 設置鑽石數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                }else{
                                    鑽石買體力 = true;
                                }
                                return false;
                            case "用體力藥":
                                if (體力藥數量 <= 設置體力藥數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                    return false;
                                } else {
                                    體力藥數量 --;
                                    return true;
                                }
                            default:
                                等待體力回復 = true;
                                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                if (村莊踢箱子_開關){
                                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                }
                                t_體力回復 = System.currentTimeMillis();
                                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("使用藥水頁面1",new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!檢查藥水數量){
                            檢查藥水數量 = true;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Point point = getMscdPos();
                        體力藥數量 = checkNum(point);
                        if (體力藥數量 == -1){
                            體力藥數量 = 0;
                        }
                    }
                }),
                new UnitAction("關閉", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        if (SettingPreference.getString("半主線體力使用","").equals("等待回復體力")) {
                            等待體力回復 = true;
                            村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子", false);
                            if (村莊踢箱子_開關) {
                                巴尼亞村踢箱子 = true;
                                達瑪麗鎮踢箱子 = false;
                                塔拉踢箱子 = false;
                                貝塞爾鎮踢箱子 = false;
                                奧登村踢箱子 = false;
                                里奧內斯城踢箱子 = false;
                                競技場踢箱子 = true;
                                村莊踢箱子中 = false;
                                酒館踢箱子中 = true;
                            }
                            t_體力回復 = System.currentTimeMillis();
                            成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("使用", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        switch (SettingPreference.getString("半主線體力使用","")) {
                            case "用鑽石購買":
                                if (鑽石數量 <= 設置鑽石數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                }else{
                                    鑽石買體力 = true;
                                }
                                return false;
                            case "用體力藥":
                                if (體力藥數量 <= 設置體力藥數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                    return false;
                                } else {
                                    體力藥數量 --;
                                    return true;
                                }
                            default:
                                等待體力回復 = true;
                                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                if (村莊踢箱子_開關){
                                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                }
                                t_體力回復 = System.currentTimeMillis();
                                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("使用藥水頁面3",new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!檢查藥水數量){
                            檢查藥水數量 = true;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Point point = getMscdPos();
                        體力藥數量 = checkNum(point);
                        if (體力藥數量 == -1){
                            體力藥數量 = 0;
                        }
                    }
                }),
                new UnitAction("關閉", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        if (SettingPreference.getString("半主線體力使用","").equals("等待回復體力")) {
                            等待體力回復 = true;
                            村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子", false);
                            if (村莊踢箱子_開關) {
                                巴尼亞村踢箱子 = true;
                                達瑪麗鎮踢箱子 = false;
                                塔拉踢箱子 = false;
                                貝塞爾鎮踢箱子 = false;
                                奧登村踢箱子 = false;
                                里奧內斯城踢箱子 = false;
                                競技場踢箱子 = true;
                                村莊踢箱子中 = false;
                                酒館踢箱子中 = true;
                            }
                            t_體力回復 = System.currentTimeMillis();
                            成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("使用", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        switch (SettingPreference.getString("半主線體力使用","")) {
                            case "用鑽石購買":
                                if (鑽石數量 <= 設置鑽石數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                }else{
                                    鑽石買體力 = true;
                                }
                                return false;
                            case "用體力藥":
                                if (體力藥數量 <= 設置體力藥數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                    return false;
                                } else {
                                    體力藥數量 --;
                                    return true;
                                }
                            default:
                                等待體力回復 = true;
                                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                if (村莊踢箱子_開關){
                                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                }
                                t_體力回復 = System.currentTimeMillis();
                                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("戰鬥頁面1","大招","金卡","銀卡","銅卡","等待");
        taskAction.addLayerAction("戰鬥頁面","AUTO","等待");
        return taskAction;
    }

    /**
     * 每日任務
     * @return
     */
    private TaskAction initMeiRiRenWu(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮頁面","打開任務");
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("關閉列表", new UnitCallback() {
            @Override
            public boolean before() {
                if (!列表縮回){
                    Point point = getMscdPos();
                    swipe(point.x,point.y,698,328,2000);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                列表縮回 = true;
            }
        }),new UnitAction("打開任務", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("故事進行中頁面","打開日常");
        taskAction.addLayerAction("故事村莊頁面","打開日常");
        taskAction.addLayerAction("每日任務頁面","領取獎勵",
                new UnitAction("材料本", new UnitCallback() {
            @Override
            public boolean before() {
                日常材料本_開關 = true;
                return true;
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("普通本", new UnitCallback() {
            @Override
            public boolean before() {
                if(每日普通本不能刷){
                    return false;
                }else{
                    日常普通本_開關 = true;
                    return true;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("裝備強化", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getBoolean("日常強化任務",false) && !缺強化石頭){
                    日常強化任務_開關 = true;
                    return true;
                }else{
                    日常強化任務_開關 = false;
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("製作料理", new UnitCallback() {
            @Override
            public boolean before() {
                if (!無食材){
                    日常製作料理_開關 = true;
                    return true;
                }else{
                    日常製作料理_開關 = false;
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("BOSS戰", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (SettingPreference.getBoolean("日常BOSS戰",false) && !每日BOSS戰無法選擇){
                            日常BOSS戰_開關 = true;
                            return true;
                        }else{
                            日常BOSS戰_開關 = false;
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("PVP戰鬥", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (SettingPreference.getBoolean("日常競技任務",false)){
                            日常PVP戰鬥_開關 = true;
                            return true;
                        }else{
                            日常PVP戰鬥_開關 = false;
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),"上滑",new UnitAction("返回", new UnitCallback() {
                    @Override
                    public boolean before() {
                        Setting.putBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "每日任務",true);
                        return true;
                    }

                    @Override
                    public void after() {

                    }
                }));
        return taskAction;
    }

    /**
     * 每日任務材料本
     * @return
     */
    private TaskAction initMeiRiCaiLiaoBen(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮頁面","打開戰鬥",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                日常材料本_開關 = false;
                當前材料本 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("關閉列表", new UnitCallback() {
            @Override
            public boolean before() {
                if (!列表縮回){
                    Point point = getMscdPos();
                    swipe(point.x,point.y,698,328,2000);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                列表縮回 = true;
            }
        }),new UnitAction("打開戰鬥", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                日常材料本_開關 = false;
                當前材料本 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥內頁面","打開副本");
        taskAction.addLayerAction("副本頁面",new UnitAction(日常SP副本類型 + 日常SP副本選本 + "本", new UnitCallback() {
            @Override
            public boolean before() {
                當前日常sp任務 = true;
                當前裝備本 = false;
                當前材料本 = false;
                當前金幣本 = false;
                當前強化本 = false;
                當前進化本 = false;
                當前BOSS戰 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }),進化本方向,new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                if (進化本方向.equals("下個副本")){
                    進化本方向 = "上個副本";
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("副本選關頁面",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                if (當前日常sp任務){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {

            }
        }),new UnitAction(日常SP副本類型 + 日常SP副本選本 + 日常SP副本選等級, new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }),"上滑");
        taskAction.addLayerAction("戰鬥準備頁面",new UnitAction("start", new UnitCallback() {
            @Override
            public boolean before() {
                if (當前日常sp任務){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("入場體力不足頁面",new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                等待體力回復 = true;
                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                if (村莊踢箱子_開關){
                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                }
                t_體力回復 = System.currentTimeMillis();
                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥頁面","AUTO","等待");
        taskAction.addLayerAction("刷圖成功頁面",new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                日常材料本_開關 = false;
                當前日常sp任務 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("通關失敗頁面",new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("ok1", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("ok2", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("ok3", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
            }
        }));
        taskAction.addLayerAction("已獲得獎勵頁面","確認");
        return taskAction;
    }

    /**
     * 每日任務普通本
     * @return
     */
    private TaskAction initMeiRiPuTongBen(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮頁面","打開世界");
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("關閉列表", new UnitCallback() {
            @Override
            public boolean before() {
                if (!列表縮回){
                    Point point = getMscdPos();
                    swipe(point.x,point.y,698,328,2000);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                列表縮回 = true;
            }
        }),new UnitAction("打開世界", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("世界頁面","打開戰鬥");
        taskAction.addLayerAction("戰鬥內頁面","打開裝備副本");
        taskAction.addLayerAction("刷裝備選擇頁面",日常普通本類型,"領取","開始",new UnitAction("前往", new UnitCallback() {
            @Override
            public boolean before() {
                當前裝備本 = true;
                當前材料本 = false;
                當前金幣本 = false;
                當前強化本 = false;
                當前進化本 = false;
                當前BOSS戰 = false;
                當前日常sp任務 = false;
                當前活動普通本 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }), new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                if (日常普通本類型.equals("回復")){
                    日常普通本類型 = "心眼";
                }else if (日常普通本類型.equals("心眼")){
                    日常普通本類型 = "集中";
                }else if (日常普通本類型.equals("集中")){
                    日常普通本類型 = "生命";
                }else if (日常普通本類型.equals("生命")){
                    日常普通本類型 = "鐵壁";
                }else if (日常普通本類型.equals("鐵壁")){
                    日常普通本類型 = "猛攻";
                }else{
                    每日普通本不能刷 = true;
                    日常普通本_開關 = false;
                }
                if (每日普通本不能刷){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("副本選關頁面",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                if (每日普通本不能刷){
                    return false;
                }else{
                    return !當前裝備本;
                }
            }

            @Override
            public void after() {

            }
        }),"hard","normal","easy");
        taskAction.addLayerAction("戰鬥準備頁面","start");
        taskAction.addLayerAction("入場體力不足頁面",new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                等待體力回復 = true;
                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                if (村莊踢箱子_開關){
                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                }
                t_體力回復 = System.currentTimeMillis();
                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥頁面","AUTO","等待");
        taskAction.addLayerAction("刷圖成功頁面",new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                日常普通本_開關 = false;
                當前裝備本 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("班料理頁面","料理","任務要求","返回");
        taskAction.addLayerAction("通關失敗頁面",new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("ok1", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("ok2", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("ok3", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
            }
        }));
        taskAction.addLayerAction("已獲得獎勵頁面","確認");
        return taskAction;
    }

    /**
     * 每日任務裝備強化
     * @return
     */
    private TaskAction initMeiRiZhuangBeiQiangHua(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮頁面","打開卡包");
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("關閉列表", new UnitCallback() {
            @Override
            public boolean before() {
                if (!列表縮回){
                    Point point = getMscdPos();
                    swipe(point.x,point.y,698,328,2000);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                列表縮回 = true;
            }
        }),new UnitAction("打開卡包", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("卡列表頁面","UR","SSR","SR",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                日常強化任務_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("卡詳情頁面","打開裝備",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                日常強化任務_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("卡裝備頁面","強化","選SSR","選SR","選R","選UC","選C","上滑",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                日常強化任務_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("裝備強化頁面","連續強化","選5次","強化",new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                日常強化任務_開關 = false;
                缺強化石頭 = true;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("強化結果頁面","關閉");
        return taskAction;
    }

    /**
     * 每日任務製作料理
     * @return
     */
    private TaskAction initMeiRiZhiZuoLiaoLI(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮頁面","進入酒館");
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("關閉列表", new UnitCallback() {
            @Override
            public boolean before() {
                if (!列表縮回){
                    Point point = getMscdPos();
                    swipe(point.x,point.y,698,328,2000);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                列表縮回 = true;
            }
        }),new UnitAction("料理", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                日常製作料理_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("班料理頁面","料理","切換團長");
        taskAction.addLayerAction("彈框接任務頁面","開始");
        taskAction.addLayerAction("對話接任務頁面","領取");
        taskAction.addLayerAction("團長料理頁面",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                if (料理完成){
                    日常製作料理_開關 = false;
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                料理完成 = false;
            }
        }),new UnitAction("料理", new UnitCallback() {
            @Override
            public boolean before() {
                料理完成 = true;
                return true;
            }

            @Override
            public void after() {

            }
        }),"有機野菜","高級雞肉","小麥粉","熏肉乾","米","肉塊","胡椒","鹽","牛奶","黃油","野菜","雞蛋","蜂蜜","白糖",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                團長料理計數 --;
                if (團長料理計數 == 0){
                    return false;
                }else {
                    return true;
                }
            }

            @Override
            public void after() {

            }
        }),new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                團長料理計數 = 3;
                日常製作料理_開關 = false;
                無食材 = true;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        return taskAction;
    }

    /**
     * 日常BOSS戰
     * @return
     */
    private TaskAction initMeiRiBOSSZhan(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮頁面","打開戰鬥",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                日常BOSS戰_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("關閉列表", new UnitCallback() {
            @Override
            public boolean before() {
                if (!列表縮回){
                    Point point = getMscdPos();
                    swipe(point.x,point.y,698,328,2000);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                列表縮回 = true;
            }
        }),new UnitAction("打開戰鬥", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                日常BOSS戰_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥內頁面",new UnitAction("打開BOSS戰", new UnitCallback() {
            @Override
            public boolean before() {
                當前BOSS戰 = true;
                當前裝備本 = false;
                當前材料本 = false;
                當前金幣本 = false;
                當前強化本 = false;
                當前進化本 = false;
                當前日常sp任務 = false;
                當前活動普通本 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("BOSS戰選關頁面",日常BOSS戰關卡,new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                每日BOSS戰無法選擇 = true;
                日常BOSS戰_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("副本選關頁面",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                if (當前BOSS戰){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {

            }
        }),日常BOSS戰等級,new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                每日BOSS戰無法選擇 = true;
                日常BOSS戰_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥準備頁面",new UnitAction("start", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }), "返回");
        taskAction.addLayerAction("入場體力不足頁面",new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                等待體力回復 = true;
                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                if (村莊踢箱子_開關){
                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                }
                t_體力回復 = System.currentTimeMillis();
                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥頁面","AUTO","等待");
        taskAction.addLayerAction("刷圖成功頁面",new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                日常BOSS戰_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("通關失敗頁面",new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                if (日常BOSS戰等級.equals("extreme")){
                    日常BOSS戰等級 = "hard";
                }else{
                    日常BOSS戰等級 = "normal";
                }
                return true;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("ok1", new UnitCallback() {
            @Override
            public boolean before() {
                if (日常BOSS戰等級.equals("extreme")){
                    日常BOSS戰等級 = "hard";
                }else{
                    日常BOSS戰等級 = "normal";
                }
                return true;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("ok2", new UnitCallback() {
            @Override
            public boolean before() {
                if (日常BOSS戰等級.equals("extreme")){
                    日常BOSS戰等級 = "hard";
                }else{
                    日常BOSS戰等級 = "normal";
                }
                return true;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("ok3", new UnitCallback() {
            @Override
            public boolean before() {
                if (日常BOSS戰等級.equals("extreme")){
                    日常BOSS戰等級 = "hard";
                }else{
                    日常BOSS戰等級 = "normal";
                }
                return true;
            }

            @Override
            public void after() {
            }
        }));
        taskAction.addLayerAction("已獲得獎勵頁面","確認");
        return taskAction;
    }

    /**
     * 日常PVP
     * @return
     */
    private TaskAction initMeiRIPVP(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮頁面","打開戰鬥",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                日常PVP戰鬥_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("打開戰鬥", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                if (競技場計數 <= 0){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {
                競技場計數 --;
            }
        }));
        taskAction.addLayerAction("戰鬥內頁面","打開PVP",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                日常PVP戰鬥_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("競技場頁面","競技一般",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                if (競技場計數 <= 0){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {
                競技場計數 --;
            }
        }),new UnitAction("進入酒館", new UnitCallback() {
            @Override
            public boolean before() {
                日常PVP戰鬥_開關 = false;
                return true;
            }

            @Override
            public void after() {
                競技場計數 = 5;
            }
        }));
        taskAction.addLayerAction("PVP一般頁面",new UnitAction("檢索", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("PVP上級頁面","一般");
        taskAction.addLayerAction("PVP確認頁面","檢索","返回");
        taskAction.addLayerAction("PVP戰鬥頁面","AUTO","等待");
        taskAction.addLayerAction("PVP勝利頁面",new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                日常PVP戰鬥_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("PVP失敗頁面",new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                日常PVP戰鬥_開關 = false;
                return true;
            }

            @Override
            public void after() {
            }
        }));
        return taskAction;
    }

    /**
     * 掛機刷強化本
     * @return
     */
    private TaskAction initShuaTuQiangHuaBen(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮頁面","打開戰鬥",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("關閉列表", new UnitCallback() {
            @Override
            public boolean before() {
                if (!列表縮回){
                    Point point = getMscdPos();
                    swipe(point.x,point.y,698,328,2000);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                列表縮回 = true;
            }
        }),new UnitAction("打開戰鬥", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥內頁面","打開副本");
        taskAction.addLayerAction("副本頁面",new UnitAction(強化本類型 + "強化本", new UnitCallback() {
            @Override
            public boolean before() {
                當前強化本 = true;
                當前裝備本 = false;
                當前材料本 = false;
                當前金幣本 = false;
                當前進化本 = false;
                當前BOSS戰 = false;
                當前活動普通本 = false;
                當前日常sp任務 = false;
                強化本方向 = "下個副本";
                return true;
            }

            @Override
            public void after() {

            }
        }),強化本方向,new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                if (強化本方向.equals("下個副本")){
                    強化本方向 = "上個副本";
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),"返回");
        taskAction.addLayerAction("副本選關頁面",new UnitAction(強化本類型 + "強化" + 強化本等級, new UnitCallback() {
            @Override
            public boolean before() {
                if (當前強化本){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),"上滑","返回");
        taskAction.addLayerAction("戰鬥準備頁面",new UnitAction(SettingPreference.getString("強化本選隊伍",""), new UnitCallback() {
            @Override
            public boolean before() {
                隊伍選中 = true;
                return false;
            }

            @Override
            public void after() {

            }
        }), new UnitAction("下一隊", new UnitCallback() {
            @Override
            public boolean before() {
                if (隊伍選中){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {

            }
        }),"start","返回");
        taskAction.addLayerAction("入場體力不足頁面",new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getString("強化本體力使用","").equals("等待回復體力")){
                    等待體力回復 = true;
                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                    if (村莊踢箱子_開關){
                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                    }
                    t_體力回復 = System.currentTimeMillis();
                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("確定", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("使用藥水頁面1",new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!檢查藥水數量){
                            檢查藥水數量 = true;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Point point = getMscdPos();
                        體力藥數量 = checkNum(point);
                        if (體力藥數量 == -1){
                            體力藥數量 = 0;
                        }
                    }
                }),
                new UnitAction("關閉", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        if (SettingPreference.getString("強化本體力使用","").equals("等待回復體力")) {
                            等待體力回復 = true;
                            村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子", false);
                            if (村莊踢箱子_開關) {
                                巴尼亞村踢箱子 = true;
                                達瑪麗鎮踢箱子 = false;
                                塔拉踢箱子 = false;
                                貝塞爾鎮踢箱子 = false;
                                奧登村踢箱子 = false;
                                里奧內斯城踢箱子 = false;
                                競技場踢箱子 = true;
                                村莊踢箱子中 = false;
                                酒館踢箱子中 = true;
                            }
                            t_體力回復 = System.currentTimeMillis();
                            成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("使用", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        switch (SettingPreference.getString("強化本體力使用","")) {
                            case "用鑽石購買":
                                if (鑽石數量 <= 設置鑽石數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                }else{
                                    鑽石買體力 = true;
                                }
                                return false;
                            case "用體力藥":
                                if (體力藥數量 <= 設置體力藥數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                    return false;
                                } else {
                                    體力藥數量 --;
                                    return true;
                                }
                            default:
                                等待體力回復 = true;
                                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                if (村莊踢箱子_開關){
                                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                }
                                t_體力回復 = System.currentTimeMillis();
                                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("使用藥水頁面2",new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!檢查藥水數量){
                            檢查藥水數量 = true;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Point point = getMscdPos();
                        體力藥數量 = checkNum(point);
                        if (體力藥數量 == -1){
                            體力藥數量 = 0;
                        }
                    }
                }),
                new UnitAction("關閉", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        if (SettingPreference.getString("強化本體力使用","").equals("等待回復體力")) {
                            等待體力回復 = true;
                            村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子", false);
                            if (村莊踢箱子_開關) {
                                巴尼亞村踢箱子 = true;
                                達瑪麗鎮踢箱子 = false;
                                塔拉踢箱子 = false;
                                貝塞爾鎮踢箱子 = false;
                                奧登村踢箱子 = false;
                                里奧內斯城踢箱子 = false;
                                競技場踢箱子 = true;
                                村莊踢箱子中 = false;
                                酒館踢箱子中 = true;
                            }
                            t_體力回復 = System.currentTimeMillis();
                            成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("使用", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        switch (SettingPreference.getString("強化本體力使用","")) {
                            case "用鑽石購買":
                                if (鑽石數量 <= 設置鑽石數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                }else{
                                    鑽石買體力 = true;
                                }
                                return false;
                            case "用體力藥":
                                if (體力藥數量 <= 設置體力藥數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                    return false;
                                } else {
                                    體力藥數量 --;
                                    return true;
                                }
                            default:
                                等待體力回復 = true;
                                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                if (村莊踢箱子_開關){
                                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                }
                                t_體力回復 = System.currentTimeMillis();
                                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("使用藥水頁面3",new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!檢查藥水數量){
                            檢查藥水數量 = true;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Point point = getMscdPos();
                        體力藥數量 = checkNum(point);
                        if (體力藥數量 == -1){
                            體力藥數量 = 0;
                        }
                    }
                }),
                new UnitAction("關閉", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        if (SettingPreference.getString("強化本體力使用","").equals("等待回復體力")) {
                            等待體力回復 = true;
                            村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子", false);
                            if (村莊踢箱子_開關) {
                                巴尼亞村踢箱子 = true;
                                達瑪麗鎮踢箱子 = false;
                                塔拉踢箱子 = false;
                                貝塞爾鎮踢箱子 = false;
                                奧登村踢箱子 = false;
                                里奧內斯城踢箱子 = false;
                                競技場踢箱子 = true;
                                村莊踢箱子中 = false;
                                酒館踢箱子中 = true;
                            }
                            t_體力回復 = System.currentTimeMillis();
                            成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("使用", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        switch (SettingPreference.getString("強化本體力使用","")) {
                            case "用鑽石購買":
                                if (鑽石數量 <= 設置鑽石數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                }else{
                                    鑽石買體力 = true;
                                }
                                return false;
                            case "用體力藥":
                                if (體力藥數量 <= 設置體力藥數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                    return false;
                                } else {
                                    體力藥數量 --;
                                    return true;
                                }
                            default:
                                等待體力回復 = true;
                                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                if (村莊踢箱子_開關){
                                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                }
                                t_體力回復 = System.currentTimeMillis();
                                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("戰鬥頁面","AUTO","等待");
        taskAction.addLayerAction("刷圖成功頁面",new UnitAction("再來一次", new UnitCallback() {
            @Override
            public boolean before() {
                if (當前強化本){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                當前強化本 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("通關失敗頁面",new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                if(SettingPreference.getBoolean("強化本智能降階",false)) {
                    if (強化本等級.equals("第六階")) {
                        強化本等級 = "第五階";
                    } else if (強化本等級.equals("第五階")) {
                        強化本等級 = "第四階";
                    }else if (強化本等級.equals("第四階")) {
                        強化本等級 = "第三階";
                    }else if (強化本等級.equals("第三階")) {
                        強化本等級 = "第二階";
                    }else if (強化本等級.equals("第二階")) {
                        強化本等級 = "第一階";
                    }
                }
                return true;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("ok1", new UnitCallback() {
            @Override
            public boolean before() {
                if(SettingPreference.getBoolean("強化本智能降階",false)) {
                    if (強化本等級.equals("第六階")) {
                        強化本等級 = "第五階";
                    } else if (強化本等級.equals("第五階")) {
                        強化本等級 = "第四階";
                    }else if (強化本等級.equals("第四階")) {
                        強化本等級 = "第三階";
                    }else if (強化本等級.equals("第三階")) {
                        強化本等級 = "第二階";
                    }else if (強化本等級.equals("第二階")) {
                        強化本等級 = "第一階";
                    }
                }
                return true;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("ok2", new UnitCallback() {
            @Override
            public boolean before() {
                if(SettingPreference.getBoolean("強化本智能降階",false)) {
                    if (強化本等級.equals("第六階")) {
                        強化本等級 = "第五階";
                    } else if (強化本等級.equals("第五階")) {
                        強化本等級 = "第四階";
                    }else if (強化本等級.equals("第四階")) {
                        強化本等級 = "第三階";
                    }else if (強化本等級.equals("第三階")) {
                        強化本等級 = "第二階";
                    }else if (強化本等級.equals("第二階")) {
                        強化本等級 = "第一階";
                    }
                }
                return true;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("ok3", new UnitCallback() {
            @Override
            public boolean before() {
                if(SettingPreference.getBoolean("強化本智能降階",false)) {
                    if (強化本等級.equals("第六階")) {
                        強化本等級 = "第五階";
                    } else if (強化本等級.equals("第五階")) {
                        強化本等級 = "第四階";
                    }else if (強化本等級.equals("第四階")) {
                        強化本等級 = "第三階";
                    }else if (強化本等級.equals("第三階")) {
                        強化本等級 = "第二階";
                    }else if (強化本等級.equals("第二階")) {
                        強化本等級 = "第一階";
                    }
                }
                return true;
            }

            @Override
            public void after() {
            }
        }));
        taskAction.addLayerAction("已獲得獎勵頁面","確認");
        return taskAction;
    }

    /**
     * 掛機刷進化本
     * @return
     */
    private TaskAction initShuaTuJinHuaBen(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮頁面","打開戰鬥",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("關閉列表", new UnitCallback() {
            @Override
            public boolean before() {
                if (!列表縮回){
                    Point point = getMscdPos();
                    swipe(point.x,point.y,698,328,2000);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                列表縮回 = true;
            }
        }),new UnitAction("打開戰鬥", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥內頁面","打開副本");
        taskAction.addLayerAction("副本頁面",new UnitAction(進化本類型 + "進化本", new UnitCallback() {
            @Override
            public boolean before() {
                當前進化本 = true;
                當前裝備本 = false;
                當前材料本 = false;
                當前金幣本 = false;
                當前強化本 = false;
                當前BOSS戰 = false;
                當前日常sp任務 = false;
                當前活動普通本 = false;
                進化本方向 = "下個副本";
                return true;
            }

            @Override
            public void after() {

            }
        }),進化本方向,new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                if (進化本方向.equals("下個副本")){
                    進化本方向 = "上個副本";
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),"返回");
        taskAction.addLayerAction("副本選關頁面",new UnitAction(進化本類型 + "進化" +進化本等級, new UnitCallback() {
            @Override
            public boolean before() {
                if (當前進化本){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),"上滑","","返回");
        taskAction.addLayerAction("戰鬥準備頁面",new UnitAction(SettingPreference.getString("進化本選隊伍",""), new UnitCallback() {
            @Override
            public boolean before() {
                隊伍選中 = true;
                return false;
            }

            @Override
            public void after() {

            }
        }), new UnitAction("下一隊", new UnitCallback() {
            @Override
            public boolean before() {
                if (隊伍選中){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {

            }
        }),"start","返回");
        taskAction.addLayerAction("入場體力不足頁面",
                new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getString("進化本體力使用","").equals("等待回復體力")){
                    等待體力回復 = true;
                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                    if (村莊踢箱子_開關){
                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                    }
                    t_體力回復 = System.currentTimeMillis();
                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("確定", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("使用藥水頁面1",new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!檢查藥水數量){
                            檢查藥水數量 = true;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Point point = getMscdPos();
                        體力藥數量 = checkNum(point);
                        if (體力藥數量 == -1){
                            體力藥數量 = 0;
                        }
                    }
                }),
                new UnitAction("關閉", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        if (SettingPreference.getString("進化本體力使用","").equals("等待回復體力")) {
                            等待體力回復 = true;
                            村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子", false);
                            if (村莊踢箱子_開關) {
                                巴尼亞村踢箱子 = true;
                                達瑪麗鎮踢箱子 = false;
                                塔拉踢箱子 = false;
                                貝塞爾鎮踢箱子 = false;
                                奧登村踢箱子 = false;
                                里奧內斯城踢箱子 = false;
                                競技場踢箱子 = true;
                                村莊踢箱子中 = false;
                                酒館踢箱子中 = true;
                            }
                            t_體力回復 = System.currentTimeMillis();
                            成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("使用", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        switch (SettingPreference.getString("進化本體力使用","")) {
                            case "用鑽石購買":
                                if (鑽石數量 <= 設置鑽石數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                }else{
                                    鑽石買體力 = true;
                                }
                                return false;
                            case "用體力藥":
                                if (體力藥數量 <= 設置體力藥數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                    return false;
                                } else {
                                    體力藥數量 --;
                                    return true;
                                }
                            default:
                                等待體力回復 = true;
                                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                if (村莊踢箱子_開關){
                                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                }
                                t_體力回復 = System.currentTimeMillis();
                                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("使用藥水頁面2",new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!檢查藥水數量){
                            檢查藥水數量 = true;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Point point = getMscdPos();
                        體力藥數量 = checkNum(point);
                        if (體力藥數量 == -1){
                            體力藥數量 = 0;
                        }
                    }
                }),
                new UnitAction("關閉", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        if (SettingPreference.getString("進化本體力使用","").equals("等待回復體力")) {
                            等待體力回復 = true;
                            村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子", false);
                            if (村莊踢箱子_開關) {
                                巴尼亞村踢箱子 = true;
                                達瑪麗鎮踢箱子 = false;
                                塔拉踢箱子 = false;
                                貝塞爾鎮踢箱子 = false;
                                奧登村踢箱子 = false;
                                里奧內斯城踢箱子 = false;
                                競技場踢箱子 = true;
                                村莊踢箱子中 = false;
                                酒館踢箱子中 = true;
                            }
                            t_體力回復 = System.currentTimeMillis();
                            成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("使用", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        switch (SettingPreference.getString("進化本體力使用","")) {
                            case "用鑽石購買":
                                if (鑽石數量 <= 設置鑽石數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                }else{
                                    鑽石買體力 = true;
                                }
                                return false;
                            case "用體力藥":
                                if (體力藥數量 <= 設置體力藥數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                    return false;
                                } else {
                                    體力藥數量 --;
                                    return true;
                                }
                            default:
                                等待體力回復 = true;
                                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                if (村莊踢箱子_開關){
                                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                }
                                t_體力回復 = System.currentTimeMillis();
                                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("使用藥水頁面3",new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!檢查藥水數量){
                            檢查藥水數量 = true;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Point point = getMscdPos();
                        體力藥數量 = checkNum(point);
                        if (體力藥數量 == -1){
                            體力藥數量 = 0;
                        }
                    }
                }),
                new UnitAction("關閉", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        if (SettingPreference.getString("進化本體力使用","").equals("等待回復體力")) {
                            等待體力回復 = true;
                            村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子", false);
                            if (村莊踢箱子_開關) {
                                巴尼亞村踢箱子 = true;
                                達瑪麗鎮踢箱子 = false;
                                塔拉踢箱子 = false;
                                貝塞爾鎮踢箱子 = false;
                                奧登村踢箱子 = false;
                                里奧內斯城踢箱子 = false;
                                競技場踢箱子 = true;
                                村莊踢箱子中 = false;
                                酒館踢箱子中 = true;
                            }
                            t_體力回復 = System.currentTimeMillis();
                            成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("使用", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        switch (SettingPreference.getString("進化本體力使用","")) {
                            case "用鑽石購買":
                                if (鑽石數量 <= 設置鑽石數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                }else{
                                    鑽石買體力 = true;
                                }
                                return false;
                            case "用體力藥":
                                if (體力藥數量 <= 設置體力藥數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                    return false;
                                } else {
                                    體力藥數量 --;
                                    return true;
                                }
                            default:
                                等待體力回復 = true;
                                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                if (村莊踢箱子_開關){
                                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                }
                                t_體力回復 = System.currentTimeMillis();
                                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("戰鬥頁面","AUTO","等待");
        taskAction.addLayerAction("刷圖成功頁面",new UnitAction("再來一次", new UnitCallback() {
            @Override
            public boolean before() {
                if (當前進化本){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }), new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                當前進化本 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("通關失敗頁面",new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                if(SettingPreference.getBoolean("進化本智能降階",false)) {
                    if (進化本等級.equals("第六階")) {
                        進化本等級 = "第五階";
                    } else if (進化本等級.equals("第五階")) {
                        進化本等級 = "第四階";
                    }else if (進化本等級.equals("第四階")) {
                        進化本等級 = "第三階";
                    }else if (進化本等級.equals("第三階")) {
                        進化本等級 = "第二階";
                    }else if (進化本等級.equals("第二階")) {
                        進化本等級 = "第一階";
                    }
                }
                return true;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("ok1", new UnitCallback() {
            @Override
            public boolean before() {
                if(SettingPreference.getBoolean("進化本智能降階",false)) {
                    if (進化本等級.equals("第六階")) {
                        進化本等級 = "第五階";
                    } else if (進化本等級.equals("第五階")) {
                        進化本等級 = "第四階";
                    }else if (進化本等級.equals("第四階")) {
                        進化本等級 = "第三階";
                    }else if (進化本等級.equals("第三階")) {
                        進化本等級 = "第二階";
                    }else if (進化本等級.equals("第二階")) {
                        進化本等級 = "第一階";
                    }
                }
                return true;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("ok2", new UnitCallback() {
            @Override
            public boolean before() {
                if(SettingPreference.getBoolean("進化本智能降階",false)) {
                    if (進化本等級.equals("第六階")) {
                        進化本等級 = "第五階";
                    } else if (進化本等級.equals("第五階")) {
                        進化本等級 = "第四階";
                    }else if (進化本等級.equals("第四階")) {
                        進化本等級 = "第三階";
                    }else if (進化本等級.equals("第三階")) {
                        進化本等級 = "第二階";
                    }else if (進化本等級.equals("第二階")) {
                        進化本等級 = "第一階";
                    }
                }
                return true;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("ok3", new UnitCallback() {
            @Override
            public boolean before() {
                if(SettingPreference.getBoolean("進化本智能降階",false)) {
                    if (進化本等級.equals("第六階")) {
                        進化本等級 = "第五階";
                    } else if (進化本等級.equals("第五階")) {
                        進化本等級 = "第四階";
                    }else if (進化本等級.equals("第四階")) {
                        進化本等級 = "第三階";
                    }else if (進化本等級.equals("第三階")) {
                        進化本等級 = "第二階";
                    }else if (進化本等級.equals("第二階")) {
                        進化本等級 = "第一階";
                    }
                }
                return true;
            }

            @Override
            public void after() {
            }
        }));
        taskAction.addLayerAction("已獲得獎勵頁面","確認");

        return taskAction;
    }

    /**
     * 刷圖裝備
     * @return
     */
    private TaskAction initShuaTuZhuangBei(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮頁面","打開戰鬥",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("關閉列表", new UnitCallback() {
            @Override
            public boolean before() {
                if (!列表縮回){
                    Point point = getMscdPos();
                    swipe(point.x,point.y,698,328,2000);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                列表縮回 = true;
            }
        }),new UnitAction("打開世界", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("世界頁面",new UnitAction("打開戰鬥", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥內頁面","打開裝備副本");
        taskAction.addLayerAction("刷裝備選擇頁面",刷裝備類型,"領取","開始",new UnitAction("前往", new UnitCallback() {
            @Override
            public boolean before() {
                當前裝備本 = true;
                當前材料本 = false;
                當前金幣本 = false;
                當前強化本 = false;
                當前進化本 = false;
                當前BOSS戰 = false;
                當前日常sp任務 = false;
                當前活動普通本 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                掛機刷圖模式 = "-1";
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("副本選關頁面",new UnitAction("裝備完成", new UnitCallback() {
            @Override
            public boolean before() {
                裝備任務完成 = true;
                return false;
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("每日任務完成", new UnitCallback() {
            @Override
            public boolean before() {
                每日任務領取 = true;
                return false;
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                if (!當前裝備本){
                    return true;
                }else{
                    if (每日任務領取 || 裝備任務完成){
                        return true;
                    }else{
                        return false;
                    }
                }
            }

            @Override
            public void after() {
                裝備任務完成 = false;
            }
        }),"裝備本需要","裝備hard","裝備normal","返回");
        taskAction.addLayerAction("戰鬥準備頁面",new UnitAction(SettingPreference.getString("裝備任務選隊伍",""), new UnitCallback() {
            @Override
            public boolean before() {
                隊伍選中 = true;
                return false;
            }

            @Override
            public void after() {

            }
        }), new UnitAction("下一隊", new UnitCallback() {
            @Override
            public boolean before() {
                if (隊伍選中){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {

            }
        }),"start","返回");
        taskAction.addLayerAction("入場體力不足頁面",
                new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getString("裝備任務體力使用","").equals("等待回復體力")){
                    等待體力回復 = true;
                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                    if (村莊踢箱子_開關){
                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                    }
                    t_體力回復 = System.currentTimeMillis();
                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("確定", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("使用藥水頁面1",
                new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!檢查藥水數量){
                            檢查藥水數量 = true;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Point point = getMscdPos();
                        體力藥數量 = checkNum(point);
                        if (體力藥數量 == -1){
                            體力藥數量 = 0;
                        }
                    }
                }),
                new UnitAction("關閉", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        if (SettingPreference.getString("裝備任務體力使用","").equals("等待回復體力")){
                            等待體力回復 = true;
                            村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                            if (村莊踢箱子_開關){
                                巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                            }
                            t_體力回復 = System.currentTimeMillis();
                            成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("使用", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        switch (SettingPreference.getString("裝備任務體力使用", "")) {
                            case "用鑽石購買":
                                if (鑽石數量 <= 設置鑽石數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                }else{
                                    鑽石買體力 = true;
                                }
                                return false;
                            case "用體力藥":
                                if (體力藥數量 <= 設置體力藥數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                    return false;
                                } else {
                                    體力藥數量 --;
                                    return true;
                                }
                            default:
                                等待體力回復 = true;
                                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                if (村莊踢箱子_開關){
                                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                }
                                t_體力回復 = System.currentTimeMillis();
                                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("使用藥水頁面2",
                new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!檢查藥水數量){
                            檢查藥水數量 = true;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Point point = getMscdPos();
                        體力藥數量 = checkNum(point);
                        if (體力藥數量 == -1){
                            體力藥數量 = 0;
                        }
                    }
                }),
                new UnitAction("關閉", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        if (SettingPreference.getString("裝備任務體力使用","").equals("等待回復體力")){
                            等待體力回復 = true;
                            村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                            if (村莊踢箱子_開關){
                                巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                            }
                            t_體力回復 = System.currentTimeMillis();
                            成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("使用", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        switch (SettingPreference.getString("裝備任務體力使用", "")) {
                            case "用鑽石購買":
                                if (鑽石數量 <= 設置鑽石數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                }else{
                                    鑽石買體力 = true;
                                }
                                return false;
                            case "用體力藥":
                                if (體力藥數量 <= 設置體力藥數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                    return false;
                                } else {
                                    體力藥數量 --;
                                    return true;
                                }
                            default:
                                等待體力回復 = true;
                                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                if (村莊踢箱子_開關){
                                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                }
                                t_體力回復 = System.currentTimeMillis();
                                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("使用藥水頁面3",
                new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!檢查藥水數量){
                            檢查藥水數量 = true;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Point point = getMscdPos();
                        體力藥數量 = checkNum(point);
                        if (體力藥數量 == -1){
                            體力藥數量 = 0;
                        }
                    }
                }),
                new UnitAction("關閉", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        if (SettingPreference.getString("裝備任務體力使用","").equals("等待回復體力")){
                            等待體力回復 = true;
                            村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                            if (村莊踢箱子_開關){
                                巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                            }
                            t_體力回復 = System.currentTimeMillis();
                            成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("使用", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        switch (SettingPreference.getString("裝備任務體力使用", "")) {
                            case "用鑽石購買":
                                if (鑽石數量 <= 設置鑽石數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                }else{
                                    鑽石買體力 = true;
                                }
                                return false;
                            case "用體力藥":
                                if (體力藥數量 <= 設置體力藥數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                    return false;
                                } else {
                                    體力藥數量 --;
                                    return true;
                                }
                            default:
                                等待體力回復 = true;
                                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                if (村莊踢箱子_開關){
                                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                }
                                t_體力回復 = System.currentTimeMillis();
                                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("戰鬥頁面","AUTO","等待");
        taskAction.addLayerAction("刷圖成功頁面",new UnitAction("再來一次", new UnitCallback() {
            @Override
            public boolean before() {
                if (!裝備任務完成 && 當前裝備本){
                    刷裝備等待 = 2;
                    return true;
                }else {
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                當前裝備本 = false;
                裝備任務完成 = false;
                刷裝備等待 = 2;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("通關失敗頁面",new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                if(SettingPreference.getBoolean("裝備任務失敗降類型",false)) {
                    if (刷裝備類型.equals("回復")) {
                        刷裝備類型 = "心眼";
                    } else if (刷裝備類型.equals("心眼")) {
                        刷裝備類型 = "集中";
                    }else if (刷裝備類型.equals("集中")) {
                        刷裝備類型 = "生命";
                    }else if (刷裝備類型.equals("生命")) {
                        刷裝備類型 = "鐵壁";
                    }else if (刷裝備類型.equals("鐵壁")) {
                        刷裝備類型 = "猛攻";
                    }
                }
                return true;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("ok1", new UnitCallback() {
            @Override
            public boolean before() {
                if(SettingPreference.getBoolean("裝備任務失敗降類型",false)) {
                    if (刷裝備類型.equals("回復")) {
                        刷裝備類型 = "心眼";
                    } else if (刷裝備類型.equals("心眼")) {
                        刷裝備類型 = "集中";
                    }else if (刷裝備類型.equals("集中")) {
                        刷裝備類型 = "生命";
                    }else if (刷裝備類型.equals("生命")) {
                        刷裝備類型 = "鐵壁";
                    }else if (刷裝備類型.equals("鐵壁")) {
                        刷裝備類型 = "猛攻";
                    }
                }
                return true;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("ok2", new UnitCallback() {
            @Override
            public boolean before() {
                if(SettingPreference.getBoolean("裝備任務失敗降類型",false)) {
                    if (刷裝備類型.equals("回復")) {
                        刷裝備類型 = "心眼";
                    } else if (刷裝備類型.equals("心眼")) {
                        刷裝備類型 = "集中";
                    }else if (刷裝備類型.equals("集中")) {
                        刷裝備類型 = "生命";
                    }else if (刷裝備類型.equals("生命")) {
                        刷裝備類型 = "鐵壁";
                    }else if (刷裝備類型.equals("鐵壁")) {
                        刷裝備類型 = "猛攻";
                    }
                }
                return true;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("ok3", new UnitCallback() {
            @Override
            public boolean before() {
                if(SettingPreference.getBoolean("裝備任務失敗降類型",false)) {
                    if (刷裝備類型.equals("回復")) {
                        刷裝備類型 = "心眼";
                    } else if (刷裝備類型.equals("心眼")) {
                        刷裝備類型 = "集中";
                    }else if (刷裝備類型.equals("集中")) {
                        刷裝備類型 = "生命";
                    }else if (刷裝備類型.equals("生命")) {
                        刷裝備類型 = "鐵壁";
                    }else if (刷裝備類型.equals("鐵壁")) {
                        刷裝備類型 = "猛攻";
                    }
                }
                return true;
            }

            @Override
            public void after() {
            }
        }));
        taskAction.addLayerAction("已獲得獎勵頁面","確認");
        taskAction.addLayerAction("任務信息頁面",new UnitAction("任務完成", new UnitCallback() {
            @Override
            public boolean before() {
                裝備任務完成 = true;
                return false;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                if (刷裝備等待 == 0){
                    return false;
                }else {
                    return true;
                }
            }

            @Override
            public void after() {
                刷裝備等待 --;
            }
        }),new UnitAction("確認", new UnitCallback() {
            @Override
            public boolean before() {
                刷裝備等待 = 2;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        return taskAction;
    }

    /**
     * 刷圖BOSS戰
     * @return
     */
    private TaskAction initShuaTuBOSSZhan(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮頁面","打開戰鬥",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                日常BOSS戰_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("關閉列表", new UnitCallback() {
            @Override
            public boolean before() {
                if (!列表縮回){
                    Point point = getMscdPos();
                    swipe(point.x,point.y,698,328,2000);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                列表縮回 = true;
            }
        }),new UnitAction("打開戰鬥", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                日常BOSS戰_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥內頁面",new UnitAction("打開BOSS戰", new UnitCallback() {
            @Override
            public boolean before() {
                當前BOSS戰 = true;
                當前裝備本 = false;
                當前材料本 = false;
                當前金幣本 = false;
                當前強化本 = false;
                當前進化本 = false;
                當前日常sp任務 = false;
                當前活動普通本 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("BOSS戰選關頁面",new UnitAction("殲滅戰", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getBoolean("自動殲滅戰",false)){
                    殲滅戰_開關 = true;
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),new UnitAction("夢幻激戰紅", new UnitCallback() {
            @Override
            public boolean before() {
                夢幻激戰 = false;
                return false;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("無法者之岩紅", new UnitCallback() {
            @Override
            public boolean before() {
                無法者之岩 = false;
                return false;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("水晶洞窟紅", new UnitCallback() {
            @Override
            public boolean before() {
                水晶洞窟 = false;
                return false;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("紅色大地紅", new UnitCallback() {
            @Override
            public boolean before() {
                紅色大地 = false;
                return false;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("山神之森紅", new UnitCallback() {
            @Override
            public boolean before() {
                山神之森 = false;
                return false;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("墮落根源紅", new UnitCallback() {
            @Override
            public boolean before() {
                墮落根源 = false;
                return false;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("夢幻激戰", new UnitCallback() {
            @Override
            public boolean before() {
                if (夢幻激戰){
                    return true;
                }else {
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),new UnitAction("無法者之岩", new UnitCallback() {
            @Override
            public boolean before() {
                if (無法者之岩){
                    return true;
                }else {
                    return false;
                }
            }

            @Override
            public void after() {
                夢幻激戰 = false;
            }
        }),new UnitAction("水晶洞窟", new UnitCallback() {
            @Override
            public boolean before() {
                if (水晶洞窟){
                    return true;
                }else {
                    return false;
                }
            }

            @Override
            public void after() {
                夢幻激戰 = false;
                無法者之岩 = false;
            }
        }),new UnitAction("紅色大地", new UnitCallback() {
            @Override
            public boolean before() {
                if (紅色大地){
                    return true;
                }else {
                    return false;
                }
            }

            @Override
            public void after() {
                夢幻激戰 = false;
                無法者之岩 = false;
                水晶洞窟 = false;
            }
        }),new UnitAction("山神之森", new UnitCallback() {
            @Override
            public boolean before() {
                if (山神之森){
                    return true;
                }else {
                    return false;
                }
            }

            @Override
            public void after() {
                夢幻激戰 = false;
                無法者之岩 = false;
                水晶洞窟 = false;
                紅色大地 = false;
            }
        }),new UnitAction("墮落根源", new UnitCallback() {
            @Override
            public boolean before() {
                if (墮落根源){
                    return true;
                }else {
                    return false;
                }
            }

            @Override
            public void after() {
                夢幻激戰 = false;
                無法者之岩 = false;
                水晶洞窟 = false;
                紅色大地 = false;
                山神之森 = false;
            }
        }),new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                BOSS戰_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("副本選關頁面",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                if (當前BOSS戰){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("殲滅戰發生1", new UnitCallback() {
            @Override
            public boolean before() {
                殲滅戰發生 = true;
                if (SettingPreference.getBoolean("自動殲滅戰",false)){
                    殲滅戰_開關 = true;
                }
                return false;
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("殲滅戰發生2", new UnitCallback() {
                    @Override
                    public boolean before() {
                        殲滅戰發生 = true;
                        if (SettingPreference.getBoolean("自動殲滅戰",false)){
                            殲滅戰_開關 = true;
                        }
                        return false;
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction(BOSS戰等級, new UnitCallback() {
            @Override
            public boolean before() {
                if (殲滅戰發生){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                殲滅戰發生 = false;
            }
        }));
        taskAction.addLayerAction("戰鬥準備頁面",new UnitAction(SettingPreference.getString("BOSS戰選隊伍",""), new UnitCallback() {
            @Override
            public boolean before() {
                隊伍選中 = true;
                return false;
            }

            @Override
            public void after() {

            }
        }), new UnitAction("下一隊", new UnitCallback() {
            @Override
            public boolean before() {
                if (隊伍選中){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {

            }
        }),"start","返回");
        taskAction.addLayerAction("入場體力不足頁面",
                new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getString("BOSS戰體力使用","").equals("等待回復體力")){
                    等待體力回復 = true;
                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                    if (村莊踢箱子_開關){
                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                    }
                    t_體力回復 = System.currentTimeMillis();
                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("確定", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("使用藥水頁面1",new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!檢查藥水數量){
                            檢查藥水數量 = true;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Point point = getMscdPos();
                        體力藥數量 = checkNum(point);
                        if (體力藥數量 == -1){
                            體力藥數量 = 0;
                        }
                    }
                }),
                new UnitAction("關閉", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        if (SettingPreference.getString("BOSS戰體力使用","").equals("等待回復體力")){
                            等待體力回復 = true;
                            村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                            if (村莊踢箱子_開關){
                                巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                            }
                            t_體力回復 = System.currentTimeMillis();
                            成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("使用", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        switch (SettingPreference.getString("BOSS戰體力使用", "")) {
                            case "用鑽石購買":
                                if (鑽石數量 <= 設置鑽石數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                }else{
                                    鑽石買體力 = true;
                                }
                                return false;
                            case "用體力藥":
                                if (體力藥數量 <= 設置體力藥數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                    return false;
                                } else {
                                    體力藥數量 --;
                                    return true;
                                }
                            default:
                                等待體力回復 = true;
                                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                if (村莊踢箱子_開關){
                                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                }
                                t_體力回復 = System.currentTimeMillis();
                                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("使用藥水頁面2",new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!檢查藥水數量){
                            檢查藥水數量 = true;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Point point = getMscdPos();
                        體力藥數量 = checkNum(point);
                        if (體力藥數量 == -1){
                            體力藥數量 = 0;
                        }
                    }
                }),
                new UnitAction("關閉", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        if (SettingPreference.getString("BOSS戰體力使用","").equals("等待回復體力")){
                            等待體力回復 = true;
                            村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                            if (村莊踢箱子_開關){
                                巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                            }
                            t_體力回復 = System.currentTimeMillis();
                            成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("使用", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        switch (SettingPreference.getString("BOSS戰體力使用", "")) {
                            case "用鑽石購買":
                                if (鑽石數量 <= 設置鑽石數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                }else{
                                    鑽石買體力 = true;
                                }
                                return false;
                            case "用體力藥":
                                if (體力藥數量 <= 設置體力藥數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                    return false;
                                } else {
                                    體力藥數量 --;
                                    return true;
                                }
                            default:
                                等待體力回復 = true;
                                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                if (村莊踢箱子_開關){
                                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                }
                                t_體力回復 = System.currentTimeMillis();
                                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("使用藥水頁面3",new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!檢查藥水數量){
                            檢查藥水數量 = true;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Point point = getMscdPos();
                        體力藥數量 = checkNum(point);
                        if (體力藥數量 == -1){
                            體力藥數量 = 0;
                        }
                    }
                }),
                new UnitAction("關閉", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        if (SettingPreference.getString("BOSS戰體力使用","").equals("等待回復體力")){
                            等待體力回復 = true;
                            村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                            if (村莊踢箱子_開關){
                                巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                            }
                            t_體力回復 = System.currentTimeMillis();
                            成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("使用", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        switch (SettingPreference.getString("BOSS戰體力使用", "")) {
                            case "用鑽石購買":
                                if (鑽石數量 <= 設置鑽石數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                }else{
                                    鑽石買體力 = true;
                                }
                                return false;
                            case "用體力藥":
                                if (體力藥數量 <= 設置體力藥數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                    return false;
                                } else {
                                    體力藥數量 --;
                                    return true;
                                }
                            default:
                                等待體力回復 = true;
                                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                if (村莊踢箱子_開關){
                                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                }
                                t_體力回復 = System.currentTimeMillis();
                                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("戰鬥頁面","AUTO","等待");
        taskAction.addLayerAction("刷圖成功頁面","ok");
        taskAction.addLayerAction("通關失敗頁面",new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                if(SettingPreference.getBoolean("BOSS戰智能降階",false)) {
                    if (BOSS戰等級.equals("extreme")) {
                        BOSS戰等級 = "hard";
                    } else if (BOSS戰等級.equals("hard")) {
                        BOSS戰等級 = "normal";
                    }
                }
                return true;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("ok1", new UnitCallback() {
            @Override
            public boolean before() {
                if(SettingPreference.getBoolean("BOSS戰智能降階",false)) {
                    if (BOSS戰等級.equals("extreme")) {
                        BOSS戰等級 = "hard";
                    } else if (BOSS戰等級.equals("hard")) {
                        BOSS戰等級 = "normal";
                    }
                }
                return true;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("ok2", new UnitCallback() {
            @Override
            public boolean before() {
                if(SettingPreference.getBoolean("BOSS戰智能降階",false)) {
                    if (BOSS戰等級.equals("extreme")) {
                        BOSS戰等級 = "hard";
                    } else if (BOSS戰等級.equals("hard")) {
                        BOSS戰等級 = "normal";
                    }
                }
                return true;
            }

            @Override
            public void after() {
            }
        }),new UnitAction("ok3", new UnitCallback() {
            @Override
            public boolean before() {
                if(SettingPreference.getBoolean("BOSS戰智能降階",false)) {
                    if (BOSS戰等級.equals("extreme")) {
                        BOSS戰等級 = "hard";
                    } else if (BOSS戰等級.equals("hard")) {
                        BOSS戰等級 = "normal";
                    }
                }
                return true;
            }

            @Override
            public void after() {
            }
        }));
        taskAction.addLayerAction("已獲得獎勵頁面","確認");
        return taskAction;
    }

    /**
     * 公共操作
     */
    private void initGongGong(){
        g_taskAction = new TaskAction();
        g_taskAction.addLayerAction("友情點最大頁面","ok");
        g_taskAction.addLayerAction("使用藥水頁面1","關閉");
        g_taskAction.addLayerAction("使用藥水頁面2","關閉");
        g_taskAction.addLayerAction("使用藥水頁面3","關閉");
        g_taskAction.addLayerAction("PVP斷線頁面","ok");
        g_taskAction.addLayerAction("殲滅戰邀請頁面",
                new UnitAction("以後再說", new UnitCallback() {
            @Override
            public boolean before() {
                if (!等待體力回復 || !SettingPreference.getBoolean("接受殲滅戰邀請",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("紅魔", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getBoolean("殲滅戰邀請紅魔",false)){
                    殲滅戰邀請紅魔 = true;
                }else{
                    殲滅戰邀請紅魔 = false;
                }
                殲滅戰邀請識別 = true;
                return false;
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("灰魔", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getBoolean("殲滅戰邀請灰魔",false)){
                    殲滅戰邀請灰魔 = true;
                }else{
                    殲滅戰邀請灰魔 = false;
                }
                殲滅戰邀請識別 = true;
                return false;
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("魔獸", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getBoolean("殲滅戰邀請魔獸",false)){
                    殲滅戰邀請魔獸 = true;
                }else{
                    殲滅戰邀請魔獸 = false ;
                }
                殲滅戰邀請識別 = true;
                return false;
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("螃蟹", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (SettingPreference.getBoolean("殲滅戰邀請螃蟹",false)){
                            殲滅戰邀請螃蟹 = true;
                        }else{
                            殲滅戰邀請螃蟹 = false ;
                        }
                        殲滅戰邀請識別 = true;
                        return false;
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("hell", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (殲滅戰邀請紅魔 && 殲滅戰邀請紅魔hell){
                            殲滅戰邀請成功 = true;
                        }
                        if (殲滅戰邀請灰魔 && 殲滅戰邀請灰魔hell){
                            殲滅戰邀請成功 = true;
                        }
                        if (殲滅戰邀請魔獸 && 殲滅戰邀請魔獸hell){
                            殲滅戰邀請成功 = true;
                        }
                        if (殲滅戰邀請螃蟹 && 殲滅戰邀請螃蟹hell){
                            殲滅戰邀請成功 = true;
                        }
                        return false;
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("extreme", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (殲滅戰邀請紅魔 && 殲滅戰邀請紅魔extreme){
                            殲滅戰邀請成功 = true;
                        }
                        if (殲滅戰邀請灰魔 && 殲滅戰邀請灰魔extreme){
                            殲滅戰邀請成功 = true;
                        }
                        if (殲滅戰邀請魔獸 && 殲滅戰邀請魔獸extreme){
                            殲滅戰邀請成功 = true;
                        }
                        if (殲滅戰邀請螃蟹 && 殲滅戰邀請螃蟹extreme){
                            殲滅戰邀請成功 = true;
                        }
                        return false;
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("hard", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (殲滅戰邀請紅魔 && 殲滅戰邀請紅魔hard){
                            殲滅戰邀請成功 = true;
                        }
                        if (殲滅戰邀請灰魔 && 殲滅戰邀請灰魔hard){
                            殲滅戰邀請成功 = true;
                        }
                        if (殲滅戰邀請魔獸 && 殲滅戰邀請魔獸hard){
                            殲滅戰邀請成功 = true;
                        }
                        if (殲滅戰邀請螃蟹 && 殲滅戰邀請螃蟹hard){
                            殲滅戰邀請成功 = true;
                        }
                        return false;
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("normal", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (殲滅戰邀請紅魔 && 殲滅戰邀請紅魔normal){
                            殲滅戰邀請成功 = true;
                        }
                        if (殲滅戰邀請灰魔 && 殲滅戰邀請灰魔normal){
                            殲滅戰邀請成功 = true;
                        }
                        if (殲滅戰邀請魔獸 && 殲滅戰邀請魔獸normal){
                            殲滅戰邀請成功 = true;
                        }
                        if (殲滅戰邀請螃蟹 && 殲滅戰邀請螃蟹normal){
                            殲滅戰邀請成功 = true;
                        }
                        return false;
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("ok", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (殲滅戰邀請成功){
                            殲滅戰邀請開始 = true;
                            邀請殲滅站計數 = 5;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        殲滅戰邀請成功 = false;
                        殲滅戰邀請識別 = false;
                    }
                }),
                new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!殲滅戰邀請識別){
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),"以後再說");
        g_taskAction.addLayerAction("萬聖次數限制頁面","ok");
        g_taskAction.addLayerAction("萬聖任務完成頁面","ok");
        g_taskAction.addLayerAction("萬聖殲滅準備頁面","返回");
        g_taskAction.addLayerAction("萬聖魔獸頁面","返回");
        g_taskAction.addLayerAction("萬聖灰魔頁面","返回");
        g_taskAction.addLayerAction("萬聖紅魔頁面","返回");
        g_taskAction.addLayerAction("進入萬聖殲滅頁面","取消");
        g_taskAction.addLayerAction("活動列表頁面","返回");
        g_taskAction.addLayerAction("万圣节任务页面","返回");
        g_taskAction.addLayerAction("料理途徑頁面","ok");
        g_taskAction.addLayerAction("料理詳情頁面","關閉");
        g_taskAction.addLayerAction("道具不足頁面1","取消");
        g_taskAction.addLayerAction("道具不足頁面2","取消");
        g_taskAction.addLayerAction("鑽石商店頁面","返回");
        g_taskAction.addLayerAction("商店買體力頁面","關閉");
        g_taskAction.addLayerAction("背包頁面","返回");
        g_taskAction.addLayerAction("背包出售頁面","返回");
        g_taskAction.addLayerAction("祈禱頁面","返回");
        g_taskAction.addLayerAction("騎士團信息頁面","返回");
        g_taskAction.addLayerAction("騎士團成員頁面","返回");
        g_taskAction.addLayerAction("騎士團任務頁面","返回");
        g_taskAction.addLayerAction("音樂播放頁面","返回");
        g_taskAction.addLayerAction("背包擴容頁面",new UnitAction("前往倉庫", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getBoolean("出售金幣箱子",false)){
                    出售金幣箱子_開關 = true;
                    return true;
                }else {
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),"關閉");
        g_taskAction.addLayerAction("PVP上級頁面","返回");
        g_taskAction.addLayerAction("循環結束頁面","ok");
        g_taskAction.addLayerAction("競技場頁面","進入酒館");
        g_taskAction.addLayerAction("刷裝備選擇頁面","返回");
        g_taskAction.addLayerAction("多日未登錄頁面",多日未登錄獎勵,"領取");
        g_taskAction.addLayerAction("裝備擴容頁面",new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                裝備分解_開關 = true;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        g_taskAction.addLayerAction("通知領獎頁面","關閉");
        g_taskAction.addLayerAction("郵件領獎頁面","關閉");
        g_taskAction.addLayerAction("禮物領獎頁面","關閉");
        g_taskAction.addLayerAction("友情領獎頁面","關閉");
        g_taskAction.addLayerAction("殲滅邀請頁面","關閉");
        g_taskAction.addLayerAction("比試邀請頁面","關閉");
        g_taskAction.addLayerAction("大招跳過頁面","skip");
        g_taskAction.addLayerAction("好友頁面","返回");
        g_taskAction.addLayerAction("城鎮對話頁面","skip");
        g_taskAction.addLayerAction("劇情頁面","skip");
        g_taskAction.addLayerAction("對話頁面","自動","等待");
        g_taskAction.addLayerAction("任務完成頁面","確認");
        g_taskAction.addLayerAction("通關成功頁面","確認");
        g_taskAction.addLayerAction("等級提升頁面","確認");
        g_taskAction.addLayerAction("任務信息頁面","確認");
        g_taskAction.addLayerAction("登錄頁面", new UnitAction("使用引繼碼", new UnitCallback() {
            @Override
            public boolean before() {
                if (使用引繼碼_開關){
                    if (引繼成功){
                        return false;
                    }else{
                        return true;
                    }
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
            }
        }),new UnitAction("點擊繼續", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                引繼成功 = false;
            }
        }));
        g_taskAction.addLayerAction("引繼選擇頁面",new UnitAction("引繼碼", new UnitCallback() {
            @Override
            public boolean before() {
                if (使用引繼碼_開關){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),"返回");
        g_taskAction.addLayerAction("填寫引繼碼頁面",new UnitAction("賬號", new UnitCallback() {
            @Override
            public boolean before() {
                if (引繼碼賬號輸入完成){
                    return false;
                }else {
                    return true;
                }
            }

            @Override
            public void after() {
                inputText(引繼賬號);
                delay(1000);
                引繼碼賬號輸入完成 = true;
                tap(612,1198);
            }
        }), new UnitAction("密碼", new UnitCallback() {
            @Override
            public boolean before() {
                if (引繼碼密碼輸入完成){
                    return false;
                }else {
                    return true;
                }
            }

            @Override
            public void after() {
                inputText(引繼密碼);
                delay(1000);
                引繼碼密碼輸入完成 = true;
                tap(612,1198);
            }
        }), new UnitAction("登錄", new UnitCallback() {
            @Override
            public boolean before() {
                if (引繼碼密碼輸入完成 && 引繼碼賬號輸入完成){
                    return true;
                }else {
                    return false;
                }
            }

            @Override
            public void after() {
                引繼碼密碼輸入完成 = false;
                引繼碼賬號輸入完成 = false;
            }
        }));
        g_taskAction.addLayerAction("引繼確認頁面",new UnitAction("確認", new UnitCallback() {
            @Override
            public boolean before() {
                引繼成功 = true;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        g_taskAction.addLayerAction("引繼成功提示頁面",new UnitAction("確認", new UnitCallback() {
            @Override
            public boolean before() {
                引繼成功 = true;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        g_taskAction.addLayerAction("廣告頁面","關閉");
        g_taskAction.addLayerAction("網絡波動斷線頁面","再連接","重啟");
        g_taskAction.addLayerAction("通信不安斷線頁面",new UnitAction("他端登錄", new UnitCallback() {
            @Override
            public boolean before() {
                斷線等待 = true;
                斷線等待時間 = System.currentTimeMillis();
                return false;
            }

            @Override
            public void after() {

            }
        }),"ok");
        g_taskAction.addLayerAction("重啟動頁面","重啟");
        g_taskAction.addLayerAction("加載頁面","ok","等待");
        g_taskAction.addLayerAction("廣告頁面","關閉");
        g_taskAction.addLayerAction("使用條款頁面","同意");
        g_taskAction.addLayerAction("通知頁面","同意");
        g_taskAction.addLayerAction("新玩家獎勵頁面","領取");
        g_taskAction.addLayerAction("日常獎勵頁面","領取");
        g_taskAction.addLayerAction("一次獎勵頁面","領取");
        g_taskAction.addLayerAction("更新頁面","確定");
        g_taskAction.addLayerAction("未知頁面",new UnitAction("友情點滿", new UnitCallback() {
            @Override
            public boolean before() {
                消費友情點_開關 = true;
                return false;
            }

            @Override
            public void after() {

            }
        }),"1鑽石","主線開戰","取消","ok","ok1","ok2","ok3","ok4","確認","廣告ok","開始任務1","等待");
        g_taskAction.addLayerAction("卡列表頁面","返回");
        g_taskAction.addLayerAction("設置頁面","返回");
        g_taskAction.addLayerAction("世界頁面","返回");
        g_taskAction.addLayerAction("商店頁面1","返回");
        g_taskAction.addLayerAction("商店頁面2","返回");
        g_taskAction.addLayerAction("商店頁面4","返回");
        g_taskAction.addLayerAction("商店頁面3","返回");
        g_taskAction.addLayerAction("抽獎頁面","返回");
        g_taskAction.addLayerAction("友情點確認頁面","確認");
        g_taskAction.addLayerAction("獎勵頁面","確定");
        g_taskAction.addLayerAction("成長功績頁面","返回");
        g_taskAction.addLayerAction("冒險功績頁面","返回");
        g_taskAction.addLayerAction("挑戰功績頁面","返回");
        g_taskAction.addLayerAction("其他功績頁面","返回");
        g_taskAction.addLayerAction("活動功績頁面","返回");
        g_taskAction.addLayerAction("故事村莊頁面","返回");
        g_taskAction.addLayerAction("故事進行中頁面","返回");
        g_taskAction.addLayerAction("日常任務頁面","返回");
        g_taskAction.addLayerAction("酒館頁面",new UnitAction("清算營業", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("回復體力", new UnitCallback() {
            @Override
            public boolean before() {
                體力回復中 = true;
                return false;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                if (體力回復中 && 等待體力回復){
                    return true;
                }else{
                    等待體力回復 = false;
                    體力回復中 = false;
                    return false;
                }
            }

            @Override
            public void after() {
                體力回復中 = false;
            }
        }));
        g_taskAction.addLayerAction("戰鬥內頁面","返回");
        g_taskAction.addLayerAction("副本頁面","金幣本獎勵領取","材料本獎勵領取","返回");
        g_taskAction.addLayerAction("副本選關頁面","返回");
        g_taskAction.addLayerAction("戰鬥準備頁面","返回");
        g_taskAction.addLayerAction("入場體力不足頁面","取消");
        g_taskAction.addLayerAction("自動編成頁面","確定");
        g_taskAction.addLayerAction("通關成功頁面","ok","確認");
        g_taskAction.addLayerAction("刷圖成功頁面","ok","確認");
        g_taskAction.addLayerAction("通關失敗頁面","ok","ok1","ok2","ok3");
        g_taskAction.addLayerAction("已獲得獎勵頁面","確認");
        g_taskAction.addLayerAction("任務領取獎勵頁面","領取");
        g_taskAction.addLayerAction("班料理頁面","返回");
        g_taskAction.addLayerAction("團長料理頁面","返回");
        g_taskAction.addLayerAction("買入食材頁面","關閉");
        g_taskAction.addLayerAction("雜貨商店頁面","返回");
        g_taskAction.addLayerAction("章節村莊頁面","返回");
        g_taskAction.addLayerAction("章節故事頁面","返回");
        g_taskAction.addLayerAction("對話接任務頁面","返回");
        g_taskAction.addLayerAction("彈框接任務頁面","關閉","開始");
        g_taskAction.addLayerAction("卡詳情頁面","返回");
        g_taskAction.addLayerAction("卡裝備頁面","返回");
        g_taskAction.addLayerAction("裝備強化頁面","關閉");
        g_taskAction.addLayerAction("強化結果頁面","關閉");
        g_taskAction.addLayerAction("BOSS戰選關頁面","返回");
        g_taskAction.addLayerAction("PVP一般頁面","返回");
        g_taskAction.addLayerAction("PVP確認頁面","返回");
        g_taskAction.addLayerAction("PVP勝利頁面","ok");
        g_taskAction.addLayerAction("PVP失敗頁面","ok");
        g_taskAction.addLayerAction("戰鬥頁面",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                return false;
            }

            @Override
            public void after() {

            }
        }));
        g_taskAction.addLayerAction("戰鬥頁面1",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                return false;
            }

            @Override
            public void after() {

            }
        }));
        g_taskAction.addLayerAction("PVP戰鬥頁面",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                return false;
            }

            @Override
            public void after() {

            }
        }));
        g_taskAction.addLayerAction("活動登錄獎勵頁面","ok");
        g_taskAction.addLayerAction("活動抽獎頁面","下一頁","返回");
        g_taskAction.addLayerAction("刷圖獎勵頁面","ok");
        g_taskAction.addLayerAction("鑰匙拉動頁面",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                touchDown(359,792);
                touchMove(357,1014);
                delay(1000);
                touchUp();
            }
        }));
        g_taskAction.addLayerAction("抽裝備結算頁面","返回");
        g_taskAction.addLayerAction("裝備抽取頁面","返回");
        g_taskAction.addLayerAction("選擇裝備頁面","返回");
        g_taskAction.addLayerAction("裝備一鍵選擇頁面","關閉");
        g_taskAction.addLayerAction("殲滅戰準備頁面","返回");
        g_taskAction.addLayerAction("殲滅戰編隊頁面","返回");
        g_taskAction.addLayerAction("隨機邀請頁面","關閉");
        g_taskAction.addLayerAction("好友邀請頁面","關閉");
        g_taskAction.addLayerAction("團友邀請頁面","關閉");
        g_taskAction.addLayerAction("殲滅戰戰鬥頁面","auto");
        g_taskAction.addLayerAction("殲滅戰勝利頁面","ok");
        g_taskAction.addLayerAction("殲滅戰失敗頁面","ok");
        g_taskAction.addLayerAction("騎士團聊天頁面","進入騎士團");
        g_taskAction.addLayerAction("騎士團內頁面","返回酒館");
        g_taskAction.addLayerAction("彩幣兌換頁面","返回");
        g_taskAction.addLayerAction("金幣兌換頁面","返回");
        g_taskAction.addLayerAction("銀幣兌換頁面","返回");
    }

    /**
     * 裝備分解
     */
    private void initFenJieZhuangBei(){
        fenJieZhuangBeiTaskAction = new TaskAction();
        fenJieZhuangBeiTaskAction.addLayerAction("城鎮頁面","進入酒館");
        fenJieZhuangBeiTaskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("關閉列表", new UnitCallback() {
            @Override
            public boolean before() {
                if (!列表縮回){
                    Point point = getMscdPos();
                    swipe(point.x,point.y,698,328,2000);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                列表縮回 = true;
            }
        }),new UnitAction("裝備分解", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                裝備分解_開關 = false;
                return false;
            }

            @Override
            public void after() {

            }
        }));
        fenJieZhuangBeiTaskAction.addLayerAction("選擇裝備頁面",new UnitAction("灰分解", new UnitCallback() {
            @Override
            public boolean before() {
                if(裝備選定){
                    裝備分解_開關 = false;
                    裝備選定 = false;
                    return true;
                }else{
                    return false;
                }

            }

            @Override
            public void after() {

            }
        }),new UnitAction("亮分解", new UnitCallback() {
            @Override
            public boolean before() {
                缺強化石頭 = false;
                return true;
            }

            @Override
            public void after() {
                裝備選定 = false;
            }
        }),new UnitAction("一鍵選擇", new UnitCallback() {
            @Override
            public boolean before() {
                if (裝備選定){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {

            }
        }));
        fenJieZhuangBeiTaskAction.addLayerAction("裝備一鍵選擇頁面",
                new UnitAction("選C", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getBoolean("品級C",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("選UC", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getBoolean("品級UC",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("選R", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getBoolean("品級R",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("選SR", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getBoolean("品級SR",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("選SSR", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getBoolean("品級SSR",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("選UR", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (SettingPreference.getBoolean("品級UR",false)){
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("選猛攻", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getBoolean("種類猛攻",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("取消猛攻", new UnitCallback() {
            @Override
            public boolean before() {
                if (!SettingPreference.getBoolean("種類猛攻",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("選鐵壁", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getBoolean("種類鐵壁",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("取消鐵壁", new UnitCallback() {
            @Override
            public boolean before() {
                if (!SettingPreference.getBoolean("種類鐵壁",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("選生命", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getBoolean("種類生命",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("取消生命", new UnitCallback() {
            @Override
            public boolean before() {
                if (!SettingPreference.getBoolean("種類生命",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("選集中", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getBoolean("種類集中",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("取消集中", new UnitCallback() {
            @Override
            public boolean before() {
                if (!SettingPreference.getBoolean("種類集中",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("選心眼", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getBoolean("種類心眼",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("取消心眼", new UnitCallback() {
            @Override
            public boolean before() {
                if (!SettingPreference.getBoolean("種類心眼",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("選回復", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getBoolean("種類回復",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("取消回復", new UnitCallback() {
            @Override
            public boolean before() {
                if (!SettingPreference.getBoolean("種類回復",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("選會心", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getBoolean("種類會心",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("取消會心", new UnitCallback() {
            @Override
            public boolean before() {
                if (!SettingPreference.getBoolean("種類會心",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("選不屈", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getBoolean("種類不屈",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("取消不屈", new UnitCallback() {
            @Override
            public boolean before() {
                if (!SettingPreference.getBoolean("種類不屈",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("選吸血", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getBoolean("種類吸血",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("取消吸血", new UnitCallback() {
            @Override
            public boolean before() {
                if (!SettingPreference.getBoolean("種類吸血",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("選擇強化", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getBoolean("分解強化裝備",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("取消強化", new UnitCallback() {
            @Override
            public boolean before() {
                if (!SettingPreference.getBoolean("分解強化裝備",false)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("選定", new UnitCallback() {
            @Override
            public boolean before() {
                裝備選定 = true;
                return true;
            }

            @Override
            public void after() {

            }
        }));
    }

    /**
     * 清理背包
     */
    private TaskAction initBeiBaoTengWeiZi(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮頁面","進入酒館");
        taskAction.addLayerAction("酒館頁面","清算营业","打開設置",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                出售金幣箱子_開關 = false;
                return false;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("設置頁面","打開倉庫");
        taskAction.addLayerAction("背包頁面","打開其他","一鍵出售","返回");
        taskAction.addLayerAction("背包出售頁面",new UnitAction("UR寶箱", new UnitCallback() {
            @Override
            public boolean before() {
                if (UR寶箱選中){
                    return false;
                }else {
                    背包騰出空間 = true;
                    return true;
                }
            }

            @Override
            public void after() {
                UR寶箱選中 = true;
            }
        }),new UnitAction("SSR寶箱", new UnitCallback() {
            @Override
            public boolean before() {
                if (SSR寶箱選中){
                    return false;
                }else {
                    背包騰出空間 = true;
                    return true;
                }
            }

            @Override
            public void after() {
                SSR寶箱選中 = true;
            }
        }),new UnitAction("SR寶箱", new UnitCallback() {
            @Override
            public boolean before() {
                if (SR寶箱選中){
                    return false;
                }else {
                    背包騰出空間 = true;
                    return true;
                }
            }

            @Override
            public void after() {
                SR寶箱選中 = true;
            }
        }),new UnitAction("R寶箱", new UnitCallback() {
            @Override
            public boolean before() {
                if (R寶箱選中){
                    return false;
                }else {
                    背包騰出空間 = true;
                    return true;
                }
            }

            @Override
            public void after() {
                R寶箱選中 = true;
            }
        }),new UnitAction("UC寶箱", new UnitCallback() {
            @Override
            public boolean before() {
                if (UC寶箱選中){
                    return false;
                }else {
                    背包騰出空間 = true;
                    return true;
                }
            }

            @Override
            public void after() {
                UC寶箱選中 = true;
            }
        }), new UnitAction("C寶箱", new UnitCallback() {
            @Override
            public boolean before() {
                if (C寶箱選中){
                    return false;
                }else {
                    背包騰出空間 = true;
                    return true;
                }
            }

            @Override
            public void after() {
                C寶箱選中 = true;
            }
        }),"上滑",new UnitAction("出售", new UnitCallback() {
            @Override
            public boolean before() {
                if (出售完成){
                    return false;
                }else{
                    UR寶箱選中 = false;
                    SSR寶箱選中 = false;
                    SR寶箱選中 = false;
                    R寶箱選中 = false;
                    UC寶箱選中 = false;
                    C寶箱選中 = false;
                    return true;
                }
            }

            @Override
            public void after() {
                出售完成 = true;
            }
        }),new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                出售完成 = false;
                出售金幣箱子_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        return taskAction;
    }

    /**
     * 殲滅戰
     * @return
     */
    private TaskAction initJianMieZhan(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮頁面","打開戰鬥",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                殲滅戰_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("關閉列表", new UnitCallback() {
            @Override
            public boolean before() {
                if (!列表縮回){
                    Point point = getMscdPos();
                    swipe(point.x,point.y,698,328,2000);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                列表縮回 = true;
            }
        }),new UnitAction("打開戰鬥", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                殲滅戰_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥內頁面","打開BOSS戰");
        taskAction.addLayerAction("BOSS戰選關頁面","殲滅戰",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                殲滅戰_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("副本選關頁面",new UnitAction(殲滅戰等級, new UnitCallback() {
            @Override
            public boolean before() {
                少隊友 = true;
                return true;
            }

            @Override
            public void after() {

            }
        }),"返回");
        taskAction.addLayerAction("殲滅戰準備頁面","招募",new UnitAction("有隊友", new UnitCallback() {
            @Override
            public boolean before() {
                少隊友 = false;
                return false;
            }

            @Override
            public void after() {

            }
        }),"準備完了",new UnitAction("開始", new UnitCallback() {
            @Override
            public boolean before() {
                if (少隊友){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {

            }
        }),"等待");
        taskAction.addLayerAction("隨機邀請頁面","全部招募","關閉");
        taskAction.addLayerAction("好友邀請頁面","全部招募","關閉");
        taskAction.addLayerAction("團友邀請頁面","全部招募","關閉");
        taskAction.addLayerAction("殲滅戰戰鬥頁面","auto");
        taskAction.addLayerAction("殲滅戰勝利頁面","ok");
        taskAction.addLayerAction("殲滅戰失敗頁面",new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getBoolean("殲滅戰自動降階",false)){
                    if (殲滅戰等級.equals("extreme")){
                        殲滅戰等級 = "hard";
                    }else if(殲滅戰等級.equals("hard")){
                        殲滅戰等級 = "normal";
                    }
                }
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("入場體力不足頁面", new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getString("殲滅戰體力使用","").equals("等待回復體力")){
                    等待體力回復 = true;
                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                    t_體力回復 = System.currentTimeMillis();
                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }), new UnitAction("確定", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("使用藥水頁面2",new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!檢查藥水數量){
                            檢查藥水數量 = true;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Point point = getMscdPos();
                        體力藥數量 = checkNum(point);
                        if (體力藥數量 == -1){
                            體力藥數量 = 0;
                        }
                    }
                }),
                new UnitAction("關閉", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        if (SettingPreference.getString("殲滅戰體力使用","").equals("等待回復體力")){
                            等待體力回復 = true;
                            村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                            if (村莊踢箱子_開關){
                                巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                            }
                            t_體力回復 = System.currentTimeMillis();
                            成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("使用", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        switch (SettingPreference.getString("殲滅戰體力使用", "")) {
                            case "用鑽石購買":
                                if (鑽石數量 <= 設置鑽石數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                }else{
                                    鑽石買體力 = true;
                                }
                                return false;
                            case "用體力藥":
                                if (體力藥數量 <= 設置體力藥數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                    return false;
                                } else {
                                    體力藥數量 --;
                                    return true;
                                }
                            default:
                                等待體力回復 = true;
                                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                if (村莊踢箱子_開關){
                                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                }
                                t_體力回復 = System.currentTimeMillis();
                                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("使用藥水頁面1",new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!檢查藥水數量){
                            檢查藥水數量 = true;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Point point = getMscdPos();
                        體力藥數量 = checkNum(point);
                        if (體力藥數量 == -1){
                            體力藥數量 = 0;
                        }
                    }
                }),
                new UnitAction("關閉", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        if (SettingPreference.getString("殲滅戰體力使用","").equals("等待回復體力")){
                            等待體力回復 = true;
                            村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                            if (村莊踢箱子_開關){
                                巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                            }
                            t_體力回復 = System.currentTimeMillis();
                            成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("使用", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        switch (SettingPreference.getString("殲滅戰體力使用", "")) {
                            case "用鑽石購買":
                                if (鑽石數量 <= 設置鑽石數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                }else{
                                    鑽石買體力 = true;
                                }
                                return false;
                            case "用體力藥":
                                if (體力藥數量 <= 設置體力藥數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                    return false;
                                } else {
                                    體力藥數量 --;
                                    return true;
                                }
                            default:
                                等待體力回復 = true;
                                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                if (村莊踢箱子_開關){
                                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                }
                                t_體力回復 = System.currentTimeMillis();
                                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("使用藥水頁面3",new UnitAction("等待", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (!檢查藥水數量){
                            檢查藥水數量 = true;
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {
                        Point point = getMscdPos();
                        體力藥數量 = checkNum(point);
                        if (體力藥數量 == -1){
                            體力藥數量 = 0;
                        }
                    }
                }),
                new UnitAction("關閉", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        if (SettingPreference.getString("殲滅戰體力使用","").equals("等待回復體力")){
                            等待體力回復 = true;
                            村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                            if (村莊踢箱子_開關){
                                巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                            }
                            t_體力回復 = System.currentTimeMillis();
                            成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                            return true;
                        }else{
                            return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }),
                new UnitAction("使用", new UnitCallback() {
                    @Override
                    public boolean before() {
                        檢查藥水數量 = false;
                        switch (SettingPreference.getString("殲滅戰體力使用", "")) {
                            case "用鑽石購買":
                                if (鑽石數量 <= 設置鑽石數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                }else{
                                    鑽石買體力 = true;
                                }
                                return false;
                            case "用體力藥":
                                if (體力藥數量 <= 設置體力藥數量) {
                                    等待體力回復 = true;
                                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                    if (村莊踢箱子_開關){
                                        巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                    }
                                    t_體力回復 = System.currentTimeMillis();
                                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                    return false;
                                } else {
                                    體力藥數量 --;
                                    return true;
                                }
                            default:
                                等待體力回復 = true;
                                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                                if (村莊踢箱子_開關){
                                    巴尼亞村踢箱子 = true;達瑪麗鎮踢箱子 = false;塔拉踢箱子 = false;貝塞爾鎮踢箱子 = false;奧登村踢箱子 = false;里奧內斯城踢箱子 = false;競技場踢箱子 = true;村莊踢箱子中 = false;酒館踢箱子中 = true;
                                }
                                t_體力回復 = System.currentTimeMillis();
                                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵", false);
                                return false;
                        }
                    }

                    @Override
                    public void after() {

                    }
                }));
        taskAction.addLayerAction("戰鬥頁面","AUTO","等待");
        taskAction.addLayerAction("已獲得獎勵頁面","確認");
        return taskAction;
    }

    /**
     * 鑽石買體力
     * @return
     */
    private TaskAction initZhuanShiMaiTiLi(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮頁面","打開商店");
        taskAction.addLayerAction("酒館頁面","清算营业","打開商店");
        taskAction.addLayerAction("商店頁面1","上滑");
        taskAction.addLayerAction("商店頁面2","鑽石商店");
        taskAction.addLayerAction("商店頁面3","鑽石商店");
        taskAction.addLayerAction("鑽石商店頁面","體力購買");
        taskAction.addLayerAction("商店買體力頁面",new UnitAction("購買", new UnitCallback() {
            @Override
            public boolean before() {
                鑽石買體力 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        return taskAction;
    }

    /**
     * 領取每日任務
     * @return
     */
    private TaskAction initMeiRiRenWuLingQu(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮頁面","打開任務");
        taskAction.addLayerAction("酒館頁面","清算营业","打開任務");
        taskAction.addLayerAction("故事進行中頁面","打開日常");
        taskAction.addLayerAction("每日任務頁面","領取獎勵",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                每日任務領取 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        return taskAction;
    }

    /**
     * 邀请歼灭战
     * @return
     */
    private TaskAction initYaoQingJianMieZhan(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮頁面",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                if (邀請殲滅站計數 <= 0){
                    殲滅戰邀請開始 = false;
                }
                return true;
            }

            @Override
            public void after() {
                邀請殲滅站計數--;
            }
        }));
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                if (邀請殲滅站計數 <= 0){
                    殲滅戰邀請開始 = false;
                }
                return true;
            }

            @Override
            public void after() {
                邀請殲滅站計數--;
            }
        }));
        taskAction.addLayerAction("世界頁面",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                if (邀請殲滅站計數 <= 0){
                    殲滅戰邀請開始 = false;
                }
                return true;
            }

            @Override
            public void after() {
                邀請殲滅站計數--;
            }
        }));
        taskAction.addLayerAction("殲滅戰準備頁面","準備完了","等待");
        taskAction.addLayerAction("殲滅戰戰鬥頁面","auto");
        taskAction.addLayerAction("殲滅戰勝利頁面","ok");
        taskAction.addLayerAction("殲滅戰失敗頁面",new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getBoolean("殲滅戰自動降階",false)){
                    if (殲滅戰等級.equals("extreme")){
                        殲滅戰等級 = "hard";
                    }else if(殲滅戰等級.equals("hard")){
                        殲滅戰等級 = "normal";
                    }
                }
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥頁面","AUTO","等待");
        taskAction.addLayerAction("已獲得獎勵頁面","確認");
        return taskAction;
    }

    /**
     * 初始化原始數據
     */
    private void initData(){
        if (抽卡個數Str.equals("0")){
            指定抽卡個數 = 0;
            if (!SettingPreference.getString("一張牌1","").equals("不指定")){
                抽卡一 = SettingPreference.getString("一張牌1","");
                指定抽卡個數 ++;
            }else{
                抽卡一 = "不指定";
            }
            抽卡二 = "";
            抽卡三 = "";
            抽卡個數 = 1;
        }else if (抽卡個數Str.equals("1")){
            指定抽卡個數 = 0;
            if (!SettingPreference.getString("兩張牌1","").equals("不指定")){
                抽卡一 = SettingPreference.getString("兩張牌1","");
                指定抽卡個數 ++;
            }else{
                抽卡一 = "不指定";
            }
            if (!SettingPreference.getString("兩張牌2","").equals("不指定")){
                抽卡二 = SettingPreference.getString("兩張牌2","");
                指定抽卡個數 ++;
            }else{
                抽卡二 = "不指定";
            }
            抽卡三 = "";
            抽卡個數 = 2;
        }else{
            指定抽卡個數 = 0;
            if (!SettingPreference.getString("三張牌1","").equals("不指定")){
                抽卡一 = SettingPreference.getString("三張牌1","");
                指定抽卡個數 ++;
            }else{
                抽卡一 = "不指定";
            }
            if (!SettingPreference.getString("三張牌2","").equals("不指定")){
                抽卡二 = SettingPreference.getString("三張牌2","");
                指定抽卡個數 ++;
            }else{
                抽卡二 = "不指定";
            }
            if (!SettingPreference.getString("三張牌3","").equals("不指定")){
                抽卡三 = SettingPreference.getString("三張牌3","");
                指定抽卡個數 ++;
            }else{
                抽卡三 = "不指定";
            }
            抽卡個數 = 3;
        }
        if (保存遊戲名.replace(" ","").equals("")){
            保存遊戲名 = "默認名";
        }else{
            保存遊戲名 = SettingPreference.getString("保存遊戲名","").replace(" ","");
        }
        if (保存密碼.equals("")){
            保存密碼 = "123456789";
        }else{
            if (保存密碼.length() < 6){
                保存密碼 = "123456789";
            }else{
                保存密碼 = SettingPreference.getString("保存密碼","").replace(" ","");
            }
        }
        if (使用引繼碼_開關) {
            if (引繼賬號.equals("")) {
                使用引繼碼_開關 = false;
            }else{
                引繼賬號 = SettingPreference.getString("引繼碼賬號","").replace(" ","");
            }
            if (引繼密碼.equals("")) {
                使用引繼碼_開關 = false;
            }else{
                引繼密碼 = SettingPreference.getString("引繼碼密碼","").replace(" ","");
            }
        }
        if (!SettingPreference.getString("斷線等待時長","").replace(" ","").equals("") && !SettingPreference.getString("斷線等待時長","").replace(" ","").equals("0")){
            斷線重連時長 = Integer.parseInt(SettingPreference.getString("斷線等待時長","").replace(" ",""));
        }else{
            斷線重連時長 = 0;
        }
        if (SettingPreference.getString("重置任務時間","").equals("")){
            重置任務時間 = 0;
        }else{
            重置任務時間 = Integer.parseInt(SettingPreference.getString("重置任務時間",""));
        }
        if (卡死重啟時長.equals("")){
            卡死重啟剩餘時間 = 5 * 60 * 1000;
        }else{
            卡死重啟剩餘時間 = Integer.parseInt(卡死重啟時長) * 60 * 1000;
        }
        if (金幣本_開關){
            金幣本自動降級 = SettingPreference.getBoolean("金幣本智能降階",false);
        }
        if (材料本_開關){
            材料本自動降級 = SettingPreference.getBoolean("材料本智能降階",false);
        }
        if (村莊好感度_開關){
            村莊好感度切換村莊 = SettingPreference.getBoolean("村莊切換村莊",false);
            村莊刷好感失敗 = SettingPreference.getBoolean("村莊刷好感失敗",false);
            switch (村莊好感度村莊){
                case "第一村":
                    村莊好感度村莊 = "巴尼亞村";
                    break;
                case "第二村":
                    村莊好感度村莊 = "達瑪麗鎮";
                    break;
                case "第三村":
                    村莊好感度村莊 = "塔拉";
                    break;
                case "第四村":
                    村莊好感度村莊 = "貝塞爾鎮";
                    break;
                case "第五村":
                    村莊好感度村莊 = "奧登村";
                    break;
                case "第六村":
                    村莊好感度村莊 = "里奧內斯城";
                    break;
                default:
                    村莊好感度村莊 = "巴尼亞村";
                    break;
            }
        }
        if (SettingPreference.getString("體力藥數量","").replace(" ","").equals("")){
            設置體力藥數量 = 0;
        }else{
            設置體力藥數量 = Integer.parseInt(SettingPreference.getString("體力藥數量","").replace(" ",""));
        }

        if (SettingPreference.getString("鑽石數量","").replace(" ","").equals("")){
            設置鑽石數量 = 0;
        }else{
            設置鑽石數量 = Integer.parseInt(SettingPreference.getString("鑽石數量","").replace(" ",""));
        }
        if (萬聖節活動_開關){
            萬聖節活動主線_開關 = SettingPreference.getBoolean("萬聖節活動主線",false);
            萬聖節活動殲滅戰_開關 = SettingPreference.getBoolean("萬聖節活動殲滅戰",false);
            萬聖節活動普通本_開關 = SettingPreference.getBoolean("萬聖節活動普通副本",false);
        }
        if (SettingPreference.getBoolean("殲滅戰邀請紅魔",false)){
            殲滅戰邀請紅魔normal = SettingPreference.getBoolean("殲滅戰邀請紅魔normal",false);
            殲滅戰邀請紅魔hard = SettingPreference.getBoolean("殲滅戰邀請紅魔hard",false);
            殲滅戰邀請紅魔extreme = SettingPreference.getBoolean("殲滅戰邀請紅魔extreme",false);
            殲滅戰邀請紅魔hell = SettingPreference.getBoolean("殲滅戰邀請紅魔hell",false);
        }
        if (SettingPreference.getBoolean("殲滅戰邀請灰魔",false)){
            殲滅戰邀請灰魔normal = SettingPreference.getBoolean("殲滅戰邀請灰魔normal",false);
            殲滅戰邀請灰魔hard = SettingPreference.getBoolean("殲滅戰邀請灰魔hard",false);
            殲滅戰邀請灰魔extreme = SettingPreference.getBoolean("殲滅戰邀請灰魔extreme",false);
            殲滅戰邀請灰魔hell = SettingPreference.getBoolean("殲滅戰邀請灰魔hell",false);
        }
        if (SettingPreference.getBoolean("殲滅戰邀請魔獸",false)){
            殲滅戰邀請魔獸normal = SettingPreference.getBoolean("殲滅戰邀請魔獸normal",false);
            殲滅戰邀請魔獸hard = SettingPreference.getBoolean("殲滅戰邀請魔獸hard",false);
            殲滅戰邀請魔獸extreme = SettingPreference.getBoolean("殲滅戰邀請魔獸extreme",false);
            殲滅戰邀請魔獸hell = SettingPreference.getBoolean("殲滅戰邀請魔獸hell",false);
        }
        if (SettingPreference.getBoolean("殲滅戰邀請螃蟹",false)){
            殲滅戰邀請螃蟹normal = SettingPreference.getBoolean("殲滅戰邀請螃蟹normal",false);
            殲滅戰邀請螃蟹hard = SettingPreference.getBoolean("殲滅戰邀請螃蟹hard",false);
            殲滅戰邀請螃蟹extreme = SettingPreference.getBoolean("殲滅戰邀請螃蟹extreme",false);
            殲滅戰邀請螃蟹hell = SettingPreference.getBoolean("殲滅戰邀請螃蟹hell",false);
        }
    }

    /**
     * 重置所有任務
     */
    private void resetMission(){

    }

    @Override
    public void run() {
        if (socketClient == null){
            socketClient = new SocketClient("127.0.0.1",51022);
            socketClient.startDisconnectCheck(false);
            new Thread(new DisconnectCheckTh()).start();
        }
        村莊踢箱子中 = false;
        scriptStart();
        delay(2000);
        initData();
        initTaskAction();
        File file = new File("/sdcard/Pictures/Screenshots/qdzData.txt");
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
                t_卡死重啟 = System.currentTimeMillis();
                initTaskAction();
                showHUDInfo(lastHUDText);
            }
            if(!gamePkg.equals(getFront())){
                runApp(gamePkg);
                new Thread(new DisconnectCheckTh()).start();
                try{
                    fileWriter("啟動遊戲"+"**************************************",file);
                }catch (IOException e){
                    e.printStackTrace();
                }
                delay(1000);
            }else{
                keepCapture();
                Log.e("TAG", "run: " + 當前進化本);
                String curLayerName = null;
                TaskAction curTaskAction=null;
                curLayerName = getLayerName();
                /************************************判斷執行任務順序******************************/
                if (刷初始_開關){
                    curTaskAction = initShuaChuShi();
                    showHUDInfo("刷初始");
                }else{
                    if (每日任務領取){
                        curTaskAction = initMeiRiRenWuLingQu();
                        showHUDInfo("每日任務獎勵領取");
                    }else if (出售金幣箱子_開關){
                        curTaskAction = initBeiBaoTengWeiZi();
                        showHUDInfo("清理背包");
                    }else if (裝備分解_開關){
                        curTaskAction = fenJieZhuangBeiTaskAction;
                        showHUDInfo("分解裝備");
                    }else if (鑽石買體力){
                        curTaskAction = initZhuanShiMaiTiLi();
                        showHUDInfo("鑽石買體力");
                    }else if(殲滅戰_開關){
                        curTaskAction = initJianMieZhan();
                        showHUDInfo("殲滅戰");
                    }else if (購買食材_開關){
                        curTaskAction = initGouMaiShiCai();
                        showHUDInfo("購買食材");
                    }else if (酒館好感度_開關){
                        curTaskAction = initJiuGuanShuaQinMi();
                        showHUDInfo("酒館刷親密度");
                    }else if (每日抽裝備_開關 && !Setting.getBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "每日抽裝備",false)){
                        curTaskAction = initMeiRiChouZhuangBei();
                        showHUDInfo("每日抽裝備");
                    }else if (每日領寶箱_開關 && !Setting.getBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "每日領寶箱",false)){
                        curTaskAction = initMeiRiLingBaoXiang();
                        showHUDInfo("每日領寶箱");
                    }else if (一鑽石抽獎_開關 && !Setting.getBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "一鑽石抽卡",false)){
                        curTaskAction = initYiZuanShiChouKa();
                        showHUDInfo("一鑽石抽卡");
                    }else if (贈送友情點_開關 && !Setting.getBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "贈送友情點",false)){
                        curTaskAction = initSongYouQingDian();
                        showHUDInfo("贈送友情點");
                    }else if (友情點買藥_開關 && !Setting.getBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "友情點買藥",false)){
                        curTaskAction = initYouQingDianMaiYao();
                        showHUDInfo("友情點買藥");
                    }else if (騎士團祈禱 && !Setting.getBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "騎士團祈禱",false)){
                        curTaskAction = initQiShiTuanQiDao();
                        showHUDInfo("騎士團祈禱");
                    }else if (騎士團獎勵_開關){
                        curTaskAction = initQiShiTuanJiangLi();
                        showHUDInfo("騎士團獎勵");
                    }else if (金幣本_開關 && System.currentTimeMillis() - 金幣本等待 > 91*60*1000) {
                        curTaskAction = initJinBiBen();
                        showHUDInfo("金幣本");
                    }else if (材料本_開關  && System.currentTimeMillis() - 材料本等待 > 8*60*60*1000 + 60000){
                        curTaskAction = initCaiLiaoBen();
                        showHUDInfo("材料本");
                    }else if (村莊好感度_開關){
                        curTaskAction = initCunZhuangHaoGanDu();
                        showHUDInfo("村莊刷好感度");
                    }else if (半自動主線_開關){
                        curTaskAction = initBanZiDongZhuXian();
                        showHUDInfo("半自動主線");
                    }else if (BOSS戰_開關){
                        curTaskAction = initShuaTuBOSSZhan();
                        showHUDInfo("BOSS戰");
                    }else if ( 萬聖節活動主線_開關){
                        curTaskAction = initWanShengJieHuoDongRenWu();
                        showHUDInfo("萬聖節活動");
                    }else if (萬聖節活動殲滅戰_開關){
                        curTaskAction = initWanShengJieHuoDongJianMieZhan();
                        showHUDInfo("萬聖節活動");
                    }else if (萬聖節活動普通本_開關){
                        curTaskAction = initWanShengJieHuoDongPuTongBen();
                        showHUDInfo("萬聖節活動");
                    }else if (日常材料本_開關){
                        curTaskAction = initMeiRiCaiLiaoBen();
                        showHUDInfo("每日任務");
                    }else if (日常普通本_開關){
                        curTaskAction = initMeiRiPuTongBen();
                        showHUDInfo("每日任務");
                    }else if (日常強化任務_開關){
                        curTaskAction = initMeiRiZhuangBeiQiangHua();
                        showHUDInfo("每日任務");
                    }else if (日常製作料理_開關){
                        curTaskAction = initMeiRiZhiZuoLiaoLI();
                        showHUDInfo("每日任務");
                    } else if (日常BOSS戰_開關){
                        curTaskAction = initMeiRiBOSSZhan();
                        showHUDInfo("每日任務");
                    }else if (日常PVP戰鬥_開關){
                        curTaskAction = initMeiRIPVP();
                        showHUDInfo("每日任務");
                    }else if (每日任務_開關  && !Setting.getBoolean(DateUtil.getNowDateStr() + 引繼賬號 + "每日任務",false)){
                        curTaskAction = initMeiRiRenWu();
                        showHUDInfo("每日任務");
                    }else{
                        if (掛機刷圖模式.equals("0")){
                            curTaskAction = initShuaTuQiangHuaBen();
                            showHUDInfo("掛機強化本");
                        }else if (掛機刷圖模式.equals("1")){
                            curTaskAction = initShuaTuJinHuaBen();
                            showHUDInfo("掛機進化本");
                        }else if (掛機刷圖模式.equals("2")){
                            curTaskAction = initShuaTuZhuangBei();
                            showHUDInfo("掛機刷裝備");
                        }else{
                            curTaskAction = null;
                            showHUDInfo("暫無任務");
                        }
                    }
                }
                /*************************************任務重置*************************************/
                if (任務重置_開關 && TimingUtil.timingByHour(重置任務時間)){
                    任務重置_開關 = false;
                    resetMission();
                }
                /*****************************過零點任務重置開關重置為true*************************/
                if (!任務重置_開關 && DateUtil.getHour() == 0){
                    任務重置_開關 = true;
                }
                /**********************************畫面超時不動重啟遊戲****************************/
                if (!等待體力回復){
                    String newRGB = getPixelColor(34,45);
                    if (newRGB.equals(oldRGB)){
                        long shicha = System.currentTimeMillis() - t_卡死重啟;
                        卡死重啟剩餘時間 = 卡死重啟剩餘時間 - shicha;
                        t_卡死重啟 = System.currentTimeMillis();
                        if (卡死重啟剩餘時間 <= 0){
                            if (卡死重啟時長.equals("")){
                                卡死重啟剩餘時間 = 5 * 60 * 1000;
                            }else{
                                卡死重啟剩餘時間 = Integer.parseInt(卡死重啟時長) * 60 * 1000;
                            }
                            killApp(gamePkg);
                        }
                    }else{
                        oldRGB = newRGB;
                        t_卡死重啟 = System.currentTimeMillis();
                        if (卡死重啟時長.equals("")){
                            卡死重啟剩餘時間 = 5 * 60 * 1000;
                        }else{
                            卡死重啟剩餘時間 = Integer.parseInt(卡死重啟時長) * 60 * 1000;
                        }
                    }
                }
                if (cmpColorEx("603|54|f0e0be-101010,602|71|bd9258-101010,593|62|d0b486-101010,611|63|d4b586-101010",0.95f)){
                    鑽石數量 = checkDiamond();
                }

                /**********************************等待體力恢復時的操作****************************/
                if (等待體力回復 && System.currentTimeMillis() - t_體力回復 < 60*60*1000){
                    Point[] points = findMultiColors(433, 1, 719, 111,"ffffff-101010","0|4|ffffff-101010,0|10|fffcfc-101010,0|19|ffffff-101010,-13|11|febc32-101010,12|11|fdba32-101010",0.95f);
                    if (points != null && points.length != 0) {
                        if (SettingPreference.getBoolean("領取郵件", false)) {
                            郵件領取_開關 = true;
                        }else{
                            郵件領取_開關 = false;
                        }
                    }
                    if (殲滅戰邀請開始){
                        curTaskAction = initYaoQingJianMieZhan();
                        showHUDInfo("邀請殲滅戰");
                    }else if (出售金幣箱子_開關){
                        curTaskAction = initBeiBaoTengWeiZi();
                        showHUDInfo("清理背包");
                    }else if (裝備分解_開關){
                        curTaskAction = fenJieZhuangBeiTaskAction;
                        showHUDInfo("分解裝備");
                    }else if (金幣本_開關 && System.currentTimeMillis() - 金幣本等待 > 91*60*1000) {
                        curTaskAction = initJinBiBen();
                        showHUDInfo("金幣本");
                    }else if (材料本_開關  && System.currentTimeMillis() - 材料本等待 > 8*60*60*1000 + 60000){
                        curTaskAction = initCaiLiaoBen();
                        showHUDInfo("材料本");
                    }else if (消費友情點_開關){
                        curTaskAction = initXiaoFeiYouQingDian();
                        showHUDInfo("消耗友情點");
                    }else if (郵件領取_開關){
                        curTaskAction = initYouJianLingQu();
                        showHUDInfo("郵件領取");
                    }else if (成就領取_開關){
                        curTaskAction = initChengJiuJiangLi();
                        showHUDInfo("成就領取");
                    }else if (村莊踢箱子_開關){
                        if (t_村莊踢箱子 == 0){
                            curTaskAction = initCunZhuangTiXiangZi();
                        }else if (System.currentTimeMillis() - t_村莊踢箱子 >= 6*60*60*1000){
                            curTaskAction = initCunZhuangTiXiangZi();
                        }else{
                            村莊踢箱子_開關 = false;
                        }
                        showHUDInfo("村莊踢箱子");
                    }else{
                        curTaskAction = null;
                        showHUDInfo("等待體力回復");
                    }
                }else{
                    等待體力回復 = false;
                }
                /***************************************打印日誌***********************************/
                Log.e(getTag(), "run:" + curLayerName);
                if (鑽石數量 >= 0){
                    showNumInfo("鑚:" + 鑽石數量);
                }else{
                    showNumInfo("鑚:未知");
                }
                if (體力藥數量 >= 0){
                    showDiaInfo("藥:" + 體力藥數量);
                }else{
                    showDiaInfo("藥:未知");
                }
                showMOVEInfo(curLayerName + "-" + getUnitName());
                try{
                    fileWriter(curLayerName + "-" + getUnitName(),file);
                }catch (IOException e){
                    e.printStackTrace();
                }
                /*************************************斷線重連等待*********************************/
                if (斷線等待){
                    if (System.currentTimeMillis() - 斷線等待時間 < 斷線重連時長*60*1000){
                        curTaskAction = null;
                        g_taskAction = null;
                        showHUDInfo("其他設備登錄");
                        showMOVEInfo("需要等待" + (斷線重連時長*60*1000 - (System.currentTimeMillis() - 斷線等待時間))/60/1000 + "分鐘");
                    }else{
                        斷線等待 = false;
                    }
                }else{
                    g_taskAction = null;
                    initGongGong();
                }
                /***************************************執行操作***********************************/
                if (execTask(curLayerName,curTaskAction)) {
                    delay(500);
                }else{
                    execTask(curLayerName,g_taskAction);
                }
                delay(500);
                releaseCapture();
            }
        }
        scriptEnd();
    }

    private class DisconnectCheckTh implements Runnable{
        @Override
        public void run() {
            delay(15000);
            if(socketClient.isConnected()){
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "連接成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "連接失敗", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
    @Override
    protected void scriptEnd() {
        super.scriptEnd();
        socketClient.disconnect();
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                hudManage.hideHUD("move");
                hudManage.hideHUD("info");
                hudManage.hideHUD("num");
                hudManage.hideHUD("diamond");
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

    private int checkNum(Point point){
        int one = 0;
        int two = 0;
        int three = 0;
        one = getNum(point.x - 14,point.y - 10,point.x - 2,point.y + 12);

        if (one == -1){
            return -1;
        }else if (one == 1){
            two = getNum(point.x - 25,point.y - 10,point.x - 11,point.y +12);
            if (two == -1){
                return one;
            }else if (two == 1){
                three = getNum(point.x - 35,point.y - 10,point.x - 21,point.y +12);
            }else{
                three = getNum(point.x - 37,point.y - 10,point.x - 23,point.y +12);
            }
            if (three == -1){
                return two*10 + one;
            }else{
                return three*100 + two*10 + one;
            }
        }else{
            two = getNum(point.x - 27,point.y - 10,point.x - 13,point.y +12);
            if (two == -1){
                return one;
            }else if (two == 1){
                three = getNum(point.x - 37,point.y - 10,point.x - 24,point.y +12);
            }else{
                three = getNum(point.x - 39,point.y - 10,point.x - 25,point.y +12);
            }
            if (three == -1){
                return two*10 + one;
            }else{
                return three*100 + two*10 + one;
            }
        }
    }
    private int getNum(int x1,int y1,int x2,int y2){
        Point[] point0s = findMultiColors(x1,y1,x2,y2,"ffffff-303030","-1|-1|ffffff-303030,-2|-2|fefefe-303030,-3|-3|fefefe-303030,-5|-3|e7e5e3-303030,-8|0|efeeec-303030,-7|4|ffffff-303030,-6|5|fcfcfc-303030,-1|7|ffffff-303030,-2|9|fdfdfd-303030,-5|11|f9f8f8-303030",0.95f);
        if (point0s.length > 0){
            return 9;
        }
        Point[] point9s = findMultiColors(x1,y1,x2,y2,"ffffff-303030","1|2|ffffff-303030,0|1|ffffff-303030,2|4|fefefe-303030,4|5|fefefe-303030,6|6|ffffff-303030,7|8|fefefe-303030,4|12|eceae8-303030,2|12|ffffff-303030,0|11|ffffff-303030,-1|9|ffffff-303030",0.95f);
        if (point9s.length > 0){
            return 8;
        }
        Point[] point8s = findMultiColors(x1,y1,x2,y2,"fefefe-303030","2|-1|ffffff-303030,6|-1|fbfbfa-303030,6|1|f7f6f5-303030,4|7|f4f3f2-303030,3|9|f8f8f7-303030,3|12|ffffff-303030,1|14|fbfbfa-303030",0.95f);
        if (point8s.length > 0){
            return 7;
        }
        Point[] point7s = findMultiColors(x1,y1,x2,y2,"ffffff-303030","-5|1|ffffff-303030,1|1|ffffff-303030,-5|2|ffffff-303030,-5|6|fefefe-303030,-4|8|fefefe-303030,-1|9|f4f4f3-303030,3|6|fcfcfc-303030,0|-5|f5f4f3-303030",0.95f);
        if (point7s.length > 0){
            return 6;
        }
        Point[] point6s = findMultiColors(x1,y1,x2,y2,"fdfdfd-303030","-3|1|fcfcfc-303030,-5|1|fbfbfa-303030,-6|5|ffffff-303030,-3|6|fdfdfd-303030,-1|7|fefefe-303030,0|10|fefefe-303030,-6|15|fcfcfc-303030,-8|13|ffffff-303030",0.95f);
        if (point6s.length > 0){
            return 5;
        }
        Point[] point5s = findMultiColors(x1,y1,x2,y2,"ffffff-303030","-3|1|fbfafa-303030,-8|0|f7f7f6-303030,-4|-5|f2f1ef-303030,-2|-7|ffffff-303030,-3|0|f8f8f7-303030,-3|4|fafaf9-303030,-2|5|fcfbfb-303030",0.95f);
        if (point5s.length > 0){
            return 4;
        }
        Point[] point4s = findMultiColors(x1,y1,x2,y2,"ffffff-303030","3|0|ffffff-303030,1|1|f3f2f1-303030,2|5|ffffff-303030,1|6|f8f8f7-303030,4|6|ffffff-303030,5|8|fcfbfb-303030,6|10|fefefe-303030,-2|13|fefefe-303030",0.95f);
        if (point4s.length > 0){
            return 3;
        }
        Point[] point3s = findMultiColors(x1,y1,x2,y2,"fdfdfd-303030","-3|0|fdfdfd-303030,-5|1|f3f2f1-303030,1|-6|f4f4f3-303030,2|-9|ffffff-303030,2|-11|ffffff-303030,0|-13|fefefe-303030,-4|-13|f9f8f8-303030",0.95f);
        if (point3s.length > 0){
            return 2;
        }
        Point[] point2s = findMultiColors(x1,y1,x2,y2,"ffffff-303030","-1|0|ffffff-303030,0|-1|ffffff-303030,1|-2|fdfdfc-303030,1|6|f7f7f6-303030,0|6|ffffff-303030,-1|9|ffffff-303030,1|11|fbfafa-303030,-1|12|fcfbfb-303030",0.95f);
        if (point2s.length > 0){
            return 1;
        }
        Point[] point1s = findMultiColors(x1,y1,x2,y2,"ffffff-303030","-2|-2|ffffff-303030,-4|-4|ffffff-303030,-8|1|fdfdfc-303030,-9|4|ffffff-303030,-8|7|ffffff-303030,-6|9|ffffff-303030,-5|10|ffffff-303030,-4|11|fefefe-303030,1|6|fdfdfd-303030,0|2|ffffff-303030",0.95f);
        if (point1s.length > 0){
            return 0;
        }
        return -1;
    }

    private int checkDiamond(){
        int one = 0;
        int two = 0;
        int three = 0;
        int four = 0;
        int x1 = 564;
        int y1 = 50;
        int x2 = 582;
        int y2 = 75;
        one = getDiamond(x1,y1,x2,y2);
        if (one == -1){
            return -1;
        }else if (one == 0){
            //one減18
            two = getDiamond(x1-18,y1,x2-18,y2);
            if (two == -1){
                return one;
            }else if (two == 0){
                //two減18
                three = getDiamond(x1-18-18,y1,x2-18-18,y2);
                if (three == -1){
                    return two*10 + one;
                }else if (three == 0){
                    //three減18
                    four = getDiamond(x1-18-18-18,y1,x2-18-18-18,y2);
                }else if (three == 1){
                    //three減12
                    four = getDiamond(x1-18-18-12,y1,x2-18-18-12,y2);
                }else if (three == 2 ||three == 3 ||three == 5 ||three == 6 ||three == 8 ){
                    //three減16
                    four = getDiamond(x1-18-18-16,y1,x2-18-18-16,y2);
                }else{
                    //three減17
                    four = getDiamond(x1-18-18-17,y1,x2-18-18-17,y2);
                }
                if (four == -1){
                    return three*100 + two*10 + one;
                }else{
                    return four*1000 + three*100 + two*10 + one;
                }
            }else if (two == 1){
                //two減12
                three = getDiamond(x1-18-12,y1,x2-18-12,y2);
                if (three == -1){
                    return two*10 + one;
                }else if (three == 0){
                    //three減18
                    four = getDiamond(x1-18-12-18,y1,x2-18-12-18,y2);
                }else if (three == 1){
                    //three減12
                    four = getDiamond(x1-18-12-12,y1,x2-18-12-12,y2);
                }else if (three == 2 ||three == 3 ||three == 5 ||three == 6 ||three == 8 ){
                    //three減16
                    four = getDiamond(x1-18-12-16,y1,x2-18-12-16,y2);
                }else{
                    //three減17
                    four = getDiamond(x1-18-12-17,y1,x2-18-12-17,y2);
                }
                if (four == -1){
                    return three*100 + two*10 + one;
                }else{
                    return four*1000 + three*100 + two*10 + one;
                }
            }else if (two == 2 ||two == 3 ||two == 5 ||two == 6 ||two == 8 ){
                //two減16
                three = getDiamond(x1-18-16,y1,x2-18-16,y2);
                if (three == -1){
                    return two*10 + one;
                }else if (three == 0){
                    //three減18
                    four = getDiamond(x1-18-16-18,y1,x2-18-16-18,y2);
                }else if (three == 1){
                    //three減12
                    four = getDiamond(x1-18-16-12,y1,x2-18-16-12,y2);
                }else if (three == 2 ||three == 3 ||three == 5 ||three == 6 ||three == 8 ){
                    //three減16
                    four = getDiamond(x1-18-16-16,y1,x2-18-16-16,y2);
                }else{
                    //three減17
                    four = getDiamond(x1-18-16-17,y1,x2-18-16-17,y2);
                }
                if (four == -1){
                    return three*100 + two*10 + one;
                }else{
                    return four*1000 + three*100 + two*10 + one;
                }
            }else{
                //two減17
                three = getDiamond(x1-18-17,y1,x2-18-17,y2);
                if (three == -1){
                    return two*10 + one;
                }else if (three == 0){
                    //three減18
                    four = getDiamond(x1-18-17-18,y1,x2-18-17-18,y2);
                }else if (three == 1){
                    //three減12
                    four = getDiamond(x1-18-17-12,y1,x2-18-17-12,y2);
                }else if (three == 2 ||three == 3 ||three == 5 ||three == 6 ||three == 8 ){
                    //three減16
                    four = getDiamond(x1-18-17-16,y1,x2-18-17-16,y2);
                }else{
                    //three減17
                    four = getDiamond(x1-18-17-17,y1,x2-18-17-17,y2);
                }
                if (four == -1){
                    return three*100 + two*10 + one;
                }else{
                    return four*1000 + three*100 + two*10 + one;
                }
            }
        }else if (one == 1){
            //one減12
            two = getDiamond(x1-12,y1,x2-12,y2);
            if (two == -1){
                return one;
            }else if (two == 0){
                //two減18
                three = getDiamond(x1-12-18,y1,x2-12-18,y2);
                if (three == -1){
                    return two*10 + one;
                }else if (three == 0){
                    //three減18
                    four = getDiamond(x1-12-18-18,y1,x2-12-18-18,y2);
                }else if (three == 1){
                    //three減12
                    four = getDiamond(x1-12-18-12,y1,x2-12-18-12,y2);
                }else if (three == 2 ||three == 3 ||three == 5 ||three == 6 ||three == 8 ){
                    //three減16
                    four = getDiamond(x1-12-18-16,y1,x2-12-18-16,y2);
                }else{
                    //three減17
                    four = getDiamond(x1-12-18-17,y1,x2-12-18-17,y2);
                }
                if (four == -1){
                    return three*100 + two*10 + one;
                }else{
                    return four*1000 + three*100 + two*10 + one;
                }
            }else if (two == 1){
                //two減12
                three = getDiamond(x1-12-12,y1,x2-12-12,y2);
                if (three == -1){
                    return two*10 + one;
                }else if (three == 0){
                    //three減18
                    four = getDiamond(x1-12-12-18,y1,x2-12-12-18,y2);
                }else if (three == 1){
                    //three減12
                    four = getDiamond(x1-12-12-12,y1,x2-12-12-12,y2);
                }else if (three == 2 ||three == 3 ||three == 5 ||three == 6 ||three == 8 ){
                    //three減16
                    four = getDiamond(x1-12-12-16,y1,x2-12-12-16,y2);
                }else{
                    //three減17
                    four = getDiamond(x1-12-12-17,y1,x2-12-12-17,y2);
                }
                if (four == -1){
                    return three*100 + two*10 + one;
                }else{
                    return four*1000 + three*100 + two*10 + one;
                }
            }else if (two == 2 ||two == 3 ||two == 5 ||two == 6 ||two == 8 ){
                //two減16
                three = getDiamond(x1-12-16,y1,x2-12-16,y2);
                if (three == -1){
                    return two*10 + one;
                }else if (three == 0){
                    //three減18
                    four = getDiamond(x1-12-16-18,y1,x2-12-16-18,y2);
                }else if (three == 1){
                    //three減12
                    four = getDiamond(x1-12-16-12,y1,x2-12-16-12,y2);
                }else if (three == 2 ||three == 3 ||three == 5 ||three == 6 ||three == 8 ){
                    //three減16
                    four = getDiamond(x1-12-16-16,y1,x2-12-16-16,y2);
                }else{
                    //three減17
                    four = getDiamond(x1-12-16-17,y1,x2-12-16-17,y2);
                }
                if (four == -1){
                    return three*100 + two*10 + one;
                }else{
                    return four*1000 + three*100 + two*10 + one;
                }
            }else{
                //two減17
                three = getDiamond(x1-12-17,y1,x2-12-17,y2);
                if (three == -1){
                    return two*10 + one;
                }else if (three == 0){
                    //three減18
                    four = getDiamond(x1-12-12-18,y1,x2-12-12-18,y2);
                }else if (three == 1){
                    //three減12
                    four = getDiamond(x1-12-12-12,y1,x2-12-12-12,y2);
                }else if (three == 2 ||three == 3 ||three == 5 ||three == 6 ||three == 8 ){
                    //three減16
                    four = getDiamond(x1-12-12-16,y1,x2-12-12-16,y2);
                }else{
                    //three減17
                    four = getDiamond(x1-12-12-17,y1,x2-12-12-17,y2);
                }
                if (four == -1){
                    return three*100 + two*10 + one;
                }else{
                    return four*1000 + three*100 + two*10 + one;
                }
            }
        }else if (one == 2 ||one == 3 ||one == 5 ||one == 6 ||one == 8 ){
            //one減16
            two = getDiamond(x1-16,y1,x2-16,y2);
            if (two == -1){
                return one;
            }else if (two == 0){
                //two減18
                three = getDiamond(x1-16-18,y1,x2-16-18,y2);
                if (three == -1){
                    return two*10 + one;
                }else if (three == 0){
                    //three減18
                    four = getDiamond(x1-16-18-18,y1,x2-16-18-18,y2);
                }else if (three == 1){
                    //three減12
                    four = getDiamond(x1-16-18-12,y1,x2-16-18-12,y2);
                }else if (three == 2 ||three == 3 ||three == 5 ||three == 6 ||three == 8 ){
                    //three減16
                    four = getDiamond(x1-16-18-16,y1,x2-16-18-16,y2);
                }else{
                    //three減17
                    four = getDiamond(x1-16-18-17,y1,x2-16-18-17,y2);
                }
                if (four == -1){
                    return three*100 + two*10 + one;
                }else{
                    return four*1000 + three*100 + two*10 + one;
                }
            }else if (two == 1){
                //two減12
                three = getDiamond(x1-16-12,y1,x2-16-12,y2);
                if (three == -1){
                    return two*10 + one;
                }else if (three == 0){
                    //three減18
                    four = getDiamond(x1-16-12-18,y1,x2-16-12-18,y2);
                }else if (three == 1){
                    //three減12
                    four = getDiamond(x1-16-12-12,y1,x2-16-12-12,y2);
                }else if (three == 2 ||three == 3 ||three == 5 ||three == 6 ||three == 8 ){
                    //three減16
                    four = getDiamond(x1-16-12-16,y1,x2-16-12-16,y2);
                }else{
                    //three減17
                    four = getDiamond(x1-16-12-17,y1,x2-16-12-17,y2);
                }
                if (four == -1){
                    return three*100 + two*10 + one;
                }else{
                    return four*1000 + three*100 + two*10 + one;
                }
            }else if (two == 2 ||two == 3 ||two == 5 ||two == 6 ||two == 8 ){
                //two減16
                three = getDiamond(x1-16-16,y1,x2-16-16,y2);
                if (three == -1){
                    return two*10 + one;
                }else if (three == 0){
                    //three減18
                    four = getDiamond(x1-16-16-18,y1,x2-16-16-18,y2);
                }else if (three == 1){
                    //three減12
                    four = getDiamond(x1-16-16-12,y1,x2-16-16-12,y2);
                }else if (three == 2 ||three == 3 ||three == 5 ||three == 6 ||three == 8 ){
                    //three減16
                    four = getDiamond(x1-16-16-16,y1,x2-16-16-16,y2);
                }else{
                    //three減17
                    four = getDiamond(x1-16-16-17,y1,x2-16-16-17,y2);
                }
                if (four == -1){
                    return three*100 + two*10 + one;
                }else{
                    return four*1000 + three*100 + two*10 + one;
                }
            }else{
                //two減17
                three = getDiamond(x1-16-17,y1,x2-16-17,y2);
                if (three == -1){
                    return two*10 + one;
                }else if (three == 0){
                    //three減18
                    four = getDiamond(x1-16-17-18,y1,x2-16-17-18,y2);
                }else if (three == 1){
                    //three減12
                    four = getDiamond(x1-16-17-12,y1,x2-16-17-12,y2);
                }else if (three == 2 ||three == 3 ||three == 5 ||three == 6 ||three == 8 ){
                    //three減16
                    four = getDiamond(x1-16-17-16,y1,x2-16-17-16,y2);
                }else{
                    //three減17
                    four = getDiamond(x1-16-17-17,y1,x2-16-17-17,y2);
                }
                if (four == -1){
                    return three*100 + two*10 + one;
                }else{
                    return four*1000 + three*100 + two*10 + one;
                }
            }
        }else{
            //one減17
            two = getDiamond(x1-17,y1,x2-17,y2);
            if (two == -1){
                return one;
            }else if (two == 0){
                //two減18
                three = getDiamond(x1-17-18,y1,x2-17-18,y2);
                if (three == -1){
                    return two*10 + one;
                }else if (three == 0){
                    //three減18
                    four = getDiamond(x1-17-18-18,y1,x2-17-18-18,y2);
                }else if (three == 1){
                    //three減12
                    four = getDiamond(x1-17-18-12,y1,x2-17-18-12,y2);
                }else if (three == 2 ||three == 3 ||three == 5 ||three == 6 ||three == 8 ){
                    //three減16
                    four = getDiamond(x1-17-18-16,y1,x2-17-18-16,y2);
                }else{
                    //three減17
                    four = getDiamond(x1-17-18-17,y1,x2-17-18-17,y2);
                }
                if (four == -1){
                    return three*100 + two*10 + one;
                }else{
                    return four*1000 + three*100 + two*10 + one;
                }
            }else if (two == 1){
                //two減12
                three = getDiamond(x1-17-12,y1,x2-17-12,y2);
                if (three == -1){
                    return two*10 + one;
                }else if (three == 0){
                    //three減18
                    four = getDiamond(x1-17-12-18,y1,x2-17-12-18,y2);
                }else if (three == 1){
                    //three減12
                    four = getDiamond(x1-17-12-12,y1,x2-17-12-12,y2);
                }else if (three == 2 ||three == 3 ||three == 5 ||three == 6 ||three == 8 ){
                    //three減16
                    four = getDiamond(x1-17-12-16,y1,x2-17-12-16,y2);
                }else{
                    //three減17
                    four = getDiamond(x1-17-12-17,y1,x2-17-12-17,y2);
                }
                if (four == -1){
                    return three*100 + two*10 + one;
                }else{
                    return four*1000 + three*100 + two*10 + one;
                }
            }else if (two == 2 ||two == 3 ||two == 5 ||two == 6 ||two == 8 ){
                //two減16
                three = getDiamond(x1-17-16,y1,x2-17-16,y2);
                if (three == -1){
                    return two*10 + one;
                }else if (three == 0){
                    //three減18
                    four = getDiamond(x1-17-16-18,y1,x2-17-16-18,y2);
                }else if (three == 1){
                    //three減12
                    four = getDiamond(x1-17-16-12,y1,x2-17-16-12,y2);
                }else if (three == 2 ||three == 3 ||three == 5 ||three == 6 ||three == 8 ){
                    //three減16
                    four = getDiamond(x1-17-16-16,y1,x2-17-16-16,y2);
                }else{
                    //three減17
                    four = getDiamond(x1-17-16-17,y1,x2-17-16-17,y2);
                }
                if (four == -1){
                    return three*100 + two*10 + one;
                }else{
                    return four*1000 + three*100 + two*10 + one;
                }
            }else{
                //two減17
                three = getDiamond(x1-17-17,y1,x2-17-17,y2);
                if (three == -1){
                    return two*10 + one;
                }else if (three == 0){
                    //three減18
                    four = getDiamond(x1-17-17-18,y1,x2-17-17-18,y2);
                }else if (three == 1){
                    //three減12
                    four = getDiamond(x1-17-17-12,y1,x2-17-17-12,y2);
                }else if (three == 2 ||three == 3 ||three == 5 ||three == 6 ||three == 8 ){
                    //three減16
                    four = getDiamond(x1-17-17-16,y1,x2-17-17-16,y2);
                }else{
                    //three減17
                    four = getDiamond(x1-17-17-17,y1,x2-17-17-17,y2);
                }
                if (four == -1){
                    return three*100 + two*10 + one;
                }else{
                    return four*1000 + three*100 + two*10 + one;
                }
            }
        }
    }
    private int getDiamond(int x1,int y1,int x2,int y2){
        Point[] point1s = findMultiColors(x1,y1,x2,y2,"bf9d73-101010","2|-1|be9c72-101010,4|2|bf9d73-101010,6|5|bf9d73-101010,6|12|bf9d73-101010,1|17|bf9d73-101010,-2|15|bf9d73-101010,-4|10|bf9d73-101010,-4|4|b6966e-101010",0.95f);
        if (point1s.length > 0){
            return 0;
        }
        Point[] point2s = findMultiColors(x1,y1,x2,y2,"b2926b-101010","3|-1|bf9d73-101010,3|5|bf9d73-101010,4|15|be9c72-101010,1|15|be9c72-101010,2|4|bf9d73-101010,1|0|be9d73-101010",0.95f);
        if (point2s.length > 0){
            return 1;
        }
        Point[] point3s = findMultiColors(x1,y1,x2,y2,"b2926b-101010","4|-3|be9c72-101010,7|-2|bf9d73-101010,8|2|bf9d73-101010,8|5|bf9d73-101010,2|14|bf9d73-101010,8|13|bf9d73-101010,9|13|bf9d73-101010",0.95f);
        if (point3s.length > 0){
            return 2;
        }
        Point[] point4s = findMultiColors(x1,y1,x2,y2,"be9d73-101010","4|-1|be9c72-101010,7|0|b9986f-101010,3|6|bf9d73-101010,6|6|bf9d73-101010,8|8|bf9d73-101010,9|12|bf9d73-101010,0|15|bf9d73-101010,-1|14|bd9c72-101010",0.95f);
        if (point4s.length > 0){
            return 3;
        }
        Point[] point5s = findMultiColors(x1,y1,x2,y2,"bf9d73-101010","0|7|b7966e-101010,0|16|be9d73-101010,3|10|ab8c67-101010,-8|10|b8976f-101010,-2|4|bb9a71-101010,-1|11|bf9d73-101010,-2|16|bd9c72-101010",0.95f);
        if (point5s.length > 0){
            return 4;
        }
        Point[] point6s = findMultiColors(x1,y1,x2,y2,"bf9d73-101010","-4|-1|bd9c72-101010,-6|1|bf9d73-101010,-8|5|bf9d73-101010,-4|6|bf9d73-101010,-2|5|b3936c-101010,0|14|be9d73-101010,-9|15|bf9d73-101010",0.95f);
        if (point6s.length > 0){
            return 5;
        }
        Point[] point7s = findMultiColors(x1,y1,x2,y2,"be9d73-101010","-3|3|bf9d73-101010,-5|8|bf9d73-101010,-5|11|bf9d73-101010,-2|15|bf9d73-101010,0|17|bf9d73-101010,3|16|ba9970-101010,5|10|bf9d73-101010,4|8|bf9d73-101010,2|6|bf9d73-101010",0.95f);
        if (point7s.length > 0){
            return 6;
        }
        Point[] point8s = findMultiColors(x1,y1,x2,y2,"be9d73-101010","2|-2|bf9d73-101010,9|-2|bf9d73-101010,8|0|bf9d73-101010,5|10|bf9d73-101010,4|16|be9c72-101010,2|16|be9c72-101010",0.95f);
        if (point8s.length > 0){
            return 7;
        }
        Point[] point9s = findMultiColors(x1,y1,x2,y2,"b8976f-101010","-2|4|bf9d73-101010,0|6|bf9d73-101010,2|8|bf9d73-101010,4|10|bf9d73-101010,6|12|bf9d73-101010,1|18|bf9d73-101010,-2|16|bf9d73-101010,-4|13|bf9d73-101010",0.95f);
        if (point9s.length > 0){
            return 8;
        }
        Point[] point0s = findMultiColors(x1,y1,x2,y2,"be9c72-101010","2|2|bf9d73-101010,5|5|bf9d73-101010,5|9|bf9d73-101010,-4|9|bf9d73-101010,-2|11|bf9d73-101010,4|14|bf9d73-101010,2|16|bf9d73-101010",0.95f);
        if (point0s.length > 0){
            return 9;
        }
        return -1;
    }

    private void sendLineMessage(String userName,String notifCodes,String notifStr){

    }
}
