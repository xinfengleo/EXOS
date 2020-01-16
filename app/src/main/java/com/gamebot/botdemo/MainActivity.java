package com.gamebot.botdemo;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fauth.library.core.AuthService;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.gamebot.botdemo.entity.VersionResult;
import com.gamebot.botdemo.service.FloatingViewService;
import com.gamebot.botdemo.utils.ConsoleHelper;
import com.gamebot.botdemo.utils.FileUtils;
import com.gamebot.botdemo.utils.HUDManage;
import com.gamebot.botdemo.utils.LoadDialog;
import com.gamebot.botdemo.utils.MsgboxUtils;
import com.gamebot.botdemo.utils.ProcessUtils;
import com.gamebot.botdemo.utils.ScreenMetrics;
import com.gamebot.botdemo.utils.Utils;
import com.gamebot.sdk.GameBotConfig;
import com.github.johnpersano.supertoasts.library.SuperToast;
import com.google.gson.Gson;
import com.topjohnwu.superuser.Shell;
import com.umeng.analytics.MobclickAgent;


import org.apache.commons.lang3.StringUtils;
import org.zeroturnaround.zip.ZipUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static String CHECK_VERSION_URL = "http://wmsj.ai.igcps.com/index/auth/get_version_link";
    @BindView(R.id.btn_start)
    ImageView startBtn;
    @BindView(R.id.btn_restart)
    ImageView restartBtn;
    @BindView(R.id.version_text)
    TextView versionText;
    AuthService authService;
    private boolean consoleStop = false;
    private LoadDialog loadDialog;
    private RelativeLayout rlMain;
    private boolean flag = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        consoleCheck();
        HUDManage.getInstance().init(getApplicationContext());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        versionText.setText("版本："+Utils.getLocalVersionName(this));
        checkScreenWH();
        authService=AuthService.getInstance();
        rlMain = (RelativeLayout)findViewById(R.id.rl_main);
        if (isServiceRunning(this,"com.gamebot.botdemo.service.FloatingViewService")){
            restartBtn.setVisibility(View.VISIBLE);
            startBtn.setVisibility(View.GONE);
        }
        checkOrientation();
        checkVersion();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkOverlays();
        }
        showWaiting();
        MobclickAgent.onProfileSignIn(authService.getDevId());
    }

    private void checkOrientation(){
        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
            //横屏
            rlMain.setBackgroundResource(R.drawable.gdlmg_logo_h);
        } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {
            //竖屏
            rlMain.setBackgroundResource(R.drawable.gdlmg_logo_v);
        }
    }

    public void checkVersion() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(20, TimeUnit.SECONDS).build();
        final Request request = new Request.Builder()
                .url(CHECK_VERSION_URL)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("TAG", "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resultStr = response.body().string();
                Log.e("onResponse", "onResponse: " + resultStr);
                if (StringUtils.contains(resultStr, "{")) {
                    try {
                        VersionResult result = new Gson().fromJson(resultStr, VersionResult.class);
                        if(result.getCode().equals(200)){
                            if(result.getData().getVersion_id()>Utils.getLocalVersion(getBaseContext())){
                                new Handler(getBaseContext().getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
//                                        MsgboxUtils.showMsg(getBaseContext(),"新版本提示","發現新的版本："+result.getData().getVersion()+"\n請到手遊助手(www.gdlmg.net)更新。\n更新內容：\n"+result.getData().getProfile(),null);
                                        showNormalDialog(result);
                                    }
                                });
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private void showNormalDialog(VersionResult result){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MainActivity.this);
        normalDialog.setIcon(R.mipmap.ic_launcher);
        normalDialog.setTitle("有新的版本更新");
        normalDialog.setMessage("\n更新內容：\n"+result.getData().getProfile());
        normalDialog.setPositiveButton("更新",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downLoadApk(result);
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        normalDialog.show();
    }


    public void downLoadApk(VersionResult result) {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("版本升級");
        progressDialog.setMessage("正在下載安裝包，");
        progressDialog.setProgressNumberFormat("");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(result.getData().getLink());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    // 获取到文件的大小
                    InputStream is = conn.getInputStream();
                    progressDialog.setMax(conn.getContentLength());
                    String fileName = "/sdcard/wmsj/wmsj.apk";
                    File file = new File(fileName);
                    if (file.exists()){
                        file.delete();
                    }
                    // 目录不存在创建目录
                    if (!file.getParentFile().exists())
                        file.getParentFile().mkdirs();
                    FileOutputStream fos = new FileOutputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    byte[] buffer = new byte[1024];
                    int len;
                    int total = 0;
                    while ((len = bis.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                        total += len;
                        // 获取当前下载量
                        progressDialog.setProgress(total);
                    }
                    fos.close();
                    bis.close();
                    is.close();
                    installApk(file);
                } catch (Exception e) {
                    Log.e("tag", "Exception="+e.getMessage());
                }finally {
                    progressDialog.dismiss();
                }
            }
        }.start();
    }

    private void installApk( File file) {
        Uri fileUri;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 防止打不开应用
//        if (Build.VERSION.SDK_INT < 23) {
            fileUri = Uri.fromFile(file);
//        }else if(Build.VERSION.SDK_INT >= 24){
//            fileUri = FileProvider.getUriForFile(MainActivity.this, "com.yunyi.yunk.fileprovider", file);
//            intent.addCategory("android.intent.category.DEFAULT");
//            //添加这一句表示对目标应用临时授权该Uri所代表的文件
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        }else {
//            fileUri=null;
//        }
        intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
        MainActivity.this.startActivity(intent);
    }

    private void consoleCheck() {
        ConsoleHelper consoleHelper = ConsoleHelper.getInstance();
        consoleHelper.init(this, getIntent());
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (consoleHelper.consoleMode && !consoleStop) {
                    startService();
                }
            }
        }, 2000);
    }

    protected ConsoleHelper consoleHelper;


    @Override
    protected void onDestroy() {
//        PushAgent.getInstance(this).disable(newwmsj IUmengCallback() {
//            @Override
//            public void onSuccess() {
//            }
//            @Override
//            public void onFailure(String s, String s1) {
//            }
//        });
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    private void checkScreenWH(){
        int w = ScreenMetrics.getDeviceScreenWidth();
        int h = ScreenMetrics.getDeviceScreenHeight();
        if(!(w==720 && h==1280)){
            MsgboxUtils.showMsg(getApplicationContext(),"當前分辨率("+w+"x"+h+")不支持!\n請設置為720x1280.");
        }
    }

    @OnClick(R.id.btn_start)
    public void startService() {
        progressDialog.show();
        Intent intent = new Intent(MainActivity.this, FloatingViewService.class);
        startService(intent);
        progressDialog.cancel();
        finish();
    }

    @OnClick(R.id.btn_restart)
    public void restartService() {
        progressDialog.show();
        Intent intent = new Intent(MainActivity.this, FloatingViewService.class);
        startService(intent);
        progressDialog.cancel();
        finish();
    }

    @OnTouch(R.id.btn_start)
    public boolean btnOnTouch(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            startBtn.setAlpha(0.8f);
        } else if(motionEvent.getAction() == MotionEvent.ACTION_MOVE){
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
            startBtn.setAlpha(1.0f);
        }
        return false;
    }

    @OnTouch(R.id.btn_restart)
    public boolean rebtnOnTouch(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            restartBtn.setAlpha(0.8f);
        } else if(motionEvent.getAction() == MotionEvent.ACTION_MOVE){
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
            restartBtn.setAlpha(1.0f);
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkOverlays(){
        Shell.Sync.su();
        if (!Settings.canDrawOverlays(this)) {
            final MaterialDialog dialog = new MaterialDialog(this);
            dialog.setCanceledOnTouchOutside(false);
            dialog.content("未开启顶层窗口权限!")//
                    .btnText("去开启")//
                    .btnNum(1)
                    .show();
            dialog.setOnBtnClickL(
                    new OnBtnClickL() {
                        @Override
                        public void onBtnClick() {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + getPackageName()));
                            startActivityForResult(intent,10);
                            dialog.dismiss();
                        }
                    }
            );
            return false;
        }else{
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
                checkOverlays();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            //询问用户权限
            if (permissions[0].equals(Manifest.permission.SYSTEM_ALERT_WINDOW) && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {
                //用户同意
            } else {
                //用户不同意
            }
        }

    }

    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);
        if (!(serviceList.size()>0)) {
            return false;
        }
        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    /**
     * 圆圈加载进度的 dialog
     */
    private void showWaiting() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("腳本加载中...");
        progressDialog.setIndeterminate(true);// 是否形成一个加载动画  true表示不明确加载进度形成转圈动画  false 表示明确加载进度
        progressDialog.setCancelable(false);//点击返回键或者dialog四周是否关闭dialog  true表示可以关闭 false表示不可关闭
    }
}
