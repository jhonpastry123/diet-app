package com.diet.trinity.activity;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.diet.trinity.data.datasources.FoodItemDataSource;
import com.diet.trinity.data.datasources.RecipeDataSource;
import com.diet.trinity.data.models.FoodItem;
import com.diet.trinity.data.models.Recipe;
import com.diet.trinity.data.models.Wrappers;

import java.util.List;
import java.util.Locale;

import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchFoodActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    RecyclerView listView;
    ImageView food_btn, recipe_btn;
    SearchView searchView;
    private SearchFoodActivityViewModel mModel;
    private Bundle mParams;
    float size;
    String prefix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);

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
                swipe.setVisibility(View.VISIBLE);
                swipe1.setVisibility(View.GONE);
                String query = searchView.getQuery().toString();
                mModel.factory1.params.putString("q", query);
                if (Global.timing_id > 3) {
                    mModel.factory1.params.putInt("category", 4);
                } else {
                    mModel.factory1.params.putInt("category", Global.timing_id);
                }
                RecipeDataSource source = mModel.factory1.source.getValue();
                if (source != null) {
                    source.invalidate();
                }
            }
        });

        setupSearchView();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SearchFoodActivity.this, DailyCaleandarActivity.class);
        startActivity(intent);
        finish();
    }

    private void addEventListener(){
        final TextView _count = findViewById(R.id.txtSelectedMealCount);

        findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchFoodActivity.this, DailyCaleandarActivity.class);
                startActivity(intent);
                finish();
            }
        });

        listView = findViewById(R.id.mainlistview);
    }

    public void addFoodNumber(){
        final TextView _count = findViewById(R.id.txtSelectedMealCount);
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
            if (Global.timing_id > 3) {
                mModel.factory1.params.putInt("category", 4);
            } else {
                mModel.factory1.params.putInt("category", Global.timing_id);
            }
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


    public void selectGram(final View view) {
        int food_id = view.getId();
        if (food_btn.getAlpha() == 1f) {
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

        REST rest = MainApplication.getContainer().get(REST.class);
        rest.MealStore(user_id, food_id, recipe_id, gram, timing_id)
                .enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        Boolean flag = response.body();
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

            String point_txt = String.format(Locale.US, "%.1f", food.points) + " points / " + String.format(Locale.US, "%.1f", food.units) + " units";
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
            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SearchFoodActivity.this, RecipeSingleActivity.class);
                    intent.putExtra("foodID", v.getId());
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
