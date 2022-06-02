package com.diet.trinity.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.diet.trinity.MainApplication;
import com.diet.trinity.R;
import com.diet.trinity.Utility.Common;
import com.diet.trinity.Utility.Global;
import com.diet.trinity.data.api.REST;
import com.diet.trinity.data.common.DietMode;
import com.diet.trinity.data.common.PersonalData;
import com.diet.trinity.data.models.Category;
import com.diet.trinity.data.models.Recipe;
import com.diet.trinity.data.models.Wrappers;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFooditemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fooditem);

        Button button = findViewById(R.id.next_btn);
        EditText editFoodName = findViewById(R.id.food_name);
        EditText editCategories = findViewById(R.id.food_categories_id);
        EditText editCarbon = findViewById(R.id.carbon);
        EditText editProtein = findViewById(R.id.protein);
        EditText editFat = findViewById(R.id.fat);
        EditText editPortionInGrams = findViewById(R.id.portion_in_grams);
        EditText editKcal = findViewById(R.id.kcal);
        EditText editServingSize = findViewById(R.id.serving_size);
        EditText editServingPrefix = findViewById(R.id.serving_prefix);
        EditText editBarcode = findViewById(R.id.barcode);

        String barcode = getIntent().getStringExtra("barcode");
        editBarcode.setText(barcode);

        ProgressDialog mProgressDialog = new ProgressDialog(AddFooditemActivity.this);
        mProgressDialog.setTitle("Loading...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();

        REST rest = MainApplication.getContainer().get(REST.class);
        rest.FoodCategoriesIndex()
                .enqueue(new Callback<Wrappers.Collection<Category>>() {
                    @Override
                    public void onResponse(Call<Wrappers.Collection<Category>> call, Response<Wrappers.Collection<Category>> response) {
                        List<Category> categories = response.body().data;

                        boolean[] selectedLanguage;
                        ArrayList<Integer> langList = new ArrayList<>();
                        selectedLanguage = new boolean[categories.size()];

                        List<String> arr_category = new ArrayList<String>();
                        List<Integer> arr_id = new ArrayList<Integer>();

                        for (int i = 0; i < categories.size(); i++) {
                            Category cat = categories.get(i);
                            Log.e(i+"", cat.name);
                            arr_category.add(cat.name);
                            arr_id.add(cat.id);
                        }

                        String[] langArray = new String[arr_category.size()];
                        arr_category.toArray(langArray);

                        editCategories.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Initialize alert dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(AddFooditemActivity.this);

                                // set title
                                builder.setTitle("Επέλεξε κατηγορία/ες");

                                // set dialog non cancelable
                                builder.setCancelable(false);

                                builder.setMultiChoiceItems(langArray, selectedLanguage, new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                        // check condition
                                        if (b) {
                                            // when checkbox selected
                                            // Add position  in lang list
                                            langList.add(i);
                                            // Sort array list
                                            Collections.sort(langList);
                                        } else {
                                            // when checkbox unselected
                                            // Remove position from langList
                                            langList.remove(Integer.valueOf(i));
                                        }
                                    }
                                });

                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // Initialize string builder
                                        StringBuilder stringBuilder = new StringBuilder();
                                        // use for loop
                                        for (int j = 0; j < langList.size(); j++) {
                                            // concat array value
                                            stringBuilder.append(langArray[langList.get(j)]);
                                            // check condition
                                            if (j != langList.size() - 1) {
                                                // When j value  not equal
                                                // to lang list size - 1
                                                // add comma
                                                stringBuilder.append(", ");
                                            }
                                        }
                                        // set text on textView
                                        editCategories.setText(stringBuilder.toString());

                                        ArrayList<Integer> filterId = new ArrayList<Integer>();
                                        for (int k = 0; k< langList.size(); k++) {
                                            filterId.add(arr_id.get(langList.get(k)));
                                        }
                                        Global.food_categories_id = new Integer[]{};
                                        Global.food_categories_id = filterId.toArray(Global.food_categories_id);
                                        Global.food_categories = stringBuilder.toString();
                                    }
                                });

                                builder.setNegativeButton("ΑΚΥΡΟ", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // dismiss dialog
                                        dialogInterface.dismiss();
                                    }
                                });
                                builder.setNeutralButton("ΕΚΚΑΘΑΡΙΣΗ", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // use for loop
                                        for (int j = 0; j < selectedLanguage.length; j++) {
                                            // remove all selection
                                            selectedLanguage[j] = false;
                                            // clear language list
                                            langList.clear();
                                            // clear text view value
                                            editCategories.setText("");
                                        }
                                    }
                                });
                                // show dialog
                                builder.show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<Wrappers.Collection<Category>> call, Throwable t) {

                    }
                });

        mProgressDialog.dismiss();

        editServingPrefix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] prefixes = { "τεμάχιο/ια", "φλιτζάνι/ια", "κουταλιά/ες σούπας", "κουταλάκι/ια γλυκού", "μερίδα/ες", "φέτα/ες", "μέτριο/ια" };

                // Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(AddFooditemActivity.this);

                // set title
                builder.setTitle("Επίλεξε πρόθεμα");

                // set dialog non cancelable
                builder.setCancelable(false);

                builder.setItems(prefixes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editServingPrefix.setText(prefixes[which]);
                    }
                });

                builder.show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.food_name = editFoodName.getText().toString();
                Global.carbon = Double.parseDouble(editCarbon.getText().toString());
                Global.protein = Double.parseDouble(editProtein.getText().toString());
                Global.fat = Double.parseDouble(editFat.getText().toString());
                Global.portion_in_grams = Double.parseDouble(editPortionInGrams.getText().toString());
                Global.kcal = Double.parseDouble(editKcal.getText().toString());
                Global.serving_size = Double.parseDouble(editServingSize.getText().toString());
                Global.serving_prefix = editServingPrefix.getText().toString();
                Global.barcode = editBarcode.getText().toString();

                Intent intent = new Intent(AddFooditemActivity.this, FooditemPreviewActivity.class);
                intent.putExtra("date", getIntent().getStringExtra("date"));
                startActivity(intent);
            }
        });
    }
}