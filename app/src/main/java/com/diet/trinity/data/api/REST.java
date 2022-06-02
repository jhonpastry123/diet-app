package com.diet.trinity.data.api;

import androidx.annotation.Nullable;

import com.diet.trinity.data.models.Category;
import com.diet.trinity.data.models.FoodItem;
import com.diet.trinity.data.models.GraphValue;
import com.diet.trinity.data.models.Information;
import com.diet.trinity.data.models.Meal;
import com.diet.trinity.data.models.Recipe;
import com.diet.trinity.data.models.Sport;
import com.diet.trinity.data.models.Token;
import com.diet.trinity.data.models.User;
import com.diet.trinity.data.models.Wrappers;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
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
    @POST("reset_password")
    Call<Token> resetPassword(
            @Field("email") String email
    );

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("register")
    Call<Token> register(
            @Field("email") String email,
            @Field("password") String password,
            @Field("type") int type
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
            @Query("neck") float neck,
            @Query("waist") float waist,
            @Query("thigh") float thigh
    );

    @Headers("Accept: application/json")
    @POST("fooditems")
    Call<Boolean> FoodItemStore(
            @Query("food_name") String food_name,
            @Query("carbon") double carbon,
            @Query("protein") double protein,
            @Query("fat") double fat,
            @Query("portion_in_grams") double portion_in_grams,
            @Query("kcal") double kcal,
            @Query("serving_size") double serving_size,
            @Query("serving_prefix") String serving_prefix,
            @Query("user_id") int user_id,
            @Query("barcode") String barcode,
            @Query("food_categories_id") String food_categories_id
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
            @Query("user_id") @Nullable int user_id,
            @Query("q") @Nullable String q
    );

    @GET("fooditems/{id}")
    Call<Wrappers.Single<FoodItem>> FoodItemShow(
            @Path("id") int id
    );

    @GET("recipes")
    Call<Wrappers.Paginated<Recipe>> RecipesIndex(
            @Query("page") int page,
            @Query("q") @Nullable String q,
            @Query("category") @Nullable int category,
            @Query("user_id") int user_id
    );

    @Headers("Accept: application/json")
    @POST("recipes")
    Call<Boolean> RecipeStore(
            @Query("user_id") int id,
            @Query("title") String title,
            @Query("categories_id") int categories_id,
            @Query("food_id") String food_id,
            @Query("food_amount") String food_amount
    );

    @Headers("Accept: application/json")
    @GET("recipes/{id}")
    Call<Wrappers.Single<Recipe>> RecipeShow(
            @Path("id") int id
    );

    @GET("recipes/list")
    Call<Wrappers.Collection<Recipe>> RecipesByCategory(
            @Query("index") @Nullable int index,
            @Query("q") @Nullable String q
    );

    @GET("categories")
    Call<Wrappers.Collection<Category>> CategoriesIndex();

    @GET("food_categories")
    Call<Wrappers.Collection<Category>> FoodCategoriesIndex();

    @GET("sports")
    Call<Wrappers.Collection<Sport>> SportsIndex();

    @Headers("Accept: application/json")
    @POST("meals")
    Call<Boolean> MealStore(
            @Query("customer_id") int id,
            @Query("food_id") int food_id,
            @Query("recipe_id") int recipe_id,
            @Query("gram") float gram,
            @Query("timing_id") int timing_id, //[1=>'breakfast', 2=>'lunch', 3=>'dinner', 4=>'breakfast_snack', 5=>'lunch_snack', 6=>'dinner_snack']
            @Query("date") String date
    );

    @Headers("Accept: application/json")
    @GET("check_available")
    Call<Boolean> checkAvailable();

    @Headers("Accept: application/json")
    @POST("purchase_membership")
    Call<Boolean> purchaseMembership(
            @Query("type") int type
    );

    @GET("meals/list")
    Call<Wrappers.Collection<Meal>> MealsByCategory(
            @Query("date") @Nullable String date,
            @Query("timing_id") @Nullable int timing_id
    );

    @DELETE("meals/{id}")
    Call<Boolean> MealsDelete(@Path("id") int id);

    @DELETE("fooditems/{id}")
    Call<Boolean> FoodItemDelete(@Path("id") int id);

    @DELETE("recipes/{id}")
    Call<Boolean> RecipeDelete(@Path("id") int id);

    @Headers("Accept: application/json")
    @POST("informations/save_dietmode")
    Call<Boolean> SaveDietMode(
            @Query("diet_mode") int diet_mode
    );

    @Headers("Accept: application/json")
    @POST("informations/save_weight")
    Call<Boolean> SaveWeight(
            @Query("weight") float weight
    );

    @Headers("Accept: application/json")
    @POST("informations/save_water")
    Call<Boolean> SaveWater(
            @Query("water") int water
    );

    @GET("informations/get_graph_values")
    Call<Wrappers.Collection<GraphValue>> GetGraphValues(
            @Query("date") @Nullable String date
    );
}
