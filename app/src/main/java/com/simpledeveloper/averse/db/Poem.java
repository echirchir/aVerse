package com.simpledeveloper.averse.db;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class Poem extends RealmObject {

    @PrimaryKey @Index
    private long id;

    private String title;
    private String author;
    private String lines;
    private int linecount;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLines() {
        return lines;
    }

    public void setLines(String lines) {
        this.lines = lines;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getLinecount() {
        return linecount;
    }

    public void setLinecount(int linecount) {
        this.linecount = linecount;
    }
}
