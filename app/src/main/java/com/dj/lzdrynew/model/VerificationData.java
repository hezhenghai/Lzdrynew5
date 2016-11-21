package com.dj.lzdrynew.model;

import java.io.Serializable;

/**
 * 验证码数据
 */

public class VerificationData implements Serializable {
    //    "data":{"verification":"626698","clientid":1,"customername":"www","remaintime":3568000,"sex":1,"age":1,"devicecode":"PBE4-1"},
    private String verification;
    private int clientid;
    private String customername;
    private int remaintime;
    private int sex;
    private int age;
    private String devicecode;

    public String getVerification() {
        return verification;
    }

    public void setVerification(String verification) {
        this.verification = verification;
    }

    public int getClientid() {
        return clientid;
    }

    public void setClientid(int clientid) {
        this.clientid = clientid;
    }

    public String getCustomername() {
        return customername;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public int getRemaintime() {
        return remaintime;
    }

    public void setRemaintime(int remaintime) {
        this.remaintime = remaintime;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDevicecode() {
        return devicecode;
    }

    public void setDevicecode(String devicecode) {
        this.devicecode = devicecode;
    }
}
