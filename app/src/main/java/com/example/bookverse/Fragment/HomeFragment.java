package com.example.bookverse.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookverse.API.ApiService;
import com.example.bookverse.AdapterCustom.HomeAdapterRecycle;
import com.example.bookverse.models.ApiClient;
import com.example.bookverse.models.Book;
import com.example.bookverse.models.ListOfBook;
import com.example.bookverse.R;
import com.example.bookverse.activities.SettingAppActivity;
import com.example.bookverse.activities.ViewResultActivity;
import com.example.bookverse.activities.ViewRecentBookActivity;
import com.example.bookverse.databinding.ActivityMainBinding;
import com.example.bookverse.models.Person;
import com.example.bookverse.repository.BookRepository;
import com.example.bookverse.utilities.Constants;
import com.example.bookverse.utilities.PreferenceManager;
import com.example.bookverse.viewmodels.HomeViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

    HomeViewModel homeViewModel;
    BookRepository bookRepository;
    ActivityMainBinding bindingMain;
    NestedScrollView home_fragment;
    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;
    LinearLayout home_favorite;
    ImageButton btnSettings, home_recent;
    //FragmentHomeBinding binding;
    RecyclerView recyclerViewAllBook, recyclerRecentView, recyclerMostPopular;
    ArrayList<Book> listAllBook, listRecentBook, listMostPopular;
    HomeAdapterRecycle adapterAllBook, adapterRecent, adapterViral;
    ListOfBook resultApi;

    TextView btnViewAllBook, frgHome_viewRecent, frgHome_viewViral, tvTime;
    private String nextPageUrl = null;
    FirebaseFirestore firebaseFirestore;
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

        bookRepository = new BookRepository(getContext());
        homeViewModel = new HomeViewModel(getContext(), bookRepository);
        home_fragment = view.findViewById(R.id.home_fragment);
        bottomNavigationView = bindingMain.bottomNavigation;
        frameLayout = bindingMain.fragmentContainer;
        bottomNavigationView.setVisibility(View.GONE);
        home_favorite = view.findViewById(R.id.home_favorite);
        home_recent = view.findViewById(R.id.home_recent);
        btnSettings = view.findViewById(R.id.home_settings);
        btnViewAllBook = view.findViewById(R.id.btnViewAllBook);
        frgHome_viewRecent = view.findViewById(R.id.frgHome_viewRecent);
        frgHome_viewViral = view.findViewById(R.id.frgHome_viewViral);
        preferenceManager = new PreferenceManager(getContext());
        tvTime = view.findViewById(R.id.tvTime);

        firebaseFirestore = FirebaseFirestore.getInstance();

        homeViewModel.loadMessageHome();
        homeViewModel.getMessageHome().observe(getViewLifecycleOwner(), messageHome -> {
            tvTime.setText(messageHome);
        });
        recyclerViewAllBook = view.findViewById(R.id.recyclerAllBook);
        recyclerRecentView = view.findViewById(R.id.recyclerRecentView);
        recyclerViewAllBook.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        listAllBook = new ArrayList<>();
        homeViewModel.getAllBook();
        homeViewModel.getListAllBook().observe(getViewLifecycleOwner(), allBook -> {
            if (allBook != null) {
                listAllBook.addAll(allBook);
                adapterAllBook = new HomeAdapterRecycle(requireContext(), listAllBook);
                recyclerViewAllBook.setAdapter(adapterAllBook);
            }
        });

        recyclerRecentView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        listRecentBook = new ArrayList<>();
        homeViewModel.getRecentBook();
        homeViewModel.getListRecentBook().observe(getViewLifecycleOwner(), recentBook -> {
            if (recentBook != null) {
                listRecentBook.addAll(recentBook);
                adapterRecent = new HomeAdapterRecycle(requireContext(), listRecentBook);
                recyclerRecentView.setAdapter(adapterRecent);
            }
        });

        recyclerMostPopular = view.findViewById(R.id.recyclerMostPopular);
        recyclerMostPopular.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        listMostPopular = new ArrayList<>();
        homeViewModel.getViralBook();
        homeViewModel.getListViralBook().observe(getViewLifecycleOwner(), viralBook -> {
            if (viralBook != null) {
                listMostPopular.addAll(viralBook);
                adapterViral = new HomeAdapterRecycle(requireContext(), listMostPopular);
                recyclerMostPopular.setAdapter(adapterViral);
            }
        });

        btnViewAllBook.setOnClickListener(view1 -> {
            Intent viewAllBook = new Intent(getActivity(), ViewResultActivity.class);
            viewAllBook.putExtra("keyView", "ViewAllBook");
            startActivity(viewAllBook);
        });

        frgHome_viewViral.setOnClickListener(frgHome_viewViral -> {
            Intent viewViral = new Intent(getActivity(), ViewResultActivity.class);
            viewViral.putExtra("keyView", "ViewViral");
            startActivity(viewViral);
        });

        frgHome_viewRecent.setOnClickListener(frgHome_viewRecent -> {
           Intent viewRecent = new Intent(getActivity(), ViewResultActivity.class);
           viewRecent.putExtra("keyView", "ViewRecent");
           startActivity(viewRecent);
        });

        home_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                Intent viewFavorite = new Intent(requireContext(), ViewResultActivity.class);
                viewFavorite.putExtra("keyView", "ViewFavorite");
                startActivity(viewFavorite);
            }
        });

        btnSettings.setOnClickListener(view2 -> {
            Intent viewSetting = new Intent(getActivity(), SettingAppActivity.class);
            startActivity(viewSetting);
        });

        home_recent.setOnClickListener(viewRecent ->{
            Intent viewRecentBook = new Intent(getActivity(), ViewRecentBookActivity.class);
            viewRecentBook.putExtra("keyView", "ViewRecent");
            startActivity(viewRecentBook);
        });

        home_fragment.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) frameLayout.getLayoutParams();
        });
    }

//    public void writeToFirebase(){
//        ApiService apiService = ApiClient.getApiService();
//        Call<ListOfBook> listOfBookCall = nextPageUrl == null ? apiService.getListBook(null, null): apiService.getListBookByUrl(nextPageUrl);
//        listOfBookCall.enqueue(new Callback<ListOfBook>() {
//            @Override
//            public void onResponse(Call<ListOfBook> call, Response<ListOfBook> response) {
//                Toast.makeText(requireContext(), "Call Api success", Toast.LENGTH_SHORT).show();
//                if (response.body() != null){
//                    Log.d("API Response", response.body().toString());
//                    resultApi = response.body();
//                    ArrayList<Book>currentListBook = resultApi.getResults();
//                    listAllBook.addAll(currentListBook);
//                    for(int i = 0; i < currentListBook.size(); i++){
//                        Book book = currentListBook.get(i);
//                        Map<String, String> formats = new HashMap<>();
//                        ArrayList<Person> authors = book.getAuthors();
//                        StringBuilder authorList = new StringBuilder();
//                        for(int index = 0; index < authors.size(); index++){
//                            authorList.append(authors.get(index).getName());
//                            if (index < authors.size()-1) authorList.append(", ");
//                        }
//                        formats.put(Constants.KEY_ID, String.valueOf(book.getId()));
//                        formats.put(Constants.KEY_BOOK_AUTHOR, String.valueOf(authorList));
//                        formats.put(Constants.KEY_BOOK_BOOKSHELVES, Arrays.asList(book.getBookshelves()).toString());
//                        formats.put(Constants.KEY_BOOK_COPPYRIGHT, String.valueOf(book.isCopyright()));
//                        formats.put(Constants.KEY_BOOK_DOWNLOAD_COUNT, String.valueOf(book.getDownload_count()));
//                        formats.put(Constants.KEY_BOOK_FORMATS, String.valueOf(book.getFormats()));
//                        formats.put(Constants.KEY_BOOK_LANGUAGES, Arrays.toString(book.getLanguages()));
//                        formats.put(Constants.KEY_BOOK_MEDIA_TYPE, String.valueOf(book.getMedia_type()));
//                        formats.put(Constants.KEY_BOOK_SUBJECTS, Arrays.toString(book.getSubjects()));
//                        formats.put(Constants.KEY_BOOK_TITLE, String.valueOf(book.getTitle()));
//                        formats.put(Constants.KEY_BOOK_SUMMARIES, String.valueOf(book.getSummaries()));
//
//                        StringBuilder translatorList = new StringBuilder();
//                        for(int index = 0; index < authors.size(); index++){
//                            translatorList.append(authors.get(index).getName());
//                            if (index < authors.size()-1) translatorList.append(", ");
//                        }
//                        formats.put(Constants.KEY_BOOK_TRANSLATORS, String.valueOf(translatorList));
//
//                        Gson gson = new Gson();
//                        // Chuyển book object thành map
//                        Map<String, Object> bookMap = gson.fromJson(gson.toJson(book), new TypeToken<Map<String, Object>>(){}.getType());
//                        firebaseFirestore.collection(Constants.KEY_COLLECTION_BOOKS)
//                                .document(String.valueOf(book.getId()))
//                                .set(bookMap);
//                    }
////                Toast.makeText(getContext(), "Size: " + Integer.toString(listAllBook.size()), Toast.LENGTH_SHORT).show();
//                    nextPageUrl = resultApi.getNext();
//                    if(nextPageUrl != null){
//                        writeToFirebase();
//                    }
//                } else {
//                    Toast.makeText(requireContext(), "Call Api failure", Toast.LENGTH_SHORT).show();
//                }
//                Log.e("API_RESPONSE", "Response is null or unsuccessful: " + response.code());
//            }
//
//            @Override
//            public void onFailure(Call<ListOfBook> call, Throwable t) {
//                Toast.makeText(requireContext(), "Call Api failure", Toast.LENGTH_SHORT).show();
//                if (t instanceof IOException){
//                    Log.e("API","Network failure: " + t.getMessage());
//                }
//                else{
//                    Log.e("API", "Conversion error: "+ t.getMessage());
//                }
//            }
//        });
//    }
}