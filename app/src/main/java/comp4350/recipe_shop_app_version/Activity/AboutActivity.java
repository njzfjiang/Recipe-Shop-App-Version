package comp4350.recipe_shop_app_version.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import comp4350.recipe_shop_app_version.Other.Services;
import comp4350.recipe_shop_app_version.R;



public class AboutActivity extends AppCompatActivity {

    private BottomNavigationView navBar;
    private TextView edamameLink, githubLink, githubAppLink, webpageLink;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);

        navBar = findViewById(R.id.bottomNavigationView);
        navBar.setSelectedItemId(R.id.about_us);
        edamameLink = findViewById(R.id.edamame);
        githubLink = findViewById(R.id.github);
        githubAppLink = findViewById(R.id.githubApp);
        webpageLink = findViewById(R.id.webpage);

        edamameLink.setMovementMethod(LinkMovementMethod.getInstance());
        githubLink.setMovementMethod(LinkMovementMethod.getInstance());
        githubAppLink.setMovementMethod(LinkMovementMethod.getInstance());
        webpageLink.setText(Html.fromHtml("<a href=\"http://" + Services.ip + "\">Webpage Verion</a>"));
        webpageLink.setMovementMethod(LinkMovementMethod.getInstance());

        setListeners();
    }//onCreate

    public void setListeners(){
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
            else if(item.getItemId() == R.id.settings){
                goToSettings();
                success = true;
            }
            return success;
        });
    }//setListeners

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