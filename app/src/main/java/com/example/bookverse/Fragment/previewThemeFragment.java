package com.example.bookverse.Fragment;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.bookverse.R;
import com.example.bookverse.databinding.ActivityMainBinding;
import com.example.bookverse.databinding.FragmentPreviewThemeBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link previewThemeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class previewThemeFragment extends Fragment {

    ActivityMainBinding mainBinding;
    FrameLayout frameLayout;
    SharedPreferences preferences;
    TextView previewThemeBtn;
    ImageView previewThemeImageView;
    FragmentPreviewThemeBinding binding;
    int idImage;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public previewThemeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment previewThemeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static previewThemeFragment newInstance(String param1, String param2) {
        previewThemeFragment fragment = new previewThemeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPreviewThemeBinding.inflate(inflater, container, false);
        mainBinding = ActivityMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
        //return inflater.inflate(R.layout.fragment_preview_theme, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        previewThemeBtn = binding.previewThemeBtn;
        previewThemeImageView = binding.preViewThemeImageView;
        if(getArguments() != null){
            idImage = getArguments().getInt("pathTheme");
            Glide.with(this)
                    .load(idImage)  // Tải hình ảnh từ resource ID
                    .placeholder(R.drawable.ic_default_image)  // Hiển thị ảnh mặc định khi đang tải
                    .error(R.drawable.ic_error_load_image)  // Hiển thị ảnh lỗi nếu tải không thành công
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            // Sử dụng Drawable trả về từ Glide để đặt vào ImageView
                            previewThemeImageView.setImageDrawable(resource);
                        }
                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // Xử lý khi ảnh không còn được hiển thị (ví dụ khi bị thay đổi)
                            previewThemeImageView.setImageDrawable(placeholder);
                        }
                    });
        }

        previewThemeBtn.setOnClickListener(view1 -> {
            preferences = getActivity().getSharedPreferences("MySharePref", Context.MODE_PRIVATE);
            frameLayout = mainBinding.fragmentContainer;

            // Đặt background trực tiếp ngay lập tức
            frameLayout.setBackgroundResource(idImage);

            // Lưu giá trị vào SharedPreferences
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("pathTheme", idImage);
            editor.apply();

        });



    }
}