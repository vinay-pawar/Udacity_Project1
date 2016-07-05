package com.udacitynanodegree.vinay.movie_poster_stage2;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.udacitynanodegree.vinay.movie_poster_stage2.Data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieListActivityFragment extends Fragment {


    public static ArrayList<JSONObject> jsonObjects;
    static JSONObject jsonObject;
    static String orderPref, check_pref;
    GridView gridview;
    SharedPreferences sharedPref;
    String[] projection = new String[]{
            MovieContract.MovieEntry.COLUMN_ID_KEY,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE};

    public MovieListActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        gridview = (GridView) rootView.findViewById(R.id.gridview);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        check_pref = sharedPref.getString("pref_orderKey", "");


        if (!check_pref.equals("fav"))
            updateDataFromNetwork();
        else
            updateDataFromDatabase();


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

//                Intent detailActivity = new Intent(getActivity(), MovieDetailActivity.class).putExtra("position", position);
//                startActivity(detailActivity);

                ((Callback) getActivity())
                        .onItemSelected(position);

            }
        });


        return rootView;
    }

    public void updateDataFromNetwork() {
        new DataFetchFromNetwork().execute();
    }

    public void updateDataFromDatabase() {
        retriveData();

        setcursorAdapter();
    }

    public void setAdapter() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String key = sharedPref.getString("pref_orderKey", "");
        if (!key.equals("fav"))
            gridview.setAdapter(new MyAdapter(getActivity()));

    }

    public void setcursorAdapter() {

        gridview.setAdapter(new MyCursorAdapter(getActivity(), jsonObjects));
    }

    public void retriveData() {
        ContentResolver resolver = getActivity().getContentResolver();

        Cursor cursor =
                resolver.query(MovieContract.MovieEntry.CONTENT_URI,
                        projection,
                        null,
                        null,
                        null);

        jsonObjects = new ArrayList<JSONObject>();

        if (cursor.moveToFirst()) {
            do {

                //String word = cursor.getString(1);
                // do something meaningful
                JSONObject object = new JSONObject();
                try {

                    int id = cursor.getInt(0);

                    object.put("id", id);

                    String title = cursor.getString(1);
                    object.put("title", title);
                    String overview = cursor.getString(2);
                    object.put("overview", overview);
                    String poster_path = cursor.getString(3);
                    object.put("poster_path", poster_path);
                    String release_date = cursor.getString(4);
                    object.put("release_date", release_date);
                    String vote_average = cursor.getString(5);
                    object.put("vote_average", vote_average);

                    jsonObjects.add(object);


                } catch (JSONException e) {

                    e.printStackTrace();
                }


            } while (cursor.moveToNext());
        }

    }

    @Override
    public void onPause() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        check_pref = sharedPref.getString("pref_orderKey", "");
        super.onPause();
    }

    @Override
    public void onResume() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String key = sharedPref.getString("pref_orderKey", "");
        if (!check_pref.equals(key) && !key.equals("fav"))
            updateDataFromNetwork();
        else
            updateDataFromDatabase();
        super.onResume();
    }

    public interface Callback {
        void onItemSelected(int position);
    }

    public class DataFetchFromNetwork extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            String uri, apiKey, order;

            uri = "http://api.themoviedb.org/3/discover/movie?";
            apiKey = "&api_key=" + "api_key";

            sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            orderPref = sharedPref.getString("pref_orderKey", "");

            order = orderPref;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                URL url = new URL(uri + order + apiKey);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();

                try {
                    jsonObject = new JSONObject(forecastJsonStr);
                    MyAdapter.myData = jsonObject;


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the data, there's no point in attempting
                // to parse it.
                forecastJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {


            if (MyAdapter.myData != null) {
                MyAdapter myAdapter = new MyAdapter(getActivity());
                setAdapter();
                myAdapter.notifyDataSetChanged();

                if (MovieListActivity.mTwoPane) {
                    ((Callback) getActivity())
                            .onItemSelected(0);
                }

            } else
                Toast.makeText(getContext(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();

            super.onPostExecute(aVoid);
        }

    }


}
