package com.diet.trinity.model;

import org.json.JSONException;

import java.util.Locale;

public class Listmodel {
    Integer item_id;
    String item_name;
    String item_carbon;
    String item_protein;
    String item_fat;
    String item_gram;
    String item_point;
    String item_unit;
    boolean recipe;
    public Listmodel(Integer id, String foodname, String carbon_m, String protein_m, String fat_m, String gram_m, String point_m, String unit_m) throws JSONException {
        this.item_id = id;
        this.item_name = foodname;
        this.item_carbon = carbon_m;
        this.item_protein = protein_m;
        this.item_fat = fat_m;
        if (Float.parseFloat(gram_m) == 0f) {
            this.item_gram = "";
        }
        else {
            this.item_gram = "Ποσότητα (" + gram_m + "γρ.)";
        }
        this.item_point = point_m;
        this.item_unit = unit_m;
        this.recipe = false;
    }

    public String getListTitle() throws JSONException {
        return item_name;
    }
    public String getListGram() throws JSONException {
        return item_gram;
    }
    public String getListPoint() throws JSONException {
        return String.format(Locale.US, "%.1f", Float.parseFloat(item_point)) + " points / " + String.format(Locale.US, "%.1f", Float.parseFloat(item_unit)) + " units";
    }
    public Integer getListId() throws JSONException{
        return item_id;
    }

    public void setRecipe(boolean b) throws JSONException{
        recipe = b;
    }

    public boolean getRecipe() throws JSONException{
        return recipe;
    }
}
