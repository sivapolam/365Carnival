package com.techplicit.mycarnival.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.techplicit.mycarnival.IntentGenerator;
import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;


/**
 * Created by pnaganjane001 on 14/11/15.
 */
public class GalleryAdapter extends BaseAdapter implements Constants {

    private Context context;

    LayoutInflater inflater;
    private JSONArray stringArrayList;
    ImageLoader imageLoader;

    public GalleryAdapter(Context context, JSONArray stringArrayList) {
        this.context = context;
        this.stringArrayList = stringArrayList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(context);
    }

    @Override
    public int getCount() {
        return this.stringArrayList.length();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder = new ViewHolder();
        final View rootView;
        rootView = inflater.inflate(R.layout.gallery_item, null);
        holder.iv_gallery_image = (ImageView) rootView.findViewById(R.id.iv_gallery_image);
        holder.iv_gallery_fav = (ImageView) rootView.findViewById(R.id.iv_gallery_fav);
        rootView.setId(position);

        try {
            if (stringArrayList.getString(position) != null) {
                Picasso.with(context).load(stringArrayList.getString(position)).into(holder.iv_gallery_image);

//                imageLoader.DisplayImage(stringArrayList.getString(position), holder.iv_gallery_image);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.iv_gallery_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    IntentGenerator.startFullImageActivity(context, stringArrayList.getString(position));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return rootView;
    }

    public class ViewHolder {
        ImageView iv_gallery_image;
        ImageView iv_gallery_fav;
    }
}
