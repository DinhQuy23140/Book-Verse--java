package com.example.bookverse.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookverse.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.util.Base64;


public class SignupChooseImgActivity extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore;
    String endcodeedImage;
    TextView signup_chooseImage;
    ImageView signup_avatar;
    LinearLayout layout_chooseImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_choose_img);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseFirestore = FirebaseFirestore.getInstance();
        layout_chooseImg = findViewById(R.id.layout_chooseImg);
        signup_chooseImage = findViewById(R.id.signup_chooseImage);
        signup_avatar = findViewById(R.id.signup_avatar);
        Intent getInfUser = getIntent();
        Bundle infUser = getInfUser.getBundleExtra("newUser");
        String username = infUser.getString("username");
        String email = infUser.getString("email");
        String password = infUser.getString("password");
        String phoneNumber = infUser.getString("phoneNumber");

        layout_chooseImg.setOnClickListener(view->{
            Intent chooseImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            chooseImage.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            launcher.launch(chooseImage);
        });


    }

    private String enCodeImage(Bitmap bitmap){
        //set with
        int previewWith = 150;
        //set height
        int previewHeight = bitmap.getHeight() * previewWith / bitmap.getWidth();
        //scale image
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWith, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result->{
                if(result.getResultCode() == RESULT_OK){
                    if(result.getData() != null){
                        Uri imageuri = result.getData().getData();
                        try{
                            InputStream inputStream = getContentResolver().openInputStream(imageuri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            if(bitmap != null){
                                signup_avatar.setImageBitmap(bitmap);
                                endcodeedImage = enCodeImage(bitmap);
                            }
                            else{
                                bitmap = getBitmapFromResource(getApplicationContext(), R.drawable.background_default_user);
                                endcodeedImage = enCodeImage(bitmap);
                            }
                        }
                        catch (FileNotFoundException e){
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
    );
    public Bitmap getBitmapFromResource(Context context, int id){
        return BitmapFactory.decodeResource(context.getResources(), id);
    }
}