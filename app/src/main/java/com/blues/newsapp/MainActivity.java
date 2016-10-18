package com.blues.newsapp;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderCallbacks<List<Article>> {

    private static final int Article_LOADER_ID = 1;
    String url = "http://content.guardianapis.com/search?q=debates&api-key=test";

    private TextView mEmptyStateTextView;
    private ArticleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initListView();
        initLoader();
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


}
