package com.simpledeveloper.averse.pojos;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Poem {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("lines")
    @Expose
    private List<String> lines = new ArrayList<>();
    @SerializedName("linecount")
    @Expose
    private int linecount;

    /**
     *
     * @return
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The author
     */
    public String getAuthor() {
        return author;
    }

    /**
     *
     * @param author
     * The author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     *
     * @return
     * The lines
     */
    public List<String> getLines() {
        return lines;
    }

    /**
     *
     * @param lines
     * The lines
     */
    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    /**
     *
     * @return
     * The linecount
     */
    public int getLinecount() {
        return linecount;
    }

    /**
     *
     * @param linecount
     * The linecount
     */
    public void setLinecount(int linecount) {
        this.linecount = linecount;
    }

}