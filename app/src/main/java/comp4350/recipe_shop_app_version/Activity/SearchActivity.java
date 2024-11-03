package comp4350.recipe_shop_app_version.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import comp4350.recipe_shop_app_version.Other.HTTPRequestTask;
import comp4350.recipe_shop_app_version.Other.ImageRequestTask;
import comp4350.recipe_shop_app_version.Other.RecipeListArrayAdapter;
import comp4350.recipe_shop_app_version.Other.Services;
import comp4350.recipe_shop_app_version.R;


public class SearchActivity extends AppCompatActivity {

    private BottomNavigationView navBar;
    private Spinner mealTypeSpinner;
    private TextView minTimeInput, maxTimeInput, keywordsInput, message;
    private Button searchButton;
    private String mealType;
    private String keywords = "";
    private String minTime, maxTime = "0";
    private Activity activity;
    private ListView recipeList;
    private ArrayList<JSONObject> recipes;
    private ArrayAdapter<JSONObject> listArrayAdapter;
    private Context context;
    private ArrayList<Bitmap> images;
    private ArrayList<ArrayList> listList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        activity = this;
        navBar = findViewById(R.id.bottomNavigationView);
        navBar.setSelectedItemId(R.id.search);
        mealTypeSpinner = findViewById(R.id.meal_type_spinner);
        minTimeInput = findViewById(R.id.min_time_input);
        maxTimeInput = findViewById(R.id.max_time_input);
        keywordsInput = findViewById(R.id.keyword_input);
        searchButton = findViewById(R.id.searchButton);
        message = findViewById(R.id.messageText);
        recipeList = findViewById(R.id.listView);
        context = this;

        //recipes = new ArrayList<>();

        ArrayList<String> mealTypeOptions = new ArrayList<>();
        mealTypeOptions.add("--meal type--");
        mealTypeOptions.add("Breakfast");
        mealTypeOptions.add("Dinner");
        mealTypeOptions.add("Lunch");
        mealTypeOptions.add("Snack");
        mealTypeOptions.add("Teatime");
        ArrayAdapter spinnerViewAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_layout, R.id.listText, mealTypeOptions);
        mealTypeSpinner.setAdapter(spinnerViewAdapter);

        setListeners();
        mealTypeSpinner.setSelection(0);
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
            return success;
        });

        mealTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int spinnerPosition = adapterView.getPositionForView(view);
                if (spinnerPosition == 0) {
                    mealType = "";
                } else if (spinnerPosition == 1) {
                    mealType = "Breakfast";
                } else if(spinnerPosition == 2){
                    mealType = "Dinner";
                }else if(spinnerPosition == 3){
                    mealType = "Lunch";
                }else if(spinnerPosition == 4){
                    mealType = "Snack";
                }else if(spinnerPosition == 5){
                    mealType = "Teatime";
                }
            }//onItemSelected
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        searchButton.setOnClickListener(view -> {
            search();
        });

    }//setListeners

    private void search(){
        recipes = new ArrayList<>();
        images = new ArrayList<>();
        listList = new ArrayList<>();
        recipeList.setVisibility(View.GONE);
        try {
            keywords = keywordsInput.getText().toString();
            minTime = minTimeInput.getText().toString();
            maxTime = maxTimeInput.getText().toString();
        }catch(Exception e){
            e.printStackTrace();
        }
        if(!keywords.isEmpty()){
            searchAttempt();
            String[] params = {"search", mealType, minTime, maxTime, keywords};
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(new HTTPRequestTask(params,activity));
        }
        else{
            keywordsEmpty();
        }
    }//search

    private void searchAttempt(){
        message.setVisibility(View.VISIBLE);
        message.setText("Searching ...");
        message.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.black));
    }//searchAttempt

    private void keywordsEmpty(){
        message.setVisibility(View.VISIBLE);
        message.setText("Please enter keywords and try again!");
        message.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.red));
    }//searchEmpty

    private void searchEmpty(){
        message.setVisibility(View.VISIBLE);
        message.setText("No results.");
        message.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.black));
    }//searchEmpty

    public void searchFail(){
        message.setVisibility(View.VISIBLE);
        message.setText("Search Failed!");
        message.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.red));
    }//searchFail

    public void searchSuccess(String response){
        System.out.println(response);
        try {
            JSONObject searchResults = new JSONObject(response);
            int from = (int) searchResults.get("from");
            int to = (int) searchResults.get("to");
            if(to>0){
                for(int i=from-1;i<to;i++){
                    JSONObject recipe = new JSONObject(searchResults.getJSONArray("hits").get(i).toString());
                    recipes.add(recipe);
                    images.add(null);
                }
                listList.add(recipes);
                listList.add(images);
                listArrayAdapter = new RecipeListArrayAdapter(this,R.layout.recipe_list_layout, listList, activity);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recipeList.setAdapter(listArrayAdapter);
                        updateListView();
                        message.setVisibility(View.GONE);
                        recipeList.setVisibility(View.VISIBLE);
                        for(int i=from-1;i<to;i++){
                            try {
                                String url = recipes.get(i).getJSONObject("recipe").get("image").toString();
                                ExecutorService executor = Executors.newSingleThreadExecutor();
                                executor.submit(new ImageRequestTask(i, url, activity));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }//for
                    }//run
                });

                recipeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        int itemPosition = adapterView.getPositionForView(view);
                        System.out.println(itemPosition);
                        Services.recipe = recipes.get(itemPosition);
                        Services.recipeImage = images.get(itemPosition);
                        goToRecipe();
                    }
                });
            }
            else{
                searchEmpty();
            }
        }catch (Exception e){
            e.printStackTrace();
            searchFail();
        }
    }//searchSuccess

    public void loadImage(int pos, Bitmap image){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((RecipeListArrayAdapter)recipeList.getAdapter()).setImage(pos, image);
                updateListView();
            }
        });
    }//loadImage

    private void updateListView(){
        ArrayAdapter arrayAdapter = (ArrayAdapter) recipeList.getAdapter();
        int height = 0;
        for(int i=0;i<arrayAdapter.getCount();i++){
            View listItem = arrayAdapter.getView(i,null, recipeList);
            listItem.measure(0,0);
            height += listItem.getMeasuredHeight() + recipeList.getDividerHeight();
        }
        ViewGroup.LayoutParams layoutParams = recipeList.getLayoutParams();
        layoutParams.height = height;
        recipeList.setLayoutParams(layoutParams);
    }//updateListView


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