package com.example.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;


import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SongAdapter adapter;
    private ArrayList<Song> sampleSongList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerSongs);
        sampleSongList = new ArrayList<>();

        sampleSongList.add(new Song("さくら", "森山直太朗"));
        sampleSongList.add(new Song("Lemon", "よねづけんし"));

        adapter = new SongAdapter(this, sampleSongList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button btnAddSong = findViewById(R.id.btnAddSong);
        btnAddSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddSongDialog();
            }
        });
    }

    private void showAddSongDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_song, null);
        builder.setView(dialogView);

        EditText editSongTitle = dialogView.findViewById(R.id.editSongTitle);
        EditText editArtist = dialogView.findViewById(R.id.editArtist);

        builder.setTitle("노래 추가")
                .setPositiveButton("추가", (dialog, which) -> {
                    String title = editSongTitle.getText().toString().trim();
                    String artist = editArtist.getText().toString().trim();

                    if (title.isEmpty() || artist.isEmpty()) {
                        Toast.makeText(this, "제목과 아티스트명을 모두 입력하세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Song newSong = new Song(title, artist);
                    sampleSongList.add(newSong);
                    adapter.notifyItemInserted(sampleSongList.size() - 1);

                })
                .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                .show();
    }


}