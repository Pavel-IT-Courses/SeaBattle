package com.gmail.pavkascool.update.delegates;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ConnectionModel extends AndroidViewModel {

    private BluetoothAdapter bluetoothAdapter;
    private Application application;
    private BroadcastReceiver mReceiver;
    private List<String> newDevices = new ArrayList<>();

    public ConnectionModel(@NonNull Application application) {
        super(application);
        this.application = application;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private MutableLiveData<List<String>> liveData;

    public LiveData<List<String>> getData() {
        if(liveData == null) {
            liveData = new MutableLiveData<>();
            updateConnections();
        }
        return liveData;
    }
    public void updateConnections() {
        Set<BluetoothDevice> pairedDevices= bluetoothAdapter.getBondedDevices();
        if(pairedDevices.size()>0){
            List<String> connections = new ArrayList<>();
            for(BluetoothDevice device: pairedDevices){
                connections.add(device.getName()+": "+ device.getAddress());
            }
            if(newDevices != null && !newDevices.isEmpty()) {
                connections.addAll(newDevices);
            }
            liveData.setValue(connections);
        }
    }

    public void searchConnections() {
        boolean success = bluetoothAdapter.startDiscovery();
        if(!success) {
            Toast.makeText(application, "No More Available Opponents", Toast.LENGTH_SHORT).show();
        }
        else {
            mReceiver = new BroadcastReceiver(){
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                        BluetoothDevice device= intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        newDevices.add(device.getName()+": "+ device.getAddress());
                        System.out.println(" DEVICE FOUND : " + device.getName()+": "+ device.getAddress());
                        updateConnections();
                    }
                }

            };
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            application.registerReceiver(mReceiver, filter);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        application.unregisterReceiver(mReceiver);
    }
}
