package com.gamebot.botdemo.entity;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/10/20.
 */

public class ColorAction {
    private List<String[]> layer;
    private HashMap<String,List<ColorLayerUnit>> unit;

    public List<String[]> getLayer() {
        return layer;
    }

    public void setLayer(List<String[]> layer) {
        this.layer = layer;
    }

    public HashMap<String, List<ColorLayerUnit>> getUnit() {
        return unit;
    }

    public void setUnit(HashMap<String, List<ColorLayerUnit>> unit) {
        this.unit = unit;
    }
}
