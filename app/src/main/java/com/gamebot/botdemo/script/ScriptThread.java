package com.gamebot.botdemo.script;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import com.gamebot.botdemo.R;
import com.gamebot.botdemo.entity.TaskAction;
import com.gamebot.botdemo.entity.UnitAction;
import com.gamebot.botdemo.entity.UnitCallback;

import java.util.Date;

import com.gamebot.botdemo.utils.DateUtil;
import com.gamebot.botdemo.utils.TimingUtil;
import com.gamebot.sdk.preference.*;
import com.topjohnwu.superuser.Shell;


public class ScriptThread extends SuperScriptThread {
    private final static String gamePkg="com.netmarble.nanatsunotaizai";


    private TaskAction g_taskAction,每日抽_taskAction,一鑽石抽_taskAction,郵件領取_taskAction,成就領取_taskAction,送友情點_taskAction
            ,友情點買藥_taskAction;
    private boolean 刷初始_開關 = SettingPreference.getBoolean("刷初始開關",false);
    private boolean 使用引繼碼_開關 = SettingPreference.getBoolean("使用引繼碼",false);
    private boolean 每日免費抽_開關 = SettingPreference.getBoolean("每日免費抽",false);
    private boolean 一鑽石抽獎_開關 = SettingPreference.getBoolean("1鑽石抽卡",false);
    private boolean 郵件領取_開關 = false;
    private boolean 成就領取_開關 = SettingPreference.getBoolean("領取成就獎勵",false);
    private boolean 贈送友情點_開關 = SettingPreference.getBoolean("贈送友情點數",false);
    private boolean 友情點買藥_開關 = SettingPreference.getBoolean("友情點買體力",false);
    private boolean 金幣本_開關 = SettingPreference.getBoolean("金幣本",false);
    private boolean 材料本_開關 = SettingPreference.getBoolean("材料本",false);
    private boolean 村莊好感度_開關 = SettingPreference.getBoolean("村莊好感度",false);
    private boolean 半自動主線_開關 = SettingPreference.getBoolean("半自動主線",false);

    private boolean 抽獎完成 = false,检查个数完成 = false,密碼設置成功 = false,引繼碼賬號輸入完成 = false,引繼碼密碼輸入完成 = false
            ,引繼成功 = false,斷線等待 = false,任務重置_開關 = true,MAX選中 = false, 隊伍選中 = false,滿好感度 = false;
    private boolean 金幣本68 = false,金幣本1113 = false,金幣本1921 = false,金幣本自動降級 = false,材料本自動降級 = false,村莊刷好感停止 = false,村莊刷好感失敗 = false;
    private boolean 等待體力回復 = false,通關完成 = false,買食材選好村莊 = false,肉塊購買 = false,野菜購買 = false,料理完成 = false,主線自動編成 = false;
    private boolean easy任務 = false,normal任務 = false,hard任務 = false,刷食材 = false,購買食材_開關 = false;

    private String 抽卡個數Str = String.valueOf((SettingPreference.getInt("抽卡個數", 0)));
    private String 保存遊戲名 = SettingPreference.getString("保存遊戲名","").replace(" ","");
    private String 保存密碼 = SettingPreference.getString("保存密碼","").replace(" ","");
    private String 引繼賬號 = SettingPreference.getString("引繼碼賬號","").replace(" ","");
    private String 引繼密碼 = SettingPreference.getString("引繼碼密碼","").replace(" ","");
    private String 斷線等待時長 = SettingPreference.getString("斷線等待時長","").replace(" ","");
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
    private String oldRGB = "";

    private int 抽卡個數 = 0,實際個數 = 0;
    private int 重置任務時間 = 0,斷線重連時長 = 0,每日抽計數 = 3,郵件選擇類型計數 = 3,團長料理計數 = 3;

    private long 斷線等待時間 = 0,t_卡死重啟 = 0,卡死重啟剩餘時間 = 0,t_體力回復 = 0;


    public ScriptThread(Context context, ScriptServiceListener scriptServiceListener) {
        super(context, scriptServiceListener);
    }
    @Override
    protected String getTag() {
        return "qdzbot";
    }
    @Override
    protected String getAppName() {
        return context.getResources().getString(R.string.app_name);
    }



    private void initTaskAction() {
        initColorAction(false);
        initGongGong();
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
     * 抽裝備和領寶箱
     * @return
     */
    private TaskAction initMeiRiChouJiang(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("酒館頁面","清算营业","打開商店");
        taskAction.addLayerAction("城鎮頁面","打開商店");
        taskAction.addLayerAction("商店頁面1","可領","上滑");
        taskAction.addLayerAction("商店頁面2","可領",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                每日抽計數 --;
                if (每日抽計數 == 0){
                    每日免費抽_開關 = false;
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {
                每日抽計數 = 3;
            }
        }),"等待");
        taskAction.addLayerAction("裝備抽取頁面","每日一抽","返回");
        taskAction.addLayerAction("未知頁面","ok","廣告ok","等待");
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
        taskAction.addLayerAction("抽裝備結算頁面","返回");
        taskAction.addLayerAction("彩幣兌換頁面","可領","返回");
        taskAction.addLayerAction("金幣兌換頁面","可領","返回");
        taskAction.addLayerAction("銀幣兌換頁面","可領","返回");
        taskAction.addLayerAction("友情幣兌換頁面","可領","返回");

        return taskAction;
    }

    /**
     * 一鑽石抽卡
     * @return
     */
    private TaskAction initYiZuanShiChouKa(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("酒館頁面","清算营业","進入抽獎");
        taskAction.addLayerAction("城鎮頁面","進入抽獎");
        taskAction.addLayerAction("抽獎頁面","每日一抽","下一頁",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                一鑽石抽獎_開關 = false;
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
                一鑽石抽獎_開關 = true;
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
        taskAction.addLayerAction("酒館頁面","清算营业","打開郵件");
        taskAction.addLayerAction("城鎮頁面","打開郵件");
        taskAction.addLayerAction("郵件頁面","領取","一鍵領取","打開郵件","選擇類型",new UnitAction("關閉", new UnitCallback() {
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
        taskAction.addLayerAction("酒館頁面","清算营业","打開任務");
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
        taskAction.addLayerAction("日常任務頁面","打開功績",new UnitAction("返回", new UnitCallback() {
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
     * 贈送友情點
     * @return
     */
    private TaskAction initSongYouQingDian(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("酒館頁面","清算营业","打開設置");
        taskAction.addLayerAction("城鎮頁面","打開設置");
        taskAction.addLayerAction("設置頁面","打開好友");
        taskAction.addLayerAction("好友頁面","全部贈送",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                贈送友情點_開關 = false;
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
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("酒館頁面","清算营业","打開戰鬥",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                金幣本68 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥內頁面","打開副本");
        taskAction.addLayerAction("副本頁面","金幣本",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                金幣本68 = false;
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
                    return true;
                }else if (金幣本買藥.equals("等待回復體力")){
                    等待體力回復 = true;
                    t_體力回復 = System.currentTimeMillis();
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
                if (金幣本買藥.equals("用體力藥")){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("使用體力藥頁面", new UnitAction("使用", new UnitCallback() {
            @Override
            public boolean before() {
                if (金幣本買藥.equals("用體力藥")){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }), new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                金幣本68 = false;
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
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("酒館頁面","清算营业","打開戰鬥",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                金幣本1113 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥內頁面","打開副本");
        taskAction.addLayerAction("副本頁面","金幣本",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                金幣本1113 = false;
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
                    金幣本1113 = false;
                    return true;
                }else if (金幣本買藥.equals("等待回復體力")){
                    等待體力回復 = true;
                    t_體力回復 = System.currentTimeMillis();
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
                if (金幣本買藥.equals("用體力藥")){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("使用體力藥頁面", new UnitAction("使用", new UnitCallback() {
            @Override
            public boolean before() {
                if (金幣本買藥.equals("用體力藥")){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }), new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                金幣本1113 = false;
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
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("酒館頁面","清算营业","打開戰鬥",new UnitAction("等待", new UnitCallback() {
            @Override
            public boolean before() {
                金幣本1921 = false;
                return true;
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("戰鬥內頁面","打開副本");
        taskAction.addLayerAction("副本頁面","金幣本",new UnitAction("返回", new UnitCallback() {
            @Override
            public boolean before() {
                金幣本1921 = false;
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
                    金幣本1921 = false;
                    return true;
                }else if (金幣本買藥.equals("等待回復體力")){
                    等待體力回復 = true;
                    t_體力回復 = System.currentTimeMillis();
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
                if (金幣本買藥.equals("用體力藥")){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("使用體力藥頁面", new UnitAction("使用", new UnitCallback() {
            @Override
            public boolean before() {
                if (金幣本買藥.equals("用體力藥")){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }), new UnitAction("關閉", new UnitCallback() {
            @Override
            public boolean before() {
                金幣本1921 = false;
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
        taskAction.addLayerAction("酒館頁面","清算营业","打開戰鬥",new UnitAction("等待", new UnitCallback() {
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
                    t_體力回復 = System.currentTimeMillis();
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
                if (材料本買藥.equals("用體力藥")){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("使用體力藥頁面", new UnitAction("使用", new UnitCallback() {
            @Override
            public boolean before() {
                if (材料本買藥.equals("用體力藥")){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

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
        taskAction.addLayerAction("戰鬥頁面","AUTO","等待");
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
        taskAction.addLayerAction("酒館頁面","清算营业","打開任務");
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
        }),"開始",new UnitAction("前往", new UnitCallback() {
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
        taskAction.addLayerAction("刷圖選關頁面",new UnitAction("返回", new UnitCallback() {
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
                    t_體力回復 = System.currentTimeMillis();
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
                if (材料本買藥.equals("用體力藥")){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

            }
        }));
        taskAction.addLayerAction("使用體力藥頁面", new UnitAction("使用", new UnitCallback() {
            @Override
            public boolean before() {
                if (村莊好感度買藥.equals("用體力藥")){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void after() {

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
        taskAction.addLayerAction("酒館頁面","清算营业","打開世界");
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

    private TaskAction initBanZiDongZhuXian(){
        TaskAction taskAction = new TaskAction();
        taskAction.addLayerAction("城鎮頁面","打開任務");
        taskAction.addLayerAction("酒館頁面","清算营业","打開任務");
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
     * 公共操作
     */
    private void initGongGong(){
        g_taskAction = new TaskAction();
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
                引繼成功 = false;
            }
        }),"點擊繼續");
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
        g_taskAction.addLayerAction("引繼確認頁面","確認");
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
        g_taskAction.addLayerAction("斷線重連頁面",new UnitAction("重啟", new UnitCallback() {
            @Override
            public boolean before() {
                if (斷線重連時長 != 0){
                    斷線等待 = true;
                    斷線等待時間 = System.currentTimeMillis();
                }
                return true;
            }

            @Override
            public void after() {

            }
        }));
        g_taskAction.addLayerAction("重啟動頁面","重啟");
        g_taskAction.addLayerAction("加載頁面","等待");
        g_taskAction.addLayerAction("廣告頁面","關閉");
        g_taskAction.addLayerAction("使用條款頁面","同意");
        g_taskAction.addLayerAction("通知頁面","同意");
        g_taskAction.addLayerAction("新玩家獎勵頁面","領取");
        g_taskAction.addLayerAction("日常獎勵頁面","領取");
        g_taskAction.addLayerAction("一次獎勵頁面","領取");
        g_taskAction.addLayerAction("更新頁面","確定");
        g_taskAction.addLayerAction("未知頁面","ok","ok1","確認","廣告ok","等待");
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
        g_taskAction.addLayerAction("酒館頁面","清算营业");
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
        g_taskAction.addLayerAction("刷圖選關頁面","返回");
        g_taskAction.addLayerAction("任務領取獎勵頁面","領取");
        g_taskAction.addLayerAction("班料理頁面","返回");
        g_taskAction.addLayerAction("團長料理頁面","返回");
        g_taskAction.addLayerAction("買入食材頁面","關閉");
        g_taskAction.addLayerAction("雜貨商店頁面","返回");
        g_taskAction.addLayerAction("章節村莊頁面","返回");
        g_taskAction.addLayerAction("章節故事頁面","返回");
        g_taskAction.addLayerAction("對話接任務頁面","領取");
        g_taskAction.addLayerAction("彈框接任務頁面","關閉","開始");
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
        if (!斷線等待時長.equals("") && !斷線等待時長.equals("0")){
            斷線重連時長 = Integer.parseInt(斷線等待時長);
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
    }

    /**
     * 重置所有任務
     */
    private void resetMission(){

    }


    @Override
    public void run() {
        scriptStart();
        delay(2000);
        initData();
        initTaskAction();
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
            }else{
                keepCapture();
                String curLayerName = null;
                TaskAction curTaskAction=null;
                curLayerName = getLayerName();
                Point[] points = findMultiColors(433, 1, 719, 111,"ffffff-101010","0|4|ffffff-101010,0|10|fffcfc-101010,0|19|ffffff-101010,-13|11|febc32-101010,12|11|fdba32-101010",0.95f);
                if (points != null && points.length != 0) {
                    if (points.length > 0 && SettingPreference.getBoolean("領取郵件", false)) {
                        郵件領取_開關 = true;
                    }
                }
                if (刷初始_開關){
                    curTaskAction = initShuaChuShi();
                    showHUDInfo("刷初始");
                }else{
                    if (購買食材_開關){
                        curTaskAction = initGouMaiShiCai();
                        showHUDInfo("購買食材");
                    }else if (郵件領取_開關){
                        curTaskAction = initYouJianLingQu();
                        showHUDInfo("郵件領取");
                    }else if (每日免費抽_開關){
                        curTaskAction = initMeiRiChouJiang();
                        showHUDInfo("每日免費抽");
                    }else if (一鑽石抽獎_開關){
                        curTaskAction = initYiZuanShiChouKa();
                        showHUDInfo("一鑽石抽卡");
                    }else if (成就領取_開關){
                        curTaskAction = initChengJiuJiangLi();
                        showHUDInfo("成就領取");
                    }else if (贈送友情點_開關){
                        curTaskAction = initSongYouQingDian();
                        showHUDInfo("贈送友情點");
                    }else if (友情點買藥_開關){
                        curTaskAction = initYouQingDianMaiYao();
                        showHUDInfo("友情點買藥");
                    }else if (金幣本_開關 && 金幣本68 && TimingUtil.betweenHour(5,7)) {
                        curTaskAction = initJinBiBen68();
                        showHUDInfo("金幣本");
                    }else if (金幣本_開關 && 金幣本1113 && TimingUtil.betweenHour(10,12)){
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
                    }else{
                        showHUDInfo("暫無任務");
                    }
                }

                if (斷線等待){
                    if (System.currentTimeMillis() - 斷線等待時間 < 斷線重連時長*60*1000){
                        curTaskAction = null;
                        g_taskAction = null;
                    }else{
                        斷線等待 = false;
                    }
                }

//                if (任務重置_開關 && TimingUtil.timingByHour(重置任務時間)){
//                    任務重置_開關 = false;
//                    killApp(gamePkg);
//                    resetMission();
//                }
//                if (!任務重置_開關 && DateUtil.getHour() == 0){
//                    任務重置_開關 = true;
//                }

                /**********************************畫面超時不動重啟遊戲****************************************/
                String newRGB = getPixelColor(16,307);
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
                if (等待體力回復 && System.currentTimeMillis() - t_體力回復 < 2*60*1000){
                    curTaskAction = null;
                    delay(2000);
                }else{
                    等待體力回復 = false;
                }
                Log.e(getTag(), "run: " + 主線停止模式);
                Log.e(getTag(), "run:" + curLayerName);
                showMOVEInfo(curLayerName + "-" + getUnitName());
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
}
