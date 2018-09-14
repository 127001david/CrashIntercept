package com.qmtv.crashintercept;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Main3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        findViewById(R.id.btn_do_sth_bad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrashMockUtil.mockNullPointer();
            }
        });

        findViewById(R.id.btn_do_sth_damn_it).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrashMockUtil.mockNullPointer();
            }
        });
    }
}
