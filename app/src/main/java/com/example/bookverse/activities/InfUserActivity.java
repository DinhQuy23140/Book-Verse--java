package com.example.bookverse.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookverse.R;
import com.example.bookverse.utilities.Constants;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;

public class InfUserActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    ImageView infUser_avatar;
    TextView infUser_emailedt;
    EditText infUser_fullnameEdt, infUser_phoneEdt;
    ImageButton infUser_save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inf_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseFirestore = FirebaseFirestore.getInstance();
        infUser_avatar = findViewById(R.id.infUser_avatar);
        infUser_emailedt = findViewById(R.id.infUser_emailedt);
        infUser_fullnameEdt = findViewById(R.id.infUser_fullnameEdt);
        infUser_phoneEdt = findViewById(R.id.infUser_phoneEdt);
        infUser_save = findViewById(R.id.infUser_save);
        
        Intent getInfUser = getIntent();
        String email = getInfUser.getStringExtra(Constants.KEY_EMAIL);
        firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, email)
                .get()
                .addOnCompleteListener(task ->{
                    if(task.isSuccessful() && !task.getResult().isEmpty()){
                        String strImg = task.getResult().getDocuments().get(0).getString(Constants.KEY_IMAGE);
                        String fullname = task.getResult().getDocuments().get(0).getString(Constants.KEY_NAME);
                        String phone = task.getResult().getDocuments().get(0).getString(Constants.KEY_PHONE);
                        infUser_avatar.setImageBitmap(decodeBase64ToImage(strImg));
                        infUser_fullnameEdt.setText(fullname);
                        infUser_emailedt.setText(email);
                        infUser_phoneEdt.setText(phone);
                    }
                });
        infUser_save.setOnClickListener(view -> {

        });
    }

    public Bitmap decodeBase64ToImage(String base64){
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}