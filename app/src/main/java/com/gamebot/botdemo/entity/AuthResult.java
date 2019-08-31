package com.gamebot.botdemo.entity;

/**
 * Created by Administrator on 2018/10/27.
 */

public class AuthResult {
    private String token;
    private Integer endtime;
    private Integer servertime;
    private Integer remainingtimes;
    private Integer code;
    private Integer status;
    private String msg;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getEndtime() {
        return endtime;
    }

    public void setEndtime(Integer endtime) {
        this.endtime = endtime;
    }

    public Integer getServertime() {
        return servertime;
    }

    public void setServertime(Integer servertime) {
        this.servertime = servertime;
    }

    public Integer getRemainingtimes() {
        return remainingtimes;
    }

    public void setRemainingtimes(Integer remainingtimes) {
        this.remainingtimes = remainingtimes;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg==null?"":msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
