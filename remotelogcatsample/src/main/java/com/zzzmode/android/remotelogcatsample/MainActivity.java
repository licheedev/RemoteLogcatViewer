package com.zzzmode.android.remotelogcatsample;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.zzzmode.android.remotelogcat.LogcatRunner;

import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.logOn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LogcatRunner.getInstance()
                            .config(LogcatRunner.LogConfig.builder()
                                    .setWsCanReceiveMsg(false)
                                    .write2File(true)
                                    .setLogFileDir(Environment.getExternalStorageDirectory() + "/lpsdklog"))
                            .with(getApplicationContext())
                            .start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                test();
            }
        });

        findViewById(R.id.logOff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogcatRunner.getInstance().stop();
            }
        });

        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
            }
        });
    }

    private void test() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                int i = random.nextInt(5000);
                Log.e("testlog", "run --> test e " + i);
                Log.w("testlog", "run --> test w " + i);
                Log.i("testlog", "run --> test i " + i);
                Log.d("testlog", "run --> test d " + i);
            }
        }).start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogcatRunner.getInstance().stop();
    }
}
