package com.diet.trinity.Utility;

public class Common {
//    private String baseUrl = "http://zafeiraki.com";
    private String baseUrl = "http://192.168.107.81:8000";
    private String registerUrl = baseUrl + "/api/register";
    private String loginUrl = baseUrl + "/api/login";
    private String foodUrl = baseUrl + "/api/fooditems";
    private String settingUrl = baseUrl + "/api/settings";
    private String recipeUrl = baseUrl + "/api/recipes";
    private String sportUrl = baseUrl + "/api/sports";
    private String imageUrl = baseUrl + "/images/";

    private static Common instance = new Common();

    public static Common getInstance()
    {
        return instance;
    }

    public String getImageUrl(){return imageUrl;}
}