package comp4350.recipe_shop_app_version.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import comp4350.recipe_shop_app_version.R;


public class SearchActivity extends AppCompatActivity {

    private BottomNavigationView navBar;
    private Spinner mealTypeSpinner;
    private TextView minTimeInput, maxTimeInput, keywordsInput;
    private Button searchButton;
    private String mealType, keywords;
    private int minTime, maxTime;

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

        navBar = findViewById(R.id.bottomNavigationView);
        navBar.setSelectedItemId(R.id.search);
        mealTypeSpinner = findViewById(R.id.meal_type_spinner);
        minTimeInput = findViewById(R.id.min_time_input);
        maxTimeInput = findViewById(R.id.max_time_input);
        keywordsInput = findViewById(R.id.keyword_input);
        searchButton = findViewById(R.id.searchButton);


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
                int position = adapterView.getPositionForView(view);

                if (position == 1) {
                    mealType = "--meal type--";
                } else if (position == 2) {
                    mealType = "Breakfast";
                } else if(position == 3){
                    mealType = "Dinner";
                }else if(position == 4){
                    mealType = "Lunch";
                }else if(position == 5){
                    mealType = "Snack";
                }else if(position == 6){
                    mealType = "Teatime";
                }
            }//onItemSelected

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }//onNothingSelected
        });

        searchButton.setOnClickListener(view -> {
            search();
        });

    }//setListeners

    private void search(){

    }//search

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