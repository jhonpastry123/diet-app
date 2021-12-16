package com.diet.trinity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.diet.trinity.MainApplication;
import com.diet.trinity.R;
import com.diet.trinity.data.api.REST;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentDetails extends AppCompatActivity {
    TextView txtId,txtAmount,txtStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        txtId = findViewById(R.id.txtId);
        txtAmount = findViewById(R.id.txtAmount);
        txtStatus = findViewById(R.id.txtStatus);

        Intent intent = getIntent();

        showDetails(intent.getStringExtra("Amount"));
    }

    private void showDetails(String paymentAmount) {
        int type = 0;
        if(paymentAmount.equals("3"))
        {
            type = 1;
        }
        else if(paymentAmount.equals("49.99"))
        {
            type = 2;
        }

        if (type != 0) {
            REST rest = MainApplication.getContainer().get(REST.class);
            rest.purchaseMembership(type)
                    .enqueue(new Callback<Boolean>() {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                            Boolean flag = response.body();
                            if (flag) {
                                Intent intent = new Intent(PaymentDetails.this, DailyCaleandarActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<Boolean> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
        }
    }
}
