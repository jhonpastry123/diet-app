package com.diet.trinity.model;

public class RecipeList {

    String name = "";
    int id=0;

    public RecipeList(String name, Integer id){
        this.name = name;
        this.id=id;
    }

    public String getRecipeName() {
        return name;
    }

    public int getId(){return id;}
}
