package com.gmail.pavkascool.update;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gmail.pavkascool.update.delegates.ConnectionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ConnectionActivity extends AppCompatActivity implements View.OnClickListener, ConnectionListener {

    private TextView intro, empty;
    private Button server;
    private RecyclerView recyclerView;
    private List<String> connections;
    private ConnectionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        intro = findViewById(R.id.intro);
        empty = findViewById(R.id.empty);
        server = findViewById(R.id.server);
        server.setOnClickListener(this);

        Set<BluetoothDevice> pairedDevices = BattleApplication.getInstance().getPairedDevices();
        if(pairedDevices.size()>0){
            connections = new ArrayList<>();
            for(BluetoothDevice device: pairedDevices){
                connections.add(device.getName()+ " MAC: "+ device.getAddress());
            }
        }

        recyclerView = findViewById(R.id.recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, getResources().getConfiguration().orientation);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new ConnectionAdapter();
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if(connections == null || connections.isEmpty()) {
            empty.setText(getString(R.string.empty));
        }
        else empty.setText("");
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.server) {
            //model.connectOpponent(null);
            new Connector(this).setConnectionAsServer();

        }
        else {
            String dev = ((TextView)v).getText().toString();
            //model.connectOpponent(dev);
            new Connector(this).setConnectionAsClient(dev);
        }
    }

    @Override
    public void onSocketConnected() {
        Intent intent = new Intent(this, DeploymentActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onReceive(Intent intent) {

    }


    private class ConnectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_connection, parent, false);
            return new ConnectionViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(connections != null) {
                ConnectionViewHolder connectionViewHolder = (ConnectionViewHolder) holder;
                connectionViewHolder.data.setText(connections.get(position));
            }
        }

        @Override
        public int getItemCount() {
            if (connections == null) return 0;
            return connections.size();
        }
    }
    private class ConnectionViewHolder extends RecyclerView.ViewHolder {

        TextView data;
        public ConnectionViewHolder(@NonNull View itemView) {
            super(itemView);
            data = itemView.findViewById(R.id.connect);
            data.setOnClickListener(ConnectionActivity.this);
        }
    }
}
