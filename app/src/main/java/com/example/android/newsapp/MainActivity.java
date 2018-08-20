package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public
class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<NewsItem>>{
    private static final String REQUEST_URL = "https://content.guardianapis.com/search";
    //private static final String REQUEST_URL = "https://content.guardianapis.com/search?order-by=newest&show-tags=contributor&show-fields=thumbnail&q=design&api-key=418ac8ca-84c8-4acb-9c12-338ef6ffaa39";
    //private static final String REQUEST_URL = "https://content.guardianapis.com/search?order-by=newest&show-tags=contributor&show-fields=thumbnail&q=ux%20design&api-key=418ac8ca-84c8-4acb-9c12-338ef6ffaa39";
    /**
     * Adapter for the list of newsItems
     */
    private NewsAdapter mAdapter;
    ListView newsListView;
    TextView mEmptyState;
    ProgressBar mProgressBar;


    @Override
    protected
    void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = findViewById(R.id.progressBar);
        mEmptyState = findViewById(R.id.empty_state);
        newsListView = findViewById(R.id.list);
        newsListView.setEmptyView(mEmptyState);

        // Checking the internet connection before initializing the loader
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()) {
            getLoaderManager().initLoader(0, null, this);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mEmptyState.setText(R.string.no_internet_connection);
        }


        // Create a new adapter that takes an empty list of news items as input
        mAdapter = new NewsAdapter(this, new ArrayList<NewsItem>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);


        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected earthquake.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public
            void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                NewsItem currentNewsItem = (NewsItem) mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = Uri.parse(currentNewsItem.getItemUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
    }

    @Override
    public
    Loader<ArrayList<NewsItem>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String orderBy = sharedPrefs.getString(getString(R.string.settings_order_by_key),getString(R.string.settings_order_by_default));

        Uri baseUri = Uri.parse(REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("show-fields","thumbnail");
        uriBuilder.appendQueryParameter("q","politics");
        uriBuilder.appendQueryParameter("api-key","418ac8ca-84c8-4acb-9c12-338ef6ffaa39");

        Log.e("uribuilder", "url: " + uriBuilder.toString());
        return new NewsLoader(MainActivity.this, uriBuilder.toString());

    }

    @Override
    public
    void onLoadFinished(Loader<ArrayList<NewsItem>> loader, ArrayList<NewsItem> newsItems) {
        mEmptyState.setText(R.string.empty_state_text);
        mProgressBar.setVisibility(View.GONE);
        mAdapter.clear();
        if (newsItems != null && !newsItems.isEmpty()) {
            mAdapter.addAll(newsItems);
        }
    }

    @Override
    public
    void onLoaderReset(Loader<ArrayList<NewsItem>> loader) {

    }
    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
