package com.blues.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by blues on 2016/10/18.
 */

public class ArticleAdapter extends ArrayAdapter<Article>{

    public ArticleAdapter(Context context, List<Article> articles) {
        super(context, 0,articles);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.article_list_item, parent, false);
        }

        Article currentArticle = getItem(position);

        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title_tv);
        titleTextView.setText(currentArticle.getTitle());

        TextView sectionTextView = (TextView) listItemView.findViewById(R.id.section_tv);
        sectionTextView.setText(currentArticle.getSectionName());

        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date_tv);
        dateTextView.setText(currentArticle.getDate());

        return listItemView;
    }
}
