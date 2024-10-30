package comp4350.recipe_shop_app_version.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import comp4350.recipe_shop_app_version.R;


public class LoginActivity extends AppCompatActivity {

    private TextView ipInput, usernameInput, passwordInput;
    private Button loginButton, registerButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ipInput = findViewById(R.id.ip_input);
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        setListeners();
    }//onCreate

    public void setListeners(){
        loginButton.setOnClickListener(view -> {
            login();
        });
        registerButton.setOnClickListener(view -> {
            goToRegister();
        });
    }//setListeners

    private void login(){
        String ip = (String) ipInput.getText();
        String username = (String) usernameInput.getText();
        String password = (String) passwordInput.getText();
    }//login

    private void goToRegister(){
        Intent finishIntent = new Intent(getApplicationContext(), RegisterActivity.class);
        finishIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(finishIntent);
    }//goToRegister

}