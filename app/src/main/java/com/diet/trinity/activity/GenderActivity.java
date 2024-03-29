package com.diet.trinity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.diet.trinity.R;
import com.diet.trinity.data.common.Gender;
import com.diet.trinity.data.common.PersonalData;

public class GenderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender);

        addEventListener();
    }

    private void addEventListener(){
        final ImageView _male = findViewById(R.id.imgMale);
        final ImageView _female = findViewById(R.id.imgFemale);

        _male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setAlpha(0.3f);
                _female.setAlpha(1.0f);

                PersonalData.getInstance().setGender(Gender.MALE);

                Intent intent = new Intent(getBaseContext(), HeightActivity.class);
                startActivity(intent);
            }
        });

        _female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setAlpha(0.3f);
                _male.setAlpha(1.0f);

                PersonalData.getInstance().setGender(Gender.FEMALE);

                Intent intent = new Intent(getBaseContext(), HeightActivity.class);
                startActivity(intent);
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
}