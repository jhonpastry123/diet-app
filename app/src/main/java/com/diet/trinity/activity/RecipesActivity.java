package com.diet.trinity.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.diet.trinity.Adapter.RecyclerViewAdapter;
import com.diet.trinity.R;
import com.diet.trinity.Utility.Common;
import com.diet.trinity.Utility.ReceipeDatabaseHelper;
import com.diet.trinity.model.DietMode;
import com.diet.trinity.model.PersonalData;
import com.diet.trinity.model.Recipe;
import com.diet.trinity.model.RecipeList;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class RecipesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    ArrayList<RecipeList> recipeList1 = new ArrayList<>();
    private SQLiteDatabase db, db_food;
    private SQLiteOpenHelper openHelper, openHelper_food;
    RecyclerViewAdapter adapter;
    ArrayList<Recipe> RecipeItems = new ArrayList<>();
    ArrayList<Integer> category_ids = new ArrayList<Integer>();
    ArrayList<String> category_names = new ArrayList<String>();
    LinearLayout contentLayout;

    String category_name, recipe_name;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);
        contentLayout = findViewById(R.id.contentLayout);
        addEventListener();

        ImageView imgView  = findViewById(R.id.imageView2);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.dietaz.gr"));
                startActivity(browserIntent);
            }
        });

        try {
            initData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        searchView = findViewById(R.id.SearchTitle);
        searchView.setVisibility(View.GONE);
        setupSearchView();

        findViewById(R.id.imgSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchView.getVisibility() == View.GONE)
                    searchView.setVisibility(View.VISIBLE);
                else
                    searchView.setVisibility(View.GONE);
            }
        });
    }

    private void addEventListener(){
        findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipesActivity.this, DailyCaleandarActivity.class);
                startActivity(intent);
            }
        });
    }


    @SuppressLint("ResourceAsColor")
    private void initData() throws IOException {
        openHelper = new ReceipeDatabaseHelper(this);
        db = openHelper.getWritableDatabase();

        final Cursor cursor = db.rawQuery("SELECT *FROM " + ReceipeDatabaseHelper.TABLE_NAME, null);
        int id2 = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Category_id")));
                    int food_id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("ID")));
                    category_name = cursor.getString(cursor.getColumnIndex("Category_name"));
                    recipe_name = cursor.getString(cursor.getColumnIndex("Title"));
                    RecipeItems.add(new Recipe(id, food_id, recipe_name, cursor.getString(cursor.getColumnIndex(ReceipeDatabaseHelper.COL_5)), Float.parseFloat(cursor.getString(cursor.getColumnIndex(ReceipeDatabaseHelper.COL_7))), Float.parseFloat(cursor.getString(cursor.getColumnIndex(ReceipeDatabaseHelper.COL_8)))));

                    if (!category_ids.contains(id)) {
                        category_ids.add(id);
                        category_names.add(category_name);
                    }
                }
                    while (cursor.moveToNext()) ;
            }
                cursor.close();
        }

        for(int j=0;j<category_ids.size();j++)
        {
            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            titleParams.setMargins(0,10,0,5);
            LinearLayout titlelayout = new LinearLayout(this);
            titlelayout.setOrientation(LinearLayout.VERTICAL);
            titlelayout.setLayoutParams(titleParams);

            contentLayout.addView(titlelayout);

            TextView titleText = new TextView(this);
            LinearLayout.LayoutParams textparams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            textparams.setMargins(0, 0, 10, 5);
            titleText.setLayoutParams(textparams);
            titleText.setTextSize(getResources().getDimension(R.dimen.recipe_title));
            titleText.setTextColor(getResources().getColor(R.color.dayTextColor));
            titleText.setText(category_names.get(j));
            titlelayout.addView(titleText);

            HorizontalScrollView scrollView = new HorizontalScrollView(this);
            titlelayout.addView(scrollView);

            LinearLayout.LayoutParams recipeParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            recipeParams.setMargins(1, 1, 1, 1);
            LinearLayout recipelayout = new LinearLayout(this);
            recipelayout.setOrientation(LinearLayout.HORIZONTAL);
            recipelayout.setLayoutParams(recipeParams);
            scrollView.addView(recipelayout);

            for(int i=0;i<RecipeItems.size();i++)
            {
                if(category_ids.get(j) == RecipeItems.get(i).categoryID)
                {
                    LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    cardParams.setMargins(30,5,5,5);
                    CardView card = new CardView(this);
                    card.setId(RecipeItems.get(i).foodID);
                    card.setLayoutParams(cardParams);
                    recipelayout.addView(card);
                    card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(RecipesActivity.this, RecipieSingleActivity.class);
                            intent.putExtra("foodID", view.getId());
                            startActivity(intent);
                        }
                    });

                    LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout itemlayout = new LinearLayout(this);
                    itemlayout.setOrientation(LinearLayout.VERTICAL);
                    itemlayout.setLayoutParams(itemParams);
                    card.addView(itemlayout);

                    //-----bitmap--------//
                    String storageUrl = Common.getInstance().getImageUrl();//"http://zafeiraki.com/images";
                    URL imageUrl = new URL(storageUrl + RecipeItems.get(i).photoResId);

                    //-------------------//

                    LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(550, 400);
                    ImageView imgView = new ImageView(this);
                    imgView.setLayoutParams(imageParams);
                    imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imgView.setImageDrawable(getResources().getDrawable(R.drawable.back_recipe1));
                    Picasso.get().load(String.valueOf(imageUrl)).resize(300, 0).into(imgView);
                    itemlayout.addView(imgView);

                    LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    nameParams.setMargins(15, 5, 0, 0);
                    String title = RecipeItems.get(i).foodTitle;
                    TextView nameText = new TextView(this);
                    nameText.setLayoutParams(nameParams);
                    nameText.setMaxLines(1);
                    nameText.setTextColor(getResources().getColor(R.color.colorText1));
                    nameText.setTextSize(10);
                    if (title.length() > 35) {
                        nameText.setText(title.substring(0, 35) + "...");
                    }
                    else {
                        nameText.setText(title);
                    }

                    itemlayout.addView(nameText);

                    LinearLayout.LayoutParams pointParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    pointParams.setMargins(15, 1, 0, 5);
                    TextView pointText = new TextView(this);
                    pointText.setLayoutParams(nameParams);
                    pointText.setTextColor(getResources().getColor(R.color.colorText1));
                    pointText.setTextSize(7);
                    if (PersonalData.getInstance().getDietMode() == DietMode.POINT) {
                        pointText.setText(String.format(Locale.US, "%.1f", RecipeItems.get(i).points)+" points");
                    }
                    else {
                        pointText.setText(String.format(Locale.US, "%.1f", RecipeItems.get(i).units)+" units");
                    }
                    itemlayout.addView(pointText);
                }
            }
        }
    }

    void SearchData(String text){
        contentLayout.removeAllViews();
        for(int j=0;j<category_ids.size();j++)
        {
            if(category_names.get(j).toLowerCase().contains(text.toLowerCase())) {
                LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                titleParams.setMargins(0, 10, 0, 5);
                LinearLayout titlelayout = new LinearLayout(this);
                titlelayout.setOrientation(LinearLayout.VERTICAL);
                titlelayout.setLayoutParams(titleParams);

                contentLayout.addView(titlelayout);
                TextView titleText = new TextView(this);
                LinearLayout.LayoutParams textparams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                textparams.setMargins(0, 0, 10, 5);
                titleText.setLayoutParams(textparams);
                titleText.setTextSize(getResources().getDimension(R.dimen.recipe_title));
                titleText.setTextColor(getResources().getColor(R.color.dayTextColor));
                titleText.setText(category_names.get(j));
                titlelayout.addView(titleText);

                LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                HorizontalScrollView scrollView = new HorizontalScrollView(this);
                titlelayout.addView(scrollView);

                LinearLayout.LayoutParams recipeParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                recipeParams.setMargins(1, 1, 1, 1);
                LinearLayout recipelayout = new LinearLayout(this);
                recipelayout.setOrientation(LinearLayout.HORIZONTAL);
                recipelayout.setLayoutParams(recipeParams);
                scrollView.addView(recipelayout);

                for (int i = 0; i < RecipeItems.size(); i++) {
                    if (category_ids.get(j) == RecipeItems.get(i).categoryID) {
                        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        cardParams.setMargins(30, 5, 5, 5);
                        CardView card = new CardView(this);
                        card.setId(RecipeItems.get(i).foodID);
                        card.setLayoutParams(cardParams);
                        recipelayout.addView(card);
                        card.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(RecipesActivity.this, RecipieSingleActivity.class);
                                intent.putExtra("foodID", view.getId());
                                startActivity(intent);
                            }
                        });

                        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        LinearLayout itemlayout = new LinearLayout(this);
                        itemlayout.setOrientation(LinearLayout.VERTICAL);
                        itemlayout.setLayoutParams(itemParams);
                        card.addView(itemlayout);

                        //-----bitmap--------//
                        String base64String = "data:image/png;base64," + RecipeItems.get(i).photoResId;
                        String base64Image = base64String.split(",")[1];

                        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        //-------------------//

                        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(550, 400);
                        ImageView imgView = new ImageView(this);
                        imgView.setLayoutParams(imageParams);
                        imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imgView.setImageDrawable(getResources().getDrawable(R.drawable.back_recipe1));
                        imgView.setImageBitmap(decodedByte);
                        itemlayout.addView(imgView);

                        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        nameParams.setMargins(15, 5, 0, 0);
                        TextView nameText = new TextView(this);
                        nameText.setLayoutParams(nameParams);
                        nameText.setMaxLines(1);
                        nameText.setTextColor(getResources().getColor(R.color.colorText1));
                        nameText.setTextSize(10);
                        nameText.setText(RecipeItems.get(i).foodTitle);
                        itemlayout.addView(nameText);

                        LinearLayout.LayoutParams pointParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        pointParams.setMargins(15, 1, 0, 5);
                        TextView pointText = new TextView(this);
                        pointText.setLayoutParams(nameParams);
                        pointText.setTextColor(getResources().getColor(R.color.colorText1));
                        pointText.setTextSize(7);
                        if (PersonalData.getInstance().getDietMode() == DietMode.POINT) {
                            pointText.setText(String.format(Locale.US, "%.1f", RecipeItems.get(i).points)+" points");
                        }
                        else {
                            pointText.setText(String.format(Locale.US, "%.1f", RecipeItems.get(i).units)+" units");
                        }
                        itemlayout.addView(pointText);
                    }
                }
            }
        }
    }

    private void setupSearchView() {
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        SearchData(newText);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }
}