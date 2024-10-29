package com.example.bookverse.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookverse.MainActivity;
import com.example.bookverse.R;
import com.example.bookverse.utilities.Constants;
import com.example.bookverse.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    //login
    EditText login_editEmail, login_editPassword, signup_username, signup_email, signup_password,
    signup_confirmPassword, signup_phoneNumber;
    Button login_btnLogin, signup_btnSigup;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    PreferenceManager preferenceManager;
    ProgressBar log_prbLoadin;
    TextView login_viewSignup, signup_btnViewLogin;
    LinearLayout signup_nextview;

    //signup
    LinearLayout layout_signup, layout_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        preferenceManager = new PreferenceManager(getApplicationContext());
        login_editEmail = findViewById(R.id.login_editEmail);
        login_editPassword = findViewById(R.id.login_edtPassword);
        login_viewSignup = findViewById(R.id.login_viewSignup);
        login_btnLogin = findViewById(R.id.log_btnLogin);
        log_prbLoadin = findViewById(R.id.log_prbLoadin);

        //signup
        layout_signup = findViewById(R.id.layout_signup);
        layout_login = findViewById(R.id.layout_login);
        signup_btnViewLogin = findViewById(R.id.signup_btnViewLogin);
        signup_username = findViewById(R.id.signup_editDisplayName);
        signup_email = findViewById(R.id.signup_editEmail);
        signup_password = findViewById(R.id.signup_editPassword);
        signup_confirmPassword = findViewById(R.id.signup_editConfirmPassword);
        signup_phoneNumber = findViewById(R.id.signup_editPhone);
        signup_nextview = findViewById(R.id.signup_nextview);

        login_btnLogin.setOnClickListener(view->{
            login_btnLogin.setVisibility(ProgressBar.VISIBLE);
            login_btnLogin.setVisibility(View.GONE);
            if (validateLogin()) {
                String email = login_editEmail.getText().toString();
                String password = login_editPassword.getText().toString();
                firestore.collection("users")
                        .whereEqualTo(Constants.KEY_EMAIL, email)
                        .whereEqualTo(Constants.KEY_PASSWORD, password)
                        .get()
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful() && !task.getResult().isEmpty()){
                                preferenceManager.putString(Constants.KEY_EMAIL, email);
                                preferenceManager.putString(Constants.KEY_PASSWORD, password);
                                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                                Intent login = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(login);
                                Toast.makeText(getApplicationContext(), R.string.notifiloginSuccess, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), R.string.notifiLoginFailure, Toast.LENGTH_SHORT).show();
                                login_btnLogin.setVisibility(ProgressBar.GONE);
                                login_btnLogin.setVisibility(View.VISIBLE);
                            }
                        });
            }
            else{
                Toast.makeText(getApplicationContext(), R.string.notifiLoginFailure, Toast.LENGTH_SHORT).show();
                login_btnLogin.setVisibility(ProgressBar.GONE);
                login_btnLogin.setVisibility(View.VISIBLE);
            }
        });

        signup_nextview.setOnClickListener(view->{
            if(validateSignup()){
                Intent nextSignup = new Intent(getApplicationContext(), SignupChooseImgActivity.class);
                Bundle newUser = new Bundle();
                newUser.putString("username", signup_username.getText().toString());
                newUser.putString("email", signup_email.getText().toString());
                newUser.putString("password", signup_password.getText().toString());
                newUser.putString("phoneNumber", signup_phoneNumber.getText().toString());
                nextSignup.putExtra("newUser", newUser);
                startActivity(nextSignup);
            }
            else{
                Toast.makeText(this, R.string.notifiLoginFailure, Toast.LENGTH_SHORT).show();
            }
        });

        login_viewSignup.setOnClickListener(viewSignup->{
            layout_signup.setVisibility(View.VISIBLE);
            signup_nextview.setVisibility(View.VISIBLE);
            layout_login.setVisibility(View.GONE);
        });

        signup_btnViewLogin.setOnClickListener(viewLogin->{
            layout_signup.setVisibility(View.GONE);
            signup_nextview.setVisibility(View.GONE);
            layout_login.setVisibility(View.VISIBLE);
        });
    }

    public boolean validateLogin(){
        String email = login_editEmail.getText().toString();
        String password = login_editPassword.getText().toString();
        if(email.isEmpty()){
            showNotifi(R.string.notifiEmptyEmail);
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showNotifi(R.string.notifiValiEmail);
            return false;
        }
        else if(password.isEmpty()){
            showNotifi(R.string.notifiEmptyPassword);
            return false;
        }
        else if(password.length() < 8){
            showNotifi(R.string.notifiValiPassword);
            return false;
        }
        return true;
    }

    public boolean validateSignup(){
        String username = signup_username.getText().toString();
        String email = signup_email.getText().toString();
        String password = signup_password.getText().toString();
        String confirmPassword = signup_confirmPassword.getText().toString();
        String phoneNumber = signup_phoneNumber.getText().toString();
        if(username.isEmpty()){
            showNotifi(R.string.notifiUsernameEmpty);
            return false;
        }
        else if(email.isEmpty()){
            showNotifi(R.string.notifiEmptyEmail);
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showNotifi(R.string.notifiValiEmail);
            return false;
        }
        else if(password.isEmpty()) {
            showNotifi(R.string.notifiEmptyPassword);
            return false;
        }
        else if(password.length() < 8){
            showNotifi(R.string.notifiValiPassword);
            return false;
        }
        else if(!confirmPassword.equals(password)){
            showNotifi(R.string.notifiConfirmPassword);
        }
        else if(phoneNumber.length() < 10){
            showNotifi(R.string.notifiPhoneValid);
        }
        return true;
    }

    void showNotifi(int message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}