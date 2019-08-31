package com.gamebot.botdemo.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.gamebot.botdemo.R;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.IFloatWindow;
import com.yhao.floatwindow.MoveType;



import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;


/**
 * Created by Administrator on 2018/10/13.
 */

public class HUDManage {
    private Context mContext;
    private HashMap<String,FloatHUD> HUDList;


    private static HUDManage instance;
    public static HUDManage getInstance() {
        if (instance == null) {
            synchronized (HUDManage.class) {
                if (instance == null) {
                    instance = new HUDManage();
                }
            }
        }
        return instance;
    }

    public HUDManage init(Context context){
        mContext=context;
        return this;
    }

    private HUDManage(){
        HUDList=new HashMap<>(2);
    }

    private class FloatHUD{
        private TextView textView;
        private Integer id;
        private View parentView;
        private IFloatWindow floatWindow;

        public TextView getTextView() {
            return textView;
        }

        public void setTextView(TextView textView) {
            this.textView = textView;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public View getParentView() {
            return parentView;
        }

        public void setParentView(View parentView) {
            this.parentView = parentView;
        }

        public IFloatWindow getFloatWindow() {
            return floatWindow;
        }

        public void setFloatWindow(IFloatWindow floatWindow) {
            this.floatWindow = floatWindow;
        }
    }

    public FloatHUD createHUD(String tag,Integer size,Integer width,Integer height,Integer x,Integer y,Integer pos){
        IFloatWindow floatWindow=FloatWindow.get(tag);
        if(floatWindow==null){
            FloatWindow
                    .with(mContext)
                    .setView(R.layout.hud_layout)
                    .setWidth(CommonUtils.getInteger(width,120)) //设置悬浮控件宽高
                    .setHeight(CommonUtils.getInteger(height,40))
                    .setX(CommonUtils.getInteger(x,0))
                    .setY(CommonUtils.getInteger(y,0))
                    .setTag(tag)
                    .setMoveType(MoveType.inactive)
                    .setDesktopShow(true)
                    .build();
            floatWindow=FloatWindow.get(tag);
        }
        View parentView=floatWindow.getView();
        FloatHUD floatHUD=new FloatHUD();
        floatHUD.setFloatWindow(floatWindow);
        floatHUD.setParentView(parentView);
        floatHUD.setTextView((TextView)parentView.findViewById(R.id.hud_text));
        HUDList.put(tag,floatHUD);
        return floatHUD;
    }
    public void showHUD(String tag,String text){
        showHUD( tag, text,null,null,null,null,null,null,null,null);
    }

    public String getHUDText(String tag){
        FloatHUD floatHUD=HUDList.get(tag);
        if(floatHUD!=null){
            IFloatWindow hudWindow=floatHUD.getFloatWindow();
            TextView textView=floatHUD.getTextView();
            return textView.getText().toString();
        }
        return "";
    }

    public void showHUD(String tag,String text,Integer size,Integer width,Integer height,Integer x,Integer y,String color,String bg,Integer pos){
        FloatHUD floatHUD=HUDList.get(tag);
        if(floatHUD==null){
            floatHUD=createHUD(tag,size,width,height,x,y,pos);
        }
        IFloatWindow hudWindow=floatHUD.getFloatWindow();
        if(!hudWindow.isShowing()){
            hudWindow.show();
        }

        TextView textView=floatHUD.getTextView();
        textView.setText(text);
        if(size!=null){
            textView.setTextSize(CommonUtils.px2sp(mContext,size));
        }
        if(StringUtils.isNotEmpty(color) && StringUtils.contains(color,"#")){
            textView.setTextColor(Color.parseColor(color));
        }
        if(StringUtils.isNotEmpty(bg) && StringUtils.contains(bg,"#")){
            floatHUD.getParentView().setBackgroundColor(Color.parseColor(bg));
        }
        if(x!=null && hudWindow.getX()!=x.intValue()){
            hudWindow.updateX(x);
        }
        if(y!=null && hudWindow.getY()!=y.intValue()){
            hudWindow.updateY(y);
        }
        if(width!=null && height!=null){
            hudWindow.setSize(CommonUtils.dp2px(mContext,width),CommonUtils.dp2px(mContext,height));
        }
        if(!hudWindow.isShowing()){
            hudWindow.show();
        }
    }

    public void hideHUD(String id){
        FloatHUD floatHUD=HUDList.get(id);
        if(floatHUD!=null){
            IFloatWindow hudWindow=floatHUD.getFloatWindow();
            if(hudWindow!=null && hudWindow.isShowing()){
                hudWindow.hide();
            }
        }
    }
}
