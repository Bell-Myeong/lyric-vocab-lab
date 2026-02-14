package com.example.project;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import android.util.Log;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class LyricsAnalysisActivity extends AppCompatActivity {

    private TextView textSongTitle, textSongArtist, textLyrics;
    private Button btnLearnWords;
    private List<String> extractedWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyrics_analysis);

        textSongTitle = findViewById(R.id.textSongTitle);
        textSongArtist = findViewById(R.id.textSongArtist);
        textLyrics = findViewById(R.id.textLyrics);
        btnLearnWords = findViewById(R.id.btnLearnWords);


        String title = getIntent().getStringExtra("title");
        String artist = getIntent().getStringExtra("artist");

        textSongTitle.setText(title != null ? title : "제목 없음");
        textSongArtist.setText(artist != null ? artist : "아티스트 없음");

        if (title != null && artist != null) {
            fetchLyrics(title , artist);
        } else {
            textLyrics.setText("가사를 불러올 수 없습니다.");
        }

        btnLearnWords.setOnClickListener(v -> {
            String lyricsToAnalyze = textLyrics.getText().toString();

            extractedWords = analyzeJapaneseLyrics(lyricsToAnalyze);

            for (String word : extractedWords) {
                Log.d("TestKuromoji", "추출된 단어: " + word);
            }

            Intent intent = new Intent(LyricsAnalysisActivity.this, WordLearningActivity.class);
            intent.putStringArrayListExtra("wordList", new ArrayList<>(extractedWords));
            startActivity(intent);
        });
    }


    private void fetchLyrics(String title, String artist) {
        Toast.makeText(this, "가사 검색 중...", Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            try {
                String encodedTitle = URLEncoder.encode(title, "UTF-8");
                String encodedArtist = URLEncoder.encode(artist, "UTF-8");
                String searchUrl = "https://j-lyric.net/search.php?kt=" + encodedTitle + "&ct=2&ka=" + encodedArtist + "&ca=2&kl=&cl=2";
                Log.d("LyricsDebug", "Search URL: " + searchUrl);

                Document searchDoc = Jsoup.connect(searchUrl)
                        .userAgent("Mozilla/5.0")
                        .get();



                Element firstLink = searchDoc.selectFirst("p.mid > a");

                if (firstLink != null) {
                    Log.d("LyricsDebug", "First link href: " + firstLink.attr("href"));
                } else {
                    Log.d("LyricsDebug", "No result link found.");
                    runOnUiThread(() -> textLyrics.setText("가사를 찾을 수 없습니다."));
                    return;
                }

                String detailUrl = firstLink.attr("href");
                Document detailDoc = Jsoup.connect(detailUrl)
                        .userAgent("Mozilla/5.0")
                        .get();



                Element lyricsDiv = detailDoc.selectFirst("p#Lyric");
                String lyrics;
                if (lyricsDiv != null) {
                    lyrics = lyricsDiv.html().replaceAll("(?i)<br\\s*/?>", "\n");
                    Log.d("LyricsDebug", "가사 추출 성공 (줄바꿈 포함):\n" + lyrics);
                } else {
                    lyrics = "가사를 찾을 수 없습니다.";
                    Log.d("LyricsDebug", "가사 요소 <p id='Lyric'> 찾기 실패");
                }

                runOnUiThread(() -> textLyrics.setText(lyrics));

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> textLyrics.setText("오류가 발생했습니다."));
            }

        }).start();

    }


    private List<String> analyzeJapaneseLyrics(String lyrics) {
        List<String> words = new ArrayList<>();

        if (lyrics == null || lyrics.trim().isEmpty()) {
            return words;
        }

        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenize(lyrics);

        for (Token token : tokens) {
            String surface = token.getSurface();
            String[] features = token.getAllFeaturesArray();

            String pos = features[0];       // 품사
            String baseForm = features[6];  // 원형
            String reading = features[7];   // 읽기

            if (pos.equals("名詞")) {
                String wordEntry = surface + "（" + baseForm + "）[" + reading + "]";
                words.add(wordEntry);
            }
        }
        Set<String> uniqueWords = new LinkedHashSet<>(words);

        return new ArrayList<>(uniqueWords);

    }

}
