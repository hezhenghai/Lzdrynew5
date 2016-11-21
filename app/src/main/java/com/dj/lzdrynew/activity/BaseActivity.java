package com.dj.lzdrynew.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.dj.lzdrynew.R;
import com.dj.lzdrynew.model.MyConfig;
import com.dj.lzdrynew.utils.NetWorkUtils;
import com.dj.lzdrynew.utils.Util;
import com.dj.lzdrynew.views.NetWorkDialog;
import com.tencent.bugly.beta.Beta;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Activity基类
 */
public class BaseActivity extends Activity {

    /**
     * 当前子activity
     */
    private Activity activity;

    /**
     * 网络状态监听器
     */
    public NetWorkChangeReceiver mReceiver;

    /**
     * 网络设置提示框
     */
    public NetWorkDialog.Builder builder;
    public NetWorkDialog dialog;

    /**
     * wifi按钮
     */
    public ImageButton ib_wifi;

    /**
     * wifi信号图片显示
     */
    public ImageView iv_wifi;

    /**
     * 用于定时检测wifi信号的Timer
     */
    public Timer wifiTimer;

    /**
     * wifi信号检测
     */
    //Wifi管理器
    public WifiManager wifiManager = null;
    //获得的Wifi信息
    public WifiInfo wifiInfo = null;
    //信号强度值
    public int level;


    /**
     * 播放按键音
     */
    public SoundPool sp;//声明一个SoundPool
    public int music, music2, music3;//定义一个整型用load（）；来设置suondID

    /**
     * 音频管理器
     *
     * @param savedInstanceState
     */
    public AudioManager mAudioManager;


    /**
     * 定义Handler完成倒计时任务
     */
    public Handler baseHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            // 网络断开
            if (msg.what == 0x444) {
                Util.showLog("TAG", "4444444444444444444");
                iv_wifi.setImageResource(R.drawable.icon_wifi_no);
                showWifiSetDialog();
            }
            //网络连接成功
            if (msg.what == 0x555) {
                wifiSuccess();
            }

            //wifi信号
            if (msg.what == 0x9999) {
                wifiInfo = wifiManager.getConnectionInfo();
                level = wifiInfo.getRssi();
                Util.showLog("TAG", "wifi-------------- " + level);
                //根据获得的信号强度发送信息
                if (level <= 0 && level >= -50) {
                    iv_wifi.setImageResource(R.drawable.icon_wifi_strong);
                } else if (level < -50 && level >= -90) {
                    iv_wifi.setImageResource(R.drawable.icon_wifi_mediu);
                } else if (level < -90 && level > -200) {
                    iv_wifi.setImageResource(R.drawable.icon_wifi_weak);
                } else {
                    iv_wifi.setImageResource(R.drawable.icon_wifi_no);
                }
            }
        }
    };

    //网络连接后执行的操作
    public void wifiSuccess() {
        if (dialog != null) {
            dialog.dismiss();
        }
        Util.showLog("TAG", "55555555555555555555");
        wifiInfo = wifiManager.getConnectionInfo();
        level = wifiInfo.getRssi();
        Util.showLog("TAG", "wifi-------------- " + level);
        //根据获得的信号强度发送信息
        if (level <= 0 && level >= -50) {
            iv_wifi.setImageResource(R.drawable.icon_wifi_strong);
        } else if (level < -50 && level >= -90) {
            iv_wifi.setImageResource(R.drawable.icon_wifi_mediu);
        } else if (level < -90 && level > -200) {
            iv_wifi.setImageResource(R.drawable.icon_wifi_weak);
        } else {
            iv_wifi.setImageResource(R.drawable.icon_wifi_no);
        }
        // 检查升级
        Beta.checkUpgrade(false, false);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* 隐藏标题栏 */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /* 隐藏状态栏 */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        /* 设定屏幕显示为横屏 */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        /* 设定屏幕显示为竖屏 */
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//        if (Build.VERSION.SDK_INT >= 21) {
//            View decorView = getWindow().getDecorView();
//            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//            decorView.setSystemUiVisibility(option);
//            getWindow().setNavigationBarColor(Color.TRANSPARENT);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
        sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
        music = sp.load(this, R.raw.keytone, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
        music2 = sp.load(this, R.raw.playkey, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
        music3 = sp.load(this, R.raw.working, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级


        activity = this;

        /**
         * 获取音频管理器
         */
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // 检测wifi信号
        checkWifi();

        // 注册网络监听器
        mReceiver = new NetWorkChangeReceiver();
        registerReceiver(mReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }

    /**
     * 初始化wifi按钮
     */
    public void initWifiSet() {
        ib_wifi = (ImageButton) findViewById(R.id.ib_wifi);
        iv_wifi = (ImageView) findViewById(R.id.iv_wifi);
        ib_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(music, 1, 1, 0, 0, 1);
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);//系统设置界面
                startActivity(intent);
            }
        });
    }

//    /**
//     * 设置edittext获取焦点，hint隐藏
//     */
//    public View.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener() {
//        @Override
//        public void onFocusChange(View v, boolean hasFocus) {
//            EditText textView = (EditText) v;
//            String hint;
//            if (hasFocus) {
//                hint = textView.getHint().toString();
//                textView.setTag(hint);
//                textView.setHint("");
//            } else {
//                hint = textView.getTag().toString();
//                textView.setHint(hint);
//            }
//        }
//    };

    /**
     * 获取系统音量
     */
    public int getSystemVolume() {
        SharedPreferences share = getSharedPreferences(MyConfig.SYSTEM_VOLUME, MODE_PRIVATE);
        String volume = share.getString(MyConfig.SYSTEM_VOLUME, null);
        if (volume == null) {
            return MyConfig.DEFAULT_VOLUME;
        }
        return Integer.parseInt(volume);
    }

    /**
     * 获取系统最大音量
     */
    public int getSystemMaxVolume() {
        return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
    }


    /**
     * 设置系统音量
     */
    public void setSystemVolume(int volume) {
        SharedPreferences share = getSharedPreferences(MyConfig.SYSTEM_VOLUME, MODE_PRIVATE);
        SharedPreferences.Editor editor = share.edit();
        editor.putString(MyConfig.SYSTEM_VOLUME, String.valueOf(volume));
        editor.apply();
        volume = getSystemMaxVolume() * volume / 100;
        mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, volume, 0);
    }

    /**
     * 获取系统亮度
     */
    public int getScreenBrightness() {
        //先关闭系统的亮度自动调节
        try {
            if (Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE, android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        SharedPreferences share = getSharedPreferences(MyConfig.SYSTEM_BRIGHTNESS, MODE_PRIVATE);
        String brightness = share.getString(MyConfig.SYSTEM_BRIGHTNESS, null);
        if (brightness == null) {
            return MyConfig.DEFAULT_BRIGHTNESS;
        }
        return Integer.parseInt(brightness);
    }

    /**
     * 设置系统亮度
     */
    public void setScreenBrightness(int brightness) {
        brightness = brightness * 255 / 100;
        //不让屏幕全暗
        if (brightness <= 30) {
            brightness = 30;
        }
        //设置当前activity的屏幕亮度
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        //0到1,调整亮度暗到全亮
        lp.screenBrightness = brightness / 255f;
        getWindow().setAttributes(lp);
    }

    /**
     * 保存系统亮度
     */
    public void saveScreenBrightness(int brightness) {
        SharedPreferences share = getSharedPreferences(MyConfig.SYSTEM_BRIGHTNESS, MODE_PRIVATE);
        SharedPreferences.Editor editor = share.edit();
        editor.putString(MyConfig.SYSTEM_BRIGHTNESS, String.valueOf(brightness));
        editor.apply();
        //保存为系统亮度方法
        brightness = brightness * 255 / 100;
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
    }

    /**
     * 监听wifi信号，10秒监听一次
     */
    public void checkWifi() {
        // 获得WifiManager
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        // 使用定时器,每隔10秒获得一次信号强度值
        if (!NetWorkUtils.isWifiConnected(getBaseContext())) return;
        if (wifiTimer != null) {
            wifiTimer.cancel();
            wifiTimer = null;
        }
        wifiTimer = new Timer();
        wifiTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                baseHandler.sendEmptyMessage(0x9999);
            }
        }, 1000, 10000);
    }

    /**
     * 显示网络设置提示对话框
     */
    public synchronized void showWifiSetDialog() {
        if (dialog == null) {
            builder = new NetWorkDialog.Builder(activity);
            dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);// 点击外部区域不关闭
        }
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    /**
     * 监听网络状态
     */
    public class NetWorkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
                baseHandler.sendEmptyMessage(0x444);
            } else {
                baseHandler.sendEmptyMessage(0x555);
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (wifiTimer != null) {
            wifiTimer.cancel();
            wifiTimer = null;
        }
    }

    @Override
    protected void onDestroy() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (wifiTimer != null) {
            wifiTimer.cancel();
            wifiTimer = null;
        }
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
