package com.blues.newsapp;

/**
 * Created by blues on 2016/10/18.
 */

public class Article {

    private String mTitle,mSectionName,mDate,mUrl;

    public Article(String title, String sectionName, String date,String url){
        mTitle = title;
        mSectionName = sectionName;
        mDate = date;
        mUrl = url;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSectionName() {
        return mSectionName;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getDate() {
        return mDate;
    }

}
