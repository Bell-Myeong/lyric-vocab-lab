package com.example.project;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.net.URLEncoder;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WordLearningActivity extends AppCompatActivity {

    private ListView wordListView;
    private List<String> wordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_learning);

        wordListView = findViewById(R.id.wordListView);

        wordList = getIntent().getStringArrayListExtra("wordList");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                wordList
        );

        wordListView.setAdapter(adapter);

        wordListView.setOnItemClickListener((parent, view, position, id) -> {
            String clickedWord = wordList.get(position);

            String surface = clickedWord;
            if (clickedWord.contains("（")) {
                surface = clickedWord.substring(0, clickedWord.indexOf("（"));
            }

            fetchWordMeaning(surface);
        });
    }

    private void fetchWordMeaning(String word) {
        new Thread(() -> {
            try {
                String url = "https://jisho.org/api/v1/search/words?keyword=" + URLEncoder.encode(word, "UTF-8");
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
                    JsonArray dataArray = jsonObject.getAsJsonArray("data");

                    if (dataArray.size() > 0) {
                        JsonObject firstEntry = dataArray.get(0).getAsJsonObject();
                        JsonArray senses = firstEntry.getAsJsonArray("senses");
                        JsonArray englishDefs = senses.get(0).getAsJsonObject().getAsJsonArray("english_definitions");

                        StringBuilder meaningBuilder = new StringBuilder();
                        for (int i = 0; i < englishDefs.size(); i++) {
                            meaningBuilder.append(englishDefs.get(i).getAsString());
                            if (i < englishDefs.size() - 1) {
                                meaningBuilder.append(", ");
                            }
                        }

                        String meaning = meaningBuilder.toString();
                        Log.d("JishoAPI", word + " 뜻: " + meaning);


                        runOnUiThread(() -> {
                            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
                            bottomSheetDialog.setContentView(R.layout.dialog_bottom_sheet);

                            TextView textWord = bottomSheetDialog.findViewById(R.id.textWord);
                            TextView textMeaning = bottomSheetDialog.findViewById(R.id.textMeaning);

                            if (textWord != null) textWord.setText(word);
                            if (textMeaning != null) textMeaning.setText(meaning);

                            bottomSheetDialog.show();
                        });

                    } else {
                        runOnUiThread(() -> {
                            new AlertDialog.Builder(this)
                                    .setTitle(word + " 뜻")
                                    .setMessage("뜻을 찾을 수 없습니다.")
                                    .setPositiveButton("확인", null)
                                    .show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        new AlertDialog.Builder(this)
                                .setTitle("API 요청 실패")
                                .setMessage("서버에서 응답을 받지 못했습니다.")
                                .setPositiveButton("확인", null)
                                .show();
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    new AlertDialog.Builder(this)
                            .setTitle("오류 발생")
                            .setMessage(e.getMessage())
                            .setPositiveButton("확인", null)
                            .show();
                });
            }
        }).start();
    }
}