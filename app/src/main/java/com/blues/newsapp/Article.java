package com.blues.newsapp;

/**
 * Created by blues on 2016/10/18.
 */

public class Article {

    private String mTitle,mSectionName,mDate;

    public Article(String title, String sectionName, String date){
        mTitle = title;
        mSectionName = sectionName;
        mDate = date;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSectionName() {
        return mSectionName;
    }

    public String getDate() {
        return mDate;
    }

}
