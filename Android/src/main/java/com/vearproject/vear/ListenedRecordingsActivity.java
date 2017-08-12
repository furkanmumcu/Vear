package com.vearproject.vear;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class ListenedRecordingsActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    final String PATH = Environment.getExternalStorageDirectory().toString()+"/Vear/ToListen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listened_recordings);


        /* Path handling */
        String[] parts = getRecordingNames(PATH);

        /* list view */
        ArrayList<String> list = new ArrayList<String>(Arrays.asList(parts));
        adapter = new ArrayAdapter<String>(this, R.layout.activity_listview,list );
        ListView listView = (ListView) findViewById(R.id.soundlist);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);

    }
    @Override
    public void onCreateContextMenu(android.view.ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.soundlist) {
            super.onCreateContextMenu(menu, v, menuInfo);
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.listenmenu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String name = ((TextView) info.targetView).getText().toString();
        String path = PATH + "/" + name;
        String monoPath = Environment.getExternalStorageDirectory().toString()+"/Vear/MonoToListen"+ "/" + name;
        File file = new File(path);
        File monoFile = new File(monoPath);
        SharedPreferences pref = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        switch (item.getItemId()) {
            case R.id.delete:
                adapter.remove(adapter.getItem(info.position));
                file.delete();
                monoFile.delete();
                break;
            case R.id.tweet:
                //editor.clear();
                editor.putString(name, "message");
                editor.commit();
                break;
            case R.id.vibrate:
                //editor.clear();
                editor.putString(name, "vibration");
                editor.commit();
                break;
            case R.id.open:
                openFile(file);
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return super.onContextItemSelected(item);
    }

    private String[] getRecordingNames(String path){
        File f = new File(path);
        File file[] = f.listFiles();

        int length = file.length;
        String [] returning = new String[length];
        for(int i = 0; i<length; i++){
            String parts[] = file[i].toString().split("/");
            returning[i] = parts[parts.length-1];
        }
        return returning;
    }


    private  void openFile(File file)  {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "audio/*");
        startActivity(intent);
    }

}
