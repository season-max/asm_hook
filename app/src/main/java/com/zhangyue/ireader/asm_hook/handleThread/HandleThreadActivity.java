package com.zhangyue.ireader.asm_hook.handleThread;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.zhangyue.ireader.asm_hook.R;

public class HandleThreadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_thread);


        Runnable runnable = () -> Log.i("TAG", "11111");

        new Thread(runnable).start();

        new Thread(runnable, "thread_season").start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("TAG", "3333");
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("TAG", "444444");
            }
        },"thread-44444");

        new Thread() {
            @Override
            public void run() {
                super.run();
                Log.i("TAG", "22222");
            }
        }.start();

        new Thread("thrad-55555") {
            @Override
            public void run() {
                Log.i("TAG", "55555");
            }
        }.start();

        new MyThread("mThread_1111").start();

        new MyThread_2("mythread_222").start();
    }

    static class MyThread extends Thread{

        public MyThread(@NonNull String name) {
            super(name);
        }

        @Override
        public void run() {
            super.run();
            Log.i("TAG", "6666");
        }
    }
}