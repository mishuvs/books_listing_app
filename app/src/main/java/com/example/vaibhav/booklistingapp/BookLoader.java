package com.example.vaibhav.booklistingapp;

import android.app.DownloadManager;
import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Vaibhav on 6/1/2017.
 */

public class BookLoader extends AsyncTaskLoader<List<Book>> {

    String queryUrlString;
    public BookLoader(Context context, String string) {
        super(context);
        queryUrlString = string;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        return QueryUtils.fetchBookData(queryUrlString);
    }
}
