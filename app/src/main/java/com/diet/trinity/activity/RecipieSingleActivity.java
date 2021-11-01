package com.diet.trinity.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.diet.trinity.R;
import com.diet.trinity.Utility.Common;
import com.diet.trinity.Utility.Global;
import com.diet.trinity.Utility.MealDatabaseHelper;
import com.diet.trinity.Utility.ReceipeDatabaseHelper;
import com.diet.trinity.Utility.Recipe_FoodDatabaseHelper;
import com.diet.trinity.model.Recipe;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class RecipieSingleActivity extends AppCompatActivity{
    Integer food_id=0;
    ImageView foodImage, pdfDownload;
    TextView description_txt, title_txt, points_txt, ingredient_txt, ingredient_grams_txt, meal_label;
    private SQLiteDatabase db, db1, db_meal;
    private SQLiteOpenHelper openHelper, openHelper1, openHelper_meal;
    ArrayList<Recipe> RecipeItems = new ArrayList<>();
    private LinearLayout rootContent;

    ScrollView scrollView;
    LinearLayout ll_linear;

    String dirpath;
    LinearLayout relativeLayout;

    String food_names = "";
    String food_grams = "";
    Spinner meal_dropdown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipie_single);
        food_id=getIntent().getIntExtra("foodID", 0);

        foodImage = findViewById(R.id.foodImage);
        description_txt = findViewById(R.id.description);
        title_txt = findViewById(R.id.titletxt);
        points_txt = findViewById(R.id.pointstxt);
        rootContent = findViewById(R.id.contentLayout);
        pdfDownload = findViewById(R.id.pdfDownload);

        meal_dropdown = findViewById(R.id.mealDropdown);
        meal_label = findViewById(R.id.txtMealLabel);
        meal_dropdown.setVisibility(View.GONE);
        meal_label.setVisibility(View.GONE);
        //ingredient_txt = findViewById(R.id.ingredient);
        //ingredient_grams_txt = findViewById(R.id.ingredient_grams);
        openHelper_meal = new MealDatabaseHelper(this);
        db_meal = openHelper_meal.getWritableDatabase();
        Global.timing = "breakfast";
        ActivityCompat.requestPermissions(RecipieSingleActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        //-----screen shot--------//
        scrollView = findViewById(R.id.rootView);
        ll_linear = findViewById(R.id.contentLayout);
        relativeLayout =  findViewById(R.id.contentLayout);
        //-----------------------//

        try {
            addEventListener();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void addEventListener() throws MalformedURLException {

        openHelper = new ReceipeDatabaseHelper(this);
        db = openHelper.getWritableDatabase();

        final Cursor cursor = db.rawQuery("SELECT *FROM " + ReceipeDatabaseHelper.TABLE_NAME, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getInt(cursor.getColumnIndex("ID")) == food_id) {
                        String imageStr = cursor.getString(cursor.getColumnIndex(ReceipeDatabaseHelper.COL_5));
                        String descStr = cursor.getString(cursor.getColumnIndex(ReceipeDatabaseHelper.COL_4));
                        String titleStr = cursor.getString(cursor.getColumnIndex(ReceipeDatabaseHelper.COL_2));
                        String pointStr = cursor.getString(cursor.getColumnIndex(ReceipeDatabaseHelper.COL_7));

                        //-----bitmap--------//
                        String storageUrl = Common.getInstance().getImageUrl();//"http://zafeiraki.com/storage";
                        URL imageUrl = new URL(storageUrl + imageStr);

                        Picasso.get().load(String.valueOf(imageUrl)).into(foodImage);
                        description_txt.setText(Html.fromHtml(descStr));
                        title_txt.setText(titleStr);
                        points_txt.setText(pointStr);
                    }
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        }

        openHelper1 = new Recipe_FoodDatabaseHelper(this);
        db1 = openHelper1.getWritableDatabase();
        food_names = "";
        food_grams = "";
        LinearLayout content = findViewById(R.id.content);
        final Cursor cursor1 = db1.rawQuery("SELECT *FROM " + Recipe_FoodDatabaseHelper.TABLE_NAME, null);
        if (cursor1 != null) {
            if (cursor1.moveToFirst()) {
                do {
                    if (cursor1.getInt(cursor1.getColumnIndex(Recipe_FoodDatabaseHelper.COL_1)) == food_id) {
                        String food_name = cursor1.getString(cursor1.getColumnIndex(Recipe_FoodDatabaseHelper.COL_5));
                        String gram_s = cursor1.getString(cursor1.getColumnIndex(Recipe_FoodDatabaseHelper.COL_2));

                        food_names = food_name;
                        food_grams = gram_s+"gr";

                        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        titleParams.setMargins(0,0,0,5);
                        LinearLayout titlelayout = new LinearLayout(this);
                        titlelayout.setOrientation(LinearLayout.HORIZONTAL);
                        titlelayout.setLayoutParams(titleParams);

                        content.addView(titlelayout);

                        TextView titleText = new TextView(this);
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

                        TextView titleText1 = new TextView(this);
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
                }
                while (cursor1.moveToNext());
            }
            cursor1.close();
        }

        findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipieSingleActivity.this, RecipesActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.imgPlus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (meal_dropdown.getVisibility() == View.VISIBLE) {
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
                if(position==1)
                {
                    Global.timing = "breakfast";
                }
                else if(position==2)
                {
                    Global.timing = "lunch";
                }
                else if(position==3)
                {
                    Global.timing = "dinner";
                }
                else if(position==4)
                {
                    Global.timing = "snack_breakfast";
                }
               else if(position==5)
                {
                    Global.timing = "snack_lunch";
                }
                else if(position==6)
                {
                    Global.timing = "snack_dinner";
                }

                final Cursor cursor1 = db.rawQuery("SELECT *FROM " + ReceipeDatabaseHelper.TABLE_NAME,  null);
                final Cursor cursor2 = db1.rawQuery("SELECT *FROM " + Recipe_FoodDatabaseHelper.TABLE_NAME,  null);

                if (cursor1 != null && position != 0) {
                    if (cursor1.moveToFirst()) {
                        do {
                            if (food_id == cursor1.getInt(cursor1.getColumnIndex("ID"))) {
                                Global.carbon = 0;
                                Global.protein = 0;
                                Global.fat = 0;
                                Global.points = 0;
                                Global.total = 0;
                                Global.categoryid = Integer.parseInt(cursor1.getString(cursor1.getColumnIndex(ReceipeDatabaseHelper.COL_3)));
                                String foodname = cursor1.getString(cursor1.getColumnIndex("Title"));
                                String categoryid = "";
                                float grams=0;
                                if (cursor2 != null) {
                                    if (cursor2.moveToFirst()) {
                                        do {
                                            float portion = 0;
                                            float amount = 0;
                                            if (food_id == cursor2.getInt(cursor2.getColumnIndex("ID"))) {
                                                portion = cursor2.getFloat(cursor2.getColumnIndex("Grams"));
                                                amount = cursor2.getFloat(cursor2.getColumnIndex("Amount"));

                                                Global.carbon += (cursor2.getFloat(cursor2.getColumnIndex("Carbon"))) * amount / portion;
                                                Global.protein += (cursor2.getFloat(cursor2.getColumnIndex("Protein"))) * amount / portion;
                                                Global.fat += (cursor2.getFloat(cursor2.getColumnIndex("Fat"))) * amount / portion;
                                                float kcal = (cursor2.getFloat(cursor2.getColumnIndex("kcal")));
                                                grams += amount;

                                                if (categoryid == "") {
                                                    categoryid += cursor2.getString(cursor2.getColumnIndex("Food_Category_ID"));
                                                }
                                                else {
                                                    categoryid += ","+cursor2.getString(cursor2.getColumnIndex("Food_Category_ID"));
                                                }

                                            }
                                        } while (cursor2.moveToNext());
                                    }
                                    cursor2.close();
                                }

                                String carbon_m = Global.carbon+"";
                                String protein_m = Global.protein+"";
                                String fat_m = Global.fat+"";
                                String gram_m = grams+"";
                                String points_m = cursor1.getString(cursor1.getColumnIndex("Points"));
                                String units_m = cursor1.getString(cursor1.getColumnIndex("Units"));
                                String date = getCurrentDate();
                                String timing = Global.timing;
                                insertData(foodname, carbon_m, protein_m, fat_m, gram_m, points_m,units_m, categoryid, timing, date);
                            }
                        } while (cursor1.moveToNext());
                    }
                    cursor1.close();
                }
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

    public String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
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


        OutputStream outputStream = null;

        try{
            File imgFile = new File(android.os.Environment.getExternalStorageDirectory() + File.separator + title + "image.png");

            outputStream = new FileOutputStream(imgFile);
            viewBitmap.compress(Bitmap.CompressFormat.PNG, 40, outputStream);
            outputStream.close();
        }
        catch (Exception e){
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
    public void insertData(String foodname, String carbon_m, String protein_m, String fat_m, String gram_m,String points_m, String units_m, String category_id,String timing, String date){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MealDatabaseHelper.COL_2, foodname);
        contentValues.put(MealDatabaseHelper.COL_3, carbon_m);
        contentValues.put(MealDatabaseHelper.COL_4, protein_m);
        contentValues.put(MealDatabaseHelper.COL_5, fat_m);
        contentValues.put(MealDatabaseHelper.COL_6, gram_m);
        contentValues.put(MealDatabaseHelper.COL_7, points_m);
        contentValues.put(MealDatabaseHelper.COL_8, category_id);
        contentValues.put(MealDatabaseHelper.COL_9, timing);
        contentValues.put(MealDatabaseHelper.COL_10, date);
        contentValues.put(MealDatabaseHelper.COL_11, units_m);
        db_meal.insert(MealDatabaseHelper.TABLE_NAME,null,contentValues);
    }
}