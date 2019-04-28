package com.example.recipeapp.viewmodels;

import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.repositories.RecipeRepository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

public class RecipeViewModel extends ViewModel {

    private RecipeRepository recipeRepository;
    private String recipeId;

    public RecipeViewModel() {
        recipeRepository = RecipeRepository.getInstance();
    }

    public LiveData<Recipe> getRecipe() {
        return recipeRepository.getRecipe();
    }

    public void searchRecipeById(String recipeId) {
        this.recipeId = recipeId;
        recipeRepository.searchRecipeById(recipeId);
    }

    public String getRecipeId() {
        return recipeId;
    }
}
