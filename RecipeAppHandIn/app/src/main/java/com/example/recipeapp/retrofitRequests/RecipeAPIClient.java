package com.example.recipeapp.retrofitRequests;

import android.util.Log;

import com.example.recipeapp.AppExecutors;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.retrofitRequests.responses.RecipeResponse;
import com.example.recipeapp.retrofitRequests.responses.RecipeSearchResponse;
import com.example.recipeapp.util.Constants;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Response;

import static com.example.recipeapp.util.Constants.NETWORK_TIMEOUT;

public class RecipeAPIClient {

    private static final String TAG = "RecipeAPIClient";
    private static RecipeAPIClient instance;
    private MutableLiveData<List<Recipe>> recipies;
    private RetrieveRecipesRunnable retrieveRecipesRunnable;
    private MutableLiveData<Recipe> mRecipe;
    private RetrieveRecipeRunnable retrieveRecipeRunnable;
    public static RecipeAPIClient getInstance() {
        if (instance == null) {
            instance = new RecipeAPIClient();
        }
        return instance;
    }

    private RecipeAPIClient() {
        recipies = new MutableLiveData<>();
        mRecipe = new MutableLiveData<>();
    }

    public LiveData<List<Recipe>> getRecipes() {
        return recipies;
    }

    public LiveData<Recipe> getRecipe() {
        return mRecipe;
    }

    public void searchRecipesAPI(String query, int pageNumber) {
        if(retrieveRecipesRunnable != null) {
            retrieveRecipesRunnable = null;
        }
        retrieveRecipesRunnable = new RetrieveRecipesRunnable(query, pageNumber);
        final Future handler = AppExecutors.getInstance().networkIO().submit(retrieveRecipesRunnable);

        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // notify the user that the network has timed out and the handler cancels the request after 5 seconds
                handler.cancel(true);
            }
        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public void searchRecipeById(String recipeId){
        if(retrieveRecipeRunnable != null) {
            retrieveRecipeRunnable = null;
        }
        retrieveRecipeRunnable = new RetrieveRecipeRunnable(recipeId);

        final Future handler = AppExecutors.getInstance().networkIO().submit(retrieveRecipeRunnable);

        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // let the user know its timed out
            handler.cancel(true);
            }
        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    private class RetrieveRecipesRunnable implements Runnable {

        // responsible for doing the search query
        private String query;
        private int pageNumber;
        boolean cancelRequest; // stopping the runnable if there is something wrong

        public RetrieveRecipesRunnable(String query, int pageNumber) {
            this.query = query;
            this.pageNumber = pageNumber;
            cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getRecipes(query, pageNumber).execute();
                if (cancelRequest) {
                    return;
                }
                if(response.code() == 200) {
                    List<Recipe> list = new ArrayList<>(((RecipeSearchResponse)response.body()).getRecipes());
                    if (pageNumber == 1) {
                        recipies.postValue(list);
                    } else {
                        List<Recipe> currentrecipes = recipies.getValue();
                        currentrecipes.addAll(list);
                    }
                } else {
                    String error = response.errorBody().string();
                    Log.e(TAG, "run: " + error);
                    recipies.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                recipies.postValue(null);
            }

        }

        private Call<RecipeSearchResponse> getRecipes(String query, int pageNumber) {
            return WebServiceGenerator.getRecipeAPI().searchRecipes(
                    Constants.API_KEY, query, String.valueOf(pageNumber)
            );
        }
        
        private void cancelRequest() {
            Log.d(TAG, "cancelRequest: cancelling the searching request.");
        }

    }

    private class RetrieveRecipeRunnable implements Runnable {

        // responsible for doing the search query
        private String recipeId;
        private String query;
        private int pageNumber;
        boolean cancelRequest; // stopping the runnable if there is something wrong

        public RetrieveRecipeRunnable(String recipeId) {
            this.recipeId = recipeId;
            cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getRecipe(recipeId).execute();
                if (cancelRequest) {
                    return;
                }
                if(response.code() == 200) {
                    Recipe recipe = ((RecipeResponse)response.body()).getRecipe();
                    mRecipe.postValue(recipe);
                } else {
                    String error = response.errorBody().string();
                    Log.e(TAG, "run: " + error);
                    mRecipe.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mRecipe.postValue(null);
            }

        }

        private Call<RecipeResponse> getRecipe(String recipeId) {
            return WebServiceGenerator.getRecipeAPI().getRecipe(
                    Constants.API_KEY,
                    recipeId
            );
        }

        private void cancelRequest() {
            Log.d(TAG, "cancelRequest: cancelling the searching request.");
        }

    }

    public void cancelRequest() {
        if(retrieveRecipesRunnable != null) {
            retrieveRecipesRunnable.cancelRequest();
        }
        if(retrieveRecipeRunnable != null) {
            retrieveRecipeRunnable.cancelRequest();
        }
    }
}

