package com.diet.trinity.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.diet.trinity.R;
import com.diet.trinity.model.PersonalData;

public class WeightActivity extends AppCompatActivity {
    EditText _weight;
    float mWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        addEventListener();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void addEventListener(){
        findViewById(R.id.imgNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    PersonalData.getInstance().setWeight(mWeight);
                    PersonalData.getInstance().setInitial_weight(mWeight);

                    SharedPreferences sp=getSharedPreferences("Setting", MODE_PRIVATE);
                    SharedPreferences.Editor Ed=sp.edit();
                    Ed.putFloat("weight", mWeight);
                    Ed.putFloat("initial_weight", mWeight);
                    Ed.commit();

                    Intent intent = new Intent(getBaseContext(), GenderActivity.class);
                    startActivity(intent);
                }
            }
        });

        findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
    }

    private boolean validate(){
        boolean validate = true;
        _weight = findViewById(R.id.editWeight);

        try{
            mWeight = Float.parseFloat(_weight.getText().toString());
        }
        catch (Exception e){
            mWeight = 0.0f;
        }

        if(mWeight <= 1.0f){
            _weight.setError("εισάγετε Βάρος");
            validate = false;
        }
        else{
            _weight.setError(null);
        }
        return validate;
    }
}