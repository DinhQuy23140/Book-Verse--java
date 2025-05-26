package com.example.bookverse.activities;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.bookverse.R;
import com.example.bookverse.utilities.Constants;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
//import com.folioreader.FolioReader;

public class EpubFolioActivity extends AppCompatActivity {

    //private FolioReader folioReader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_epub_folio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String epubPath = getIntent().getStringExtra("epub_path");
        //folioReader = FolioReader.get();
        //folioReader.openBook(epubPath);

        WebView webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true); // nếu cần JS
        webView.getSettings().setDomStorageEnabled(true); // hỗ trợ localStorage nếu cần

        OkHttpClient client = new OkHttpClient();
        String url = getIntent().getStringExtra(Constants.KEY_EPUB_PATH);
        //.makeText(this, url, Toast.LENGTH_SHORT).show();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(), "Lỗi tải nội dung", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String htmlContent = response.body().string();

                    runOnUiThread(() -> {
                        webView.loadDataWithBaseURL(
                                "https://www.gutenberg.org/",
                                htmlContent,
                                "text/html",
                                "UTF-8",
                                null);
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(getApplicationContext(), "Không thể tải nội dung", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}