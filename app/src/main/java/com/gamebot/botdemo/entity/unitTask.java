package com.gamebot.botdemo.entity;

import java.util.List;

public class unitTask {

    private List<taskBean> taskBean;

    public List<taskBean> getTaskName() {
        return taskBean;
    }

    public void setTaskName(List<taskBean> taskBeans) {
        this.taskBean = taskBeans;
    }

    public static class taskBean {
        /**
         * n : 主字体
         * type : MSC
         * c : [[49,112,89,486],"0|0|f0cb93,18|-6|ecc690,0|-6|f0cb93,12|2|ffd79c,12|5|ffd79c"]
         * sim : 98
         * tap : [59,130]
         */

        private String n;
        private String type;
        private int sim;
        private List<List<Integer>> c;
        private List<Integer> tap;

        public String getN() {
            return n;
        }

        public void setN(String n) {
            this.n = n;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getSim() {
            return sim;
        }

        public void setSim(int sim) {
            this.sim = sim;
        }

        public List<List<Integer>> getC() {
            return c;
        }

        public void setC(List<List<Integer>> c) {
            this.c = c;
        }

        public List<Integer> getTap() {
            return tap;
        }

        public void setTap(List<Integer> tap) {
            this.tap = tap;
        }
    }
}
