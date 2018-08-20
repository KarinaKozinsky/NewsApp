package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

public class NewsLoader extends AsyncTaskLoader<ArrayList<NewsItem>> {

    private String Url;

    public
    NewsLoader(Context context, String url) {
        super(context);
        this.Url = url;
    }

    @Override
    protected
    void onStartLoading() {
        forceLoad();
    }

    @Override
    public
    ArrayList<NewsItem> loadInBackground() {
        if (Url == null){
            return null;
        }
        return QueryUtils.fetchNewsItems(Url);

   }
}
