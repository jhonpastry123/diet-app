package com.diet.trinity.data.datasources;

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
import com.diet.trinity.data.models.FoodItem;
import com.diet.trinity.data.models.Wrappers;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.diet.trinity.data.common.LoadingState.*;

public class FoodItemDataSource extends PageKeyedDataSource<Integer, FoodItem> {

    private static final String TAG = "FoodItemDataSource";
    public static final String EXTRA_USER = "user";
    private final Bundle mParams;

    public final MutableLiveData<LoadingState> state = new MutableLiveData<>();

    public FoodItemDataSource(@NonNull Bundle params) {
        mParams = params;
        state.postValue(IDLE);
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, FoodItem> callback) {
        state.postValue(LOADING);
        REST rest = MainApplication.getContainer().get(REST.class);
        rest.FoodItemsIndex(1, mParams.getString("q"))
                .enqueue(new Callback<Wrappers.Paginated<FoodItem>>() {
                    @Override
                    public void onResponse(@Nullable Call<Wrappers.Paginated<FoodItem>> call, @Nullable Response<Wrappers.Paginated<FoodItem>> response) {
                        Log.v(TAG, "Server responded with " + response.code() + " status.");
                        if (response.isSuccessful()) {
                            Wrappers.Paginated<FoodItem> FoodItems = response.body();
                            //noinspection ConstantConditions

                            callback.onResult(FoodItems.data, null, 2);
                            state.postValue(LOADED);
                        } else {
                            state.postValue(ERROR);
                        }
                    }

                    @Override
                    public void onFailure(Call<Wrappers.Paginated<FoodItem>> call, Throwable t) {
                        Log.e(TAG, "Fetching FoodItems has failed.", t);
                        state.postValue(ERROR);
                    }
                });
    }

    @Override
    public void loadBefore(
            @NonNull LoadParams<Integer> params,
            @NonNull LoadCallback<Integer, FoodItem> callback
    ) {
    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, FoodItem> callback) {
        state.postValue(LOADING);
        REST rest = MainApplication.getContainer().get(REST.class);
        int user = mParams.getInt(EXTRA_USER);
        rest.FoodItemsIndex(params.key, mParams.getString("q"))
                .enqueue(new Callback<Wrappers.Paginated<FoodItem>>() {

                    @Override
                    public void onResponse(
                            @Nullable Call<Wrappers.Paginated<FoodItem>> call,
                            @Nullable Response<Wrappers.Paginated<FoodItem>> response
                    ) {
                        //noinspection ConstantConditions
                        Log.v(TAG, "Server responded with " + response.code() + " status.");
                        if (response.isSuccessful()) {
                            Wrappers.Paginated<FoodItem> FoodItems = response.body();
                            //noinspection ConstantConditions
                            callback.onResult(FoodItems.data,params.key + 1);
                            state.postValue(LOADED);
                        } else {
                            state.postValue(ERROR);
                        }
                    }

                    @Override
                    public void onFailure(
                            @Nullable Call<Wrappers.Paginated<FoodItem>> call,
                            @Nullable Throwable t
                    ) {
                        Log.e(TAG, "Fetching FoodItems has failed.", t);
                        state.postValue(ERROR);
                    }
                });
    }

    public static class Factory extends DataSource.Factory<Integer, FoodItem> {

        @NonNull public Bundle params;

        public MutableLiveData<FoodItemDataSource> source = new MutableLiveData<>();

        public Factory(@NonNull Bundle params) {
            this.params = params;
        }

        @NonNull
        @Override
        public DataSource<Integer, FoodItem> create() {
            FoodItemDataSource source = new FoodItemDataSource(params);
            this.source.postValue(source);
            return source;
        }
    }
}