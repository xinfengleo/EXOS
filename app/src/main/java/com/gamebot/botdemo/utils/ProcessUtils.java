package com.gamebot.botdemo.utils;

import android.nfc.Tag;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.topjohnwu.superuser.Shell;

import org.apache.commons.lang3.StringUtils;


/**
 * Created by dzy on 2017/8/3.
 */

public class ProcessUtils {
    private static final String LOG_TAG = "ProcessUtils";

    public static int getProcessPid(Process process) {
        try {
            Field pid = process.getClass().getDeclaredField("pid");
            pid.setAccessible(true);
            return (int) pid.get(process);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getProcessID(String processName) {
        List<String> list = Shell.Sync.su("ps | grep '" + processName + "'");
        if (list!=null && list.size() > 0) {
            for (String line : list) {
                String[] strs = line.split("\\s+");
                if (strs.length == 9 && strs[8].equals(processName)) {
                    return Integer.parseInt(strs[1]);
                }
            }
        }
        return 0;
    }


    public static List<Integer> getProcessIDs(String processName) {
        List<String> list = Shell.Sync.su("ps | grep '" + processName + "'");
        List<Integer> result = new ArrayList<>(list.size());
        if (list!=null && list.size() > 0) {
            for (String line : list) {
                String[] strs = line.split("\\s+");
                if (strs.length == 9 && strs[8].equals(processName)) {
                    result.add(Integer.parseInt(strs[1]));
                }
            }
        }
        return result;
    }


    public static boolean killAllProcess(String processName) {
        List<Integer> ids = ProcessUtils.getProcessIDs(processName);
        boolean result = false;
        for (Integer id : ids) {
            Shell.Sync.su("kill " + id);
            result = true;
        }
        return result;
    }

    public static String getAppProcess() {
        String str = "app_process";
        if (new File("/system/bin/app_process32").exists()) {
            return "app_process32";
        }
        return str;
    }



    /**
     * 关闭Linux进程
     *
     * @param Pid 进程的PID
     */
    public static void killProcess(String Pid) {
        if (StringUtils.isEmpty(Pid)) {
            return;
        }
        Process process = null;
        BufferedReader reader = null;
        try {
            //杀掉进程  
            process = Runtime.getRuntime().exec("kill -9 " + Pid);
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                Log.d("ProcessUtils", "kill PID return info -----> " + line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {

                }
            }
        }
    }


    public String getFront() {
        String var1 = "";
        List<String> list = Shell.Sync.su("dumpsys activity top | grep 'ACTIVITY'");
        for (int i = 0; i < list.size(); ++i) {
            Pattern var4 = Pattern.compile("ACTIVITY ([^/]+)");
            Matcher var5 = var4.matcher((CharSequence) list.get(i));
            if (var5.find()) {
                var1 = var5.group(1);
            }
        }
        return var1;
    }


    public static boolean findModule(int pid, String moduleName) {
        if(pid>0){
            List<String> list = Shell.Sync.su("cat /proc/" + pid + "/maps");
            if (list!=null && list.size()>0) {
                for(String name : list) {
                    if (name.contains(moduleName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean findModule(String processName, String moduleName) {
        return ProcessUtils.findModule(ProcessUtils.getProcessID(processName),moduleName);
    }


}
