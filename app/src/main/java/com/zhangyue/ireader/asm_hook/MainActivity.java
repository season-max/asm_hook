package com.zhangyue.ireader.asm_hook;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.zhangyue.ireader.asm_hook.doubleclickcheck.DoubleClickCheckActivity;
import com.zhangyue.ireader.asm_hook.privacy.PrivacyActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.jump_privacy).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(MainActivity.this, PrivacyActivity.class));
            startActivity(intent);
        });

        findViewById(R.id.jump_double_click_check).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(MainActivity.this, DoubleClickCheckActivity.class));
            startActivity(intent);
        });
    }

}