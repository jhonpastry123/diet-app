package com.diet.trinity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.diet.trinity.R;
import com.diet.trinity.data.common.Goal;
import com.diet.trinity.data.common.PersonalData;

public class GoalActivity extends AppCompatActivity implements View.OnClickListener {
private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        TextView txtLogin = (TextView)findViewById(R.id.txtLogin);
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                intent.putExtra("activity", "goal");
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.imgLose).setOnClickListener(this);
        findViewById(R.id.imgGain).setOnClickListener(this);
        findViewById(R.id.imgStay).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgLose :
                PersonalData.getInstance().setGoal(Goal.LOSE);
                break;
            case R.id.imgGain :
                PersonalData.getInstance().setGoal(Goal.GAIN);
                break;
            case R.id.imgStay :
                PersonalData.getInstance().setGoal(Goal.STAY);
                break;
        }
        Intent intent = new Intent(this, WeightActivity.class);
        startActivity(intent);
    }
}