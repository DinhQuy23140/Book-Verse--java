package com.example.bookverse.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

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
    EditText login_editEmail, login_editPassword;
    Button login_btnLogin;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    PreferenceManager preferenceManager;
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
        login_btnLogin = findViewById(R.id.log_btnLogin);
        login_btnLogin.setOnClickListener(view->{
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
                            finish();
                        }
                    });
        });
    }
}