package com.vearproject.vear;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.Map;
import java.util.Set;

public class ListenService extends Service {
    String PATH = Environment.getExternalStorageDirectory().getPath() + "/Vear/ToListen";
    Recorder recorder = new Recorder();
    SoundComparator soundComparator;
    boolean isrRunning;

    public ListenService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        int bufferSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
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
                    SharedPreferences pref = getSharedPreferences("preferences", Context.MODE_PRIVATE);

                    //File f = new File(PATH);
                    //File file[] = f.listFiles();
                    //Sound s = new Sound(file[0].toString()); // assume one recording to listen

                    recorder.setWavname("target");
                    recorder.startRecording();
                    int duration =  5000;
                    try {
                        Thread.sleep(duration); // 5 s record
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    recorder.stopRecording();

                    String tempPath = Environment.getExternalStorageDirectory().getPath() + "/Vear/MyRecordings/target.wav";
                    float result = compareFiles(tempPath);


                }
            }
        };
        t.start();
    }

    //ToListen klasorunun icindeki sesleri ile verilen ses ile karsilastir
    public float compareFiles(String soundPath) {
        Sound compare = new Sound(soundPath);
        File ToListen = new File(PATH);
        File[] ToListenList = ToListen.listFiles();
        return compareSounds(ToListenList, compare);
    }

    public float compareSounds(File[] files, Sound compare) {
        float maxSimilarity= 0.11f;
        float result = 0.0f;
        boolean flag = false;
        for (File file : files) {
            flag=false;
            if (file.isDirectory()) {
                compareSounds(file.listFiles(), compare);
            } else {
                Sound toListen = new Sound(file.getPath());
                soundComparator = new SoundComparator();
                result = soundComparator.compare(toListen, compare);
                String preference = getPreference(file);
                System.out.println(file.getPath()+ "  " + result + " " + preference);
                if (result > maxSimilarity) {
                    flag = true;
                }
                if (flag) {
                    //String preference = getPreference(file);
                    System.out.println(file.getPath() + "  " + preference + "  " + result);
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

    @Override
    public void onDestroy() {
        System.out.println("STOP Service");
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
        //t.interrupt();
        //this.stopSelf();
        isrRunning = false;
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
        SharedPreferences pref = getSharedPreferences("settings", Context.MODE_PRIVATE);
        String phoneNumber = pref.getString("phoneNumber", "default");
        String message = pref.getString("message", "default");
        SmsManager sms = SmsManager.getDefault();
        //sms.sendTextMessage(phoneNumber, null, message, null, null);
    }
}
