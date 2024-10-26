package com.example.bookverse.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bookverse.AdapterCustom.SearchRecyclerAdapter;
import com.example.bookverse.R;
import com.example.bookverse.activities.InfBokkActivity;
import com.example.bookverse.activities.SearchActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final ArrayList<String> subjects = new ArrayList<>(Arrays.asList(
            "Fiction",
            "Science Fiction",
            "History",
            "Biography",
            "Adventure",
            "Philosophy",
            "Poetry",
            "Mystery",
            "Children's literature",
            "Religion"
    ));

    private static final int[] pathList = {R.drawable.background_search, R.drawable.background_purple200,
            R.drawable.background_search500, R.drawable.background_search_lavent, R.drawable.background_search_teal200, R.drawable.background_search_teal700,
    };
    private static final ArrayList<Integer> listrandomBackgroundSearch = new ArrayList<>();


    ConstraintLayout searchViewSearch;
    RecyclerView searchRecyclerSub;
    SearchRecyclerAdapter recyclerAdapter;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchRecyclerSub = view.findViewById(R.id.searchRecyclerSub);
        searchRecyclerSub.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        Random randomIndex = new Random();
        for (int i = 0; i < subjects.size(); i++){
            int index = randomIndex.nextInt(pathList.length);
            listrandomBackgroundSearch.add(pathList[index]);
        }
        recyclerAdapter = new SearchRecyclerAdapter(requireContext(), listrandomBackgroundSearch, subjects);
        searchRecyclerSub.setAdapter(recyclerAdapter);
        searchRecyclerSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), InfBokkActivity.class);
                startActivity(intent);
            }
        });
        searchViewSearch = view.findViewById(R.id.searchViewSearch);
        searchViewSearch.setOnClickListener(viewSearch ->{
            Log.d("SearchClick", "Search button clicked");
            Intent searchint = new Intent(requireContext(), SearchActivity.class);
            startActivity(searchint);
        });
    }
}