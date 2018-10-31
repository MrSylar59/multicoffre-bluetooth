package fr.centralelille.ig2i.multicoffrebluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements PairedDeviceAdapter.ListItemClickListener {

    private static final int REQUEST_ENABLE_BT = 1;

    private RecyclerView mDeviceList;
    private PairedDeviceAdapter mDeviceAdapter;

    BluetoothAdapter mBlutoothAdapter;
    Set<BluetoothDevice> pairedDevices;
    List<BluetoothDevice> listDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDeviceList = findViewById(R.id.rv_paired_bluetooth);

        // First getting the bluetooth adapter
        mBlutoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBlutoothAdapter == null){
            Toast.makeText(this, "Votre appareil ne supporte pas le Bluetooth",
                    Toast.LENGTH_LONG).show();
        }
        else {
            if (!mBlutoothAdapter.isEnabled()){
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
            }

            pairedDevices = mBlutoothAdapter.getBondedDevices();
            int numDevices = pairedDevices.size();

            listDevices = new ArrayList<>(pairedDevices);

            if (numDevices > 0){
                mDeviceAdapter = new PairedDeviceAdapter(numDevices, listDevices, this);
                LinearLayoutManager linearLayout = new LinearLayoutManager(this);

                mDeviceList.setLayoutManager(linearLayout);
                mDeviceList.setHasFixedSize(true);
                mDeviceList.setAdapter(mDeviceAdapter);
            }
            else
                Toast.makeText(this, "Aucun appareil Bluetooth n'est apparaillé",
                        Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_ENABLE_BT){
            if (resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Vous devez activer le Bluetooth pour utiliser cette app",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Intent controlActivity = new Intent(MainActivity.this, ControlActivity.class);
        controlActivity.putExtra("BtDevice", listDevices.get(clickedItemIndex));
        startActivity(controlActivity);
    }
}
