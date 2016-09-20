package com.simpledeveloper.averse.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.simpledeveloper.averse.R;
import com.simpledeveloper.averse.adapters.AuthorAdapter;
import com.simpledeveloper.averse.api.PoemsService;
import com.simpledeveloper.averse.db.Poet;
import com.simpledeveloper.averse.helpers.DividerItemDecorator;
import com.simpledeveloper.averse.listeners.RecyclerItemClickListener;
import com.simpledeveloper.averse.pojos.Poem;
import com.simpledeveloper.averse.pojos.Poets;
import com.simpledeveloper.averse.ui.Author;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AverseCoreActivity extends AppCompatActivity {

    private PoemsService apiService;

    private Realm mRealm;

    private RecyclerView mRecyclerView;

    private List<Author> authorsList;

    private AuthorAdapter mAuthorAdapter;

    private TextView mNoPoetsView;

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
                //queryPoetsAsync();

                queryPoemsByPoetName("Ben Jonson");
            }
        });

        mNoPoetsView = (TextView) findViewById(R.id.no_poets);

        com.simpledeveloper.averse.db.Poem myPoem = mRealm.where(com.simpledeveloper.averse.db.Poem.class)
                .equalTo("author", "Ben Jonson").findAllSorted("id").get(10);

        /*TextView sample = (TextView) findViewById(R.id.sample);

        String formatted = myPoem.getLines().replace("$", "\n");

        sample.setText(formatted);*/

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addItemDecoration(new DividerItemDecorator(this, DividerItemDecorator.VERTICAL_LIST));

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener
                .OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // TODO: 9/17/16 probably disable clicks here
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));


    }

    @Override
    protected void onResume() {
        super.onResume();

        initPoets();
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

                    RealmResults<com.simpledeveloper.averse.db.Poem> poemsByAuthor = mRealm.where(com.simpledeveloper
                            .averse.db.Poem.class)
                            .equalTo("author", response.body().get(0).getAuthor())
                            .findAll();

                    if (!poemsByAuthor.isEmpty()){
                        mRealm.beginTransaction();
                        poemsByAuthor.deleteAllFromRealm();
                        mRealm.commitTransaction();
                    }

                    com.simpledeveloper.averse.db.Poem poem;

                    for (int i = 0; i < response.body().size(); i++) {

                        List<String> lines = response.body().get(i).getLines();

                        List<String> formattedLines = new ArrayList<>();

                        for (String str : lines) {
                            Log.d("LINESFORMATTED", str);
                            if (str.equals("")){
                                formattedLines.add("$");
                            }else{
                                formattedLines.add(str);
                            }

                        }

                        String completed = TextUtils.join("$", formattedLines);

                        Log.d("LINESFORMATTED", completed);

                        RealmResults<com.simpledeveloper.averse.db.Poem> currentPoems = mRealm.where(com.simpledeveloper
                                .averse.db.Poem.class)
                                .equalTo("author", response.body().get(i).getAuthor())
                                .findAll();

                        poem = new com.simpledeveloper.averse.db.Poem();

                        long lastPoemId;

                        if (currentPoems.isEmpty()){
                            poem.setId(0);
                        }else{
                            lastPoemId = currentPoems.last().getId();
                            poem.setId(lastPoemId + 1);
                        }

                        poem.setAuthor(response.body().get(i).getAuthor());
                        poem.setTitle(response.body().get(i).getTitle());
                        poem.setLinecount(response.body().get(i).getLinecount());
                        poem.setLines(completed);

                        mRealm.beginTransaction();
                        mRealm.copyToRealm(poem);
                        mRealm.commitTransaction();
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

    void initPoets(){
        authorsList = new ArrayList<>();

        RealmResults<Poet> poets = mRealm.where(Poet.class).findAllSorted("poetName", Sort.ASCENDING);

        if (!poets.isEmpty()){
            for (Poet p: poets) {
                authorsList.add(new Author(p.getId(), p.getPoetName()));
            }
        }

        mAuthorAdapter = new AuthorAdapter(authorsList);
        mRecyclerView.setAdapter(mAuthorAdapter);
        mAuthorAdapter.notifyDataSetChanged();

        if (mAuthorAdapter.getItemCount() != 0){
            mNoPoetsView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
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
        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
