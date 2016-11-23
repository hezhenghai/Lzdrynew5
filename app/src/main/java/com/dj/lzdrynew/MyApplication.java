package com.dj.lzdrynew;

import android.app.Application;
import android.os.Environment;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

/**
 * Created by Administrator
 * on 2016/8/22.
 */
public class MyApplication extends Application {
    public static final String APP_ID = "900059168"; // TODO 替换成bugly上注册的appid
//    public static final String APP_CHANNEL = "DEBUG"; // TODO 自定义渠道
//    private static final String TAG = "OnUILifecycleListener";

//    /*自定义文案，修改后立即生效，默认控件文案如下*/
//    public static String strToastYourAreTheLatestVersion = "你已经是最新版了";
//    public static String strToastCheckUpgradeError = "检查新版本失败，请稍后重试";
//    public static String strToastCheckingUpgrade = "正在检查，请稍候...";
//    public static String strNotificationDownloading = "正在下载";
//    public static String strNotificationClickToView = "点击查看";
//    public static String strNotificationClickToInstall = "点击安装";
//    public static String strNotificationClickToRetry = "点击重试";
//    public static String strNotificationDownloadSucc = "下载完成";
//    public static String strNotificationDownloadError = "下载失败";
//    public static String strNotificationHaveNewVersion = "有新版本";
//    public static String strNetworkTipsMessage = "你已切换到移动网络，是否继续当前下载？";
//    public static String strNetworkTipsTitle = "网络提示";
//    public static String strNetworkTipsConfirmBtn = "继续下载";
//    public static String strNetworkTipsCancelBtn = "取消";
//    public static String strUpgradeDialogVersionLabel = "版本";
//    public static String strUpgradeDialogFileSizeLabel = "包大小";
//    public static String strUpgradeDialogUpdateTimeLabel = "更新时间";
//    public static String strUpgradeDialogFeatureLabel = "更新说明";
//    public static String strUpgradeDialogUpgradeBtn = "立即更新";
//    public static String strUpgradeDialogInstallBtn = "安装";
//    public static String strUpgradeDialogRetryBtn = "重试";
//    public static String strUpgradeDialogContinueBtn = "继续";
//    public static String strUpgradeDialogCancelBtn = "下次再说";

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * 修改版本升级对话框中英文
         */
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


        /**** Beta高级设置*****/
        /**
         * true表示app启动自动初始化升级模块；
         * false不好自动初始化
         * 开发者如果担心sdk初始化影响app启动速度，可以设置为false
         * 在后面某个时刻手动调用
         */
//        Beta.autoInit = true;

        /**
         * true表示初始化时自动检查升级
         * false表示不会自动检查升级，需要手动调用Beta.checkUpgrade()方法
         */
        Beta.autoCheckUpgrade = false;

        /**
         * 设置升级周期为60s（默认检查周期为0s），60s内SDK不重复向后天请求策略
         */
//        Beta.initDelay = 60 * 1000;

        /**
         * 设置通知栏大图标，largeIconId为项目中的图片资源；
         */
//        Beta.largeIconId = R.mipmap.ic_launcher;

        /**
         * 设置状态栏小图标，smallIconId为项目中的图片资源id;
         */
//        Beta.smallIconId = R.mipmap.ic_launcher;


        /**
         * 设置更新弹窗默认展示的banner，defaultBannerId为项目中的图片资源Id;
         * 当后台配置的banner拉取失败时显示此banner，默认不设置则展示“loading“;
         */
//        Beta.defaultBannerId = R.mipmap.ic_launcher;

        /**
         * 设置sd卡的Download为更新资源保存目录;
         * 后续更新资源会保存在此目录，需要在manifest中添加WRITE_EXTERNAL_STORAGE权限;
         */
//        Beta.storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        /**
         * 点击过确认的弹窗在APP下次启动自动检查更新时会再次显示;
         */
//        Beta.showInterruptedStrategy = true;

        /**
         * 只允许在MainActivity上显示更新弹窗，其他activity上不显示弹窗;
         * 不设置会默认所有activity都可以显示弹窗;
         */
//        Beta.canShowUpgradeActs.add(MainActivity.class);


        /**
         *  设置自定义升级对话框UI布局
         *  注意：因为要保持接口统一，需要用户在指定控件按照以下方式设置tag，否则会影响您的正常使用：
         *  标题：beta_title，如：android:tag="beta_title"
         *  升级信息：beta_upgrade_info  如： android:tag="beta_upgrade_info"
         *  更新属性：beta_upgrade_feature 如： android:tag="beta_upgrade_feature"
         *  取消按钮：beta_cancel_button 如：android:tag="beta_cancel_button"
         *  确定按钮：beta_confirm_button 如：android:tag="beta_confirm_button"
         *  详见layout/dialog_upgrade.xml
         */
        Beta.upgradeDialogLayoutId = R.layout.dialog_upgrade;

        /**
         * 设置自定义tip弹窗UI布局
         * 注意：因为要保持接口统一，需要用户在指定控件按照以下方式设置tag，否则会影响您的正常使用：
         *  标题：beta_title，如：android:tag="beta_title"
         *  提示信息：beta_tip_message 如： android:tag="beta_tip_message"
         *  取消按钮：beta_cancel_button 如：android:tag="beta_cancel_button"
         *  确定按钮：beta_confirm_button 如：android:tag="beta_confirm_button"
         *  详见layout/tips_dialog.xml
         */
//        Beta.tipsDialogLayoutId = R.layout.tips_dialog;

        /**
         *  如果想监听升级对话框的生命周期事件，可以通过设置OnUILifecycleListener接口
         *  回调参数解释：
         *  context - 当前弹窗上下文对象
         *  view - 升级对话框的根布局视图，可通过这个对象查找指定view控件
         *  upgradeInfo - 升级信息
         */
//        Beta.upgradeDialogLifecycleListener = new UILifecycleListener<UpgradeInfo>() {
//            @Override
//            public void onCreate(Context context, View view, UpgradeInfo upgradeInfo) {
//                Log.d(TAG, "onCreate");
//                // 注：可通过这个回调方式获取布局的控件，如果设置了id，可通过findViewById方式获取，如果设置了tag，可以通过findViewWithTag，具体参考下面例子:
//
//                // 通过id方式获取控件，并更改imageview图片
//                ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
//                imageView.setImageResource(R.mipmap.ic_launcher);
//
//                // 通过tag方式获取控件，并更改布局内容
//                TextView textView = (TextView) view.findViewWithTag("textview");
//                textView.setText("my custom text");
//
//                // 更多的操作：比如设置控件的点击事件
//                imageView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(getApplicationContext(), OtherActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                    }
//                });
//            }
//
//            @Override
//            public void onStart(Context context, View view, UpgradeInfo upgradeInfo) {
//                Log.d(TAG, "onStart");
//            }
//
//            @Override
//            public void onResume(Context context, View view, UpgradeInfo upgradeInfo) {
//                Log.d(TAG, "onResume");
//            }
//
//            @Override
//            public void onPause(Context context, View view, UpgradeInfo upgradeInfo) {
//                Log.d(TAG, "onPause");
//            }
//
//            @Override
//            public void onStop(Context context, View view, UpgradeInfo upgradeInfo) {
//                Log.d(TAG, "onStop");
//            }
//
//            @Override
//            public void onDestroy(Context context, View view, UpgradeInfo upgradeInfo) {
//                Log.d(TAG, "onDestory");
//            }
//        };


        /**
         * 已经接入Bugly用户改用上面的初始化方法,不影响原有的crash上报功能;
         * init方法会自动检测更新，不需要再手动调用Beta.checkUpdate(),如需增加自动检查时机可以使用Beta.checkUpdate(false,false);
         * 参数1： applicationContext
         * 参数2：appId
         * 参数3：是否开启debug
         */
        Bugly.init(getApplicationContext(), APP_ID, false);

        /**
         * 如果想自定义策略，按照如下方式设置
         */

        /***** Bugly高级设置 *****/
        //        BuglyStrategy strategy = new BuglyStrategy();
        /**
         * 设置app渠道号
         */
        //        strategy.setAppChannel(APP_CHANNEL);

        //        Bugly.init(getApplicationContext(), APP_ID, true, strategy);

    }
}
