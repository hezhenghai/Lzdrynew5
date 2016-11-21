package com.dj.lzdrynew.views;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dj.lzdrynew.R;

/**
 * 自定义弹框,提示wifi连接
 */
public class NetWorkDialog extends Dialog {


    public NetWorkDialog(Context context) {
        super(context);
    }

    public NetWorkDialog(Context context, int themeResId) {
        super(context, themeResId);
    }


    public static class Builder {
        /**
         * 播放按键音
         */
        public SoundPool sp;//声明一个SoundPool
        public int music;//定义一个整型用load（）；来设置suondID

        public Context context;
        public TextView tv_cancel, tv_set;

        public Builder(Context context) {
            this.context = context;
        }

        public NetWorkDialog create() {
            sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
            music = sp.load(context, R.raw.keytone, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final NetWorkDialog dialog = new NetWorkDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_wifi, null);
            dialog.addContentView(layout, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            dialog.setContentView(layout);
            tv_cancel = (TextView) layout.findViewById(R.id.tv_cancel);
            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sp.play(music, 1, 1, 0, 0, 1);
                    dialog.dismiss();
                }
            });
            tv_set = (TextView) layout.findViewById(R.id.tv_set);
            tv_set.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sp.play(music, 1, 1, 0, 0, 1);
                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);//系统设置界面
                    context.startActivity(intent);
                }
            });
            return dialog;
        }
    }

}
