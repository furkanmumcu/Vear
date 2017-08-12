package com.vearproject.vear;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

public class MyRecordingsActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private Socket client;
    private FileInputStream fileInputStream;
    private BufferedInputStream bufferedInputStream;
    private OutputStream outputStream;
    private ObjectOutputStream objectOutputStream;
    public String AudioSavePathInDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recordings);


        /* Path handling */
        String path = Environment.getExternalStorageDirectory().toString()+"/Vear/MyRecordings";
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
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String name = ((TextView) info.targetView).getText().toString();
        String path = Environment.getExternalStorageDirectory().toString()+"/Vear/MyRecordings"+"/"+name;
        //String monoPath = Environment.getExternalStorageDirectory().toString()+"/Vear/MyRecordings/mono"+"/"+name;
        File file = new File(path);
        //File monoFile = new File(monoPath);
        SharedPreferences pref = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        switch (item.getItemId()) {
            case R.id.deleteItem:
                adapter.remove(adapter.getItem(info.position));
                boolean deleted = file.delete();
                //boolean monoDeleted = monoFile.delete();
                System.out.println(deleted);
                break;
            case R.id.listen:
                String dest = Environment.getExternalStorageDirectory().toString()+"/Vear/ToListen"+"/"+name;
                String monoDest = Environment.getExternalStorageDirectory().toString()+"/Vear/MonoToListen"+"/"+name;
                transferFile(path,dest);
                transferFile(path,monoDest);
                break;
            case R.id.openItem:
                openFile(file);
                break;
            case R.id.upload:
                AudioSavePathInDevice = path;//Environment.getExternalStorageDirectory().toString()+"/Vear/ToListen"+"/"+name;
                new LongOp().execute("");
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return super.onContextItemSelected(item);
    }

    public String[] getRecordingNames(String path){
        File f = new File(path);
        File file[] = f.listFiles();

        int length = file.length;
        String [] returning = new String[length];
        for(int i = 0; i<length; i++){
                String parts[] = file[i].toString().split("/");
                returning[i] = parts[parts.length - 1];
        }
        return returning;
    }


    public  void openFile(File file)  {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "audio/*");
        startActivity(intent);
    }

    private void transferFile(String source, String destination ){
        try {
            InputStream in = new FileInputStream(source);
            OutputStream out = new FileOutputStream(destination);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public class LongOp extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            System.out.println("girdim" + AudioSavePathInDevice);
            File file = new File(AudioSavePathInDevice);
            try {

                //Log.i("mesaj", "trying");
                String username = "uservear asd123";
                //String password = "asd123";
                client = new Socket();
                client.connect(new InetSocketAddress("213.159.1.91", 8086), 30000);
                //Log.i("mesaj","baglanti kuruldu");
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                objectOutputStream.writeObject(username);
                objectOutputStream.reset();
                //objectOutputStream.writeObject(password);
                //objectOutputStream.reset();
                byte[] mybytearray = new byte[(int) file.length()];
                fileInputStream = new FileInputStream(file);
                bufferedInputStream = new BufferedInputStream(fileInputStream);
                //Log.i("mesaj","test1");
                bufferedInputStream.read(mybytearray, 0, mybytearray.length); //read the file
                outputStream = client.getOutputStream();
                //Log.i("mesaj","test2");
                outputStream.write(mybytearray, 0, mybytearray.length); //write file to the output stream byte by byte
                outputStream.flush();
                bufferedInputStream.close();
                //Log.i("mesaj","test3");
                outputStream.close();
                client.close();
                //Log.i("mesaj","gönderildi");

            } catch (UnknownHostException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }


    }

}
