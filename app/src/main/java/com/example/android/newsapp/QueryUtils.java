package com.example.android.newsapp;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public final class QueryUtils {

    private static final int urlConnectionReadTimeout = 10000;
    private static final int urlConnectionConnectTimeout = 15000;
    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }
    //calls all helper functions and returns newsItems array
    public static ArrayList<NewsItem> fetchNewsItems(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("Utils", "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and return an array list

        return extractNewsItems(jsonResponse);
    }
    /**
     * Returns new URL object from the given string URL.
     */
    private static
    URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("Utils", "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
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
            urlConnection.setReadTimeout(urlConnectionReadTimeout /* milliseconds */);
            urlConnection.setConnectTimeout(urlConnectionConnectTimeout /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("Utils", "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e("Utils", "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
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
    /**
     * Return a list of {@link NewsItem} objects that has been built up from
     * parsing a JSON response.
     */
    private static
    ArrayList<NewsItem> extractNewsItems(String jsonResponse)  {
        // Create an empty ArrayList that we can start adding news items to
        ArrayList<NewsItem> newsItems = new ArrayList<>();
        Bitmap thumbnailBitmap = null;
        String contributor = null;
        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        try {
            //get json response object
            JSONObject main = new JSONObject(jsonResponse);
            JSONObject response = main.getJSONObject("response");
            //get results array and iterate over the array elements
            JSONArray results = response.getJSONArray("results");
            for (int i = 0; i <= results.length(); i++) {
                JSONObject everyResult = results.getJSONObject(i);
                //retrieve section name
                String section = everyResult.getString("sectionName");
                //retrieve image
                if (everyResult.has("fields")) {
                    JSONObject fields = everyResult.getJSONObject("fields");
                    if (fields.has("thumbnail")) {
                        String thumbnail = fields.getString("thumbnail");
                        URL thumbnailUrl = new URL(thumbnail);
                        thumbnailBitmap = BitmapFactory.decodeStream(thumbnailUrl.openConnection().getInputStream());
                    }
                }
                //retrieve header
                String header = everyResult.getString("webTitle");
                //retrieve date
                String rawDate = everyResult.getString("webPublicationDate");
                String[] parts = rawDate.split("T");
                String date = parts[0];
                //retrieve url
                String url = everyResult.getString("webUrl");

                // retrieve tags JSON array for contributor data
                if(everyResult.has("tags")){
                    JSONArray tagsArray = everyResult.getJSONArray("tags");

                    for (int j = 0; j < tagsArray.length(); j++) {
                            JSONObject tag = tagsArray.getJSONObject(j);
                            contributor = tag.getString("webTitle");
                    }
                }
                newsItems.add(new NewsItem(thumbnailBitmap, header, date, url, section, contributor));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);

        } catch (MalformedURLException e) {
            Log.e("QueryUtils", "Malformed URL", e);
        } catch (IOException e) {
            Log.e("QueryUtils", "I/O Exception", e);;
        }

        // Return the list of newsItems
        return newsItems;
    }
}
