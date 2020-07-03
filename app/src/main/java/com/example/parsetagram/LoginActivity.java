package com.example.parsetagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    TextView tvTitle;
    EditText etUsername;
    EditText etPassword;
    Button btnSignUp;
    Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // check for persistance. If user already logged in instantly go to next screen
        if(ParseUser.getCurrentUser() != null){
            gotoMainActivity();
        }

        //set all my views
        tvTitle = findViewById(R.id.tvTitle);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogin = findViewById(R.id.btnLogin);

        //when you press login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the username and password from edit text
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                
                Login(username, password);
            }
        });

        //when you press sign up
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the username and password from edit text
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                Signup(username, password);
            }
        });
    }

    //create a new account
    private void Signup(String username, String password) {
        // create a new ParseUser and set mandatory fields
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    // this means no exception so sign up was successful
                    Toast.makeText(LoginActivity.this, "New Account Created", Toast.LENGTH_SHORT).show();
                } else {
                    //Unsuccessful signup
                    Log.e("LoginActivity", "Error signup failed", e);
                }
            }
        });
    }

    private void Login(String username, String password) {
        Log.i(TAG, "Attempting to login user " + username);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null){
                    //something wrong happened
                    Log.e(TAG, "issue with login", e);
                    Toast.makeText(LoginActivity.this, "Issue with login", Toast.LENGTH_SHORT).show();
                    return;
                }
                // if e == null. Successful login.
                gotoMainActivity();
                Toast.makeText(LoginActivity.this, "Success!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void gotoMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
