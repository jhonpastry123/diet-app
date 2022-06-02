package com.diet.trinity.Utility;

import com.diet.trinity.data.models.FoodItem;
import com.diet.trinity.data.models.FoodValue;

public class Global {
    static public float points = 0;
    static public float units = 0;

    //---------morning------//
    static public float morning_carbon = 0;
    static public float morning_fat = 0;
    static public float morning_protein = 0;
    static public float morning_points = 0;
    static public float morning_units = 0;
    static public float morning_total = 0;

    //---------lunch------//
    static public float lunch_carbon = 0;
    static public float lunch_fat = 0;
    static public float lunch_protein = 0;
    static public float lunch_points = 0;
    static public float lunch_units = 0;
    static public float lunch_total = 0;

    //---------dinner------//
    static public float dinner_carbon = 0;
    static public float dinner_fat = 0;
    static public float dinner_protein = 0;
    static public float dinner_points = 0;
    static public float dinner_units = 0;
    static public float dinner_total = 0;

    //---------snack_morning------//
    static public float snack_morning_carbon = 0;
    static public float snack_morning_fat = 0;
    static public float snack_morning_protein = 0;
    static public float snack_morning_points = 0;
    static public float snack_morning_units = 0;
    static public float snack_morning_total = 0;

    //---------snack_lunch------//
    static public float snack_lunch_carbon = 0;
    static public float snack_lunch_fat = 0;
    static public float snack_lunch_protein = 0;
    static public float snack_lunch_points = 0;
    static public float snack_lunch_units = 0;
    static public float snack_lunch_total = 0;

    //---------snack_dinner------//
    static public float snack_dinner_carbon = 0;
    static public float snack_dinner_fat = 0;
    static public float snack_dinner_protein = 0;
    static public float snack_dinner_points = 0;
    static public float snack_dinner_units = 0;
    static public float snack_dinner_total = 0;

    static public int timing_id=0;
    static public int user_id=0;
    static public String token = "";

    //--------BMR---------//
    static public Integer bmr=0;
    static public float daily_bmr = 0;

    //------meal-----------//
    static public Integer meal_num = 0;
    static public Integer pasta_num = 0;
    static public Integer legumes_num = 0;
    static public Integer oily_num = 0;
    static public Integer junk_img_num = 0;
    static public Integer fruit_num = 0;
    static public Integer meat_num = 0;
    static public Integer oily_img_num = 0;

    static public Boolean bCarbon = false;
    static public Boolean bProtein = false;
    static public Boolean bFat = false;

    static public Boolean lCarbon = false;
    static public Boolean lProtein = false;
    static public Boolean lFat = false;

    static public Boolean dCarbon = false;
    static public Boolean dProtein = false;
    static public Boolean dFat = false;

    static public String food_name = "";
    static public Integer[] food_categories_id = {};
    static public String food_categories = "";
    static public double carbon = 0;
    static public double protein = 0;
    static public double fat = 0;
    static public double portion_in_grams = 0;
    static public double kcal = 0;
    static public double serving_size = 0;
    static public String serving_prefix = "";
    static public String barcode = "";
    static public FoodValue[] food_values = {};

    static public float gram = 0;
}
