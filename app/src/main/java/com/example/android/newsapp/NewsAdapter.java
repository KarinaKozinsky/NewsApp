package com.example.android.newsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter {
    public NewsAdapter(@NonNull Context context, ArrayList<NewsItem>newsItems) {
        super(context, 0, newsItems);
    }
   @NonNull
    @Override
    public
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item, parent, false);
        }
        NewsItem currentItem = (NewsItem) getItem(position);

        ImageView thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
       assert currentItem != null;
       if(currentItem.getThumbnail()==null) {
            thumbnail.setImageResource(R.drawable.noimage);
        }else{
            thumbnail.setImageBitmap(formatImageFromBitmap(currentItem.getThumbnail()));
        }


        TextView title = (TextView) convertView.findViewById(R.id.title);
        title.setText(currentItem.getTitle());

        TextView date = (TextView) convertView.findViewById(R.id.date);
        date.setText(currentItem.getDate());

        TextView section = (TextView)convertView.findViewById(R.id.section);
        section.setText(currentItem.getSection());

        TextView contributor = (TextView) convertView.findViewById(R.id.contributor);
        if(currentItem.getContributor()==null) {
            contributor.setVisibility(View.GONE);
        }else {
            contributor.setText(currentItem.getContributor());
        }
        return convertView;
    }

    // Get the thumbnail image
    private Bitmap formatImageFromBitmap(Bitmap articleThumbnail) {
        // Bitmap for image
        Bitmap returnBitmap;
        // Check thumbnail valid
        if (articleThumbnail == null) {
            // If not valid return default image
            returnBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.noimage);
        } else {
            // If valid return image
            returnBitmap = articleThumbnail;
        }
        // Return bitmap
        return returnBitmap;
    }
}
