package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import androidx.appcompat.widget.SearchView;

import com.example.recipeapp.account.LoginActivity;
import com.example.recipeapp.adapters.OnRecipeListener;
import com.example.recipeapp.adapters.RecipeRecyclerAdapter;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.util.VerticalSpacingDecorator;
import com.example.recipeapp.util.testing;
import com.example.recipeapp.viewmodels.RecipeListViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecipeListActivity extends BaseActivity implements OnRecipeListener {

    private static final String TAG = "RecipeListActivity";
    private RecipeListViewModel recipeListViewModel;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private RecipeRecyclerAdapter adapter;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        mSearchView = findViewById(R.id.search_view);
        recyclerView = findViewById(R.id.recipe_list);
        firebaseAuth = FirebaseAuth.getInstance();

        recipeListViewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);
        initRecyclerView();
        subscribeObservers();
        initSearchView();
        if(!recipeListViewModel.isViewingRecipe()) {
            // display search categories
            displaySearchCategories();
        }
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));

    }

    private void subscribeObservers() {
        recipeListViewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {
                if (recipes != null) {
                    if (recipeListViewModel.isViewingRecipe()) {
                        testing.printRecipes(recipes, "recipes test");
                        recipeListViewModel.setPerformingQuery(false);
                        adapter.setmRecipes(recipes);
                    }
                }

            }
        });
    }

    private void initRecyclerView() {
        adapter = new RecipeRecyclerAdapter(this);
        VerticalSpacingDecorator itemDecoration = new VerticalSpacingDecorator(30);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if(!recyclerView.canScrollVertically(1)){
                    // search next page
                recipeListViewModel.searchNextPage();
                }
            }
        });

    }





    private void initSearchView() {
       mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                recipeListViewModel.searchRecipesAPI(query, 1);
                mSearchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


    FirebaseAuth.AuthStateListener fAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                startActivity(new Intent(RecipeListActivity.this, LoginActivity.class));

            }


        }
    };

    public void signOut() {
        firebaseAuth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(fAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (fAuthListener != null) {
            firebaseAuth.removeAuthStateListener(fAuthListener);
        }
    }


    @Override
    public void onRecipeClick(int position) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra("recipe", adapter.getSelectedRecipe(position));
        startActivity(intent);
    }

    @Override
    public void onCategoryClick(String category) {
        recipeListViewModel.searchRecipesAPI(category, 1);
        mSearchView.clearFocus();
    }

    private void displaySearchCategories() {
        recipeListViewModel.setViewingRecipe(false);
        adapter.displaySearchCategories();
    }

    @Override
    public void onBackPressed() {
        if(recipeListViewModel.onBackPressed()){
            super.onBackPressed();
        } else {
            displaySearchCategories();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_categories) {
            displaySearchCategories();
        } else {
            signOut();
            startActivity(new Intent(RecipeListActivity.this, LoginActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
