package comp4350.recipe_shop_app_version.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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
import comp4350.recipe_shop_app_version.Other.Services;
import comp4350.recipe_shop_app_version.R;


public class SettingsActivity extends AppCompatActivity {

    private BottomNavigationView navBar;
    private TextView ipInput, usernameInput, keywordsInput, message;
    private Button logoutButton;
    private String keywords = "";
    private ListView uploadList;
    private Activity activity;
    private ArrayList<JSONObject> recipes;
    private ArrayList<Bitmap> images;
    private ArrayList<ArrayList> listList;
    private ArrayAdapter<JSONObject> listArrayAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        activity = this;

        navBar = findViewById(R.id.bottomNavigationView);
        navBar.setSelectedItemId(R.id.settings);
        ipInput = findViewById(R.id.ip_input);
        usernameInput = findViewById(R.id.username_input);
        logoutButton = findViewById(R.id.logoutButton);
        keywordsInput = findViewById(R.id.keyword_input);
        message = findViewById(R.id.messageText);
        uploadList = findViewById(R.id.listView);

        ipInput.setText(R.string.ip_label + Services.ip);
        usernameInput.setText(R.string.username_label + Services.username);

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
    }//setListeners

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

    public void getUploadsSuccess(){
        message.setVisibility(View.GONE);
        message.setText("No results.");
    }//uploadSuccess

    public void getUploadsEmpty(){
        message.setVisibility(View.VISIBLE);
        message.setText("No uploaded recipes!");
        message.setTextColor(usernameInput.getCurrentTextColor());
    }//uploadFail

    public void getUploadsFail(){
        message.setVisibility(View.VISIBLE);
        message.setText("Failed to retrieve uploaded recipes!");
        message.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.red));
    }//uploadFail
    //----------------------------------------------------------------------------------------------

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