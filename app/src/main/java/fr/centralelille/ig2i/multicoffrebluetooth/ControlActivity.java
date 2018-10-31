package fr.centralelille.ig2i.multicoffrebluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ControlActivity extends AppCompatActivity {

    private static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = ControlActivity.class.getSimpleName();

    TextView mNameTextView;
    Button mBtnOpen, mBtnClose;

    Handler mHandler;

    BluetoothDevice mBluetoothDevice;
    BluetoothSocket mSocket;
    ConnectedThread mBluetoothConnection;

    private interface MessageConstants {
        int MESSAGE_READ= 0;
        int MESSAGE_WRITE = 1;
        int MESSAGE_TOAST = 2;
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInputStream;
        private final OutputStream mmOutputStream;
        private byte[] mmBuffer;

        public ConnectedThread(BluetoothSocket socket){
            mmSocket = socket;
            InputStream tmpI = null;
            OutputStream tmpO = null;

            try{
                tmpI = mmSocket.getInputStream();
            }catch(IOException e){
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try{
                tmpO = mmSocket.getOutputStream();
            }catch(IOException e){
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInputStream = tmpI;
            mmOutputStream = tmpO;
        }

        public void run(){
            mmBuffer = new byte[1024];
            int numBytes;

            try{
                mmSocket.connect();
            } catch(IOException e){
                Log.e(TAG, "Error while connecting to socket", e);
            }

            /*while (true){
                try{
                    numBytes = mmInputStream.read(mmBuffer);
                    Message readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_READ,
                            numBytes,
                            -1,
                            mmBuffer
                    );
                    readMsg.sendToTarget();
                }catch (IOException e){
                    Log.e(TAG, "Input stream was disconnected", e);
                    break;
                }
            }*/
        }

        public void write(byte[] bytes){
            try{
                mmOutputStream.write(bytes);
            }catch (IOException e){
                Log.e(TAG, "Error occurred while sending data", e);
            }
        }

        public void cancel(){
            try {
                mmSocket.close();
            }catch (IOException e){
                Log.e(TAG, "Couldn' close the opened socket", e);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        mNameTextView = findViewById(R.id.tv_device_name2);
        mBtnOpen = findViewById(R.id.btn_open);
        mBtnClose = findViewById(R.id.btn_close);

        Intent startingIntent = getIntent();

        if (startingIntent.hasExtra("BtDevice")){
            if (startingIntent.getExtras() != null)
                mBluetoothDevice = startingIntent.getExtras().getParcelable("BtDevice");
            else{
                Toast.makeText(this, "Aucun appareil n'a été passé", Toast.LENGTH_LONG)
                        .show();
                finish();
            }

            if (mBluetoothDevice != null){
                mNameTextView.setText(mBluetoothDevice.getName());

                try{
                    // On créer le socket contenant notre connexion ici
                    mSocket = mBluetoothDevice.createInsecureRfcommSocketToServiceRecord(mUUID);

                    if (mSocket != null){
                        mBluetoothConnection = new ConnectedThread(mSocket);
                        mBluetoothConnection.run();

                        Toast.makeText(this, "Connexion avec "+
                                mBluetoothDevice.getName()+" établie", Toast.LENGTH_LONG).show();
                    }

                } catch (IOException e){
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(this, "Aucun appareil n'a été passé", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
        }
        else{
            Toast.makeText(this, "Aucun appareil n'a été passé", Toast.LENGTH_LONG)
                    .show();
            finish();
        }

        mBtnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBluetoothConnection != null){
                    mBluetoothConnection.write("1".getBytes());
                }
            }
        });

        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBluetoothConnection != null){
                    mBluetoothConnection.write("0".getBytes());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mBluetoothConnection != null) {
            mBluetoothConnection.write("q".getBytes());
            mBluetoothConnection.cancel();

            Toast.makeText(this, "Connexion à "+mBluetoothDevice.getName()+" terminée",
                    Toast.LENGTH_LONG).show();
        }

        super.onDestroy();
    }
}
