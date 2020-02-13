package com.gmail.pavkascool.update.delegates;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.gmail.pavkascool.update.BattleActivity;
import com.gmail.pavkascool.update.DeploymentActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ConnectionModel extends AndroidViewModel {

    private BluetoothAdapter bluetoothAdapter;
    private Application application;
    private BroadcastReceiver mReceiver;
    private final UUID DEFAULT_UUID = UUID.fromString("48499999-8cf0-11bd-b23e-10b96e4ef00d");


    public ConnectionModel(@NonNull Application application) {
        super(application);
        this.application = application;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        System.out.println("UUID = " + DEFAULT_UUID);
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
                connections.add(device.getName()+ " MAC: "+ device.getAddress());
            }
            liveData.setValue(connections);
        }
    }

    public void connected() {
        liveData.setValue(new ArrayList<String>());
    }

    public void connectOpponent(String opponent) {
        if(opponent == null) {
            Thread accepting = new AcceptThread();
            accepting.start();
        }
        else {
            Thread connecting = new ConnectThread(opponent);
            connecting.start();
        }
    }

    public void manageConnectedSocket(BluetoothSocket socket) {
        Thread manageThread = new ManageThread(socket);
        manageThread.start();
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket serverSocket;

        public AcceptThread() {
            super();
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("Sea Battle", DEFAULT_UUID);
            }
            catch(IOException e) {

            }
            serverSocket = tmp;
            System.out.println("Server Socket is " + serverSocket);
        }

        public void run() {
            System.out.println("INSIDE RUN");
            BluetoothSocket socket = null;
            while(true) {
                try {
                    System.out.println("1st accepting socket is " + socket);
                    socket = serverSocket.accept();
                    System.out.println("2nd accepting socket is " + socket);
                    if(socket != null) {
                        manageConnectedSocket(socket);
                        serverSocket.close();
                        application.getApplicationContext().startActivity(new Intent(application, DeploymentActivity.class));
                        break;
                    }
                }
                catch(IOException e) {
                    break;
                }

            }
        }

        public void cancel() {
            try {
                serverSocket.close();
            }
            catch(IOException e) {
                return;
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket socket;
        private String opponent;
        private BluetoothDevice device;

        public ConnectThread(String opponent) {
            super();
            this.opponent = opponent;
            String[] data = opponent.split("MAC: ");
            String address = data[data.length - 1];
            device = bluetoothAdapter.getRemoteDevice(address);
            System.out.println("I have contacted another device! It is " + device.getName() + " and " );
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(DEFAULT_UUID);
                System.out.println("Socket is " + tmp);
            } catch (IOException e) {

            }
            socket = tmp;
        }
        public void run() {
            try {
                bluetoothAdapter.cancelDiscovery();
                socket.connect();

            } catch (IOException e) {
                try {
                    socket.close();
                }
                catch(IOException ex) {

                }
            }
            manageConnectedSocket(socket);
            //application.getApplicationContext().startActivity(new Intent(application, DeploymentActivity.class));
            System.out.println(" Socket Connected? " + socket.isConnected());
        }

        public void cancel() {
            try {
                socket.close();
            }
            catch(IOException e) {

            }
        }

    }

    //TODO
    private class ManageThread extends Thread {
        private BluetoothSocket bluetoothSocket;

        ManageThread(BluetoothSocket bluetoothSocket) {
            this.bluetoothSocket = bluetoothSocket;
        }
    }
}
