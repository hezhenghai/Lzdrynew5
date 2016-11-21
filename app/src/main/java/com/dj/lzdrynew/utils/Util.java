package com.dj.lzdrynew.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * 工具类
 */
public class Util {

    private static Toast toast;

    /**
     * 吐司
     *
     * @param context
     * @param content
     */
    public static void showToast(Context context, String content) {
        if (toast == null) {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

    /**
     * 打印log
     *
     * @param context
     * @param s
     */
    public static void showLog(String context, String s) {
//        Log.i(context, s);
    }

    /**
     * 获取cup_id
     *
     * @return
     */
    public static String getCpuId() {
        String str = "", strCPU = "", cpuId = "";
        try {
            //读取CPU信息
            Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            //查找CPU序列号
            for (int i = 1; i < 100; i++) {
                str = input.readLine();
                if (str != null) {
                    //查找到序列号所在行
                    if (str.indexOf("Serial") > -1) {
                        //提取序列号
                        strCPU = str.substring(str.indexOf(":") + 1, str.length());
                        //去空格
                        cpuId = strCPU.trim();
                        //Log.d("GetCpuId ","= "+cpuId);
                        break;
                    }
                } else {
                    //文件结尾
                    break;
                }
            }
        } catch (Exception ex) {
            //赋予默认值
            ex.printStackTrace();
        }
        return cpuId;
    }

    /**
     * 获取蓝牙id
     */
    public static String getBleId() {
        BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
        return mAdapter.getAddress();
    }


    /**
     * 将秒值转成 HH:mm:ss 格式的字符串
     */
    public static String makeMS2Format(long mSecond) {
        long mMinute = mSecond / 60;
        long mHour = mMinute / 60;
        return String.format("%02d", mHour) + ":" + String.format("%02d", mMinute % 60) + ":" + String.format("%02d", mSecond % 60);
    }

    /**
     * 将秒值转成 mm:ss 格式的字符串
     */
    public static String makeMS2Format2(long mSecond) {
        long mMinute = mSecond / 60;
        return String.format("%02d", mMinute % 60) + ":" + String.format("%02d", mSecond % 60);
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
