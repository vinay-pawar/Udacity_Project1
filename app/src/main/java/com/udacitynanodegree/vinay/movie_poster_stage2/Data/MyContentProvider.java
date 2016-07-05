package com.udacitynanodegree.vinay.movie_poster_stage2.Data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MyContentProvider extends ContentProvider {


    // Codes for the UriMatcher //////
    private static final int MOVIE = 100;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;


    public MyContentProvider() {

    }

    private static UriMatcher buildUriMatcher() {
        // Build a UriMatcher by adding a specific code to return based on a match
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // add a code for each type of URI you want
        matcher.addURI(authority, MovieContract.MovieEntry.TABLE_NAME, MOVIE);


        return matcher;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        mOpenHelper = new MovieDbHelper(getContext());

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int rowsDeleted;

        String id = selectionArgs[0];

        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";

        rowsDeleted = db.delete(
                MovieContract.MovieEntry.TABLE_NAME,
                MovieContract.MovieEntry.COLUMN_ID_KEY + "=" + id, null);

        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;


    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE: {
                return MovieContract.MovieEntry.CONTENT_TYPE;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        Uri returnUri = null;

        try {


            mOpenHelper = new MovieDbHelper(getContext());

            final SQLiteDatabase db = mOpenHelper.getWritableDatabase();


            long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
            if (_id > 0)
                returnUri = MovieContract.MovieEntry.buildMovieData(_id);
        } catch (Exception e) {
            returnUri = Uri.parse("Data Already Exit");
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        mOpenHelper = new MovieDbHelper(getContext());

        retCursor = mOpenHelper.getReadableDatabase().query(
                MovieContract.MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

        return retCursor;


    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
