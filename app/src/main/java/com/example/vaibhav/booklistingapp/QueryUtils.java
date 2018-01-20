package com.example.vaibhav.booklistingapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.author;
import static com.example.vaibhav.booklistingapp.MainActivity.BASE_URL;
import static com.example.vaibhav.booklistingapp.MainActivity.LOG_TAG;

/**
 * Created by Vaibhav on 5/31/2017.
 */

public class QueryUtils {

    //constructor needs to be private since no instance of this class should ever be created
    private QueryUtils(){

    }

    private static URL createUrl(String targetUrlString){

        //Check if the input string is valid. If valid, return a URL object to make http request to

        if(targetUrlString==null || targetUrlString.isEmpty()) return null;//check for any possible errors: REMAINING

        URL url;
        try{
            url = new URL(targetUrlString);
        }
        catch (MalformedURLException e){
            Log.e(LOG_TAG, "URL formed incorrectly: " + e);
            e.printStackTrace();
            return null;
        }
        Log.i(LOG_TAG, "The URL formed is: " + url);
        return url;

    }

    private static String makeHttpRequest(URL targetURL){

        HttpURLConnection urlConnection = null;
        int responseCode;
        InputStream inputStream;
        String jsonResponseString = "";
        try{
            urlConnection = (HttpURLConnection) targetURL.openConnection();
            responseCode = urlConnection.getResponseCode();
            Log.i(LOG_TAG, "The response code is: " + responseCode);
            if(responseCode != 200) return null;
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(150000);
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            jsonResponseString = readFromStream(inputStream);
        }
        catch (IOException e){
            Log.e(LOG_TAG, "Problem connecting to URL: " + e);
            e.printStackTrace();
        }
        Log.i(LOG_TAG, "Obtained jsonresponse String is: " + jsonResponseString);
        return jsonResponseString;
    }

    private static String readFromStream(InputStream stream){
        InputStreamReader inputStreamReader = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuilder builder = new StringBuilder();
        String line;
        try{
            line = reader.readLine();
            while(line != null){
                builder.append(line);
                line = reader.readLine();
            }
        }
        catch (IOException e){
            Log.e(LOG_TAG, "Problem reading from stream " + e);
            e.printStackTrace();
        }
        return builder.toString();
    }

    private static List<Book> jsonStringToBooks(String jsonResponse){
        JSONObject rootJson;
        JSONArray jsonArray;
        JSONObject properties;
        JSONArray authorsJson;
        String bookName;
        String author;
        List<Book> bookList = new ArrayList<Book>();
        try{
            rootJson = new JSONObject(jsonResponse);
            jsonArray = rootJson.getJSONArray("items");
            for(int i=0; i < jsonArray.length(); i++) {
                properties = jsonArray.getJSONObject(i).getJSONObject("volumeInfo");
                bookName = properties.getString("title");
                author = properties.getJSONArray("authors").getString(0);
                bookList.add(new Book(bookName, author));
            }
        }
        catch (JSONException e){
            Log.e(LOG_TAG, "Problem converting to JSON array: " + e);
            e.printStackTrace();
        }
        Log.i(LOG_TAG, "The json object is: " + bookList.toString());
        return bookList;
    }

    static List<Book> fetchBookData(String queryUrlString){
        URL createdUrl = createUrl(queryUrlString);
        if(createdUrl == null) return null;

        String jsonResponseString = makeHttpRequest(createdUrl);
        if(jsonResponseString==null || jsonResponseString.isEmpty()) {
            Log.e(LOG_TAG, "Error getting json response");
            return null;
        }
        return jsonStringToBooks(jsonResponseString);
    }
}
