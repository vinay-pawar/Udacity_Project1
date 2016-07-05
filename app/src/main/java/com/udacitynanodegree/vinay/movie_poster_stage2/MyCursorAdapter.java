package com.udacitynanodegree.vinay.movie_poster_stage2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by somesh on 24/03/16.
 */
public class MyCursorAdapter extends BaseAdapter {
    public static JSONObject myData;
    public static ArrayList<JSONObject> jsonObjects;
    private Context mContext;

    public MyCursorAdapter(Context c, ArrayList<JSONObject> jsonObjects) {
        mContext = c;
        MyCursorAdapter.jsonObjects = jsonObjects;

    }

    public int getCount() {

        int count = 0;


        if (jsonObjects != null) {
            count = jsonObjects.size();
            return count;
        } else
            return 0;

    }


    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        {

            // gridView = new View(mContext);

            gridView = inflater.inflate(R.layout.movie_poster_view, null);


            ImageView imageViewNew = (ImageView) gridView
                    .findViewById(R.id.myImageView);

            String imagePath = null;

            try {
                JSONObject jsonObject = jsonObjects.get(position);


                imagePath = jsonObject.getString("poster_path");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Picasso.with(mContext).load(imagePath)
                    .placeholder(R.drawable.movieplaceholder)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(imageViewNew);


        }

        return gridView;
    }


}
