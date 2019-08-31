package com.gamebot.botdemo.entity;

/**
 * Created by Administrator on 2018/10/20.
 */

public class UnitAction {
    private ColorLayerUnit unit;
    private UnitCallback callback;
    private Object unitName;

    public UnitAction(String unitName, UnitCallback callback){
        this.unitName=unitName;
        this.callback=callback;
    }

    public UnitAction(UnitNameFun unitNameFun, UnitCallback callback){
        this.unitName=unitNameFun;
        this.callback=callback;
    }

    public UnitAction(UnitNameFun unitNameFun){
        this.unitName=unitNameFun;
    }

    public UnitAction(String unitName, Boolean sw){
        this.unitName=unitName;
        this.callback=new UnitCallback() {
            @Override
            public boolean before() {
                return sw;
            }

            @Override
            public void after() {

            }
        };
    }

    public UnitAction(String unitName){
        this.unitName=unitName;
    }

    public ColorLayerUnit getUnit() {
        return unit;
    }

    public void setUnit(ColorLayerUnit unit) {
        this.unit = unit;
    }

    public UnitCallback getCallback() {
        return callback;
    }

    public void setCallback(UnitCallback callback) {
        this.callback = callback;
    }

    public String getUnitName() {
        if (unitName instanceof String){
            return unitName.toString();
        }else if(unitName instanceof UnitNameFun){
            return ((UnitNameFun) unitName).getName();
        }
        return null;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
}
