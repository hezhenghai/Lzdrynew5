package com.dj.lzdrynew.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dj.lzdrynew.R;
import com.dj.lzdrynew.model.MyConfig;
import com.dj.lzdrynew.model.SaveMsg;
import com.dj.lzdrynew.serial.SerialData;
import com.dj.lzdrynew.serial.SerialDataUtils;
import com.dj.lzdrynew.utils.Util;
import com.dj.lzdrynew.views.SettingDialog;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import android_serialport_api.SerialPort;
import okhttp3.Call;

/**
 * 操作界面
 */

public class OperationActivity extends BaseActivity {

    /**
     * 再按一次退出
     */
    private long exitTime = 0;

    /**
     * cpuid
     */
    private String cpuid;

    /**
     * 验证码
     */
    private String verification;


    /**
     * 卡剩余时间显示控件
     */
    private TextView tv_total_remaining;

    /**
     * 返回到验证码界面
     */
    private ImageButton ib_back;

    /**
     * 控制正负极
     */
    private ToggleButton tb_anode_cathode;

    /**
     * 正负极文本显示
     */
    private TextView tv_anode, tv_cathode;

    /**
     * 能量状态控制按钮
     */
    private RadioButton low_radio_button, high_radio_button;
    private RadioGroup power_level_radioGroup;

    /**
     * 能量控制条
     */
    private SeekBar lowSeekBar, highSeekBar;

    /**
     * 能量控制条，背景
     */
    private ImageView imageView_low, imageView_high;

    /**
     * 时间控制按钮
     */
    private ImageButton ib_add_time, ib_minus_time;

    /**
     * 单次剩余时间显示控件
     */
    private TextView tv_remaining;

    /**
     * 开始，暂停按钮
     */
    private ImageButton ib_start;
    private TextView tv_start;

    /**
     * 操作记录
     */
    private LinearLayout ll_operation_record;

    /**
     * 设置
     */
    private ImageButton ib_setting;

    /**
     * 串口通信相关变量
     */
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private String sPort = "/dev/ttyS4";
    private int iBaudRate = 115200;
    private String receiveString;

    /**
     * 进度条的13个位置值
     */
    private int[] progressValue = {0, 85, 167, 252, 335, 418, 502, 583, 666, 750, 832, 916, 1000};

    /**
     * 定义多少秒上传一次总时间
     */
    private int typeTime = 0;

    /**
     * 能量状态值
     */
    public static final String LOW = "low";//低电流
    public static final String HIGH = "high";//高电流
    private String powerState = LOW;

    /**
     * 能量值等级
     */
    private int power = 0;

    /**
     * 剩余时间 分钟
     */
    private int mTime;

    /**
     * 用于记录分钟变换
     */
    private int minuteType;


    /**
     * 定义一个Timer,定时发消息
     */
    private Timer mTimer;

    /**
     * 卡剩余时间
     */
    private int mTotalTimeRemaining;

    /**
     * 判断是否在治疗中
     */
    private boolean isWorking = false;

    /**
     * 治疗时间最大值（15*60秒）
     */
    private int max = 15 * 60;

    /**
     * 工作提示音，间隔
     */
    private int mmmm = 0;

    /**
     * 亮度进度值
     */
    int brightnessProgress = 0;

    /**
     * 定义Handler完成倒计时任务
     */
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                if (mTime > 0) {
                    if (mmmm >= 2) {
                        mmmm = 0;
                        sp.play(music3, 1, 1, 0, 0, 1);
                    } else {
                        mmmm++;
                    }
                    mTotalTimeRemaining--;
                    mTime--;
                    updateTimeDisplay();
                    updateTimeRemaining();
                }
                //消费码剩余时间用完
                //工作完成,结束计时任务,恢复初始,提示,重置时间为max,重置电流为0
                if (mTime <= 0 || mTotalTimeRemaining <= 0) {
                    stopWork();
                    setInit();
                }
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_operation);

        verification = getIntent().getExtras().getString(MyConfig.VERIFICATION);

        //获取串口实例
        try {
            mSerialPort = new SerialPort(new File(sPort), iBaudRate, 0);
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 初始化控件
        setViews();

        // 为各组件设置监听事件
        setListeners();

        // 将联网获取的卡剩余时间赋值,获取客户id
        getMsg();

        // 设置初始状态
        setInit();
    }

    /**
     * 设置初始状态
     */
    private void setInit() {
        tb_anode_cathode.setChecked(false);
        low_radio_button.setChecked(true);
        //进度条位置归零
        power = 0;
        updatePowerSend();
        lowSeekBar.setProgress(0);
        highSeekBar.setProgress(0);
        lowSeekBar.setEnabled(true);
        highSeekBar.setEnabled(false);
        mTime = 0;
        updateTimeDisplay();
        updateTimeSend();
        tv_start.setText(getResources().getString(R.string.activity_operation_start));
        isWorking = false;
        send(SerialData.CHOOSE_LOW_ENERGY_TREATMENT);
    }

    /**
     * 获取剩余时间
     */
    private void getMsg() {
        SharedPreferences share = getSharedPreferences(MyConfig.REMAINING_TIME, MODE_PRIVATE);
        String remaining_time = share.getString(verification, null);
        if (remaining_time == null) {
            mTotalTimeRemaining = 0;
        } else {
            try {
                int localTime = Integer.parseInt(remaining_time);
                if (localTime > 0) {
                    mTotalTimeRemaining = localTime;
                } else {
                    mTotalTimeRemaining = 0;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        Util.showLog("TAG", "get----totaltime--------" + mTotalTimeRemaining);
        updateTimeRemaining();
    }

    /**
     * 初始化控件
     */
    private void setViews() {
        initWifiSet();
        ib_back = (ImageButton) findViewById(R.id.ib_back);
        ll_operation_record = (LinearLayout) findViewById(R.id.ll_operation_record);
        tv_total_remaining = (TextView) findViewById(R.id.tv_total_remaining);
        tb_anode_cathode = (ToggleButton) findViewById(R.id.tb_anode_cathode);
        tv_anode = (TextView) findViewById(R.id.tv_anode);
        tv_cathode = (TextView) findViewById(R.id.tv_cathode);
        low_radio_button = (RadioButton) findViewById(R.id.low_radio_button);
        high_radio_button = (RadioButton) findViewById(R.id.high_radio_button);
        power_level_radioGroup = (RadioGroup) findViewById(R.id.power_level_radioGroup);
        lowSeekBar = (SeekBar) findViewById(R.id.low_seekBar);
        highSeekBar = (SeekBar) findViewById(R.id.high_seekBar);
        imageView_low = (ImageView) findViewById(R.id.imageView_low);
        imageView_high = (ImageView) findViewById(R.id.imageView_high);
        ib_add_time = (ImageButton) findViewById(R.id.ib_add_time);
        ib_minus_time = (ImageButton) findViewById(R.id.ib_minus_time);
        tv_remaining = (TextView) findViewById(R.id.tv_remaining);
        ib_start = (ImageButton) findViewById(R.id.ib_start);
        tv_start = (TextView) findViewById(R.id.tv_start);
        ib_setting = (ImageButton) findViewById(R.id.ib_setting);
    }

    /**
     * 设置监听
     */
    private void setListeners() {
        //返回到消费码界面
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(music, 1, 1, 0, 0, 1);
                if (System.currentTimeMillis() - exitTime > 2000) {
                    Util.showToast(OperationActivity.this, getResources().getString(R.string.operation_activity_press_another_exit));
                    exitTime = System.currentTimeMillis();
                } else {
                    stopWork();
                    Intent intent = new Intent(OperationActivity.this, VerificationCodeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        //设置操作记录按钮点击效果
        ll_operation_record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ll_operation_record.setBackgroundResource(R.drawable.btn_bg_nor_2);
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ll_operation_record.setBackgroundResource(R.drawable.btn_bg_sel_2);
                }
                return false;
            }
        });
        // 进入操作记录界面
        ll_operation_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(music, 1, 1, 0, 0, 1);
                Util.showToast(OperationActivity.this, getResources().getString(R.string.this_function_to_develop));
//                Intent intent = new Intent();
//                startActivity(intent);
            }
        });

        lowSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sp.play(music, 1, 1, 0, 0, 1);
                int seekProgress = lowSeekBar.getProgress();
                // {0, 85, 167, 252, 335, 418, 502, 583, 666, 750, 832, 916, 1000}
                //  {42, 126, 209, 293, 376, 460, 542, 624, 708, 791, 874, 958}
                if (seekProgress < 43) {
                    power = 0;
                } else if (seekProgress >= 42 && seekProgress < 126) {
                    power = 1;
                } else if (seekProgress >= 126 && seekProgress < 209) {
                    power = 2;
                } else if (seekProgress >= 209 && seekProgress < 293) {
                    power = 3;
                } else if (seekProgress >= 293 && seekProgress < 376) {
                    power = 4;
                } else if (seekProgress >= 376 && seekProgress < 460) {
                    power = 5;
                } else if (seekProgress >= 460 && seekProgress < 542) {
                    power = 6;
                } else if (seekProgress >= 542 && seekProgress < 624) {
                    power = 7;
                } else if (seekProgress >= 624 && seekProgress < 708) {
                    power = 8;
                } else if (seekProgress >= 708 && seekProgress < 791) {
                    power = 9;
                } else if (seekProgress >= 791 && seekProgress < 874) {
                    power = 10;
                } else if (seekProgress >= 874 && seekProgress < 958) {
                    power = 11;
                } else if (seekProgress >= 958) {
                    power = 12;
                }

                //更新能量等级
                updatePowerSend();
                //设置进度条显示
                setSeekBarUpdate();
            }
        });

        highSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sp.play(music, 1, 1, 0, 0, 1);
                int seekProgress = highSeekBar.getProgress();
                // {0, 85, 167, 252, 335, 418, 502, 583, 666, 750, 832, 916, 1000}
                //  {42, 126, 209, 293, 376, 460, 542, 624, 708, 791, 874, 958}
                if (seekProgress < 43) {
                    power = 0;
                } else if (seekProgress >= 42 && seekProgress < 126) {
                    power = 1;
                } else if (seekProgress >= 126 && seekProgress < 209) {
                    power = 2;
                } else if (seekProgress >= 209 && seekProgress < 293) {
                    power = 3;
                } else if (seekProgress >= 293 && seekProgress < 376) {
                    power = 4;
                } else if (seekProgress >= 376 && seekProgress < 460) {
                    power = 5;
                } else if (seekProgress >= 460 && seekProgress < 542) {
                    power = 6;
                } else if (seekProgress >= 542 && seekProgress < 624) {
                    power = 7;
                } else if (seekProgress >= 624 && seekProgress < 708) {
                    power = 8;
                } else if (seekProgress >= 708 && seekProgress < 791) {
                    power = 9;
                } else if (seekProgress >= 791 && seekProgress < 874) {
                    power = 10;
                } else if (seekProgress >= 874 && seekProgress < 958) {
                    power = 11;
                } else if (seekProgress >= 958) {
                    power = 12;
                }
                //更新能量等级
                updatePowerSend();
                //设置进度条显示
                setSeekBarUpdate();
            }
        });
        // 正负极切换
        tb_anode_cathode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.play(music2, 1, 1, 0, 0, 1);
                if (isChecked) {
                    //负极
                    tv_anode.setVisibility(View.GONE);
                    tv_cathode.setVisibility(View.VISIBLE);
                    send(SerialData.SET_THE_OUTPUT_POSITIVE_VOLTAGE);
                } else {
                    //正极
                    tv_anode.setVisibility(View.VISIBLE);
                    tv_cathode.setVisibility(View.GONE);
                    send(SerialData.SET_THE_OUTPUT_LOAD_VOLTAGE);
                }
            }
        });

        // 设置能量高低
        power_level_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                sp.play(music, 1, 1, 0, 0, 1);
                if (checkedId == low_radio_button.getId()) {
                    lowSeekBar.setThumb(getResources().getDrawable(R.drawable.btn_slide_sel));
                    imageView_low.setImageResource(R.drawable.content_power_sel);
                    highSeekBar.setThumb(getResources().getDrawable(R.drawable.btn_slide_nor));
                    imageView_high.setImageResource(R.drawable.content_power_nor);
                    lowSeekBar.setThumbOffset(0);
                    highSeekBar.setThumbOffset(0);
                    powerState = LOW;
                    lowSeekBar.setEnabled(true);
                    highSeekBar.setEnabled(false);
                    send(SerialData.CHOOSE_LOW_ENERGY_TREATMENT);
                }
                if (checkedId == high_radio_button.getId()) {
                    lowSeekBar.setThumb(getResources().getDrawable(R.drawable.btn_slide_nor));
                    imageView_low.setImageResource(R.drawable.content_power_nor);
                    highSeekBar.setThumb(getResources().getDrawable(R.drawable.btn_slide_sel));
                    imageView_high.setImageResource(R.drawable.content_power_sel);
                    lowSeekBar.setThumbOffset(0);
                    highSeekBar.setThumbOffset(0);
                    powerState = HIGH;
                    lowSeekBar.setEnabled(false);
                    highSeekBar.setEnabled(true);
                    send(SerialData.CHOOSE_HIGH_ENERGY_TREATMENT);
                }
                power = 0;
                updatePowerSend();
                stopWork();
                lowSeekBar.setProgress(0);
                highSeekBar.setProgress(0);
            }
        });

        // 加时间
        ib_add_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(music, 1, 1, 0, 0, 1);
                setMax();
                if (mTime == max) return;
                if (mTime < max) {
                    mTime += 60;
                }
                if (mTime >= max) {
                    mTime = max;
                    if (mTotalTimeRemaining < 15 * 60) {
                        // 剩余时间已不足15分钟，请及时充值！
                        Util.showToast(OperationActivity.this, getResources().getString(R.string.operation_activity_remaining_time_insufficient));
                    }
                }
                //更新时间
                updateTimeDisplay();
                updateTimeSend();
            }
        });
        // 减时间
        ib_minus_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(music, 1, 1, 0, 0, 1);
                if (mTime <= 0) {
                    if (isWorking) {
                        stopWork();
                        setInit();
                    }
                    return;
                }
                if (mTime > 0) {
                    mTime -= 60;
                }
                if (mTime <= 0) {
                    mTime = 0;
                    if (isWorking) {
                        stopWork();
                        setInit();
                    }
                }
                //更新时间
                updateTimeDisplay();
                updateTimeSend();
            }
        });

        // 开始
        ib_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(music, 1, 1, 0, 0, 1);
                if (isWorking) {
                    //暂停
                    stopWork();
                } else {
                    if (mTime != 0 && power != 0) {
                        //开始
                        startWork();
                    } else {
                        // 能量或时间为0，请设置！
                        Util.showToast(OperationActivity.this, getResources().getString(R.string.operation_activity_power_or_time_is_0));
                    }
                }
            }
        });

        // 设置声音，亮度
        ib_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(music, 1, 1, 0, 0, 1);
                final SettingDialog.Builder builder = new SettingDialog.Builder(OperationActivity.this);
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
     * 发串口数据
     */
    public void send(final String string) {
        try {
            //去掉空格
            String s = string;
            s = s.replace(" ", "");
            Util.showLog("TAG", s);
            byte[] bytes = SerialDataUtils.HexToByteArr(s);
            mOutputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化max
     */
    private void setMax() {
        if (mTotalTimeRemaining <= 0) {
            max = 0;
            return;
        }
        if (mTotalTimeRemaining >= 15 * 60) {
            max = 15 * 60;
        }

        if (mTotalTimeRemaining < 15 * 60) {
            max = mTotalTimeRemaining;
        }
    }

    /**
     * 给下位机发送能量等级
     */
    private void updatePowerSend() {
        switch (power) {
            case 0:
                send(SerialData.SET_THE_ENERGY_LEVEL_OF_0);
                break;
            case 1:
                send(SerialData.SET_THE_ENERGY_LEVEL_OF_1);
                break;
            case 2:
                send(SerialData.SET_THE_ENERGY_LEVEL_OF_2);
                break;
            case 3:
                send(SerialData.SET_THE_ENERGY_LEVEL_OF_3);
                break;
            case 4:
                send(SerialData.SET_THE_ENERGY_LEVEL_OF_4);
                break;
            case 5:
                send(SerialData.SET_THE_ENERGY_LEVEL_OF_5);
                break;
            case 6:
                send(SerialData.SET_THE_ENERGY_LEVEL_OF_6);
                break;
            case 7:
                send(SerialData.SET_THE_ENERGY_LEVEL_OF_7);
                break;
            case 8:
                send(SerialData.SET_THE_ENERGY_LEVEL_OF_8);
                break;
            case 9:
                send(SerialData.SET_THE_ENERGY_LEVEL_OF_9);
                break;
            case 10:
                send(SerialData.SET_THE_ENERGY_LEVEL_OF_10);
                break;
            case 11:
                send(SerialData.SET_THE_ENERGY_LEVEL_OF_11);
                break;
            case 12:
                send(SerialData.SET_THE_ENERGY_LEVEL_OF_12);
                break;
        }
    }

    /**
     * 更新时间显示
     */
    private void updateTimeDisplay() {
        tv_remaining.setText(Util.makeMS2Format2(mTime));
    }

    /**
     * 发送时间给下位机
     */
    private void updateTimeSend() {
        int fff = mTime / 60;
        int mmm = mTime % 60;
        if (mmm > 0) {
            minuteType = fff + 1;
        } else {
            minuteType = fff;
        }
        switch (minuteType) {
            case 0:
                send(SerialData.SET_THE_COURSE_TIME_0);
                break;
            case 1:
                send(SerialData.SET_THE_COURSE_TIME_1);
                break;
            case 2:
                send(SerialData.SET_THE_COURSE_TIME_2);
                break;
            case 3:
                send(SerialData.SET_THE_COURSE_TIME_3);
                break;
            case 4:
                send(SerialData.SET_THE_COURSE_TIME_4);
                break;
            case 5:
                send(SerialData.SET_THE_COURSE_TIME_5);
                break;
            case 6:
                send(SerialData.SET_THE_COURSE_TIME_6);
                break;
            case 7:
                send(SerialData.SET_THE_COURSE_TIME_7);
                break;
            case 8:
                send(SerialData.SET_THE_COURSE_TIME_8);
                break;
            case 9:
                send(SerialData.SET_THE_COURSE_TIME_9);
                break;
            case 10:
                send(SerialData.SET_THE_COURSE_TIME_10);
                break;
            case 11:
                send(SerialData.SET_THE_COURSE_TIME_11);
                break;
            case 12:
                send(SerialData.SET_THE_COURSE_TIME_12);
                break;
            case 13:
                send(SerialData.SET_THE_COURSE_TIME_13);
                break;
            case 14:
                send(SerialData.SET_THE_COURSE_TIME_14);
                break;
            case 15:
                send(SerialData.SET_THE_COURSE_TIME_15);
                break;
        }

    }

    /**
     * 设置进度条显示
     */
    private void setSeekBarUpdate() {
        if (powerState.equals(LOW)) {
            lowSeekBar.setProgress(progressValue[power]);
        } else if (powerState.equals(HIGH)) {
            highSeekBar.setProgress(progressValue[power]);
        }
    }

    /**
     * 更新剩余时间
     */
    private void updateTimeRemaining() {
        tv_total_remaining.setText(Util.makeMS2Format(mTotalTimeRemaining));
        typeTime++;
        if (typeTime >= 60) {
            typeTime = 0;
            uploading();
        }
    }

    /**
     * 执行倒计时任务
     */
    private void startCountDown() {
        stopCountDown();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0x123);
            }
        }, 0, 1000);
    }

    /**
     * 停止计时任务
     */
    private void stopCountDown() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    /**
     * 上传时间到服务器
     */
    private void uploading() {
        Util.showLog("TAG", "uploading----totaltime--------" + mTotalTimeRemaining);
        OkHttpUtils
                .post()
                .url(MyConfig.RemainingTimeUrl)
                .addParams("verification", verification)
                .addParams("remaintime", String.valueOf(mTotalTimeRemaining))
                .build()
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, final Exception e, int i) {
                        Util.showLog("TAG", "remaining_time-------onError----------" + e);
                        //上传失败
//                            Util.showToast(MainActivity.this, e.toString());
                    }

                    @Override
                    public void onResponse(final String s, int i) {
                        Util.showLog("TAG", "remaining_time-------onResponse----------" + s);
                        SaveMsg saveMsg = null;
                        try {
                            saveMsg = new Gson().fromJson(s, SaveMsg.class);
                            if (saveMsg.isSuccess()) {
                                //上传成功
                            } else {
                                Util.showToast(OperationActivity.this, saveMsg.getMessages());
                            }
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 读串口线程
     */
    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                if (mInputStream != null) {
                    byte[] buffer = new byte[512];
                    int size = 0;
                    try {
                        size = mInputStream.read(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (size > 0) {
                        byte[] buffer2 = new byte[size];
                        for (int i = 0; i < size; i++) {
                            buffer2[i] = buffer[i];
                        }
                        receiveString = SerialDataUtils.ByteArrToHex(buffer2).trim();
                        Util.showLog("TAG", receiveString);
                    }
                    try {
                        //延时50ms
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 开始
     */
    private void startWork() {
        startCountDown();
        tv_start.setText(getResources().getString(R.string.activity_operation_stop));
        isWorking = true;
        send(SerialData.SET_START);
    }

    /**
     * 暂停
     */
    private void stopWork() {
        stopCountDown();
        //上传时间到服务器
        uploading();
        tv_start.setText(getResources().getString(R.string.activity_operation_start));
        isWorking = false;
        send(SerialData.SET_STOP);
    }


//    /**
//     * 界面停止
//     */
//    @Override
//    protected void onStop() {
//        super.onStop();
//        stopWork();
//    }

    /**
     * activity销毁后,释放timer,释放串口
     * 结束串口读取线程
     */
    @Override
    protected void onDestroy() {
        stopWork();
        //释放流
        if (mInputStream != null) {
            try {
                mInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mInputStream = null;
        }
        if (mOutputStream != null) {
            try {
                mOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mOutputStream = null;
        }
        //释放串口
        mSerialPort.close();
        super.onDestroy();
    }

}
