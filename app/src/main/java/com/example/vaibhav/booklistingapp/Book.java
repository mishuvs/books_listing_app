package com.example.vaibhav.booklistingapp;

/**
 * Created by Vaibhav on 5/31/2017.
 */

public class Book {

    //class attributes
    private String name;
    private String author;

    //constructor
    public Book(String bookName, String bookAuthor){
        name = bookName;
        author = bookAuthor;
    }

    //various get methods
    public String getBookName(){
        return name;
    }

    public String getAuthorName(){
        return author;
    }
}
