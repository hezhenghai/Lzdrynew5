package com.dj.lzdrynew.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dj.lzdrynew.R;

/**
 * Created by Administrator
 * on 2016/11/1.
 */

public class VersionDialog extends Dialog {

    public VersionDialog(Context context) {
        super(context);
    }

    public VersionDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected VersionDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder {

        /**
         * 播放按键音
         */
        public SoundPool sp;//声明一个SoundPool
        public int music;//定义一个整型用load（）；来设置suondID


        private Activity context;

        public TextView tv_version, tv_date;

        public ImageButton ib_delete;

        public Builder(Activity context) {
            this.context = context;
        }

        public VersionDialog create() {
            sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
            music = sp.load(context, R.raw.keytone, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final VersionDialog dialog = new VersionDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_version, null);
            tv_version = (TextView) layout.findViewById(R.id.tv_version);
            tv_date = (TextView) layout.findViewById(R.id.tv_date);
            ib_delete = (ImageButton) layout.findViewById(R.id.ib_delete);
            ib_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sp.play(music, 1, 1, 0, 0, 1);
                    dialog.dismiss();
                }
            });
            dialog.addContentView(layout, new RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            dialog.setContentView(layout);
            return dialog;
        }
    }

}
