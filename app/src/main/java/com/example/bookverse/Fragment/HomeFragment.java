package com.example.bookverse.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookverse.API.ApiService;
import com.example.bookverse.AdapterCustom.HomeAdapterRecycle;
import com.example.bookverse.Class.ApiClient;
import com.example.bookverse.Class.Book;
import com.example.bookverse.Class.Format;
import com.example.bookverse.Class.ListOfBook;
import com.example.bookverse.R;
import com.example.bookverse.activities.ViewAllRecyclerView;
import com.example.bookverse.databinding.FragmentHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    BottomNavigationView bottomNavigationView;
    LinearLayout home_favorite;
    ImageButton btnSettings;
    FragmentHomeBinding binding;
    RecyclerView recyclerViewAllBook;
    ArrayList<Book> listAllBook;
    HomeAdapterRecycle adapterAllBook;
    ListOfBook resultApi;

    TextView btnViewAllBook, tvTime;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        //return inflater.inflate(R.layout.fragment_home, container, false);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setMessage(R.string.title_dialogBackPress)
//                        .setPositiveButton(R.string.dialogBackPressOk, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                // Thoát ứng dụng
//                                getActivity().finish();  // Đóng Activity hiện tại
//                                System.exit(0);  // Thoát ứng dụng
//                            }
//                        })
//                        .setNegativeButton(R.string.dialogBackPressCancel, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                // Đóng dialog
//                                dialogInterface.dismiss(); // Đóng dialog
//                            }
//                        });
//                builder.show();
//
//            }
//        };
//        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
//    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        home_favorite = binding.homeFavorite;
        btnSettings = binding.homeSettings;
        btnViewAllBook = binding.btnViewAllBook;
        tvTime = binding.tvTime;

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        //Toast.makeText(requireContext(), Integer.toString(hour), Toast.LENGTH_SHORT).show();
        if(hour >= 7 && hour <12) {
            tvTime.setText(R.string.setTimeMorining);
        }
        else if(hour >=12 && hour < 18) {
            tvTime.setText(R.string.setTimeAfternoon);
        }
        else if(hour>= 18 && hour < 21) {
            tvTime.setText(R.string.setTimeEvening);
        }
        else {
            tvTime.setText(R.string.setTimeNight);
        }

        recyclerViewAllBook = binding.recyclerAllBook;
        recyclerViewAllBook.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        listAllBook = new ArrayList<Book>();
        adapterAllBook = new HomeAdapterRecycle(requireContext(), listAllBook);
        getListBook();
        recyclerViewAllBook.setAdapter(adapterAllBook);

        btnViewAllBook.setOnClickListener(view1 -> {
            Intent viewAllBook = new Intent(getActivity(), ViewAllRecyclerView.class);
            viewAllBook.putExtra("key", "allBook");
            startActivity(viewAllBook);

        });
        home_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                home_favorite.setScaleX(0.9F);
//                home_favorite.setScaleY(0.9F);
//
//                //set lai kich thuoc sau khi click
//                home_favorite.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        home_favorite.setScaleX(1.0F);
//                        home_favorite.setScaleY(1.0F);
//                    }
//                }, 150);

                //tao doi tuong amination co ten phan tu, hieu ung, kich thuoc sau khi ap dung hieu ung
                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(home_favorite, "scaleX", 0.9F);
                ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(home_favorite, "scaleY", 0.9F);

                //set thoi gian scale
                scaleDownX.setDuration(200);
                scaleDownY.setDuration(200);

                //chay doi tuong animator
                scaleDownX.start();
                scaleDownY.start();

                scaleDownX.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(home_favorite, "scaleX", 1.0F);
                        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(home_favorite, "scaleY", 1.0F);

                        scaleUpX.setDuration(200);
                        scaleUpY.setDuration(200);

                        scaleUpX.setStartDelay(100);
                        scaleUpY.setStartDelay(100);

                        scaleUpX.start();
                        scaleUpY.start();
                    }
                });
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Tạo một instance của SettingsFragment
                SettingsFragment settingsFragment = new SettingsFragment();

                // Thay thế fragment hiện tại trong fragment_container bằng SettingsFragment
                fragmentTransaction.replace(R.id.fragment_container, settingsFragment, "SettingsFragment");

                // Thêm transaction vào backstack để quay lại được fragment trước đó
                fragmentTransaction.addToBackStack(null);

                // Commit transaction
                fragmentTransaction.commit();
            }
        });
    }



    public void getListBook(){
        ApiService apiService = ApiClient.getApiService();
        Call<ListOfBook> listOfBookCall = apiService.getListBook(null, null);
        listOfBookCall.enqueue(new Callback<ListOfBook>() {
            @Override
            public void onResponse(Call<ListOfBook> call, Response<ListOfBook> response) {
                //Toast.makeText(requireContext(), "Call Api success", Toast.LENGTH_SHORT).show();
                if (response.body() != null){
                    Log.d("API Response", response.body().toString());
                    resultApi = response.body();
                    ArrayList<Book>currentListBook = resultApi.getResults();
                    listAllBook.addAll(currentListBook);
                    adapterAllBook.notifyItemRangeChanged(listAllBook.size(), currentListBook.size());
//                Toast.makeText(getContext(), "Size: " + Integer.toString(listAllBook.size()), Toast.LENGTH_SHORT).show();
                    if(resultApi.getNext() != null){
                        getListBook();
                    }
                }
                Log.e("API_RESPONSE", "Response is null or unsuccessful: " + response.code());
            }

            @Override
            public void onFailure(Call<ListOfBook> call, Throwable t) {
                Toast.makeText(requireContext(), "Call Api failure", Toast.LENGTH_SHORT).show();
                if (t instanceof IOException){
                    Log.e("API","Network failure: " + t.getMessage());
                }
                else{
                    Log.e("API", "Conversion error: "+ t.getMessage());
                }
            }
        });
    }

}