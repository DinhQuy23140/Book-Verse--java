package com.example.bookverse.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;

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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookverse.API.ApiService;
import com.example.bookverse.AdapterCustom.HomeAdapterRecycle;
import com.example.bookverse.Class.ApiClient;
import com.example.bookverse.Class.Book;
import com.example.bookverse.Class.ListOfBook;
import com.example.bookverse.MainActivity;
import com.example.bookverse.R;
import com.example.bookverse.activities.SettingAppActivity;
import com.example.bookverse.activities.ViewAllRecyclerView;
import com.example.bookverse.activities.ViewFavoriteBookActivity;
import com.example.bookverse.activities.ViewRecentBookActivity;
import com.example.bookverse.databinding.ActivityMainBinding;
import com.example.bookverse.utilities.Constants;
import com.example.bookverse.utilities.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
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
    ActivityMainBinding bindingMain;
    ScrollView home_fragment;
    BottomNavigationView bottomNavigationView;
    LinearLayout home_favorite;
    ImageButton btnSettings, home_recent;
    //FragmentHomeBinding binding;
    RecyclerView recyclerViewAllBook, recyclerRecentView, recyclerMostPopular;
    ArrayList<Book> listAllBook, listRecentBook, listMostPopular;
    HomeAdapterRecycle adapterAllBook, adapterRecent, adapterViral;
    ListOfBook resultApi;

    TextView btnViewAllBook, frgHome_viewRecent, frgHome_viewViral, tvTime;
    private String nextPageUrl = null;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    PreferenceManager preferenceManager;

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
        bindingMain = ActivityMainBinding.inflate(getLayoutInflater());
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        home_fragment = view.findViewById(R.id.home_fragment);
        bottomNavigationView = bindingMain.bottomNavigation;
        bottomNavigationView.setVisibility(View.GONE);
        home_favorite = view.findViewById(R.id.home_favorite);
        home_recent = view.findViewById(R.id.home_recent);
        btnSettings = view.findViewById(R.id.home_settings);
        btnViewAllBook = view.findViewById(R.id.btnViewAllBook);
        frgHome_viewRecent = view.findViewById(R.id.frgHome_viewRecent);
        frgHome_viewViral = view.findViewById(R.id.frgHome_viewViral);
        preferenceManager = new PreferenceManager(getContext());
        tvTime = view.findViewById(R.id.tvTime);

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
        recyclerViewAllBook = view.findViewById(R.id.recyclerAllBook);
        recyclerRecentView = view.findViewById(R.id.recyclerRecentView);
        recyclerViewAllBook.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        listAllBook = new ArrayList<>();
        adapterAllBook = new HomeAdapterRecycle(requireContext(), listAllBook);

        getDataFirebase();
        recyclerViewAllBook.setAdapter(adapterAllBook);
        recyclerRecentView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        listRecentBook = new ArrayList<>();
        adapterRecent = new HomeAdapterRecycle(requireContext(), listRecentBook);
        getRecentBook();
        recyclerRecentView.setAdapter(adapterRecent);

        recyclerMostPopular = view.findViewById(R.id.recyclerMostPopular);
        recyclerMostPopular.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        listMostPopular = new ArrayList<>();
        adapterViral = new HomeAdapterRecycle(requireContext(), listMostPopular);
        getViralBook();
        recyclerMostPopular.setAdapter(adapterViral);

        btnViewAllBook.setOnClickListener(view1 -> {
            Intent viewAllBook = new Intent(getActivity(), ViewAllRecyclerView.class);
            viewAllBook.putExtra("keyView", "ViewAllBook");
            startActivity(viewAllBook);
        });

        frgHome_viewViral.setOnClickListener(frgHome_viewViral -> {
            Intent viewViral = new Intent(getActivity(), ViewAllRecyclerView.class);
            viewViral.putExtra("keyView", "ViewViral");
            startActivity(viewViral);
        });

        frgHome_viewRecent.setOnClickListener(frgHome_viewRecent -> {
           Intent viewRecent = new Intent(getActivity(), ViewAllRecyclerView.class);
           viewRecent.putExtra("keyView", "ViewRecent");
           startActivity(viewRecent);
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

                Intent viewFavorite = new Intent(requireContext(), ViewAllRecyclerView.class);
                viewFavorite.putExtra("keyView", "ViewFavorite");
                startActivity(viewFavorite);
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//                // Tạo một instance của SettingsFragment
//                SettingsFragment settingsFragment = new SettingsFragment();
//
//                // Thay thế fragment hiện tại trong fragment_container bằng SettingsFragment
//                fragmentTransaction.replace(R.id.fragment_container, settingsFragment, "SettingsFragment");
//
//                // Thêm transaction vào backstack để quay lại được fragment trước đó
//                fragmentTransaction.addToBackStack(null);
//
//                // Commit transaction
//                fragmentTransaction.commit();
                Intent viewSetting = new Intent(getActivity(), SettingAppActivity.class);
                startActivity(viewSetting);
            }
        });

        home_recent.setOnClickListener(viewRecent ->{
            Intent viewRecentBook = new Intent(getActivity(), ViewRecentBookActivity.class);
            viewRecentBook.putExtra("keyView", "ViewRecent");
            startActivity(viewRecentBook);
        });

        home_fragment.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    ((MainActivity)requireActivity()).hideBottomNavigationView();
                } else {
                    ((MainActivity)requireActivity()).showBottomNavigationView();
                }
            }
        });
    }

    public void getDataFirebase() {
                firebaseFirestore.collection(Constants.KEY_COLLECTION_BOOKS)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() & !task.getResult().isEmpty()) {
                        Gson gson = new Gson();
                        for (DocumentSnapshot documentSnapshot : task.getResult()){
                            Map<String, Object> data = documentSnapshot.getData();
                            Book book = gson.fromJson(gson.toJson(data), Book.class);
                            listAllBook.add(book);
                            adapterAllBook.notifyItemRangeChanged(listAllBook.size(), listAllBook.size());
                        }
//                        Toast.makeText(requireContext(), Integer.toString(listAllBook.size()), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getListBook(){
        ApiService apiService = ApiClient.getApiService();
        Call<ListOfBook> listOfBookCall = nextPageUrl == null ? apiService.getListBook(null, null): apiService.getListBookByUrl(nextPageUrl);
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
                    nextPageUrl = resultApi.getNext();
                    if(nextPageUrl != null){
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

    public void getRecentBook(){
        firebaseFirestore.collection(Constants.KEY_COLLECTION_RECENTREAD)
                .document(preferenceManager.getString(Constants.KEY_EMAIL))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Object> BookId = (List<Object>) task.getResult().get("BookId");
                        if (BookId != null && !BookId.isEmpty()) {
                            // Chuyển đổi tất cả các giá trị trong BookId thành String
                            List<String> stringBookId = new ArrayList<>();
                            for (Object id : BookId) {
                                if (id instanceof Long) {
                                    // Nếu ID là Long, chuyển thành String
                                    stringBookId.add(String.valueOf(id));
                                } else if (id instanceof String) {
                                    stringBookId.add((String) id);
                                }
                            }

                            // Tiến hành truy vấn với danh sách BookId dạng String
                            firebaseFirestore.collection(Constants.KEY_COLLECTION_BOOKS)
                                    .whereIn(FieldPath.documentId(), stringBookId)
                                    .get()
                                    .addOnCompleteListener(bookTask -> {
                                        if (bookTask.isSuccessful() && bookTask.getResult() != null) {
                                            for (DocumentSnapshot documentSnapshot : bookTask.getResult()) {
                                                Gson gson = new Gson();
                                                Map<String, Object> data = documentSnapshot.getData();
                                                if (data != null) {
                                                    Book book = gson.fromJson(gson.toJson(data), Book.class);
                                                    listRecentBook.add(book);
                                                    adapterRecent.notifyItemInserted(listRecentBook.size() - 1); // Cập nhật item mới
                                                }
                                            }
                                            // Thông báo về số lượng sách đã được tải
                                            //Toast.makeText(getContext(), "Size: " + listRecentBook.size(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.e("Firestore", "Error fetching books", bookTask.getException());
                                        }
                                    });
                        } else {
                            Log.w("Firestore", "No BookId found in the document");
                        }
                    } else {
                        Log.e("Firestore", "Error fetching recent read document", task.getException());
                    }
                });


    }

    public void getViralBook(){
        firebaseFirestore.collection(Constants.KEY_COLLECTION_BOOKS)
                .orderBy("download_count")
                .limit(100)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && !task.getResult().isEmpty()){
                        for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                            Gson gson = new Gson();
                            Map<String, Object> data = documentSnapshot.getData();
                            Book book = gson.fromJson(gson.toJson(data), Book.class);
                            listMostPopular.add(book);
                            adapterViral.notifyItemInserted(listMostPopular.size() - 1);
                        }
                    }
                });
    }
}