package com.example.switchwifisender;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.net.SocketException;
import java.sql.Time;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CheckWiFiSSID_State extends BroadcastReceiver {

    private final String LOG_TAG = CheckWiFiSSID_State.class.getSimpleName();
    private String CurrentSSID = null;
    private String shipSSID = "\"20380\"";
    private WifiManager wifiManager;
    private WifiInfo wifiInfo;
    private PacketSended packetSender = new PacketSended();
    private boolean Wifi_Status = true;
    private PacketSended packetSendedStatus;
    protected WifiManager.WifiLock wifiLock;
    protected PowerManager.WakeLock wakeLock;
    protected PowerManager powerManager;
    protected Context context4Locker;
    static AtomicInteger wifistatus = new AtomicInteger(1);


    public CheckWiFiSSID_State() throws SocketException {
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(LOG_TAG, "RunReceiverAndSendPackeg onReceive started");
        context4Locker = context;


        String action = intent.getAction();
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            int netType = intent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, -1);
            if (ConnectivityManager.TYPE_WIFI == netType) {
                NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);


                if (networkInfo.isConnected()) {
                    Wifi_Status = networkInfo.isConnected();
                    packetSendedStatus = new PacketSended(Wifi_Status);
                    wifistatus.set(1);
                    Log.d(LOG_TAG, "START if (networkInfo != null && CurrentSSID != <unknown ssid> && networkInfo.isConnected())");
                    wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    wifiInfo = wifiManager.getConnectionInfo();

                    locker(context, wifiManager);

                    if (wifiLock.isHeld() && wakeLock.isHeld()) {
                        Log.d(LOG_TAG, "WiFi and PowerMNG locked");
                    }

                    Log.d(LOG_TAG, wifiInfo.toString());
                    CurrentSSID = wifiInfo.getSSID();
                    RunThread thread = new RunThread();
                    thread.start();

                } else {
                    Log.d(LOG_TAG, "No WiFi!!!");
                    Wifi_Status = networkInfo.isConnected();
                    packetSendedStatus = new PacketSended(Wifi_Status);
                    wifistatus.set(0);
                    try {
                        if (!wifiLock.isHeld() && wakeLock.isHeld()) {
                            Log.d(LOG_TAG, "WiFi and power lock isn't helded, trying");
                            locker(context, wifiManager);
                            Log.d(LOG_TAG, "WiFi and power locked");
                        }
                    } catch (Exception e) {
                        Log.d(LOG_TAG, "No WiFi connection");
                    }

                    RunThread thread = new RunThread();
                    thread.start();
                }

            }

        }
        Log.d(LOG_TAG, "RunReceiverAndSendPackage onReceive finish");
    }


    void locker(Context context, WifiManager wifiManager) {
        wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, getClass().getSimpleName());
        wifiLock.acquire();
        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOG_TAG);
        wakeLock.acquire();
    }

    class RunThread extends Thread {
        private final String LOG_TAG = RunThread.class.getSimpleName();

        @Override
        public void run() {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                wifiInfo = wifiManager.getConnectionInfo();
                CurrentSSID = wifiInfo.getSSID();
                Log.d(LOG_TAG, wifiInfo.toString());
            } catch (NullPointerException e) {
                Log.d(LOG_TAG, "Nothing to bring");
            }

//            if (wifistatus == 1){
//                try {
//                    packetSender = new PacketSended();
//                } catch (SocketException e) {
//                    e.printStackTrace();
//                }
//                packetSender.start();
//            }
//            while (CurrentSSID == "<unknown ssid>" || CurrentSSID == null) {
//                try {
//                    wifiManager.reconnect();
//                    Log.d(LOG_TAG, "Reconnecting");
//
//                    wifiInfo = wifiManager.getConnectionInfo();
//                    Log.d(LOG_TAG, "Checking in thread WHILE method" + wifiInfo.toString());
//
//
//                    if(!wifiLock.isHeld()){
//                        Log.d(LOG_TAG, "WiFi lock isn't helded, trying");
//                        wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, getClass().getSimpleName());
//                        wifiLock.acquire();
//                        Log.d(LOG_TAG, "WiFi locked");
//                    } else {
//                        Log.d(LOG_TAG, "WiFi locked");
//                    }
//                    Thread.sleep(1000);
//                    counter++;
//                    Log.d(LOG_TAG, "counter = " + counter);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                if (CurrentSSID != null && CurrentSSID != "<unknown ssid>") {
//                    Log.d(LOG_TAG, "WIFI CONNECTED");
//                } else {
//                    Log.d(LOG_TAG, "WIFI NOT CONNECTED"+Thread.currentThread().toString());
//                }
//            }

/*            try {
                Log.d(LOG_TAG, "Trying reconnect to ship net. SSID, enter");
                if (CurrentSSID != shipSSID) {
                    Log.d(LOG_TAG, "CurrentSSID = " + CurrentSSID);
                    Log.d(LOG_TAG, "shipSSID    = " + shipSSID);
                    int counter1 = 0;
                    Log.d(LOG_TAG, "CurrentSSID != shipSSID PIZDEC KAK" + counter1);
                    List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                    for(WifiConfiguration i : list){
                        //if(i.SSID != null && i.SSID != "<unknown ssid>" && i.SSID.equals("\"" + shipSSID + "\"")) {
                            if(i.SSID.equals(shipSSID)) {
                            Log.d(LOG_TAG, "ALARM different SSID or no WiFI");
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            if(wifiManager.setWifiEnabled(false)){
//                                Log.d(LOG_TAG, "WiFI enable");
//                                wifiManager.setWifiEnabled(true);
//                            }
                            //wifiManager.disconnect();
                            Log.d(LOG_TAG, "Disconnect is ____ " + wifiManager.disconnect());
                            wifiManager.enableNetwork(i.networkId, true);
                            try {
                                Thread.sleep(6000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            wifiManager.reconnect();
                            Log.d(LOG_TAG, "reconnect");
                            try {
                                Thread.sleep(4000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Log.d(LOG_TAG, "Reconnect is _____" + wifiManager.reconnect());

                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    counter1++;
                }
                Log.d(LOG_TAG, "Trying reconnect to ship net SSID, ended");

            }catch (Exception e){
                Log.d(LOG_TAG, "No WiFi connection");
            }


            try {
                Log.d(LOG_TAG, "Trying reconnect to ship net SSID, enter");
                if (CurrentSSID == "<unknown ssid>" || CurrentSSID == null) {
                    Log.d(LOG_TAG, "CurrentSSID == \"<unknown ssid>\" || CurrentSSID == null");
                    List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                    for(WifiConfiguration i : list){
                        //if(i.SSID != null && i.SSID != "<unknown ssid>" && i.SSID.equals("\"" + shipSSID + "\"")) {
                        //if(i.SSID != null && i.SSID != "<unknown ssid>")
                        {
                            Log.d(LOG_TAG, "ALARM no WiFI");
                            *//*
                            try { Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if(wifiManager.setWifiEnabled(false)){
                                Log.d(LOG_TAG, "WiFI enable");
                                wifiManager.setWifiEnabled(true);
                            }
                            ifiManager.disconnect();
                            *//*
                            Log.d(LOG_TAG, "Disconnect is ____ " + wifiManager.disconnect());
                            wifiManager.enableNetwork(i.networkId, true);
                            try {
                                Thread.sleep(6000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            wifiManager.reconnect();
                            Log.d(LOG_TAG, "reconnect");
                            try {
                                Thread.sleep(4000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Log.d(LOG_TAG, "Reconnect is _____" + wifiManager.reconnect());

                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                Log.d(LOG_TAG, "Trying reconnect to ship net SSID, ended");
            } catch (Exception e){
                Log.d(LOG_TAG, "Trying reconnect to ship net SSID, ended");
            }*/

            try {
                Log.d(LOG_TAG, "Trying reconnect to 20380");
                Log.d(LOG_TAG, "CurrentSSID = " + CurrentSSID);
                while (CurrentSSID.equals("<unknown ssid>")) {
                    if (ActivityCompat.checkSelfPermission(context4Locker, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    List<WifiConfiguration> listOfWifiConf = wifiManager.getConfiguredNetworks();
                    Log.d(LOG_TAG, "CurrentSSID = " + CurrentSSID);
                    Log.d(LOG_TAG, "shipSSID    = " + shipSSID);
                    for(WifiConfiguration i : listOfWifiConf){
                        Log.d(LOG_TAG, "Getting WifiConfiguration");
                        while(i.SSID != null && i.SSID.equals(shipSSID)){
                            Log.d(LOG_TAG, "enter to while(i.SSID != null && i.SSID.equals(shipSSID))");
                            wifiManager.disconnect();
                            wifiManager.enableNetwork(i.networkId, true);
                            wifiManager.reconnect();
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
//                            break;
                            Log.d(LOG_TAG, "exit from while(i.SSID != null && i.SSID.equals(shipSSID))");
                        }
                        Log.d(LOG_TAG, "Getting WifiConfiguration ended");
                    }

                    Log.d(LOG_TAG, "reconnect");
                    Log.d(LOG_TAG, "Reconnect is _____" + wifiManager.reconnect());
                }

                Log.d(LOG_TAG, "Trying reconnect to ship net SSID, ended");

            } catch (Exception e) {
                Log.d(LOG_TAG, "No WiFi connection");
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}

