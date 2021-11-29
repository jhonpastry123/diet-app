package com.diet.trinity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.diet.trinity.activity.DailyCaleandarActivity;
import com.diet.trinity.activity.GoalActivity;
import com.diet.trinity.data.api.REST;
import com.diet.trinity.data.models.User;
import com.diet.trinity.data.models.Wrappers;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private void moveToDaily(){
        Intent intent = new Intent(getBaseContext(), DailyCaleandarActivity.class);
        startActivity(intent);
    }

    private void moveToGoal(){
        Intent intent = new Intent(getBaseContext(), GoalActivity.class);
        startActivity(intent);
    }


    private void loadData() {
        REST rest = MainApplication.getContainer().get(REST.class);
        rest.profileShow()
                .enqueue(new Callback<Wrappers.Single<User>>() {

                    @Override
                    public void onResponse(
                            @Nullable Call<Wrappers.Single<User>> call,
                            @Nullable Response<Wrappers.Single<User>> response
                    ) {
                        int code = response != null ? response.code() : -1;
                        Log.w("token", "Checking token validity with server returned " + code + ".");
                        if (response != null && response.isSuccessful()) {
                            moveToDaily();
                        } else {
                            moveToGoal();
                        }

                        finish();
                    }

                    @Override
                    public void onFailure(
                            @Nullable Call<Wrappers.Single<User>> call,
                            @Nullable Throwable t
                    ) {
                        Log.e("token", "Failed to validate token status.", t);
                        moveToGoal();

                        finish();
                    }
                });
    }
}
