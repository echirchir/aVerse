package com.simpledeveloper.averse.ui;


public class Author {

    private long id;
    private String author;

    public Author(long id, String author) {
        this.id = id;
        this.author = author;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
