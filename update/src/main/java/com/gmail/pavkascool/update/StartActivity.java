package com.gmail.pavkascool.update;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    private Button ai;
    private Button person;
    private TextView stat;
    private RecyclerView recyclerView;

    private LiveData<Statistics> statisticsLiveData;
    private Statistics statistic;
    private List<Result> results;
    private ResultAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        stat = findViewById(R.id.stat);
        recyclerView = findViewById(R.id.recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, getResources().getConfiguration().orientation);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new ResultAdapter();
        recyclerView.setAdapter(adapter);

        StartModel startModel = ViewModelProviders.of(this).get(StartModel.class);

        statisticsLiveData = startModel.getStatData();

        statisticsLiveData.observe(this, new Observer<Statistics>() {
            @Override
            public void onChanged(Statistics statistics) {
                statistic = statistics;
                if(statistic != null) {
                    stat.setText(String.format(getString(R.string.stat), String.valueOf(statistic.getVictories()),
                            String.valueOf(statistic.getDefeats())));
                    results = statistic.getResults();
                    adapter.notifyDataSetChanged();

                }

            }
        });


        ai = findViewById(R.id.ai);
        ai.setOnClickListener(this);

        person = findViewById(R.id.person);
        person.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        if(v.getId() == R.id.ai) {
            BattleApplication.getInstance().setAgainstAI(true);
        }
        if(v.getId() == R.id.person) {
            BattleApplication.getInstance().setAgainstAI(false);
        }
        intent = new Intent(this, DeploymentActivity.class);
        startActivity(intent);
    }

    private class ResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public ResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false);

            return new ResultHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ResultHolder resultHolder = (ResultHolder)holder;
            resultHolder.date.setText(results.get(position).getDate());
            resultHolder.winner.setText(results.get(position).getWinner());
            resultHolder.turns.setText(results.get(position).getTurns());
        }

        @Override
        public int getItemCount() {
            if(results != null) return results.size();
            return 0;
        }
    }

    private class ResultHolder extends RecyclerView.ViewHolder {

        TextView date, winner, turns;
        public ResultHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            winner = itemView.findViewById(R.id.winner);
            turns = itemView.findViewById(R.id.turns);
        }
    }
}
