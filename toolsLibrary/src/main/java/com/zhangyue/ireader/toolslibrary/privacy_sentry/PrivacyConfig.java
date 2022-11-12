package com.zhangyue.ireader.toolslibrary.privacy_sentry;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.zhangyue.ireader.asm_annotation.sentry_privacy.AsmMethodOpcodes;
import com.zhangyue.ireader.asm_annotation.sentry_privacy.AsmMethodReplace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 隐私合规插件配置类
 */
public class PrivacyConfig {

    public static final String TIP = "请先同意隐私协议";

    public static final int OPCODE_INVOKEVIRTUAL = AsmMethodOpcodes.INVOKEVIRTUAL;
    public static final int OPCODE_INVOKESTATIC = AsmMethodOpcodes.INVOKESTATIC;

    public static final String CLASS_NAME_TELEPHONYMANAGER = "android/telephony/TelephonyManager";
    public static final String CLASS_NAME_ACTIVITYMANAGER = "android/app/ActivityManager";
    public static final String CLASS_NAME_WIFIINFO = "android/net/wifi/WifiInfo";

    public static String TAG = "PrivacyConfig";


    @SuppressLint("HardwareIds")
    String serial = Build.SERIAL;
    public static void setTAG(String tag) {
        TAG = tag;
    }


    private static boolean isAgreePrivacyDialog = false;

    public static void setAgreePrivacyDialog(boolean isAgree) {
        isAgreePrivacyDialog = isAgree;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean checkAgreePrivacy(String name) {
        if (!isAgreePrivacyDialog) {
            Log.d(TAG, "未同意隐私权限-" + name);
        }
        return isAgreePrivacyDialog;
    }

    @SuppressLint("HardwareIds")
    @AsmMethodReplace(targetMethodOpcode = OPCODE_INVOKEVIRTUAL
            , targetClass = CLASS_NAME_TELEPHONYMANAGER,hook = true)
    public static String getDeviceId(TelephonyManager telephonyManager) {
        if (!checkAgreePrivacy("getDeviceId")) {
            Log.e(TAG, TIP);
            return "";
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.i(TAG, "getImei-SDK_INT above android Q");
            return "";
        }
        return telephonyManager.getDeviceId();
    }

    @SuppressLint("HardwareIds")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @AsmMethodReplace(targetMethodOpcode = OPCODE_INVOKEVIRTUAL
            , targetClass = CLASS_NAME_TELEPHONYMANAGER,hook = true)
    public static String getDeviceId(TelephonyManager telephonyManager, int index) {
        if (!checkAgreePrivacy("getDeviceId")) {
            Log.e(TAG, TIP);
            return "";
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.i(TAG, "getImei-SDK_INT above android Q");
            return "";
        }
        return telephonyManager.getDeviceId(index);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @AsmMethodReplace(targetMethodOpcode = OPCODE_INVOKEVIRTUAL
            , targetClass = CLASS_NAME_TELEPHONYMANAGER,hook = true)
    public static String getImei(TelephonyManager telephonyManager) {
        if (!checkAgreePrivacy("getImei")) {
            Log.e(TAG, TIP);
            return "";
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.i(TAG, "getImei-SDK_INT above android Q");
            return "";
        }
        return telephonyManager.getImei();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @AsmMethodReplace(targetMethodOpcode = OPCODE_INVOKEVIRTUAL
            , targetClass = CLASS_NAME_TELEPHONYMANAGER,hook = true)
    public static String getImei(TelephonyManager telephonyManager, int index) {
        if (!checkAgreePrivacy("getImei")) {
            Log.e(TAG, TIP);
            return "";
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.i(TAG, "getImei-SDK_INT above android Q");
            return "";
        }
        return telephonyManager.getImei(index);
    }

    @SuppressLint("HardwareIds")
    @AsmMethodReplace(targetMethodOpcode = OPCODE_INVOKEVIRTUAL
            , targetClass = CLASS_NAME_TELEPHONYMANAGER,hook = true)
    public static String getSimSerialNumber(TelephonyManager telephonyManager) {
        if (!checkAgreePrivacy("getSimSerialNumber")) {
            Log.e(TAG, TIP);
            return "";
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.i(TAG, "getImei-SDK_INT above android Q");
            return "";
        }
        return telephonyManager.getSimSerialNumber();
    }

    @AsmMethodReplace(targetMethodOpcode = OPCODE_INVOKEVIRTUAL
            , targetClass = CLASS_NAME_ACTIVITYMANAGER)
    public static List<ActivityManager.RunningAppProcessInfo> getRunningAppProcesses(ActivityManager activityManager) {
        if (!checkAgreePrivacy("getRunningAppProcesses")) {
            return Collections.emptyList();
        }
        return activityManager.getRunningAppProcesses();
    }

    @AsmMethodReplace(targetMethodOpcode = OPCODE_INVOKEVIRTUAL
            , targetClass = CLASS_NAME_ACTIVITYMANAGER)
    public static List<ActivityManager.RecentTaskInfo> getRecentTasks(ActivityManager activityManager, int maxNum, int flag) {
        if (!checkAgreePrivacy("getRecentTasks")) {
            return new ArrayList<>();
        }
        return activityManager.getRecentTasks(maxNum, flag);
    }


    @AsmMethodReplace(targetMethodOpcode = OPCODE_INVOKEVIRTUAL
            , targetClass = CLASS_NAME_ACTIVITYMANAGER)
    public static List<ActivityManager.RunningTaskInfo> getRunningTasks(ActivityManager activityManager, int max) {
        if (!checkAgreePrivacy("getRunningTasks")) {
            return new ArrayList<>();
        }
        return activityManager.getRunningTasks(max);
    }


    /**
     * 读取基站信息，需要开启定位
     */
    @SuppressLint("MissingPermission")
    @AsmMethodReplace(targetMethodOpcode = OPCODE_INVOKEVIRTUAL
            , targetClass = CLASS_NAME_TELEPHONYMANAGER)
    public static List<CellInfo> getAllCellInfo(TelephonyManager telephonyManager) {
        if (!checkAgreePrivacy("getAllCellInfo")) {
            return new ArrayList<>();
        }
        return telephonyManager.getAllCellInfo();
    }


    @AsmMethodReplace(targetMethodOpcode = OPCODE_INVOKEVIRTUAL
            , targetClass = CLASS_NAME_WIFIINFO)
    public static String getSSID(WifiInfo wifiInfo) {
        if (!checkAgreePrivacy("getSSID")) {
            Log.e(TAG, TIP);
            return "";
        }
        return wifiInfo.getSSID();
    }

    @AsmMethodReplace(targetMethodOpcode = OPCODE_INVOKEVIRTUAL
            , targetClass = CLASS_NAME_WIFIINFO)
    public static String getBSSID(WifiInfo wifiInfo) {
        if (!checkAgreePrivacy("getBSSID")) {
            Log.e(TAG, TIP);
            return "";
        }
        return wifiInfo.getBSSID();
    }

    @SuppressLint("HardwareIds")
    @AsmMethodReplace(targetMethodOpcode = OPCODE_INVOKEVIRTUAL
            , targetClass = CLASS_NAME_WIFIINFO)
    public static String getMacAddress(WifiInfo wifiInfo) {
        if (!checkAgreePrivacy("getMacAddress")) {
            Log.e(TAG, TIP);
            return "";
        }
        return wifiInfo.getMacAddress();
    }

    @AsmMethodReplace(targetMethodOpcode = OPCODE_INVOKESTATIC
            , targetClass = "android/provider/Settings$System")
    public static String getString(ContentResolver resolver, String name) {
        if (!checkAgreePrivacy("getAndroidId")) {
            Log.e(TAG, TIP);
            return "";
        }
        return Settings.System.getString(resolver, name);
    }

    @AsmMethodReplace(targetMethodOpcode = OPCODE_INVOKEVIRTUAL
            , targetClass = "android/hardware/SensorManager")
    public static List<Sensor> getSensorList(SensorManager sensorManager, int type) {
        if (!checkAgreePrivacy("getSensorList")) {
            return new ArrayList<>();
        }
        return sensorManager.getSensorList(type);
    }

    @AsmMethodReplace(targetMethodOpcode = OPCODE_INVOKEVIRTUAL
            , targetClass = "android/net/wifi/WifiManager")
    public static DhcpInfo getDhcpInfo(WifiManager wifiManager) {
        if (!checkAgreePrivacy("getDhcpInfo")) {
            return null;
        }
        return wifiManager.getDhcpInfo();
    }

    @SuppressLint("MissingPermission")
    @AsmMethodReplace(targetMethodOpcode = OPCODE_INVOKEVIRTUAL
            , targetClass = "android/net/wifi/WifiManager")
    public static List<WifiConfiguration> getConfiguredNetworks(WifiManager wifiManager) {
        if (!checkAgreePrivacy("getConfiguredNetworks")) {
            return null;
        }
        return wifiManager.getConfiguredNetworks();
    }

    @SuppressLint("MissingPermission")
    @AsmMethodReplace(targetMethodOpcode = OPCODE_INVOKEVIRTUAL
            , targetClass = "android/location/LocationManager")
    public static Location getLastKnownLocation(LocationManager locationManager, String provider) {
        if (!checkAgreePrivacy("getLastKnownLocation")) {
            return null;
        }
        return locationManager.getLastKnownLocation(provider);
    }


    @SuppressLint("MissingPermission")
    @AsmMethodReplace(targetMethodOpcode = OPCODE_INVOKEVIRTUAL
            , targetClass = "android/location/LocationManager")
    public static void requestLocationUpdates(LocationManager locationManager, String provider, long minTime, float minDistance, LocationListener listener) {
        if (!checkAgreePrivacy("requestLocationUpdates")) {
            return;
        }
        locationManager.requestLocationUpdates(provider, minTime, minDistance, listener);
    }

}
