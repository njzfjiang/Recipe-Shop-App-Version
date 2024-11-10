package comp4350.recipe_shop_app_version.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import comp4350.recipe_shop_app_version.Other.Services;
import comp4350.recipe_shop_app_version.R;


public class GroceryActivity extends AppCompatActivity {

    private BottomNavigationView navBar;
    private TextView username, message, ingredients;
    private String ingredientList = "";
    private ArrayList<JSONObject> recipes;
    private Activity activity;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_grocery);

        activity = this;
        navBar = findViewById(R.id.bottomNavigationView);
        navBar.setSelectedItemId(R.id.grocery);
        username = findViewById(R.id.username);
        message = findViewById(R.id.messageText);
        ingredients = findViewById(R.id.ingredients);

        username.setText(Services.username);

        setListeners();
    }//onCreate

    @Override
    protected void onResume() {
        super.onResume();
        navBar.getMenu().findItem(R.id.grocery).setChecked(true);
        getList();
    }//onResume

    public void setListeners(){
        navBar.setOnNavigationItemSelectedListener(item -> {
            boolean success = false;
            if(item.getItemId() == R.id.search){
                goToSearch();
                success = true;
            }
            else if(item.getItemId() == R.id.favorites){
                goToFavorites();
                success = true;
            }
            else if(item.getItemId() == R.id.about_us){
                goToAbout();
                success = true;
            }
            else if(item.getItemId() == R.id.settings){
                goToSettings();
                success = true;
            }
            return success;
        });
    }//setListeners

    private void getList(){
        ingredients.setVisibility(View.GONE);
        recipes = new ArrayList<>();
        getListAttempt();
        String[] params = {"generate-list"};
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new HTTPRequestTask(params,activity));
    }//getList

    private void getListAttempt(){
        message.setVisibility(View.VISIBLE);
        message.setText("Fetching ...");
        message.setTextColor(username.getCurrentTextColor());
    }//getListAttempt

    public void getListSuccess(String response){
        try {
            JSONArray recipeArray = new JSONObject(response).getJSONArray("recipes");
            for(int i=0;i<recipeArray.length();i++){
                recipes.add(null);
                String[] params = {"recipe", String.valueOf(i), recipeArray.get(i).toString()};
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(new HTTPRequestTask(params,activity));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//getListSuccess

    public void getRecipeSuccess(int pos, String response){
        try {
            JSONObject recipe = new JSONObject(response);
            recipes.set(pos, recipe);
            updateIngredientList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//getRecipeSuccess

    public void getRecipeFail(int pos){

    }//getRecipeFail

    private void updateIngredientList(){
        ingredientList = "";
        for(int i=0;i<recipes.size();i++){
            if(recipes.get(i) != null) {
                try {
                    JSONArray ingredientLines = recipes.get(i).getJSONObject("recipe").getJSONArray("ingredientLines");
                    ingredientList += recipes.get(i).getJSONObject("recipe").get("label").toString() + "\n";
                    for (int j = 0; j < ingredientLines.length(); j++) {
                        try {
                            ingredientList += "\t\t\u2022" + ingredientLines.get(j) + "\n";
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }//each ingredient
                    ingredientList += "\n\n";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }//recipe not null
        }//each recipe
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ingredients.setText(ingredientList);
                message.setVisibility(View.GONE);
                ingredients.setVisibility(View.VISIBLE);
            }
        });
    }//updateIngredientList

    public void getListFail(){
        message.setVisibility(View.VISIBLE);
        message.setText("No grocery list found!");
        message.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.red));
    }//getListFail

    private void goToSearch(){
        Intent finishIntent = new Intent(getApplicationContext(), SearchActivity.class);
        finishIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finishIntent.putExtra("CallingActivity", activity.getLocalClassName());
        startActivity(finishIntent);
    }//goToSearch

    private void goToFavorites(){
        Intent finishIntent = new Intent(getApplicationContext(), FavoritesActivity.class);
        finishIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finishIntent.putExtra("CallingActivity", activity.getLocalClassName());
        startActivity(finishIntent);
    }//goToFavorites

    private void goToGrocery(){
        Intent finishIntent = new Intent(getApplicationContext(), GroceryActivity.class);
        finishIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finishIntent.putExtra("CallingActivity", activity.getLocalClassName());
        startActivity(finishIntent);
    }//goToGrocery

    private void goToSettings(){
        Intent finishIntent = new Intent(getApplicationContext(), SettingsActivity.class);
        finishIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finishIntent.putExtra("CallingActivity", activity.getLocalClassName());
        startActivity(finishIntent);
    }//goToSettings

    private void goToAbout(){
        Intent finishIntent = new Intent(getApplicationContext(), AboutActivity.class);
        finishIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finishIntent.putExtra("CallingActivity", activity.getLocalClassName());
        startActivity(finishIntent);
    }//goToAbout
}