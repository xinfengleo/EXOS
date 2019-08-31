package com.gamebot.botdemo.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.gamebot.botdemo.R;

public class LoadDialog extends Dialog {
    public LoadDialog(Context context, int theme) {
        super(context, theme);
        // TODO Auto-generated constructor stub
    }

    protected LoadDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
    }

    public LoadDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.app_customdialog);
    }
}
