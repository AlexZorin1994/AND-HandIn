package com.example.recipeapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.viewmodels.RecipeViewModel;

import org.w3c.dom.Text;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class RecipeActivity extends BaseActivity {

    // UI components
    private AppCompatImageView recipeImage;
    private TextView recipteTitle, recipeRank;
    private LinearLayout recipeIngredientContainer;
    private ScrollView scrollView;
    private static final String TAG = "RecipeActivity";

    private RecipeViewModel recipeViewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        recipeImage = findViewById(R.id.recipe_image);
        recipteTitle = findViewById(R.id.recipe_title);
        recipeRank = findViewById(R.id.recipe_social_score);
        recipeIngredientContainer = findViewById(R.id.ingredients_container);
        scrollView = findViewById(R.id.parent);

        recipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
        showProgressBar(true);
        subscribeObservers();
        getIncomingIntent();


    }


    private void getIncomingIntent() {
        if(getIntent().hasExtra("recipe")){
            Recipe recipe = getIntent().getParcelableExtra("recipe");
            Log.d(TAG, "getIncomingIntent: " + recipe.getTitle());
            recipeViewModel.searchRecipeById(recipe.getRecipe_id());
        }
    }

    private void subscribeObservers(){
        recipeViewModel.getRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(Recipe recipe) {
                if(recipe != null){
                    if(recipe.getRecipe_id().equals(recipeViewModel.getRecipeId())){
                    setRecipeProperties(recipe);

                    }
                }
            }
        });
    }

    private void setRecipeProperties(Recipe recipe){
        if(recipe != null){
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background);
            Glide.with(this).setDefaultRequestOptions(requestOptions)
                    .load(recipe.getImage_url())
                    .into(recipeImage);

            recipteTitle.setText(recipe.getTitle());
            recipeRank.setText(String.valueOf(Math.round(recipe.getSocial_rank())));
            recipeIngredientContainer.removeAllViews();
            for(String ingredient: recipe.getIngredients()) {
                TextView textView = new TextView(this);
                textView.setText(ingredient);
                textView.setTextSize(15);
                textView.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                recipeIngredientContainer.addView(textView);
            }
        }
        showParent();
        showProgressBar(false);
    }

    private void showParent(){
        scrollView.setVisibility(View.VISIBLE);
    }
}
