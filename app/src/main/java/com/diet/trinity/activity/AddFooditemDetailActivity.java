package com.diet.trinity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.diet.trinity.R;
import com.diet.trinity.Utility.Global;

public class AddFooditemDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fooditem_detail);

        Log.e("Count", Global.food_categories_id.length+"");
        Button prev_btn = findViewById(R.id.prev_btn);
        prev_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        Button next_btn = findViewById(R.id.next_btn1);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddFooditemDetailActivity.this, FooditemPreviewActivity.class);
                intent.putExtra("date", getIntent().getStringExtra("date"));
                startActivity(intent);
            }
        });
    }
}