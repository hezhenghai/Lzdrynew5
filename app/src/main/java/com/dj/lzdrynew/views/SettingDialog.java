package com.dj.lzdrynew.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dj.lzdrynew.R;
import com.dj.lzdrynew.utils.Util;

/**
 * Created by Administrator
 * on 2016/11/1.
 */

public class SettingDialog extends Dialog {

    public SettingDialog(Context context) {
        super(context);
    }

    public SettingDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected SettingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder {
        private Activity context;

        public MySeekBar sb_voice, sb_brightness;


        public ImageButton ib_delete;

        public Builder(Activity context) {
            this.context = context;
        }

        public SettingDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final SettingDialog dialog = new SettingDialog(context, R.style.SettingDialog);
            View layout = inflater.inflate(R.layout.dialog_setting, null);
            sb_voice = (MySeekBar) layout.findViewById(R.id.sb_voice);
            sb_brightness = (MySeekBar) layout.findViewById(R.id.sb_brightness);
            ib_delete = (ImageButton) layout.findViewById(R.id.ib_delete);
            dialog.addContentView(layout, new RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            dialog.setContentView(layout);
            return dialog;
        }
    }

}
