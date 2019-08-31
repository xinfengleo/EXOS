package com.gamebot.botdemo.entity;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/10/20.
 */

public class TaskAction {
    private HashMap<String,List<UnitAction>> unitActionMap=new HashMap<>();
    private ColorAction colorAction;

    public void clearAllUnitAction(){
        unitActionMap.clear();
        unitActionMap=new HashMap<>();
    }

    public HashMap<String, List<UnitAction>> getUnitActionMap() {
        return unitActionMap;
    }

    public void setUnitActionMap(HashMap<String, List<UnitAction>> unitActionMap) {
        this.unitActionMap = unitActionMap;
    }

    public ColorAction getColorAction() {
        return colorAction;
    }

    public void setColorAction(ColorAction colorAction) {
        this.colorAction = colorAction;
    }

    public void addLayerAction(String layerName,UnitAction... unitActions){
        List<UnitAction> list=new ArrayList<>(unitActions.length);
        for(UnitAction unitAction : unitActions){
            list.add(unitAction);
        }
        unitActionMap.put(layerName,list);
    }

    public void addLayerAction(String layerName,String... unitNames){
        List<UnitAction> list=new ArrayList<>(unitNames.length);
        for(String name : unitNames){
            list.add(new UnitAction(name));
        }
        unitActionMap.put(layerName,list);
    }

    public void addLayerAction(String layerName,Object... unitObjs){
        List<UnitAction> list=new ArrayList<>(unitObjs.length);
        for(Object obj : unitObjs){
            if(obj instanceof String){
                list.add(new UnitAction(obj.toString()));
            }else if(obj instanceof UnitAction){
                list.add((UnitAction)obj);
            }else if(obj instanceof UnitNameFun){
                list.add(new UnitAction((UnitNameFun)obj));
            }
        }
        unitActionMap.put(layerName,list);
    }



}
