package com.diet.trinity.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.diet.trinity.MainApplication;
import com.diet.trinity.R;
import com.diet.trinity.Utility.Common;
import com.diet.trinity.Utility.Global;
import com.diet.trinity.data.api.REST;
import com.diet.trinity.data.models.FoodItem;
import com.diet.trinity.data.models.FoodValue;
import com.diet.trinity.data.models.Recipe;
import com.diet.trinity.data.models.Wrappers;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeSingleActivity extends AppCompatActivity{
    int food_id=0, category=0;
    String act = "";
    ImageView foodImage, pdfDownload;
    TextView description_txt, title_txt, points_txt, meal_label;

    ScrollView scrollView;
    LinearLayout ll_linear;

    String dirpath;
    LinearLayout relativeLayout;

    String food_names = "";
    String food_grams = "";
    Spinner meal_dropdown;

    ArrayList<String> meal_list = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipie_single);

        food_id=getIntent().getIntExtra("foodID", 0);
        act=getIntent().getStringExtra("activity");

        foodImage = findViewById(R.id.foodImage);
        description_txt = findViewById(R.id.description);
        title_txt = findViewById(R.id.titletxt);
        points_txt = findViewById(R.id.pointstxt);
        pdfDownload = findViewById(R.id.pdfDownload);

        meal_dropdown = findViewById(R.id.mealDropdown);
        meal_label = findViewById(R.id.txtMealLabel);
        meal_dropdown.setVisibility(View.GONE);
        meal_label.setVisibility(View.GONE);


        //-----screen shot--------//
        scrollView = findViewById(R.id.rootView);
        ll_linear = findViewById(R.id.contentLayout);
        relativeLayout =  findViewById(R.id.contentLayout);
        //-----------------------//
        addEventListener();
        setupViews();
    }
    private void addEventListener() {
        findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (act.equals("recipes")) {
                    Intent intent = new Intent(RecipeSingleActivity.this, RecipesActivity.class);
                    startActivity(intent);
                } else if (act.equals("search")) {
                    Intent intent = new Intent(RecipeSingleActivity.this, SearchFoodActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        });
        findViewById(R.id.imgPlus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (meal_dropdown.getVisibility() == View.VISIBLE) {

                    switch (meal_dropdown.getSelectedItem().toString()){
                        case "ΠΡΩΙΝΟ":
                            category = 1;
                            break;
                        case "ΜΕΣΗΜΕΡΙΑΝΟ":
                            category = 2;
                            break;
                        case "ΒΡΑΔΙΝΟ":
                            category = 3;
                            break;
                        case "ΔΕΚΑΤΙΑΝΟ":
                            category = 4;
                            break;
                        case "ΑΠΟΓΕΥΜΑΤΙΝΟ":
                            category = 5;
                            break;
                        case "ΠΡΟ ΥΠΝΟΥ":
                            category = 6;
                            break;
                        default:
                            category = 0;
                            break;
                    }

                    ProgressDialog mProgressDialog = new ProgressDialog(RecipeSingleActivity.this);
                    mProgressDialog.setTitle("Loading...");
                    mProgressDialog.setIndeterminate(false);
                    mProgressDialog.show();

                    REST rest = MainApplication.getContainer().get(REST.class);
                    rest.MealStore(Global.user_id, 0, food_id, 0, category)
                            .enqueue(new Callback<Boolean>() {
                                @Override
                                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                    Boolean flag = response.body();
                                    if (flag) {
                                        Toast.makeText(RecipeSingleActivity.this, "Current recipe added to meal successfully!", Toast.LENGTH_SHORT).show();
                                        mProgressDialog.dismiss();
                                    }
                                    else {
                                        Toast.makeText(RecipeSingleActivity.this, R.string.offline_text, Toast.LENGTH_SHORT).show();
                                        mProgressDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Boolean> call, Throwable t) {
                                    Toast.makeText(RecipeSingleActivity.this, R.string.offline_text, Toast.LENGTH_SHORT).show();
                                    mProgressDialog.dismiss();
                                }
                            });
                    meal_dropdown.setVisibility(View.GONE);
                    meal_label.setVisibility(View.GONE);
                }
                else {
                    meal_dropdown.setVisibility(View.VISIBLE);
                    meal_label.setVisibility(View.VISIBLE);
                }
            }
        });
        meal_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Global.timing_id = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        pdfDownload.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //File file = saveBitMap(RecipieSingleActivity.this, relativeLayout);
                String fileStamp = new SimpleDateFormat("yyyymmdd_HHmmss").format(new Date());
                layoutToImage("recipe"+fileStamp);
                try {
                    imageToPDF("recipe"+fileStamp);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void setupViews() {
        ProgressDialog mProgressDialog = new ProgressDialog(RecipeSingleActivity.this);
        mProgressDialog.setTitle("Loading...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();

        REST rest = MainApplication.getContainer().get(REST.class);
        rest.RecipeShow(food_id)
                .enqueue(new Callback<Wrappers.Single<Recipe>>() {
                    @Override
                    public void onResponse(Call<Wrappers.Single<Recipe>> call, Response<Wrappers.Single<Recipe>> response) {
                        Recipe recipe = response.body().data;
                        String storageUrl = Common.getInstance().getImageUrl();
                        URL imageUrl = null;
                        try {
                            imageUrl = new URL(storageUrl + recipe.image);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                        Picasso.get().load(String.valueOf(imageUrl)).into(foodImage);
                        description_txt.setText(Html.fromHtml(recipe.description));
                        title_txt.setText(recipe.title);
                        points_txt.setText(recipe.points + " points / " + recipe.units + " units");

                        food_names = "";
                        food_grams = "";
                        LinearLayout content = findViewById(R.id.content);
                        List<FoodValue> values =  recipe.foodvalues;
                        for (int i = 0; i < values.size(); i++) {
                            FoodValue value = values.get(i);
                            FoodItem food = value.food_item;

                            food_names = food.food_name;
                            food_grams = value.amount + "gr";

                            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            titleParams.setMargins(0,0,0,5);
                            LinearLayout titlelayout = new LinearLayout(RecipeSingleActivity.this);
                            titlelayout.setOrientation(LinearLayout.HORIZONTAL);
                            titlelayout.setLayoutParams(titleParams);

                            content.addView(titlelayout);

                            TextView titleText = new TextView(RecipeSingleActivity.this);
                            LinearLayout.LayoutParams textparams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    1.0f
                            );

                            textparams.setMargins(0, 0, 0, 5);
                            titleText.setLayoutParams(textparams);
                            titleText.setTextSize(12);
                            titleText.setTextColor(getResources().getColor(R.color.dayTextColor));
                            titleText.setText(food_names);
                            titlelayout.addView(titleText);

                            TextView titleText1 = new TextView(RecipeSingleActivity.this);
                            LinearLayout.LayoutParams textparams1 = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );

                            textparams.setMargins(0, 0, 0, 5);
                            titleText1.setLayoutParams(textparams1);
                            titleText1.setGravity(Gravity.END);
                            titleText1.setTextSize(12);
                            titleText1.setTextColor(getResources().getColor(R.color.dayTextColor));
                            titleText1.setText(food_grams);
                            titlelayout.addView(titleText1);
                        }

                        Adapter adapter = meal_dropdown.getAdapter();
                        int n = adapter.getCount();

                        for (int i=0; i< n; i ++) {
                            meal_list.add(adapter.getItem(i).toString());
                        }

                        if (act.equals("recipes")) {
                            meal_list.clear();
                            switch (recipe.categories_id) {
                                case 4:
                                    meal_list.add(adapter.getItem(4).toString());
                                    meal_list.add(adapter.getItem(5).toString());
                                    meal_list.add(adapter.getItem(6).toString());
                                    break;
                                default:
                                    meal_list.add(adapter.getItem(recipe.categories_id).toString());
                                    break;
                            }
                        } else if (act.equals("search")){
                            meal_list.clear();
                            meal_list.add(adapter.getItem(Global.timing_id).toString());
                        }

                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(RecipeSingleActivity.this, R.layout.spinner_dropdown_item, meal_list);
                        meal_dropdown.setAdapter(spinnerArrayAdapter);

                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Wrappers.Single<Recipe>> call, Throwable t) {
                        mProgressDialog.dismiss();
                    }
                });
    }
    public void layoutToImage(String title) {

        Bitmap viewBitmap = Bitmap.createBitmap(relativeLayout.getWidth(), relativeLayout.getHeight(), Bitmap.Config.RGB_565);
        Canvas viewCanvas = new Canvas(viewBitmap);

        Drawable backgroundDrawable = relativeLayout.getBackground();

        if(backgroundDrawable != null)
        {
            backgroundDrawable.draw(viewCanvas);
        }
        else
        {
            viewCanvas.drawColor(Color.WHITE);
            relativeLayout.draw(viewCanvas);
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        viewBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);

        File imgFile = new File(android.os.Environment.getExternalStorageDirectory() + File.separator + title + "image.png");
        try {
            imgFile.createNewFile();
            FileOutputStream fo = new FileOutputStream(imgFile);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void imageToPDF(String title) throws FileNotFoundException {
        try {
            Document document = new Document();
            dirpath = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            PdfWriter.getInstance(document, new FileOutputStream(dirpath + "/"+title+".pdf")); //  Change pdf's name.
            document.open();
            Image img = Image.getInstance(android.os.Environment.getExternalStorageDirectory() + File.separator + title + "image.png");
            float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                    - document.rightMargin() - 0) / img.getWidth()) * 100;
            img.scalePercent(scaler);
            img.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
            document.add(img);
            document.close();
            Toast.makeText(this, "Η αποθήκευση ολοκληρώθηκε!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
    }
}