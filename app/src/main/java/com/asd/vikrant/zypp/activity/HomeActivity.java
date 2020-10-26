package com.asd.vikrant.zypp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.asd.vikrant.zypp.R;
import com.asd.vikrant.zypp.backgroundservices.GPSTracker;
import com.asd.vikrant.zypp.backgroundservices.MyBackgroundService;
import com.asd.vikrant.zypp.custom.PrefManager;
import com.asd.vikrant.zypp.custom.ShowTimer;
import com.asd.vikrant.zypp.dao.RideGpsData;
import com.asd.vikrant.zypp.database.DatabaseHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Button start_btn, history_btn;
    private TextView timerText;
    private Boolean checkRide;
    private PrefManager prefManager;
    private int ACCESS_FINE_LOCATION = 1;
    private DatabaseHelper db;
    double lat ;
    double longi;
    int ride_count =0;
    List<RideGpsData> gpsDataList;
    Handler mHandler = new Handler();
    Boolean activeThread = true;
    GoogleMap googleMap;
    Polyline polyline;
    Marker mCurrLocationMarker;
    Marker mStartLocationMarker;
    ShowTimer showTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefManager = new PrefManager(getApplicationContext());
        db = new DatabaseHelper(this);
        showTimer = new ShowTimer();
        requestPermission();
        init();

    }

    // start your ride click on start btn..........
    private void init() {
        start_btn = findViewById(R.id.start_btn);
        history_btn = findViewById(R.id.history_btn);
        timerText = findViewById(R.id.timerText);
        showTimer.StopTimer(timerText);

        if(prefManager.getData("check").equalsIgnoreCase("true")){
            prefManager.saveData("check",prefManager.getData("check"));
            start_btn.setText(getString(R.string.end_ride));
        }
        else{

            prefManager.saveData("check",prefManager.getData("check"));
           // start_btn.setText(getString(R.string.end_ride));
        }

        // start your ride......................
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(prefManager.getData("check").equalsIgnoreCase(""))
                {
                    prefManager.saveData("check","true");
                    start_btn.setText(getString(R.string.start));
                }
                if(prefManager.getData("check").equalsIgnoreCase("true")
                        && start_btn.getText().toString().equalsIgnoreCase(getString(R.string.start)))
                {
                    if(prefManager.getData("ride_no").equalsIgnoreCase(""))
                    {
                        prefManager.saveData("ride_no", "0");
                    }
                    else
                    {
                        ride_count = Integer.parseInt(prefManager.getData("ride_no")) + 1 ;
                        prefManager.saveData("ride_no", String.valueOf(ride_count));
                    }

                    start_btn.setText(getString(R.string.end_ride));
                    Intent serviceIntent = new Intent(getApplicationContext(), MyBackgroundService.class);
                    serviceIntent.putExtra("ride", "Ride" + ride_count);
                    serviceIntent.putExtra("inputExtra", "If you are completed your ride then press end ride button.");
                    ContextCompat.startForegroundService(Objects.requireNonNull(getApplicationContext()), serviceIntent);
                    prefManager.saveData("check","true");
                    showTimer.StartTimer(timerText);
                    activeThread = true;
                     /*If already exits previous ride polyline and marker............
                     so firstly remove it.......*/
                    if(polyline!=null )
                    {
                        polyline.remove();
                    }
                    if(mCurrLocationMarker!=null)
                    {
                        mCurrLocationMarker.remove();
                    }
                    getCurrentRideData();
                }

                else{
                    Intent stopServiceIntent = new Intent(getApplicationContext(), MyBackgroundService.class);
                    Objects.requireNonNull(getApplicationContext()).stopService(stopServiceIntent);
                    prefManager.saveData("check","true");
                    start_btn.setText(getString(R.string.start));
                    showTimer.StopTimer(timerText);
                    activeThread = false;
                }

            }
        });

        // all ride list find here...................
        history_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), RideHistoryListActivity.class);
                startActivity(intent);
            }
        });
    }

    // request app location permission .....................
    private void requestPermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                &&ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_FINE_LOCATION);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == ACCESS_FINE_LOCATION)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Permission granted successfully", Toast.LENGTH_SHORT).show();
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                assert mapFragment != null;
                mapFragment.getMapAsync(this);

            }
            else
            {
                Toast.makeText(this, "Permission is denied!", Toast.LENGTH_SHORT).show();
                finish();
                boolean showRationale = shouldShowRequestPermissionRationale( Manifest.permission.ACCESS_FINE_LOCATION );
                boolean showRationale2 = shouldShowRequestPermissionRationale( Manifest.permission.ACCESS_COARSE_LOCATION );
                if (!showRationale && !showRationale2) {
                    openSettingsDialog();
                }
            }
        }
    }

    // required permissions settings ...................
    private void openSettingsDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Required Permissions");
        builder.setMessage("This app require permission to use awesome feature. Grant them in app settings.");
        builder.setPositiveButton("Take Me To SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 101);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onMapReady(GoogleMap map) {
          //  Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        googleMap = map;
        GPSTracker gps = new GPSTracker(getApplicationContext());
        if (gps.canGetLocation()) {
                lat = gps.getLatitude();
                longi = gps.getLongitude();
//                updateLocation();
                LatLng vehiclePoint = new LatLng(lat, longi);
                CameraPosition googlePlex = CameraPosition.builder().target(new LatLng(lat, longi))
                        .bearing(45)
                        .zoom(18)
                        .tilt(0)
                        .build();

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 100, null);
                mStartLocationMarker = googleMap.addMarker(new MarkerOptions().position(vehiclePoint).title("Current"));
                mStartLocationMarker.showInfoWindow();
            //showLocation.setText("Your Location: " + "\n" + "Latitude: " + latitude + "\n" + "Longitude: " + longitude);
            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }


    }

    // get current ride data.........................
     private void getCurrentRideData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub

                while (activeThread) {
                    try {
                        Thread.sleep(11000);
                        mHandler.post(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                if (activeThread) {
                                    getLocationByDb();
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

    private void getLocationByDb() {
            gpsDataList = new ArrayList<>();
            Cursor cursor = db.getUnSyncedData("Ride" + ride_count);

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    try {
                        RideGpsData rideGpsData = new RideGpsData(cursor.getString(cursor.getColumnIndex(DatabaseHelper.LATITUDE)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.LONGITUDE)));
                        gpsDataList.add(rideGpsData);
                        genratePolyLine();
                        cursor.moveToNext();

                    }

                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
    }

    private void genratePolyLine() {
        //  get ride start and point.........................
        List<LatLng>coordinateList = new ArrayList<>();
        LatLng startPoint = new LatLng(Double.valueOf(gpsDataList.get(0).getLat()),Double.valueOf(gpsDataList.get(0).getLng()));
        LatLng endPoint = new LatLng(Double.valueOf(gpsDataList.get(gpsDataList.size()-1).getLat()), Double.valueOf(gpsDataList.get(gpsDataList.size()-1).getLng()));
        CameraPosition googlePlex = CameraPosition.builder()
                .target(new LatLng(Double.valueOf(gpsDataList.get(gpsDataList.size()-1).getLat()),
                Double.valueOf(gpsDataList.get(gpsDataList.size()-1).getLng())))
                .bearing(45)
                .zoom(18)
                .tilt(0)
                .build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 100, null);
        // set start and current position in map..........................
        if(mStartLocationMarker!=null)
        {
            mStartLocationMarker.remove();
        }
        mStartLocationMarker = googleMap.addMarker(new MarkerOptions().position(startPoint).title("Start"));
        mStartLocationMarker.showInfoWindow();
        if(mCurrLocationMarker!=null)
        {
            mCurrLocationMarker.remove();
        }
        mCurrLocationMarker = googleMap.addMarker(new MarkerOptions().position(endPoint).title("current"));
        mCurrLocationMarker.showInfoWindow();
        for(int v =0; v< gpsDataList.size(); v++)
        {
            coordinateList.add(new LatLng(Double.valueOf(gpsDataList.get(v).getLat()), Double.valueOf(gpsDataList.get(v).getLng())));
        }
         // create track start point to end point.............
        if(polyline!=null)
        {
            polyline.remove();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        List<LatLng> latLngList = new ArrayList<>();
        latLngList.addAll(coordinateList);
        polyOptions.color(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        polyOptions.addAll(latLngList);
        polyline = googleMap.addPolyline(polyOptions);
    }

}
