package com.gamebot.botdemo.script;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Handler;
import android.support.annotation.RawRes;
import android.util.Log;

import com.antscript.sdk.utils.AntScriptUtils;
import com.gamebot.botdemo.R;
import com.gamebot.botdemo.entity.ColorAction;
import com.gamebot.botdemo.entity.ColorLayerUnit;
import com.gamebot.botdemo.entity.TaskAction;
import com.gamebot.botdemo.entity.UnitAction;
import com.gamebot.botdemo.utils.ConsoleHelper;
import com.gamebot.botdemo.utils.FileUtils;
import com.gamebot.botdemo.utils.HUDManage;
import com.gamebot.botdemo.utils.Utils;
import com.gamebot.sdk.client.BaseScriptThread;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.topjohnwu.superuser.Shell;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.antscript.sdk.utils.AntScriptUtils.Int2Rgb;
import static com.antscript.sdk.utils.AntScriptUtils.MAX;
import static com.antscript.sdk.utils.AntScriptUtils.MIN;

public abstract class SuperScriptThread extends BaseScriptThread {
    protected Context mContext;
    protected HUDManage hudManage;
    protected HashMap<String, Integer> endWait = new HashMap<>();
    protected HashMap<String, Integer> slideNum = new HashMap<>();
    protected String lastLayerName="";
    private Point g_MscdPos;
    private String publicUnitName;
    protected ConsoleHelper consoleHelper;


    public SuperScriptThread(Context context, ScriptServiceListener scriptServiceListener) {
        super(context, scriptServiceListener);
        mContext = context;
        hudManage = HUDManage.getInstance();
        consoleHelper=ConsoleHelper.getInstance();
        if(consoleHelper.consoleMode)
        {
            consoleHelper.startDataHeartbeat();
        }
    }

    @Override
    protected abstract String getTag();

    @Override
    protected abstract String getAppName();

    public Point getMscdPos() {
        return g_MscdPos;
    }

    public String getUnitName() {
        return publicUnitName;
    }

    protected void mTap(int x, int y){
        Shell.Sync.sh(String.format("input tap %d %d",x,y));
    }

    protected void mSwipe(int x1,int y1,int x2,int y2){
        Shell.Sync.sh(String.format("input swipe %d %d %d %d",x1,y1,x2,y2));
    }

    protected void mTap(int x,int y,boolean conver){
        if(conver){
            mTap(y,719-x);
        }else{
            mTap(x,y);
        }
    }

    protected void mSwipe(int x1,int y1,int x2,int y2,boolean conver){
        if(conver){
            mSwipe(y1,719-x1,y2,719-x2);
        }else{
            mSwipe(x1,y1,x2,y2);
        }
    }
    private List<String[]> mLayer;
    private HashMap<String, List<ColorLayerUnit>> mUnit;
    protected void initColorAction(boolean conver) {
        //setColorMode(0);
        ColorAction colorAction = new ColorAction();
        String jsonLayerStr = FileUtils.readTextFromAESRaw(mContext, R.raw.layer_colors);
        mLayer = new Gson().fromJson(jsonLayerStr, new TypeToken<List<String[]>>() {
        }.getType());
        String jsonLayerUnitStr = FileUtils.readTextFromAESRaw(mContext, R.raw.unit_colors);
        HashMap<String, List<ColorLayerUnit>> unitMap = new Gson().fromJson(jsonLayerUnitStr, new TypeToken<HashMap<String, List<ColorLayerUnit>>>() {
        }.getType());
        if (conver) {
            Utils.colorsConver(mLayer);
        }
        for (String key : unitMap.keySet()) {
            List<ColorLayerUnit> unitList = unitMap.get(key);
            for (ColorLayerUnit unit : unitList) {
                String type = unit.getType();

                if (unit.getTap() instanceof ArrayList) {
                    ArrayList<Object> arr = ((ArrayList) unit.getTap());
                    if (arr.get(0) instanceof ArrayList) {
                        List<List<Integer>> newTapList = new ArrayList<>();
                        for (Object obj : arr) {
                            ArrayList objArr = (ArrayList) obj;
                            Point pt;
                            if (conver) {
                                pt = Utils.pointConver(Utils.objToInt(objArr.get(0)), Utils.objToInt(objArr.get(1)));
                            } else {
                                pt = new Point(Utils.objToInt(objArr.get(0)), Utils.objToInt(objArr.get(1)));
                            }
                            objArr.set(0, pt.x);
                            objArr.set(1, pt.y);
                            newTapList.add(objArr);
                        }
                        unit.setTap(newTapList);
                    } else {
                        if (type.equals("MSD")) {
                            if (conver) {
                                unit.setTap(new ArrayList<Integer>(Arrays.asList(-Utils.objToInt(arr.get(1)), Utils.objToInt(arr.get(0)))));
                            } else {
                                unit.setTap(new ArrayList<Integer>(Arrays.asList(Utils.objToInt(arr.get(1)), Utils.objToInt(arr.get(1)))));
                            }
                        } else {
                            Point pt;
                            if (conver) {
                                pt = Utils.pointConver(Utils.objToInt(arr.get(0)), Utils.objToInt(arr.get(1)));
                            } else {
                                pt = new Point(Utils.objToInt(arr.get(0)), Utils.objToInt(arr.get(1)));
                            }
                            arr.set(0, pt.x);
                            arr.set(1, pt.y);
                        }

                    }
                }

                switch (type) {
                    case "CC":
                        if (conver) {
                            unit.setC(Utils.pointColorsConver(unit.getC().toString(), true));
                        }
                        break;
                    case "MSC":
                    case "MSD":
                    case "FSD":
                    case "SD":
                        if (unit.getC() instanceof List) {
                            List<Object> mscArr = (List<Object>) unit.getC();
                            if (mscArr != null) {
                                List<Double> rect = (List<Double>) mscArr.get(0);
                                List<Integer> newRect;
                                if (conver) {
                                    mscArr.set(1, Utils.pointColorsConver(mscArr.get(1).toString(), true));
                                    newRect = new ArrayList<Integer>(Arrays.asList(719 - rect.get(3).intValue(), rect.get(0).intValue(), 719 - rect.get(1).intValue(), rect.get(2).intValue()));
                                } else {
                                    newRect = new ArrayList<Integer>(Arrays.asList(rect.get(0).intValue(), rect.get(1).intValue(), rect.get(2).intValue(), rect.get(3).intValue()));
                                }
                                mscArr.set(0, newRect);
                                unit.setC(mscArr);
                            }
                        }

                        if (CollectionUtils.isNotEmpty(unit.getNslide())) {
                            List<Integer> ns = unit.getNslide();
                            List<Integer> newNslide;
                            if (conver) {
                                newNslide = new ArrayList<>(Arrays.asList(719 - ns.get(1), ns.get(2), 719 - ns.get(3), ns.get(0)));
                            } else {
                                newNslide = new ArrayList<>(Arrays.asList(ns.get(0), ns.get(1), ns.get(2), ns.get(3)));
                            }
                            if (ns.size() > 4) {
                                newNslide.add(ns.get(4));
                            }
                            if (ns.size() > 5) {
                                newNslide.add(ns.get(5));
                            }
                            if (ns.size() > 6) {
                                newNslide.add(ns.get(6));
                            }
                            unit.setNslide(newNslide);
                        }
                        if (CollectionUtils.isNotEmpty(unit.getSlide())) {
                            List<Integer> sld = unit.getSlide();
                            List<Integer> newSlide;
                            if (conver) {
                                newSlide = new ArrayList<>(Arrays.asList(719 - sld.get(1), sld.get(2), 719 - sld.get(3), sld.get(0)));
                            } else {
                                newSlide = new ArrayList<>(Arrays.asList(sld.get(0), sld.get(1), sld.get(2), sld.get(3)));
                            }
                            if (sld.size() > 4) {
                                newSlide.add(sld.get(4));
                            }
                            if (sld.size() > 5) {
                                newSlide.add(sld.get(5));
                            }
                            if (sld.size() > 6) {
                                newSlide.add(sld.get(6));
                            }
                            unit.setSlide(newSlide);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        mUnit = unitMap;
    }

    protected boolean isEndWait(ColorLayerUnit unit, String layerName) {
        if (unit.getWait() != null) {
            if (endWait.get(layerName) == null) {
                endWait.put(layerName, 0);
            }
            if (endWait.get(layerName) < unit.getWait()) {
                endWait.put(layerName, endWait.get(layerName) + 1);
                return false;
            }
            endWait.put(layerName, 0);
        }
        return true;
    }


    protected boolean execTask(String layerName,TaskAction taskAction) {
        if (StringUtils.isEmpty(layerName)) {
            return false;
        }
        if (taskAction == null){
            return false;
        }
        List<ColorLayerUnit> unitList = mUnit.get(layerName);
        List<UnitAction> listTUA = taskAction.getUnitActionMap().get(layerName);
        if (CollectionUtils.isEmpty(unitList) || CollectionUtils.isEmpty(listTUA)) {
            return false;
        }

        g_MscdPos=null;
        for (UnitAction unitAction : listTUA) {
            for (ColorLayerUnit unit : unitList) {
                String unitName=unitAction.getUnitName();
                if(unitName==null){
                    break;
                }
                if (unit.getN().equals(unitName)) {
                    String type = unit.getType();
                    switch (type) {
                        case "CC":
                            if (cmpColorEx(unit.getC().toString(), 0.95f)) {
                                if (!isEndWait(unit, layerName)) {
                                    break;
                                }
                                if (unitAction.getCallback() == null || unitAction.getCallback().before()) {
                                    Log.d(getTag(), unit.getN() + unit.getC().toString());
                                    publicUnitName = unit.getN();
                                    if (unit.getTap() != null) {
                                        if (unit.getBdelay() != null) {
                                            delay(unit.getBdelay());
                                        }
                                        if (unit.getTap() instanceof ArrayList) {
                                            ArrayList<Object> arr = ((ArrayList) unit.getTap());
                                            if (arr.get(0) instanceof ArrayList) {
                                                for (Object obj : arr) {
                                                    ArrayList<Integer> objArr = (ArrayList) obj;
                                                    mTap(objArr.get(0), objArr.get(1),true);
                                                    if(objArr.size()>2){
                                                        delay(Utils.objToInt(objArr.get(2)));
                                                    }else{
                                                        delay(500);
                                                    }

                                                }
                                            } else {
                                                tap(Utils.objToInt(arr.get(0)), Utils.objToInt(arr.get(1)));
                                            }
                                        }
                                    }
                                    if (unitAction.getCallback() != null) {
                                        unitAction.getCallback().after();
                                    }
                                    if (unit.getDelay() != null) {
                                        delay(unit.getDelay());
                                    }
                                    return true;
                                }
                            }
                            break;
                        case "MSC":
                        case "MSD":
                            List<Object> mscArr = (List<Object>) unit.getC();
                            List<Integer> rect = (List<Integer>) mscArr.get(0);
                            Point mscPonit = findColors(rect.get(0), rect.get(1), rect.get(2), rect.get(3),
                                    mscArr.get(1).toString(), (float) ((double) mscArr.get(2) / 100));
                            if (mscPonit != null) {
                                Log.d(getTag(),unit.getN()+": "+mscPonit.x + "," + mscPonit.y);
                                publicUnitName = unit.getN();
                                g_MscdPos=mscPonit;
                                if (unitAction.getCallback() == null || unitAction.getCallback().before()) {
                                    int tapX = mscPonit.x, tapY = mscPonit.y;
                                    if (unit.getTap() != null) {
                                        if (unit.getBdelay() != null) {
                                            delay(unit.getBdelay());
                                        }
                                        if (unit.getTap() instanceof ArrayList) {
                                            ArrayList<Object> arr = ((ArrayList) unit.getTap());
                                            int tx = Utils.objToInt(arr.get(0));
                                            int ty = Utils.objToInt(arr.get(1));
                                            if (type.equals("MSD")) {
                                                tapX = mscPonit.x + tx;
                                                tapY = mscPonit.y + ty;
                                            } else {
                                                tapX = tx;
                                                tapY = ty;
                                            }
                                        }
                                    }
                                    tap(tapX, tapY);
                                    if (unitAction.getCallback() != null) {
                                        unitAction.getCallback().after();
                                    }
                                    if (unit.getDelay() != null) {
                                        delay(unit.getDelay());
                                    }
                                    return true;
                                }
                            } else if (CollectionUtils.isNotEmpty(unit.getNslide())) {
                                if (unitAction.getCallback() == null || unitAction.getCallback().before()) {
                                    if(unit.getSlideNum()!=null){
                                        String key=layerName+"_"+unitName;
                                        if (slideNum.get(key) == null) {
                                            slideNum.put(key, 0);
                                        }
                                        if (slideNum.get(key) >= unit.getSlideNum()) {
                                            continue;
                                        }
                                        slideNum.put(key, slideNum.get(key) + 1);
                                    }
                                    mSwipe(unit.getNslide().get(0), unit.getNslide().get(1),
                                            unit.getNslide().get(2), unit.getNslide().get(3),true);
                                    if (unit.getNslide().size() > 4) {
                                        delay(unit.getNslide().get(4));
                                    }
                                }
                            }
                            break;
                        case "FSD":
                            Point fsdPonit=null;
                            if(unit.getC() instanceof List){
                                List<Object> fsdArr = (List<Object>) unit.getC();
                                List<Integer> fsdRect = (List<Integer>) fsdArr.get(0);
                                fsdPonit = findColors(fsdRect.get(0), fsdRect.get(1), fsdRect.get(2), fsdRect.get(3),
                                        fsdArr.get(1).toString(), (float) ((double) fsdArr.get(2) / 100));
                            }
                            if (fsdPonit != null) {
                                Log.d(getTag(), fsdPonit.x + "," + fsdPonit.y);
                                if (unitAction.getCallback() == null || unitAction.getCallback().before()) {
                                    swipe(unit.getSlide().get(0), unit.getSlide().get(1),
                                            unit.getSlide().get(2), unit.getSlide().get(3));
                                    if (unitAction.getCallback() != null) {
                                        unitAction.getCallback().after();
                                    }
                                    if (unit.getDelay() != null) {
                                        delay(unit.getDelay());
                                    }
                                    return true;
                                }
                            }
                            break;
                        case "SD":
                            if (unitAction.getCallback() == null || unitAction.getCallback().before()) {

                                if(unit.getSlideNum()!=null){
                                    String key=layerName+"_"+unitName;
                                    if (slideNum.get(key) == null) {
                                        slideNum.put(key, 0);
                                    }
                                    if (slideNum.get(key) >= unit.getSlideNum()) {
                                        continue;
                                    }
                                    slideNum.put(key, slideNum.get(key) + 1);
                                }

                                swipe(unit.getSlide().get(0), unit.getSlide().get(1),
                                            unit.getSlide().get(2), unit.getSlide().get(3));
                                if (unit.getSlide().size() > 4) {
                                    delay(unit.getSlide().get(4));
                                }
                                if (unitAction.getCallback() != null) {
                                    unitAction.getCallback().after();
                                }
                                if (unit.getDelay() != null) {
                                    delay(unit.getDelay());
                                }

                                return true;
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        return false;
    }

    protected Point findColors(int x1, int y1, int x2, int y2, String colors, float similarity) {
        String colors1 = null;
        String colors2 = null;
        if (StringUtils.countMatches(colors, ",") > 0) {
            String[] strs = StringUtils.split(colors, ",");
            colors1 = strs[0].replace("0|0|", "");
            colors2 = colors.replace(strs[0] + ",", "");
        } else {
            colors1 = colors.replace("0|0|", "");
        }
        return findMultiColor(x1, y1, x2, y2, colors1, colors2, 1,similarity);
    }

    protected String getLayerName() {
        String result="未知頁面";
        for (String[] strs : mLayer) {
            float v;
            if (strs.length > 2 && strs[2] != null) {
                v = Float.valueOf(strs[2]) / 100;
            } else {
                v = 0.95f;
            }
            if (cmpColorEx(strs[1], v)) {
                result=strs[0];
                break;
            }
        }
        if(!lastLayerName.equals(result)){
            lastLayerName=result;
            if(!result.equals("未知頁面")){
                endWait.clear();
                slideNum.clear();
            }
        }
        return result;
    }


    protected void showTimeInfo(String text) {
        new Handler(mContext.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                hudManage.showHUD("time", text, 13, 200, 20, 200, 649, "#ffffff","#80000000",0);
            }
        });
    }

    protected void showMOVEInfo(String text) {
        new Handler(mContext.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                hudManage.showHUD("move", text, 13, 200, 20, 0, 1209, "#ffffff","#80000000",0);
            }
        });
    }

    protected void showHUDInfo(String text) {
        showHUDInfo(text, 13, 200, 20, 0, 1189, "#ffffff", "#80000000", 0);
    }

    protected void showHUDInfo(String text, Integer size, Integer width, Integer height, Integer x, Integer y, String color, String bg, Integer pos) {
        new Handler(mContext.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                hudManage.showHUD("info", text, size, width, height, x, y, color, bg, 0);
            }
        });
    }

    protected String dmocrGetText(int dictIndex,int x1, int y1, int x2, int y2, int color, int offset_color, int sim)
    {
        long s1 = System.currentTimeMillis();
        AntScriptUtils.RGB srcRgb = Int2Rgb(color);
        int r = srcRgb.R, g = srcRgb.G, b = srcRgb.B;
        AntScriptUtils.RGB offsetRgb = Int2Rgb(offset_color);
        int pr = offsetRgb.R, pg = offsetRgb.G, pb = offsetRgb.B;
        int MaxR = MIN(r + pr, 255), MaxG = MIN(g + pg, 255), MaxB = MIN(b + pb, 255);
        int SmlR = MAX(r - pr, 0), SmlG = MAX(g - pg, 0), SmlB = MAX(b - pb, 0);
        int lx = 0xffffff, ly = 0xffffff, rx = 0, ry = 0;
        AntScriptUtils.DICTINFO tMap=new AntScriptUtils.DICTINFO();
        tMap.w = y2 - y1;
        tMap.h = x2 - x1;
        tMap.data = new byte[tMap.w * tMap.h];
        x1 = x1 - 1;
        y1 = y1 - 1;
        Bitmap bitmap=rotaingImageView(-90,getScreenBitmap(x1,y1,x2,y2));
        if(bitmap!=null){
            for (int y = 1; y < tMap.h; y++)
            {
                for (int x = 1; x < tMap.w; x++)
                {
                    int c=bitmap.getPixel(x,y);
                    AntScriptUtils.RGB rgb = new AntScriptUtils.RGB(Color.red(c),Color.green(c),Color.blue(c));
                    if (rgb.R > MaxR || rgb.R < SmlR || rgb.G > MaxG || rgb.G < SmlG || rgb.B > MaxB || rgb.B < SmlB)
                    {
                        tMap.data[y * tMap.w + x] = 0;
                    }
                    else
                    {
                        tMap.data[y * tMap.w + x] = 1;
                        lx = x < lx ? x : lx;
                        ly = y < ly ? y : ly;
                        rx = x > rx ? x : rx;
                        ry = y > ry ? y : ry;
                        tMap.num++;
                    }
                }
            }
            bitmap.recycle();
        }
        String result="";
        if(rx>0 && ry>0){
            result=AntScriptUtils.dmocrGetText(dictIndex,tMap.w,tMap.h,tMap.data, lx, ly, rx + 2, ry + 2, sim);
        }
        Log.i("DMOCR",String.format("识别到字符串:%s 耗时:%d", result, (System.currentTimeMillis() - s1) / 1000));
        return result;
    }


    protected Integer dmocrGetInteger(int dictIndex,int x1, int y1, int x2, int y2, int color, int offset_color, int sim)
    {
        Integer result=null;
        String numStr=dmocrGetText(dictIndex,x1, y1, x2,y2,color,offset_color,sim);
        if(StringUtils.isNotEmpty(numStr)){
            result=Integer.valueOf(numStr);
        }
        return result;
    }


    protected int dmocrCreateDict(@RawRes int dict)
    {
        String dirPath="/sdcard/antscript/res";
        FileUtils.copyFilesFromRaw(mContext, dict, dict+".dict", dirPath);
        return AntScriptUtils.dmocrCreate(dirPath+"/"+dict+".dict");
    }

    protected Bitmap rotaingImageView(int angle, Bitmap bitmap)
    {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (resizedBitmap != bitmap && bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
            bitmap = null;
        }
        return resizedBitmap;
    }

    abstract public void run();
}
