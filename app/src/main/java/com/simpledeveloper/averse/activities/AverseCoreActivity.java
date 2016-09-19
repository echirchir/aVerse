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
import com.simpledeveloper.averse.pojos.Poets;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AverseCoreActivity extends AppCompatActivity {

    private PoemsService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_averse_core);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        apiService = new PoemsService();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryPoetsAsync();
            }
        });

    }

    void queryPoetsAsync(){
        try {
            apiService.getPoetsAsync(new Callback<Poets>() {
                @Override
                public void onResponse(Call<Poets> call, Response<Poets> response) {
                    Log.d("TAG", "completed json: " + response.body().getAuthors().size());
                }

                @Override
                public void onFailure(Call<Poets> call, Throwable t) {

                }
            });
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
