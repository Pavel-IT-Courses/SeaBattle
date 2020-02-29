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
import java.util.Random;
import java.util.UUID;

import static com.gmail.pavkascool.update.BattleActivity.FLEET_SIZE;

public class Connector {
    private static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final UUID DEFAULT_UUID = UUID.fromString("48499999-8cf0-11bd-b23e-10b96e4ef00d");
    private CommunicationThread communication;

    private List<ConnectionListener> connectionListeners;
    private static Connector instance;

    private Coordinates yourShell, enemyShell;

    public static Connector getInstance() {
        if(instance == null) {
            instance = new Connector();
        }
        return instance;
    }

    private Connector() {
        connectionListeners = new ArrayList<>();
    }

    public void bombarding(Coordinates coordinates) {
        yourShell = coordinates;
    }

    public Coordinates shelled() {
        if(enemyShell != null) {
            System.out.println("ENEMY SHELL = " + enemyShell + " Thread: " + Thread.currentThread().getName());
            Coordinates shell = new Coordinates(enemyShell.getRow(), enemyShell.getCol());
            enemyShell = null;
            return shell;
        }
        else return null;
    }

    public void setListener(ConnectionListener connectionListener) {
        connectionListeners.add(connectionListener);
    }
    public void removeListener(ConnectionListener connectionListener) {
        connectionListeners.remove(connectionListener);
    }

    public void setConnectionAsServer() {
        Thread accepting = new AcceptThread();
        accepting.start();
    }

    public void setConnectionAsClient(String address) {
        Thread connecting = new ConnectThread(address);
        connecting.start();
    }

    public void startCommunication(Intent intent, List<CellView> ships) {
        communication = new CommunicationThread(intent, ships);
        communication.start();
    }
    public void stopCommunication() {
        communication.interrupt();
    }

    private class CommunicationThread extends Thread {
        private Intent intent;
        private List<CellView> ships;
        private BluetoothSocket bluetoothSocket;

        public CommunicationThread(Intent intent, List<CellView> ships) {
            this.intent = intent;
            this.ships = ships;
            bluetoothSocket = BattleApplication.getInstance().getBluetoothSocket();
        }
        public void run() {
            try(InputStream is = bluetoothSocket.getInputStream(); DataInputStream dis = new DataInputStream(is); OutputStream os = bluetoothSocket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os)) {

                for (CellView ship : ships) {
                    dos.writeInt(ship.getLocationCol());
                    dos.writeInt(ship.getLocationRow());
                    dos.writeInt(ship.getCols());
                    dos.writeInt(ship.getRows());
                }

                Random random = new Random();
                boolean yourTurn = random.nextBoolean();
                if(yourTurn) {
                    dos.writeInt(-1);
                }
                else {
                    dos.writeInt(1);
                }

                dos.flush();

                int[] enemies = new int[FLEET_SIZE * 4 + 1];

                boolean determine = false;
                if(dis.available() == 0) determine = true;

                while (dis.available() == 0) {
                    Thread.currentThread().sleep(1000);

                }
                int i = 0;
                while (dis.available() > 0) {
                    enemies[i++] = dis.readInt();
                }

                if(determine) {
                    if(yourTurn) {
                        enemies[FLEET_SIZE * 4] = 1;
                    }
                    else {
                        enemies[FLEET_SIZE * 4] = -1;
                    }
                }

                intent.putExtra("enemies", enemies);

                for(ConnectionListener cl: connectionListeners) {
                    cl.onReceive(intent);
                }
                System.out.println("FIGHTING BEGAN IN THREAD " + Thread.currentThread().getName());

                while(!isInterrupted()) {
                    fight(dos, dis);
                }

            }
            catch(IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void fight(DataOutputStream dos, DataInputStream dis) throws IOException {
        if(yourShell != null) {
            System.out.println("CONNECTOR FIGHTS " + yourShell.getRow() + "  " + yourShell.getCol());
            dos.writeInt(yourShell.getRow());
            dos.writeInt(yourShell.getCol());
            yourShell = null;

        }

        if(dis.available() > 0) {
            int r = dis.readInt();
            int c = dis.readInt();
            enemyShell = new Coordinates(r,c);
            System.out.println("CONNECTOR GET FIRED " + r + "  " + c);
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
                        //connectionListener.onSocketConnected();
                        for(ConnectionListener cl: connectionListeners) {
                            cl.onSocketConnected();
                        }
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
                //connectionListener.onSocketConnected();
                for(ConnectionListener cl: connectionListeners) {
                    cl.onSocketConnected();
                }
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
