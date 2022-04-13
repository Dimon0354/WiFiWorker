package com.example.switchwifisender;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "MainActivity onCreate start");
        Switch startServiceSwith = findViewById(R.id.switch_prog);
        startServiceSwith.setOnClickListener(new Switch.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startServiceSwith.isChecked()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(new Intent(MainActivity.this, RunReceiverAndSendPackeg.class));
                        Log.d(LOG_TAG, "RunReceiverAndSendPackeg started");
                    } else {
                        startService(new Intent(MainActivity.this, RunReceiverAndSendPackeg.class));
                    }

                } else if (startServiceSwith.isChecked() != true) {
                    Log.d(LOG_TAG, "Can't  start RunReceiverAndSendPack");
                }
            }
        });
        Log.d(LOG_TAG, "MainActivity onCreate end");
    }

    @Override
    protected void onStart() {
        Log.d(LOG_TAG, "MainActivity onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(LOG_TAG, "MainActivity onResume");
        super.onResume();
    }
}