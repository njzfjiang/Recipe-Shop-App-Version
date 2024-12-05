package comp4350.recipe_shop_app_version.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import comp4350.recipe_shop_app_version.Other.HTTPRequestTask;
import comp4350.recipe_shop_app_version.Other.RecipeListArrayAdapter;
import comp4350.recipe_shop_app_version.Other.Services;
import comp4350.recipe_shop_app_version.R;


public class UploadActivity extends AppCompatActivity {

    private BottomNavigationView navBar;
    private ImageView recipeImage;
    private TextView recipeName, recipeSource, uploader, recipeInstructions, message;
    private Button addIngredient, uploadImage, uploadRecipe;
    private LinearLayout ingredientList;
    private Spinner privacySpinner;
    private String privacy = "";
    private Activity activity;
    private JSONObject recipe;
    private Bitmap image;
    private String previousActivity;
    private int ingredientID = 0;
    private ArrayList<View> ingredientViews;
    private ArrayList<String> ingredients;
    private float yStart;
    private int yDead = 50;

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
        recipeInstructions = findViewById(R.id.instruction_input);
        addIngredient = findViewById(R.id.addIngredientButton);
        uploadImage = findViewById(R.id.uploadImageButton);
        privacySpinner = findViewById(R.id.privacy_spinner);
        message = findViewById(R.id.messageText);
        uploadRecipe = findViewById(R.id.uploadButton);

        String uploadBy = getResources().getString(R.string.uploader) + Services.username;
        uploader.setText(uploadBy);
        ingredients = new ArrayList<>();
        ingredientViews = new ArrayList<>();
        addIngredientLine();
        image = null;

        ArrayList<String> privacyOptions = new ArrayList<>();
        privacyOptions.add("public");
        privacyOptions.add("private");
        ArrayAdapter spinnerViewAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_layout, R.id.listText, privacyOptions);
        privacySpinner.setAdapter(spinnerViewAdapter);

        setListeners();
        privacySpinner.setSelection(0);
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

        privacySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int spinnerPosition = adapterView.getPositionForView(view);
                if (spinnerPosition == 0) {
                    privacy = "public";
                } else if (spinnerPosition == 1) {
                    privacy = "private";
                }
            }//onItemSelected
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
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

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    System.out.println("PhotoPicker\t Selected URI: " + uri);
                    try {
                        image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        image = resizeImageResolution(image, 500);
                        System.out.println("resized to 500");
                        int quality = 100;
                        while(!smallerThan(image, 10000000) && quality > 10){
                            quality -= 10;
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            image.compress(Bitmap.CompressFormat.PNG, quality, byteArrayOutputStream);
                            byte[] decoded = byteArrayOutputStream.toByteArray();
                            //String encoded = Base64.getEncoder().encodeToString(imageBase64);
                            //[] decoded = Base64.getDecoder().decode(encoded);
                            image = BitmapFactory.decodeByteArray(decoded,0, decoded.length);
                        }
                        recipeImage.setImageBitmap(image);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("PhotoPicker\t No media selected");
                }
            });

    private Bitmap resizeImageResolution(Bitmap image, int newMaxDim){
        int width = image.getWidth();
        int height = image.getHeight();
        float aspectRatio = (float)width / (float)height;
        if(aspectRatio > 1){
            width = newMaxDim;
            height = (int) (width / aspectRatio);
        }
        else{
            height = newMaxDim;
            width = (int) (height * aspectRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }//resizeImageResolution


    //checks if image is smaller than maxSize bytes
    private boolean smallerThan(Bitmap image, int maxSize){
        boolean result = false;
        int size;
        int quality = 100;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        image.compress(Bitmap.CompressFormat.PNG, quality, byteArrayOutputStream);
        byte[] imageBase64 = byteArrayOutputStream.toByteArray();
        String encoded = Base64.getEncoder().encodeToString(imageBase64);

        byte[] encodedBytes = encoded.getBytes();
        size = encodedBytes.length;

        if(size < maxSize){
            result = true;
        }
        return result;
    }//smallerThan

    private void uploadRecipe() {
        int count = 0;
        for(int i=0; i<ingredientViews.size(); i++){
            if(ingredientViews.get(i) != null){
                count += 1;
            }
        }
        Services.recipeImage = image;
        String ingredientString = "";
        int added = 0;
        for(int i=0; i<ingredientViews.size(); i++){
            if(ingredientViews.get(i) != null) {
                TextView tv = ingredientViews.get(i).findViewById(R.id.ingredient_input);
                added += 1;
                if(tv.getText().length() > 0) {
                    ingredientString += "\t\t\"" + tv.getText() + "\"";
                    if(added < count){
                        ingredientString += ",";
                        ingredientString += "\n";
                    }
                }
            }
        }

        String[] params = {"upload", recipeName.getText().toString(), recipeSource.getText().toString(),
                ingredientString, recipeInstructions.getText().toString().replaceAll("\n", "\\\\n"), privacy};
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new HTTPRequestTask(params,activity));
        uploadAttempt();
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
        ingredients.add(null);
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
            ingredients.set(index, null);
            updateIngredientRequire();
        }
    }//removeIngredientLines

    private void uploadAttempt(){
        message.setVisibility(View.VISIBLE);
        message.setText("...");
        message.setTextColor(uploader.getCurrentTextColor());
    }//uploadAttempt

    public void uploadSuccess() {
        message.setVisibility(View.VISIBLE);
        message.setText("Upload Successful!");
        message.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.green));
        this.finish();
    }//uploadSuccess

    public void uploadEmpty(String data) {
        message.setVisibility(View.VISIBLE);
        try {
            message.setText(new JSONObject(data).get("error").toString());
        } catch (JSONException e) {
            e.printStackTrace();
            message.setText("Failed to upload!\nInvalid uploader username");
        }
        message.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.red));
    }//uploadEmpty

    public void uploadFail(String data) {
        message.setVisibility(View.VISIBLE);
        try {
            message.setText(new JSONObject(data).get("error").toString());
        } catch (JSONException e) {
            e.printStackTrace();
            message.setText("Failed to upload!\nCheck all required fields are filled out");
        }
        message.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.red));
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