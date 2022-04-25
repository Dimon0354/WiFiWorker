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
    protected Context context4Locker;
    static AtomicInteger wifistatus = new AtomicInteger(1);
    private String badSSID = "<unknown ssid>";
    private Object object = new Object();
    private NetworkInfo networkInfo;


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
                networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);


                if (networkInfo.isConnected()) {
                    Wifi_Status = networkInfo.isConnected();
                    packetSendedStatus = new PacketSended(Wifi_Status);
                    wifistatus.set(1);
                    Log.d(LOG_TAG, "START if (networkInfo != null && CurrentSSID != <unknown ssid> && networkInfo.isConnected())");
                    wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    wifiInfo = wifiManager.getConnectionInfo();

                    Log.d(LOG_TAG, wifiInfo.toString());
                    CurrentSSID = wifiInfo.getSSID();
                    RunThread thread = new RunThread();
                    thread.start();

                } else {
                    Log.d(LOG_TAG, "No WiFi!!!");
                    Wifi_Status = networkInfo.isConnected();
                    packetSendedStatus = new PacketSended(Wifi_Status);
                    wifistatus.set(0);

                    RunThread thread = new RunThread();
                    thread.start();
                }

            }

        }
        Log.d(LOG_TAG, "RunReceiverAndSendPackage onReceive finish");
    }

    class RunThread extends Thread {
        private final String LOG_TAG = RunThread.class.getSimpleName();

        @Override
        public synchronized void run() {

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

            try {
                Log.d(LOG_TAG, "Trying reconnect to 20380");
                Log.d(LOG_TAG, "CurrentSSID = [" + CurrentSSID + "]");
                Log.d(LOG_TAG, "CurrentSSID == [" + badSSID + "] is  " + (CurrentSSID == "<unknown ssid>"));
                while (CurrentSSID != shipSSID || CurrentSSID == badSSID) {
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

                            wait();

                            Log.d(LOG_TAG, "exit from while(i.SSID != null && i.SSID.equals(shipSSID))");
                            //break;

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

