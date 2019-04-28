package com.example.recipeapp.repositories;

import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.retrofitRequests.RecipeAPIClient;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class RecipeRepository {

    private static RecipeRepository instance;
    private RecipeAPIClient recipeAPIClient;
    private String mQuerry;
    private int mPageNumber;


    public static RecipeRepository getInstance() {
        if (instance == null) {
            instance = new RecipeRepository();
        }
        return instance;
    }

    private RecipeRepository() {
        recipeAPIClient = RecipeAPIClient.getInstance();
    }

    public LiveData<List<Recipe>> getRecipes() {
        return recipeAPIClient.getRecipes();
    }

    public LiveData<Recipe> getRecipe() {
        return recipeAPIClient.getRecipe();
    }

    public void searchRecipeById(String recipeId){
        recipeAPIClient.searchRecipeById(recipeId);
    }

    public void searchRecipesAPI(String query, int pageNumber) {
        if(pageNumber == 0) {
            pageNumber = 1;
        }
        mQuerry = query;
        mPageNumber = pageNumber;
        recipeAPIClient.searchRecipesAPI(query, pageNumber);
    }

    public void searchNextPage(){
        searchRecipesAPI(mQuerry, mPageNumber + 1);
    }

    public void cancelRequest() {
        recipeAPIClient.cancelRequest();
    }



}
