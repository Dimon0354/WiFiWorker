package com.example.switchwifisender;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

public class RunReceiverAndSendPackeg extends Service {

    private final String LOG_TAG = RunReceiverAndSendPackeg.class.getSimpleName();
    private CheckWiFiSSID_State checkWiFiSSID_state = new CheckWiFiSSID_State();
    public static final String CHANNEL_ID = "#123";
    public static final String CHANNEL_NAME = "my notification";
    public static final String CHANNEL_DESCRIPTION = "Test";
    private int counter = 0;
    private Timer timer;
    private TimerTask timerTask;
    private PacketSended packetSender = new PacketSended();

    public RunReceiverAndSendPackeg() throws SocketException {
    }

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "RunReceiverAndSendPackeg service created");
        super.onCreate();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startMyOwnForeground();
        }
        packetSender.start();
//        } else {
//            startMyOwnForeground(1, new Notification());
//        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand start");
        Toast.makeText(this, "Сервис запущен", Toast.LENGTH_SHORT).show();
        startTimer();
        IntentFilter brIntentFilter = new IntentFilter();
        brIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        try {
            registerReceiver(checkWiFiSSID_state, brIntentFilter);
        } catch (Exception e){
            Log.d(LOG_TAG, "onStartCommand: error with registering receiver");
            registerReceiver(checkWiFiSSID_state, brIntentFilter);
        }
        Log.d(LOG_TAG, "CheckWiFiSSID_State receiver created");



        Log.d(LOG_TAG, "onStartCommand finish");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "RunReceiverAndSendPackeg service onDestroy");
        unregisterReceiver(checkWiFiSSID_state);
        Intent broadcastreceiver = new Intent(this, RunServiceMore.class);
        sendBroadcast(broadcastreceiver);
        stopTimer();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startMyOwnForeground(){
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
        channel.setLightColor(Color.RED);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager != null){
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        Notification notification = notifBuilder.setOngoing(true)
                .setContentTitle("Сервис удержания WiFi")
                .setContentText("Во избежание неполадок - не закрывать!")
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(1, notification);

    }


    public void inizialiseTimerTask(){
        timerTask = new TimerTask() {
            @Override
            public void run() {
            Log.d(LOG_TAG, "Timer ticking " + counter++ + "___" + Thread.currentThread());
            }
        };
    }

    public void startTimer(){
        timer = new Timer();
        inizialiseTimerTask();
        timer.schedule(timerTask, 1000, 1000);
    }

    public void stopTimer(){
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

}