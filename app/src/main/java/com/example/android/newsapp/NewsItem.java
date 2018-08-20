package com.example.android.newsapp;

import android.graphics.Bitmap;

public class NewsItem {
    //news item thumbnail bitmap image
    private Bitmap thumbnail;
    //news item header
    private String title;
    //news item date
    private String date;
    //news item  url
    private String itemUrl;
    //section name
    private String section;
    //author name
    private String contributor;

    public NewsItem(Bitmap thumbnail, String title, String date, String itemUrl, String section, String contributor){
        this.thumbnail = thumbnail;
        this.title = title;
        this.date = date;
        this.itemUrl = itemUrl;
        this.section = section;
        this.contributor = contributor;
    }

    public
    Bitmap getThumbnail() {
        return thumbnail;
    }

    public String getTitle(){
        return title;
    }

    public String getDate(){
        return date;
    }

    public String getItemUrl(){
        return itemUrl;
    }

    public
    String getSection() {
        return section;
    }

    public
    String getContributor() {
        return contributor;
    }
}
