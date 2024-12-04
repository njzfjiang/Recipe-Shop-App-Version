package comp4350.recipe_shop_app_version.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import comp4350.recipe_shop_app_version.Other.HTTPRequestTask;
import comp4350.recipe_shop_app_version.Other.ImageRequestTask;
import comp4350.recipe_shop_app_version.Other.RecipeListArrayAdapter;
import comp4350.recipe_shop_app_version.Other.Services;
import comp4350.recipe_shop_app_version.R;


public class SettingsActivity extends AppCompatActivity {

    private BottomNavigationView navBar;
    private TextView ipInput, usernameInput, keywordsInput, message;
    private Button logoutButton, uploadButton;
    private String keywords = "";
    private ListView uploadList;
    private Activity activity;
    private ArrayList<JSONObject> recipes;
    private ArrayList<Bitmap> images;
    private ArrayList<ArrayList> listList;
    private ArrayAdapter<JSONObject> listArrayAdapter;
    private ArrayList<Boolean> visible;
    private float yStart;
    private int yDead = 50;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        activity = this;

        navBar = findViewById(R.id.bottomNavigationView);
        navBar.setSelectedItemId(R.id.settings);
        ipInput = findViewById(R.id.ip);
        usernameInput = findViewById(R.id.username);
        logoutButton = findViewById(R.id.logoutButton);
        keywordsInput = findViewById(R.id.keyword_input);
        message = findViewById(R.id.messageText);
        uploadList = findViewById(R.id.listView);
        uploadButton = findViewById(R.id.uploadButton);

        String ip = getResources().getString(R.string.ip_label) + Services.ip;
        ipInput.setText(ip);
        String username = getResources().getString(R.string.username_label) + Services.username;
        usernameInput.setText(username);

        setListeners();
    }//onCreate

    @Override
    protected void onResume() {
        super.onResume();
        navBar.getMenu().findItem(R.id.settings).setChecked(true);
        getUploads();
    }//onResume

    public void setListeners(){
        logoutButton.setOnClickListener(view -> {
            logout();
        });

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
            else if(item.getItemId() == R.id.grocery){
                goToGrocery();
                success = true;
            }
            else if(item.getItemId() == R.id.about_us){
                goToAbout();
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
                        JSONObject json = ((RecipeListArrayAdapter)uploadList.getAdapter()).getRecipe(i);
                        String title = "";
                        if(json.has("recipe")){
                            title = json.getJSONObject("recipe").get("label").toString();
                        }
                        else if(json.has("find_recipe")){
                            title =  json.getJSONObject("find_recipe").get("title").toString();
                        }
                        System.out.println(title);
                        if(title.toLowerCase().contains(keywords.toLowerCase())){
                            ((RecipeListArrayAdapter) uploadList.getAdapter()).setVisibility(i, true);
                            updateListView();
                        }
                        else{
                            ((RecipeListArrayAdapter) uploadList.getAdapter()).setVisibility(i, false);
                            updateListView();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

        uploadButton.setOnClickListener(view -> {
            goToUpload();
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

    private void logout(){
        Services.password = "";
        Services.confirm = "";
        Intent finishIntent = new Intent(getApplicationContext(), LoginActivity.class);
        finishIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finishIntent.putExtra("CallingActivity", activity.getLocalClassName());
        startActivity(finishIntent);
        this.finish();
    }


    private void getUploads(){
        recipes = new ArrayList<>();
        images = new ArrayList<>();
        listList = new ArrayList<>();
        uploadList.setVisibility(View.GONE);
        getUploadsAttempt();
        String[] params = {"all-uploads"};
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new HTTPRequestTask(params,activity));
    }//getFavorites

    //----------------------------------------------------------------------------------------------
    private void getUploadsAttempt(){
        message.setVisibility(View.VISIBLE);
        message.setText("...");
        message.setTextColor(usernameInput.getCurrentTextColor());
    }//uploadSuccess

    public void getUploadsSuccess(String response){
        try{
            JSONArray recipeArray = new JSONObject(response).getJSONArray("recipes");
            System.out.println(recipeArray.length());
            for(int i=0;i<recipeArray.length();i++){
                recipes.add(null);
                images.add(null);
                getShopRecipe(i, recipeArray.getJSONObject(i).get("_id").toString());
                System.out.println(i);
            }
            listList.add(recipes);
            listList.add(images);
            listArrayAdapter = new RecipeListArrayAdapter(this,R.layout.recipe_list_layout, listList, activity);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    uploadList.setAdapter(listArrayAdapter);
                    updateListView();
                    message.setVisibility(View.GONE);
                    uploadList.setVisibility(View.VISIBLE);
                }//run
            });

            uploadList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
    }//uploadSuccess

    private void getShopRecipe(int pos, String id){
        try {
            System.out.println("getShopRecipe");
            System.out.println(id);
            String[] params = {"shop-recipe", String.valueOf(pos), id};
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(new HTTPRequestTask(params, activity));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//getShopRecipe

    public void getShopRecipeSuccess(int pos, String response){
        try {
            JSONObject recipe = new JSONObject(response);
            Bitmap image = null;
            byte[] decoded = null;
            try {
                decoded = Base64.getDecoder().decode(recipe.getJSONObject("find_recipe").get("image").toString());
                image = BitmapFactory.decodeByteArray(decoded,0, decoded.length);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Bitmap finalImage = image;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recipes.set(pos, recipe);
                    ((RecipeListArrayAdapter)uploadList.getAdapter()).setRecipe(pos, recipe);
                    if(finalImage != null) {
                        ((RecipeListArrayAdapter) uploadList.getAdapter()).setImage(pos, finalImage);
                    }
                    updateListView();
                }
            });
            /*
            try {
                String url = recipe.getJSONObject("recipe").get("image").toString();
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(new ImageRequestTask(pos, url, activity));
            } catch (Exception e) {
                e.printStackTrace();
            }
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//getShopRecipeSuccess



    public void getShopRecipeFail(int pos){

    }//getShopRecipeFail



    private void updateListView(){
        ViewGroup.LayoutParams layoutParams = uploadList.getLayoutParams();
        layoutParams.height = 1000000000; //set big so all list elements draw before get their heights
        uploadList.setLayoutParams(layoutParams);

        uploadList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                uploadList.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                visible = ((RecipeListArrayAdapter)uploadList.getAdapter()).getVisibility();
                int height = 0;
                for(int i=0;i<uploadList.getChildCount();i++){
                    View listItem = uploadList.getChildAt(i);
                    height += listItem.getHeight() + uploadList.getDividerHeight();
                }
                ViewGroup.LayoutParams layoutParams = uploadList.getLayoutParams();
                layoutParams.height = height - uploadList.getDividerHeight();
                uploadList.setLayoutParams(layoutParams);
            }
        });
    }//updateListView

    public void getUploadsEmpty(String data){
        message.setVisibility(View.VISIBLE);
        try {
            message.setText(new JSONObject(data).get("error").toString());
        } catch (JSONException e) {
            e.printStackTrace();
            message.setText("No uploaded recipes found!");
        }
        message.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.red));
    }//uploadFail

    public void getUploadsFail(String data){
        message.setVisibility(View.VISIBLE);
        try {
            message.setText(new JSONObject(data).get("error").toString());
        } catch (JSONException e) {
            e.printStackTrace();
            message.setText("Failed to retrieve uploaded recipes!");
        }
        message.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.red));
    }//uploadFail
    //----------------------------------------------------------------------------------------------

    private void goToRecipe(){
        Intent finishIntent = new Intent(getApplicationContext(), RecipeInfoActivity.class);
        finishIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finishIntent.putExtra("CallingActivity", activity.getLocalClassName());
        startActivity(finishIntent);
    }//goToRecipe

    private void goToUpload(){
        Intent finishIntent = new Intent(getApplicationContext(), UploadActivity.class);
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