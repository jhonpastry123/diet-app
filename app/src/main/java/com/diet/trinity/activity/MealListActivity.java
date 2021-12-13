package com.diet.trinity.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.diet.trinity.Adapter.CustomMealAdapter;
import com.diet.trinity.MainApplication;
import com.diet.trinity.R;
import com.diet.trinity.Utility.Global;
import com.diet.trinity.Utility.MealDatabaseHelper;
import com.diet.trinity.data.api.REST;
import com.diet.trinity.data.models.Category;
import com.diet.trinity.data.models.Meal;
import com.diet.trinity.data.models.Wrappers;
import com.diet.trinity.model.Listmodel;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealListActivity extends AppCompatActivity {
    ArrayList<Listmodel> Listitem=new ArrayList<Listmodel>();
    ListView listView;
    ImageView imgAddMeal2, imgBack;
    String  food_name, serving_prefix, mSelectedDate;
    int id;
    float serving_size, serving_count, units, points;

    CustomMealAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_list);
        imgBack = findViewById(R.id.imgBack);
        imgAddMeal2 = findViewById(R.id.imgAddMeal2);

        mSelectedDate = getIntent().getStringExtra("date");
        if (mSelectedDate.equals(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()))) {
            if (imgAddMeal2.getVisibility() == View.GONE) {
                imgAddMeal2.setVisibility(View.VISIBLE);
            }
        } else {
            if (imgAddMeal2.getVisibility() == View.VISIBLE) {
                imgAddMeal2.setVisibility(View.GONE);
            }
        }
        initMeal();
        addEventListener();
    }

    private void addEventListener() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MealListActivity.this, DailyCaleandarActivity.class);
                startActivity(intent);
                finish();
            }
        });

        imgAddMeal2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MealListActivity.this, SearchFoodActivity.class);
                intent.putExtra("activity", "meal");
                startActivity(intent);
                finish();
            }
        });
    }

     void initMeal() {
         listView = findViewById(R.id.listview);
         REST rest = MainApplication.getContainer().get(REST.class);
         rest.MealsByCategory(mSelectedDate, Global.timing_id)
                 .enqueue(new Callback<Wrappers.Collection<Meal>>() {
                     @Override
                     public void onResponse(Call<Wrappers.Collection<Meal>> call, Response<Wrappers.Collection<Meal>> response) {
                         List<Meal> meals = response.body().data;
                         for (int i=0; i<meals.size(); i++) {
                             Meal meal = meals.get(i);
                             id = meal.id;
                             food_name = meal.food_name;
                             serving_size = meal.serving_size;
                             serving_count = meal.serving_count;
                             serving_prefix = meal.serving_prefix;
                             units = meal.units;
                             points = meal.points;

                             Listitem.add(new Listmodel(id, food_name, serving_size, serving_count, serving_prefix, units, points));

                             myAdapter=new CustomMealAdapter(MealListActivity.this, Listitem);
                             listView.setAdapter(myAdapter);
                         }
                     }

                     @Override
                     public void onFailure(Call<Wrappers.Collection<Meal>> call, Throwable t) {

                     }
                 });
    }
}