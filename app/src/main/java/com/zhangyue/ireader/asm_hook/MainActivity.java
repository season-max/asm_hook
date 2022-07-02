package com.zhangyue.ireader.asm_hook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

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
    }
}