package com.example.minazakaria.thirdaid;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Mina Zakaria on 4/10/2018.
 */

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.DirectoryViewHolder> {

    private Context mCtx;
    private List<DirectoryObject> directoryObjectList;

    public DirectoryAdapter(Context mCtx, List<DirectoryObject> directoryObjectList) {
        this.mCtx = mCtx;
        this.directoryObjectList = directoryObjectList;
    }



    @Override
    public DirectoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_layout, null);
        DirectoryViewHolder holder = new DirectoryViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final DirectoryViewHolder holder, int position) {
        final DirectoryObject directoryObject = directoryObjectList.get(position);
        holder.title.setText(directoryObject.getTitle());
        holder.desc.setText(directoryObject.getDesc());
        holder.p1.setText(directoryObject.getPhoneNumDisplay());
        holder.callNow.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + directoryObject.getPhoneNum()));
                Log.e("INTENT", "THIS IS WORKING");
                mCtx.startActivity(intent);
            }
        });

        holder.GetDir.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/search/?api=1&query=30.5, 30.2"));
                if(intent.resolveActivity(mCtx.getPackageManager()) != null){
                    mCtx.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return directoryObjectList.size();
    }

    class DirectoryViewHolder extends RecyclerView.ViewHolder {

        TextView title, desc, p1;
        ImageView callNow, GetDir;

        public DirectoryViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textViewTitle);
            desc = itemView.findViewById(R.id.textViewName);
            p1 = itemView.findViewById(R.id.textViewNum1);
            callNow = itemView.findViewById(R.id.callNowButton);
            GetDir = itemView.findViewById(R.id.get_directions_button);

        }
    }

}
