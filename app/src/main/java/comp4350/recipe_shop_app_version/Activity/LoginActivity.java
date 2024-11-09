package comp4350.recipe_shop_app_version.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import comp4350.recipe_shop_app_version.R;
import comp4350.recipe_shop_app_version.Other.Services;
import comp4350.recipe_shop_app_version.Other.HTTPRequestTask;


public class LoginActivity extends AppCompatActivity {

    private TextView ipInput, usernameInput, passwordInput, message;
    private Button loginButton, registerButton;
    private Activity activity;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        activity = this;
        ipInput = findViewById(R.id.ip_input);
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        message = findViewById(R.id.messageText);

        ipInput.setText(Services.ip);
        usernameInput.setText(Services.username);

        setListeners();
    }//onCreate

    public void setListeners(){
        loginButton.setOnClickListener(view -> {
            login();
        });

        registerButton.setOnClickListener(view -> {
            goToRegister();
        });

        ipInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                Services.ip = ipInput.getText().toString();
            }
        });

        usernameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                Services.username = usernameInput.getText().toString();
            }
        });
    }//setListeners

    private void login(){
        Services.ip = ipInput.getText().toString();
        Services.username = usernameInput.getText().toString();
        Services.password = passwordInput.getText().toString();
        if(!Services.ip.isEmpty() && !Services.username.isEmpty() && !Services.password.isEmpty()){
            loginAttempt();
            String[] params = {"login"};
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(new HTTPRequestTask(params,activity));
        }
        else{
            loginFail();
        }
    }//login

    private void goToRegister(){
        Intent finishIntent = new Intent(getApplicationContext(), RegisterActivity.class);
        finishIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(finishIntent);
        this.finish();
    }//goToRegister

    private void goToSearch(){
        Intent finishIntent = new Intent(getApplicationContext(), SearchActivity.class);
        finishIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(finishIntent);
        this.finish();
    }//goToSearch


    public void loginFail(){
        message.setVisibility(View.VISIBLE);
        message.setText("Invalid ip, username, or password!");
        message.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.red));
    }//loginFail


    public void loginSuccess(){
        message.setVisibility(View.VISIBLE);
        message.setText("Login Successful!");
        message.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.green));
        goToSearch();
    }//loginSuccess

    public void loginAttempt(){
        message.setVisibility(View.VISIBLE);
        message.setText("...");
        message.setTextColor(usernameInput.getCurrentTextColor());
    }//loginAttempt

}