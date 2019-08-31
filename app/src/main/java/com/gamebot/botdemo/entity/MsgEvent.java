package com.gamebot.botdemo.entity;

import com.google.gson.JsonObject;

/**
 * Created by Administrator on 2018/9/29.
 */

public class MsgEvent {

    public class EVENT {
        public static final int START =1;
        public static final int STOP=2;
        public static final int PAUSE=3;
        public static final int STATUS=4;
        public static final int CONSOLE_DATA=7;
        public static final int LOAD=8;
        public static final int DEV_START=9;
        public static final int SCREEN_CHANGED=9;
        public static final int SCREENCAP=11;
        public static final int TOAST=13;
        public static final int SHOW_HUD=16;
        public static final int HIDE_HUD=17;
        public static final int SHOW_SETTING=18;
        public static final int HIDE_SETTING=19;
        public static final int VIEW_ADD_PAGE=20;
        public static final int VIEW_INIT_PAGE=21;
        public static final int VIEW_DEL_PAGE=22;
        public static final int VIEW_SET_TEXT=23;
        public static final int VIEW_SET_VALUE=24;
        public static final int VIEW_VISIBLE=25;
        public static final int VIEW_HIDE=26;
        public static final int VIEW_SET_ON_CLICK=27;
        public static final int VIEW_SET_ON_CHANGE=28;
        public static final int VIEW_GET_TEXT=29;
        public static final int SETTING_ON_SHOW=40;
        public static final int SETTING_ON_HIDE=41;
        public static final int VIEW_ENABLED=30;


        public static final int VIEW_ON_CLICK=127;
        public static final int VIEW_ON_CHANGE=128;

        public static final int INJECT=101;
    }

    private Integer e;
    private JsonObject data;
    private Integer timestamp;
    private Integer resultE;


    public Integer getE() {
        return e;
    }

    public void setE(Integer e) {
        this.e = e;
    }

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getResultE() {
        return resultE;
    }

    public void setResultE(Integer resultE) {
        this.resultE = resultE;
    }


}
