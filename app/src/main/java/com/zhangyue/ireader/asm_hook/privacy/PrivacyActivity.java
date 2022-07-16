package com.zhangyue.ireader.asm_hook.privacy;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.hardware.Sensor;
import android.location.Location;
import android.location.LocationListener;
import android.net.DhcpInfo;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.zhangyue.ireader.asm_hook.R;
import com.zhangyue.ireader.toolslibrary.privacy_sentry.PrivacyConfig;

import java.util.List;

public class PrivacyActivity extends AppCompatActivity {

    public static final String TAG = "PrivacyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        PrivacyConfig.setTAG(TAG);

        ((CheckBox) findViewById(R.id.cbAgree)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            PrivacyConfig.setAgreePrivacyDialog(isChecked);
        });
        updateData();
    }

    private void updateData() {
        Activity context = PrivacyActivity.this;
        findViewById(R.id.btnGetRunningAppProcesses).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = PrivacyUtil.getRunningAppProcesses(context);
                Log.i(TAG, "runningAppProcesses::" + listToString(runningAppProcesses));
            }
        });

        findViewById(R.id.btnGetRecentTasks).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ActivityManager.RecentTaskInfo> recentTasks = PrivacyUtil.getRecentTasks(context);
                Log.i(TAG, "recentTasks::" + listToString(recentTasks));
            }
        });

        findViewById(R.id.btnGetRunningTasks).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ActivityManager.RunningTaskInfo> runningTasks = PrivacyUtil.getRunningTasks(context);
                Log.i(TAG, "runningTasks::" + listToString(runningTasks));
            }
        });

        findViewById(R.id.btnGetAllCellInfo).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                List<CellInfo> cellInfos = PrivacyUtil.getAllCellInfo(context);
                Log.i(TAG, "runningTasks::" + listToString(cellInfos));
            }
        });

        findViewById(R.id.btnGetDeviceId).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                String deviceId = PrivacyUtil.getDeviceId(context);
                Log.i(TAG, "deviceId::" + deviceId);
            }
        });

        findViewById(R.id.getImei).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imei = PrivacyUtil.getImei(context);
                Log.i(TAG, "imei::" + imei);
            }
        });

        findViewById(R.id.getSimSerialNumber).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                String simSerialNumber = PrivacyUtil.getSimSerialNumber(context);
                Log.i(TAG, "simSerialNumber::" + simSerialNumber);
            }
        });

        findViewById(R.id.btnGetIdAndroid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String androidId = PrivacyUtil.getAndroidId(context);
                Log.i(TAG, "androidId::" + androidId);
            }
        });


        findViewById(R.id.getSSID).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ssid = PrivacyUtil.getSSID(context);
                Log.i(TAG, "ssid::" + ssid);
            }
        });

        findViewById(R.id.getBSSID).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bSsid = PrivacyUtil.getBSSID(context);
                Log.i(TAG, "bSsid::" + bSsid);
            }
        });

        findViewById(R.id.getMacAddress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String macAddress = PrivacyUtil.getMacAddress(context);
                Log.i(TAG, "macAddress::" + macAddress);
            }
        });


        findViewById(R.id.getConfiguredNetworks).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                List<WifiConfiguration> configuredNetworks = PrivacyUtil.getConfiguredNetworks(context);
                Log.i(TAG, "configuredNetworks::" + listToString(configuredNetworks));
            }
        });

        findViewById(R.id.getSensorList).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                List<Sensor> sensorList = PrivacyUtil.getSensorList(context);
                Log.i(TAG, "sensorList::" + listToString(sensorList));
            }
        });

        findViewById(R.id.getDhcpInfo).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                DhcpInfo dhcpInfo = PrivacyUtil.getDhcpInfo(context);
                Log.i(TAG, "dhcpInfo::" + objectToString(dhcpInfo));
            }
        });

        findViewById(R.id.getLastKnownLocation).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Location lastKnownLocation = PrivacyUtil.getLastKnownLocation(context);
                Log.i(TAG, "lastKnownLocation::" + objectToString(lastKnownLocation));
            }
        });

        findViewById(R.id.requestLocationUpdates).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                LocationListener listener = new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        Log.i(TAG, "onLocationChanged::" + objectToString(location));
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        Log.i(TAG, "onProviderEnabled::" + provider);
                    }
                };
                PrivacyUtil.requestLocationUpdates(context, listener);
            }
        });
    }

    private <T> String listToString(List<T> list) {
        if (list != null) {
            return list.toString();
        }
        return "null";
    }

    private <T> String objectToString(T t) {
        return t != null ? t.toString() : "null";
    }

}