package com.pos.billingapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pos.billingapp.R;
import com.pos.billingapp.callback.OnSelectDeviceListener;
import com.pos.billingapp.constants.Constants;
import com.pos.poslib.model.DeviceDetails;

import java.util.List;

/**
 * Adapter class for displaying a list of scanned devices in a RecyclerView.
 */
public class ScanDevicesAdapter extends RecyclerView.Adapter<ScanDevicesAdapter.ScanViewHolder> {
    private static final String TAG = "ScanDevicesAdapter";
    private LayoutInflater inflater;
    private List<DeviceDetails> listdata;
    private OnSelectDeviceListener selectDeviceListener;

    /**
     * Constructor to create an instance of ScanDevicesAdapter.
     *
     * @param ctx              The context of the adapter.
     * @param listdata         The list of DeviceDetails objects to display.
     * @param listener         The listener for device selection events.
     */
    public ScanDevicesAdapter(Context ctx, List<DeviceDetails> listdata, OnSelectDeviceListener listener) {
        Log.d(TAG , "Entering ScanDevicesAdapter");
        inflater = LayoutInflater.from(ctx);
        this.listdata = listdata;
        this.selectDeviceListener = listener;
        Log.d(TAG,"Exiting ScanDevicesAdapter");
    }

    /**
     * Called when the RecyclerView needs a new ViewHolder to represent an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it's bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ScanViewHolder that holds a View representing an item of the adapter.
     */
    @Override
    public ScanDevicesAdapter.ScanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.device_item_list_layout, parent, false);
        return new ScanViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the item at the given position.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ScanDevicesAdapter.ScanViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Log.d(TAG ,"Entering onBindViewHolder");
        final int pos = position;
        Log.i(TAG,"Binding position: " + position);
        if (listdata.get(position).isBtDevice()) {
            holder.name.setText(Constants.DEVICE_NAME + listdata.get(position).getBtDeviceName());
            holder.ssidBtTv.setText(Constants.BT_SSID + listdata.get(position).getBtDeviceSsid());
        } else {
            holder.name.setText(Constants.DEVICE_SLNO + listdata.get(position).getDeviceSlNo());
            holder.ssidBtTv.setText(Constants.DEVICE_ID + listdata.get(position).getDeviceId());
        }

        holder.cardView.setOnClickListener(view -> {
            Toast.makeText(view.getContext(), "Item Selected!!", Toast.LENGTH_LONG).show();
            Log.i(TAG,"Item selected at position: " + pos);
            selectDeviceListener.onSuccess(pos, listdata);
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in the adapter's data set.
     */
    @Override
    public int getItemCount() {
        return listdata.size();
    }


    /**
     * ViewHolder class for holding the views of each item in the RecyclerView.
     */
    public static class ScanViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView ssidBtTv;
        CardView cardView;

        public ScanViewHolder(View itemView) {
            super(itemView);

            this.name = itemView.findViewById(R.id.name);
            this.ssidBtTv = itemView.findViewById(R.id.ssidbt);
            cardView = itemView.findViewById(R.id.cardView);

        }

    }
}