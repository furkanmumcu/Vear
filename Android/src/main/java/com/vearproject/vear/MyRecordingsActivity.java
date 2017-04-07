package com.vearproject.vear;

import android.content.Intent;
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

public class MyRecordingsActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recordings);


        /* Path handling */
        String path = Environment.getExternalStorageDirectory().toString()+"/AudioRecorder"; // change this
        String[] parts = getRecordingNames(path);
        System.out.println(Arrays.toString(parts));



        /* list view */
        ArrayList<String> lstt = new ArrayList<String>(Arrays.asList(parts));

        adapter = new ArrayAdapter<String>(this, R.layout.activity_listview,lstt );

        ListView listView = (ListView) findViewById(R.id.recordinglist);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);

    }
    @Override
    public void onCreateContextMenu(android.view.ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.recordinglist) {
            super.onCreateContextMenu(menu, v, menuInfo);
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.contextmenu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteItem:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

                //remove from the list
                adapter.remove(adapter.getItem(info.position));

                //delete from memory
                String name = ((TextView) info.targetView).getText().toString();
                String path = Environment.getExternalStorageDirectory().toString()+"/AudioRecorder"+"/"+name;
                File file = new File(path);
                boolean deleted = file.delete();
                System.out.println(deleted);

                return true;
            case R.id.openItem:
                AdapterView.AdapterContextMenuInfo info2 = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                String name2 = ((TextView) info2.targetView).getText().toString();
                String path2 = Environment.getExternalStorageDirectory().toString()+"/AudioRecorder"+"/"+name2;
                File file2 = new File(path2);
                openFile(file2);

            default:
                return super.onContextItemSelected(item);
        }
    }

    public String[] getRecordingNames(String path){
        // get path of all recordings
        File f = new File(path);
        File file[] = f.listFiles();

        // prepare returning array
        int length = file.length;
        String [] returning = new String[length];

        // get recording names and fill the array
        for(int i = 0; i<length; i++){
            String parts[] = file[i].toString().split("/");
            returning[i] = parts[parts.length-1];
        }
        return returning;
    }

    public  void openFile(File file)  {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "audio/*");
        startActivity(intent);
    }
}
