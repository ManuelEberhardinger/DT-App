package com.example.sven.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Button startButton;
    private Button searchButton;
    private BluetoothSocket btSocket;
    private ConnectThread connectThread;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try
        {
            btSocket = getBTSocket();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //connectThread = new ConnectThread(btSocket);

        startButton = (Button)findViewById(R.id.startButton);
        searchButton = (Button)findViewById(R.id.searchButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchParkSpaceViaWifi();
            }
        });

    }


    private void searchParkSpaceViaWifi()
    {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try
                {
                    Socket socket = new Socket("172.16.9.138",8000);
                    DataOutputStream DOS = new DataOutputStream(socket.getOutputStream());
                    DOS.writeUTF("SEARCH");
                    socket.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startParkingViaWifi()
    {
        try
        {
            Socket socket = new Socket("172.16.9.138",1755);
            DataOutputStream DOS = new DataOutputStream(socket.getOutputStream());
            DOS.writeBytes("START");
            socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    private BluetoothSocket getBTSocket() throws IOException{
        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice btDevice;
        if (blueAdapter != null) {
            if (blueAdapter.isEnabled()) {
                Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();

                if (bondedDevices.size() > 0) {
                    Object[] devices = (Object[]) bondedDevices.toArray();
                    for(int i = 0; i < devices.length; i++)
                    {
                        if(devices[i] instanceof BluetoothDevice) {
                            BluetoothDevice tmpDevice = (BluetoothDevice) devices[i];
                            //B8:27:EB:97:43:B6
                            String macAdress = tmpDevice.getAddress();
                            if(tmpDevice.getAddress().equals("B8:27:EB:97:43:B6"))
                            {
                                btDevice = tmpDevice;
                                ParcelUuid[] uuids = btDevice.getUuids();
                                BluetoothSocket socket = btDevice.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                                return socket;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }


    public void search()
    {
        connectThread.write("SEARCH");
    }


    public void start()
    {
        connectThread.write("START");
    }

    @Override
    protected void onDestroy()
    {
        connectThread.cancel();
    }



}
