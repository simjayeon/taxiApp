package com.ossw.taxiapp.historyRecyclerView;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ossw.taxiapp.R;

public class HistoryViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView rideId;
    public HistoryViewHolders(View itemView){
        super(itemView);
        itemView.setOnClickListener(this);

        rideId = (TextView)itemView.findViewById(R.id.rideId);
    }

    @Override
    public void onClick(View v){

    }

}