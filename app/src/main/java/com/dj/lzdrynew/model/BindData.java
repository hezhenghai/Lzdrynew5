package com.dj.lzdrynew.model;

import java.io.Serializable;

/**
 * 绑定到服务器返回的数据
 */

public class BindData implements Serializable {
    private String messages;
    private boolean success;
    private userData data;

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public userData getData() {
        return data;
    }

    public void setData(userData data) {
        this.data = data;
    }
}
