package com.udacitynanodegree.vinay.movie_poster_stage2;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
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
import com.udacitynanodegree.vinay.movie_poster_stage2.Data.MyTrailerListAdapter;

/**
 * A placeholder fragment containing a simple view.
 */

public class MovieDetailActivityFragment extends Fragment {

    static int position = 0;
    static JSONObject jsonObject;
    String id, title, imagePath, releaseDate, rating, overview;
    ImageView moviePoster, like, removelike;
    TextView movieRating, movieReleaseDate, overView;
    ListView trailerlistView;
    Boolean favexist = false, favcursorexist = false;
    String[] projection = new String[]{
            MovieContract.MovieEntry.COLUMN_ID_KEY};
    ArrayList<String> trailers, trailerkeys;
    private ShareActionProvider mShareActionProvider;


    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rooView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        setHasOptionsMenu(true);

        moviePoster = (ImageView) rooView.findViewById(R.id.movieposter);
        movieRating = (TextView) rooView.findViewById(R.id.rating);
        movieReleaseDate = (TextView) rooView.findViewById(R.id.releasedate);
        overView = (TextView) rooView.findViewById(R.id.overview);
        like = (ImageView) rooView.findViewById(R.id.like);
        removelike = (ImageView) rooView.findViewById(R.id.unlike);
        trailerlistView = (ListView) rooView.findViewById(R.id.trailors);


        if (savedInstanceState == null) {
            Bundle args = getArguments();
            Intent arguments = getActivity().getIntent();
            if (args != null)
                position = args.getInt("position", 0);
            else
                position = arguments.getIntExtra("position", 0);
        } else {

        }

        if (MyAdapter.myData != null && MyAdapter.ismyadapterpresent == true) {

            try {
                JSONObject jsonObject = MyAdapter.myData;
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                JSONObject jsonObject1 = jsonArray.getJSONObject(position);
                id = jsonObject1.getString("id");
                title = jsonObject1.getString("title");
                imagePath = jsonObject1.getString("poster_path");
                releaseDate = jsonObject1.getString("release_date");
                rating = jsonObject1.getString("vote_average");
                overview = jsonObject1.getString("overview");
                retriveData();
                retriveTrailers();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

            if (MyCursorAdapter.jsonObjects != null && MyAdapter.ismyadapterpresent == false) {

                try {

                    favcursorexist = true;

                    JSONObject jsonObject1 = MyCursorAdapter.jsonObjects.get(position);
                    id = String.valueOf(jsonObject1.getInt("id"));
                    title = jsonObject1.getString("title");
                    imagePath = jsonObject1.getString("poster_path");
                    releaseDate = jsonObject1.getString("release_date");
                    rating = jsonObject1.getString("vote_average");
                    retriveData();
                    retriveTrailers();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w500/" + imagePath).placeholder(R.drawable.movieplaceholder).into(moviePoster);

        getActivity().setTitle(title);

        movieRating.setText("Rating: " + rating + "/10");
        movieReleaseDate.setText("Release Date : " + releaseDate);
        overView.setText(overview);


        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Add a new like record
                ContentValues values = new ContentValues();

                values.put(MovieContract.MovieEntry.COLUMN_ID_KEY, id);

                values.put(MovieContract.MovieEntry.COLUMN_TITLE, title);

                values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "http://image.tmdb.org/t/p/w500/" + imagePath);

                values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);

                values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);

                values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, rating);


                Uri uri = getActivity().getContentResolver().insert(
                        MovieContract.MovieEntry.CONTENT_URI, values);

                Toast.makeText(getContext(),
                        "Liked", Toast.LENGTH_LONG).show();

                like.setVisibility(View.GONE);
                removelike.setVisibility(View.VISIBLE);


            }
        });

        removelike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] args = new String[]{id};

                int uri = getActivity().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                        MovieContract.MovieEntry.COLUMN_ID_KEY, args);

                Toast.makeText(getContext(),
                        "Rmoved ", Toast.LENGTH_LONG).show();


                like.setVisibility(View.VISIBLE);
                removelike.setVisibility(View.GONE);

            }
        });

        trailerlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                watchYoutubeVideo(trailerkeys.get(position));
            }
        });


        return rooView;
    }

    public void retriveData() {
        ContentResolver resolver = getActivity().getContentResolver();

        Cursor cursor =
                resolver.query(MovieContract.MovieEntry.CONTENT_URI,
                        projection,
                        null,
                        null,
                        null);


        if (cursor.moveToFirst()) {
            do {
                String _id = String.valueOf(cursor.getInt(0));
                if (id.equals(_id)) {
                    favexist = true;

                }

            } while (cursor.moveToNext() && favexist != true);
        }

        if (!favexist) {
            like.setVisibility(View.VISIBLE);

        } else {
            removelike.setVisibility(View.VISIBLE);

        }

    }


    public void retriveTrailers() {

        new FetchTrailorsFromNetwork().execute();


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_detail, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        mShareActionProvider.setShareIntent(getDefaultShareIntent());


    }

    /**
     * Returns a share intent
     */
    private Intent getDefaultShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        if (trailers != null) {
            intent.putExtra(Intent.EXTRA_SUBJECT, trailers.get(0));
            intent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=" + trailerkeys.get(0));
        } else
            intent.putExtra(Intent.EXTRA_TEXT, "Awesome Trailor");
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);

    }


    public void watchYoutubeVideo(String id) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            startActivity(intent);
        }
    }


    public class FetchTrailorsFromNetwork extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            String uri, apiKey, movie_id;

            uri = "http://api.themoviedb.org/3/movie/";
            movie_id = id;
            apiKey = "/videos?api_key=" + "api_key";


            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                URL url = new URL(uri + movie_id + apiKey);

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

                    JSONArray jsonArray = jsonObject.getJSONArray("results");

                    int size = jsonArray.length();

                    trailers = new ArrayList<String>();

                    trailerkeys = new ArrayList<String>();


                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        trailers.add(jsonObject1.getString("name"));

                        trailerkeys.add(jsonObject1.getString("key"));


                    }


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

            if (trailers != null) {
                trailerlistView.setAdapter(new MyTrailerListAdapter(getContext(), trailers, trailerkeys));
            }


            super.onPostExecute(aVoid);
        }


    }


}
