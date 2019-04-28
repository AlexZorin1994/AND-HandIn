package com.example.recipeapp.retrofitRequests;

import com.example.recipeapp.retrofitRequests.responses.RecipeResponse;
import com.example.recipeapp.retrofitRequests.responses.RecipeSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeAPI {

    // Search request to get a list of recipes
    @GET("api/search")
    Call<RecipeSearchResponse> searchRecipes(
            @Query("key") String key,       // query automatically appends the ? symbol -> https://www.food2fork.com/api/search?key=YOUR_API_KEY&q=chicken%20breast&page=2
            @Query("q") String query,       // query automatically appends the & symbol and so forth
            @Query("page") String page
            );

    // get single recipe request
    @GET("api/get")
    Call<RecipeResponse> getRecipe(
            @Query("key") String key,
            @Query("rId") String recipe_id
    );
}
