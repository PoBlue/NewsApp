package com.blues.newsapp;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements LoaderCallbacks<List<Article>>, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int Article_LOADER_ID = 1;
    String url = "http://content.guardianapis.com/search?q=debates&api-key=test";

    private TextView mEmptyStateTextView;
    private ArticleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupSharePreferences();
        initListView();
        initLoader();
        updateProperly();

    }

    private String buildNewsUrl(String section, String tag) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("content.guardianapis.com")
                .appendPath("search")
                .appendQueryParameter("q", section)
                .appendQueryParameter("tag", tag)
                .appendQueryParameter("api-key", "test");
        return builder.build().toString();
    }

    private void setupSharePreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String tag = sharedPreferences.getString(getString(R.string.pref_tag_key), getString(R.string.pref_count_default));
        String section = sharedPreferences.getString(getString(R.string.pref_section_key), getString(R.string.pref_section_default));

        url = buildNewsUrl(section, tag);
        // Register the listener
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        if (key == getString(R.string.pref_count_key)){
//            String newsCount = sharedPreferences.getString(key, getString(R.string.pref_count_default));
//            getLoaderManager().restartLoader(Article_LOADER_ID, null, this);
//        } else if (key == getString(R.string.pref_section_key)){
//            String section = sharedPreferences.getString(key, getString(R.string.pref_section_default));
//            getLoaderManager().restartLoader(Article_LOADER_ID, null, this);
//        }
        String tag = sharedPreferences.getString(getString(R.string.pref_tag_key), getString(R.string.pref_count_default));
        String section = sharedPreferences.getString(getString(R.string.pref_section_key), getString(R.string.pref_section_default));

        url = buildNewsUrl(section, tag);
        getLoaderManager().restartLoader(Article_LOADER_ID, null, this);
    }

    private void updateProperly(){
        int delay = 60000; // delay for 60 sec.
        int period = 60000; // repeat every 60 sec.

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                getLoaderManager().restartLoader(Article_LOADER_ID, null, MainActivity.this);
            }
        }, delay, period);
    }

    private void initListView(){
        ListView articleListView = (ListView) findViewById(R.id.list);

        //set empty view
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        articleListView.setEmptyView(mEmptyStateTextView);

        mAdapter = new ArticleAdapter(this,new ArrayList<Article>());
        articleListView.setAdapter(mAdapter);

        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Article currentArticle = mAdapter.getItem(position);

                Uri articleUri = Uri.parse(currentArticle.getUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);
                startActivity(websiteIntent);
            }
        });

    }

    private void initLoader(){
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            android.app.LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(Article_LOADER_ID, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }


    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {
        return new ArticleLoader(this,url);
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        mEmptyStateTextView.setText(R.string.no_article);
        mAdapter.clear();

        if (articles != null && !articles.isEmpty()){
            mAdapter.addAll(articles);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        mAdapter.clear();
    }

    /**
     * Methods for setting up the menu
     **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our visualizer_menu layout to this menu */
        inflater.inflate(R.menu.news_menu, menu);
        /* Return true so that the visualizer_menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
