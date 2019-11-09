package com.gamebot.botdemo.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.gamebot.sdk.view.BotControl;

public class LinearLayoutExd extends LinearLayout implements BotControl {
    public LinearLayoutExd(Context context) {
        super(context);
    }

    public LinearLayoutExd(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public String getUniqueKey() {
        return null;
    }

    @Override
    public void save() {

    }
}
