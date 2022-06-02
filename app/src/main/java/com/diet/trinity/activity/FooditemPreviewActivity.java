package com.diet.trinity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.diet.trinity.MainApplication;
import com.diet.trinity.R;
import com.diet.trinity.Utility.Constants;
import com.diet.trinity.Utility.Global;
import com.diet.trinity.data.api.REST;
import com.diet.trinity.data.models.Token;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FooditemPreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fooditem_preview);


        String food_name = Global.food_name;
        TextView food_name_preview = findViewById(R.id.food_name_preview);
        food_name_preview.setText(food_name);


        double carbon = Global.carbon;
        TextView carbon_preview = findViewById(R.id.carbon_preview);
        carbon_preview.setText(carbon+"");


        double protein = Global.protein;
        TextView protein_preview = findViewById(R.id.protein_preview);
        protein_preview.setText(protein+"");

        double fat = Global.fat;
        TextView fat_preview = findViewById(R.id.fat_preview);
        fat_preview.setText(fat+"");

        double portion_in_grams = Global.portion_in_grams;
        TextView portion_in_grams_preview = findViewById(R.id.portion_in_grams_preview);
        portion_in_grams_preview.setText(portion_in_grams+"");

        double kcal = Global.kcal;
        TextView kcal_preview = findViewById(R.id.kcal_preview);
        kcal_preview.setText(kcal+"");

        double serving_size = Global.serving_size;
        TextView serving_size_preview = findViewById(R.id.serving_size_preview);
        serving_size_preview.setText(serving_size+"");

        String serving_prefix = Global.serving_prefix;
        TextView serving_prefix_preview = findViewById(R.id.serving_prefix_preview);
        serving_prefix_preview.setText(serving_prefix);

        int user_id = Global.user_id;
        String barcode = Global.barcode;
        TextView barcode_preview = findViewById(R.id.barcode_preview);
        barcode_preview.setText(barcode);

        TextView food_categories_preview = findViewById(R.id.food_categories_preview);
        // set text on textView
        food_categories_preview.setText(Global.food_categories);

        Button prev_btn = findViewById(R.id.prev_btn1);
        prev_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        Button save_btn = findViewById(R.id.save_btn);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressDialog mProgressDialog = new ProgressDialog(FooditemPreviewActivity.this);
                mProgressDialog.setTitle("SignUp...");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.show();

                REST rest = MainApplication.getContainer().get(REST.class);
                rest.FoodItemStore(food_name, carbon, protein, fat, portion_in_grams, kcal, serving_size, serving_prefix, user_id, barcode, Arrays.toString(Global.food_categories_id))
                        .enqueue(new Callback<Boolean>() {
                            @Override
                            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                mProgressDialog.dismiss();

                                Boolean result = response.body();
                                if (result == true) {
                                    Intent intent = new Intent(FooditemPreviewActivity.this, SearchFoodActivity.class);
                                    intent.putExtra("date", getIntent().getStringExtra("date"));
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<Boolean> call, Throwable t) {
                                Toast.makeText(FooditemPreviewActivity.this, getResources().getString(R.string.offline_text), Toast.LENGTH_LONG).show();
                                mProgressDialog.dismiss();
                            }
                        });
            }
        });
    }
}