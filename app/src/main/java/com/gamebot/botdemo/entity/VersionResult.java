package com.gamebot.botdemo.entity;

/**
 * Created by Administrator on 2018/10/27.
 */

public class VersionResult {
    private String token;
    private Integer code;
    private Integer status;
    private String msg;
    private VersionData data;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public VersionData getData() {
        return data;
    }

    public void setData(VersionData data) {
        this.data = data;
    }

    public static class VersionData{
        private String version;
        private String date;
        private String link;
        private String profile;
        private Integer no_skip;
        private Integer edit_time;
        private Integer version_id;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getProfile() {
            return profile;
        }

        public void setProfile(String profile) {
            this.profile = profile;
        }

        public Integer getNo_skip() {
            return no_skip;
        }

        public void setNo_skip(Integer no_skip) {
            this.no_skip = no_skip;
        }

        public Integer getEdit_time() {
            return edit_time;
        }

        public void setEdit_time(Integer edit_time) {
            this.edit_time = edit_time;
        }

        public Integer getVersion_id() {
            return version_id;
        }

        public void setVersion_id(Integer version_id) {
            this.version_id = version_id;
        }
    }


}
