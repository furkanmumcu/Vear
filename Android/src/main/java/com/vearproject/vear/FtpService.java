package com.vearproject.vear;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Environment;
import android.os.IBinder;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class FtpService extends Service {
    final String PATH = Environment.getExternalStorageDirectory().getPath() + "/Vear/ToListen";
    final String address = "192.168.1.33";
    final String username = "pi";
    final String password = "raspberry";
    final String destination = Environment.getExternalStorageDirectory().getPath() +"/Vear/FTP/latest.wav";
    final String source = "/home/pi/Documents/Vear/latest.wav";
    FTPClient ftpClient;
    Recorder recorder = new Recorder();
    SoundComparator soundComparator;
    boolean isrRunning;

    public FtpService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        int bufferSize = AudioRecord.getMinBufferSize(44000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
        recorder.setBufferSize(bufferSize);
        isrRunning = true;

    }

    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        Thread t = new Thread() {
            public void run() {
                int cnt = 0;
                while (isrRunning) {
                    ftpClient = new FTPClient();
                    try {
                        ftpClient.connect(address);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        ftpClient.login(username, password);
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
                            System.out.println("File has been downloaded successfully.");
                        }
                        outputStream.close();
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    float result = compareFiles();
                    System.out.println("Similarity result is " + result);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }

    @Override
    public void onDestroy() {
        System.out.println("STOP Service");
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
        //t.interrupt();
        //this.stopSelf();
        isrRunning = false;
    }


    public float compareFiles() {
        Sound compare = new Sound(Environment.getExternalStorageDirectory().getPath() +"/Vear/FTP/latest.wav");
        File ToListen = new File(PATH);
        File[] ToListenList = ToListen.listFiles();
        return compareSounds(ToListenList, compare);
    }

    public float compareSounds(File[] files, Sound compare) {
        float maxSimilarity = 0.11f;
        float result = 0.0f;
        boolean flag = true;
        for (File file : files) {
            if (file.isDirectory()) {
                compareSounds(file.listFiles(), compare);
            } else {
                Sound toListen = new Sound(file.getPath());
                soundComparator = new SoundComparator();
                result = soundComparator.compare(toListen, compare);
                if (result > maxSimilarity) {
                    flag = true;
                }
                if (flag) {
                    String preference = getPreference(file);
                    if (preference.equals("vibration")) {
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(2000);
                    } else if (preference.equals("message")) {
                        sendSMS();
                    }
                }
            }
        }
        return result;
    }

    public String getPreference(File file) {
        String path = file.getPath();
        String parts[] = path.split("/");
        String name = parts[parts.length - 1];
        SharedPreferences pref = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String result = pref.getString(name, "Default");
        return result;
    }

    public void sendSMS() {
        SharedPreferences pref = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String phoneNumber = pref.getString("phoneNumber", "");
        String message = pref.getString("message", "Hello World");
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    public int randomWithRange(int min, int max)
    {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }
}
