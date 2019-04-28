package com.example.recipeapp.viewmodels;

import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.repositories.RecipeRepository;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class RecipeListViewModel extends ViewModel {

    private RecipeRepository recipeRepository;
    private boolean isViewingRecipe;
    private boolean isPerformingQuery = false;

    public RecipeListViewModel() {
        recipeRepository = RecipeRepository.getInstance();
        isPerformingQuery = false;
    }

    public LiveData<List<Recipe>> getRecipes(){
        return recipeRepository.getRecipes();
    }

    public void searchRecipesAPI(String query, int pageNumber) {
        isViewingRecipe = true;
        isPerformingQuery = true;
       recipeRepository.searchRecipesAPI(query, pageNumber);
    }


    public boolean isViewingRecipe() {
        return isViewingRecipe;
    }

    public void setViewingRecipe(boolean isViewingRecipe) {
        this.isViewingRecipe = isViewingRecipe;
    }

    public boolean isPerformingQuery() {
        return isPerformingQuery;
    }

    public void setPerformingQuery(boolean performingQuery) {
        isPerformingQuery = performingQuery;
    }

    public boolean onBackPressed() {
        if(isPerformingQuery){
            // cancel query
            recipeRepository.cancelRequest();
            isPerformingQuery = false;
        }
        if(isViewingRecipe){
            isViewingRecipe = false;
            return false;
        }
        return true;
    }

    public void searchNextPage(){
        if(!isPerformingQuery && isViewingRecipe){
            recipeRepository.searchNextPage();
        }
    }

}
