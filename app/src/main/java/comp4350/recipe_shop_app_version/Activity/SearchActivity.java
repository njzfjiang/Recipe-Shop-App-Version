package comp4350.recipe_shop_app_version.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

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
    private View activityLayout;
    private float yStart;
    private int yDead = 50;
    private WindowInsets defaultInsets;
    private ArrayList<Integer> heights;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);


        activity = this;
        activityLayout = findViewById(R.id.search_layout);
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

    @Override
    protected void onResume() {
        super.onResume();
        navBar.getMenu().findItem(R.id.search).setChecked(true);
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

        keywordsInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEARCH || keyEvent != null){
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    search();
                    navBar.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        minTimeInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEARCH || keyEvent != null){
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    search();
                    navBar.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        maxTimeInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEARCH || keyEvent != null){
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    search();
                    navBar.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });


        keywordsInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(keywordsInput.hasFocus() || minTimeInput.hasFocus() || maxTimeInput.hasFocus()){
                    navBar.setVisibility(View.GONE);
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        minTimeInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(keywordsInput.hasFocus() || minTimeInput.hasFocus() || maxTimeInput.hasFocus()){
                    navBar.setVisibility(View.GONE);
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        maxTimeInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(keywordsInput.hasFocus() || minTimeInput.hasFocus() || maxTimeInput.hasFocus()){
                    navBar.setVisibility(View.GONE);
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), (v, insets) -> {
            boolean imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime());
            if(!imeVisible){
                navBar.setVisibility(View.VISIBLE);
            }
            else{
                navBar.setVisibility(View.GONE);
            }
            return insets;
        });

    }//setListeners


    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            yStart = event.getY();
        }
        else if(event.getAction() == MotionEvent.ACTION_UP){
            //if tap
            if(!(event.getY() < yStart - yDead || event.getY() > yStart + yDead)) {
                View touchedView = getCurrentFocus();
                if (touchedView instanceof TextInputEditText) {
                    Rect viewBounds = new Rect();
                    touchedView.getGlobalVisibleRect(viewBounds);
                    if (!viewBounds.contains((int) event.getRawX(), (int) event.getRawY())) {
                        touchedView.clearFocus();
                        //hide keyboard
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(touchedView.getWindowToken(), 0);
                        navBar.setVisibility(View.VISIBLE);
                    }//outside bounds
                }//TextInputEditText
                else{
                    navBar.setVisibility(View.VISIBLE);
                }
            }//within dead zone
        }//touch release

        return super.dispatchTouchEvent(event);
    }


    private void search(){
        recipes = new ArrayList<>();
        images = new ArrayList<>();
        listList = new ArrayList<>();
        heights = new ArrayList<>();
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
        message.setTextColor(keywordsInput.getCurrentTextColor());
    }//searchAttempt

    private void keywordsEmpty(){
        message.setVisibility(View.VISIBLE);
        message.setText("Please enter keywords and try again!");
        message.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.red));
    }//searchEmpty

    private void searchEmpty(){
        message.setVisibility(View.VISIBLE);
        message.setText("No results.");
        message.setTextColor(keywordsInput.getCurrentTextColor());
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
                    heights.add(0);
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
                images.set(pos, image);
                ((RecipeListArrayAdapter)recipeList.getAdapter()).setImage(pos, image);
                updateListView();
            }
        });
    }//loadImage

    private void updateListView(){
        ViewGroup.LayoutParams layoutParams = recipeList.getLayoutParams();
        layoutParams.height = 1000000000; //set big so all list elements draw before get their heights
        recipeList.setLayoutParams(layoutParams);

        recipeList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recipeList.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int height = 0;
                for(int i=0;i<recipeList.getChildCount();i++){
                    View listItem = recipeList.getChildAt(i);
                    height += listItem.getHeight() + recipeList.getDividerHeight();
                }
                ViewGroup.LayoutParams layoutParams = recipeList.getLayoutParams();
                layoutParams.height = height - recipeList.getDividerHeight();
                recipeList.setLayoutParams(layoutParams);
            }
        });
    }//updateListView


    private void goToRecipe(){
        Intent finishIntent = new Intent(getApplicationContext(), RecipeInfoActivity.class);
        finishIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finishIntent.putExtra("CallingActivity", activity.getLocalClassName());
        startActivity(finishIntent);
    }//goToSearch

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