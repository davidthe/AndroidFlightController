package com.example.airplanecontroller.socket;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.util.Date;

import io.ably.lib.realtime.AblyRealtime;
import io.ably.lib.realtime.Channel;
import io.ably.lib.realtime.CompletionListener;
import io.ably.lib.realtime.ConnectionStateListener;
import io.ably.lib.types.AblyException;
import io.ably.lib.types.ErrorInfo;

public class AblyService extends Service {
    private final String locationChannel = "locations";
    private final String sensorsChannel = "sensors";

    private BroadcastReceiver locationReceiver;
    private BroadcastReceiver sensorsReceiver;
    private final AblyRealtime ably = new AblyRealtime("ZES6pg.8neWXQ:E9STKSNMIwKaIpMrrrDD1iZUwe6SKSJRQGiT9pwMF1A");
    private Boolean isAblyConected = false;
    private int messageCounter = 0;
    private int sentMessages = 0;


    public AblyService() throws AblyException {
        ably.connection.on(new ConnectionStateListener() {
            @Override
            public void onConnectionStateChanged(ConnectionStateChange state) {
                System.out.println("New state is " + state.current.name());
                switch (state.current) {
                    case connected: {
                        isAblyConected = true;
                        break;
                    }
                    case failed: {
                        isAblyConected = false;
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(locationReceiver == null){
            locationReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String data = (String) intent.getExtras().get("coordinates");
                    Log.i("AblyService",  data+"\n" );
                    try {
                        publishMessage(locationChannel, "send",data );
                    } catch (AblyException e) {
                        e.printStackTrace();
                    }
                }
            };
        }
        registerReceiver(locationReceiver,new IntentFilter("location_update"));

        if(sensorsReceiver == null){
            sensorsReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String data = (String) intent.getExtras().get("sensors");
                    Log.i("AblyService",  data+"\n" );
                    try {
                        publishMessage(sensorsChannel, "send",data );
                    } catch (AblyException e) {
                        e.printStackTrace();
                    }
                }
            };
        }
        registerReceiver(sensorsReceiver,new IntentFilter("sensors_update"));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationReceiver != null){
            unregisterReceiver(locationReceiver);
        }

        if(sensorsReceiver != null){
            unregisterReceiver(sensorsReceiver);
        }
    }

    public void publishMessage(String channelName, String name, String message) throws AblyException {
        if(!isAblyConected){
            return;
        }

        Channel channel = ably.channels.get(channelName);
        Log.d( "AblyService", new Date().getTime()+ " sending message: "+ messageCounter++);
        channel.publish(name, message, new CompletionListener() {
            @Override
            public void onSuccess() {
                System.out.println(new Date().getTime()+" Message successfully sent " + sentMessages++);
            }

            @Override
            public void onError(ErrorInfo reason) {
                System.err.println("Unable to publish message; err = " + reason.message);
            }
        });
    }
}