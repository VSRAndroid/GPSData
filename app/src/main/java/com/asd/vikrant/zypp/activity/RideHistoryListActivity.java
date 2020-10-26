package com.asd.vikrant.zypp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.asd.vikrant.zypp.R;
import com.asd.vikrant.zypp.adapter.RideHistoryAdapter;
import com.asd.vikrant.zypp.dao.RideDataList;
import com.asd.vikrant.zypp.database.DatabaseHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class RideHistoryListActivity extends AppCompatActivity {
    private DatabaseHelper db;
    List<RideDataList> rideDataListList;
    List<RideDataList> filter_rideDataListList;
    List<String> rideList;
    TextView noRideText;
    String  rideName, startTime, endTime, distance;
    RecyclerView ride_recyclerView;
    RideHistoryAdapter rideHistoryAdapter;
    int count = 0;
    Double distanceRide=0.00;
    DecimalFormat decimalFormat = new DecimalFormat("##.00");
    Boolean checkCount = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_history_list);
        db = new DatabaseHelper(this);
        init();
        getRideDBData();
    }

    // init view here.................
    private void init() {
        ride_recyclerView = findViewById(R.id.ride_recyclerView);
        noRideText = findViewById(R.id.noRideText);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        ride_recyclerView.setLayoutManager(mLayoutManager);
    }

    // get all ride data ...........
    private void getRideDBData() {

        Cursor cursor = db.getride();
        rideDataListList = new ArrayList<>();
        rideList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                try {
                    rideList.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.RIDE_NO)));
                    cursor.moveToNext();
                }

                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }

        Cursor cursor2 = db.getData();
        if (cursor2.moveToFirst()) {
            while (!cursor2.isAfterLast()) {
                try {

                    RideDataList rideGpsData = new RideDataList(
                            cursor2.getString(cursor2.getColumnIndex(DatabaseHelper.RIDE_NO)),
                            cursor2.getString(cursor2.getColumnIndex(DatabaseHelper.CURRENT_TIME)),
                            cursor2.getString(cursor2.getColumnIndex(DatabaseHelper.CURRENT_TIME)),
                            cursor2.getString(cursor2.getColumnIndex(DatabaseHelper.LATITUDE)),
                            cursor2.getString(cursor2.getColumnIndex(DatabaseHelper.LONGITUDE)));

                    rideDataListList.add(rideGpsData);
                    cursor2.moveToNext();

                }

                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }

        getRideListData();

        if(rideList.size()==0)
        {
            noRideText.setVisibility(View.VISIBLE);
        }
        else{
            noRideText.setVisibility(View.GONE);
        }

    }

    // showing ride data in list.......

    private void getRideListData() {
        filter_rideDataListList = new ArrayList<>();
        for(int i = 0; i < rideList.size(); i++)
        {
            checkCount = true;
            for(int v = 0 ; v < rideDataListList.size(); v ++)
            {
                if(rideList.get(i).toString().equalsIgnoreCase(rideDataListList.get(v).getRide_name())) {
                    if(checkCount)
                    {
                        if(i == 0){
                            count = 0;
                        }
                        else{
                            count = v;
                        }
                    }

                    checkCount = false;
                    rideName = rideDataListList.get(v).getRide_name();
                    startTime = rideDataListList.get(count).getRide_start();
                    endTime = rideDataListList.get(v).getRide_end();

                    if(v!=0)
                    {
                        distanceMethod(Double.valueOf(rideDataListList.get(v-1).getLat()),
                                Double.valueOf(rideDataListList.get(v-1).getLng()),
                                Double.valueOf(rideDataListList.get(v).getLat()),
                                Double.valueOf(rideDataListList.get(v).getLng())
                                );
                    }
                }

            }

            RideDataList rideGpsData2 = new RideDataList(rideName, startTime, endTime, distance);
            filter_rideDataListList.add(rideGpsData2);
            Log.v("DataList","value"+filter_rideDataListList.size());
        }

        rideHistoryAdapter = new RideHistoryAdapter(getApplicationContext(), filter_rideDataListList);
        ride_recyclerView.setAdapter(rideHistoryAdapter);
    }

    // calculate distance.......................

    private void distanceMethod(double latStart, double lonStart, double latEnd, double lonEnd) {
        Location startPoint=new Location("Starting");
        startPoint.setLatitude(latStart);
        startPoint.setLongitude(lonStart);
        Location endPoint=new Location("Ending");
        endPoint.setLatitude(latEnd);
        endPoint.setLongitude(lonEnd);
        double distanceFirst = startPoint.distanceTo(endPoint);
        distanceRide = distanceRide + distanceFirst;
        distance = String.valueOf(decimalFormat.format(distanceRide * 0.001));
    }

}
