package com.diet.trinity.data.datasources;

import static com.diet.trinity.data.common.LoadingState.ERROR;
import static com.diet.trinity.data.common.LoadingState.IDLE;
import static com.diet.trinity.data.common.LoadingState.LOADED;
import static com.diet.trinity.data.common.LoadingState.LOADING;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;

import com.diet.trinity.MainApplication;
import com.diet.trinity.data.api.REST;
import com.diet.trinity.data.common.LoadingState;
import com.diet.trinity.data.models.Recipe;
import com.diet.trinity.data.models.Wrappers;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRecipeDataSource extends PageKeyedDataSource<Integer, Recipe> {

    private static final String TAG = "RecipeDataSource";
    public static final String EXTRA_USER = "user";
    private final Bundle mParams;

    public final MutableLiveData<LoadingState> state = new MutableLiveData<>();

    public MyRecipeDataSource(@NonNull Bundle params) {
        mParams = params;
        state.postValue(IDLE);
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, Recipe> callback) {
        state.postValue(LOADING);
        REST rest = MainApplication.getContainer().get(REST.class);
        rest.RecipesIndex(1, mParams.getString("q"), mParams.getInt("category"), mParams.getInt("user_id"))
                .enqueue(new Callback<Wrappers.Paginated<Recipe>>() {
                    @Override
                    public void onResponse(@Nullable Call<Wrappers.Paginated<Recipe>> call, @Nullable Response<Wrappers.Paginated<Recipe>> response) {
                        Log.v(TAG, "Server responded with " + response.code() + " status.");
                        if (response.isSuccessful()) {
                            Wrappers.Paginated<Recipe> Recipes = response.body();
                            //noinspection ConstantConditions

                            callback.onResult(Recipes.data, null, 2);
                            state.postValue(LOADED);
                        } else {
                            state.postValue(ERROR);
                        }
                    }

                    @Override
                    public void onFailure(Call<Wrappers.Paginated<Recipe>> call, Throwable t) {
                        Log.e(TAG, "Fetching Recipes has failed.", t);
                        state.postValue(ERROR);
                    }
                });
    }

    @Override
    public void loadBefore(
            @NonNull LoadParams<Integer> params,
            @NonNull LoadCallback<Integer, Recipe> callback
    ) {
    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, Recipe> callback) {
        state.postValue(LOADING);
        REST rest = MainApplication.getContainer().get(REST.class);
        int user = mParams.getInt(EXTRA_USER);
        rest.RecipesIndex(params.key, mParams.getString("q"), mParams.getInt("category"), mParams.getInt("user_id"))
                .enqueue(new Callback<Wrappers.Paginated<Recipe>>() {

                    @Override
                    public void onResponse(
                            @Nullable Call<Wrappers.Paginated<Recipe>> call,
                            @Nullable Response<Wrappers.Paginated<Recipe>> response
                    ) {
                        //noinspection ConstantConditions
                        Log.v(TAG, "Server responded with " + response.code() + " status.");
                        if (response.isSuccessful()) {
                            Wrappers.Paginated<Recipe> Recipes = response.body();
                            //noinspection ConstantConditions
                            callback.onResult(Recipes.data,params.key + 1);
                            state.postValue(LOADED);
                        } else {
                            state.postValue(ERROR);
                        }
                    }

                    @Override
                    public void onFailure(
                            @Nullable Call<Wrappers.Paginated<Recipe>> call,
                            @Nullable Throwable t
                    ) {
                        Log.e(TAG, "Fetching Recipes has failed.", t);
                        state.postValue(ERROR);
                    }
                });
    }

    public static class Factory extends DataSource.Factory<Integer, Recipe> {

        @NonNull public Bundle params;

        public MutableLiveData<MyRecipeDataSource> source = new MutableLiveData<>();

        public Factory(@NonNull Bundle params) {
            this.params = params;
        }

        @NonNull
        @Override
        public DataSource<Integer, Recipe> create() {
            MyRecipeDataSource source = new MyRecipeDataSource(params);
            this.source.postValue(source);
            return source;
        }
    }
}