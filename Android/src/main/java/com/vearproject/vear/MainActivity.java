package com.vearproject.vear;

import android.content.Intent;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.musicg.wave.Wave;

import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    FTPClient ftpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ////////////////////////////////////////////////////////////////////////////////////////
        ////////////// Adjust Vear related folders
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,"Vear");

        if(!file.exists()){
            file.mkdirs();
        }

        String filepath2 = Environment.getExternalStorageDirectory().getPath() +"/Vear";
        File file2 = new File(filepath2,"MyRecordings");

        if(!file2.exists()){
            file2.mkdirs();
        }

        String filepath3 = Environment.getExternalStorageDirectory().getPath() +"/Vear";
        File file3 = new File(filepath3,"FTP");

        if(!file3.exists()){
            file3.mkdirs();
        }

        String filepath4 = Environment.getExternalStorageDirectory().getPath() +"/Vear";
        File file4 = new File(filepath4,"ToListen");

        if(!file4.exists()){
            file4.mkdirs();
        }

        ////////////////////////////////////////////////////////////////////////////////////////

        ((Button)findViewById(R.id.ftpp)).setOnClickListener(ftpClick);
        ((Button)findViewById(R.id.listenbtn)).setOnClickListener(listenClick);
        ((Button)findViewById(R.id.recordingsbtn)).setOnClickListener(stopclick);

    }

    private View.OnClickListener ftpClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            System.out.println("eben");

            Thread t = new Thread()
            {
                public void run() {

                    ftpClient = new FTPClient();
                    System.out.println("1");
                    try {
                        ftpClient.connect("192.168.230.103");
                        System.out.println("2");
                        System.out.println(ftpClient.getReplyString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        //ftpClient.login(usernamee,passs);
                        ftpClient.login("pi","raspberry");
                        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    try {
                        String remoteFile2 = "/home/pi/Documents/BlueJ Projects/music1" +
                                ".wav";
                        File downloadFile2 = new File(Environment.getExternalStorageDirectory().getPath() +"/Vear/FTP/veardeneme.wav");

                        OutputStream outputStream2 = new BufferedOutputStream(new FileOutputStream(downloadFile2));
                        InputStream inputStream = ftpClient.retrieveFileStream(remoteFile2);

                        byte[] bytesArray = new byte[4096000];
                        int bytesRead = -1;
                        while ((bytesRead = inputStream.read(bytesArray)) != -1) {
                            outputStream2.write(bytesArray, 0, bytesRead);
                        }

                        boolean success = ftpClient.completePendingCommand();
                        if (success) {
                            System.out.println("File #2 has been downloaded successfully.");
                        }
                        outputStream2.close();
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            t.start();
        }
    };


    private View.OnClickListener listenClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            System.out.println("serviceClick");
            startService(new Intent(MainActivity.this, ListenService.class));

/*            Thread listenT = new Thread()
            {
                public void run() {
                    startService(new Intent(MainActivity.this, ListenService.class));
                }
            }; listenT.start();*/

        }
    };

    private View.OnClickListener stopclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            System.out.println("stopppp");
            stopService(new Intent(MainActivity.this, ListenService.class));
        }
    };
}
