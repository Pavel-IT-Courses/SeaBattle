package com.gmail.pavkascool.update;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;

import com.gmail.pavkascool.update.utils.Coordinates;
import com.gmail.pavkascool.update.views.CellView;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.gmail.pavkascool.update.BattleActivity.FLEET_SIZE;

public class Connector {
    private static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final UUID DEFAULT_UUID = UUID.fromString("48499999-8cf0-11bd-b23e-10b96e4ef00d");
    private ConnectionListener connectionListener;

    public Connector(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    public void setConnectionAsServer() {
        Thread accepting = new AcceptThread();
        accepting.start();
    }

    public void setConnectionAsClient(String address) {
        Thread connecting = new ConnectThread(address);
        connecting.start();
    }

    public void sendAndReceive(Intent intent, List<CellView> ships) {
        BluetoothSocket bluetoothSocket = BattleApplication.getInstance().getBluetoothSocket();

        try(InputStream is = bluetoothSocket.getInputStream(); DataInputStream dis = new DataInputStream(is); OutputStream os = bluetoothSocket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os)) {
            Thread sending = new SendThread(ships, dos);
            sending.start();

            Thread.currentThread().sleep(500);

            Thread receiving = new ReceiveThread(intent, dis);
            receiving.start();

            Thread.currentThread().sleep(1000);
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private class ReceiveThread extends Thread {
        Intent intent;
        DataInputStream dis;
        public ReceiveThread(Intent intent, DataInputStream dis) {
            this.intent = intent;
            this.dis = dis;
        }

        public void run() {
            int[] enemies = new int[FLEET_SIZE * 4];
            try {
                while (dis.available() == 0) {
                    Thread.currentThread().sleep(500);
                }
                int i = 0;
                while (dis.available() > 0) {
                    enemies[i++] = dis.readInt();
                    System.out.println("THE ELEMENT IS " + enemies[i-1]);
                }
                System.out.println("ENEMIES: " + enemies);
            }
            catch (Exception e) {
            }

//                for(int i = 0; i < enemies.length; i++) {
//                    enemies[i] = ds.readInt();
//                    System.out.println("I = " + i + ", array var = " + enemies[i]);
//                }
                intent.putExtra("enemies", enemies);
                connectionListener.onReceive(intent);

//            try  {
//                InputStream is = socket.getInputStream();
//                DataInputStream dis = new DataInputStream(is);
//                while(dis.available() == 0) {
//                    Thread.currentThread().sleep(500);
//                }
//                while (dis.available() > 0) {
//                    int res = dis.readInt();
//
//                    System.out.println("I Read " + res);
//                }
//            }
//            catch(IOException e) {
//                System.out.println("Exception!");
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }

    private class SendThread extends Thread {
        List<CellView> ships;
        DataOutputStream dos;

        public SendThread(List<CellView> ships, DataOutputStream dos) {
            this.ships = ships;
            this.dos = dos;
        }

        public void run() {
            try {
                for (CellView ship : ships) {
                    System.out.println("Sending ship " + ship + " totally: " + ships.size());
                    dos.writeInt(ship.getLocationCol());
                    dos.writeInt(ship.getLocationRow());
                    dos.writeInt(ship.getCols());
                    dos.writeInt(ship.getRows());
                }
                dos.flush();
            } catch (IOException e) {

            }
            System.out.println("Socket is Connected = " + BattleApplication.getInstance().getBluetoothSocket().isConnected());
        }
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
                        BattleApplication.getInstance().setBluetoothSocket(socket);
                        serverSocket.close();
                        connectionListener.onSocketConnected();
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
        private BluetoothSocket socket;
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
                catch(Exception ex) {

                }
            }

            if(socket.isConnected()) {
                BattleApplication.getInstance().setBluetoothSocket(socket);
                connectionListener.onSocketConnected();
            }
        }

        public void cancel() {
            try {
                socket.close();
            }
            catch(IOException e) {

            }
        }

    }
}
