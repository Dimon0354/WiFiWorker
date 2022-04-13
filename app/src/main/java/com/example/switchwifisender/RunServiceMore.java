package com.example.switchwifisender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RunServiceMore extends BroadcastReceiver {
    private final String LOG_TAG = RunServiceMore.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "RunReceiverAndSendPackeg service has been stoped, but...");
        try {
            context.startService(new Intent(context, RunReceiverAndSendPackeg.class));
        } catch (RuntimeException e){
            Log.d(LOG_TAG, "RuntimeException, trying to restart RunReceiverAndSendPackeg");
            context.startService(new Intent(context, RunReceiverAndSendPackeg.class));
        }
    }
}