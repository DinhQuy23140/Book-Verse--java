package com.example.bookverse.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.bookverse.R;
import com.example.bookverse.activities.InfUserActivity;
import com.example.bookverse.activities.SettingAppActivity;
import com.example.bookverse.activities.VertifiAccountActivity;
import com.example.bookverse.activities.ViewFavoriteBookActivity;
import com.example.bookverse.activities.ViewRecentBookActivity;
import com.example.bookverse.utilities.Constants;
import com.example.bookverse.utilities.PreferenceManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonFragment extends Fragment {

    Button person_view_btn;

    ImageView person_imvAvatar;
    LinearLayout person_infUser, person_favoriteBook, person_recentBook, person_vertifi, person_settingApp;
    PreferenceManager preferenceManager;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PersonFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PersonFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonFragment newInstance(String param1, String param2) {
        PersonFragment fragment = new PersonFragment();
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
        return inflater.inflate(R.layout.fragment_person, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        person_imvAvatar = view.findViewById(R.id.person_imvAvatar);
        person_infUser = view.findViewById(R.id.person_infUser);
        person_favoriteBook = view.findViewById(R.id.person_favoriteBook);
        person_recentBook = view.findViewById(R.id.person_recentBook);
        person_vertifi = view.findViewById(R.id.person_vertifi);
        person_settingApp = view.findViewById(R.id.person_settingApp);

        preferenceManager = new PreferenceManager(requireContext());
        String strImg = preferenceManager.getString(Constants.KEY_IMAGE);
        String email = preferenceManager.getString(Constants.KEY_EMAIL);
        //person_imvAvatar.setImageBitmap(decodeBase64ToImage(strImg));
        if (strImg != null) {
            person_imvAvatar.setImageBitmap(decodeBase64ToImage(strImg));
        }
        else {
            person_imvAvatar.setImageResource(R.drawable.background_default_user);
        }

        //event
        person_infUser.setOnClickListener(viewInfUser -> {
            Intent viewInf = new Intent(requireContext(), InfUserActivity.class);
            viewInf.putExtra(Constants.KEY_EMAIL, email); //pass email to next view
            startActivity(viewInf);
        });
        person_favoriteBook.setOnClickListener(viewFavoriteBook -> {
            Intent viewInf = new Intent(requireContext(), ViewFavoriteBookActivity.class);
            startActivity(viewInf);
        });
        person_recentBook.setOnClickListener(viewRecentBook -> {
            Intent viewInf = new Intent(requireContext(), ViewRecentBookActivity.class);
            startActivity(viewInf);
        });
        person_vertifi.setOnClickListener(viewVertifi -> {
            Intent viewInf = new Intent(requireContext(), VertifiAccountActivity.class);
            startActivity(viewInf);
        });
        person_settingApp.setOnClickListener(viewSettingApp -> {
            Intent viewInf = new Intent(requireContext(), SettingAppActivity.class);
            startActivity(viewInf);
        });
    }

    public Bitmap decodeBase64ToImage(String base64){
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}