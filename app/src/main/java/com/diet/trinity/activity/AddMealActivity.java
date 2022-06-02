package com.diet.trinity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.diet.trinity.Adapter.CustomMealAdapter;
import com.diet.trinity.MainApplication;
import com.diet.trinity.R;
import com.diet.trinity.Utility.Global;
import com.diet.trinity.data.api.REST;
import com.diet.trinity.data.models.FoodItem;
import com.diet.trinity.data.models.FoodValue;
import com.diet.trinity.model.Listmodel;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMealActivity extends AppCompatActivity {

    Button add_more_btn, save_btn;
    ImageView imgBack;
    ListView listView;
    TextView points;
    EditText recipe_title;

    ArrayList<Listmodel> Listitem=new ArrayList<Listmodel>();

    CustomMealAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        initView();
        initMeal();
        addEventListener();
    }

    private void addEventListener() {
        add_more_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddMealActivity.this, SearchFoodActivity.class);
                intent.putExtra("activity", "add_meal");
                intent.putExtra("date", getIntent().getStringExtra("date"));
                startActivity(intent);
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
                Global.food_values = new FoodValue[]{};
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = recipe_title.getText().toString();
                int size = Global.food_values.length;

                int categories_id = 0;

                if (Global.timing_id > 3) {
                    categories_id = 4;
                } else {
                    categories_id = Global.timing_id;
                }

                if (title.equals("")) {
                    Toast.makeText(AddMealActivity.this, "Please Input Food Title first!", Toast.LENGTH_SHORT).show();
                } else if (size == 0) {
                    Toast.makeText(AddMealActivity.this, "You need to add at least a food item", Toast.LENGTH_SHORT).show();
                } else {
                    int[] food_ids = new int[size];
                    int[] amounts = new int[size];

                    for (int i=0; i<Global.food_values.length ; i++) {
                        FoodValue value = Global.food_values[i];
                        food_ids[i] = value.food_items_id;
                        amounts[i] = value.amount;
                    }

                    String food_id = Arrays.toString(food_ids);
                    String food_amount = Arrays.toString(amounts);
                    int user_id = Global.user_id;

                    ProgressDialog mProgressDialog = new ProgressDialog(AddMealActivity.this);
                    mProgressDialog.setTitle("SignUp...");
                    mProgressDialog.setIndeterminate(false);
                    mProgressDialog.show();

                    REST rest = MainApplication.getContainer().get(REST.class);
                    rest.RecipeStore(user_id, title, categories_id, food_id, food_amount)
                            .enqueue(new Callback<Boolean>() {
                                @Override
                                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                    mProgressDialog.dismiss();

                                    Boolean result = response.body();
                                    if (result == true) {
                                        Intent intent = new Intent(AddMealActivity.this, SearchFoodActivity.class);
                                        intent.putExtra("date", getIntent().getStringExtra("date"));
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Boolean> call, Throwable t) {
                                    Toast.makeText(AddMealActivity.this, getResources().getString(R.string.offline_text), Toast.LENGTH_LONG).show();
                                    mProgressDialog.dismiss();
                                }
                            });

                }
            }
        });
    }

    private void initMeal() {
        FoodValue[] foodValues = Global.food_values;
        float points_total = 0, units_total = 0;
        for (int i=0; i<foodValues.length; i++) {
            FoodValue foodValue = foodValues[i];

            String name = foodValue.food_item.food_name;
            float size = foodValue.food_item.serving_size;
            float count = foodValue.amount / size;
            String prefix = foodValue.food_item.serving_prefix;
            float units = foodValue.food_item.units * foodValue.amount / foodValue.food_item.portion_in_grams;
            float points = foodValue.food_item.points * foodValue.amount / foodValue.food_item.portion_in_grams;

            units_total += units;
            points_total += points;

            Listitem.add(new Listmodel(i,name,size,count,prefix,units,points));

            myAdapter=new CustomMealAdapter(AddMealActivity.this, Listitem);
            listView.setAdapter(myAdapter);
        }

        points.setText(String.format("%.2f", points_total) + " Points / " + String.format("%.2f", units_total) + " Units");
    }

    private void initView() {
        add_more_btn = findViewById(R.id.add_more_btn);
        save_btn = findViewById(R.id.save_btn);
        imgBack = findViewById(R.id.imgBack);
        listView = findViewById(R.id.listview);
        points = findViewById(R.id.points);
        recipe_title = findViewById(R.id.recipe_title);

        points.setText("0 Points / 0 Units");
    }
}