package com.gamebot.botdemo.utils;

public class TimingUtil {

    /**
     * 超過一個時間點后執行
     */
    public static boolean timingByHour(int Hour){
        if (DateUtil.getHour() >= Hour){
            return true;
        }
        return false;
    }

    public static boolean timingByMinute(int Hour,int Minute){
        if (DateUtil.getHour() == Hour){
            if (DateUtil.getMin() >= Minute){
                return true;
            }
            return false;
        }else if (DateUtil.getHour() > Hour){
            return true;
        }
        return false;
    }

    public static boolean timingBySecond(int Hour,int Minute,int Second){
        if (DateUtil.getHour() == Hour){
            if (DateUtil.getMin() == Minute){
                if (DateUtil.getSecond() >= Second){
                    return true;
                }
            }else if (DateUtil.getMin() > Minute){
                return true;
            }
        }else if (DateUtil.getHour() > Hour){
            return true;
        }
        return false;
    }

    public static boolean betweenHour(int Hour1,int Hour2){
        if (DateUtil.getHour() >= Hour1 && DateUtil.getHour() < Hour2){
            return true;
        }
        return false;
    }
}
