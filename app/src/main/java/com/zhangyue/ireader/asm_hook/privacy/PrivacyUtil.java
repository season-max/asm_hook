package com.zhangyue.ireader.asm_hook.privacy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.util.List;

public class PrivacyUtil {

    public static String TAG = "PrivacyUtil";

    public static void setTAG(String tag) {
        TAG = tag;
    }

    public static String getImei(Activity context) {
        TelephonyManager manager = getTelephonyManager(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                context.requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
                Log.i(TAG, "getImei-has no permission");
                return "";
            }
            try {
                return manager.getImei();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String getImei(Activity context, int slotIndex) {
        TelephonyManager manager = getTelephonyManager(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                context.requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
                Log.i(TAG, "getImei-has no permission");
                return "";
            }
            try {
                return manager.getImei();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("HardwareIds")
    public static String getDeviceId(Activity context) {
        TelephonyManager manager = getTelephonyManager(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            context.requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
            Log.i(TAG, "getDeviceId-has no permission");
            return "";
        }
        try {
            return manager.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("HardwareIds")
    public static String getDeviceId(Activity context, int slotIndex) {
        TelephonyManager manager = getTelephonyManager(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            context.requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
            Log.i(TAG, "getDeviceId-has no permission");
            return "";
        }
        try {
            return manager.getDeviceId(slotIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("HardwareIds")
    public static String getSimSerialNumber(Activity context) {
        TelephonyManager manager = getTelephonyManager(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            context.requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
            Log.i(TAG, "getSimSerialNumber-has no permission");
            return "";
        }
        try {
            return manager.getSimSerialNumber();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String getAndroidId(Activity context) {
        return Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static List<ActivityManager.RunningAppProcessInfo> getRunningAppProcesses(Context context) {
        return getActivityManager(context).getRunningAppProcesses();
    }

    public static List<ActivityManager.RecentTaskInfo> getRecentTasks(Context context) {
        return getActivityManager(context).getRecentTasks(100, ActivityManager.RECENT_WITH_EXCLUDED);
    }

    public static List<ActivityManager.RunningTaskInfo> getRunningTasks(Context context) {
        return getActivityManager(context).getRunningTasks(100);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static List<CellInfo> getAllCellInfo(Activity context) {
        TelephonyManager telephonyManager = getTelephonyManager(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            context.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            return null;
        }
        return telephonyManager.getAllCellInfo();
    }


    public static List<Sensor> getSensorList(Activity context) {
        return getSensorManager(context).getSensorList(Sensor.TYPE_ALL);
    }


    private static WifiInfo getWifiInfo(Activity context) {
        ConnectivityManager connectivityManager = getConnectivityManager(context);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            WifiManager manager = getWifiManager(context);
            return manager.getConnectionInfo();
        }
        return null;
    }

    public static String getSSID(Activity context) {
        WifiInfo wifiInfo = getWifiInfo(context);
        return wifiInfo != null ? wifiInfo.getSSID() : "wifi info is null";
    }

    public static String getBSSID(Activity context) {
        WifiInfo wifiInfo = getWifiInfo(context);
        return wifiInfo != null ? wifiInfo.getBSSID() : "wifi info is null";
    }

    @SuppressLint("HardwareIds")
    public static String getMacAddress(Activity context) {
        WifiInfo wifiInfo = getWifiInfo(context);
        return wifiInfo != null ? wifiInfo.getMacAddress() : "wifi info is null";
    }

    public static DhcpInfo getDhcpInfo(Activity context) {
        return getWifiManager(context).getDhcpInfo();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static List<WifiConfiguration> getConfiguredNetworks(Activity context) {
        WifiManager wifiManager = getWifiManager(context);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            context.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            return null;
        }
        return wifiManager.getConfiguredNetworks();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void requestLocationUpdates(Activity context, LocationListener listener) {
        LocationManager locationManager = getLocationManager(context);
        List<String> providers = locationManager.getProviders(true);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            context.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return;
        }
        Log.i(TAG, "requestLocationUpdates: provider.size=" + providers.size());
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, listener);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static Location getLastKnownLocation(Activity context) {
        LocationManager locationManager = getLocationManager(context);
        List<String> providers = locationManager.getProviders(true);
        Log.i(TAG, "getLastKnownLocation: provider.size=" + providers.size());
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            context.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return null;
        }
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }


    private static TelephonyManager getTelephonyManager(Context context) {
        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    private static ActivityManager getActivityManager(Context context) {
        return (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    private static SensorManager getSensorManager(Context context) {
        return (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    private static ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private static WifiManager getWifiManager(Context context) {
        return (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    private static LocationManager getLocationManager(Context context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }
}
