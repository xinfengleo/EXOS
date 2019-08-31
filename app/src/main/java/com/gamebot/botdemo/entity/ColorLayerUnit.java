package com.gamebot.botdemo.entity;

import java.util.List;

/**
 * Created by Administrator on 2018/10/20.
 */

public class ColorLayerUnit {
    private String n;
    private Integer delay;
    private List<Integer> nslide;
    private List<Integer> slide;
    private Integer bdelay;
    private Integer wait;
    private String type;
    private Object tap;
    private Object c;
    private Integer slideNum;

    public Integer getSlideNum() {
        return slideNum;
    }

    public void setSlideNum(Integer slideNum) {
        this.slideNum = slideNum;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public List<Integer> getNslide() {
        return nslide;
    }

    public void setNslide(List<Integer> nslide) {
        this.nslide = nslide;
    }

    public Integer getBdelay() {
        return bdelay;
    }

    public void setBdelay(Integer bdelay) {
        this.bdelay = bdelay;
    }

    public Integer getWait() {
        return wait;
    }

    public void setWait(Integer wait) {
        this.wait = wait;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getTap() {
        return tap;
    }

    public void setTap(Object tap) {
        this.tap = tap;
    }

    public Object getC() {
        return c;
    }

    public void setC(Object c) {
        this.c = c;
    }

    public List<Integer> getSlide() {
        return slide;
    }

    public void setSlide(List<Integer> slide) {
        this.slide = slide;
    }
}
