package com.example.bookverse.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.bookverse.MainActivity;
import com.example.bookverse.R;
import com.example.bookverse.utilities.Constants;
import com.example.bookverse.utilities.PreferenceManager;
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
    Button infUser_bthLogout;
    String endcodeedImage;
    NestedScrollView layout;
    SharedPreferences sharedPreferences;
    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    PreferenceManager preferenceManager;
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

        layout = findViewById(R.id.main);
        sharedPreferences = getSharedPreferences("MySharePref", MODE_PRIVATE);
        preferenceChangeListener = (sharedPreferences, key)->{
            if(key.equals("pathTheme")){
                int newpathTheme = sharedPreferences.getInt("pathTheme", R.drawable.background_app);
                updateBackground(newpathTheme);
            }
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        int pathTheme = sharedPreferences.getInt("pathTheme", R.drawable.background_app);
        updateBackground(pathTheme);

        firebaseFirestore = FirebaseFirestore.getInstance();
        infUser_avatar = findViewById(R.id.infUser_avatar);
        infUser_emailedt = findViewById(R.id.infUser_emailedt);
        infUser_fullnameEdt = findViewById(R.id.infUser_fullnameEdt);
        infUser_phoneEdt = findViewById(R.id.infUser_phoneEdt);
        infUser_BirthOfDate = findViewById(R.id.infUser_BirthOfDate);
        infUser_save = findViewById(R.id.infUser_save);
        infUser_bthLogout = findViewById(R.id.infUser_bthLogout);
        preferenceManager = new PreferenceManager(getApplicationContext());
        
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

        infUser_bthLogout.setOnClickListener(LogoutTask -> {
            Intent logout = new Intent(this, LoginActivity.class);
            logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logout);
            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, false);
        });

    }

    public void updateBackground(int pathTheme) {
        Glide.with(this)
                .load(pathTheme)  // Load trực tiếp từ pathTheme
                .placeholder(R.drawable.ic_default_image)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        int layoutWidth = layout.getWidth();
                        int layoutHeight = layout.getHeight();

                        // Đảm bảo layout đã được vẽ (kích thước > 0)
                        if (layoutWidth <= 0 || layoutHeight <= 0) {
                            // Layout chưa được vẽ, có thể xử lý lại sau
                            layout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                                @Override
                                public boolean onPreDraw() {
                                    layout.getViewTreeObserver().removeOnPreDrawListener(this);
                                    updateBackground(pathTheme); // Gọi lại phương thức khi layout đã được vẽ
                                    return true;
                                }
                            });
                            return;
                        }

                        Bitmap bitmap = drawableToBitmap(resource);

                        int imageWidth = bitmap.getWidth();
                        int imageHeight = bitmap.getHeight();

                        float scaleX = (float)layoutWidth / imageWidth;
                        float scaleY = (float)layoutHeight / imageHeight;
                        float scale = Math.min(scaleX, scaleY);

                        int newWidth = Math.round(imageWidth * scale);
                        int newHeight = Math.round(imageHeight * scale);

                        Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
                        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), scaleBitmap);

                        // Set background chỉ một lần
                        layout.setBackground(bitmapDrawable);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        layout.setBackgroundResource(R.drawable.ic_error_load_image);
                    }
                });
    }

    private Bitmap drawableToBitmap(Drawable drawable){
        if(drawable instanceof BitmapDrawable){
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
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