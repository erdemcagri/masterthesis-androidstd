package com.example.appbq_r;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);*/

        try {
            Runtime.getRuntime().exec("chmod -R 777 " + getApplicationContext().getFilesDir());
            Runtime.getRuntime().exec("chmod -R 777 " + "/data/local/tmp");
        } catch (Exception e) {
            logToFile(this, e.getMessage());
        }

        logToFile(this, "example line.\n");

        runCommand("screenrecord --time-limit 5 "
                + getApplicationContext().getFilesDir() + "/sRec.mp4\n");
        runCommand("screencap "
                + getApplicationContext().getFilesDir() + "/screen.png");
    }

    private void logToFile(Context context, String sBody) {
        File dir = new File(context.getFilesDir(), "permLogs");
        if (!dir.exists()){
            dir.mkdir();
        }
        try {
            File gpxfile = new File(dir, "plogs.txt");
            FileWriter writer = new FileWriter(gpxfile, true);
            writer.append(sBody).append("\n\n");
            writer.flush();
            writer.close();
            Toast.makeText(this, "Data logged to " + context.getFilesDir(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runCommand(String...commands) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
            for(String s : commands) {
                dataOutputStream.writeBytes(s + "\n");
                dataOutputStream.flush();
            }
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            try {
                process.waitFor();
            } catch(Exception e) {
                logToFile(this, e.getMessage());
            }
            logToFile(this, "Process completed.");
            dataOutputStream.close();
        } catch (Exception e) {
            logToFile(this, e.getMessage());
        }
    }

    //https://developer.android.com/studio/command-line/adb

}