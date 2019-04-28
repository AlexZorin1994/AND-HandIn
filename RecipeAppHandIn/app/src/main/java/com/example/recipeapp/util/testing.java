package com.example.recipeapp.util;

import android.util.Log;

import com.example.recipeapp.models.Recipe;

import java.util.List;

public class testing {

    public static void printRecipes(List<Recipe> list, String TAG) {

        for (Recipe recipe: list) {
            Log.d(TAG, "onChanged: " + recipe.getTitle());
        }

    }
}
