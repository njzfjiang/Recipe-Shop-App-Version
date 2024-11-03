package comp4350.recipe_shop_app_version.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import comp4350.recipe_shop_app_version.Other.HTTPRequestTask;
import comp4350.recipe_shop_app_version.Other.ImageRequestTask;
import comp4350.recipe_shop_app_version.Other.RecipeListArrayAdapter;
import comp4350.recipe_shop_app_version.Other.Services;
import comp4350.recipe_shop_app_version.R;


public class FavoritesActivity extends AppCompatActivity {

    private BottomNavigationView navBar;
    private TextView username, keywordsInput, message;
    private String keywords = "";
    private ListView favoriteList;
    private Activity activity;
    private ArrayList<JSONObject> recipes;
    private ArrayList<Bitmap> images;
    private ArrayList<ArrayList> listList;
    private ArrayAdapter<JSONObject> listArrayAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorites);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.favorites_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        activity = this;
        navBar = findViewById(R.id.bottomNavigationView);
        navBar.setSelectedItemId(R.id.favorites);
        username = findViewById(R.id.username);
        keywordsInput = findViewById(R.id.keyword_input);
        message = findViewById(R.id.messageText);
        favoriteList = findViewById(R.id.listView);

        username.setText(Services.username);

        getFavorites();

        setListeners();
    }//onCreate

    @Override
    protected void onResume() {
        super.onResume();
        getFavorites();
    }//onResume

    public void setListeners(){
        navBar.setOnNavigationItemSelectedListener(item -> {
            boolean success = false;
            if(item.getItemId() == R.id.search){
                goToSearch();
                success = true;
            }
            else if(item.getItemId() == R.id.about_us){
                goToAbout();
                success = true;
            }
            else if(item.getItemId() == R.id.grocery){
                goToGrocery();
                success = true;
            }
            else if(item.getItemId() == R.id.settings){
                goToSettings();
                success = true;
            }
            return success;
        });

        keywordsInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                keywords = keywordsInput.getText().toString();
                for(int i=0;i<recipes.size();i++){
                    try {
                        JSONObject json = ((RecipeListArrayAdapter)favoriteList.getAdapter()).getRecipe(i);
                        System.out.println(json.getJSONObject("recipe").get("label").toString());
                        if(json.getJSONObject("recipe").get("label").toString().toLowerCase().indexOf(keywords.toLowerCase()) != -1){
                            ((RecipeListArrayAdapter) favoriteList.getAdapter()).setVisibility(i, true);
                            updateListView();
                        }
                        else{
                            ((RecipeListArrayAdapter) favoriteList.getAdapter()).setVisibility(i, false);
                            updateListView();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }//setListeners

    private void search(){

    }//search

    private void getFavorites(){
        recipes = new ArrayList<>();
        images = new ArrayList<>();
        listList = new ArrayList<>();
        favoriteList.setVisibility(View.GONE);
        getFavoritesAttempt();
        String[] params = {"all-favorites"};
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new HTTPRequestTask(params,activity));
    }//getFavorites

    private void getFavoritesAttempt(){
        message.setVisibility(View.VISIBLE);
        message.setText("Fetching ...");
        message.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.black));
    }//getFavoritesAttempt

    public void getFavoritesSuccess(String response){
        try{
            JSONArray recipeArray = new JSONObject(response).getJSONArray("recipes");
            for(int i=0;i<recipeArray.length();i++){
                recipes.add(null);
                images.add(null);
                getRecipe(i, recipeArray.getJSONObject(i).get("recipeID").toString());
            }
            listList.add(recipes);
            listList.add(images);
            listArrayAdapter = new RecipeListArrayAdapter(this,R.layout.recipe_list_layout, listList, activity);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    favoriteList.setAdapter(listArrayAdapter);
                    updateListView();
                    message.setVisibility(View.GONE);
                    favoriteList.setVisibility(View.VISIBLE);
                }//run
            });

            favoriteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    int itemPosition = adapterView.getPositionForView(view);
                    System.out.println(itemPosition);
                    Services.recipe = recipes.get(itemPosition);
                    Services.recipeImage = images.get(itemPosition);
                    goToRecipe();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//gotFavorites

    private void getRecipe(int pos, String id){
        try {
            System.out.println("getRecipe");
            System.out.println(id);
            String[] params = {"recipe", String.valueOf(pos), id};
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(new HTTPRequestTask(params, activity));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//getRecipe

    public void getRecipeSuccess(int pos, String response){
        try {
            JSONObject recipe = new JSONObject(response);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recipes.set(pos, recipe);
                    ((RecipeListArrayAdapter)favoriteList.getAdapter()).setRecipe(pos, recipe);
                    updateListView();
                }
            });
            try {
                String url = recipe.getJSONObject("recipe").get("image").toString();
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(new ImageRequestTask(pos, url, activity));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//getRecipe

    public void loadImage(int pos, Bitmap image){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                images.set(pos, image);
                ((RecipeListArrayAdapter)favoriteList.getAdapter()).setImage(pos, image);
                updateListView();
            }
        });
    }//loadImage

    private void updateListView(){
        ArrayAdapter arrayAdapter = (ArrayAdapter) favoriteList.getAdapter();
        int height = 0;
        for(int i=0;i<arrayAdapter.getCount();i++){
            View listItem = arrayAdapter.getView(i,null, favoriteList);
            if(listItem.getVisibility() == View.VISIBLE) {
                listItem.measure(0, 0);
                height += listItem.getMeasuredHeight() + favoriteList.getDividerHeight();
            }
        }
        ViewGroup.LayoutParams layoutParams = favoriteList.getLayoutParams();
        layoutParams.height = height;
        favoriteList.setLayoutParams(layoutParams);
    }//updateListView

    public void getRecipeFail(int pos){

    }//getRecipe

    public void getFavoritesFail(){
        message.setVisibility(View.VISIBLE);
        message.setText("Error! Could not get favorites!");
        message.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.red));
    }//getFavoritesFail


    private void goToRecipe(){
        Intent finishIntent = new Intent(getApplicationContext(), RecipeInfoActivity.class);
        finishIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(finishIntent);
    }//goToSearch

    private void goToSearch(){
        Intent finishIntent = new Intent(getApplicationContext(), SearchActivity.class);
        finishIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(finishIntent);
    }//goToSearch

    private void goToFavorites(){
        Intent finishIntent = new Intent(getApplicationContext(), FavoritesActivity.class);
        finishIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(finishIntent);
    }//goToFavorites

    private void goToGrocery(){
        Intent finishIntent = new Intent(getApplicationContext(), GroceryActivity.class);
        finishIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(finishIntent);
    }//goToGrocery

    private void goToSettings(){
        Intent finishIntent = new Intent(getApplicationContext(), SettingsActivity.class);
        finishIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(finishIntent);
    }//goToSettings

    private void goToAbout(){
        Intent finishIntent = new Intent(getApplicationContext(), AboutActivity.class);
        finishIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(finishIntent);
    }//goToAbout
}