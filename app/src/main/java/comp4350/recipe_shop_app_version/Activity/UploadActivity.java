package comp4350.recipe_shop_app_version.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import comp4350.recipe_shop_app_version.Other.HTTPRequestTask;
import comp4350.recipe_shop_app_version.Other.Services;
import comp4350.recipe_shop_app_version.R;


public class UploadActivity extends AppCompatActivity {

    private BottomNavigationView navBar;
    private ImageView recipeImage;
    private TextView recipeName, recipeSource, ingredients, instructionLink;
    private Button addFavorite, removeFavorite;
    private Activity activity;
    private JSONObject recipe;
    private Bitmap image;
    private String previousActivity;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_info);

        activity = this;
        recipe = Services.recipe;
        image = Services.recipeImage;

        navBar = findViewById(R.id.bottomNavigationView);
        previousActivity = (String) this.getIntent().getExtras().get("CallingActivity");
        System.out.println(previousActivity);
        if(previousActivity.equalsIgnoreCase("Activity.SearchActivity")){
            navBar.setSelectedItemId(R.id.search);
        }
        else if(previousActivity.equalsIgnoreCase("Activity.FavoritesActivity")){
            navBar.setSelectedItemId(R.id.favorites);
        }
        else if(previousActivity.equalsIgnoreCase("Activity.GroceryActivity")){
            navBar.setSelectedItemId(R.id.grocery);
        }
        else if(previousActivity.equalsIgnoreCase("Activity.SettingsActivity")){
            navBar.setSelectedItemId(R.id.settings);
        }
        else if(previousActivity.equalsIgnoreCase("Activity.AboutActivity")){
            navBar.setSelectedItemId(R.id.about_us);
        }

        recipeImage = findViewById(R.id.recipeImage);
        recipeName = findViewById(R.id.recipeInfoTitle);
        recipeSource = findViewById(R.id.recipeSource);
        ingredients = findViewById(R.id.ingredients);
        instructionLink = findViewById(R.id.instructionLink);
        addFavorite = findViewById(R.id.favoriteButton);
        removeFavorite = findViewById(R.id.unfavoriteButton);

        String name = "";
        String source = "";
        ArrayList<String> ingredientList = new ArrayList<>();
        String link = "";
        try {
            name = recipe.getJSONObject("recipe").get("label").toString();
            source = recipe.getJSONObject("recipe").get("source").toString();
            JSONArray ingredientLines = (JSONArray) recipe.getJSONObject("recipe").get("ingredientLines");
            for(int i=0;i<ingredientLines.length();i++){
                ingredientList.add("\u2022" + ingredientLines.get(i).toString() + "\n");
            }
            link = recipe.getJSONObject("recipe").get("url").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        recipeImage.setImageBitmap(image);
        recipeName.setText(name);
        recipeSource.setText("Recipe from " + source);
        String ingredientText = "";
        for(int i=0;i<ingredientList.size();i++){
            ingredientText += ingredientList.get(i);
            ingredientText += "\n";
        }
        ingredients.setText(ingredientText);
        String linkText = (String) instructionLink.getText();
        instructionLink.setText(Html.fromHtml("<a href=\"" + link + "\">" + linkText + "</a>"));
        instructionLink.setMovementMethod(LinkMovementMethod.getInstance());

        checkFavorite();

        setListeners();
    }//onCreate

    @Override
    protected void onResume() {
        super.onResume();
        if(previousActivity.equalsIgnoreCase("Activity.SearchActivity")){
            navBar.getMenu().findItem(R.id.search).setChecked(true);
        }
        else if(previousActivity.equalsIgnoreCase("Activity.FavoritesActivity")){
            navBar.getMenu().findItem(R.id.favorites).setChecked(true);
        }
        else if(previousActivity.equalsIgnoreCase("Activity.GroceryActivity")){
            navBar.getMenu().findItem(R.id.grocery).setChecked(true);
        }
        else if(previousActivity.equalsIgnoreCase("Activity.SettingsActivity")){
            navBar.getMenu().findItem(R.id.settings).setChecked(true);
        }
        else if(previousActivity.equalsIgnoreCase("Activity.AboutActivity")){
            navBar.getMenu().findItem(R.id.about_us).setChecked(true);
        }
    }//onResume

    public void setListeners(){
        navBar.setOnNavigationItemSelectedListener(item -> {
            boolean success = false;
            if(item.getItemId() == R.id.about_us){
                goToAbout();
                success = true;
            }
            else if(item.getItemId() == R.id.favorites){
                goToFavorites();
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
            else if(item.getItemId() == R.id.search){
                goToSearch();
                success = true;
            }
            return success;
        });

        addFavorite.setOnClickListener(view -> {
            addToFavorites();
        });

        removeFavorite.setOnClickListener(view -> {
            removeFromFavorites();
        });
    }//setListeners

    private void addToFavorites(){
        try {
            int indexID = recipe.getJSONObject("recipe").get("uri").toString().indexOf("_") + 1;
            String recipeID = recipe.getJSONObject("recipe").get("uri").toString().substring(indexID);
            String name = recipe.getJSONObject("recipe").get("label").toString();
            String[] params = {"addFavorite", recipeID, name};
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(new HTTPRequestTask(params, activity));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//addToFavorites

    private void removeFromFavorites(){
        try {
            int indexID = recipe.getJSONObject("recipe").get("uri").toString().indexOf("_") + 1;
            String recipeID = recipe.getJSONObject("recipe").get("uri").toString().substring(indexID);
            String name = recipe.getJSONObject("recipe").get("label").toString();
            String[] params = {"deleteFavorite", recipeID, name};
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(new HTTPRequestTask(params, activity));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//removeFromFavorites

    public void checkFavorite(){
        try {
            int indexID = recipe.getJSONObject("recipe").get("uri").toString().indexOf("_") + 1;
            String recipeID = recipe.getJSONObject("recipe").get("uri").toString().substring(indexID);
            String[] params = {"is-favorite", recipeID};
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(new HTTPRequestTask(params, activity));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//checkFavorite

    public void updateFavoriteButtons(Boolean favorited){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(favorited){
                    addFavorite.setVisibility(View.GONE);
                    removeFavorite.setVisibility(View.VISIBLE);
                }
                else{
                    addFavorite.setVisibility(View.VISIBLE);
                    removeFavorite.setVisibility(View.GONE);
                }
            }
        });
    }//updateFavoriteButtons

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