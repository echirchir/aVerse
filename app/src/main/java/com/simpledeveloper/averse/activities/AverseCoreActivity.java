package com.simpledeveloper.averse.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.simpledeveloper.averse.R;
import com.simpledeveloper.averse.adapters.AuthorAdapter;
import com.simpledeveloper.averse.api.PoemsService;
import com.simpledeveloper.averse.db.Poet;
import com.simpledeveloper.averse.helpers.DividerItemDecorator;
import com.simpledeveloper.averse.helpers.PoetsSyncEvent;
import com.simpledeveloper.averse.helpers.Utils;
import com.simpledeveloper.averse.listeners.RecyclerItemClickListener;
import com.simpledeveloper.averse.network.InternetConnectionDetector;
import com.simpledeveloper.averse.pojos.Poets;
import com.simpledeveloper.averse.ui.Author;
import com.squareup.seismic.ShakeDetector;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AverseCoreActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,
        com.squareup.seismic.ShakeDetector.Listener  {

    private PoemsService apiService;

    private Realm mRealm;

    private RecyclerView mRecyclerView;

    private List<Author> authorsList;

    private AuthorAdapter mAuthorAdapter;

    private TextView mNoPoetsView;

    private CoordinatorLayout mCoordinatorLayout;

    private InternetConnectionDetector mConnectionDetector;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        setContentView(R.layout.activity_averse_core);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRealm = Realm.getDefaultInstance();

        apiService = new PoemsService();

        mConnectionDetector = new InternetConnectionDetector(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mConnectionDetector.isConnectedToInternet()){
                    queryPoetsAsync();
                }else{
                    Utils.showSnackBar(AverseCoreActivity.this, mCoordinatorLayout, R.string.network_warning);
                }
            }
        });

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);

        mNoPoetsView = (TextView) findViewById(R.id.no_poets);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        ViewCompat.setNestedScrollingEnabled(mRecyclerView, false);

        mRecyclerView.addItemDecoration(new DividerItemDecorator(this, DividerItemDecorator.VERTICAL_LIST));

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener
                .OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(AverseCoreActivity.this, PoemsTabActivity.class);
                intent.putExtra("name", mAuthorAdapter.getItem(position).getAuthor());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

    }

    @Subscribe
    public void onPoetsSynced(PoetsSyncEvent event){
        if (event.isSynced()){
            initPoets();
        }
    }

    void toggleProgressbar(){
        if (dialog == null){
            dialog = new ProgressDialog(this);
            dialog.setMessage(getString(R.string.fetching_poets));
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(true);
        }

        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initPoets();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRealm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_averse_core, menu);

        final MenuItem item = menu.findItem(R.id.action_search);

        MenuItemCompat.expandActionView(item);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setQueryHint(getString(R.string.search_authors));

        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_search:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    void queryPoetsAsync(){
        toggleProgressbar();
        apiService.getPoetsAsync(new Callback<Poets>() {
            @Override
            public void onResponse(Call<Poets> call, Response<Poets> response) {

                if (dialog != null && dialog.isShowing()){
                    dialog.dismiss();
                }

                RealmResults<Poet> oldPoets = mRealm.where(Poet.class).findAllSorted("id");

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

                EventBus.getDefault().post(new PoetsSyncEvent(true));
            }

            @Override
            public void onFailure(Call<Poets> call, Throwable t) {

            }
        });

    }

    void initPoets(){
        authorsList = new ArrayList<>();

        RealmResults<Poet> poets = mRealm.where(Poet.class).findAllSorted("poetName", Sort.ASCENDING);

        if (!poets.isEmpty()){
            for (Poet p: poets) {
                authorsList.add(new Author(p.getId(), p.getPoetName()));
            }
        }

        mAuthorAdapter = new AuthorAdapter(authorsList, this);
        mRecyclerView.setAdapter(mAuthorAdapter);
        mAuthorAdapter.notifyDataSetChanged();

        if (mAuthorAdapter.getItemCount() != 0){
            mNoPoetsView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (newText.equals("")){
            initPoets();
            return true;
        }else{
            final List<Author> filteredModelList = filter(authorsList, newText);

            mAuthorAdapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(mAuthorAdapter);
            mAuthorAdapter.animateTo(filteredModelList);
            mRecyclerView.scrollToPosition(0);
            return true;
        }
    }

    List<Author> filter(List<Author> models, String query) {

        query = query.toLowerCase();

        final List<Author> filteredModelList = new ArrayList<>();

        if(query.equals("")) { return authorsList; }

        for (Author model : models) {
            final String text = model.getAuthor().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    public void hearShake() {
        queryPoetsAsync();
    }
}
