package com.example.airplanecontroller.services;

import static android.hardware.SensorManager.SENSOR_DELAY_GAME;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.airplanecontroller.MainActivity;
import com.example.airplanecontroller.R;
import com.github.pwittchen.reactivesensors.library.ReactiveSensorEvent;
import com.github.pwittchen.reactivesensors.library.ReactiveSensors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SensorsService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("","onStartCommand");
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_launcher_background).build();
        startForeground(1, notification);
//
        startSubscribingToSensors();

        Thread mainThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mainThread.start();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void startSubscribingToSensors(){
        new ReactiveSensors(getApplicationContext()).observeSensor(Sensor.TYPE_ACCELEROMETER  )
                .subscribeOn(Schedulers.computation())
                .filter(ReactiveSensorEvent::sensorChanged)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ReactiveSensorEvent>() {
                    @Override
                    public void accept(ReactiveSensorEvent event) throws Throwable {
                        float x = event.sensorValues()[0];
                        float y = event.sensorValues()[1];
                        float z = event.sensorValues()[2];

                        String message = String.format("x = %f, y = %f, z = %f", x, y, z);

//                        Log.d("gyroscope readings", message);

                        Intent i = new Intent("sensors_update");
                        i.putExtra("sensors", message);
                        sendBroadcast(i);
                    }

                });
    }
}