package com.dj.lzdrynew.model;

import java.io.Serializable;

/**
 * 验证码返回信息
 */
public class VerificationMsg implements Serializable {
    private VerificationData data;
    private boolean success;
    private String messages;

    public VerificationData getData() {
        return data;
    }

    public void setData(VerificationData data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }
}
