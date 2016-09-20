package com.simpledeveloper.averse.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.simpledeveloper.averse.R;
import com.simpledeveloper.averse.custom.AverseTextView;
import com.simpledeveloper.averse.db.Poem;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.view.View.GONE;
import static com.simpledeveloper.averse.R.id.poem;

public class PoemsTabActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private Realm mRealm;

    private static RealmResults<com.simpledeveloper.averse.db.Poem> poems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        setContentView(R.layout.activity_poems_tab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRealm = Realm.getDefaultInstance();

        Intent intent = getIntent();

        String name = intent.getExtras().getString("name");

        setTitle(name);

        poems = mRealm.where(com.simpledeveloper.averse.db.Poem.class)
                .equalTo("author", name).findAllSorted("id");

        if (!poems.isEmpty()){
            findViewById(R.id.no_poems).setVisibility(GONE);
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), poems.size());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_poems_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            startActivity(new Intent(this, AverseCoreActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class PoemFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PoemFragment() {
        }

        public static PoemFragment newInstance(int sectionNumber) {
            PoemFragment fragment = new PoemFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber-1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_poems_tab, container, false);
            AverseTextView poemText = (AverseTextView) rootView.findViewById(poem);
            AverseTextView poemTitle = (AverseTextView) rootView.findViewById(R.id.title);

            int position = getArguments().getInt(ARG_SECTION_NUMBER);

            if (position < poems.size()){
                Poem poem = poems.get(position);
                String formatted = poem.getLines().replace("$", "\n");
                poemTitle.setText(poem.getTitle());
                poemText.setText(formatted);
            }

            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private int sizeOfPoems;

        public SectionsPagerAdapter(FragmentManager fm, int size) {
            super(fm);

            this.sizeOfPoems = size;
        }

        @Override
        public Fragment getItem(int position) {

            return PoemFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return sizeOfPoems;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return null;
        }
    }
}
