package com.dj.lzdrynew.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.dj.lzdrynew.R;
import com.dj.lzdrynew.model.MyConfig;
import com.dj.lzdrynew.utils.Util;

import java.util.Locale;

/**
 * 语言选择界面
 */
public class LanguageChoiceActivity extends Activity {

    /**
     * 音频管理器
     *
     * @param savedInstanceState
     */
    public AudioManager mAudioManager;

    /**
     * wifi按钮
     */
    public ImageButton ib_wifi;

    /**
     * wifi信号图片显示
     */
    public ImageView iv_wifi;


    /**
     * 播放按键音
     */
    public SoundPool sp;//声明一个SoundPool
    public int music;//定义一个整型用load（）；来设置suondID

    /**
     * 语言常量
     */
    public static final String LANGUAGE = "language";

    /**
     * 语言选择器
     */
    private CheckBox cb_ok;

    /**
     * 语言选择
     */
    private RadioButton rb_english, rb_chinese;

    /**
     * 语言
     */
    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_choice);

        sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
        music = sp.load(this, R.raw.keytone, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级

        /**
         * 获取音频管理器
         */
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        //初始化控件
        setViews();

        //设置监听事件
        setListeners();

        // 设置默认声音亮度
        setVolumeAndBBrightness();

        //初始化 language 的值
        setLanguage();

//        // 显示cpuid
//        TextView tv_cpuid = (TextView) findViewById(R.id.tv_cpuid);
//        tv_cpuid.setText(Util.getCpuId());

    }

    /**
     * 设置默认声音亮度
     */
    private void setVolumeAndBBrightness() {
        setSystemVolume(getSystemVolume());
        setScreenBrightness(getScreenBrightness());
    }

    /**
     * 初始化 language 的值
     */
    private void setLanguage() {
        SharedPreferences share = getSharedPreferences(LANGUAGE, MODE_PRIVATE);
        String lan = share.getString(LANGUAGE, null);
        if (lan != null) {
            language = lan;
            switchLanguage(language);
            Intent intent = new Intent(LanguageChoiceActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            //获取默认语言，判断是否选中按钮
            String aDefault = Locale.getDefault().toString();
            String adefault2 = aDefault.substring(0, 2);
            String english = Locale.ENGLISH.toString();
            if (english.equals(adefault2)) {
                language = "en";
                rb_english.setChecked(true);
            } else {
                language = "zh";
                rb_chinese.setChecked(true);
            }
        }

    }


    /**
     * 初始化控件
     */
    private void setViews() {
        //初始化wifi按钮
//        initWifiSet();
        ib_wifi = (ImageButton) findViewById(R.id.ib_wifi);
        iv_wifi = (ImageView) findViewById(R.id.iv_wifi);
        cb_ok = (CheckBox) findViewById(R.id.cb_ok);
        rb_english = (RadioButton) findViewById(R.id.rb_english);
        rb_chinese = (RadioButton) findViewById(R.id.rb_chinese);
    }

    /**
     * 设置监听事件
     */
    private void setListeners() {
        cb_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(music, 1, 1, 0, 0, 1);
                switchLanguage(language);
                saveLanguage();
                Intent intent = new Intent(LanguageChoiceActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        rb_english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(music, 1, 1, 0, 0, 1);
                language = "en";
            }
        });

        rb_chinese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(music, 1, 1, 0, 0, 1);
                language = "ch";
            }
        });

        ib_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(music, 1, 1, 0, 0, 1);
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);//系统设置界面
                startActivity(intent);
            }
        });

    }

    /**
     * 保存语言设置到本地
     */
    private void saveLanguage() {
        SharedPreferences share = getSharedPreferences(LANGUAGE, MODE_PRIVATE);
        SharedPreferences.Editor editor = share.edit();
        editor.putString(LANGUAGE, language);
        editor.apply();
    }

    /**
     * 改变系统语言
     * 默认为中文
     *
     * @param language
     */
    private void switchLanguage(String language) {
        //设置应用语言类型
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (language.equals("en")) {
            configuration.locale = Locale.ENGLISH;
        } else {
            configuration.locale = Locale.SIMPLIFIED_CHINESE;
        }
        resources.updateConfiguration(configuration, dm);
    }


//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (wifiTimer != null) {
//            wifiTimer.cancel();
//            wifiTimer = null;
//        }
//    }


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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
