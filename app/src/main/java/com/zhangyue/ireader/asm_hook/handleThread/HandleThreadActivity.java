package com.zhangyue.ireader.asm_hook.handleThread;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.zhangyue.ireader.asm_hook.R;
import com.zhangyue.ireader.toolslibrary.Util;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
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

        //threadPoolExecutor subClass
        handleThreadPoolExecutorSubClass();

        //ScheduleThreadPoolExecutor
        handleScheduleThreadPoolExecutor();

        //ScheduleThreadPoolExecutorSubClass
        handleScheduleThreadPoolExecutorSubClass();

        handleTimer();

        handleSubTimer();

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

    private void handleScheduleThreadPoolExecutor() {
        findViewById(R.id.schedule_thread_pool_executor_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
                Log.i(TAG, "allowCoreThreadTimeout=" + executor.allowsCoreThreadTimeOut());
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                });
            }
        });

        findViewById(R.id.schedule_thread_pool_executor_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, Executors.defaultThreadFactory());
                Log.i(TAG, "allowCoreThreadTimeout=" + executor.allowsCoreThreadTimeOut());
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                });
            }
        });

        findViewById(R.id.schedule_thread_pool_executor_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

                    }
                });
                Log.i(TAG, "allowCoreThreadTimeout=" + executor.allowsCoreThreadTimeOut());
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                });
            }
        });


        findViewById(R.id.schedule_thread_pool_executor_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, Executors.defaultThreadFactory(), new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

                    }
                });
                Log.i(TAG, "allowCoreThreadTimeout=" + executor.allowsCoreThreadTimeOut());
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                });
            }
        });
    }

    private void handleScheduleThreadPoolExecutorSubClass() {
        findViewById(R.id.sub_schedule_thread_pool_executor_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduledThreadPoolExecutor executor = new TestScheduleThreadPoolExecutor(1);
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                });
            }
        });

        findViewById(R.id.sub_schedule_thread_pool_executor_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduledThreadPoolExecutor executor = new TestScheduleThreadPoolExecutor(1, Executors.defaultThreadFactory());
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                });
            }
        });


        findViewById(R.id.sub_schedule_thread_pool_executor_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduledThreadPoolExecutor executor = new TestScheduleThreadPoolExecutor(1, new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

                    }
                });
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                });
            }
        });


        findViewById(R.id.sub_schedule_thread_pool_executor_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduledThreadPoolExecutor executor = new TestScheduleThreadPoolExecutor(1, Executors.defaultThreadFactory(), new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

                    }
                });
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                });
            }
        });
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
                ThreadPoolExecutor service = (ThreadPoolExecutor) Executors.newScheduledThreadPool(1);
                Log.i(TAG, "allowCoreThreadTimeout=" + service.allowsCoreThreadTimeOut());
                service.execute(new Runnable() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                });

                ThreadPoolExecutor service2 = (ThreadPoolExecutor) Executors.newScheduledThreadPool(1, Executors.defaultThreadFactory());
                Log.i(TAG, "allowCoreThreadTimeout=" + service2.allowsCoreThreadTimeOut());
                service2.execute(new Runnable() {
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
                ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newSingleThreadScheduledExecutor();
                Log.i(TAG, "allowCoreThreadTimeout=" + executor.allowsCoreThreadTimeOut());
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                });

                ThreadPoolExecutor executor1 = (ThreadPoolExecutor) Executors.newSingleThreadScheduledExecutor(Executors.defaultThreadFactory());
                Log.i(TAG, "allowCoreThreadTimeout=" + executor1.allowsCoreThreadTimeOut());
                executor1.execute(new Runnable() {
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

    private void handleTimer() {
        findViewById(R.id.New_Timer_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                }, 0);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                timer.cancel();
            }
        });

        findViewById(R.id.New_Timer_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timer timer = new Timer(false);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                }, 0);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                timer.cancel();
            }
        });

        findViewById(R.id.New_Timer_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timer timer = new Timer("MyTimer-----");
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                }, 0);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                timer.cancel();
            }
        });

        findViewById(R.id.New_Timer_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timer timer = new Timer("MyTimer-----", false);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                }, 0);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                timer.cancel();
            }
        });
    }

    private void handleSubTimer() {
        findViewById(R.id.Sub_Timer_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timer timer = new TestTimer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                }, 0);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                timer.cancel();
            }
        });

        findViewById(R.id.Sub_Timer_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timer timer = new TestTimer(false);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                }, 0);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                timer.cancel();
            }
        });

        findViewById(R.id.Sub_Timer_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timer timer = new TestTimer("myTestTimer.11..");
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                }, 0);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                timer.cancel();
            }
        });

        findViewById(R.id.Sub_Timer_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timer timer = new TestTimer("myTestTimer.22..", false);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        printlnThreadName();
                    }
                }, 0);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                timer.cancel();
            }
        });


    }

}