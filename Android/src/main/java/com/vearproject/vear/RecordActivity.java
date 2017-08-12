package com.vearproject.vear;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RecordActivity extends AppCompatActivity {
    Recorder recorder = new Recorder();
    //Recorder monoRecorder = new Recorder();
    //RecorderMono recorderMono = new RecorderMono();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        setButtonHandlers();
        enableButtons(false);


        int bufferSize = AudioRecord.getMinBufferSize(8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        recorder.setBufferSize(bufferSize);
        //recorderMono.setBufferSize(bufferSize);

        //monoRecorder.setRECORDER_CHANNELS(AudioFormat.CHANNEL_IN_MONO);
        //monoRecorder.setRECORDER_SAMPLERATE(8000);
        //monoRecorder.setBufferSize(bufferSize);
        //monoRecorder.setAUDIO_RECORDER_FOLDER("MyRecordings/mono");
        //monoRecorder.setAUDIO_RECORDER_TEMP_FILE("record_temp_mono.raw");

    }

    private void setButtonHandlers() {
        ((Button)findViewById(R.id.btnStart)).setOnClickListener(btnClick);
        ((Button)findViewById(R.id.btnStop)).setOnClickListener(btnClick);
    }

    private void enableButton(int id,boolean isEnable){
        ((Button)findViewById(id)).setEnabled(isEnable);
    }

    private void enableButtons(boolean isRecording) {
        enableButton(R.id.btnStart,!isRecording);
        enableButton(R.id.btnStop,isRecording);
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.btnStart:{
                    if (!isEmpty((EditText) findViewById(R.id.tw1))) {
                        enableButtons(true);
                        EditText text = (EditText)findViewById(R.id.tw1);
                        String str = text.getText().toString();
                        recorder.setWavname(str);
                        recorder.startRecording();

                        //recorderMono.setWavname(str);
                        //recorderMono.startRecording();
                        //monoRecorder.setWavname(str);
                        //monoRecorder.startRecording();

                        text.setEnabled(false);


                        break;
                    }
                    else {
                        Toast.makeText(RecordActivity.this, "Please enter a name!", Toast.LENGTH_SHORT).show();
                        break; /* instead of this, might be used following
                                  if (!isEmpty((EditText) findViewById(R.id.tw1)))
                                  in the next case */
                    }
                }
                case R.id.btnStop:{
                    enableButtons(false);
                    recorder.stopRecording();
                    //recorderMono.stopRecording();

                    //monoRecorder.stopRecording();
                    EditText text = (EditText)findViewById(R.id.tw1);
                    text.setEnabled(true);
                    break;
                }
            }
        }
    };
}
