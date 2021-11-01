package com.diet.trinity.data.api;

import android.media.session.MediaSession;

import androidx.annotation.Nullable;

import com.diet.trinity.data.models.FoodItem;
import com.diet.trinity.data.models.Recipe;
import com.diet.trinity.data.models.Wrappers;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface REST {
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("login")
    Call<MediaSession.Token> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("register")
    Call<MediaSession.Token> register(
            @Field("email") String email,
            @Field("password") String password
    );

    @Headers("Accept: application/json")
    @GET("fooditems")
    Call<Wrappers.Paginated<FoodItem>> FoodItemsIndex(
            @Query("page") int page,
            @Query("q") @Nullable String q
    );

    @GET("recipes")
    Call<Wrappers.Paginated<Recipe>> RecipesIndex(
            @Query("page") int page,
            @Query("q") @Nullable String q
    );
}
