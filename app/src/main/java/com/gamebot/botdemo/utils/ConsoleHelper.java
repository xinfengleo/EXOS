package com.gamebot.botdemo.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import static android.os.Looper.getMainLooper;

public class ConsoleHelper {
    public boolean consoleMode = false;
    public String consoleIp;
    public int consolePort;
    public int consoleIndex;
    public boolean consoleStop = false;
    public boolean consoleConnected = false;
    public AntSocketClient consoleClient;
    public volatile JSONObject consoleData;
    public volatile JSONObject consoleMsg;
    private Context mContext;
    private static ConsoleHelper instance;

    public static ConsoleHelper getInstance() {
        synchronized (ConsoleHelper.class) {
            if (instance == null) {
                instance = new ConsoleHelper();
            }
        }
        return instance;
    }


    public void init(Context context,Intent intent) {
        this.mContext=context;
        String consoleData = intent.getStringExtra("console");
        if (StringUtils.isNotEmpty(consoleData)) {
            consoleMode = true;
            String[] dataArr = StringUtils.split(consoleData, ",");
            consoleIndex = Integer.valueOf(dataArr[0]);
            consoleIp = dataArr[1];
            consolePort = Integer.valueOf(dataArr[2]);
            initConsoleSocket();
        }
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public interface MessageHandler {
        void handler(String msg);
    }

    private MessageHandler messageHandler;


    /**
     * 初始化中控连接
     */
    private void initConsoleSocket() {
        if (StringUtils.isEmpty(consoleIp) || consolePort == 0 || consoleClient != null) {
            return;
        }
        consoleData = new JSONObject();
        consoleMsg = new JSONObject();
        try {
            consoleMsg.put("e", 7);
            consoleMsg.put("index", consoleIndex);
            consoleMsg.put("data", consoleData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        consoleClient = new AntSocketClient(consoleIp, consolePort, new AntSocketClient.SocketListener() {
            StringBuilder data;

            @Override
            public void ConnectionListener() {
                consoleConnected=true;
                data = new StringBuilder();
                JSONObject json = new JSONObject();
                try {
                    json.put("e", 100001);
                    json.put("index", consoleIndex);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                consoleClient.sendMessage(json);
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        MsgboxUtils.showToast(mContext, "Console Connected");
                    }
                });
            }

            @Override
            public void ReceiveListener(String msg) {
                if(messageHandler==null){
                    return;
                }
                if (StringUtils.contains(msg, "\n")) {
                    String msgList[] = StringUtils.split(msg, "\n");
                    for (String str : msgList) {
                        if (str.startsWith("{")) {
                            data.setLength(0);
                            if (str.endsWith("}")) {
                                messageHandler.handler(str);
                                continue;
                            }
                        }
                        data.append(str);
                        if (str.endsWith("}")) {
                            messageHandler.handler(data.toString());
                            data.setLength(0);
                        }

                    }
                } else {
                    if (msg.startsWith("{")) {
                        data.setLength(0);
                        if (msg.endsWith("}")) {
                            messageHandler.handler(msg);
                            return;
                        }
                    }
                    data.append(msg);
                    if (msg.endsWith("}")) {
                        messageHandler.handler(data.toString());
                        data.setLength(0);
                    }
                }
            }

            @Override
            public void DisconnectListener() {
                consoleConnected=false;
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        MsgboxUtils.showToast(mContext, "Console Disconnect");
                    }
                });
            }
        });
    }
    private boolean dataHeartbeatOpened=false;
    public void startDataHeartbeat(){
        if(dataHeartbeatOpened){
            return;
        }
        dataHeartbeatOpened=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if(consoleClient.isConnected()){
                        try {
                            consoleMsg.put("data",consoleData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        consoleClient.sendMessage(consoleMsg);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void sendConsoleMsg(String msg) {
        if (consoleClient != null && consoleConnected) {
            JSONObject json = new JSONObject();
            JSONObject data = new JSONObject();
            try {
                json.put("e", 100002);
                json.put("index", consoleIndex);
                data.put("msg", msg);
                json.put("data", data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            consoleClient.sendMessage(json);
        }
    }

    public boolean sendMessage(JSONObject msg) {
        if(consoleMode && consoleClient!=null && msg!=null){
            try {
                msg.put("index",consoleIndex);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return consoleClient.sendMessage(msg.toString());
        }
        return false;
    }
    public boolean sendMessage(String msg) {
        JSONObject json= null;
        try {
            json = new JSONObject(msg);
            json.put("index", consoleIndex);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sendMessage(json);
    }



    public void sendConsoleStop(String msg) {
        if (consoleClient != null) {
            JSONObject json = new JSONObject();
            JSONObject data = new JSONObject();
            try {
                json.put("e", 2);
                json.put("index", consoleIndex);
                data.put("msg", msg);
                data.put("type", 0);
                json.put("data", data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            consoleClient.sendMessage(json);
            consoleStop = true;
        }
    }
}
