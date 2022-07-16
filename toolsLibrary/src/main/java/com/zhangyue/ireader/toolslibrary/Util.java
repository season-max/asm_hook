package com.zhangyue.ireader.toolslibrary;

import android.nfc.Tag;
import android.os.Looper;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {


    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

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
        String date = today + "\r\n" + log;
        try {
            FileUtils.write(file, date, Charset.defaultCharset(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("saeson", "write to file success,data::" + date);
    }

}
