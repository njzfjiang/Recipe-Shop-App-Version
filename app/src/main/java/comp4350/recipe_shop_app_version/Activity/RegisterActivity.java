package comp4350.recipe_shop_app_version.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import comp4350.recipe_shop_app_version.Other.HTTPRequestTask;
import comp4350.recipe_shop_app_version.Other.Services;
import comp4350.recipe_shop_app_version.R;


public class RegisterActivity extends AppCompatActivity {

    private TextView ipInput, usernameInput, passwordInput, confirmInput, message, messageUsername;
    private Button loginButton, registerButton;
    private Activity activity;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        activity = this;
        ipInput = findViewById(R.id.ip_input);
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        confirmInput = findViewById(R.id.confirm_input);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        message = findViewById(R.id.messageText);
        messageUsername = findViewById(R.id.messageUsernameText);

        ipInput.setText(Services.ip);
        usernameInput.setText(Services.username);
        if(!Services.username.isEmpty()){
            userExistsAttempt();
            String[] params = {"user-exists"};
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(new HTTPRequestTask(params,activity));
        }

        setListeners();
    }//onCreate

    public void setListeners(){
        loginButton.setOnClickListener(view -> {
            goToLogin();
        });

        registerButton.setOnClickListener(view -> {
            register();
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
                userExistsAttempt();
                String[] params = {"user-exists"};
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(new HTTPRequestTask(params,activity));
            }
        });
    }//setListeners

    private void register(){
        Services.ip = ipInput.getText().toString();
        Services.username = usernameInput.getText().toString();
        Services.password = passwordInput.getText().toString();
        Services.confirm = confirmInput.getText().toString();

        if(!Services.ip.isEmpty() && !Services.username.isEmpty() && !Services.password.isEmpty() && !Services.confirm.isEmpty()){
            registerAttempt();
            String[] params = {"register"};
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(new HTTPRequestTask(params,activity));
        }
        else{
            registerFail();
        }
    }//login

    private void goToLogin(){
        Intent finishIntent = new Intent(getApplicationContext(), LoginActivity.class);
        finishIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(finishIntent);
        this.finish();
    }//goToRegister

    public void registerFail(){
        message.setVisibility(View.VISIBLE);
        message.setText("Invalid ip, username, or password!");
        message.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.red));
    }//loginFail


    public void registerSuccess(){
        message.setVisibility(View.VISIBLE);
        message.setText("Registration Successful!");
        message.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.green));
        goToLogin();
    }//loginSuccess

    public void registerAttempt(){
        message.setVisibility(View.VISIBLE);
        message.setText("...");
        message.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.black));
    }//loginAttempt

    public void userExists(){
        messageUsername.setVisibility(View.VISIBLE);
        messageUsername.setText("Username already exists");
        messageUsername.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.red));
    }//userExists

    public void userNotExists(){
        messageUsername.setVisibility(View.VISIBLE);
        messageUsername.setText("Valid username");
        messageUsername.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.green));
    }//userExists

    public void userExistsAttempt(){
        messageUsername.setVisibility(View.GONE);
        messageUsername.setText("...");
        messageUsername.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.black));
    }//userExists

}