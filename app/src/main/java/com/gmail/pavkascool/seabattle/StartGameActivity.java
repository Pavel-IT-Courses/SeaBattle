package com.gmail.pavkascool.seabattle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartGameActivity extends AppCompatActivity implements View.OnClickListener{
    private Button ai;
    private Button person;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        ai = findViewById(R.id.ai);
        ai.setOnClickListener(this);

        person = findViewById(R.id.person);
        person.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        boolean againstAI = false;
        if(v.getId() == R.id.ai) againstAI = true;
        Intent intent = new Intent(this, DeploymentActivity.class);
        intent.putExtra("againstAI", againstAI);
        startActivity(intent);
    }
}
