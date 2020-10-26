package com.asd.vikrant.zypp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import com.asd.vikrant.zypp.R;
import com.asd.vikrant.zypp.backgroundservices.GPSTracker;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RideHistoryDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    TextView rideNameText, rideStartText, rideEndText, distanceText;
    GoogleMap googleMap;
    Polyline polyline;
    Marker mCurrLocationMarker;
    Marker mStartLocationMarker;
    String rideName, startTime, endTime, distance;
    List<RideGpsData> gpsDataList;
    DatabaseHelper db;
    DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_history_details);
        db = new DatabaseHelper(this);
        rideName = getIntent().getStringExtra("ride_name");
        startTime = getIntent().getStringExtra("startTime");
        endTime = getIntent().getStringExtra("endTime");
        distance = getIntent().getStringExtra("distance");
        init();
    }

    // init view here.................
    private void init() {

        rideNameText = findViewById(R.id.rideNameText);
        rideStartText =findViewById(R.id.rideStartText);
        rideEndText = findViewById(R.id.rideEndText);
        distanceText = findViewById(R.id.distanceText);
        rideNameText.setText(rideName);
        Date start_date = new Date(Long.parseLong(startTime));
        Date end_date = new Date(Long.parseLong(endTime));
        rideStartText.setText("Ride Start: "+ simple.format(start_date));
        rideEndText.setText("Ride End: "+ simple.format(end_date));
        distanceText.setText("Distance: "+distance+"km.");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        getRideHistoryPath();
    }

    /* showing ride path in map ......
     start point to end point...............*/
    private void getRideHistoryPath() {
        gpsDataList = new ArrayList<>();
        Cursor cursor = db.getUnSyncedData(rideName);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                try {
                    RideGpsData rideGpsData = new RideGpsData(cursor.getString(cursor.getColumnIndex(DatabaseHelper.LATITUDE)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.LONGITUDE)));
                    gpsDataList.add(rideGpsData);
                    cursor.moveToNext();
                }

                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    /* showing start , end and track path in map.................*/
    private void genratePolyLine() {
        List<LatLng> coordinateList = new ArrayList<>();

        LatLng startPoint = new LatLng(Double.valueOf(gpsDataList.get(0).getLat()),
                Double.valueOf(gpsDataList.get(0).getLng()));
        LatLng endPoint = new LatLng(Double.valueOf(gpsDataList.get(gpsDataList.size()-1).getLat()),
                Double.valueOf(gpsDataList.get(gpsDataList.size()-1).getLng()));
        CameraPosition googlePlex = CameraPosition.builder()
                .target(new LatLng(Double.valueOf(gpsDataList.get(gpsDataList.size()-1).getLat()),
                        Double.valueOf(gpsDataList.get(gpsDataList.size()-1).getLng())))
                .bearing(45)
                .zoom(18)
                .tilt(0)
                .build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 100, null);
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
        mCurrLocationMarker = googleMap.addMarker(new MarkerOptions().position(endPoint).title("End"));
        mCurrLocationMarker.showInfoWindow();
        for(int v =0; v< gpsDataList.size(); v++)
        {
            coordinateList.add(new LatLng(Double.valueOf(gpsDataList.get(v).getLat()), Double.valueOf(gpsDataList.get(v).getLng())));
        }

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

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        // create polyline in map.................
        genratePolyLine();
    }
}
