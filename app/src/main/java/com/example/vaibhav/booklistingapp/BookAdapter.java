package com.example.vaibhav.booklistingapp;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Vaibhav on 5/31/2017.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    private Context currentContext;
    private List<Book> bookList;

    public BookAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Book> objects) {
        super(context, resource, objects);
        currentContext = context;
        bookList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if(convertView == null) {
            listItemView = LayoutInflater.from(currentContext).inflate(R.layout.book, parent, false);
        }

        Book currentBook = bookList.get(position);

        String bookName = currentBook.getBookName();
        String author = currentBook.getAuthorName();

        TextView bookNameView = (TextView) listItemView.findViewById(R.id.book_name);
        TextView authorView = (TextView) listItemView.findViewById(R.id.author);

        bookNameView.setText(bookName);
        authorView.setText(author);

        return listItemView;
    }
}
