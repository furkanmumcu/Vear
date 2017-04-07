package com.vearproject.vear;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import java.io.File;

public class ListenService extends Service {
    String PATH = Environment.getExternalStorageDirectory().getPath()+"/Vear/ToListen";
    Recorder recorder = new Recorder();
    SoundComparator soundComparator;
    //Thread t;
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
        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
        int bufferSize = AudioRecord.getMinBufferSize(8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        recorder.setBufferSize(bufferSize);
        isrRunning = true;

    }

    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        Thread t = new Thread()
        {
            public void run() {
                int cnt = 0;
                while(isrRunning) {
                    System.out.println("Inside Service");

                    //File f = new File(PATH);
                    //File file[] = f.listFiles();
                    //Sound s = new Sound(file[0].toString()); // assume one recording to listen

                    recorder.setWavname("toCompare" + cnt);
                    recorder.startRecording();
                    try {
                        Thread.sleep(5000); // 10 s record
                    } catch (InterruptedException e) {
                        System.out.print("buldum: ");
                        e.printStackTrace();
                    }
                    recorder.stopRecording();
                    cnt++;

                    // TODO her kaydettigi dosyanin ismi ayni olacak (cnt kalkacak)
                    // kaydi karsilastirilacak sesler ile karsilastirip benzerlik bulacak
                    // benzerlik belli degerin ustundeyse notifiaction verecek

                    //String tempPath = Environment.getExternalStorageDirectory().getPath()+ "/Vear/MyRecordings/toCompare.wav";
                    //Sound temp = new Sound(tempPath);
                    //System.out.println(soundComparator.compare(s,temp));


                }
            }
        }; t.start();


    }

    @Override
    public void onDestroy() {
        System.out.println("STOP Service");
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
        //t.interrupt();
        //this.stopSelf();
        isrRunning = false;

    }
}
