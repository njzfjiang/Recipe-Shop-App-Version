package comp4350.recipe_shop_app_version.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

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
    private float yStart;
    private int yDead = 50;

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

        ipInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_NEXT || keyEvent != null){
                    usernameInput.requestFocus();
                }
                return true;
            }
        });

        usernameInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_NEXT || keyEvent != null){
                    passwordInput.requestFocus();
                }
                return true;
            }
        });

        passwordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_GO || keyEvent != null){
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    if(passwordInput.hasFocus()){
                        passwordInput.clearFocus();
                    }
                    login();
                }
                return true;
            }
        });

        ipInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(ipInput.hasFocus() || usernameInput.hasFocus() || passwordInput.hasFocus()){
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        usernameInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(ipInput.hasFocus() || usernameInput.hasFocus() || passwordInput.hasFocus()){
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        passwordInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(ipInput.hasFocus() || usernameInput.hasFocus() || passwordInput.hasFocus()){
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
                }
            }
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
                    }//outside bounds
                }//TextInputEditText
            }//within dead zone
        }//touch release

        return super.dispatchTouchEvent(event);
    }

    private void login(){
        Services.ip = ipInput.getText().toString();
        Services.username = usernameInput.getText().toString();
        Services.password = passwordInput.getText().toString();
        if(!Services.ip.isEmpty()){
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
        Services.password = "";
        Services.confirm = "";
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