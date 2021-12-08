package com.diet.trinity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.diet.trinity.R;
import com.diet.trinity.data.common.DietMode;
import com.diet.trinity.data.common.PersonalData;

public class WelcomeActivity extends AppCompatActivity {

    TextView _point, _unit, _warning;
    int mPoint, mUnit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        _point = findViewById(R.id.txtPoint);
        _unit = findViewById(R.id.txtUnit);
        _warning = findViewById(R.id.unit_warning_txt);

        addEventListener();
        initView();
    }

    private void initView(){
        mUnit = PersonalData.getInstance().getUnits();
        mPoint = PersonalData.getInstance().getPoints();

        if (mUnit > 12) {
            _warning.setVisibility(View.GONE);
        }
        else {
            _warning.setVisibility(View.VISIBLE);
        }

        _point.setText(String.valueOf(mPoint));
        _unit.setText(String.valueOf(mUnit));
    }

    private void addEventListener(){
        final LinearLayout _iconPoint = findViewById(R.id.linPoint);
        final LinearLayout _iconUnit = findViewById(R.id.linUnit);
        findViewById(R.id.imgNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonalData.getInstance().writeData(getBaseContext());
                if (_iconPoint.getAlpha() == 1.0f && _iconUnit.getAlpha() == 1.0f) {
                }
                else {
                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    intent.putExtra("activity", "welcome");
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

        _iconPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _iconPoint.setAlpha(0.5f);
                _iconUnit.setAlpha(1.0f);

                PersonalData.getInstance().setDietMode(DietMode.POINT);
            }
        });

        _iconUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _iconPoint.setAlpha(1.0f);
                _iconUnit.setAlpha(0.5f);
                PersonalData.getInstance().setDietMode(DietMode.UNIT);
            }
        });
    }
}