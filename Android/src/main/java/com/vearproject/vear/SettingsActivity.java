package com.vearproject.vear;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingsActivity extends AppCompatActivity {

    Button button;
    EditText phoneText, messageText, durationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        button = (Button) findViewById(R.id.button);
        phoneText    = (EditText) findViewById(R.id.phoneNo);
        messageText  = (EditText) findViewById(R.id.message);


        button.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        int x = 5;
                        SharedPreferences pref = getSharedPreferences("preferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();

                        if(! messageText.getText().toString().equals("")) {
                            //editor.clear();
                            editor.putString("message", messageText.getText().toString());

                        }
                        if(! phoneText.getText().toString().equals("") ){
                            //editor.clear();
                            editor.putString("phoneNumber", phoneText.getText().toString());

                        }


                        editor.commit();
                    }
                });
    }
}
