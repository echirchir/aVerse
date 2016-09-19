package com.simpledeveloper.averse.api;

import com.simpledeveloper.averse.pojos.Poem;
import com.simpledeveloper.averse.pojos.Poets;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PoemsImplInterface {

    @GET("/author")
    Call<Poets> queryAllAuthors();

    @GET("/author/{author}")
    Call<List<Poem>> queryPoemsByAuthor(@Path("author") String poet);

}
