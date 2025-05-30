package com.example.bookverse.activities;

import static android.view.View.VISIBLE;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.bookverse.R;
import com.example.bookverse.repository.UserRepository;
import com.example.bookverse.utilities.Constants;
import com.example.bookverse.utilities.PreferenceManager;
import com.example.bookverse.viewmodels.LoginViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.util.Base64;
import android.widget.Toast;


public class SignupChooseImgActivity extends AppCompatActivity {
    ConstraintLayout layout;
    SharedPreferences sharedPreferences;
    PreferenceManager preferenceManager;
    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    FirebaseFirestore firebaseFirestore;
    String endcodeedImage;
    TextView signup_chooseImage;
    ImageView signup_avatar;
    LinearLayout layout_chooseImg;
    Button signup_btnSignup;
    ProgressBar log_prbLoadin;
    private LoginViewModel loginViewModel;
    UserRepository userRepository;
    private final ActivityResultLauncher<Intent>activityResultLauncher =registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        if (bitmap != null) {
                            signup_avatar.setImageBitmap(bitmap);
                        } else {
                            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background_default_user);
                            signup_avatar.setImageResource(R.drawable.background_default_user);
                        }
                        endcodeedImage = enCodeImage(bitmap);
                        loginViewModel.selectImage(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
    
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

        userRepository = new UserRepository(this);
        loginViewModel = new LoginViewModel(userRepository, this);

        layout = findViewById(R.id.main);
        log_prbLoadin = findViewById(R.id.log_prbLoadin);
        preferenceManager = new PreferenceManager(this);
        sharedPreferences = getSharedPreferences("MySharePref", MODE_PRIVATE);
        preferenceChangeListener = (sharedPreferences, key)->{
            if(key.equals("pathTheme")){
                int newpathTheme = sharedPreferences.getInt("pathTheme", R.drawable.background_app);
                updateBackground(newpathTheme);
            }
        };

        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        int pathTheme = sharedPreferences.getInt("pathTheme", R.drawable.background_app);
        updateBackground(pathTheme);

        firebaseFirestore = FirebaseFirestore.getInstance();
        layout_chooseImg = findViewById(R.id.layout_chooseImg);
        signup_chooseImage = findViewById(R.id.signup_chooseImage);
        signup_avatar = findViewById(R.id.signup_avatar);
        Intent getInfUser = getIntent();
        Bundle infUser = getInfUser.getBundleExtra(Constants.KEY_INF_USER);
        String username = infUser.getString(Constants.KEY_NAME);
        String email = infUser.getString(Constants.KEY_EMAIL);
        String password = infUser.getString(Constants.KEY_PASSWORD);
        String phoneNumber = infUser.getString(Constants.KEY_PHONE);

        layout_chooseImg.setOnClickListener(view->{
            Intent chooseImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            chooseImage.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activityResultLauncher.launch(chooseImage);
        });

        loginViewModel.getImageBitmap().observe(this, bitmap -> {
            if (bitmap != null) {
                signup_avatar.setImageBitmap(bitmap);
            } else {
                signup_avatar.setImageResource(R.drawable.background_default_user);
            }
        });

        signup_btnSignup = findViewById(R.id.signup_btnSignup);
        signup_btnSignup.setOnClickListener(view->{
            Map<String, String> newUser = new HashMap<>();
            newUser.put(Constants.KEY_NAME, username);
            newUser.put(Constants.KEY_EMAIL, email);
            newUser.put(Constants.KEY_PASSWORD, password);
            newUser.put(Constants.KEY_PHONE, phoneNumber);
            newUser.put(Constants.KEY_IMAGE, endcodeedImage);
            loginViewModel.signupTest(newUser);
            loginViewModel.getIsSignup().observe(this, signupSuccess -> {
                if (signupSuccess) {
                    Intent intent = new Intent(SignupChooseImgActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
            loginViewModel.getMessageSignup().observe(this, message ->{
                Toast.makeText(SignupChooseImgActivity.this, message, Toast.LENGTH_SHORT).show();
            });
        });
    }

    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        launcher.launch(intent);
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

                        Bitmap bitmap = drawableToBitmap(resource);

                        int imageWidth = bitmap.getWidth();
                        int imageHeight = bitmap.getHeight();

                        float scaleX = (float)layoutWidth/imageWidth;
                        float scaleY = (float)layoutHeight/imageHeight;
                        float scale = Math.min(scaleX, scaleY);

                        int newWidth = Math.round(imageWidth * scale);
                        int newHeight = Math.round(imageHeight * scale);

                        Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
                        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), scaleBitmap);
                        layout.setBackground(bitmapDrawable);  // Sử dụng Drawable từ Glide
                        layout.setBackground(resource);  // Sử dụng Drawable từ Glide
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

    public Bitmap getBitmapFromResource(Context context, int id){
        return BitmapFactory.decodeResource(context.getResources(), id);
    }
}