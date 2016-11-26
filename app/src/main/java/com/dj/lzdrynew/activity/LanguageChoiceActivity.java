package com.dj.lzdrynew.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.dj.lzdrynew.R;

import java.lang.reflect.Method;
import java.util.Locale;

/**
 * 语言选择界面
 */
public class LanguageChoiceActivity extends BaseActivity {

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

        //初始化控件
        setViews();

        //设置监听事件
        setListeners();

        //初始化 language 的值
        setLanguage();

    }


    /**
     * 初始化 language 的值
     */
    private void setLanguage() {
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


    /**
     * 初始化控件
     */
    private void setViews() {
        //初始化wifi按钮
        initWifiSet();
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
        //通过反射改变系统语言
        updateLanguage(configuration.locale);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
