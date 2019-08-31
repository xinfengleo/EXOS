package com.gamebot.botdemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SpinnerAdapter;

import com.gamebot.botdemo.R;
import com.gamebot.sdk.preference.SettingPreference;
import com.gamebot.sdk.view.BotControl;

import org.apache.commons.lang3.StringUtils;


/**
 * Created by Administrator on 2018/10/25.
 */

public class RadioGroupExd extends RadioGroup implements BotControl {
    private String mUniqueKey = "";
    private int mDefaultIndex = 0;
    private String listArray;
    private boolean valueFromList=false;
    private String[] listStrs=null;

    public RadioGroupExd(Context var1) {
        super(var1);
    }

    public RadioGroupExd(Context var1, AttributeSet var2) {
        super(var1, var2);
        TypedArray var3 = var1.obtainStyledAttributes(var2, R.styleable.RadioGroupExd);
        this.mUniqueKey = var3.getString( R.styleable.RadioGroupExd_uniqueKey);
        this.mDefaultIndex = var3.getInt( R.styleable.RadioGroupExd_defaultIndex, 0);
        this.listArray=var3.getString( R.styleable.RadioGroupExd_listArray);
        this.valueFromList=var3.getBoolean( R.styleable.RadioGroupExd_valueFromList,false);

        if(StringUtils.isNotEmpty(listArray)){
            listStrs= StringUtils.split(listArray,",");
            for(String str : listStrs){
                RadioButton radioButton=new RadioButton(var1);
                radioButton.setText(str);
                addView(radioButton);
            }
        }
        var3.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int selectIndex=SettingPreference.getInt(this.mUniqueKey,mDefaultIndex);
        View selectView=getChildAt(selectIndex);
        if(selectView instanceof RadioButton){
            ((RadioButton) selectView).setChecked(true);
        }
    }

    public String getUniqueKey() {
        return this.mUniqueKey;
    }

    public void save() {
        int index=this.indexOfChild(this.findViewById(this.getCheckedRadioButtonId()));
        SettingPreference.putInt(this.mUniqueKey,index);
    }
}
