package com.zzzmode.android.remotelogcatsample;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView textIP;
    private WifiReceiver wifiReceiver;

    private int wsPort = 11229;
    private String wsPrefix = "/logcat";

    private IMyAidlInterface binder;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            textIP.setText("service disconnected");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = IMyAidlInterface.Stub.asInterface(service);
            startLog();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent intent = new Intent(this, MyService.class);
        startService(intent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);



        findViewById(R.id.logOn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    binder.start(wsPort, wsPrefix);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                test();
            }
        });

        findViewById(R.id.logOff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    binder.stop();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
            }
        });

        textIP = findViewById(R.id.textIP);



        wifiReceiver = new WifiReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiReceiver, filter);

    }

    private void startLog() {
        try {
            binder.start(wsPort, wsPrefix);
        } catch (RemoteException e) {
            Log.e("testlog", e.getMessage(), e);
        }
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
        unbindService(connection);
        unregisterReceiver(wifiReceiver);
        super.onDestroy();
    }


    public class WifiReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)
                || WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {


                WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(context.WIFI_SERVICE);
                int ipAddressInt = wm.getConnectionInfo().getIpAddress();
                String ipAddress = String.format("%d.%d.%d.%d", (ipAddressInt & 0xff), (ipAddressInt >> 8 & 0xff), (ipAddressInt >> 16 & 0xff), (ipAddressInt >> 24 & 0xff));

                StringBuilder sbf = new StringBuilder();
                sbf.append("ws://").append(ipAddress).append(":").append(wsPort).append(wsPrefix);
                textIP.setText(sbf);
                Log.i("testlog", "" + sbf);

            }
        }
    }
}
