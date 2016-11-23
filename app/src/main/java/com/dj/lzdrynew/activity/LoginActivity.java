package com.dj.lzdrynew.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.dj.lzdrynew.R;
import com.dj.lzdrynew.model.BindData;
import com.dj.lzdrynew.model.LoginMsg;
import com.dj.lzdrynew.model.MyConfig;
import com.dj.lzdrynew.model.userData;
import com.dj.lzdrynew.utils.MD5;
import com.dj.lzdrynew.utils.NetWorkUtils;
import com.dj.lzdrynew.utils.Util;
import com.dj.lzdrynew.views.LoadProgressDialog;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.tencent.bugly.beta.Beta;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.w3c.dom.Text;

import okhttp3.Call;

/**
 * 登录界面
 */
public class LoginActivity extends BaseActivity {

    //定位服务
    private LocationClient mLocationClient = null;

    //地址
    private String address = "";

    //cupid
    private String cpuid = "";


    //布局组件
    private RelativeLayout relativeLayout_login;
    private LinearLayout ll_language_choice, ll_booking, ll_username_error, ll_password_error;
    private TextView tv_username_error, tv_password_error;
    private EditText et_user_name, et_password;
    private Button bt_login;


    //重写父类方法，连网成功后定位，绑定设备
    @Override
    public void wifiSuccess() {
        super.wifiSuccess();
        // 定位
        myLocation();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /**
         * 修改版本升级对话框中英文
         */
        setUpdataLanguage();


        cpuid = Util.getCpuId();

        // 初始化控件
        setViews();

        // 设置监听事件
        setListeners();

    }


    /**
     * 初始化控件
     */
    private void setViews() {
        //初始化wifi按钮
        initWifiSet();
        relativeLayout_login = (RelativeLayout) findViewById(R.id.relativeLayout_login);
        et_user_name = (EditText) findViewById(R.id.et_user_name);
        et_password = (EditText) findViewById(R.id.et_password);
        bt_login = (Button) findViewById(R.id.bt_login);
        ll_language_choice = (LinearLayout) findViewById(R.id.ll_language_choice);
        ll_booking = (LinearLayout) findViewById(R.id.ll_booking);
        ll_username_error = (LinearLayout) findViewById(R.id.ll_username_error);
        ll_password_error = (LinearLayout) findViewById(R.id.ll_password_error);
        tv_username_error = (TextView) findViewById(R.id.tv_username_error);
        tv_password_error = (TextView) findViewById(R.id.tv_password_error);
    }

    /**
     * 设置监听事件
     */
    private void setListeners() {
        //设置edittext点击 hint 消失
        et_user_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText textView = (EditText) v;
                String hint;
                if (hasFocus) {
                    hint = textView.getHint().toString();
                    textView.setTag(hint);
                    textView.setHint("");
                    ll_username_error.setVisibility(View.GONE);
                } else {
                    hint = textView.getTag().toString();
                    textView.setHint(hint);
                }
            }
        });
        et_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText textView = (EditText) v;
                String hint;
                if (hasFocus) {
                    hint = textView.getHint().toString();
                    textView.setTag(hint);
                    textView.setHint("");
                    ll_password_error.setVisibility(View.GONE);
                } else {
                    hint = textView.getTag().toString();
                    textView.setHint(hint);
                }
            }
        });

        et_user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(music, 1, 1, 0, 0, 1);
                ll_username_error.setVisibility(View.GONE);
            }
        });

        et_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(music, 1, 1, 0, 0, 1);
                ll_password_error.setVisibility(View.GONE);
            }
        });

        /** 外层的布局控件 */
        relativeLayout_login.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                relativeLayout_login.setFocusable(true);
                relativeLayout_login.setFocusableInTouchMode(true);
                relativeLayout_login.requestFocus();
                // 隐藏输入法
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(relativeLayout_login.getWindowToken(), 0);
                return false;
            }
        });

        /**
         * 登录
         */
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(music, 1, 1, 0, 0, 1);
                //非空验证
                if (TextUtils.isEmpty(et_user_name.getText().toString().trim())) {
                    //获取焦点，显示信息
                    ll_username_error.setVisibility(View.VISIBLE);
                    tv_username_error.setText(getResources().getString(R.string.login_activity_user_name_cannot_be_empty));
                    et_user_name.setText("");
                    return;
                }

                if (TextUtils.isEmpty(et_password.getText().toString().trim())) {
                    ll_password_error.setVisibility(View.VISIBLE);
                    tv_password_error.setText(getResources().getString(R.string.login_activity_password_cannot_be_empty));
                    et_password.setText("");
                    return;
                }
                login();

            }
        });
        ll_language_choice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ll_language_choice.setBackgroundResource(R.drawable.btn_bg_nor_2);
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ll_language_choice.setBackgroundResource(R.drawable.btn_bg_sel_2);
                }
                return false;
            }
        });
        /**
         * 进入语言选择界面
         */
        ll_language_choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(music, 1, 1, 0, 0, 1);
                // 清空语言保存
                SharedPreferences sharedPreferences = getSharedPreferences(LanguageChoiceActivity.LANGUAGE, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(LanguageChoiceActivity.LANGUAGE, null);
                editor.apply();
                Intent intent = new Intent(LoginActivity.this, LanguageChoiceActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ll_booking.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ll_booking.setBackgroundResource(R.drawable.btn_bg_nor_2);
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ll_booking.setBackgroundResource(R.drawable.btn_bg_sel_2);
                }
                return false;
            }
        });
        /**
         * 进入客户预约界面
         */
        ll_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(music, 1, 1, 0, 0, 1);
                Util.showToast(LoginActivity.this, getResources().getString(R.string.this_function_to_develop));
//                Intent intent = new Intent(LoginActivity.this, BookingActivity.class);
//                startActivity(intent);
//                finish();
            }
        });
    }

    /**
     * 登录到网络
     */
    public void login() {
        if (!NetWorkUtils.isNetworkConnected(LoginActivity.this)) {
            showWifiSetDialog();
            return;
        }
        LoadProgressDialog.Builder builder = new LoadProgressDialog.Builder(LoginActivity.this);
        final LoadProgressDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);// 点击外部区域不关闭
        dialog.show();
        // 登录
        OkHttpUtils
                .post()
                .url(MyConfig.loginUrl)
                .addParams("username", et_user_name.getText().toString().trim())
                .addParams("password", MD5.getMD5(et_password.getText().toString().trim()))
                .build()
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .connTimeOut(10000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, final Exception e, int i) {
                        Util.showLog("TAG", "login-----onError------" + e);
                        Util.showToast(LoginActivity.this, getResources().getString(R.string.login_activity_server_exception));
                        //登录失败
                        dialog.dismiss();
                    }

                    @Override
                    public void onResponse(final String s, int i) {
                        Util.showLog("TAG", "login-----onResponse------" + s);
                        // 登录成功
                        LoginMsg loginMsg = new Gson().fromJson(s, LoginMsg.class);
                        // 验证通过
                        if (loginMsg.isSuccess()) {
                            // 跳转到核查码页面
                            Intent intent = new Intent(LoginActivity.this, VerificationCodeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            if (loginMsg.getMessages().equals("该用户名不存在")) {
                                tv_username_error.setText(getResources().getString(R.string.login_activity_user_name_does_not_exist));
                                ll_username_error.setVisibility(View.VISIBLE);
                            }
                            if (loginMsg.getMessages().equals("密码错误")) {
                                tv_password_error.setText(getResources().getString(R.string.login_activity_password_does_not_exist));
                                ll_password_error.setVisibility(View.VISIBLE);
                            }

                        }
                        dialog.dismiss();
                    }
                });
    }


    /**
     * 定位
     */
    private void myLocation() {
        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        //注册监听函数
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                double longitude = bdLocation.getLongitude();
                double latitude = bdLocation.getLatitude();
                address = bdLocation.getAddrStr();
                if (latitude != 0 && longitude != 0 && address != null) {
                    mLocationClient.stop();
                    //定位成功，上传数据到服务器
                    String longi = String.valueOf(longitude);
                    String lati = String.valueOf(latitude);
                    Util.showLog("TAG", "---------------" + longi + "---------" + lati + "---------" + address);
                    bindDevice(longi, lati, address);
                }
            }
        });
        initLocation();
        mLocationClient.start();
    }

    /**
     * 定位设置
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(false);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(false);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    /**
     * 发送网络请求，绑定设备
     */
    private synchronized void bindDevice(String longitude, String latitude, String address) {
        Util.showLog("TAG", "uuid : " + cpuid + "\nlongitude : " + longitude + "\nlatiude : " + latitude + "\ncode : " + MyConfig.CODE + "\npId : " + null + "\naddress : " + address);
        OkHttpUtils
                .get()
                .url(MyConfig.bindUrl)
                .addParams("uuid", cpuid)
                .addParams("longitude", longitude)
                .addParams("latitude", latitude)
                .addParams("code", MyConfig.CODE)
                .addParams("pId", null)
                .addParams("address", address)
                .build()
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, final Exception e, int i) {

                    }

                    @Override
                    public void onResponse(final String s, int i) {
                        Util.showLog("TAG", "bindDevice--------- " + s);
                        //登录成功
                        try {
                            BindData bindData = new Gson().fromJson(s, BindData.class);
                            if (bindData.isSuccess()) {
//                                userData data = bindData.getData();
                            }
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 修改版本升级对话框中英文
     */
    private void setUpdataLanguage() {
        Beta.strToastYourAreTheLatestVersion = getResources().getString(R.string.strToastYourAreTheLatestVersion);
        Beta.strToastCheckUpgradeError = getResources().getString(R.string.strToastCheckUpgradeError);
        Beta.strToastCheckingUpgrade = getResources().getString(R.string.strToastCheckingUpgrade);
        Beta.strNotificationDownloading = getResources().getString(R.string.strNotificationDownloading);
        Beta.strNotificationClickToView = getResources().getString(R.string.strNotificationClickToView);
        Beta.strNotificationClickToInstall = getResources().getString(R.string.strNotificationClickToInstall);
        Beta.strNotificationClickToRetry = getResources().getString(R.string.strNotificationClickToRetry);
        Beta.strNotificationDownloadSucc = getResources().getString(R.string.strNotificationDownloadSucc);
        Beta.strNotificationDownloadError = getResources().getString(R.string.strNotificationDownloadError);
        Beta.strNotificationHaveNewVersion = getResources().getString(R.string.strNotificationHaveNewVersion);
        Beta.strNetworkTipsMessage = getResources().getString(R.string.strNetworkTipsMessage);
        Beta.strNetworkTipsTitle = getResources().getString(R.string.strNetworkTipsTitle);
        Beta.strNetworkTipsConfirmBtn = getResources().getString(R.string.strNetworkTipsConfirmBtn);
        Beta.strNetworkTipsCancelBtn = getResources().getString(R.string.strNetworkTipsCancelBtn);
        Beta.strUpgradeDialogVersionLabel = getResources().getString(R.string.strUpgradeDialogVersionLabel);
        Beta.strUpgradeDialogFileSizeLabel = getResources().getString(R.string.strUpgradeDialogFileSizeLabel);
        Beta.strUpgradeDialogUpdateTimeLabel = getResources().getString(R.string.strUpgradeDialogUpdateTimeLabel);
        Beta.strUpgradeDialogFeatureLabel = getResources().getString(R.string.strUpgradeDialogFeatureLabel);
        Beta.strUpgradeDialogUpgradeBtn = getResources().getString(R.string.strUpgradeDialogUpgradeBtn);
        Beta.strUpgradeDialogInstallBtn = getResources().getString(R.string.strUpgradeDialogInstallBtn);
        Beta.strUpgradeDialogRetryBtn = getResources().getString(R.string.strUpgradeDialogRetryBtn);
        Beta.strUpgradeDialogContinueBtn = getResources().getString(R.string.strUpgradeDialogContinueBtn);
        Beta.strUpgradeDialogCancelBtn = getResources().getString(R.string.strUpgradeDialogCancelBtn);
    }


}
