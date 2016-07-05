package com.udacitynanodegree.vinay.movie_poster_stage2.Data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.udacitynanodegree.vinay.movie_poster_stage2.R;

/**
 * Created by somesh on 04/01/16.
 */
public class MyTrailerListAdapter extends BaseAdapter {
    ArrayList<String> trailers = new ArrayList<String>();
    ArrayList<String> trailerkeys = new ArrayList<String>();
    private Context mContext;


    public MyTrailerListAdapter(Context c, ArrayList<String> trailers, ArrayList<String> trailerkeys) {
        mContext = c;
        this.trailers = trailers;
        this.trailerkeys = trailerkeys;
    }

    public int getCount() {

        return trailers.size();

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

        View listView;

        {


            listView = inflater.inflate(R.layout.trailer_view, null);


            TextView trailername = (TextView) listView.findViewById(R.id.trailers);
            TextView trailerlink = (TextView) listView.findViewById(R.id.trailerkeys);


            String name = trailers.get(position);
            String link = trailerkeys.get(position);

            trailername.setText(name);
            trailerlink.setText(link);

        }

        return listView;
    }


}
