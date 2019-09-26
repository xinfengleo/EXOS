package com.gamebot.botdemo.script;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;

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
import com.gamebot.botdemo.utils.ProcessUtils;
import com.gamebot.botdemo.utils.TimingUtil;
import com.gamebot.sdk.preference.*;
import com.google.gson.Gson;
import com.topjohnwu.superuser.Shell;


public class ScriptThread extends SuperScriptThread {
    private final static String gamePkg="com.netmarble.nanatsunotaizai";
    private SocketClient socketClient;
    private static Preference Setting;


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
    private boolean 金幣本_開關 = SettingPreference.getBoolean("金幣本",false);
    private boolean 材料本_開關 = SettingPreference.getBoolean("材料本",false);
    private boolean 村莊好感度_開關 = SettingPreference.getBoolean("村莊好感度",false);
    private boolean 半自動主線_開關 = SettingPreference.getBoolean("半自動主線",false);
    private boolean 每日任務_開關 = SettingPreference.getBoolean("每日任務",false);
    private boolean 騎士團祈禱 = SettingPreference.getBoolean("騎士團祈禱",false);
    private boolean 夢幻激戰 = SettingPreference.getBoolean("BOSS戰夢幻激戰",false);
    private boolean 無法者之岩 = SettingPreference.getBoolean("BOSS戰無法者之岩",false);
    private boolean 水晶洞窟 = SettingPreference.getBoolean("BOSS戰水晶洞窟",false);
    private boolean 紅色大地 = SettingPreference.getBoolean("BOSS戰紅色大地",false);
    private boolean 山神之森 = SettingPreference.getBoolean("BOSS戰山神之森",false);
    private boolean 墮落根源 = SettingPreference.getBoolean("BOSS戰墮落的根源",false);
    private boolean 村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
    private boolean 裝備分解_開關 = false,裝備選定 = false;

    private boolean 抽獎完成 = false,检查个数完成 = false,密碼設置成功 = false,引繼碼賬號輸入完成 = false,引繼碼密碼輸入完成 = false
            ,引繼成功 = false,斷線等待 = false,任務重置_開關 = true,MAX選中 = false, 隊伍選中 = false,滿好感度 = false,自動編隊 = false;
    private boolean 金幣本68 = false,金幣本1113 = false,金幣本1921 = false,金幣本自動降級 = false,材料本自動降級 = false,村莊刷好感停止 = false,村莊刷好感失敗 = false;
    private boolean 等待體力回復 = false,通關完成 = false,買食材選好村莊 = false,肉塊購買 = false,野菜購買 = false,料理完成 = false,主線自動編成 = false;
    private boolean easy任務 = false,normal任務 = false,hard任務 = false,刷食材 = false,購買食材_開關 = false;
    private boolean 日常材料本_開關 = false,日常普通本_開關 = false,日常強化任務_開關 = false,日常製作料理_開關 = false,日常BOSS戰_開關 = false,日常PVP戰鬥_開關 = false;
    private boolean 刷裝備完成一次 = false,殲滅戰發生 = false,殲滅戰_開關 = false,殲滅戰招募完成 = false,少隊友 = true;
    private boolean 巴尼亞村踢箱子 = true,達瑪麗鎮踢箱子 = false,塔拉踢箱子 = false,貝塞爾鎮踢箱子 = false,奧登村踢箱子 = false,里奧內斯城踢箱子 = false,競技場踢箱子 = true,村莊踢箱子中 = false,酒館踢箱子中 = true;
    private boolean 當前金幣本 = false,當前裝備本 = false,當前材料本 = false,當前進化本 = false,當前BOSS戰 = false;

    private String 抽卡個數Str = String.valueOf((SettingPreference.getInt("抽卡個數", 0)));
    private String 保存遊戲名 = SettingPreference.getString("保存遊戲名","").replace(" ","");
    private String 保存密碼 = SettingPreference.getString("保存密碼","").replace(" ","");
    private String 引繼賬號 = SettingPreference.getString("引繼碼賬號","").replace(" ","");
    private String 引繼密碼 = SettingPreference.getString("引繼碼密碼","").replace(" ","");
    private String 卡死重啟時長 = SettingPreference.getString("卡死重啟時長","").replace(" ","");
    private String 金幣本挑戰等級 = SettingPreference.getString("金幣本等級","").replace(" ","");
    private String 金幣本挑戰隊伍 = SettingPreference.getString("金幣本選隊伍","").replace(" ","");
    private String 金幣本買藥 = SettingPreference.getString("金幣本體力使用","").replace(" ","");
    private String 材料本類型 = SettingPreference.getString("材料本類型","").replace(" ","");
    private String 材料本挑戰等級 = SettingPreference.getString("材料本等級","").replace(" ","");
    private String 材料本挑戰隊伍 = SettingPreference.getString("材料本選隊伍","").replace(" ","");
    private String 材料本買藥 = SettingPreference.getString("材料本體力使用","").replace(" ","");
    private String 村莊好感度村莊 = SettingPreference.getString("村莊好感度村莊","").replace(" ","");
    private String 村莊好感度挑戰隊伍 = SettingPreference.getString("村莊好感度隊伍","").replace(" ","");
    private String 村莊好感度買藥 = SettingPreference.getString("村莊好感度體力使用","").replace(" ","");
    private String 主線停止模式 = String.valueOf((SettingPreference.getInt("主線停止", 0)));
    private String 掛機刷圖模式 = String.valueOf(SettingPreference.getInt("掛機刷圖",0));
    private String 強化本等級 = SettingPreference.getString("強化本等級","");
    private String 進化本等級 = SettingPreference.getString("進化本等級","");
    private String 刷裝備類型 = SettingPreference.getString("裝備任務類型","");
    private String BOSS戰等級 = SettingPreference.getString("BOSS戰選等級","");
    private String 殲滅戰等級 = SettingPreference.getString("殲滅戰等級","");
    private String 多日未登錄獎勵 = SettingPreference.getString("多日未登錄獎勵","");
    private String 日常BOSS戰關卡 = SettingPreference.getString("日常BOSS戰選關卡","");
    private String 日常BOSS戰等級 = SettingPreference.getString("日常BOSS戰選等級","");
    private String 日常普通本類型 = SettingPreference.getString("日常普通本選類型","");
    private String 日常材料本關卡 = SettingPreference.getString("日常材料本選類型","");
    private String 日常材料本等級 = SettingPreference.getString("日常材料本選等級","");
    private String oldRGB = "";
    private Goods obj = null;

    private int 設置體力藥數量 = 0,設置鑽石數量 = 0;
    private int 抽卡個數 = 0,實際個數 = 0,鑽石數量 = 0,體力藥數量 = 0;
    private int 重置任務時間 = 0,斷線重連時長 = 0,每日抽計數 = 3,郵件選擇類型計數 = 3,團長料理計數 = 3;

    private long 斷線等待時間 = System.currentTimeMillis(),t_卡死重啟 = 0,卡死重啟剩餘時間 = 0,t_體力回復 = 0,t_村莊踢箱子 = 0;


    public ScriptThread(Context context, ScriptServiceListener scriptServiceListener) {
        super(context, scriptServiceListener);
        init(context);
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
        taskAction.addLayerAction("未知頁面","確認","開始任務","指引正上","指引左上","指引左上1","指引左下","指引右上1","指引右上2","指引右下","等待");
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
        taskAction.addLayerAction("通關成功頁面","確認");
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
        taskAction.addLayerAction("卡列表頁面",new UnitAction("打开设置", new UnitCallback() {
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
        }),new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                Point[] points = findMultiColors(23, 128, 710, 1065,"cd70bc-050505","-6|4|ffffff-050505,-3|10|f6c9f6-050505,6|12|6c42d5-050505,18|18|5b33d2-050505,12|26|5ea6ea-050505,28|17|fff5ff-050505,32|32|81d4f8-050505",0.95f);
                if (points.length != 0){
                    實際個數 = points.length;
//                    if (實際個數 >= 2){
//                        Shell.Sync.su("screencap -p /sdcard/Pictures/Screenshots/" + new Date().getTime() + ".png");
//                    }
                    检查个数完成 = true;
                }
            }
        }));
        taskAction.addLayerAction("設置頁面","打開信息");
        taskAction.addLayerAction("信息頁面", new UnitAction("重置數據", new UnitCallback() {
            @Override
            public boolean before() {
                if (抽卡個數 > 實際個數) {
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
                if (抽卡個數 <= 實際個數){
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
                Shell.Sync.su("screencap -p /sdcard/Pictures/Screenshots/" + new Date().getTime() + ".png");
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
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("打開商店", new UnitCallback() {
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
        taskAction.addLayerAction("商店頁面2",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                每日抽裝備_開關 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + 保存遊戲名 + "每日抽裝備",true);
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
                Setting.putBoolean(DateUtil.getNowDateStr() + 保存遊戲名 + "每日抽裝備",true);
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
                Setting.putBoolean(DateUtil.getNowDateStr() + 保存遊戲名 + "每日抽裝備",true);
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
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("打開商店", new UnitCallback() {
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
        taskAction.addLayerAction("商店頁面2","兌換",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                每日領寶箱_開關 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + 保存遊戲名 + "每日領寶箱",true);
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
                Setting.putBoolean(DateUtil.getNowDateStr() + 保存遊戲名 + "每日領寶箱",true);
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
                Setting.putBoolean(DateUtil.getNowDateStr() + 保存遊戲名 + "每日領寶箱",true);
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
                Setting.putBoolean(DateUtil.getNowDateStr() + 保存遊戲名 + "每日領寶箱",true);
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
                Setting.putBoolean(DateUtil.getNowDateStr() + 保存遊戲名 + "每日領寶箱",true);
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
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("進入抽獎", new UnitCallback() {
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
                Setting.putBoolean(DateUtil.getNowDateStr() + 保存遊戲名 + "一鑽石抽卡",true);
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
                Setting.putBoolean(DateUtil.getNowDateStr() + 保存遊戲名 + "一鑽石抽卡",true);
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
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("打開郵件", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("城鎮頁面","打開郵件");
        taskAction.addLayerAction("郵件頁面","領取","一鍵取消","一鍵領取","打開郵件","選擇類型",new UnitAction("關閉", new UnitCallback() {
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
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("打開任務", new UnitCallback() {
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
        taskAction.addLayerAction("功績頁面","一鍵領取","選擇類型","選擇類型1","領取鑽石",new UnitAction("返回", new UnitCallback() {
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
                    NPCBean obj = new Gson().fromJson(tem, NPCBean.class);
                    if (obj.getData().size() > 0) {
                        for (int i = 0; i < obj.getData().size(); i++) {
                            if (obj.getData().get(i).getRole() == 37 && obj.getData().get(i).isActive()) {
                                socketClient.sendMessage("{\"e\":2002,\"x\":" + obj.getData().get(i).getX() + ",\"y\":" + obj.getData().get(i).getY() + ",\"z\":" + obj.getData().get(i).getZ() + "}");
                                delay(5000);
                            }
                        }
                    }
                    酒館踢箱子中 = false;
                }
            }
        }),new UnitAction("打開戰鬥", new UnitCallback() {
            @Override
            public boolean before() {
                if (競技場踢箱子){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

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
        taskAction.addLayerAction("戰鬥內頁面","打開PVP");
        taskAction.addLayerAction("競技場頁面",new UnitAction("進入酒館", new UnitCallback() {
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
                if (競技場踢箱子){

                    return true;
                }else {
                    return false;
                }
            }

            @Override
            public void after() {
                if (socketClient.isConnected()) {
                    String tem = socketClient.sendMessageResult("{\"e\":1003}");
                    NPCBean obj = new Gson().fromJson(tem, NPCBean.class);
                    if (obj.getData().size() > 0) {
                        for (int i = 0; i < obj.getData().size(); i++) {
                            if (obj.getData().get(i).getRole() == 37 && obj.getData().get(i).isActive()) {
                                Log.e("TAG", "{\"e\":2002,\"x\":" + obj.getData().get(i).getX() + ",\"y\":" + obj.getData().get(i).getY() + ",\"z\":" + obj.getData().get(i).getZ() + "}");
                                socketClient.sendMessage("{\"e\":2002,\"x\":" + obj.getData().get(i).getX() + ",\"y\":" + obj.getData().get(i).getY() + ",\"z\":" + obj.getData().get(i).getZ() + "}");
                                delay(5000);
                            }
                        }
                    }
                    競技場踢箱子 = false;
                }
            }
        }),"打開世界");
        taskAction.addLayerAction("城鎮頁面",new UnitAction("進入酒館", new UnitCallback() {
            @Override
            public boolean before() {
                if (酒館踢箱子中 || 競技場踢箱子) {
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
                    NPCBean obj = new Gson().fromJson(tem, NPCBean.class);
                    if (obj.getData().size() > 0) {
                        for (int i = 0; i < obj.getData().size(); i++) {
                            if (obj.getData().get(i).getRole() == 37 && obj.getData().get(i).isActive()) {
                                socketClient.sendMessage("{\"e\":2002,\"x\":" + obj.getData().get(i).getX() + ",\"y\":" + obj.getData().get(i).getY() + ",\"z\":" + obj.getData().get(i).getZ() + "}");
                                delay(5000);
                            }
                            if (obj.getData().get(i).getRole() == 22 && obj.getData().get(i).isActive()) {
                                socketClient.sendMessage("{\"e\":2002,\"x\":" + obj.getData().get(i).getX() + ",\"y\":" + obj.getData().get(i).getY() + ",\"z\":" + obj.getData().get(i).getZ() + "}");
                                delay(5000);
                            }
                        }
                    }
                    村莊踢箱子中 = false;
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
        taskAction.addLayerAction("章節村莊頁面",new UnitAction("巴尼亞村", new UnitCallback() {
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
        taskAction.addLayerAction("酒館頁面","清算营业","打開設置");
        taskAction.addLayerAction("城鎮頁面","打開設置");
        taskAction.addLayerAction("設置頁面","打開好友");
        taskAction.addLayerAction("好友頁面","全部贈送", new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                贈送友情點_開關 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + 保存遊戲名 + "贈送友情點",true);
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
        taskAction.addLayerAction("酒館頁面","清算营业","打開商店");
        taskAction.addLayerAction("商店頁面1","上滑");
        taskAction.addLayerAction("商店頁面2","幣換物");
        taskAction.addLayerAction("彩幣兌換頁面","友情點兌換");
        taskAction.addLayerAction("金幣兌換頁面","友情點兌換");
        taskAction.addLayerAction("銀幣兌換頁面","友情點兌換");
        taskAction.addLayerAction("友情幣兌換頁面","選中體力藥",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                友情點買藥_開關 = false;
                Setting.putBoolean(DateUtil.getNowDateStr() + 保存遊戲名 + "友情點買藥",true);
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
                Setting.putBoolean(DateUtil.getNowDateStr() + 保存遊戲名 + "友情點買藥",true);
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
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("打開騎士團", new UnitCallback() {
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
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("祈禱頁面",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                騎士團祈禱 = false;
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
    private TaskAction initJinBiBen68(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮頁面","打開戰鬥",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                金幣本68 = false;
                當前金幣本 = false;
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
                金幣本68 = false;
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
                return true;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                金幣本68 = false;
                當前金幣本 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("副本選關頁面","金幣" + 金幣本挑戰等級);
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
        }),"start");
        taskAction.addLayerAction("入場體力不足頁面",new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                if (金幣本買藥.equals("停止任務")){
                    金幣本68 = false;
                    當前金幣本 = false;
                    return true;
                }else if (金幣本買藥.equals("等待回復體力")){
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
                if (obj==null){
                    if (socketClient.isConnected()) {
                        String tem = socketClient.sendMessageResult("{\"e\":1002}");
                        Goods obj = new Gson().fromJson(tem, Goods.class);
                        if (obj.getData().getBuyDiaCount() != 0 || obj.getData().getAPItemCount() != 0) {
                            鑽石數量 = obj.getData().getBuyDiaCount();
                            體力藥數量 = obj.getData().getAPItemCount();
                        }
                    }
                }
                if (體力藥數量 <= 設置體力藥數量){
                    等待體力回復 = true;
                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                    t_體力回復 = System.currentTimeMillis();
                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                    return false;
                }else {
                    if (金幣本買藥.equals("用體力藥") || 金幣本買藥.equals("用鑽石購買")) {
                        return true;
                    }else{
                        return false;
                    }
                }
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("使用體力藥頁面", new UnitAction("使用", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
            }
        }), new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                金幣本68 = false;
                當前金幣本 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("鑽石買體力頁面",new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                金幣本68 = false;
                當前金幣本 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥頁面","AUTO");
        taskAction.addLayerAction("通關成功頁面","ok");
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
        }));
        taskAction.addLayerAction("已獲得獎勵頁面","確認");
        return taskAction;
    }
    private TaskAction initJinBiBen1113(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮頁面","打開戰鬥",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                金幣本1113 = false;
                當前金幣本 = false;
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
                金幣本1113 = false;
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
                return true;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                金幣本1113 = false;
                當前金幣本 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("副本選關頁面",new UnitAction("金幣" + 金幣本挑戰等級, new UnitCallback() {
            @Override
            public boolean before() {
                if (當前金幣本){
                    return true;
                }else {
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),"返回");
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
        }),"start");
        taskAction.addLayerAction("入場體力不足頁面",new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                if (金幣本買藥.equals("停止任務")){
                    金幣本1113 = false;
                    當前金幣本 = false;
                    return true;
                }else if (金幣本買藥.equals("等待回復體力")){
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
                if (obj==null){
                    if (socketClient.isConnected()){
                        String tem = socketClient.sendMessageResult("{\"e\":1002}");
                        Goods obj = new Gson().fromJson(tem, Goods.class);
                        if (obj.getData().getBuyDiaCount()!=0 || obj.getData().getAPItemCount()!=0){
                            鑽石數量 = obj.getData().getBuyDiaCount();
                            體力藥數量 = obj.getData().getAPItemCount();
                        }
                    }
                }
                if (體力藥數量 <= 設置體力藥數量){
                    等待體力回復 = true;
                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                    t_體力回復 = System.currentTimeMillis();
                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                    return false;
                }else {
                    if (金幣本買藥.equals("用體力藥")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("使用體力藥頁面", new UnitAction("使用", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                體力藥數量--;
            }
        }), new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                金幣本1113 = false;
                當前金幣本 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("鑽石買體力頁面",new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                金幣本1113 = false;
                當前金幣本 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥頁面","AUTO");
        taskAction.addLayerAction("通關成功頁面","ok");
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
        }));
        taskAction.addLayerAction("已獲得獎勵頁面","確認");
        return taskAction;
    }
    private TaskAction initJinBiBen1921(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮頁面","打開戰鬥",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                金幣本1921 = false;
                當前金幣本 = false;
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
                金幣本1921 = false;
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
                return true;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                金幣本1921 = false;
                當前金幣本 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("副本選關頁面",new UnitAction("金幣" + 金幣本挑戰等級, new UnitCallback() {
            @Override
            public boolean before() {
                if (當前金幣本){
                    return true;
                }else {
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),"返回");
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
        }),"start");
        taskAction.addLayerAction("入場體力不足頁面",new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                if (金幣本買藥.equals("停止任務")){
                    金幣本1921 = false;
                    當前金幣本 = false;
                    return true;
                }else if (金幣本買藥.equals("等待回復體力")){
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
                if (obj==null){
                    if (socketClient.isConnected()){
                        String tem = socketClient.sendMessageResult("{\"e\":1002}");
                        Goods obj = new Gson().fromJson(tem, Goods.class);
                        if (obj.getData().getBuyDiaCount()!=0 || obj.getData().getAPItemCount()!=0){
                            鑽石數量 = obj.getData().getBuyDiaCount();
                            體力藥數量 = obj.getData().getAPItemCount();
                        }
                    }
                }
                if (體力藥數量 <= 設置體力藥數量){
                    等待體力回復 = true;
                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                    t_體力回復 = System.currentTimeMillis();
                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                    return false;
                }else {
                    if (金幣本買藥.equals("用體力藥")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("使用體力藥頁面", new UnitAction("使用", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                體力藥數量--;
            }
        }), new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                金幣本1921 = false;
                當前金幣本 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("鑽石買體力頁面",new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                金幣本1921 = false;
                當前金幣本 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥頁面","AUTO");
        taskAction.addLayerAction("通關成功頁面","ok");
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
                材料本_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥內頁面","打開副本");
        taskAction.addLayerAction("副本頁面","材料本",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                材料本_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("副本選關頁面","材料" + 材料本挑戰等級,new UnitAction(材料本類型, new UnitCallback() {
            @Override
            public boolean before() {
                return true;
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
            }
        }),"等待");
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
        }),"start");
        taskAction.addLayerAction("入場體力不足頁面",new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                if (材料本買藥.equals("停止任務")){
                    材料本_開關 = false;
                    return true;
                }else if (材料本買藥.equals("等待回復體力")){
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
                if (obj==null){
                    if (socketClient.isConnected()){
                        String tem = socketClient.sendMessageResult("{\"e\":1002}");
                        Goods obj = new Gson().fromJson(tem, Goods.class);
                        if (obj.getData().getBuyDiaCount()!=0 || obj.getData().getAPItemCount()!=0){
                            鑽石數量 = obj.getData().getBuyDiaCount();
                            體力藥數量 = obj.getData().getAPItemCount();
                        }
                    }
                }
                if (體力藥數量 <= 設置體力藥數量){
                    等待體力回復 = true;
                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                    t_體力回復 = System.currentTimeMillis();
                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                    return false;
                }else {
                    if (材料本買藥.equals("用體力藥")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("使用體力藥頁面", new UnitAction("使用", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                體力藥數量--;
            }
        }), new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                材料本_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("鑽石買體力頁面",new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                材料本_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥頁面","AUTO");
        taskAction.addLayerAction("通關成功頁面","ok");
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
        taskAction.addLayerAction("城鎮頁面","打開任務");
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("打開任務", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("故事進行中頁面",村莊好感度村莊,new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                村莊好感度_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("故事村莊頁面",new UnitAction("好感度滿", new UnitCallback() {
            @Override
            public boolean before() {
                if (村莊刷好感停止){
                    滿好感度 = true;
                }
                return false;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                if (滿好感度){
                    村莊好感度_開關 = false;
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                滿好感度 = false;
            }
        }),"開始", new UnitAction("前往", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                Point point = getMscdPos();
                //easy判定
                Point[] point1s = findMultiColors(point.x - 295,point.y - 58,point.x + 159,point.y + 61,"6d4930-101010","0|12|6d4930-101010,23|8|6d4930-101010,7|6|6e4a31-101010,35|9|6d4930-101010,45|7|6d4930-101010",0.95f);
                if (point1s.length > 0){
                    easy任務 = true;
                    normal任務 = false;
                    hard任務 = false;
                }else{
                    //normal判定
                    Point[] point21s = findMultiColors(point.x - 295,point.y - 58,point.x + 159,point.y + 61,"6d4930-101010","10|12|6d4930-101010,19|9|6d4930-101010,28|9|6d4930-101010,36|7|6d4930-101010,48|7|6d4930-101010,82|10|6d4930-101010,92|6|6d4930-101010",0.95f);
                    Point[] point22s = findMultiColors(point.x - 295,point.y - 58,point.x + 159,point.y + 61,"6d4930-101010","9|9|6d4930-101010,15|7|6d4930-101010,23|7|6d4930-101010,29|7|6d4930-101010,39|7|6d4930-101010,46|7|6d4930-101010,53|7|6d4930-101010,66|6|6d4930-101010,74|6|6d4930-101010",0.95f);
                    if (point21s.length > 0 || point22s.length > 0){
                        easy任務 = false;
                        normal任務 = true;
                        hard任務 = false;
                    }else{
                        //hard判定
                        Point[] point3s = findMultiColors(point.x - 295,point.y - 58,point.x + 159,point.y + 61,"6d4930-101010","0|13|6d4930-101010,13|13|6d4930-101010,13|-1|6d4930-101010,29|9|6d4930-101010,39|9|6d4930-101010,50|10|6d4930-101010,58|2|6d4930-101010",0.95f);
                        if (point3s.length > 0){
                            easy任務 = false;
                            normal任務 = false;
                            hard任務 = true;
                        }else{
                            easy任務 = false;
                            normal任務 = false;
                            hard任務 = false;
                        }
                    }
                }
                Point[] point4s = findMultiColors(point.x - 295,point.y - 58,point.x + 159,point.y + 61,"7a593f-101010","0|4|6e4a31-101010,-5|12|704c33-101010,16|13|6d4930-101010,16|5|6d4930-101010,28|4|6d4930-101010,28|14|6d4930-101010,26|6|6d4930-101010",0.95f);
                if (point4s.length > 0){
                    刷食材 = true;
                }else{
                    刷食材 = false;
                }
                delay(1000);
            }
        }),"上滑","等待");
        taskAction.addLayerAction("副本選關頁面",new UnitAction("返回", new UnitCallback() {
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
                通關完成 = false;
            }
        }),new UnitAction("上滑", new UnitCallback() {
            @Override
            public boolean before() {
                if (刷食材){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),new UnitAction("料理easy", new UnitCallback() {
            @Override
            public boolean before() {
                if (刷食材) {
                    if (easy任務) {
                        return true;
                    } else {
                        return false;
                    }
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),new UnitAction("料理normal", new UnitCallback() {
            @Override
            public boolean before() {
                if (刷食材) {
                    if (normal任務){
                        return true;
                    }else{
                        return false;
                    }
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),new UnitAction("料理hard", new UnitCallback() {
            @Override
            public boolean before() {
                if (刷食材) {
                    if (hard任務){
                        return true;
                    }else{
                        return false;
                    }
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),new UnitAction("easy關卡", new UnitCallback() {
            @Override
            public boolean before() {
                if (easy任務){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),new UnitAction("normal關卡", new UnitCallback() {
            @Override
            public boolean before() {
                if (normal任務){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),new UnitAction("hard關卡", new UnitCallback() {
            @Override
            public boolean before() {
                if (hard任務){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),"任務需要");
        taskAction.addLayerAction("戰鬥準備頁面",new UnitAction(村莊好感度挑戰隊伍, new UnitCallback() {
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
                if (村莊好感度買藥.equals("停止任務")){
                    村莊好感度_開關 = false;
                    return true;
                }else if (村莊好感度買藥.equals("等待回復體力")){
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
                if (obj==null){
                    if (socketClient.isConnected()){
                        String tem = socketClient.sendMessageResult("{\"e\":1002}");
                        Goods obj = new Gson().fromJson(tem, Goods.class);
                        if (obj.getData().getBuyDiaCount()!=0 || obj.getData().getAPItemCount()!=0){
                            鑽石數量 = obj.getData().getBuyDiaCount();
                            體力藥數量 = obj.getData().getAPItemCount();
                        }
                    }
                }
                if (體力藥數量 <= 設置體力藥數量){
                    等待體力回復 = true;
                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                    t_體力回復 = System.currentTimeMillis();
                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                    return false;
                }else {
                    if (村莊好感度買藥.equals("用體力藥")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("使用體力藥頁面", new UnitAction("使用", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                體力藥數量--;
            }
        }), new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                材料本_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("鑽石買體力頁面",new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                村莊好感度_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥頁面","AUTO","等待");
        taskAction.addLayerAction("通關成功頁面",new UnitAction("ok", new UnitCallback() {
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
                    村莊好感度_開關 = false;
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
        taskAction.addLayerAction("對話接任務頁面","領取");
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
        return taskAction;
    }

    /**
     * 購買食材
     * @return
     */
    private TaskAction initGouMaiShiCai(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("打開世界", new UnitCallback() {
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
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("打開任務", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("故事進行中頁面","主線");
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
        }));
        taskAction.addLayerAction("入場體力不足頁面","確定");
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
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("打開任務", new UnitCallback() {
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
                日常普通本_開關 = true;
                return true;
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("裝備強化", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getBoolean("日常強化任務",false)){
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
                日常製作料理_開關 = true;
                return true;
            }

            @Override
            public void after() {

            }
        }),
                new UnitAction("BOSS戰", new UnitCallback() {
                    @Override
                    public boolean before() {
                        if (SettingPreference.getBoolean("日常BOSS戰",false)){
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
                        if (SettingPreference.getBoolean("日常BOSS戰",false)){
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
                        每日任務_開關 = false;
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
                日常材料本_開關 = false;
                當前材料本 = false;
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
                return true;
            }

            @Override
            public void after() {

            }
        }),new UnitAction("返回", new UnitCallback() {
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
        taskAction.addLayerAction("副本選關頁面",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                if (當前材料本){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {

            }
        }),"材料" + 日常材料本等級,new UnitAction(日常材料本關卡, new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                if(日常材料本等級.equals("強化")){
                    swipe(292,497,311,379,2000);
                }else if (日常材料本等級.equals("金幣")){
                    swipe(306,670,318,386,2000);
                }else{
                    swipe(340,839,358,380,2000);
                }
            }
        }),"等待");
        taskAction.addLayerAction("戰鬥準備頁面","start");
        taskAction.addLayerAction("入場體力不足頁面",new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                等待體力回復 = true;
                村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                t_體力回復 = System.currentTimeMillis();
                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("鑽石買體力頁面",new UnitAction("關閉", new UnitCallback() {
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
        taskAction.addLayerAction("戰鬥頁面","AUTO","等待");
        taskAction.addLayerAction("通關成功頁面","ok");
        taskAction.addLayerAction("通關失敗頁面",new UnitAction("ok", new UnitCallback() {
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
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("打開世界", new UnitCallback() {
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
                }
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("副本選關頁面",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                if (當前裝備本){
                    return false;
                }else{
                    return true;
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
                t_體力回復 = System.currentTimeMillis();
                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥頁面","AUTO","等待");
        taskAction.addLayerAction("通關成功頁面",new UnitAction("ok", new UnitCallback() {
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
        taskAction.addLayerAction("通關失敗頁面",new UnitAction("ok", new UnitCallback() {
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
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("打開卡包", new UnitCallback() {
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
        taskAction.addLayerAction("卡裝備頁面","強化","選SSR","選SR","選R",new UnitAction("返回", new UnitCallback() {
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
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("料理", new UnitCallback() {
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
                日常製作料理_開關 = false;
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
                日常BOSS戰_開關 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥內頁面","打開BOSS戰");
        taskAction.addLayerAction("BOSS戰選關頁面",日常BOSS戰關卡);
        taskAction.addLayerAction("副本選關頁面",日常BOSS戰等級);
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
                t_體力回復 = System.currentTimeMillis();
                成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("鑽石買體力頁面",new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥頁面","AUTO","等待");
        taskAction.addLayerAction("通關成功頁面",new UnitAction("ok", new UnitCallback() {
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
                日常PVP戰鬥_開關 = false;
                return true;
            }

            @Override
            public void after() {

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
        taskAction.addLayerAction("PVP一般頁面",new UnitAction("自動編隊", new UnitCallback() {
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
        }),new UnitAction("檢索", new UnitCallback() {
            @Override
            public boolean before() {
                自動編隊 = false;
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
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥內頁面","打開副本");
        taskAction.addLayerAction("副本頁面","強化本",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("副本選關頁面",強化本等級,"上滑","返回");
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
                if (obj==null){
                    if (socketClient.isConnected()){
                        String tem = socketClient.sendMessageResult("{\"e\":1002}");
                        Goods obj = new Gson().fromJson(tem, Goods.class);
                        if (obj.getData().getBuyDiaCount()!=0 || obj.getData().getAPItemCount()!=0){
                            鑽石數量 = obj.getData().getBuyDiaCount();
                            體力藥數量 = obj.getData().getAPItemCount();
                        }
                    }
                }
                if (體力藥數量 <= 設置體力藥數量){
                    等待體力回復 = true;
                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                    t_體力回復 = System.currentTimeMillis();
                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                    return false;
                }else {
                    if (SettingPreference.getString("強化本體力使用", "").equals("用體力藥")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("使用體力藥頁面", new UnitAction("使用", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                體力藥數量--;
            }
        }),"關閉");
        taskAction.addLayerAction("鑽石買體力頁面","關閉");
        taskAction.addLayerAction("戰鬥頁面","AUTO","等待");
        taskAction.addLayerAction("通關成功頁面","ok");
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
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥內頁面","打開副本");
        taskAction.addLayerAction("副本頁面",new UnitAction("進化本", new UnitCallback() {
            @Override
            public boolean before() {
                當前進化本 = true;
                return true;
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
        taskAction.addLayerAction("副本選關頁面",new UnitAction(進化本等級, new UnitCallback() {
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
        }),"上滑","返回");
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
        taskAction.addLayerAction("入場體力不足頁面",new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getString("進化本體力使用","").equals("等待回復體力")){
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
                if (obj==null){
                    if (socketClient.isConnected()){
                        String tem = socketClient.sendMessageResult("{\"e\":1002}");
                        Goods obj = new Gson().fromJson(tem, Goods.class);
                        if (obj.getData().getBuyDiaCount()!=0 || obj.getData().getAPItemCount()!=0){
                            鑽石數量 = obj.getData().getBuyDiaCount();
                            體力藥數量 = obj.getData().getAPItemCount();
                        }
                    }
                }
                if (體力藥數量 <= 設置體力藥數量){
                    等待體力回復 = true;
                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                    t_體力回復 = System.currentTimeMillis();
                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                    return false;
                }else {
                    if (SettingPreference.getString("進化本體力使用", "").equals("用體力藥")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("使用體力藥頁面", new UnitAction("使用", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                體力藥數量--;
            }
        }),"關閉");
        taskAction.addLayerAction("鑽石買體力頁面","關閉");
        taskAction.addLayerAction("戰鬥頁面","AUTO","等待");
        taskAction.addLayerAction("通關成功頁面","ok");
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
        taskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("打開世界", new UnitCallback() {
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
                刷裝備完成一次 = false;
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
        taskAction.addLayerAction("副本選關頁面",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                if (刷裝備完成一次){
                    return true;
                }else {
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }),"hard","normal","返回");
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
        taskAction.addLayerAction("入場體力不足頁面",new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getString("裝備任務體力使用","").equals("等待回復體力")){
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
                if (obj==null){
                    if (socketClient.isConnected()){
                        String tem = socketClient.sendMessageResult("{\"e\":1002}");
                        Goods obj = new Gson().fromJson(tem, Goods.class);
                        if (obj.getData().getBuyDiaCount()!=0 || obj.getData().getAPItemCount()!=0){
                            鑽石數量 = obj.getData().getBuyDiaCount();
                            體力藥數量 = obj.getData().getAPItemCount();
                        }
                    }
                }
                if (體力藥數量 <= 設置體力藥數量){
                    等待體力回復 = true;
                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                    t_體力回復 = System.currentTimeMillis();
                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                    return false;
                }else {
                    if (SettingPreference.getString("裝備任務體力使用", "").equals("用體力藥")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("使用體力藥頁面", new UnitAction("使用", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                體力藥數量--;
            }
        }),"關閉");
        taskAction.addLayerAction("鑽石買體力頁面","關閉");
        taskAction.addLayerAction("戰鬥頁面","AUTO","等待");
        taskAction.addLayerAction("通關成功頁面",new UnitAction("ok", new UnitCallback() {
            @Override
            public boolean before() {
                刷裝備完成一次 = true;
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
        }));
        taskAction.addLayerAction("已獲得獎勵頁面","確認");
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
        }),"返回");
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
        }), new UnitAction("殲滅戰發生", new UnitCallback() {
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
        }),new UnitAction(BOSS戰等級, new UnitCallback() {
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
        }),new UnitAction("返回", new UnitCallback() {
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
        taskAction.addLayerAction("入場體力不足頁面",new UnitAction("取消", new UnitCallback() {
            @Override
            public boolean before() {
                if (SettingPreference.getString("BOSS戰體力使用","").equals("等待回復體力")){
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
                if (obj==null){
                    if (socketClient.isConnected()){
                        String tem = socketClient.sendMessageResult("{\"e\":1002}");
                        Goods obj = new Gson().fromJson(tem, Goods.class);
                        if (obj.getData().getBuyDiaCount()!=0 || obj.getData().getAPItemCount()!=0){
                            鑽石數量 = obj.getData().getBuyDiaCount();
                            體力藥數量 = obj.getData().getAPItemCount();
                        }
                    }
                }
                if (體力藥數量 <= 設置體力藥數量){
                    等待體力回復 = true;
                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                    t_體力回復 = System.currentTimeMillis();
                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                    return false;
                }else {
                    if (SettingPreference.getString("BOSS戰體力使用", "").equals("用體力藥")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("使用體力藥頁面", new UnitAction("使用", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                體力藥數量--;
            }
        }),"關閉");
        taskAction.addLayerAction("鑽石買體力頁面","關閉");
        taskAction.addLayerAction("戰鬥頁面","AUTO","等待");
        taskAction.addLayerAction("通關成功頁面","ok");
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
        }));
        taskAction.addLayerAction("已獲得獎勵頁面","確認");
        return taskAction;
    }

    /**
     * 公共操作
     */
    private void initGongGong(){
        g_taskAction = new TaskAction();
        g_taskAction.addLayerAction("背包擴容頁面","前往倉庫");
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
        g_taskAction.addLayerAction("郵件頁面","關閉");
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
                引繼碼賬號輸入完成 = true;
                inputText(引繼賬號);
                delay(1000);
                tap(616,1205);
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
                引繼碼密碼輸入完成 = true;
                inputText(引繼密碼);
                delay(1000);
                tap(616,1205);
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
        g_taskAction.addLayerAction("網絡波動斷線頁面","重啟");
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
        g_taskAction.addLayerAction("加載頁面","等待");
        g_taskAction.addLayerAction("廣告頁面","關閉");
        g_taskAction.addLayerAction("使用條款頁面","同意");
        g_taskAction.addLayerAction("通知頁面","同意");
        g_taskAction.addLayerAction("新玩家獎勵頁面","領取");
        g_taskAction.addLayerAction("日常獎勵頁面","領取");
        g_taskAction.addLayerAction("一次獎勵頁面","領取");
        g_taskAction.addLayerAction("更新頁面","確定");
        g_taskAction.addLayerAction("未知頁面","取消","以後再說","ok","ok1","ok2","確認","廣告ok","等待");
        g_taskAction.addLayerAction("卡列表頁面","返回");
        g_taskAction.addLayerAction("設置頁面","返回");
        g_taskAction.addLayerAction("世界頁面","返回");
        g_taskAction.addLayerAction("商店頁面1","返回");
        g_taskAction.addLayerAction("商店頁面2","返回");
        g_taskAction.addLayerAction("抽獎頁面","返回");
        g_taskAction.addLayerAction("友情點確認頁面","確認");
        g_taskAction.addLayerAction("獎勵頁面","確定");
        g_taskAction.addLayerAction("功績頁面","返回");
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
        }));
        g_taskAction.addLayerAction("戰鬥內頁面","返回");
        g_taskAction.addLayerAction("副本頁面","金幣本獎勵領取","材料本獎勵領取","返回");
        g_taskAction.addLayerAction("副本選關頁面","返回");
        g_taskAction.addLayerAction("戰鬥準備頁面","返回");
        g_taskAction.addLayerAction("入場體力不足頁面","取消");
        g_taskAction.addLayerAction("使用體力藥頁面","關閉");
        g_taskAction.addLayerAction("鑽石買體力頁面","關閉");
        g_taskAction.addLayerAction("通關成功頁面","ok","確認");
        g_taskAction.addLayerAction("通關失敗頁面","ok");
        g_taskAction.addLayerAction("已獲得獎勵頁面","確認");
        g_taskAction.addLayerAction("任務領取獎勵頁面","領取");
        g_taskAction.addLayerAction("班料理頁面","返回");
        g_taskAction.addLayerAction("團長料理頁面","返回");
        g_taskAction.addLayerAction("買入食材頁面","關閉");
        g_taskAction.addLayerAction("雜貨商店頁面","返回");
        g_taskAction.addLayerAction("章節村莊頁面","返回");
        g_taskAction.addLayerAction("章節故事頁面","返回");
        g_taskAction.addLayerAction("對話接任務頁面","領取");
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
        g_taskAction.addLayerAction("百日獎勵頁面","ok");
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
        g_taskAction.addLayerAction("殲滅戰招募頁面","返回");
        g_taskAction.addLayerAction("殲滅戰戰鬥頁面","auto");
        g_taskAction.addLayerAction("殲滅戰勝利頁面","ok");
        g_taskAction.addLayerAction("殲滅戰失敗頁面","ok");
        g_taskAction.addLayerAction("騎士團聊天頁面","進入騎士團");
        g_taskAction.addLayerAction("騎士團內頁面","返回酒館");
    }

    /**
     * 裝備分解
     */
    private void initFenJieZhuangBei(){
        fenJieZhuangBeiTaskAction = new TaskAction();
        fenJieZhuangBeiTaskAction.addLayerAction("城鎮頁面","進入酒館");
        fenJieZhuangBeiTaskAction.addLayerAction("酒館頁面","清算营业",new UnitAction("裝備分解", new UnitCallback() {
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
        fenJieZhuangBeiTaskAction.addLayerAction("裝備一鍵選擇頁面",new UnitAction("選C", new UnitCallback() {
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
        }),new UnitAction("選UC", new UnitCallback() {
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
        }),new UnitAction("選R", new UnitCallback() {
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
        }),new UnitAction("選SR", new UnitCallback() {
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
        }),new UnitAction("選SSR", new UnitCallback() {
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
        }),new UnitAction("選猛攻", new UnitCallback() {
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
        }),new UnitAction("取消猛攻", new UnitCallback() {
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
        }),new UnitAction("選鐵壁", new UnitCallback() {
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
        }),new UnitAction("取消鐵壁", new UnitCallback() {
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
        }),new UnitAction("選生命", new UnitCallback() {
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
        }),new UnitAction("取消生命", new UnitCallback() {
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
        }),new UnitAction("選集中", new UnitCallback() {
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
        }),new UnitAction("取消集中", new UnitCallback() {
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
        }),new UnitAction("選心眼", new UnitCallback() {
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
        }),new UnitAction("取消心眼", new UnitCallback() {
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
        }),new UnitAction("選回復", new UnitCallback() {
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
        }),new UnitAction("取消回復", new UnitCallback() {
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
        }),new UnitAction("選會心", new UnitCallback() {
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
        }),new UnitAction("取消會心", new UnitCallback() {
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
        }),new UnitAction("選不屈", new UnitCallback() {
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
        }),new UnitAction("取消不屈", new UnitCallback() {
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
        }),new UnitAction("選吸血", new UnitCallback() {
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
        }),new UnitAction("取消吸血", new UnitCallback() {
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
        }),new UnitAction("選擇強化", new UnitCallback() {
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
        }),new UnitAction("取消強化", new UnitCallback() {
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
                return true;
            }

            @Override
            public void after() {

            }
        }),"返回");
        taskAction.addLayerAction("殲滅戰準備頁面",new UnitAction("招募", new UnitCallback() {
            @Override
            public boolean before() {
                if (殲滅戰招募完成){
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void after() {

            }
        }),new UnitAction("有隊友", new UnitCallback() {
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
        taskAction.addLayerAction("殲滅戰招募頁面","全部招募",new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                殲滅戰招募完成 = true;
                return true;
            }

            @Override
            public void after() {

            }
        }));
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
        taskAction.addLayerAction("入場體力不足頁面",new UnitAction("取消", new UnitCallback() {
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
                if (obj==null){
                    if (socketClient.isConnected()){
                        String tem = socketClient.sendMessageResult("{\"e\":1002}");
                        Goods obj = new Gson().fromJson(tem, Goods.class);
                        if (obj.getData().getBuyDiaCount()!=0 || obj.getData().getAPItemCount()!=0){
                            鑽石數量 = obj.getData().getBuyDiaCount();
                            體力藥數量 = obj.getData().getAPItemCount();
                        }
                    }
                }
                if (體力藥數量 <= 設置體力藥數量){
                    等待體力回復 = true;
                    村莊踢箱子_開關 = SettingPreference.getBoolean("村莊踢箱子",false);
                    t_體力回復 = System.currentTimeMillis();
                    成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
                    return false;
                }else{
                    if (SettingPreference.getString("殲滅戰體力使用","").equals("用體力藥")){
                        return true;
                    }else{
                        return false;
                    }
                }
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("使用體力藥頁面", new UnitAction("使用", new UnitCallback() {
            @Override
            public boolean before() {
                return true;
            }

            @Override
            public void after() {
                體力藥數量--;
            }
        }),"關閉");
        taskAction.addLayerAction("鑽石買體力頁面","關閉");
        taskAction.addLayerAction("戰鬥頁面","AUTO","等待");
        taskAction.addLayerAction("已獲得獎勵頁面","確認");
        return taskAction;
    }

    /**
     * 初始化原始數據
     */
    private void initData(){
        if (抽卡個數Str.equals("0")){
            抽卡個數 = 1;
        }else if (抽卡個數Str.equals("1")){
            抽卡個數 = 2;
        }else{
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
            金幣本68 = SettingPreference.getBoolean("金幣本6-8點",false);
            金幣本1113 = SettingPreference.getBoolean("金幣本11-13點",false);
            金幣本1921 = SettingPreference.getBoolean("金幣本19-21點",false);
            金幣本自動降級 = SettingPreference.getBoolean("金幣本智能降階",false);
        }
        if (材料本_開關){
            材料本自動降級 = SettingPreference.getBoolean("材料本智能降階",false);
        }
        if (村莊好感度_開關){
            村莊刷好感停止 = SettingPreference.getBoolean("村莊刷好感停止",false);
            村莊刷好感失敗 = SettingPreference.getBoolean("村莊刷好感失敗",false);
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
            }else{
                keepCapture();
                String curLayerName = null;
                TaskAction curTaskAction=null;
                curLayerName = getLayerName();
                /************************************判斷執行任務順序******************************/
                if (刷初始_開關){
                    curTaskAction = initShuaChuShi();
                    showHUDInfo("刷初始");
                }else{
                    if (裝備分解_開關){
                        curTaskAction = fenJieZhuangBeiTaskAction;
                        showHUDInfo("分解裝備");
                    }else if(殲滅戰_開關){
                        curTaskAction = initJianMieZhan();
                        showHUDInfo("殲滅戰");
                    }else if (購買食材_開關){
                        curTaskAction = initGouMaiShiCai();
                        showHUDInfo("購買食材");
                    }else if (每日抽裝備_開關 && !Setting.getBoolean(DateUtil.getNowDateStr() + 保存遊戲名 + "每日抽裝備",false)){
                        curTaskAction = initMeiRiChouZhuangBei();
                        showHUDInfo("每日抽裝備");
                    }else if (每日領寶箱_開關 && !Setting.getBoolean(DateUtil.getNowDateStr() + 保存遊戲名 + "每日領寶箱",false)){
                        curTaskAction = initMeiRiLingBaoXiang();
                        showHUDInfo("每日領寶箱");
                    }else if (一鑽石抽獎_開關 && !Setting.getBoolean(DateUtil.getNowDateStr() + 保存遊戲名 + "一鑽石抽卡",false)){
                        curTaskAction = initYiZuanShiChouKa();
                        showHUDInfo("一鑽石抽卡");
                    }else if (贈送友情點_開關 && !Setting.getBoolean(DateUtil.getNowDateStr() + 保存遊戲名 + "贈送友情點",false)){
                        curTaskAction = initSongYouQingDian();
                        showHUDInfo("贈送友情點");
                    }else if (友情點買藥_開關 && !Setting.getBoolean(DateUtil.getNowDateStr() + 保存遊戲名 + "友情點買藥",false)){
                        curTaskAction = initYouQingDianMaiYao();
                        showHUDInfo("友情點買藥");
                    }else if (騎士團祈禱){
                        curTaskAction = initQiShiTuanQiDao();
                        showHUDInfo("騎士團祈禱");
                    }else if (金幣本_開關 && 金幣本68 && TimingUtil.betweenHour(6,8)) {
                        curTaskAction = initJinBiBen68();
                        showHUDInfo("金幣本");
                    }else if (金幣本_開關 && 金幣本1113 && TimingUtil.betweenHour(11,13)){
                        curTaskAction = initJinBiBen1113();
                        showHUDInfo("金幣本");
                    }else if (金幣本_開關 && 金幣本1921 && TimingUtil.betweenHour(18,20)){
                        curTaskAction = initJinBiBen1921();
                        showHUDInfo("金幣本");
                    }else if (材料本_開關){
                        curTaskAction = initCaiLiaoBen();
                        showHUDInfo("材料本");
                    }else if (村莊好感度_開關){
                        curTaskAction = initCunZhuangHaoGanDu();
                        showHUDInfo("村莊刷好感度");
                    }else if (半自動主線_開關){
                        curTaskAction = initBanZiDongZhuXian();
                        showHUDInfo("半自動主線");
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
                    }else if (每日任務_開關){
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
                        }else if (掛機刷圖模式.equals("3")){
                            curTaskAction = initShuaTuBOSSZhan();
                            showHUDInfo("掛機BOSS戰");
                        }else{
                            curTaskAction = null;
                            showHUDInfo("暫無任務");
                        }
                    }
                }
                /*************************************斷線重連等待*********************************/
                if (斷線等待){
                    if (System.currentTimeMillis() - 斷線等待時間 < 斷線重連時長*60*1000){
                        curTaskAction = null;
                        g_taskAction = null;
                    }else{
                        斷線等待 = false;
                    }
                }else{
                    g_taskAction = null;
                    initGongGong();
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
                /**********************************等待體力恢復時的操作****************************/
                if (等待體力回復 && System.currentTimeMillis() - t_體力回復 < 5*60*1000){
                    Point[] points = findMultiColors(433, 1, 719, 111,"ffffff-101010","0|4|ffffff-101010,0|10|fffcfc-101010,0|19|ffffff-101010,-13|11|febc32-101010,12|11|fdba32-101010",0.95f);
                    if (points != null && points.length != 0) {
                        if (points.length > 0 && SettingPreference.getBoolean("領取郵件", false)) {
                            郵件領取_開關 = true;
                        }
                    }
                    if (郵件領取_開關){
                        curTaskAction = initYouJianLingQu();
                        showHUDInfo("郵件領取");
                    }else if (成就領取_開關){
                        curTaskAction = initChengJiuJiangLi();
                        showHUDInfo("成就領取");
                    }else if (村莊踢箱子_開關){
                        if (t_村莊踢箱子 == 0){
                            t_村莊踢箱子 = System.currentTimeMillis();
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
                Log.e(getTag(), "run:" + 體力藥數量);
                showMOVEInfo(curLayerName + "-" + getUnitName());
                try{
                    fileWriter(curLayerName + "-" + getUnitName(),file);
                }catch (IOException e){
                    e.printStackTrace();
                }
                /***************************************執行操作***********************************/
                if (execTask(curLayerName,curTaskAction)) {
                    delay(500);
                }else{
                    execTask(curLayerName,g_taskAction);
                }
                delay(500);
            }
        }
        scriptEnd();
    }

    @Override
    protected void scriptEnd() {
        socketClient.disconnect();
        super.scriptEnd();
    }

    public void fileWriter(String str, File file) throws IOException{
        FileWriter writer = new FileWriter(file,true);
        // 向文件写入内容
        writer.write(DateUtil.getNowTimestampStr() + "  " + str + "\n\n");
        writer.flush();
        writer.close();
    }
}
