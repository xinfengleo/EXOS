package com.gamebot.botdemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.gamebot.botdemo.R;
import com.gamebot.sdk.preference.SettingPreference;
import com.gamebot.sdk.view.BotControl;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;


/**
 * Created by Administrator on 2018/10/25.
 */

public class SpinnerExd extends AppCompatSpinner implements BotControl {
    private String mUniqueKey = "";
    private int mDefaultIndex = 0;
    private String listArray;
    private boolean valueFromList=false;
    private boolean initialized = false;
    private String[] listStrs=null;

    public SpinnerExd(Context var1) {
        super(var1);
    }

    public SpinnerExd(Context var1, int var2) {
        super(var1, var2);
    }

    public SpinnerExd(Context var1, AttributeSet var2) {
        super(var1, var2);
        TypedArray var3 = var1.obtainStyledAttributes(var2, R.styleable.SpinnerExd);
        this.mUniqueKey = var3.getString( R.styleable.SpinnerExd_uniqueKey);
        this.mDefaultIndex = var3.getInt( R.styleable.SpinnerExd_defaultIndex, 0);
        this.listArray=var3.getString( R.styleable.SpinnerExd_listArray);
        this.valueFromList=var3.getBoolean( R.styleable.SpinnerExd_valueFromList,false);

        if(StringUtils.isNotEmpty(listArray)){
            listStrs= StringUtils.split(listArray,",");
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(var1,android.R.layout.simple_list_item_1,listStrs);
            setAdapter(adapter);
        }
        var3.recycle();
    }

    public void setAdapter(SpinnerAdapter var1) {
        super.setAdapter(var1);
        if(!this.initialized) {
            int index=SettingPreference.getInt(this.mUniqueKey+(this.valueFromList?"_index":""), this.mDefaultIndex);
            this.setSelection(index);
            this.initialized = true;
        }
    }

    public String getUniqueKey() {
        return this.mUniqueKey;
    }

    public void save() {
        if(this.valueFromList){
            SettingPreference.putInt(this.mUniqueKey+"_index", this.getSelectedItemPosition());
            SettingPreference.putString(this.mUniqueKey, listStrs[this.getSelectedItemPosition()]);
        }else{
            SettingPreference.putInt(this.mUniqueKey, this.getSelectedItemPosition());
        }
    }
    public void setSelection(Object val){
        if(this.valueFromList){
            this.setSelection(ArrayUtils.indexOf(listStrs,val.toString()));
        }else{
            if(val instanceof Double){
                this.setSelection(((Double) val).intValue());
            }else if(val instanceof Integer){
                this.setSelection((int)val);
            }

        }
    }
}
