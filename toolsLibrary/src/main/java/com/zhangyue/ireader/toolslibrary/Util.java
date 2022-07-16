package com.zhangyue.ireader.toolslibrary;

import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Util {


    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final Executor threadPool = Executors.newFixedThreadPool(1);

    public static String getStackTrace(String tag) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(LINE_SEPARATOR);
        stringBuilder.append("TAG: ");
        stringBuilder.append(tag);
        stringBuilder.append(LINE_SEPARATOR);
        stringBuilder.append("Thread: ");
        stringBuilder.append(Thread.currentThread()).append(", 主线程: ").append(Looper.getMainLooper() == Looper.myLooper());
        stringBuilder.append(LINE_SEPARATOR);
        stringBuilder.append("-------------");
        stringBuilder.append(LINE_SEPARATOR);
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        for (StackTraceElement element : stackTrace) {
            stringBuilder.append(element.toString());
            stringBuilder.append(LINE_SEPARATOR);
        }

        return stringBuilder.toString();
    }


    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.CHINA);

    public static void writeToFile(String log) {
        String today = SIMPLE_DATE_FORMAT.format(new Date(System.currentTimeMillis()));
        File file = new File(ConfigGlobal.getInstance().getContext().getExternalCacheDir(), "privacy_log.txt");
        String data = today + "\r\n" + log;
        threadPool.execute(() -> {
            try {
                write(file, data, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Log.i("saeson", "write to file success,data::" + data);
    }

    private static void write(File file, String data, boolean append) throws IOException {
        try (OutputStream out = openOutputStream(file, append)) {
            if (data != null) {
                out.write(data.getBytes(StandardCharsets.UTF_8));
            }
        }
    }


    public static FileOutputStream openOutputStream(final File file, final boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canWrite()) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            final File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file, append);
    }

}
