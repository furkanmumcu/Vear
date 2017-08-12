package com.vearproject.vear;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;

import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    FTPClient ftpClient;
    final String PATH = Environment.getExternalStorageDirectory().getPath();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        createFile(PATH, "Vear");
        createFile(PATH +"/Vear", "MyRecordings");
        createFile(PATH +"/Vear", "FTP");
        createFile(PATH +"/Vear", "ToListen");
        createFile(PATH +"/Vear", "MonoToListen");
        //createFile(PATH +"/Vear/ToListen", "mono");
        //createFile(PATH +"/Vear/MyRecordings", "mono");

        findViewById(R.id.settings).setOnClickListener(settingsClick);
        findViewById(R.id.listenbtn).setOnClickListener(listenClick);
        findViewById(R.id.recordsbtn).setOnClickListener(recordsClick);
        findViewById(R.id.recordbtn).setOnClickListener(recordClick);

    }
    private View.OnClickListener settingsClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }
    };
    private View.OnClickListener ftpClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final String address = "192.168.230.103";
            final String username = "pi";
            final String password = "raspberry";
            final String destination = Environment.getExternalStorageDirectory().getPath() +"/Vear/FTP/veardeneme.wav";
            final String source = "/home/pi/Documents/BlueJ Projects/music1" +".wav";

            Thread t = new Thread()
            {
                public void run() {

                    ftpClient = new FTPClient();
                    try {
                        ftpClient.connect(address);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        ftpClient.login(username,password);
                        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        File file = new File(destination);
                        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
                        InputStream inputStream = ftpClient.retrieveFileStream(source);

                        byte[] bytesArray = new byte[4096000];
                        int bytesRead = -1;
                        while ((bytesRead = inputStream.read(bytesArray)) != -1) {
                            outputStream.write(bytesArray, 0, bytesRead);
                        }
                        boolean success = ftpClient.completePendingCommand();
                        if (success) {
                            System.out.println("File #2 has been downloaded successfully.");
                        }
                        outputStream.close();
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            t.start();
        }
    };

    private View.OnClickListener recordsClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //startService(new Intent(MainActivity.this, ListenService.class));
            //stopService(new Intent(MainActivity.this, ListenService.class));

            startActivity(new Intent(MainActivity.this, RecordingsActivity.class));

        }
    };
    private View.OnClickListener listenClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //startService(new Intent(MainActivity.this, ListenService.class));
            //stopService(new Intent(MainActivity.this, ListenService.class));

            startActivity(new Intent(MainActivity.this, ListenActivity.class));

        }
    };



    private View.OnClickListener recordClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this, RecordActivity.class));
            //sendSMS();
            //startActivity(new Intent(MainActivity.this, MyRecordingsActivity.class));
        }
    };
    private void createFile(String path, String filename){
        File file = new File(path,filename);
        if(!file.exists()){
            file.mkdirs();
        }
    }

    public void sendSMS() {
        SharedPreferences pref = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String phoneNumber = pref.getString("phoneNumber", "default");
        String message = pref.getString("message", "default");
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }
}
