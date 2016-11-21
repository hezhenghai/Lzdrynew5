package com.dj.lzdrynew.model;

/**
 * 配置文件
 */
public class MyConfig {

    /**
     * 设备编号
     */
    public static final String CODE = "DBQ1-2";

    /**
     * 服务器接口
     */
    public static final String URL = "http://120.24.251.29";

    /**
     * 绑定设备
     */
    public static final String bindUrl = MyConfig.URL + "/Power/bindDevice";

    /**
     * 登录
     */
    public static final String loginUrl = MyConfig.URL + "/Facility/login";
    /**
     * 验证码
     */
    public static final String verificationUrl = MyConfig.URL + "/Facility/verification";

    /**
     * 上传剩余时间
     */
    public static final String RemainingTimeUrl = MyConfig.URL + "/Facility/RemainingTime";
    /**
     * 剩余时间
     */
    public static final String REMAINING_TIME = "remaining_time";
    /**
     * 客户id
     */
    public static final String CLIENT_ID = "clientid";
    /**
     * 客户验证码
     */
    public static final String VERIFICATION = "verification";
    /**
     * 系统音量
     */
    public static final String SYSTEM_VOLUME = "system_volume";
    /**
     * 系统默认音量
     */
    public static final int DEFAULT_VOLUME = 80;
    /**
     * 系统亮度
     */
    public static final String SYSTEM_BRIGHTNESS = "system_brightness";
    /**
     * 系统默认亮度
     */
    public static final int DEFAULT_BRIGHTNESS = 80;


}
