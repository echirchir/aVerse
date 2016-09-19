package com.simpledeveloper.averse.api;

import com.simpledeveloper.averse.pojos.Poets;
import com.simpledeveloper.averse.pojos.Poem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PoemsImplInterface {

    @POST("/author")
    Call<Poets> queryAllAuthors();

    @POST("/author/{poet}")
    Call<List<Poem>> queryPoemsByAuthor(@Path("poet") String poet);

}
