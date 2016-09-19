package com.simpledeveloper.averse.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.simpledeveloper.averse.R;
import com.simpledeveloper.averse.api.PoemsService;
import com.simpledeveloper.averse.db.Poet;
import com.simpledeveloper.averse.pojos.Poem;
import com.simpledeveloper.averse.pojos.Poets;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AverseCoreActivity extends AppCompatActivity {

    private PoemsService apiService;

    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_averse_core);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRealm = Realm.getDefaultInstance();

        apiService = new PoemsService();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryPoetsAsync();

                //queryPoemsByPoetName("Ernest Dowson");
            }
        });

    }

    void queryPoetsAsync(){
        try {
            apiService.getPoetsAsync(new Callback<Poets>() {
                @Override
                public void onResponse(Call<Poets> call, Response<Poets> response) {
                    Log.d("TAG", "completed json: " + response.body().getAuthors().get(0));

                    RealmResults<Poet> oldPoets = mRealm.where(Poet.class).findAllSorted("id");

                    /* clean up old data to avoid duplication */
                    if (!oldPoets.isEmpty()){
                        mRealm.beginTransaction();
                        oldPoets.deleteAllFromRealm();
                        mRealm.commitTransaction();
                    }

                    List<String> syncedPoets = response.body().getAuthors();

                    int numberOfPoets = syncedPoets.size();

                    for (int i = 0; i < numberOfPoets; i++) {

                        RealmResults<Poet> poets = mRealm.where(Poet.class).findAllSorted("id");

                        Poet poet = new Poet();

                        long lastPoetId;

                        if (poets.isEmpty()){
                            poet.setId(0);
                        }else{
                            lastPoetId = poets.last().getId();
                            poet.setId(lastPoetId + 1);
                        }

                        poet.setPoetName(syncedPoets.get(i));

                        mRealm.beginTransaction();
                        mRealm.copyToRealm(poet);
                        mRealm.commitTransaction();
                    }
                }

                @Override
                public void onFailure(Call<Poets> call, Throwable t) {

                }
            });
        }finally {
            Log.d("TAG", "completed");
        }
    }

    void queryPoemsByPoetName(String name){
        try {
            apiService.getPoemsByPoet(new Callback<List<Poem>>() {
                @Override
                public void onResponse(Call<List<Poem>> call, Response<List<Poem>> response) {
                    for (int i = 0; i < response.body().size(); i++) {
                        Log.d("TAG", "completed json: " + response.body().get(i).getAuthor());
                        Log.d("TAG", "completed json: " + response.body().get(i).getTitle());
                        Log.d("TAG", "completed json: " + response.body().get(i).getLinecount());
                    }

                }

                @Override
                public void onFailure(Call<List<Poem>> call, Throwable t) {

                }
            }, name);
        }finally {
            Log.d("TAG", "completed");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_averse_core, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
