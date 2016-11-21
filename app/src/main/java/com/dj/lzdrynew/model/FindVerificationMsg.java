package com.dj.lzdrynew.model;

import java.io.Serializable;

/**
 * 找回验证码返回的消息
 */
public class FindVerificationMsg implements Serializable {
    private int data;
    private boolean success;
    private String messages;

    public void setData(int data) {
        this.data = data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public int getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessages() {
        return messages;
    }
}
