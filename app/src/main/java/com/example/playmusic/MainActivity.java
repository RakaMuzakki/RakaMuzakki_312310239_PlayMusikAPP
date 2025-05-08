package com.example.playmusic;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SongAdapter.OnSongClickListener {
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private ImageButton btnPlay, btnNext, btnPrevious;
    private TextView currentSongTitle;
    private RecyclerView songRecyclerView;

    private List<Song> songList;
    private int currentSongIndex = 0;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi view
        seekBar = findViewById(R.id.seekBar);
        btnPlay = findViewById(R.id.btnPlay);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
        currentSongTitle = findViewById(R.id.currentSongTitle);
        songRecyclerView = findViewById(R.id.songRecyclerView);

        // Buat daftar lagu
        initializeSongList();

        // Setup RecyclerView
        setupRecyclerView();

        // Inisialisasi media player
        initializeMediaPlayer();

        // Setup kontrol
        setupControls();

        // Setup seek bar
        setupSeekBar();
    }

    private void initializeSongList() {
        songList = new ArrayList<>();
        songList.add(new Song("Nirvana - Dump", R.raw.song1));
        songList.add(new Song("Nirvana - Lithium", R.raw.song2));
        songList.add(new Song("Nirvana - Come As You Are", R.raw.song3));
        songList.add(new Song("Nirvana - Smells Like Teen Spirit", R.raw.song4));
        songList.add(new Song("Nirvana - Blew", R.raw.song5));
        songList.add(new Song("Nirvana - About A Girl", R.raw.song6));
        songList.add(new Song("Nirvana - Heart Shaped Box", R.raw.song7));
    }

    private void setupRecyclerView() {
        SongAdapter songAdapter = new SongAdapter(songList, this);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        songRecyclerView.setAdapter(songAdapter);
    }

    @Override
    public void onSongClick(int position) {
        // Ganti lagu yang sedang diputar
        currentSongIndex = position;
        initializeMediaPlayer();
        mediaPlayer.start();
        btnPlay.setImageResource(android.R.drawable.ic_media_pause);
        updateSeekBar();
        updateCurrentSongTitle();
    }

    private void initializeMediaPlayer() {
        // Stop dan release media player yang ada (jika ada)
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        // Buat media player baru
        mediaPlayer = MediaPlayer.create(this, songList.get(currentSongIndex).getResourceId());
        mediaPlayer.setOnCompletionListener(mp -> playNextSong());
    }

    private void updateCurrentSongTitle() {
        currentSongTitle.setText(songList.get(currentSongIndex).getTitle());
    }

    private void setupControls() {
        // Tombol Play/Pause
        btnPlay.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnPlay.setImageResource(android.R.drawable.ic_media_play);
            } else {
                mediaPlayer.start();
                btnPlay.setImageResource(android.R.drawable.ic_media_pause);
                updateSeekBar();
            }
        });

        // Tombol Next
        btnNext.setOnClickListener(v -> playNextSong());

        // Tombol Previous
        btnPrevious.setOnClickListener(v -> playPreviousSong());
    }

    private void setupSeekBar() {
        seekBar.setMax(mediaPlayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void updateSeekBar() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        if (mediaPlayer.isPlaying()) {
            handler.postDelayed(updateSeekBarRunnable, 100);
        }
    }

    private final Runnable updateSeekBarRunnable = this::updateSeekBar;

    private void playNextSong() {
        mediaPlayer.stop();
        mediaPlayer.release();
        currentSongIndex = (currentSongIndex + 1) % songList.size();
        initializeMediaPlayer();
        mediaPlayer.start();
        btnPlay.setImageResource(android.R.drawable.ic_media_pause);
        updateSeekBar();
        updateCurrentSongTitle();
    }

    private void playPreviousSong() {
        mediaPlayer.stop();
        mediaPlayer.release();
        currentSongIndex = (currentSongIndex - 1 + songList.size()) % songList.size();
        initializeMediaPlayer();
        mediaPlayer.start();
        btnPlay.setImageResource(android.R.drawable.ic_media_pause);
        updateSeekBar();
        updateCurrentSongTitle();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        handler.removeCallbacks(updateSeekBarRunnable);
    }
}