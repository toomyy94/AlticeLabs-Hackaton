package tomasrodrigues.pt.alticelabs.parking;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import tomasrodrigues.pt.alticelabs.R;
import tomasrodrigues.pt.alticelabs.utils.DateUtils;
import tomasrodrigues.pt.alticelabs.views.ParkingList;
import tomasrodrigues.pt.alticelabs.views.ParkingMap;

/**
 * Created by Tomás Rodrigues on 21/02/2018.
 */

public class ParkingListAdapter extends ArrayAdapter<ParkingPlace> implements View.OnClickListener{

    private ArrayList<ParkingPlace> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        ImageView car;
        TextView txtIsReserved;
        TextView txtIsFree;
        TextView txtTimestamp;
    }

    public ParkingListAdapter(ArrayList<ParkingPlace> data, Context context) {
        super(context, R.layout.park_list, data);
        this.dataSet = data;
        this.mContext=context;
    }

    public void refreshEvents(ArrayList<ParkingPlace> events) {
        this.dataSet.clear();
        this.dataSet.addAll(events);
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        final ParkingPlace parkingPlace = (ParkingPlace) object;

        switch (v.getId())
        {
            case R.id.available_parks:
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ParkingPlace parkingPlace = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.park_list, parent, false);
            viewHolder.txtIsReserved = convertView.findViewById(R.id.item_txtReserved);
            viewHolder.car = convertView.findViewById(R.id.item_icon);
            viewHolder.txtIsFree = convertView.findViewById(R.id.item_txtOcuppied);
            viewHolder.txtTimestamp = convertView.findViewById(R.id.item_txtTimestamp);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

//        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtIsReserved.setText(textToListReserved(parkingPlace.isReserved()));
        viewHolder.car.setOnClickListener(this);
        viewHolder.car.setTag(position);
        viewHolder.txtIsFree.setText(textToListFree(parkingPlace.isFree()));
        if (parkingPlace.getTimestamp() != 0L) viewHolder.txtTimestamp.setText(DateUtils.convertDateToHoursMinutes(new Date(parkingPlace.getTimestamp())));

        // Return the completed view to render on screen
        return convertView;
    }

    public String textToListReserved(boolean isReserved)
    {
        String reserved = (isReserved) ? "Reservado" : "Público"; return reserved;
    }

    public String textToListFree(boolean isFree)
    {
        String free = (isFree) ? "Livre!" : "Ocupado"; return free;
    }
}
