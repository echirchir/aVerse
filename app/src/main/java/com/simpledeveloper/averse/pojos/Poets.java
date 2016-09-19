package com.simpledeveloper.averse.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Poets {

    @SerializedName("authors")
    @Expose
    private List<String> authors = new ArrayList<>();

    /**
     *
     * @return
     * The authors
     */
    public List<String> getAuthors() {
        return authors;
    }

    /**
     *
     * @param authors
     * The authors
     */
    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

}
