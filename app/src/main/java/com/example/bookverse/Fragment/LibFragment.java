package com.example.bookverse.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.bookverse.AdapterCustom.HistoAdapter;
import com.example.bookverse.models.Book;
import com.example.bookverse.R;
import com.example.bookverse.activities.SearchActivity;
import com.example.bookverse.activities.ViewFavoriteBookActivity;
import com.example.bookverse.repository.BookRepository;
import com.example.bookverse.utilities.Constants;
import com.example.bookverse.utilities.PreferenceManager;
import com.example.bookverse.viewmodels.HomeViewModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LibFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LibFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    FirebaseFirestore firebaseFirestore;
    ImageView lib_imvAvata, lib_viewSearchFrgavorite;
    RecyclerView lib_rvRecent;
    LinearLayout lib_viewFavorite;
    PreferenceManager preferenceManager;
    ArrayList<Book> listBook;
    HistoAdapter adapter;
    BookRepository bookRepository;
    HomeViewModel homeViewModel;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LibFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LibFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LibFragment newInstance(String param1, String param2) {
        LibFragment fragment = new LibFragment();
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
        return inflater.inflate(R.layout.fragment_lib, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookRepository = new BookRepository(requireContext());
        homeViewModel = new HomeViewModel(requireContext(), bookRepository);
        preferenceManager = new PreferenceManager(requireContext());
        firebaseFirestore = FirebaseFirestore.getInstance();
        lib_imvAvata = view.findViewById(R.id.lib_imvAvata);
        lib_viewSearchFrgavorite = view.findViewById(R.id.lib_viewSearchFrg);
        lib_viewFavorite = view.findViewById(R.id.lib_viewFavorite);
        lib_rvRecent = view.findViewById(R.id.lib_rvRecent);
        lib_rvRecent.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        listBook = new ArrayList<>();
        adapter = new HistoAdapter(getContext(), listBook);
        getRecentBook();
        lib_rvRecent.setAdapter(adapter);
        String strImg = preferenceManager.getString(Constants.KEY_IMAGE);
        if (strImg != null) {
            lib_imvAvata.setImageBitmap(decodeBase64ToImage(strImg));
        }
        else {
            lib_imvAvata.setImageResource(R.drawable.background_default_user);
        }

        lib_viewSearchFrgavorite.setOnClickListener(lib_viewSearchFrgavorite -> {
            Intent viewSearchAv = new Intent(requireContext(), SearchActivity.class);
            startActivity(viewSearchAv);
        });

        lib_viewFavorite.setOnClickListener(lib_viewFavorite -> {
           Intent viewFavorite = new Intent(requireContext(), ViewFavoriteBookActivity.class);
           startActivity(viewFavorite);
        });


    }

    @SuppressLint("NotifyDataSetChanged")
    public void getRecentBook(){
//        firebaseFirestore.collection(Constants.KEY_COLLECTION_RECENTREAD)
//                .document(preferenceManager.getString(Constants.KEY_EMAIL))
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null) {
//                        List<Object> BookId = (List<Object>) task.getResult().get("BookId");
//                        if (BookId != null && !BookId.isEmpty()) {
//                            // Chuyển đổi tất cả các giá trị trong BookId thành String
//                            List<String> stringBookId = new ArrayList<>();
//                            for (Object id : BookId) {
//                                if (id instanceof Long) {
//                                    // Nếu ID là Long, chuyển thành String
//                                    stringBookId.add(String.valueOf(id));
//                                } else if (id instanceof String) {
//                                    stringBookId.add((String) id);
//                                }
//                            }
//
//                            // Tiến hành truy vấn với danh sách BookId dạng String
//                            firebaseFirestore.collection(Constants.KEY_COLLECTION_BOOKS)
//                                    .whereIn(FieldPath.documentId(), stringBookId)
//                                    .get()
//                                    .addOnCompleteListener(bookTask -> {
//                                        if (bookTask.isSuccessful() && bookTask.getResult() != null) {
//                                            for (DocumentSnapshot documentSnapshot : bookTask.getResult()) {
//                                                Gson gson = new Gson();
//                                                Map<String, Object> data = documentSnapshot.getData();
//                                                if (data != null) {
//                                                    Book book = gson.fromJson(gson.toJson(data), Book.class);
//                                                    listBook.add(book);
//                                                    adapter.notifyItemInserted(listBook.size() - 1); // Cập nhật item mới
//                                                }
//                                            }
//                                            // Thông báo về số lượng sách đã được tải
//                                            Toast.makeText(getContext(), "Size: " + listBook.size(), Toast.LENGTH_SHORT).show();
//                                        } else {
//                                            Log.e("Firestore", "Error fetching books", bookTask.getException());
//                                        }
//                                    });
//                        } else {
//                            Log.w("Firestore", "No BookId found in the document");
//                        }
//                    } else {
//                        Log.e("Firestore", "Error fetching recent read document", task.getException());
//                    }
//                });
        homeViewModel.getRecentBook();
        homeViewModel.getListRecentBook().observe(getViewLifecycleOwner(), result -> {
            listBook = (ArrayList<Book>) result;
            adapter = new HistoAdapter(getContext(), listBook);
            lib_rvRecent.setAdapter(adapter);
        });
    }

    public Bitmap decodeBase64ToImage(String base64){
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}