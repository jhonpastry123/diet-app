package com.diet.trinity.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

import com.diet.trinity.MainApplication;
import com.diet.trinity.R;
import com.diet.trinity.Utility.Constants;
import com.diet.trinity.Utility.Global;
import com.diet.trinity.data.api.REST;
import com.diet.trinity.data.common.DiffUtilCallback;
import com.diet.trinity.data.common.LoadingState;
import com.diet.trinity.data.common.PersonalData;
import com.diet.trinity.data.datasources.FoodItemDataSource;
import com.diet.trinity.data.datasources.MyFoodItemDataSource;
import com.diet.trinity.data.datasources.MyRecipeDataSource;
import com.diet.trinity.data.datasources.RecipeDataSource;
import com.diet.trinity.data.models.FoodItem;
import com.diet.trinity.data.models.FoodValue;
import com.diet.trinity.data.models.Recipe;
import com.diet.trinity.data.models.Wrappers;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchFoodActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    SearchView searchView;
    ImageView food_btn, recipe_btn, my_food_btn, my_recipe_btn, imageView, warning_back;
    ImageButton warning_btn;
    RecyclerView fooditems, recipes, myFooditems, myRecipes;
    SwipeRefreshLayout swipe_food, swipe_recipe, swipe_my_food, swipe_my_recipe;
    private SearchFoodActivityViewModel mModel;
    private Bundle mParams;
    float size;
    String prefix;
    View empty, loading;
    int membership = 0;
    RelativeLayout layout1, layout2;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // if the intentResult is null then
        // toast a message as "cancelled"
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                // if the intentResult is not null we'll set
                // the content and format of scan message
                Log.e("Contents", intentResult.getContents());
                Toast.makeText(getBaseContext(), "Barcode : "+intentResult.getContents(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SearchFoodActivity.this, AddFooditemActivity.class);
                intent.putExtra("date", getIntent().getStringExtra("date"));
                intent.putExtra("barcode", intentResult.getContents());

                startActivity(intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initView();

        String param = getIntent().getStringExtra("activity");
        if (param != null && param.equals("add_meal")) {
            findViewById(R.id.recipe_layout).setVisibility(View.GONE);
            findViewById(R.id.my_recipe_layout).setVisibility(View.GONE);
        } else {
            findViewById(R.id.recipe_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.my_recipe_layout).setVisibility(View.VISIBLE);
        }

        membership = PersonalData.getInstance().getMembership();

        mParams = new Bundle();
        SearchFoodActivityViewModel.Factory factory =
                new SearchFoodActivityViewModel.Factory(mParams);
        mModel = new ViewModelProvider(this, factory).get(SearchFoodActivityViewModel.class);

        FoodItemAdapter adapter_food = new FoodItemAdapter();
        fooditems.setAdapter(new SlideInLeftAnimationAdapter(adapter_food));
        mModel.fooditems.observe(this, adapter_food::submitList);

        mModel.state_food.observe(this, state -> {
            if (state != LoadingState.LOADING) {
                swipe_food.setRefreshing(false);
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

        RecipeAdapter adapter = new RecipeAdapter();
        recipes.setAdapter(new SlideInLeftAnimationAdapter(adapter));
        mModel.recipes.observe(this, adapter::submitList);

        mModel.state_recipe.observe(this, state -> {
            if (state != LoadingState.LOADING) {
                swipe_recipe.setRefreshing(false);
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

        MyFoodItemAdapter adapter_my_food = new MyFoodItemAdapter();
        myFooditems.setAdapter(new SlideInLeftAnimationAdapter(adapter_my_food));
        mModel.myFooditems.observe(this, adapter_my_food::submitList);

        mModel.state_my_food.observe(this, state -> {
            if (state != LoadingState.LOADING) {
                swipe_my_food.setRefreshing(false);
            }

            List<?> list = mModel.myFooditems.getValue();
            if (state == LoadingState.LOADING) {
                empty.setVisibility(View.GONE);
            } else {
                myFooditems.setVisibility(list == null || list.isEmpty() ? View.GONE : View.VISIBLE);
                empty.setVisibility(list == null || list.isEmpty() ? View.VISIBLE : View.GONE);
            }

            loading.setVisibility(state == LoadingState.LOADING ? View.VISIBLE : View.GONE);
        });

        MyRecipeAdapter my_adapter = new MyRecipeAdapter();
        myRecipes.setAdapter(new SlideInLeftAnimationAdapter(my_adapter));
        mModel.myRecipes.observe(this, my_adapter::submitList);

        mModel.state_my_recipe.observe(this, state -> {
            if (state != LoadingState.LOADING) {
                swipe_my_recipe.setRefreshing(false);
            }

            List<?> list = mModel.myRecipes.getValue();
            if (state == LoadingState.LOADING) {
                empty.setVisibility(View.GONE);
            } else {
                myRecipes.setVisibility(list == null || list.isEmpty() ? View.GONE : View.VISIBLE);
                empty.setVisibility(list == null || list.isEmpty() ? View.VISIBLE : View.GONE);
            }

            loading.setVisibility(state == LoadingState.LOADING ? View.VISIBLE : View.GONE);
        });

        recipe_btn.setAlpha(0.3f);
        my_food_btn.setAlpha(0.3f);
        my_recipe_btn.setAlpha(0.3f);

        addEventListener();
        setupSearchView();
    }

    private void initView() {

        searchView = findViewById(R.id.search_view);

        fooditems = findViewById(R.id.food_listview);
        recipes = findViewById(R.id.recipe_listview);
        myFooditems = findViewById(R.id.my_food_listview);
        myRecipes = findViewById(R.id.my_recipe_listview);

        swipe_food = findViewById(R.id.swipe_food);
        swipe_recipe = findViewById(R.id.swipe_recipe);
        swipe_my_food = findViewById(R.id.swipe_my_food);
        swipe_my_recipe = findViewById(R.id.swipe_my_recipe);

        food_btn = findViewById(R.id.foodbutton);
        recipe_btn = findViewById(R.id.recipebutton);
        my_food_btn = findViewById(R.id.myfoodbutton);
        my_recipe_btn = findViewById(R.id.myrecipebutton);
        imageView = findViewById(R.id.imageView);

        empty = findViewById(R.id.empty);
        loading = findViewById(R.id.loading);

        warning_back = findViewById(R.id.warning_back);
        warning_btn = findViewById(R.id.warning_btn);

        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SearchFoodActivity.this, DailyCaleandarActivity.class);
        startActivity(intent);
        finish();
    }

    private void addEventListener(){
        findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String param = getIntent().getStringExtra("activity");
                if (param!=null && param.equals("meal")) {
                    Intent intent = new Intent(SearchFoodActivity.this, MealListActivity.class);
                    intent.putExtra("date", new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
                    startActivity(intent);
                    finish();

                } else if (param!=null && param.equals("add_meal")) {
                    Intent intent = new Intent(SearchFoodActivity.this, AddMealActivity.class);
                    intent.putExtra("date", new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
                    startActivity(intent);
                    finish();

                } else {
                    Intent intent = new Intent(SearchFoodActivity.this, DailyCaleandarActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        swipe_food.setOnRefreshListener(() -> {
            FoodItemDataSource source = mModel.factory_food.source.getValue();
            if (source != null) {
                source.invalidate();
            }
        });

        swipe_recipe.setOnRefreshListener(() -> {
            RecipeDataSource source = mModel.factory_recipe.source.getValue();
            if (source != null) {
                source.invalidate();
            }
        });

        swipe_my_food.setOnRefreshListener(() -> {
            MyFoodItemDataSource source = mModel.factory_my_food.source.getValue();
            if (source != null) {
                source.invalidate();
            }
        });

        swipe_my_recipe.setOnRefreshListener(() -> {
            MyRecipeDataSource source = mModel.factory_my_recipe.source.getValue();
            if (source != null) {
                source.invalidate();
            }
        });

        food_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                food_btn.setAlpha(1f);
                recipe_btn.setAlpha(0.3f);
                my_food_btn.setAlpha(0.3f);
                my_recipe_btn.setAlpha(0.3f);

                swipe_food.setVisibility(View.VISIBLE);
                swipe_recipe.setVisibility(View.GONE);
                swipe_my_food.setVisibility(View.GONE);
                swipe_my_recipe.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.GONE);

                String query = searchView.getQuery().toString();
                mModel.factory_food.params.putString("q", query);
                FoodItemDataSource source = mModel.factory_food.source.getValue();
                if (source != null) {
                    source.invalidate();
                }
            }
        });

        recipe_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                food_btn.setAlpha(0.3f);
                recipe_btn.setAlpha(1f);
                my_food_btn.setAlpha(0.3f);
                my_recipe_btn.setAlpha(0.3f);

                swipe_food.setVisibility(View.GONE);
                swipe_recipe.setVisibility(View.VISIBLE);
                swipe_my_food.setVisibility(View.GONE);
                swipe_my_recipe.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.GONE);

                String query = searchView.getQuery().toString();
                mModel.factory_recipe.params.putString("q", query);
                if (Global.timing_id > 3) {
                    mModel.factory_recipe.params.putInt("category", 4);
                } else {
                    mModel.factory_recipe.params.putInt("category", Global.timing_id);
                }
                RecipeDataSource source = mModel.factory_recipe.source.getValue();
                if (source != null) {
                    source.invalidate();
                }
            }
        });

        my_food_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                food_btn.setAlpha(0.3f);
                recipe_btn.setAlpha(0.3f);
                my_food_btn.setAlpha(1f);
                my_recipe_btn.setAlpha(0.3f);

                imageView.setImageResource(R.drawable.ic_barcode);
                imageView.setTag(R.drawable.ic_barcode);

                swipe_food.setVisibility(View.GONE);
                swipe_recipe.setVisibility(View.GONE);
                swipe_my_recipe.setVisibility(View.GONE);
                swipe_my_food.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);

                if (membership == 3) {
                    layout1.setVisibility(View.VISIBLE);
                    layout2.setVisibility(View.GONE);
                } else {
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.VISIBLE);
                }

                String query = searchView.getQuery().toString();
                int user_id = Global.user_id;
                mModel.factory_my_food.params.putString("q", query);
                mModel.factory_my_food.params.putInt("user_id", user_id);
                MyFoodItemDataSource source = mModel.factory_my_food.source.getValue();
                if (source != null) {
                    source.invalidate();
                }
            }
        });

        my_recipe_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                food_btn.setAlpha(0.3f);
                recipe_btn.setAlpha(0.3f);
                my_food_btn.setAlpha(0.3f);
                my_recipe_btn.setAlpha(1f);

                imageView.setImageResource(R.drawable.ic_plus);
                imageView.setTag(R.drawable.ic_plus);

                swipe_food.setVisibility(View.GONE);
                swipe_recipe.setVisibility(View.GONE);
                swipe_my_food.setVisibility(View.GONE);
                swipe_my_recipe.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);

                if (membership == 3) {
                    layout1.setVisibility(View.VISIBLE);
                    layout2.setVisibility(View.GONE);
                } else {
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.VISIBLE);
                }

                String query = searchView.getQuery().toString();
                mModel.factory_my_recipe.params.putString("q", query);
                int user_id = Global.user_id;
                mModel.factory_my_recipe.params.putInt("user_id", user_id);
                if (Global.timing_id > 3) {
                    mModel.factory_my_recipe.params.putInt("category", 4);
                } else {
                    mModel.factory_my_recipe.params.putInt("category", Global.timing_id);
                }
                MyRecipeDataSource source = mModel.factory_my_recipe.source.getValue();
                if (source != null) {
                    source.invalidate();
                }
            }
        });

        warning_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchFoodActivity.this, TrialNotifyActivity.class);
                startActivity(intent);
                finish();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView image = (ImageView) v;
                Integer integer = (Integer) image.getTag();
                integer = integer == null ? 0 : integer;
                switch (integer) {
                    case R.drawable.ic_barcode:
                        IntentIntegrator intentIntegrator = new IntentIntegrator(SearchFoodActivity.this);
                        intentIntegrator.setPrompt("Σαρώστε έναν γραμμωτό κώδικα");
                        intentIntegrator.setOrientationLocked(true);
                        intentIntegrator.initiateScan();
                        break;
                    case R.drawable.ic_plus:
                        Intent intent = new Intent(SearchFoodActivity.this, AddMealActivity.class);
                        intent.putExtra("date", getIntent().getStringExtra("date"));
                        startActivity(intent);
                    default:
                        break;
                }
            }
        });
    }

    public void addFoodNumber(){
        final TextView _count = findViewById(R.id.txtSelectedMealCount);
        int count = Integer.parseInt(_count.getText().toString()) + 1;
        _count.setText(String.valueOf(count));
        _count.setTextColor(getResources().getColor(R.color.colorAccent));
        _count.setTextSize(25);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                _count.setTextColor(getResources().getColor(R.color.colorCancel));
                _count.setTextSize(20);
            }
        }, 500);
    }

    private void setupSearchView() {
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (recipe_btn.getAlpha() == 1f) {
            mModel.factory_recipe.params.putString("q", newText);
            if (Global.timing_id > 3) {
                mModel.factory_recipe.params.putInt("category", 4);
            } else {
                mModel.factory_recipe.params.putInt("category", Global.timing_id);
            }
            RecipeDataSource source = mModel.factory_recipe.source.getValue();
            if (source != null) {
                source.invalidate();
            }
        } else if (my_recipe_btn.getAlpha() == 1f) {
            mModel.factory_my_recipe.params.putString("q", newText);
            if (Global.timing_id > 3) {
                mModel.factory_my_recipe.params.putInt("category", 4);
            } else {
                mModel.factory_my_recipe.params.putInt("category", Global.timing_id);
            }
            MyRecipeDataSource source = mModel.factory_my_recipe.source.getValue();
            if (source != null) {
                source.invalidate();
            }
        } else if (food_btn.getAlpha() == 1f) {
            mModel.factory_food.params.putString("q", newText);
            FoodItemDataSource source = mModel.factory_food.source.getValue();
            if (source != null) {
                source.invalidate();
            }
        } else if (my_food_btn.getAlpha() == 1f) {
            mModel.factory_my_food.params.putString("q", newText);
            MyFoodItemDataSource source = mModel.factory_my_food.source.getValue();
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


    public void selectGram(final View view) {
        int food_id = view.getId();
        if (food_btn.getAlpha() == 1f || my_food_btn.getAlpha() == 1f) {
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

            REST rest = MainApplication.getContainer().get(REST.class);
            rest.FoodItemShow(food_id)
                    .enqueue(new Callback<Wrappers.Single<FoodItem>>() {
                        @Override
                        public void onResponse(Call<Wrappers.Single<FoodItem>> call, Response<Wrappers.Single<FoodItem>> response) {
                            FoodItem item = response.body().data;
                            size = item.serving_size;
                            prefix = item.serving_prefix;
                            servingInput.setText(prefix);
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
                                                        addMeal(food_id, 0, Global.gram);
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

                        @Override
                        public void onFailure(Call<Wrappers.Single<FoodItem>> call, Throwable t) {

                        }
                    });
        }
        else {
            addMeal(0, food_id, 0);
        }
    }

    public void addMeal(int food_id, int recipe_id, float gram) {
        int user_id = Global.user_id;
        int timing_id = Global.timing_id;

        String date = getIntent().getStringExtra("date");

        String param = getIntent().getStringExtra("activity");
        if (param != null && param.equals("add_meal")) {
            REST rest = MainApplication.getContainer().get(REST.class);
            rest.FoodItemShow(food_id)
                    .enqueue(new Callback<Wrappers.Single<FoodItem>>() {
                        @Override
                        public void onResponse(Call<Wrappers.Single<FoodItem>> call, Response<Wrappers.Single<FoodItem>> response) {
                            FoodItem item = response.body().data;
                            FoodValue foodValue = new FoodValue();
                            foodValue.id = 0;
                            foodValue.recipes_id = 0;
                            foodValue.amount = (int)gram;
                            foodValue.food_items_id = food_id;
                            foodValue.food_item = item;

                            ArrayList<FoodValue> values = new ArrayList<>();
                            for (int i=0; i<Global.food_values.length; i++) {
                                values.add(Global.food_values[i]);
                            }
                            values.add(foodValue);
                            Global.food_values = values.toArray(Global.food_values);
                            addFoodNumber();
                        }

                        @Override
                        public void onFailure(Call<Wrappers.Single<FoodItem>> call, Throwable t) {

                        }
                    });

        } else {
            REST rest = MainApplication.getContainer().get(REST.class);
            rest.MealStore(user_id, food_id, recipe_id, gram, timing_id, date)
                    .enqueue(new Callback<Boolean>() {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                            Boolean flag = response != null ? response.body() : false;
                            if (flag) {
                                addFoodNumber();
                            }
                            else {
                                Toast.makeText(SearchFoodActivity.this, R.string.offline_text, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Boolean> call, Throwable t) {
                            Toast.makeText(SearchFoodActivity.this, R.string.offline_text, Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    public static class SearchFoodActivityViewModel extends ViewModel {

        public SearchFoodActivityViewModel(@NonNull Bundle params) {
            PagedList.Config config = new PagedList.Config.Builder()
                    .setPageSize(Constants.DEFAULT_PAGE_SIZE)
                    .build();
            factory_food = new FoodItemDataSource.Factory(params);
            fooditems = new LivePagedListBuilder<>(factory_food, config).build();
            state_food = Transformations.switchMap(factory_food.source, input -> input.state);

            factory_recipe = new RecipeDataSource.Factory(params);
            recipes = new LivePagedListBuilder<>(factory_recipe, config).build();
            state_recipe = Transformations.switchMap(factory_recipe.source, input -> input.state);

            factory_my_food = new MyFoodItemDataSource.Factory(params);
            myFooditems = new LivePagedListBuilder<>(factory_my_food, config).build();
            state_my_food = Transformations.switchMap(factory_my_food.source, input -> input.state);

            factory_my_recipe = new MyRecipeDataSource.Factory(params);
            myRecipes = new LivePagedListBuilder<>(factory_my_recipe, config).build();
            state_my_recipe = Transformations.switchMap(factory_my_recipe.source, input -> input.state);
        }

        public final LiveData<PagedList<FoodItem>> fooditems;
        public final LiveData<PagedList<Recipe>> recipes;
        public final LiveData<PagedList<FoodItem>> myFooditems;
        public final LiveData<PagedList<Recipe>> myRecipes;
        public final FoodItemDataSource.Factory factory_food;
        public final RecipeDataSource.Factory factory_recipe;
        public final MyFoodItemDataSource.Factory factory_my_food;
        public final MyRecipeDataSource.Factory factory_my_recipe;
        public final LiveData<LoadingState> state_food;
        public final LiveData<LoadingState> state_recipe;
        public final LiveData<LoadingState> state_my_food;
        public final LiveData<LoadingState> state_my_recipe;

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

    private class FoodItemAdapter extends PagedListAdapter<FoodItem, ItemViewHolder> {

        public FoodItemAdapter() {
            super(new DiffUtilCallback<>(i -> i.id));
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            FoodItem food = getItem(position);

            holder.title.setText(food.food_name);
            String gram = "";
            if (food.portion_in_grams != 0f) {
                gram = "Ποσότητα (" + food.portion_in_grams + "γρ.)";
            }
            holder.gram_txt.setText(gram);

            String point_txt = String.format(Locale.US, "%.1f", food.points) + " points / " + String.format(Locale.US, "%.1f", food.units) + " units";
            holder.point_txt.setText(point_txt);
            holder.id_btn.setId(food.id);
            holder.rm_btn.setVisibility(View.GONE);
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(SearchFoodActivity.this)
                    .inflate(R.layout.mainlist_item, parent, false);
            return new ItemViewHolder(view);
        }
    }

    private class RecipeAdapter extends PagedListAdapter<Recipe, ItemViewHolder> {

        public RecipeAdapter() {
            super(new DiffUtilCallback<>(i -> i.id));
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            Recipe recipe = getItem(position);

            holder.title.setText(recipe.title);
            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SearchFoodActivity.this, RecipeSingleActivity.class);
                    intent.putExtra("foodID", v.getId());
                    intent.putExtra("activity", "search");
                    String date = getIntent().getStringExtra("date");
                    intent.putExtra("date", date);
                    startActivity(intent);
                }
            });
            String gram = "";
            if (recipe.amount != 0f) {
                gram = "Ποσότητα (" + recipe.amount + "γρ.)";
            }
            holder.gram_txt.setText(gram);

            String point_txt = String.format(Locale.US, "%.1f", recipe.points) + " points / " + String.format(Locale.US, "%.1f", recipe.units) + " units";
            holder.point_txt.setText(point_txt);
            holder.id_btn.setId(recipe.id);
            holder.title.setId(recipe.id);
            holder.rm_btn.setVisibility(View.GONE);
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(SearchFoodActivity.this)
                    .inflate(R.layout.mainlist_item, parent, false);
            return new ItemViewHolder(view);
        }
    }

    private class MyFoodItemAdapter extends PagedListAdapter<FoodItem, ItemViewHolder> {

        public MyFoodItemAdapter() {
            super(new DiffUtilCallback<>(i -> i.id));
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            FoodItem food = getItem(position);

            holder.title.setText(food.food_name);
            String gram = "";
            if (food.portion_in_grams != 0f) {
                gram = "Ποσότητα (" + food.portion_in_grams + "γρ.)";
            }
            holder.gram_txt.setText(gram);

            String point_txt = String.format(Locale.US, "%.1f", food.points) + " points / " + String.format(Locale.US, "%.1f", food.units) + " units";
            holder.point_txt.setText(point_txt);
            holder.id_btn.setId(food.id);
            holder.rm_btn.setId(food.id);
            holder.rm_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(holder.rm_btn.getContext());
                    alert.setTitle("Διαγράφω");
                    alert.setMessage("Θες να το διαγράψεις?\n");
                    alert.setPositiveButton("Ναι", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            REST rest = MainApplication.getContainer().get(REST.class);
                            rest.FoodItemDelete(v.getId())
                                    .enqueue(new Callback<Boolean>() {
                                        @Override
                                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                            Boolean flag = response.body();
                                            if (flag) {
                                                mModel.factory_my_food.source.getValue().invalidate();
                                                notifyItemRemoved(position);
                                                notifyItemRangeChanged(position, getCurrentList().size());
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Boolean> call, Throwable t) {

                                        }
                                    });
                            dialog.dismiss();
                        }
                    });

                    alert.setNegativeButton("Όχι", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alert.show();
                }
            });
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(SearchFoodActivity.this)
                    .inflate(R.layout.mainlist_item, parent, false);
            return new ItemViewHolder(view);
        }
    }

    private class MyRecipeAdapter extends PagedListAdapter<Recipe, ItemViewHolder> {

        public MyRecipeAdapter() {
            super(new DiffUtilCallback<>(i -> i.id));
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            Recipe recipe = getItem(position);

            holder.title.setText(recipe.title);
            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SearchFoodActivity.this, RecipeSingleActivity.class);
                    intent.putExtra("foodID", v.getId());
                    intent.putExtra("activity", "search");
                    String date = getIntent().getStringExtra("date");
                    intent.putExtra("date", date);
                    startActivity(intent);
                }
            });
            String gram = "";
            if (recipe.amount != 0f) {
                gram = "Ποσότητα (" + recipe.amount + "γρ.)";
            }
            holder.gram_txt.setText(gram);

            String point_txt = String.format(Locale.US, "%.1f", recipe.points) + " points / " + String.format(Locale.US, "%.1f", recipe.units) + " units";
            holder.point_txt.setText(point_txt);
            holder.id_btn.setId(recipe.id);
            holder.title.setId(recipe.id);
            holder.rm_btn.setId(recipe.id);
            holder.rm_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(holder.rm_btn.getContext());
                    alert.setTitle("Διαγράφω");
                    alert.setMessage("Θες να το διαγράψεις?\n");
                    alert.setPositiveButton("Ναι", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            REST rest = MainApplication.getContainer().get(REST.class);
                            rest.RecipeDelete(v.getId())
                                    .enqueue(new Callback<Boolean>() {
                                        @Override
                                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                            Boolean flag = response.body();
                                            if (flag) {
                                                mModel.factory_my_recipe.source.getValue().invalidate();
                                                notifyItemRemoved(position);
                                                notifyItemRangeChanged(position, getCurrentList().size());
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Boolean> call, Throwable t) {

                                        }
                                    });
                            dialog.dismiss();
                        }
                    });

                    alert.setNegativeButton("Όχι", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alert.show();
                }
            });
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(SearchFoodActivity.this)
                    .inflate(R.layout.mainlist_item, parent, false);
            return new ItemViewHolder(view);
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView gram_txt;
        TextView point_txt;
        ImageView rm_btn;
        ImageView id_btn;


        public ItemViewHolder(@NonNull View root) {
            super(root);

            title=root.findViewById(R.id.listitem_title);
            gram_txt=root.findViewById(R.id.gram_txt);
            point_txt=root.findViewById(R.id.point_txt);
            rm_btn=root.findViewById(R.id.imgRemoveMeal);
            id_btn=root.findViewById(R.id.imgAddMeal);
        }
    }

}
