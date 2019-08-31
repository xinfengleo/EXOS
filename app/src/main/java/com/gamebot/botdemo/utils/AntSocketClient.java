package com.gamebot.botdemo.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by dzy on 2018/11/21.
 */

public class AntSocketClient {
    private final static String TAG = "SocketClient";
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private String mIp;
    private int mPort;
    private boolean disconnectCheckEnabled = false;
    private volatile String lastMessage;
    private boolean disconnectChecking=false;
    private boolean connectedBack=false;
    private SocketListener socketListener;
    private boolean connected=false;
    private int keepAliveNum=-1;
    public boolean connect() {
        try {
            if(socket!=null){
                socket.close();
                socket=null;
            }
            if(writer!=null){
                writer.close();
                writer=null;
            }
            if(reader!=null){
                reader.close();
                reader=null;
            }
            socket = new Socket(mIp, mPort);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Log.d(TAG, "连接成功");
            return true;
        } catch (Exception e) {
            //e.printStackTrace();
            //Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public AntSocketClient(String ip, int port) {
        this(ip,port,-1,null);
    }

    public AntSocketClient(String ip, int port,SocketListener socketListener) {
        this(ip,port,-1,socketListener);
    }

    public AntSocketClient(String ip, int port,int keepAlive,SocketListener socketListener) {
        this.mPort = port;
        this.mIp = ip;
        this.socketListener=socketListener;
        this.keepAliveNum=keepAlive;
        startMessageTh();
    }
    private void startMessageTh(){
        disconnectCheckEnabled = true;
        if(disconnectChecking){
            return;
        }
        new Thread(new MessageCheckTh()).start();
    }

    public interface SocketListener{
        void ConnectionListener();
        void ReceiveListener(String msg);
        void DisconnectListener();
    }


    public void disconnect() {
        disconnectCheckEnabled = false;
        try {
            if(socket!=null){
                socket.close();
            }
            if(writer!=null){
                writer.close();
            }
            if(reader!=null){
                reader.close();
            }
            writer=null;
            reader=null;
            socket=null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int reconnectionCount=0;

    private class MessageCheckTh implements Runnable{
        @Override
        public void run() {
            disconnectChecking=true;
            try {
                while (disconnectCheckEnabled) {
                    if (socket == null || socket.isClosed() || !socket.isConnected() || isServerClose()) {
                        if(socketListener!=null && connected){
                            socketListener.DisconnectListener();
                        }
                        connected=false;
                        if(reconnectionCount>keepAliveNum && keepAliveNum>-1){
                            disconnectCheckEnabled = false;
                            break;
                        }
                        //Log.d(TAG, "重新连接中");
                        reconnectionCount++;
                        connect();
                        Thread.sleep(100);
                    } else {
                        //Log.d(TAG, "已连接");
                        if(socketListener!=null && !connected){
                            socketListener.ConnectionListener();
                        }
                        connected=true;
                        char ch[]=new char[1024];
                        int len = reader.read(ch);
                        if (len>-1) {
                            lastMessage = new String(ch);
                            if(socketListener!=null){
                                socketListener.ReceiveListener(lastMessage);
                            }
                        }else if (len==-1){
                            connected=false;
                            if(socketListener!=null){
                                socketListener.DisconnectListener();
                            }
                            socket.close();
                        }

                    }
                    Thread.sleep(100);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            disconnectChecking=false;
        }
    }



    public boolean isServerClose() {
        try {
            socket.sendUrgentData(0);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
            return false;
        } catch (Exception se) {
            return true;
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public boolean sendMessage(String msg) {
        if (socket != null && !socket.isClosed() && socket.isConnected()) {
            try {
                writer.write(msg); // 写一个UTF-8的信息
                writer.flush();
                return true;
            } catch (IOException e) {
                Log.e(TAG,e.getMessage());
                connect();
            }
        }
        return false;
    }

    public boolean sendMessage(JSONObject msg) {
        return sendMessage(msg.toString());
    }

    public String sendMessageResult(String msg) {
        lastMessage = null;
        if (sendMessage(msg)) {
            try {
                int dt = 0;
                while (true) {
                    if (lastMessage != null) {
                        return lastMessage;
                    } else if (++dt > 1000) {
                        return null;
                    }
                    //Log.d(TAG, "getResult");
                    Thread.sleep(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public <T> T sendMessageResult(String msg, Class<T> cls) {
        String result = sendMessageResult(msg);
        if (StringUtils.isNotEmpty(result)) {
            return (T) (new Gson().fromJson(result, cls));
        } else {
            return null;
        }
    }

    public <T> T sendMessageResult(JsonObject msg, Class<T> cls) {
        return sendMessageResult(msg.toString(), cls);
    }

}
