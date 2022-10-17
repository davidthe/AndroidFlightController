package com.example.airplanecontroller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ComponentName;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.util.Log;

import com.example.airplanecontroller.services.SensorsService;
//
//import com.github.pwittchen.reactivesensors.library.ReactiveSensorEvent;
//import com.github.pwittchen.reactivesensors.library.ReactiveSensors;
//
//import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
//import io.reactivex.rxjava3.functions.Consumer;
//import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent serviceIntent = new Intent(this, SensorsService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service");

        Log.e("","starting service");
        ContextCompat.startForegroundService(this, serviceIntent);


    }
}