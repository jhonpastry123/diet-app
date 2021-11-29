package com.diet.trinity.data.api;

import androidx.annotation.Nullable;

import com.diet.trinity.data.models.Category;
import com.diet.trinity.data.models.FoodItem;
import com.diet.trinity.data.models.Information;
import com.diet.trinity.data.models.Recipe;
import com.diet.trinity.data.models.Sport;
import com.diet.trinity.data.models.Token;
import com.diet.trinity.data.models.User;
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
    Call<Token> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("register")
    Call<Token> register(
            @Field("email") String email,
            @Field("password") String password,
            @Field("type") int type,
            @Field("purchase_time") String purchase_time
    );

    @Headers("Accept: application/json")
    @POST("informations")
    Call<Boolean> InformationStore(
            @Query("customer_id") int id,
            @Query("goal") int goal,
            @Query("initial_weight") float initial_weight,
            @Query("weight") float weight,
            @Query("gender") int gender,
            @Query("height") float height,
            @Query("birthday") String birthday,
            @Query("gym_type") int gym_type,
            @Query("sport_type1") int sport_type1,
            @Query("sport_type2") int sport_type2,
            @Query("sport_type3") int sport_type3,
            @Query("sport_time1") int sport_time1,
            @Query("sport_time2") int sport_time2,
            @Query("sport_time3") int sport_time3,
            @Query("goal_weight") float goal_weight,
            @Query("weekly_goal") float weekly_goal,
            @Query("diet_mode") int diet_mode,
            @Query("purchase_time") String purchase_time
    );

    @Headers("Accept: application/json")
    @GET("informations/getInformation")
    Call<Wrappers.Single<Information>> getInformation(
            @Query("date") String date
    );

    @Headers("Accept: application/json")
    @GET("profile")
    Call<Wrappers.Single<User>> profileShow();

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

    @GET("recipes/list")
    Call<Wrappers.Collection<Recipe>> RecipesByCategory(
            @Query("index") @Nullable int index,
            @Query("q") @Nullable String q
    );

    @GET("categories")
    Call<Wrappers.Collection<Category>> CategoriesIndex();

    @GET("sports")
    Call<Wrappers.Collection<Sport>> SportsIndex();
}
