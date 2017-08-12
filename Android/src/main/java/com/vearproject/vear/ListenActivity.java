package com.vearproject.vear;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ListenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);

        findViewById(R.id.btnStartP).setOnClickListener(phoneListen);
        findViewById(R.id.btnStopP).setOnClickListener(phoneStop);
        findViewById(R.id.btnStartE).setOnClickListener(earListen);
        findViewById(R.id.btnStopE).setOnClickListener(earStop);

        updateText();

    }

    private View.OnClickListener phoneListen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startService(new Intent(ListenActivity.this, ListenService.class));
            updateText();
        }
    };

    private View.OnClickListener phoneStop = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            stopService(new Intent(ListenActivity.this, ListenService.class));
            updateText();
        }
    };

    private View.OnClickListener earListen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //start ftp service
            startService(new Intent(ListenActivity.this, FtpService.class));
            updateText();

        }
    };

    private View.OnClickListener earStop = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //stop ftp service
            stopService(new Intent(ListenActivity.this, FtpService.class));
            updateText();

        }
    };



    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void updateText (){
        if(isMyServiceRunning(ListenService.class)){
            TextView tw1 = (TextView)findViewById(R.id.listenservice);
            tw1.setText("Listening via phone is on");
        }
        else{
            TextView tw1 = (TextView)findViewById(R.id.listenservice);
            tw1.setText("Listening via phone is off");
        }

        if(isMyServiceRunning(FtpService.class)){
            TextView tw1 = (TextView)findViewById(R.id.ftpservice);
            tw1.setText("Listening via Ears is on");
        }
        else{
            TextView tw1 = (TextView)findViewById(R.id.ftpservice);
            tw1.setText("Listening via Ears is off");
        }
    }

}
