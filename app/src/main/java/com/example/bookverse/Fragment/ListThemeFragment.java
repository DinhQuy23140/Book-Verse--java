package com.example.bookverse.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.bookverse.AdapterCustom.ThemeAdapter;
import com.example.bookverse.models.Image;
import com.example.bookverse.R;
import com.example.bookverse.databinding.ActivityMainBinding;
import com.example.bookverse.databinding.FragmentListThemeBinding;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListThemeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListThemeFragment extends Fragment {

    DrawerLayout layout;
    ActivityMainBinding mainBinding;
    FragmentListThemeBinding binding;
    ListView lvTheme;
    ArrayList<Image> listTheme;
    ThemeAdapter adapter;
    SharedPreferences preferences;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ListThemeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListThemeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListThemeFragment newInstance(String param1, String param2) {
        ListThemeFragment fragment = new ListThemeFragment();
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
//        return inflater.inflate(R.layout.fragment_list_theme, container, false);
        binding = FragmentListThemeBinding.inflate(inflater, container, false);
        mainBinding = ActivityMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferences = getActivity().getSharedPreferences("MySharePref", Context.MODE_PRIVATE);
        int presentPath = preferences.getInt("pathTheme", 0);
        layout = mainBinding.layoutmain;
        lvTheme = binding.lvTheme;
        int[] listPathImage = {R.drawable.background_app, R.drawable.background_login, R.drawable.favorite, R.drawable.theme_alfomedeiros18926843,
        R.drawable.theme_exel, R.drawable.theme_mati12509859, R.drawable.theme_mdsnmdsnmdsn1831234,
        R.drawable.theme_padrinan19670, R.drawable.theme_pixabay459277};
        listTheme = new ArrayList<Image>();
        for (int i : listPathImage) {
            if (i != presentPath){
                Image image = new Image(i);
                listTheme.add(image);
            }
            else{
                continue;
            }
        }
        adapter = new ThemeAdapter(requireContext(), listTheme);
        lvTheme.setAdapter(adapter);
        lvTheme.setClickable(true);

    }
}