package com.simpledeveloper.averse.api;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PoemsService {

    private PoemsImplInterface mInterface;

    private final String BASE_URL = "https://thundercomb-poetry-db-v1.p.mashape.com";

    public PoemsService() {

        OkHttpClient mHttpClient = new OkHttpClient();

        mHttpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("X-Mashape-Key", "N8gntYIsnKmshwxYo0Hky3PquUL9p1chgQcjsnLkHXNC2AkQsW")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .header("Accept", "application/json")
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(mHttpClient)
                .build();
        mInterface = retrofit.create(PoemsImplInterface.class);
    }

    public void getPoetsAsync(Callback<com.simpledeveloper.averse.ui.Author> callback){
        mInterface.queryAllAuthors().enqueue(callback);
    }

    public void getPoemsByPoet(Callback<java.util.List<com.simpledeveloper.averse.ui.Poem>> callback, String poet){
        mInterface.queryPoemsByAuthor(poet).enqueue(callback);
    }
}
