package com.gamebot.botdemo.entity;

public class Goods {

    private DataBean data;
    private int e;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getE() {
        return e;
    }

    public void setE(int e) {
        this.e = e;
    }

    public static class DataBean {

        private int APItemCount;
        private int arenaTicket;
        private int buyDiaCount;
        private boolean isMoving;
        private int stamina;
        private double x;
        private double y;
        private double z;

        public int getAPItemCount() {
            return APItemCount;
        }

        public void setAPItemCount(int APItemCount) {
            this.APItemCount = APItemCount;
        }

        public int getArenaTicket() {
            return arenaTicket;
        }

        public void setArenaTicket(int arenaTicket) {
            this.arenaTicket = arenaTicket;
        }

        public int getBuyDiaCount() {
            return buyDiaCount;
        }

        public void setBuyDiaCount(int buyDiaCount) {
            this.buyDiaCount = buyDiaCount;
        }

        public boolean isIsMoving() {
            return isMoving;
        }

        public void setIsMoving(boolean isMoving) {
            this.isMoving = isMoving;
        }

        public int getStamina() {
            return stamina;
        }

        public void setStamina(int stamina) {
            this.stamina = stamina;
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
