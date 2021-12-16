package com.diet.trinity.model;

import org.json.JSONException;

import java.util.Locale;

public class Listmodel {
    Integer item_id;
    String item_name;
    Float item_size;
    Float item_count;
    String item_prefix;
    Float item_points;
    Float item_units;

    public Listmodel(Integer id, String item_name, Float item_size, Float item_count, String item_prefix, Float item_units, Float item_points ) {
        this.item_id = id;
        this.item_name = item_name;
        this.item_size = item_size;
        this.item_count = item_count;
        this.item_prefix = item_prefix;
        this.item_points = item_points;
        this.item_units = item_units;
    }

    public String getListTitle() {
        return item_name;
    }

    public String getListGram() {
        return item_count + " " + item_prefix + " (" + item_size + "γρ.)";
    }
    public String getListPoint() {
        return String.format(Locale.US, "%.1f", item_points) + " points / " + String.format(Locale.US, "%.1f", item_units) + " units";
    }
    public Integer getListId() {
        return item_id;
    }
}
