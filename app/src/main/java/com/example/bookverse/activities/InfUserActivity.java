package com.example.bookverse.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookverse.R;
import com.example.bookverse.utilities.Constants;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Calendar;

public class InfUserActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    ImageView infUser_avatar;
    TextView infUser_emailedt, infUser_BirthOfDate;
    EditText infUser_fullnameEdt, infUser_phoneEdt;
    ImageButton infUser_save;
    String endcodeedImage;
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
        infUser_BirthOfDate = findViewById(R.id.infUser_BirthOfDate);
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
                        Object phone = task.getResult().getDocuments().get(0).get(Constants.KEY_PHONE);
                        if (strImg != null) {
                            infUser_avatar.setImageBitmap(decodeBase64ToImage(strImg));
                        }
                        else {
                            infUser_avatar.setImageResource(R.drawable.background_default_user);
                        }
                        infUser_fullnameEdt.setText(fullname);
                        infUser_emailedt.setText(email);
                        infUser_phoneEdt.setText(String.valueOf(phone));
                        infUser_BirthOfDate.setText(task.getResult().getDocuments().get(0).getString(Constants.KEY_BIRTH));
                    }
                });

        infUser_avatar.setOnClickListener(selectImg -> {
            Intent img = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            img.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            launcher.launch(img);
        });
        infUser_BirthOfDate.setOnClickListener(selectDate -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            @SuppressLint("DefaultLocale") DatePickerDialog slBirthDate = new DatePickerDialog(
                    InfUserActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        infUser_BirthOfDate.setText(
                                String.format("%d/%d/%d", selectedDay, selectedMonth + 1, selectedYear)
                        );
                    },
                    year,
                    month,
                    dayOfMonth
            );
            slBirthDate.show();

        });

        infUser_save.setOnClickListener(view -> {
            String fullname = infUser_fullnameEdt.getText().toString();
            String phone = infUser_phoneEdt.getText().toString();
            String birth = infUser_BirthOfDate.getText().toString();
            if(fullname.isEmpty() || phone.isEmpty() || birth.isEmpty()){
                Toast.makeText(this, R.string.notifiLoginFailure, Toast.LENGTH_SHORT).show();
            }
            else {
                firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                        .whereEqualTo(Constants.KEY_EMAIL, email)
                        .get()
                        .addOnCompleteListener(task ->{
                            if(task.isSuccessful() && !task.getResult().isEmpty()){
                                firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                                        .document(task.getResult().getDocuments().get(0).getId())
                                        .update(
                                                Constants.KEY_NAME, fullname,
                                                Constants.KEY_PHONE, phone,
                                                Constants.KEY_BIRTH, birth,
                                                Constants.KEY_IMAGE, endcodeedImage
                                        );
                                Toast.makeText(InfUserActivity.this, R.string.notifiUpdateSuccess, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


    }

    public Bitmap decodeBase64ToImage(String base64){
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
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
                                infUser_avatar.setImageBitmap(bitmap);
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