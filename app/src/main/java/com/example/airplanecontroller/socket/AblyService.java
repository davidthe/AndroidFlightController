package com.example.airplanecontroller.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AblyService extends Service {
    public AblyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}