package com.dj.lzdrynew.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.dj.lzdrynew.R;

/**
 * 客户预约界面
 */

public class BookingActivity extends BaseActivity {

    /**
     * 返回
     */
    private ImageButton ib_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        // 初始化控件
        setViews();
        // 设置监听
        setListeners();
    }

    /**
     * 设置监听
     */
    private void setListeners() {
        // 返回到登录界面
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(music, 1, 1, 0, 0, 1);
                Intent intent = new Intent(BookingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    /**
     * 初始化控件
     */
    private void setViews() {
        //初始化wifi按钮
        initWifiSet();
        ib_back = (ImageButton) findViewById(R.id.ib_back);
    }

}
