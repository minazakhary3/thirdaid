package com.example.minazakaria.thirdaid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Mina Zakaria on 4/16/2018.
 */

public class EmergencyAdapter extends RecyclerView.Adapter<EmergencyAdapter.EmergencyViewHolder>{
    private Context mCtx;
    private List<EmergencyObject> emergencyObjectList;

    public EmergencyAdapter(Context mCtx, List<EmergencyObject> emergencyObjectList) {
        this.mCtx = mCtx;
        this.emergencyObjectList = emergencyObjectList;
    }

    @Override
    public EmergencyAdapter.EmergencyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.emergency_layout, null);
        EmergencyAdapter.EmergencyViewHolder holder = new EmergencyAdapter.EmergencyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final EmergencyAdapter.EmergencyViewHolder holder, int position) {
        final EmergencyObject emergencyObject = emergencyObjectList.get(position);

        holder.distance.setText(emergencyObject.getDistance() + " KM");
        holder.date.setText("Reported at: " + emergencyObject.getTime());
        holder.description.setText(emergencyObject.getDescription());
        holder.type.setText(emergencyObject.getType());

        holder.getDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/search/?api=1&query=" + emergencyObject.getLocation().latitude + ", " + emergencyObject.getLocation().longitude));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if(intent.resolveActivity(mCtx.getPackageManager()) != null){
                    mCtx.startActivity(intent);
                }
            }
        });

        holder.moreDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, emergencyViewActivity.class);
                intent.putExtra("type", emergencyObject.type);
                intent.putExtra("details", emergencyObject.description);
                intent.putExtra("reporter", emergencyObject.fullName);
                intent.putExtra("lat", emergencyObject.getLocation().latitude);
                intent.putExtra("lng", emergencyObject.getLocation().longitude);
                intent.putExtra("pnum", emergencyObject.pnum);
                intent.putExtra("time", emergencyObject.time);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mCtx.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return emergencyObjectList.size();
    }

    class EmergencyViewHolder extends RecyclerView.ViewHolder {

        TextView type, description, date, distance;
        ImageView moreDetails, getDirections;

        public EmergencyViewHolder(View itemView) {
            super(itemView);

            type = itemView.findViewById(R.id.emergencyTitle);
            description = itemView.findViewById(R.id.emergencyDetails);
            date = itemView.findViewById(R.id.emergencyDate);
            distance = itemView.findViewById(R.id.emergencyDistance);
            moreDetails = itemView.findViewById(R.id.viewMoreButton);
            getDirections = itemView.findViewById(R.id.getDirectionsButton);

        }
    }
}
