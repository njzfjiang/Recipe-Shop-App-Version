package comp4350.recipe_shop_app_version.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import comp4350.recipe_shop_app_version.Other.HTTPRequestTask;
import comp4350.recipe_shop_app_version.Other.RecipeListArrayAdapter;
import comp4350.recipe_shop_app_version.Other.Services;
import comp4350.recipe_shop_app_version.R;


public class UploadActivity extends AppCompatActivity {

    private BottomNavigationView navBar;
    private ImageView recipeImage;
    private TextView recipeName, recipeSource, uploader, instructions;
    private Button addIngredient, uploadImage, uploadRecipe;
    private LinearLayout ingredientList;
    private Activity activity;
    private JSONObject recipe;
    private Bitmap image;
    private String previousActivity;
    private int ingredientID = 0;
    private ArrayList<View> ingredientViews;
    private ArrayList<String> ingredients;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload);

        activity = this;

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
        recipeName = findViewById(R.id.title_input);
        recipeSource = findViewById(R.id.source_input);
        uploader = findViewById(R.id.username);
        ingredientList = findViewById(R.id.ingredientItemLayout);
        instructions = findViewById(R.id.instruction_input);
        addIngredient = findViewById(R.id.addIngredientButton);
        uploadImage = findViewById(R.id.uploadImageButton);
        uploadRecipe = findViewById(R.id.uploadButton);

        uploader.setText(R.string.uploader + Services.username);
        ingredients = new ArrayList<>();
        ingredientViews = new ArrayList<>();
        addIngredientLine();
        image = null;

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

        addIngredient.setOnClickListener(view -> {
            addIngredientLine();
        });

        uploadImage.setOnClickListener(view -> {
            uploadRecipeImage();
        });

        uploadRecipe.setOnClickListener(view -> {
            uploadRecipe();
        });
    }//setListeners

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    System.out.println("PhotoPicker\t Selected URI: " + uri);
                    try {
                        image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        recipeImage.setImageBitmap(image);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("PhotoPicker\t No media selected");
                }
            });

    private void uploadRecipe() {
        Services.recipeImage = image;
        //send http request task
    }//uploadRecipe

    private void uploadRecipeImage() {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }//uploadRecipeImage

    private void addIngredientLine() {
        View ingredientItem = LayoutInflater.from(this).inflate(R.layout.ingredient_item_layout,ingredientList, false);

        int index = ingredientID;
        TextView require = ingredientItem.findViewById(R.id.ingredientRequired);

        TextView input = ingredientItem.findViewById(R.id.ingredient_input);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                ingredients.set(index, input.getText().toString());
            }
        });

        Button button = ingredientItem.findViewById(R.id.removeButton);
        button.setOnClickListener(view -> {
            removeIngredientLine(index);
        });

        ingredientViews.add(ingredientItem);
        ingredientList.addView(ingredientItem);
        ingredientID += 1;
        updateIngredientRequire();
    }//addIngredientLines

    private void updateIngredientRequire() {
        int count = 0;
        for(int i=0; i<ingredientViews.size(); i++){
            if(ingredientViews.get(i) != null){
                count += 1;
            }
        }
        boolean found = false;
        for(int i=0; i<ingredientViews.size(); i++){
            if(ingredientViews.get(i) != null){
                if(count > 1){
                    ingredientViews.get(i).findViewById(R.id.removeButton).setVisibility(View.VISIBLE);
                }
                else{
                    ingredientViews.get(i).findViewById(R.id.removeButton).setVisibility(View.INVISIBLE);
                }
                if(!found){
                    ingredientViews.get(i).findViewById(R.id.ingredientRequired).setVisibility(View.VISIBLE);
                    found = true;
                }
                else{
                    ingredientViews.get(i).findViewById(R.id.ingredientRequired).setVisibility(View.INVISIBLE);
                }
            }
        }
    }//updateIngredientRequire

    private void removeIngredientLine(int index) {
        int count = 0;
        for(int i=0; i<ingredientViews.size(); i++){
            if(ingredientViews.get(i) != null){
                count += 1;
            }
        }
        if(count >= 2) {
            ingredientList.removeView(ingredientViews.get(index));
            ingredientViews.set(index, null);
            ingredients.set(index, "");
            updateIngredientRequire();
        }
    }//removeIngredientLines

    public void uploadSuccess() {
    }//uploadSuccess

    public void uploadEmpty() {
    }//uploadEmpty

    public void uploadFail() {
    }//uploadFail


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