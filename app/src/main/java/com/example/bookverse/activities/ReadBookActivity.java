package com.example.bookverse.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookverse.models.Book;
import com.example.bookverse.R;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class ReadBookActivity extends AppCompatActivity {
    TextView contentBook;
    WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_read_book);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //contentBook = findViewById(R.id.contentBook);
        webview= findViewById(R.id.webview);
        Intent intent = getIntent();
        String jsonStr = intent.getStringExtra("jsonBook");
        Gson gson = new Gson();
        Book book = gson.fromJson(jsonStr, Book.class);
        if (book != null) {
            String url = getUrlImg(book.getFormats());
            String result = readTextFromUrl(url);
            //contentBook.setText(result);
            webview.loadUrl(url);

//            new DownloadTextTask(contentBook).execute(url);
            Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
        }
    }

    public String getUrlImg(Map<String, String> format){
        return format.get("text/html");
    }

    public String readTextFromUrl(String urlString) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Kiểm tra phản hồi từ server
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                reader.close();
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private class DownloadTextTask extends AsyncTask<String, Void, String> {
        private TextView textView;

        public DownloadTextTask(TextView textView) {
            this.textView = textView;
        }

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                result = stringBuilder.toString();
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // Hiển thị nội dung tải về lên TextView
            textView.setText(result);
        }
    }
}