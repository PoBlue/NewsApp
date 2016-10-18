package com.blues.newsapp;

import android.text.TextUtils;
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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by blues on 2016/10/18.
 */

public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils(){}

    public static List<Article> fetchArticleData(String requestUrl) {

        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        return extractFeatureFromJson(jsonResponse);
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the article JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            }catch (IOException e){
                Log.e(LOG_TAG, "Problem closing the inputStream.", e);
            }
        }

        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<Article> extractFeatureFromJson(String articleJSON) {

        if (TextUtils.isEmpty(articleJSON)) {
            return null;
        }

        List<Article> articles = new ArrayList<>();

        try {

            JSONObject baseJsonResponse = new JSONObject(articleJSON);

            JSONObject response = baseJsonResponse.getJSONObject("response");
            JSONArray articleArray = response.getJSONArray("results");

            for (int i = 0; i < articleArray.length(); i++) {

                JSONObject currentArticle = articleArray.getJSONObject(i);

                String title = currentArticle.getString("webTitle");
                String date = currentArticle.getString("webPublicationDate");
                String sectionName = currentArticle.getString("sectionName");
                String url = currentArticle.getString("webUrl");

                Article article = new Article(title,sectionName,date,url);

                articles.add(article);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the article JSON results", e);
        }

        return articles;
    }
}
