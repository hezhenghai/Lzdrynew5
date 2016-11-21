package com.dj.lzdrynew.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.dj.lzdrynew.R;

/**
 * 操作指南页面
 */

public class GuideActivity extends Activity {

    private ImageView iv_guide_image;

    private ImageButton ib_close, ib_back, ib_next;

    /**
     * 播放按键音
     */
    public SoundPool sp;//声明一个SoundPool
    public int music, music2, music3;//定义一个整型用load（）；来设置suondID

    // 中文版
    private int[] images = new int[]{R.drawable.msg_guide1, R.drawable.msg_guide2, R.drawable.msg_guide3, R.drawable.msg_guide4, R.drawable.msg_guide5, R.drawable.msg_guide6};
    // 英文版
    private int[] images_en = new int[]{R.drawable.msg_guide1_en, R.drawable.msg_guide2_en, R.drawable.msg_guide3_en, R.drawable.msg_guide4_en, R.drawable.msg_guide5_en, R.drawable.msg_guide6_en};

    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        SharedPreferences share = getSharedPreferences(LanguageChoiceActivity.LANGUAGE, MODE_PRIVATE);
        final String lan = share.getString(LanguageChoiceActivity.LANGUAGE, null);

        sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
        music = sp.load(this, R.raw.keytone, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
        music2 = sp.load(this, R.raw.playkey, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
        music3 = sp.load(this, R.raw.working, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级

        iv_guide_image = (ImageView) findViewById(R.id.iv_guide_image);
        if (lan.equals("en")) {
            iv_guide_image.setImageResource(images_en[index]);
        } else {
            iv_guide_image.setImageResource(images[index]);
        }
        ib_close = (ImageButton) findViewById(R.id.ib_close);
        ib_back = (ImageButton) findViewById(R.id.ib_back);
        ib_next = (ImageButton) findViewById(R.id.ib_next);
        ib_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(music, 1, 1, 0, 0, 1);
                Intent intent = new Intent(GuideActivity.this, VerificationCodeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(music, 1, 1, 0, 0, 1);
                index--;
                if (index <= 0) {
                    index = 0;
                }
                if (lan.equals("en")) {
                    iv_guide_image.setImageResource(images_en[index]);
                } else {
                    iv_guide_image.setImageResource(images[index]);
                }
            }
        });

        ib_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(music, 1, 1, 0, 0, 1);
                index++;
                if (index >= 5) {
                    index = 5;
                }
                if (lan.equals("en")) {
                    iv_guide_image.setImageResource(images_en[index]);
                } else {
                    iv_guide_image.setImageResource(images[index]);
                }
            }
        });
    }
}
