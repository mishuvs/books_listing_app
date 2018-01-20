package com.example.vaibhav.booklistingapp;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private static int LOADER_ID = 0;
    private int numberOfCallsToLoader;
    private static String queriedBook = "";

    public String completeUrlString;
    BookAdapter adapter;
    LoaderManager loaderManager;
    Loader loader;

    private TextView emptyView;
    View loadingIndicator;
    ListView listView;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.book_list);
        emptyView = (TextView) findViewById(R.id.empty_text_view);
        loadingIndicator = findViewById(R.id.loading_indicator);
        editText = (EditText) findViewById(R.id.query_edit_text_view);

        loadingIndicator.setVisibility(View.INVISIBLE);

        adapter = new BookAdapter(this, R.layout.book, new ArrayList<Book>());
        listView.setAdapter(adapter);

        loaderManager = getLoaderManager();

        //The if block makes sure that if internet is not connected the message is displayed, but when connected ..
        //you need to restart the app... using while loop is giving problem
        if(!checkNetworkConnection()) {
            emptyView.setText(R.string.no_internet);
            listView.setEmptyView(emptyView);
        }
        else {
            if(!queriedBook.isEmpty()) loaderManager.initLoader(LOADER_ID, null, this);
            Log.i(LOG_TAG, "Inside onCreate.... queriedString is: " + queriedBook);
        }

        closeKeyboard(this);
    }

    public boolean createUrlString() {

        queriedBook = editText.getText().toString();
        completeUrlString = BASE_URL + queriedBook;
        Log.i(LOG_TAG, "Required url is: " + completeUrlString);

        return queriedBook.isEmpty();
    }

    public boolean checkNetworkConnection(){
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public void fetchList(View view){
        closeKeyboard(this);
        if(createUrlString()) {
            emptyView.setText(R.string.search_empty);
            listView.setEmptyView(emptyView);
            return;
        }

        //check if the network connection available
        if(!checkNetworkConnection()) {
            adapter.clear();
            emptyView.setText(R.string.no_internet);
            listView.setEmptyView(emptyView);
            return;
        }

        if(numberOfCallsToLoader == 0){
            loader = loaderManager.initLoader(LOADER_ID, null, this);
        }
        else {
            loaderManager.restartLoader(LOADER_ID, null, this);
        }
        numberOfCallsToLoader++;
        Log.i(LOG_TAG, "Is the loader reset: " + loader.isReset());
    }

    public void closeKeyboard(Activity activity){
        //This code is to stop the keyboard from popping up
        //Don't understand the code.. using it temporarily
        InputMethodManager inputMethodManager = (InputMethodManager)activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        View focus = activity.getCurrentFocus();
        editText.clearFocus();
        if(focus != null)
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        else
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        //keyboard closed
    }

    private void hideIme() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        adapter.clear();
        emptyView.setText("");
        listView.setEmptyView(emptyView);
        loadingIndicator.setVisibility(View.VISIBLE);
        Log.i(LOG_TAG, "Inside onCreateLoader");
        return new BookLoader(this, completeUrlString);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
        loadingIndicator.setVisibility(View.INVISIBLE);
        Log.i(LOG_TAG, "Inside onLoadFinished");

        adapter.clear();

        if(data != null && !data.isEmpty()) {
            adapter.addAll(data);
        }
        else {
            emptyView.setText(R.string.no_books);
            listView.setEmptyView(emptyView);
            Log.i(LOG_TAG, "Empty or null response");
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        Log.i(LOG_TAG, "Inside onLoaderReset");

        adapter.clear();
    }

}
