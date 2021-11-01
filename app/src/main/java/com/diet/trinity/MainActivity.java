package com.diet.trinity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.diet.trinity.activity.DailyCaleandarActivity;
import com.diet.trinity.activity.GoalActivity;
import com.diet.trinity.activity.WelcomeActivity;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    private Handler hdlr = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);

        hdlr.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        }, 2000);
    }

    private void moveToWelcome(){
        Intent intent = new Intent(getBaseContext(), WelcomeActivity.class);
        startActivity(intent);
    }

    private void moveToDaily(){
        Intent intent = new Intent(getBaseContext(), DailyCaleandarActivity.class);
        startActivity(intent);
    }

    private void moveToGoal(){
        Intent intent = new Intent(getBaseContext(), GoalActivity.class);
        startActivity(intent);
    }


    private void loadData() {
        moveToGoal();
//        if (purchase_time == 0) {
//            moveToDaily();
//        } else if (flag == false && trial_time == 0) {
//            moveToGoal();
//        }

        finish();
    }
}
