package com.diet.trinity.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.diet.trinity.MainApplication;
import com.diet.trinity.R;
import com.diet.trinity.Utility.Common;
import com.diet.trinity.data.api.REST;
import com.diet.trinity.data.models.Category;
import com.diet.trinity.data.models.Recipe;
import com.diet.trinity.data.models.Wrappers;
import com.diet.trinity.data.common.DietMode;
import com.diet.trinity.data.common.PersonalData;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    ArrayList<Integer> category_ids = new ArrayList<Integer>();
    ArrayList<String> category_names = new ArrayList<String>();
    LinearLayout contentLayout;
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
        initData("");

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
    private void initData(String text){
        ProgressDialog mProgressDialog = new ProgressDialog(RecipesActivity.this);
        mProgressDialog.setTitle("Loading...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();

        REST rest = MainApplication.getContainer().get(REST.class);
        Call<Wrappers.Collection<Category>> call = rest.CategoriesIndex();
        rest.CategoriesIndex()
                .enqueue(new Callback<Wrappers.Collection<Category>>() {
                    @Override
                    public void onResponse(Call<Wrappers.Collection<Category>> call, Response<Wrappers.Collection<Category>> response) {
                        List<Category> categories = response.body().data;
                        for (int i = 0; i < categories.size(); i++) {
                            Category cat = categories.get(i);
                            category_ids.add(cat.id);
                            category_names.add(cat.name);
                            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            titleParams.setMargins(0,10,0,5);
                            LinearLayout titlelayout = new LinearLayout(RecipesActivity.this);
                            titlelayout.setOrientation(LinearLayout.VERTICAL);
                            titlelayout.setLayoutParams(titleParams);

                            contentLayout.addView(titlelayout);

                            TextView titleText = new TextView(RecipesActivity.this);
                            LinearLayout.LayoutParams textparams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );

                            textparams.setMargins(0, 0, 10, 5);
                            titleText.setLayoutParams(textparams);
                            titleText.setTextSize(getResources().getDimension(R.dimen.recipe_title));
                            titleText.setTextColor(getResources().getColor(R.color.dayTextColor));
                            titleText.setText(category_names.get(i));
                            titlelayout.addView(titleText);

                            HorizontalScrollView scrollView = new HorizontalScrollView(RecipesActivity.this);
                            titlelayout.addView(scrollView);

                            LinearLayout.LayoutParams recipeParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            recipeParams.setMargins(1, 1, 1, 1);
                            LinearLayout recipelayout = new LinearLayout(RecipesActivity.this);
                            recipelayout.setOrientation(LinearLayout.HORIZONTAL);
                            recipelayout.setLayoutParams(recipeParams);
                            scrollView.addView(recipelayout);

                            rest.RecipesByCategory(category_ids.get(i), text)
                                    .enqueue(new Callback<Wrappers.Collection<Recipe>>() {
                                        @Override
                                        public void onResponse(Call<Wrappers.Collection<Recipe>> call, Response<Wrappers.Collection<Recipe>> response) {
                                            List<Recipe> recipes = response.body().data;
                                            for (int j = 0; j < recipes.size(); j ++) {
                                                Recipe recipe = recipes.get(j);
                                                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                cardParams.setMargins(30,5,5,5);
                                                CardView card = new CardView(RecipesActivity.this);
                                                card.setId(recipe.id);
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
                                                LinearLayout itemlayout = new LinearLayout(RecipesActivity.this);
                                                itemlayout.setOrientation(LinearLayout.VERTICAL);
                                                itemlayout.setLayoutParams(itemParams);
                                                card.addView(itemlayout);

                                                //-----bitmap--------//
                                                String storageUrl = Common.getInstance().getImageUrl();//"http://zafeiraki.com/images/image_1624268588.png";
                                                URL imageUrl = null;
                                                try {
                                                    imageUrl = new URL(storageUrl + recipe.image);
                                                } catch (MalformedURLException e) {
                                                    e.printStackTrace();
                                                }

                                                //-------------------//

                                                LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(550, 400);
                                                ImageView imgView = new ImageView(RecipesActivity.this);
                                                imgView.setLayoutParams(imageParams);
                                                imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                                imgView.setImageDrawable(getResources().getDrawable(R.drawable.back_recipe1));
                                                Picasso.get().load(String.valueOf(imageUrl)).resize(300, 0).into(imgView);
                                                itemlayout.addView(imgView);

                                                LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                nameParams.setMargins(15, 5, 0, 0);
                                                String title = recipe.title;
                                                TextView nameText = new TextView(RecipesActivity.this);
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
                                                TextView pointText = new TextView(RecipesActivity.this);
                                                pointText.setLayoutParams(nameParams);
                                                pointText.setTextColor(getResources().getColor(R.color.colorText1));
                                                pointText.setTextSize(7);
                                                if (PersonalData.getInstance().getDietMode() == DietMode.POINT) {
                                                    pointText.setText(String.format(Locale.US, "%.1f", 10.2)+" points");
                                                }
                                                else {
                                                    pointText.setText(String.format(Locale.US, "%.1f", 8.5)+" units");
                                                }
                                                itemlayout.addView(pointText);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Wrappers.Collection<Recipe>> call, Throwable t) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onFailure(Call<Wrappers.Collection<Category>> call, Throwable t) {

                    }
                });

        mProgressDialog.dismiss();
    }
    private void setupSearchView() {
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        contentLayout.removeAllViews();
        initData(newText);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }
}