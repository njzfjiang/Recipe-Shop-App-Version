package comp4350.recipe_shop_app_version.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import comp4350.recipe_shop_app_version.Other.HTTPRequestTask;
import comp4350.recipe_shop_app_version.Other.ImageRequestTask;
import comp4350.recipe_shop_app_version.Other.RecipeListArrayAdapter;
import comp4350.recipe_shop_app_version.Other.Services;
import comp4350.recipe_shop_app_version.R;


public class RecipeInfoActivity extends AppCompatActivity {

    private BottomNavigationView navBar;
    ImageView recipeImage;
    private TextView recipeName, recipeSource, ingredients, instructionLink;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recipe_info_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        navBar = findViewById(R.id.bottomNavigationView);
        navBar.setSelectedItemId(R.id.search);
        recipeImage = findViewById(R.id.recipeImage);
        recipeName = findViewById(R.id.recipeName);
        recipeSource = findViewById(R.id.recipeSource);
        ingredients = findViewById(R.id.ingredients);
        instructionLink = findViewById(R.id.edamame);

        String name = "";
        String source = "";
        ArrayList<String> ingredientList = new ArrayList<>();
        String link = "";
        try {
            name = Services.recipe.getJSONObject("recipe").get("label").toString();
            source = Services.recipe.getJSONObject("recipe").get("source").toString();
            JSONArray ingredientLines = (JSONArray) Services.recipe.getJSONObject("recipe").get("ingredientLines");
            for(int i=0;i<ingredientLines.length();i++){
                ingredientList.add(ingredientLines.get(i).toString());
            }
            link = Services.recipe.getJSONObject("recipe").get("url").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        setListeners();
    }//onCreate

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
    }//setListeners

    private void goToSearch(){
        Intent finishIntent = new Intent(getApplicationContext(), RecipeInfoActivity.class);
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