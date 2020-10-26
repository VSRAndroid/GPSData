package com.asd.vikrant.zypp.backgroundservices;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.asd.vikrant.zypp.activity.HomeActivity;
import com.asd.vikrant.zypp.R;
import com.asd.vikrant.zypp.database.DatabaseHelper;

public class MyBackgroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    PendingIntent pendingIntent;
    Handler mHandler = new Handler();
    Boolean activeThread = true;
    private DatabaseHelper db;
    double latitude , longitude;
    Float  direction;
    String ride;
    long interval = 100;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // this.context = context;
        db = new DatabaseHelper(this);
        String input = intent.getStringExtra("inputExtra");
        ride = intent.getStringExtra("ride");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        rideDataSaveInDB();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Ride start.............")
                .setContentText(input)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
        //do heavy work on a background thread
        //stopSelf();
        return START_NOT_STICKY;
    }

    private void rideDataSaveInDB() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub

                while (activeThread) {
                    try {
                        Thread.sleep(interval);
                        mHandler.post(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                if (activeThread) {

                                    //if connected to wifi or mobile data plan
                                    GPSTracker gps = new GPSTracker(getApplicationContext());
                                    if (gps.canGetLocation()) {
                                             latitude = gps.getLatitude();
                                             longitude = gps.getLongitude();
                                             direction = gps.direction;
                                        Toast.makeText(getApplicationContext(), "Latitude" + latitude + ", " + "Longitude" + longitude , Toast.LENGTH_SHORT).show();
                                        Log.d("value", "latlong" + latitude + ", " + longitude);
                                        saveGPSDataLocalStorage(String.valueOf(latitude),
                                                String.valueOf(longitude),
                                                ride,
                                                String.valueOf(direction),
                                                String.valueOf(System.currentTimeMillis()), 0);
                                    }

                                    interval = 10000;
                                }

                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        activeThread = false;
        super.onDestroy();
        Toast.makeText(this, "", Toast.LENGTH_LONG).show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void saveGPSDataLocalStorage(String latitude, String longitude, String ride, String direction, String currentTime, int status) {
        db.addGpsData(latitude, longitude, ride, direction, currentTime, status);
    }

}
