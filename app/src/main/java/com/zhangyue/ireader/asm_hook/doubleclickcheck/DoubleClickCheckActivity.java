package com.zhangyue.ireader.asm_hook.doubleclickcheck;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.zhangyue.ireader.asm_annotation.double_click.CheckDoubleClick;
import com.zhangyue.ireader.asm_hook.R;
import com.zhangyue.ireader.toolslibrary.doubleclick.DoubleClickConfig;

public class DoubleClickCheckActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_double_click_check);

        findViewById(R.id.double_click).setOnClickListener(new View.OnClickListener() {
            @Override
            @CheckDoubleClick(checkClick = 800)
            public void onClick(View v) {
                Log.i(DoubleClickConfig.TAG, "111");
            }
        });

        //lambda 表达式
        findViewById(R.id.double_click_lambda).setOnClickListener(v -> Log.i(DoubleClickConfig.TAG, "2222"));
    }

}