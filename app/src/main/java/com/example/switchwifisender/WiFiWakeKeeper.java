package com.example.switchwifisender;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.PowerManager;

public class WiFiWakeKeeper {
    private final PowerManager.WakeLock mWakeLock;
    private final WifiManager.WifiLock mWifiLock;


    public WiFiWakeKeeper(Context context, String name) {
        this.mWakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).newWakeLock(1, name + "-cpu");
        this.mWifiLock = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).createWifiLock(1, name + "-wifi");
    }

    public final boolean isLocking(){
        return this.mWakeLock.isHeld() && this.mWifiLock.isHeld();
    }

    public final boolean lock(){
        if(!this.mWakeLock.isHeld()) this.mWakeLock.acquire();
        if(!this.mWifiLock.isHeld()) this.mWifiLock.acquire();
        return isLocking();
    }

    public final boolean release() {
        if (this.mWifiLock.isHeld()) {
            this.mWifiLock.release();
        }
        if (this.mWakeLock.isHeld()) {
            this.mWakeLock.release();
        }
        return !isLocking();
    }
}
