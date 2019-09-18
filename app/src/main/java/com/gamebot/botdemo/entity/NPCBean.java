package com.gamebot.botdemo.entity;

import java.util.List;

public class NPCBean {

    private int e;
    private List<DataBean> data;

    public int getE() {
        return e;
    }

    public void setE(int e) {
        this.e = e;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * active : true
         * iconType : 0
         * id : 210
         * role : 2
         * visible : true
         * x : 2.9100000858306885
         * y : 0.0
         * z : -0.41999998688697815
         */

        private boolean active;
        private int iconType;
        private int id;
        private int role;
        private boolean visible;
        private double x;
        private double y;
        private double z;

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public int getIconType() {
            return iconType;
        }

        public void setIconType(int iconType) {
            this.iconType = iconType;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getRole() {
            return role;
        }

        public void setRole(int role) {
            this.role = role;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getZ() {
            return z;
        }

        public void setZ(double z) {
            this.z = z;
        }
    }
}
