package com.pidpia.delivery;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

public class GPS_service extends Service {

    android.os.Handler mHandler;
    boolean mRunning;

    User user;


    @Override
    public void onCreate() {
        super.onCreate();

        user = new User(this);
        Log.e("GPS_service", "Service Created");
        mHandler = new android.os.Handler();
        mRunning = false;

        new Thread(new Runnable() {
            @Override
            public void run() {

                Log.i("gpsup", "SERVICE START@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

                while (user.getData("working").equals("1")) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            Log.d("service", "RUNNING@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                            //GPS 업데이트
                            user.gpsup();

                        }
                    });
                    //5분마다 작동
                    SystemClock.sleep(1500);
                }
            }
        }).start();


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("gpsup","SERVICE STOP!!!!!!!!!!!!!!!!!!!!!!!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //죽으면 살리기
        return START_REDELIVER_INTENT;

    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
