package com.qmtv.crashintercept;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        发生在 Activity 启动过程中的异常将会导致页面初始化失败，此时的错误破坏力很高
//        CrashMockUtil.mockNullPointer();

        findViewById(R.id.btn_null_pointer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrashMockUtil.mockNullPointer();
            }
        });

        findViewById(R.id.btn_out_of_memory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrashMockUtil.mockOutOfMemory();
            }
        });

        findViewById(R.id.btn_jump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Main2Activity.class));
            }
        });

        findViewById(R.id.btn_thread).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CrashMockUtil.mockNullPointer();
                    }
                }).start();
            }
        });
    }
}
