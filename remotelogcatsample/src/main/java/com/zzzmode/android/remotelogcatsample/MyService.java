package com.zzzmode.android.remotelogcatsample;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import com.zzzmode.android.remotelogcat.LogcatRunner;
import com.zzzmode.android.remotelogcat.LogcatRunner.LogConfig;
import java.io.IOException;

public class MyService extends Service {
    private IMyAidlInterface.Stub binder = new IMyAidlInterface.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }


        @Override
        public void start(int wsPort, String wsPrefix) throws RemoteException {
            try {
                LogcatRunner.getInstance()
                    .config(LogConfig.builder()
                        .port(wsPort)
                        .setWebsocketPrefix(wsPrefix)
                        .setWsCanReceiveMsg(false)
                        .write2File(true)
                        .setLogFileDir(Environment.getExternalStorageDirectory() + "/lpsdklog"))
                    .with(getApplicationContext())
                    .start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void stop() throws RemoteException {
            LogcatRunner.getInstance().stop();
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();

        // 在API11之后构建Notification的方式
        Notification.Builder builder = new Notification.Builder(this); //获取一个Notification构造器
        Intent nfIntent = new Intent(this, MainActivity.class);

        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0))
            .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
            .setContentTitle("robotLog")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText("robotLog")
            .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        Notification notification;
        if (Build.VERSION.SDK_INT < 16) {
            notification = builder.getNotification();
        } else {
            notification = builder.build();
        }
        notification.defaults = Notification.DEFAULT_ALL; //设置为默认的声音

        startForeground(1, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
