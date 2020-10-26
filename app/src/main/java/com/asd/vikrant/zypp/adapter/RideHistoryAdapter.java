package com.asd.vikrant.zypp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.asd.vikrant.zypp.R;
import com.asd.vikrant.zypp.activity.RideHistoryDetailsActivity;
import com.asd.vikrant.zypp.dao.RideDataList;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RideHistoryAdapter extends RecyclerView.Adapter<RideHistoryAdapter.MyViewHolder> {

    Context applicationContext;
    List<RideDataList> filter_rideDataListList;
    DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
    public RideHistoryAdapter(Context applicationContext, List<RideDataList> filter_rideDataListList) {
        this.applicationContext = applicationContext;
        this.filter_rideDataListList = filter_rideDataListList;
    }


    @NonNull
    @Override
    public RideHistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ride_history_item, parent, false);
        return new RideHistoryAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RideHistoryAdapter.MyViewHolder holder, final int position) {

        holder.rideNameText.setText(filter_rideDataListList.get(position).getRide_name());
        Date start_date = new Date(Long.parseLong(filter_rideDataListList.get(position).getRide_start()));
        Date end_date = new Date(Long.parseLong(filter_rideDataListList.get(position).getRide_end()));
        holder.rideStartText.setText("Ride Start: "+ simple.format(start_date));
        holder.rideEndText.setText("Ride End: "+ simple.format(end_date));

        holder.distanceText.setText("Distance: "+filter_rideDataListList.get(position).getDistance()+"km.");
        holder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(applicationContext, RideHistoryDetailsActivity.class);
                intent.putExtra("ride_name", filter_rideDataListList.get(position).getRide_name());
                intent.putExtra("startTime", filter_rideDataListList.get(position).getRide_start());
                intent.putExtra("endTime", filter_rideDataListList.get(position).getRide_end());
                intent.putExtra("distance", filter_rideDataListList.get(position).getDistance());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                applicationContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filter_rideDataListList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
         TextView rideNameText, rideStartText, rideEndText, distanceText;
         RelativeLayout main_layout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rideNameText = itemView.findViewById(R.id.rideNameText);
            rideStartText = itemView.findViewById(R.id.rideStartText);
            rideEndText = itemView.findViewById(R.id.rideEndText);
            distanceText = itemView.findViewById(R.id.distanceText);
            main_layout = itemView.findViewById(R.id.main_layout);
        }
    }
}
