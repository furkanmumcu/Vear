package com.vearproject.vear;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class RecordingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings);


        findViewById(R.id.recordingsbtn).setOnClickListener(recordingsClick);

        findViewById(R.id.toListenbtn).setOnClickListener(toListenClick);

    }
    private View.OnClickListener recordingsClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(RecordingsActivity.this, MyRecordingsActivity.class));
        }
    };
    private View.OnClickListener toListenClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(RecordingsActivity.this, ListenedRecordingsActivity.class));
        }
    };
}
