package com.example.playmusic;

public class Song {
    private final String title;
    private final int resourceId;

    public Song(String title, int resourceId) {
        this.title = title;
        this.resourceId = resourceId;
    }

    public String getTitle() {
        return title;
    }

    public int getResourceId() {
        return resourceId;
    }
}