package com.dj.lzdrynew.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dj.lzdrynew.R;
import com.dj.lzdrynew.model.MyConfig;
import com.dj.lzdrynew.model.VerificationMsg;
import com.dj.lzdrynew.utils.NetWorkUtils;
import com.dj.lzdrynew.utils.Util;
import com.dj.lzdrynew.views.LoadProgressDialog;
import com.dj.lzdrynew.views.SettingDialog;
import com.dj.lzdrynew.views.VersionDialog;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * 验证码界面
 */
public class VerificationCodeActivity extends BaseActivity {


    /**
     * 亮度进度值
     */
    int brightnessProgress = 0;

    /**
     * 返回，设置，版本，指导，视频按钮
     */
    private ImageButton ib_back, ib_setting, ib_version, ib_guide, ib_media;

    /**
     * 验证码输入框
     */
    private EditText et_verification_code;

    /**
     * 外部布局
     */
    private RelativeLayout relativeLayout_verification_code;

    /**
     * 二维码点击文字
     */
    private TextView tv_qr_code;

    /**
     * 确定按钮
     */
    private Button bt_ok;

    /**
     * 错误提示
     */
    private LinearLayout ll_verification_code_error;
    private TextView tv_verification_code_error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);
        // 初始化控件
        setViews();
        // 设置监听
        setListeners();
    }

    /**
     * 设置监听
     */
    private void setListeners() {
        et_verification_code.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText textView = (EditText) v;
                String hint;
                if (hasFocus) {
                    hint = textView.getHint().toString();
                    textView.setTag(hint);
                    textView.setHint("");
                    ll_verification_code_error.setVisibility(View.GONE);
                } else {
                    hint = textView.getTag().toString();
                    textView.setHint(hint);
                }
            }
        });

        et_verification_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(music, 1, 1, 0, 0, 1);
                ll_verification_code_error.setVisibility(View.GONE);
            }
        });

        /** 外层的布局控件 */
        relativeLayout_verification_code.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                relativeLayout_verification_code.setFocusable(true);
                relativeLayout_verification_code.setFocusableInTouchMode(true);
                relativeLayout_verification_code.requestFocus();
                // 隐藏输入法
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(relativeLayout_verification_code.getWindowToken(), 0);
                return false;
            }
        });
        // 使用二维码登录
        tv_qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(music, 1, 1, 0, 0, 1);
            }
        });
        tv_qr_code.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    tv_qr_code.getPaint().setFlags(0);
                    tv_qr_code.setText(getResources().getString(R.string.activity_verification_code_scan_qr_code));
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    tv_qr_code.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                    tv_qr_code.setText(getResources().getString(R.string.activity_verification_code_scan_qr_code));
                }
                return false;
            }
        });

        // 返回登录界面
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(music, 1, 1, 0, 0, 1);
                Intent intent = new Intent(VerificationCodeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        // 确认验证
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                sp.play(music, 1, 1, 0, 0, 1);
                //非空验证
                if (TextUtils.isEmpty(et_verification_code.getText().toString().trim())) {
                    //获取焦点，显示信息
                    ll_verification_code_error.setVisibility(View.VISIBLE);
                    tv_verification_code_error.setText(getResources().getString(R.string.verification_code_activity_verification_code_cannot_be_empty));
                    et_verification_code.setText("");
                    return;
                }
                if (!NetWorkUtils.isNetworkConnected(VerificationCodeActivity.this)) {
                    showWifiSetDialog();
                    return;
                }
                LoadProgressDialog.Builder builder = new LoadProgressDialog.Builder(VerificationCodeActivity.this);
                final LoadProgressDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);// 点击外部区域不关闭
                dialog.show();

                // 验证
                OkHttpUtils
                        .post()
                        .url(MyConfig.verificationUrl)
                        .addParams("verification", et_verification_code.getText().toString().trim())
                        .build()
                        .readTimeOut(10000)
                        .writeTimeOut(10000)
                        .connTimeOut(10000)
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, final Exception e, int i) {
                                Util.showLog("TAG", "verification-----onError------" + e);
                                dialog.dismiss();
                                //验证失败
                                Util.showToast(VerificationCodeActivity.this, getResources().getString(R.string.verification_code_activity_server_exception));
                            }

                            @Override
                            public void onResponse(final String s, int i) {
                                Util.showLog("TAG", "verification-----onResponse------" + s);
                                dialog.dismiss();
                                // 验证成功
                                VerificationMsg verificationMsg = new Gson().fromJson(s, VerificationMsg.class);
                                // 验证通过
                                if (verificationMsg.isSuccess()) {
                                    // 跳转到操作页面
                                    int remaintime = verificationMsg.getData().getRemaintime();
                                    if (remaintime <= 0) {
                                        Util.showToast(VerificationCodeActivity.this, getResources().getString(R.string.verification_code_activity_no_time_tips));
                                        return;
                                    }
                                    String verification = verificationMsg.getData().getVerification();
                                    // 保存剩余时间
                                    SharedPreferences sharedPreferences = getSharedPreferences(MyConfig.REMAINING_TIME, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(verification, String.valueOf(remaintime));
                                    editor.apply();
                                    Intent intent = new Intent(VerificationCodeActivity.this, OperationActivity.class);
                                    intent.putExtra(MyConfig.VERIFICATION, verification);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    if (verificationMsg.getMessages().equals("验证码不能为空！")) {
                                        tv_verification_code_error.setText(getResources().getString(R.string.verification_code_activity_verification_code_cannot_be_empty));
                                        ll_verification_code_error.setVisibility(View.VISIBLE);
                                    }
                                    if (verificationMsg.getMessages().equals("验证码错误！")) {
                                        tv_verification_code_error.setText(getResources().getString(R.string.verification_code_activity_verification_code_error));
                                        ll_verification_code_error.setVisibility(View.VISIBLE);
                                    }
                                }

                            }
                        });
            }
        });
        // 查看版本信息
        ib_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(music, 1, 1, 0, 0, 1);
                VersionDialog.Builder builder = new VersionDialog.Builder(VerificationCodeActivity.this);
                VersionDialog dialog = builder.create();
//                builder.tv_date.setText("11.15.2016");
//                builder.tv_version.setText(" V " + Util.getVersion(VerificationCodeActivity.this));
                dialog.setCanceledOnTouchOutside(false);// 点击外部区域不关闭
                dialog.show();
            }
        });

        // 查看操作视频
        ib_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //跳转到视频列表页面
                Util.showToast(VerificationCodeActivity.this, getResources().getString(R.string.this_function_to_develop));


            }
        });

        // 查看操作指导
        ib_guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到指导页面
                Intent intent = new Intent(VerificationCodeActivity.this, GuideActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 设置声音，亮度
        ib_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SettingDialog.Builder builder = new SettingDialog.Builder(VerificationCodeActivity.this);
                final SettingDialog dialog = builder.create();
                int voice = getSystemVolume();
                builder.sb_voice.setProgress(voice);
                builder.sb_voice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        setSystemVolume(progress);
                        seekBar.invalidate();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        builder.sb_voice.bm = BitmapFactory.decodeResource(getResources(), R.drawable.btn_voice_sel);
                        seekBar.invalidate();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        sp.play(music, 1, 1, 0, 0, 1);
                        builder.sb_voice.bm = BitmapFactory.decodeResource(getResources(), R.drawable.btn_voice_nor);
                        seekBar.invalidate();
                    }
                });
                int brightness = getScreenBrightness();
                builder.sb_brightness.setProgress(brightness);
                builder.sb_brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        brightnessProgress = progress;
                        setScreenBrightness(brightnessProgress);
                        seekBar.invalidate();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        builder.sb_brightness.bm = BitmapFactory.decodeResource(getResources(), R.drawable.btn_voice_sel);
                        seekBar.invalidate();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        sp.play(music, 1, 1, 0, 0, 1);
                        saveScreenBrightness(brightnessProgress);
                        builder.sb_brightness.bm = BitmapFactory.decodeResource(getResources(), R.drawable.btn_voice_nor);
                        seekBar.invalidate();
                    }
                });
                builder.ib_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sp.play(music, 1, 1, 0, 0, 1);
                        dialog.dismiss();
                    }
                });
                dialog.setCanceledOnTouchOutside(false);// 点击外部区域不关闭
                dialog.show();
            }
        });
    }

    /**
     * 初始化控件
     */
    private void setViews() {
        //初始化wifi按钮
        initWifiSet();
        relativeLayout_verification_code = (RelativeLayout) findViewById(R.id.relativeLayout_verification_code);
        ib_back = (ImageButton) findViewById(R.id.ib_back);
        et_verification_code = (EditText) findViewById(R.id.et_verification_code);
        bt_ok = (Button) findViewById(R.id.bt_ok);
        ib_setting = (ImageButton) findViewById(R.id.ib_setting);
        ib_guide = (ImageButton) findViewById(R.id.ib_guide);
        ib_version = (ImageButton) findViewById(R.id.ib_version);
        ib_media = (ImageButton) findViewById(R.id.ib_media);
        ll_verification_code_error = (LinearLayout) findViewById(R.id.ll_verification_code_error);
        tv_verification_code_error = (TextView) findViewById(R.id.tv_verification_code_error);
        tv_qr_code = (TextView) findViewById(R.id.tv_qr_code);
        tv_qr_code.getPaint().setFlags(0);
    }
}
