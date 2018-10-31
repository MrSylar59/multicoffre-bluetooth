package fr.centralelille.ig2i.multicoffrebluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class PairedDeviceAdapter extends RecyclerView.Adapter<PairedDeviceAdapter.DeviceViewHolder> {

    private static final String TAG = PairedDeviceAdapter.class.getSimpleName();

    private final ListItemClickListener mOnClickListener;

    private List<BluetoothDevice> pairedDevices;
    private int mViewHolderCount;
    private int mNumberOfItems;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public PairedDeviceAdapter(int numItem, List<BluetoothDevice> pDevices, ListItemClickListener listener){
        mViewHolderCount = 0;
        mOnClickListener = listener;
        mNumberOfItems = numItem;
        pairedDevices = pDevices;
        Log.d(TAG, pairedDevices.toString());
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutID = R.layout.bluetooth_device_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachParentNow = false;

        View view = inflater.inflate(layoutID, viewGroup, shouldAttachParentNow);
        DeviceViewHolder viewHolder = new DeviceViewHolder(view);

        mViewHolderCount++;
        Log.d(TAG, "Created View number "+mViewHolderCount);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int pos) {
        Log.d(TAG, "Pos: "+ pos +" "+pairedDevices.get(pos).getName()+" : "+pairedDevices.get(pos).getAddress());
        holder.bind(pairedDevices.get(pos).getName(), pairedDevices.get(pos).getAddress());
    }

    @Override
    public int getItemCount() {
        return mNumberOfItems;
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        TextView listDeviceName, listDeviceAddr;

        public DeviceViewHolder(View deviceView){
            super(deviceView);

            listDeviceName = deviceView.findViewById(R.id.tv_device_name);
            listDeviceAddr = deviceView.findViewById(R.id.tv_device_addr);

            deviceView.setOnClickListener(this);
        }

        void bind(String name, String addr){
            listDeviceName.setText(name);
            listDeviceAddr.setText(addr);
        }

        @Override
        public void onClick(View v) {
            int clickedPos = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPos);
        }
    }
}