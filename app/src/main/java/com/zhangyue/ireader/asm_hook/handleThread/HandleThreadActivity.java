package com.zhangyue.ireader.asm_hook.handleThread;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.zhangyue.ireader.asm_hook.R;
import com.zhangyue.ireader.toolslibrary.Util;

import java.lang.reflect.Field;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HandleThreadActivity extends AppCompatActivity {

    public static final String TAG = "HandleThreadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_thread);

        Log.i(TAG, "alive thread count = " + Thread.activeCount());

        //Executors
        handleExecutors();

        //threadPoolExecutor
        handleThreadPoolExecutor();

        //handle threadPoolExecutor subClass
        handleThreadPoolExecutorSubClass();

        //线程
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "1111 -> " + Thread.currentThread().getName());
            }
        };

        new Thread(runnable).start();

        new Thread(runnable, "thread_season").start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "3333 -> " + Thread.currentThread().getName());
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "444444 ->" + Thread.currentThread().getName());
            }
        }, "thread-44444").start();

        new Thread() {
            @Override
            public void run() {
                super.run();
                Log.i(TAG, "22222 -> :" + Thread.currentThread().getName());
            }
        }.start();

        new Thread("thrad-55555") {
            @Override
            public void run() {
                Log.i(TAG, "55555 -> :" + Thread.currentThread().getName());
            }
        }.start();

        new MyThread_2("mythread_222").start();
    }

    private void printlnThreadName() {
        Log.i(TAG, "threadName=" + Thread.currentThread().getName());
    }


    private void handleExecutors() {
        //线程池
        findViewById(R.id.cache).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadPoolExecutor t = (ThreadPoolExecutor) Executors.newCachedThreadPool();
                Log.i(TAG, "allowCoreThreadTimeout=" + t.allowsCoreThreadTimeOut());
                t.execute(new Runnable() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                });

                ThreadPoolExecutor t1 = (ThreadPoolExecutor) Executors.newCachedThreadPool(Executors.defaultThreadFactory());
                Log.i(TAG, "allowCoreThreadTimeout=" + t1.allowsCoreThreadTimeOut());
                t1.execute(new Runnable() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                });
            }
        });

        findViewById(R.id.fixed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ThreadPoolExecutor t = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
                Log.i(TAG, "allowCoreThreadTimeout=" + t.allowsCoreThreadTimeOut());
                t.execute(new Runnable() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                });

                ThreadPoolExecutor t1 = (ThreadPoolExecutor) Executors.newFixedThreadPool(1, Executors.defaultThreadFactory());
                Log.i(TAG, "allowCoreThreadTimeout=" + t1.allowsCoreThreadTimeOut());
                t1.execute(new Runnable() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                });
            }
        });

        findViewById(R.id.single).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object object = Executors.newSingleThreadExecutor();
                Field field = Util.getField(object, "e");
                if (field != null) {
                    try {
                        ThreadPoolExecutor t = (ThreadPoolExecutor) field.get(object);
                        Log.i(TAG, "allowCoreThreadTimeout=" + t.allowsCoreThreadTimeOut());
                        t.execute(new Runnable() {
                            @Override
                            public void run() {
                                printlnThreadName();
                            }
                        });

                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            //Executors.newSingleThreadExecutor(threadFactory) 是类似的，就不重复写了
        });


        findViewById(R.id.Scheduled).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Executors.newScheduledThreadPool(1).execute(new Runnable() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                });

                Executors.newScheduledThreadPool(1, Executors.defaultThreadFactory()).execute(new Runnable() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                });
            }
        });

        findViewById(R.id.single_scheduled).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                });

                Executors.newSingleThreadScheduledExecutor(Executors.defaultThreadFactory()).execute(new Runnable() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                });
            }
        });
    }

    private void handleThreadPoolExecutor() {
        findViewById(R.id.thread_pool_executor_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadPoolExecutor t1 = new ThreadPoolExecutor(1, 1, 30, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
                Log.i(TAG, "allowCoreThreadTimeout=" + t1.allowsCoreThreadTimeOut());
                t1.execute(new Runnable() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                });
            }
        });


        findViewById(R.id.thread_pool_executor_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadPoolExecutor t1 = new ThreadPoolExecutor(1, 1, 30, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory());
                Log.i(TAG, "allowCoreThreadTimeout=" + t1.allowsCoreThreadTimeOut());
                t1.execute(new Runnable() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                });
            }
        });


        findViewById(R.id.thread_pool_executor_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadPoolExecutor t1 = new ThreadPoolExecutor(1, 1, 30, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), (r, executor) -> {

                });
                Log.i(TAG, "allowCoreThreadTimeout=" + t1.allowsCoreThreadTimeOut());
                t1.execute(new Runnable() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                });
            }
        });


        findViewById(R.id.thread_pool_executor_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadPoolExecutor t1 = new ThreadPoolExecutor(1, 1, 30, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory(), (r, executor) -> {

                });
                Log.i(TAG, "allowCoreThreadTimeout=" + t1.allowsCoreThreadTimeOut());
                t1.execute(new Runnable() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                });
            }
        });
    }

    private void handleThreadPoolExecutorSubClass() {
        findViewById(R.id.sub_thread_pool_executor_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadPoolExecutor service = new TestThreadPoolExecutor(1, 1, 30, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
                service.execute(new Runnable() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                });
            }
        });


        findViewById(R.id.sub_thread_pool_executor_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadPoolExecutor service = new TestThreadPoolExecutor(1, 1, 30, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory());
                service.execute(new Runnable() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                });
            }
        });


        findViewById(R.id.sub_thread_pool_executor_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadPoolExecutor service = new TestThreadPoolExecutor(1, 1, 30, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), (r, executor) -> {

                });
                service.execute(new Runnable() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                });
            }
        });


        findViewById(R.id.sub_thread_pool_executor_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadPoolExecutor service = new TestThreadPoolExecutor(1, 1, 30, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory(), (r, executor) -> {

                });
                service.execute(new Runnable() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                });
            }
        });

    }

}