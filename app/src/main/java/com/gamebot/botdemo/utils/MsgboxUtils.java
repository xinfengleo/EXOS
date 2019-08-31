package com.gamebot.botdemo.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.WindowManager;
import android.widget.Toast;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.flyco.dialog.widget.NormalDialog;

/**
 * Created by Administrator on 2018/9/3.
 */

public class MsgboxUtils {

    public static void showMsg(Context context, String text) {
        MsgboxUtils.showMsg(context,null,text,null);
    }


    public static void showMsg(Context context,String title, String text,OnBtnClickL onBtnClickL) {
        final MaterialDialog dialog = new MaterialDialog(context);
        dialog.content(text)
                .btnText("知道了")
                .btnNum(1);
        if(title!=null){
            dialog.title(title);
        }
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        if(onBtnClickL!=null) {
                            onBtnClickL.onBtnClick();
                        }
                        dialog.dismiss();
                    }
                }
        );
        dialog.show();
    }
    public static void showToast(Context context, String text) {
        Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
    }

}
