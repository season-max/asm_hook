package com.zhangyue.ireader.asm_hook.handleThread;

import android.util.Log;

import androidx.annotation.NonNull;

public class MyThread_2 extends Thread{
    public MyThread_2(@NonNull String name) {
        super(name);
    }

    @Override
    public void run() {
        super.run();
        Log.i("HandleThreadActivity", "77777");
    }
}
