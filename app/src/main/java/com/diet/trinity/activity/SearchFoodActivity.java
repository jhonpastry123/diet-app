package com.diet.trinity.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.diet.trinity.Adapter.CustomAdapter;
import com.diet.trinity.Utility.Constants;
import com.diet.trinity.R;
import com.diet.trinity.Utility.FoodDatabaseHelper;
import com.diet.trinity.Utility.Global;
import com.diet.trinity.Utility.MealDatabaseHelper;
import com.diet.trinity.Utility.ReceipeDatabaseHelper;
import com.diet.trinity.Utility.Recipe_FoodDatabaseHelper;
import com.diet.trinity.data.common.DiffUtilCallback;
import com.diet.trinity.data.common.LoadingState;
import com.diet.trinity.data.datasources.FoodItemDataSource;
import com.diet.trinity.data.datasources.RecipeDataSource;
import com.diet.trinity.data.models.FoodItem;
import com.diet.trinity.data.models.FoodValue;
import com.diet.trinity.data.models.Recipe;
import com.diet.trinity.data.common.Goal;
import com.diet.trinity.model.Listmodel;
import com.diet.trinity.data.common.PersonalData;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter;


public class SearchFoodActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    ArrayList<Listmodel> Listitem=new ArrayList<Listmodel>();
    private SQLiteDatabase db, db1, db2, db3, db_recipe_food;
    private SQLiteOpenHelper openHelper, openHelper1, openHelper2, openHelper3, openHelper_recipe_food;
    RelativeLayout loading;
    RecyclerView listView;
    String  foodname, carbon_m, protein_m, fat_m, gram_m;
    int id;
    CustomAdapter myAdapter;
    boolean food_recipe=true; //food
    ImageView food_btn, recipe_btn;
    SearchView searchView;
    String points_m="",units_m="";
    String categoryid="",date="", timing="";
    int food_id;

    String prefix = "";
    float size = 0;
    private SearchFoodActivityViewModel mModel;
    private Bundle mParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);

        food_id = getIntent().getIntExtra("foodID", 0);
        searchView = findViewById(R.id.search_view);
        addEventListener();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mParams = new Bundle();
        SearchFoodActivityViewModel.Factory factory =
                new SearchFoodActivityViewModel.Factory(mParams);
        mModel = new ViewModelProvider(this, factory).get(SearchFoodActivityViewModel.class);

        RecyclerView fooditems = findViewById(R.id.mainlistview1);
        FoodItemAdapter adapter1 = new FoodItemAdapter();
        fooditems.setAdapter(new SlideInLeftAnimationAdapter(adapter1));
        mModel.fooditems.observe(this, adapter1::submitList);
        SwipeRefreshLayout swipe1 = findViewById(R.id.swipe1);
        swipe1.setOnRefreshListener(() -> {
            FoodItemDataSource source = mModel.factory.source.getValue();
            if (source != null) {
                source.invalidate();
            }
        });

        View empty = findViewById(R.id.empty);
        View loading = findViewById(R.id.loading);

        mModel.state.observe(this, state -> {
            if (state != LoadingState.LOADING) {
                swipe1.setRefreshing(false);
            }

            List<?> list = mModel.fooditems.getValue();
            if (state == LoadingState.LOADING) {
                empty.setVisibility(View.GONE);
            } else {
                fooditems.setVisibility(list == null || list.isEmpty() ? View.GONE : View.VISIBLE);
                empty.setVisibility(list == null || list.isEmpty() ? View.VISIBLE : View.GONE);
            }

            loading.setVisibility(state == LoadingState.LOADING ? View.VISIBLE : View.GONE);
        });

        RecyclerView recipes = findViewById(R.id.mainlistview);
        RecipeAdapter adapter = new RecipeAdapter();
        recipes.setAdapter(new SlideInLeftAnimationAdapter(adapter));
        mModel.recipes.observe(this, adapter::submitList);
        SwipeRefreshLayout swipe = findViewById(R.id.swipe);
        swipe.setOnRefreshListener(() -> {
            RecipeDataSource source = mModel.factory1.source.getValue();
            if (source != null) {
                source.invalidate();
            }
        });

        mModel.state1.observe(this, state -> {
            if (state != LoadingState.LOADING) {
                swipe.setRefreshing(false);
            }

            List<?> list = mModel.recipes.getValue();
            if (state == LoadingState.LOADING) {
                empty.setVisibility(View.GONE);
            } else {
                recipes.setVisibility(list == null || list.isEmpty() ? View.GONE : View.VISIBLE);
                empty.setVisibility(list == null || list.isEmpty() ? View.VISIBLE : View.GONE);
            }

            loading.setVisibility(state == LoadingState.LOADING ? View.VISIBLE : View.GONE);
        });

        food_btn = findViewById(R.id.foodbutton);
        recipe_btn = findViewById(R.id.reciepbutton);
        recipe_btn.setAlpha(0.5f);

        food_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipe_btn.setAlpha(0.5f);
                food_btn.setAlpha(1f);
                food_recipe = true;
                swipe1.setVisibility(View.VISIBLE);
                swipe.setVisibility(View.GONE);

                String query = searchView.getQuery().toString();
                mModel.factory.params.putString("q", query);
                FoodItemDataSource source = mModel.factory.source.getValue();
                if (source != null) {
                    source.invalidate();
                }
            }
        });

        recipe_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipe_btn.setAlpha(1f);
                food_btn.setAlpha(0.5f);
                food_recipe = false;
                swipe.setVisibility(View.VISIBLE);
                swipe1.setVisibility(View.GONE);
                String query = searchView.getQuery().toString();
                mModel.factory1.params.putString("q", query);
                RecipeDataSource source = mModel.factory1.source.getValue();
                if (source != null) {
                    source.invalidate();
                }
            }
        });

        setupSearchView();
    }

    private void addEventListener(){
        final TextView _count = findViewById(R.id.txtSelectedMealCount);

        findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(SearchFoodActivity.this, DailyCaleandarActivity.class);
//                intent.putExtra("yPos", getIntent().getIntExtra("yValue",0));
//                startActivity(intent);
                String timing = Global.timing;
                DailyCaleandarActivity.getInstance().initMeal_format();
                try {
                    DailyCaleandarActivity.getInstance().initMeal();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DailyCaleandarActivity.getInstance().SetTimer();
                DailyCaleandarActivity.getInstance().pieChartFormat();
                if (Global.meal_num == 1) {
                    try {
                        DailyCaleandarActivity.getInstance().Load_breakfast();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    DailyCaleandarActivity.getInstance().show_breakfast();
                }
                else if (Global.meal_num == 2) {
                    try {
                        DailyCaleandarActivity.getInstance().Load_lunch();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    DailyCaleandarActivity.getInstance().show_lunch();
                }

                else if (Global.meal_num == 3) {
                    try {
                        DailyCaleandarActivity.getInstance().Load_dinner();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    DailyCaleandarActivity.getInstance().show_dinner();
                }
                finish();
            }
        });

        listView = findViewById(R.id.mainlistview );
    }

    public void addFoodNumber(View v){
        final TextView _count = findViewById(R.id.txtSelectedMealCount);
        final ImageView _badge = findViewById(R.id.green_badge);
        int count = Integer.parseInt(_count.getText().toString()) + 1;
        _count.setText(String.valueOf(count));
        _count.setTextColor(getResources().getColor(R.color.calendar_active_month_bg));
        _count.setTextSize(25);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                _count.setTextColor(getResources().getColor(R.color.calendar_bg));
                _count.setTextSize(20);
            }
        }, 500);

        Goal goal = PersonalData.getInstance().getGoal();
        int no = v.getId();
        openHelper = new FoodDatabaseHelper(this);
        db = openHelper.getWritableDatabase();

        openHelper1 = new MealDatabaseHelper(this);
        db1 = openHelper1.getWritableDatabase();

        openHelper2 = new ReceipeDatabaseHelper(this);
        db2 = openHelper2.getWritableDatabase();


        openHelper3 = new Recipe_FoodDatabaseHelper(this);
        db3 = openHelper3.getWritableDatabase();

//-----------add food save----------//
        if(food_recipe) {
            final Cursor cursor = db.rawQuery("SELECT *FROM " + FoodDatabaseHelper.TABLE_NAME, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        if (no == cursor.getInt(cursor.getColumnIndex("ID"))) {
                            float portion = Float.parseFloat(cursor.getString(cursor.getColumnIndex("gram")));
                            Global.carbon = Float.parseFloat(cursor.getString(cursor.getColumnIndex("carbon"))) * Global.gram / portion;
                            Global.protein = Float.parseFloat(cursor.getString(cursor.getColumnIndex("protein"))) * Global.gram / portion;
                            Global.fat = Float.parseFloat(cursor.getString(cursor.getColumnIndex("fat"))) * Global.gram / portion;
                            float kcal = Float.parseFloat(cursor.getString(cursor.getColumnIndex("kcal")));

                            double dPoint = (Global.carbon / 15 + Global.protein / 35 + Global.fat / 15);
                            if (((dPoint * 1000) % 100 ) > 75 )
                                dPoint = Math.ceil(dPoint*10) / 10;
                            else
                                dPoint = Math.floor(dPoint*10) / 10;
                            Global.points = (float) dPoint;

                            double dUnit = kcal * Global.gram / portion / 100;
                            if (((dUnit * 1000) % 100 ) > 75 )
                                dUnit = Math.ceil(dUnit*10) / 10;
                            else
                                dUnit = Math.floor(dUnit*10) / 10;
                            Global.units = (float) dUnit;

                            Global.total += (Global.carbon + Global.protein + Global.fat);
                            Global.categoryid = Integer.parseInt(cursor.getString(cursor.getColumnIndex("categoryid")).split(",")[0]);


                            foodname = cursor.getString(cursor.getColumnIndex("foodname"));
                            carbon_m = cursor.getString(cursor.getColumnIndex("carbon"));
                            protein_m = cursor.getString(cursor.getColumnIndex("protein"));
                            fat_m = cursor.getString(cursor.getColumnIndex("fat"));
                            // gram_m = cursor.getString(cursor.getColumnIndex("gram"));
                            gram_m = Global.gram + "";
                            String points_m = Global.points + "";
                            String units_m = Global.units + "";
                            String categoryid = cursor.getString(cursor.getColumnIndex("categoryid"));
                            String date = getCurrentDate();
                            String timing = Global.timing;
                            if(Global.timing=="plus")
                            {
                                insertRecipe_Food(food_id, 0, Global.categoryid,Global.categoryid,foodname, Float.parseFloat(protein_m), Float.parseFloat(carbon_m), Float.parseFloat(fat_m), Float.parseFloat(gram_m), kcal);
                            }
                            else
                            insertData(foodname, Global.carbon+"", Global.protein+"", Global.fat+"", gram_m, points_m, units_m, categoryid, timing, date);
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        }
        else
        {
            final Cursor cursor1 = db2.rawQuery("SELECT *FROM " + ReceipeDatabaseHelper.TABLE_NAME,  null);
            final Cursor cursor2 = db3.rawQuery("SELECT *FROM " + Recipe_FoodDatabaseHelper.TABLE_NAME,  null);

            if (cursor1 != null) {
                if (cursor1.moveToFirst()) {
                    do {
                        if (no == cursor1.getInt(cursor1.getColumnIndex("ID"))) {
                            Global.carbon = 0;
                            Global.protein = 0;
                            Global.fat = 0;
                            Global.points = 0;
                            Global.total = 0;
                            foodname = cursor1.getString(cursor1.getColumnIndex("Title"));
                            String categoryid = "";

                            float grams=0;

                            if (cursor2 != null) {
                                if (cursor2.moveToFirst()) {
                                    do {
                                        float portion = 0;
                                        float amount = 0;
                                        if (no == cursor2.getInt(cursor2.getColumnIndex("ID"))) {
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

                            Global.total = (Global.carbon + Global.protein + Global.fat) ;
                            carbon_m = Global.carbon+"";
                            protein_m = Global.protein+"";
                            fat_m = Global.fat+"";
                            gram_m = grams+"";
                            points_m = cursor1.getString(cursor1.getColumnIndex("Points"));
                            units_m = cursor1.getString(cursor1.getColumnIndex("Units"));
                            date = getCurrentDate();
                            timing = Global.timing;

                            insertData(foodname, carbon_m, protein_m, fat_m, gram_m, points_m, units_m, categoryid, timing, date);
                        }
                    } while (cursor1.moveToNext());
                }
                cursor1.close();
            }
        }
    }

    private void reload() throws JSONException {
        //loading.setVisibility(View.VISIBLE);
        if(food_recipe) {
            Listitem.clear();
            openHelper = new FoodDatabaseHelper(this);
            db = openHelper.getWritableDatabase();
            final Cursor cursor = db.rawQuery("SELECT *FROM " + FoodDatabaseHelper.TABLE_NAME, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        id = cursor.getInt(cursor.getColumnIndex("ID"));
                        foodname = cursor.getString(cursor.getColumnIndex("foodname"));
                        carbon_m = cursor.getString(cursor.getColumnIndex("carbon"));
                        protein_m = cursor.getString(cursor.getColumnIndex("protein"));
                        fat_m = cursor.getString(cursor.getColumnIndex("fat"));
                        gram_m = cursor.getString(cursor.getColumnIndex("gram"));
                        float kcal = (cursor.getFloat(cursor.getColumnIndex("kcal")));

                        double dPoint = (Float.parseFloat(carbon_m) / 15 + Float.parseFloat(protein_m) / 35 + Float.parseFloat(fat_m) / 15);
                        if (((dPoint * 1000) % 100 ) > 75 )
                            dPoint = Math.ceil(dPoint*10) / 10;
                        else
                            dPoint = Math.floor(dPoint*10) / 10;
                        Global.points = (float) dPoint;

                        double dUnit = kcal / 100;
                        if (((dUnit * 1000) % 100 ) > 75 )
                            dUnit = Math.ceil(dUnit*10) / 10;
                        else
                            dUnit = Math.floor(dUnit*10) / 10;
                        Global.units = (float) dUnit;

                        String points_m = Global.points + "";
                        String units_m = Global.units + "";
                        Listmodel model = new Listmodel(id, foodname, carbon_m, protein_m, fat_m, gram_m, points_m, units_m);
                        model.setRecipe(false);
                        Listitem.add(model);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        }
        else {
            Listitem.clear();
            openHelper2 = new ReceipeDatabaseHelper(this);
            db2 = openHelper2.getWritableDatabase();
            final Cursor cursor1 = db2.rawQuery("SELECT *FROM " + ReceipeDatabaseHelper.TABLE_NAME,  null);

            openHelper3 = new Recipe_FoodDatabaseHelper(this);
            db3 = openHelper3.getWritableDatabase();
            final Cursor cursor2 = db3.rawQuery("SELECT *FROM " + Recipe_FoodDatabaseHelper.TABLE_NAME,  null);

            ArrayList<Integer> time_ids = new ArrayList<>();
            if (Global.timing == "breakfast") {
                time_ids.add(1);
                if (Global.meal_num == 1) {
                    time_ids.add(2);
                }
            }
            else if (Global.timing == "lunch")
                time_ids.add(2);
            else if (Global.timing == "dinner") {
                time_ids.add(3);
                if (Global.meal_num == 3) {
                    time_ids.add(2);
                }
            }
            else
                time_ids.add(4);
            if(cursor1 != null){
                if (cursor1.moveToFirst()){
                    do{
                        Integer id_s = cursor1.getInt(cursor1.getColumnIndex("ID"));
                        Integer category_id = cursor1.getInt(cursor1.getColumnIndex("Category_id"));
                        foodname = cursor1.getString(cursor1.getColumnIndex("Title"));
                        float carbon1 = 0;
                        float protein1 = 0;
                        float fat1 = 0;
                        float gram1 = 0;
                        float points = 0;
                        float units = 0;
                        float kcal = 0;
                        if(cursor2 != null){
                            if(cursor2.moveToFirst()){
                                do{
                                    if(id_s == cursor2.getInt(cursor2.getColumnIndex("ID"))) {
                                        carbon1 += (cursor2.getFloat(cursor2.getColumnIndex("Carbon")));
                                        protein1 += (cursor2.getFloat(cursor2.getColumnIndex("Protein")));
                                        fat1 += (cursor2.getFloat(cursor2.getColumnIndex("Fat")));
                                        float carbon = cursor2.getFloat(cursor2.getColumnIndex("Carbon"));
                                        float protein = cursor2.getFloat(cursor2.getColumnIndex("Protein"));
                                        float fat = cursor2.getFloat(cursor2.getColumnIndex("Fat"));
                                        float amount = cursor2.getFloat(cursor2.getColumnIndex("Amount"));
                                        float gram = cursor2.getFloat(cursor2.getColumnIndex("Grams"));
                                    }
                                }while(cursor2.moveToNext());
                            }
                        }
                        points = cursor1.getFloat(cursor1.getColumnIndex("Points"));
                        units = cursor1.getFloat(cursor1.getColumnIndex("Units"));
                        if (time_ids.contains(category_id)) {
                            Listmodel model = new Listmodel(id_s,foodname, carbon1+"", protein1+"",fat1+"",gram1+"", points+"", units+"");
                            model.setRecipe(true);
                            Listitem.add(model);
                        }

                    }while(cursor1.moveToNext());
                }
                cursor1.close();
            }

        }
        myAdapter = new CustomAdapter(SearchFoodActivity.this, Listitem);
//        listView.setAdapter(myAdapter);
//        listView.setTextFilterEnabled(false);
        setupSearchView();
    }

    private void setupSearchView() {
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (recipe_btn.getAlpha() == 1f) {
            mModel.factory1.params.putString("q", newText);
            RecipeDataSource source = mModel.factory1.source.getValue();
            if (source != null) {
                source.invalidate();
            }
        } else {
            mModel.factory.params.putString("q", newText);
            FoodItemDataSource source = mModel.factory.source.getValue();
            if (source != null) {
                source.invalidate();
            }
        }

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }

    public void insertData(String foodname, String carbon_m, String protein_m, String fat_m, String gram_m,String points_m,String units_m, String category_id,String timing, String date){
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
        db1.insert(MealDatabaseHelper.TABLE_NAME,null,contentValues);
    }

    public String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }
    public void insertRecipe_Food(Integer ID, Integer amount, float food_id, Integer food_category_id, String foodname, float carbon, float protein, float fat, float grams, float kcal)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Recipe_FoodDatabaseHelper.COL_1, ID);
        contentValues.put(Recipe_FoodDatabaseHelper.COL_2, amount);
        contentValues.put(Recipe_FoodDatabaseHelper.COL_3, food_id);
        contentValues.put(Recipe_FoodDatabaseHelper.COL_4, food_category_id);
        contentValues.put(Recipe_FoodDatabaseHelper.COL_5, foodname);
        contentValues.put(Recipe_FoodDatabaseHelper.COL_6, carbon);
        contentValues.put(Recipe_FoodDatabaseHelper.COL_7, protein);
        contentValues.put(Recipe_FoodDatabaseHelper.COL_8, fat);
        contentValues.put(Recipe_FoodDatabaseHelper.COL_9, grams);
        contentValues.put(Recipe_FoodDatabaseHelper.COL_10, kcal);
        db3.insert(Recipe_FoodDatabaseHelper.TABLE_NAME,null,contentValues);
    }

    public void selectGram(final View view) {
        size = 0;
        prefix = "";
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.input_serving_size, null);
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
        final TextView servingInput = (TextView) promptsView.findViewById(R.id.serving_size_symbol);

        final TextView gram_view = (TextView) promptsView.findViewById(R.id.txt_gram);
        Button plus = (Button)promptsView.findViewById(R.id.btn_plus_serving_size);
        Button minus = (Button)promptsView.findViewById(R.id.btn_minus_serving_size);

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double serving_size = 0;
                String input_serving_size = userInput.getText().toString();
                try{
                    serving_size = Double.valueOf(input_serving_size);
                } catch(NumberFormatException ex){ // handle your exception
                    serving_size = 0;
                }
                userInput.setText(serving_size + 1 + "");
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double serving_size = 0;
                String input_serving_size = userInput.getText().toString();
                try{
                    serving_size = Double.valueOf(input_serving_size);
                    if (serving_size > 1)
                        serving_size --;
                } catch(NumberFormatException ex){ // handle your exception
                    serving_size = 0;
                }
                userInput.setText(serving_size + "");
            }
        });

        if (food_recipe) {
            int no = view.getId();
            openHelper = new FoodDatabaseHelper(this);
            db = openHelper.getWritableDatabase();

            final Cursor cursor = db.rawQuery("SELECT *FROM " + FoodDatabaseHelper.TABLE_NAME, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        if (no == cursor.getInt(cursor.getColumnIndex("ID"))) {
                            prefix = cursor.getString(cursor.getColumnIndex("serving_prefix"));
                            servingInput.setText(prefix);
                            size = Float.parseFloat(cursor.getString(cursor.getColumnIndex("serving_size")));
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }

            userInput.addTextChangedListener(new TextWatcher() {
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(!s.equals("") ) {
                        double serving_size = 0;
                        try{
                            serving_size = Double.valueOf(userInput.getText().toString());
                        } catch(NumberFormatException ex){ // handle your exception
                            serving_size = 0;
                        }
                        String result = "";
                        if (size == 0) {
                            result = 0 + " " + "Γραμμάρια";
                        }
                        else {
                            result = String.format(Locale.US, "%.2f", serving_size * size) + " " + "Γραμμάρια";
                        }
                        gram_view.setText(result);

                        Global.gram = Float.parseFloat(String.format(Locale.US, "%.2f", serving_size * size));
                    }
                }
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                public void afterTextChanged(Editable s) {
                }
            });

            alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // get user input and set it to result
                            // edit text
                        String gram = gram_view.getText().toString();
                        if (gram == "" || gram == null) {
                            Toast.makeText(SearchFoodActivity.this, "Υπολόγισε πόσο θέλεις να φας:", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else {
                            addFoodNumber(view);
                        }
                        }
                    })
                .setNegativeButton("Άκυρο",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }
        else {
            addFoodNumber(view);
        }
    }

    public static class SearchFoodActivityViewModel extends ViewModel {

        public SearchFoodActivityViewModel(@NonNull Bundle params) {
            PagedList.Config config = new PagedList.Config.Builder()
                    .setPageSize(Constants.DEFAULT_PAGE_SIZE)
                    .build();
            factory = new FoodItemDataSource.Factory(params);
            fooditems = new LivePagedListBuilder<>(factory, config).build();
            state = Transformations.switchMap(factory.source, input -> input.state);

            factory1 = new RecipeDataSource.Factory(params);
            recipes = new LivePagedListBuilder<>(factory1, config).build();
            state1 = Transformations.switchMap(factory1.source, input -> input.state);
        }

        public final LiveData<PagedList<FoodItem>> fooditems;
        public final LiveData<PagedList<Recipe>> recipes;
        public final FoodItemDataSource.Factory factory;
        public final RecipeDataSource.Factory factory1;
        public final LiveData<LoadingState> state;
        public final LiveData<LoadingState> state1;

        private static class Factory implements ViewModelProvider.Factory {

            @NonNull private final Bundle mParams;

            public Factory(@NonNull Bundle params) {
                mParams = params;
            }

            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                //noinspection unchecked
                return (T) new SearchFoodActivityViewModel(mParams);
            }
        }
    }

    private class FoodItemAdapter extends PagedListAdapter<FoodItem, FoodItemViewHolder> {

        public FoodItemAdapter() {
            super(new DiffUtilCallback<>(i -> i.id));
        }

        @Override
        public void onBindViewHolder(@NonNull FoodItemViewHolder holder, int position) {
            FoodItem food = getItem(position);

            holder.title.setText(food.food_name);
            String gram = "";
            if (food.portion_in_grams != 0f) {
                gram = "Ποσότητα (" + food.portion_in_grams + "γρ.)";
            }
            holder.gram_txt.setText(gram);

            double dPoint = (food.carbon / 15 + food.protein / 35 + food.fat / 15);
            if (((dPoint * 1000) % 100 ) > 75 )
                dPoint = Math.ceil(dPoint*10) / 10;
            else
                dPoint = Math.floor(dPoint*10) / 10;

            double dUnit = food.kcal / 100;
            if (((dUnit * 1000) % 100 ) > 75 )
                dUnit = Math.ceil(dUnit*10) / 10;
            else
                dUnit = Math.floor(dUnit*10) / 10;

            String point_txt = String.format(Locale.US, "%.1f", dPoint) + " points / " + String.format(Locale.US, "%.1f", dUnit) + " units";
            holder.point_txt.setText(point_txt);
            holder.id_btn.setId(food.id);

        }

        @NonNull
        @Override
        public FoodItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(SearchFoodActivity.this)
                    .inflate(R.layout.mainlist_item, parent, false);
            return new FoodItemViewHolder(view);
        }
    }

    private static class FoodItemViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView gram_txt;
        TextView point_txt;
        ImageView id_btn;

        public FoodItemViewHolder(@NonNull View root) {
            super(root);

            title=(TextView) root.findViewById(R.id.listitem_title);
            gram_txt=(TextView) root.findViewById(R.id.gram_txt);
            point_txt=(TextView) root.findViewById(R.id.point_txt);
            id_btn=(ImageView) root.findViewById(R.id.imgAddMeal);
        }
    }

    private class RecipeAdapter extends PagedListAdapter<Recipe, RecipeViewHolder> {

        public RecipeAdapter() {
            super(new DiffUtilCallback<>(i -> i.id));
        }

        @Override
        public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
            Recipe recipe = getItem(position);

            holder.title.setText(recipe.title);

            Integer id_s = recipe.id;
            Integer category_id = recipe.categories_id;
            float carbon1 = 0;
            float protein1 = 0;
            float fat1 = 0;
            float gram1 = 0;
            float points = 0;
            float units = 0;
            float amounts = 0;

            for (int i = 0; i < recipe.foodvalues.size(); i ++ ){
                FoodValue food = recipe.foodvalues.get(i);

                float carbon = food.food_item.carbon;
                float protein = food.food_item.protein;
                float fat = food.food_item.fat;
                float grams = food.food_item.portion_in_grams;
                float kcal = food.food_item.kcal;
                int amount = food.amount;

                carbon1 += carbon;
                protein1 += protein;
                fat1 += fat;
                amounts += amount;

                double dPoint = (carbon / 15 + protein / 35 + fat / 15) * amount / grams;
                if (((dPoint * 1000) % 100 ) > 75 )
                    dPoint = Math.ceil(dPoint*10) / 10;
                else
                    dPoint = Math.floor(dPoint*10) / 10;
                points += dPoint;
                double dUnit = kcal * amount / grams / 100;
                if (((dUnit * 1000) % 100 ) > 75 )
                    dUnit = Math.ceil(dUnit*10) / 10;
                else
                    dUnit = Math.floor(dUnit*10) / 10;
                units += dUnit;
            }

            holder.title.setText(recipe.title);
            String gram = "";
            if (amounts != 0f) {
                gram = "Ποσότητα (" + amounts + "γρ.)";
            }
            holder.gram_txt.setText(gram);

            String point_txt = String.format(Locale.US, "%.1f", points) + " points / " + String.format(Locale.US, "%.1f", units) + " units";
            holder.point_txt.setText(point_txt);
            holder.id_btn.setId(recipe.id);
        }

        @NonNull
        @Override
        public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(SearchFoodActivity.this)
                    .inflate(R.layout.mainlist_item, parent, false);
            return new RecipeViewHolder(view);
        }
    }

    private static class RecipeViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView gram_txt;
        TextView point_txt;
        ImageView id_btn;

        public RecipeViewHolder(@NonNull View root) {
            super(root);

            title=(TextView) root.findViewById(R.id.listitem_title);
            gram_txt=(TextView) root.findViewById(R.id.gram_txt);
            point_txt=(TextView) root.findViewById(R.id.point_txt);
            id_btn=(ImageView) root.findViewById(R.id.imgAddMeal);
        }
    }
}
