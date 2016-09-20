package com.simpledeveloper.averse.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.simpledeveloper.averse.R;
import com.simpledeveloper.averse.api.PoemsService;
import com.simpledeveloper.averse.custom.AverseTextView;
import com.simpledeveloper.averse.db.Poem;
import com.simpledeveloper.averse.helpers.Utils;
import com.simpledeveloper.averse.network.InternetConnectionDetector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static com.simpledeveloper.averse.R.id.poem;

public class PoemsTabActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private Realm mRealm;

    private static RealmResults<com.simpledeveloper.averse.db.Poem> poems;

    private PoemsService apiService;

    private String name;

    private InternetConnectionDetector detector;

    private CoordinatorLayout mCoordinatorLayout;

    private ProgressDialog dialog;

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

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        apiService = new PoemsService();

        detector = new InternetConnectionDetector(this);

        Intent intent = getIntent();

        name = intent.getExtras().getString("name");

        setTitle(name);

        poems = mRealm.where(com.simpledeveloper.averse.db.Poem.class)
                .equalTo("author", name).findAllSorted("id");

        if (!poems.isEmpty()){
            findViewById(R.id.no_poems).setVisibility(GONE);
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), poems.size());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }

    void queryPoemsByPoetName(String name){
        apiService.getPoemsByPoet(new Callback<List<com.simpledeveloper.averse.pojos.Poem>>() {
            @Override
            public void onResponse(Call<List<com.simpledeveloper.averse.pojos.Poem>> call, Response<List<com.simpledeveloper.averse.pojos.Poem>> response) {

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
                        if (str.equals("")){
                            formattedLines.add("$");
                        }else{
                            formattedLines.add(str);
                        }

                    }

                    String completed = TextUtils.join("$", formattedLines);

                    RealmResults<com.simpledeveloper.averse.db.Poem> currentPoems = mRealm.where(com.simpledeveloper
                            .averse.db.Poem.class)
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

                if (dialog != null && dialog.isShowing()){
                    dialog.dismiss();
                }

                finish();
                startActivity(getIntent());
            }

            @Override
            public void onFailure(Call<List<com.simpledeveloper.averse.pojos.Poem>> call, Throwable t) {
                Utils.showSnackBar(PoemsTabActivity.this, mCoordinatorLayout, R.string.something_went_wrong);
            }
        }, name);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRealm.close();
    }

    void showProgress(){
        if (dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.setMessage(getString(R.string.fetching_poems));
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(true);
        }
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        }else if (id == R.id.action_refresh){
            if (detector.isConnectedToInternet()){
                queryPoemsByPoetName(name);
                showProgress();
            }else{
                Utils.showSnackBar(PoemsTabActivity.this, mCoordinatorLayout, R.string.network_warning);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class PoemFragment extends Fragment implements TextToSpeech.OnInitListener {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PoemFragment() {
        }

        private TextToSpeech tts;
        private String formatted;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);

            tts = new TextToSpeech(getActivity(), this);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();

            if (tts != null) {
                tts.stop();
                tts.shutdown();
            }
        }

        @Override
        public void onDetach() {
            super.onDetach();

            if (tts != null) {
                tts.stop();
                tts.shutdown();
            }
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            menu.clear();
            inflater.inflate(R.menu.poem_menu_play, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_play:
                    if (formatted != null){
                        playPoem(formatted);
                    }
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }

        }

        void playPoem(String say){

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                ttsGreaterThan21(say);
            }
            else {
                ttsUnder21(say);
            }
        }

        private void ttsGreaterThan21(String speech){
            String utteranceId=this.hashCode() + "";
            tts.speak(speech, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
        }

        @SuppressWarnings("deprecation")
        private void ttsUnder21(String quote){
            HashMap<String, String> map = new HashMap<>();
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
            tts.speak(quote, TextToSpeech.QUEUE_FLUSH, map);
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
                formatted = poem.getLines().replace("$", "\n");
                poemTitle.setText(poem.getTitle());
                poemText.setText(formatted);
            }

            return rootView;
        }

        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {

                int result = tts.setLanguage(Locale.getDefault());

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {

                    Utils.showSnackBar(getActivity(), getView(), R.string.language_not_supported);
                }

            } else {
                Utils.showSnackBar(getActivity(), getView(), R.string.something_went_wrong);
            }
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
