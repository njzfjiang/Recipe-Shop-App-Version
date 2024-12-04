package comp4350.recipe_shop_app_version.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
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
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import comp4350.recipe_shop_app_version.Other.HTTPRequestTask;
import comp4350.recipe_shop_app_version.Other.ImageRequestTask;
import comp4350.recipe_shop_app_version.Other.RecipeListArrayAdapter;
import comp4350.recipe_shop_app_version.Other.Services;
import comp4350.recipe_shop_app_version.R;


public class RecipeInfoActivity extends AppCompatActivity {

    private BottomNavigationView navBar;
    private ImageView recipeImage;
    private TextView recipeName, recipeSource, recipeAuthor, recipeUploader, ingredients, instructionLink, message;
    private Button addFavorite, removeFavorite, deleteRecipe;
    private Activity activity;
    private JSONObject recipe;
    private Bitmap image;
    private String previousActivity;
    private Dialog dialog;

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
        recipeAuthor = findViewById(R.id.recipeAuthor);
        recipeUploader = findViewById(R.id.recipeUploader);
        ingredients = findViewById(R.id.ingredients);
        instructionLink = findViewById(R.id.instructionLink);
        addFavorite = findViewById(R.id.favoriteButton);
        removeFavorite = findViewById(R.id.unfavoriteButton);
        message = findViewById(R.id.messageText);
        deleteRecipe = findViewById(R.id.deleteButton);

        String name = "";
        String source = "";
        String author = "";
        String uploader = "";
        ArrayList<String> ingredientList = new ArrayList<>();
        String link = "";
        String instructions = "";
        try {
            if(recipe.has("recipe")) {
                name = recipe.getJSONObject("recipe").get("label").toString();
                source = recipe.getJSONObject("recipe").get("source").toString();
                JSONArray ingredientLines = (JSONArray) recipe.getJSONObject("recipe").get("ingredientLines");
                for (int i = 0; i < ingredientLines.length(); i++) {
                    ingredientList.add("\u2022" + ingredientLines.get(i).toString() + "\n");
                }
                link = recipe.getJSONObject("recipe").get("url").toString();
            }
            else if(recipe.has("find_recipe")){
                name = recipe.getJSONObject("find_recipe").get("title").toString();
                source = "Recipe Shop";
                author = recipe.getJSONObject("find_recipe").get("source").toString();
                uploader = recipe.getJSONObject("find_recipe").get("username").toString();
                JSONArray ingredientLines = (JSONArray) recipe.getJSONObject("find_recipe").get("ingredients");
                for (int i = 0; i < ingredientLines.length(); i++) {
                    ingredientList.add("\u2022" + ingredientLines.get(i).toString() + "\n");
                }
                instructions = recipe.getJSONObject("find_recipe").get("instructions").toString();
            }
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

        if(!author.isEmpty()){
            author = getResources().getString(R.string.author) + author;
            recipeAuthor.setText(author);
            recipeAuthor.setVisibility(View.VISIBLE);
        }
        else{
            recipeAuthor.setVisibility(View.GONE);
        }

        if(uploader.equals(Services.username)){
            deleteRecipe.setVisibility(View.VISIBLE);
        }
        else{
            deleteRecipe.setVisibility(View.GONE);
        }

        if(!uploader.isEmpty()){
            uploader = getResources().getString(R.string.uploader) + uploader;
            recipeUploader.setText(uploader);
            recipeUploader.setVisibility(View.VISIBLE);
        }
        else{
            recipeUploader.setVisibility(View.GONE);
        }

        if(!link.isEmpty()){
            String linkText = (String) instructionLink.getText();
            instructionLink.setText(Html.fromHtml("<a href=\"" + link + "\">" + linkText + "</a>"));
            instructionLink.setMovementMethod(LinkMovementMethod.getInstance());
        }
        else{
            instructionLink.setText(instructions);
        }

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

        deleteRecipe.setOnClickListener(view -> {
            delete();
        });
    }//setListeners

    private void addToFavorites(){
        try {
            String recipeID = null;
            String name = null;
            String source = null;
            if(recipe.has("recipe")) {
                int indexID = recipe.getJSONObject("recipe").get("uri").toString().indexOf("_") + 1;
                recipeID = recipe.getJSONObject("recipe").get("uri").toString().substring(indexID);
                name = recipe.getJSONObject("recipe").get("label").toString();
            }
            else if(recipe.has("find_recipe")){
                recipeID = recipe.getJSONObject("find_recipe").get("_id").toString();
                name = recipe.getJSONObject("find_recipe").get("title").toString();
                source = "recipe-shop";
            }
            String[] params = {"addFavorite", recipeID, name, source};
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(new HTTPRequestTask(params, activity));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//addToFavorites

    private void removeFromFavorites(){
        try {
            String recipeID = null;
            String name = null;
            String[] params;
            if(recipe.has("recipe")) {
                int indexID = recipe.getJSONObject("recipe").get("uri").toString().indexOf("_") + 1;
                recipeID = recipe.getJSONObject("recipe").get("uri").toString().substring(indexID);
                name = recipe.getJSONObject("recipe").get("label").toString();
                params = new String[]{"deleteFavorite", recipeID, name};
            }
            else{
                recipeID = recipe.getJSONObject("find_recipe").get("_id").toString();
                name = recipe.getJSONObject("find_recipe").get("title").toString();
                params = new String[]{"deleteFavorite", recipeID, name, "recipe-shop"};
            }
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(new HTTPRequestTask(params, activity));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//removeFromFavorites

    private void delete(){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.confirm_delete_dialog_layout);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(getColor(R.color.transparent)));
        Button confirmDelete = dialog.findViewById(R.id.confirmDeleteButton);
        Button cancel = dialog.findViewById(R.id.cancelButton);

        confirmDelete.setOnClickListener(view -> {
            System.out.println("confirmedDeleteButton pressed");
            confirmedDelete();
        });

        cancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        message.setVisibility(View.GONE);
        dialog.show();
    }//delete

    private void confirmedDelete(){
        String recipeID = null;
        String name = null;
        try {
            recipeID = recipe.getJSONObject("find_recipe").get("_id").toString();
            name = recipe.getJSONObject("find_recipe").get("title").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(recipeID);
        String[] params = {"delete-recipe", recipeID, name};
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new HTTPRequestTask(params, activity));
    }//confirmedDelete

    public void deleteSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                message.setVisibility(View.VISIBLE);
                message.setText("Delete Successful!");
                message.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.green));
                activity.finish();
            }
        });
    }//deleteSuccess

    public void deleteFailed(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                message.setVisibility(View.VISIBLE);
                message.setText("Delete Failed!");
                message.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.red));
            }
        });
    }//deleteFailed

    public void checkFavorite(){
        try {
            int indexID;
            String recipeID;
            String[] params;
            if(recipe.has("recipe")) {
                indexID = recipe.getJSONObject("recipe").get("uri").toString().indexOf("_") + 1;
                recipeID = recipe.getJSONObject("recipe").get("uri").toString().substring(indexID);
                params = new String[]{"is-favorite", recipeID};
            }
            else{
                recipeID = recipe.getJSONObject("find_recipe").get("_id").toString();
                params = new String[]{"is-favorite", recipeID, "recipe-shop"};
            }
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