package com.techplicit.mycarnival.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.adapters.GalleryAdapter;

import org.json.JSONArray;

/**
 * Created by pnaganjane001 on 18/12/15.
 */
public class BandGallery extends Fragment {

    private static final String TAG = BandGallery.class.getName();
    private static GridView gallery;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bands_ist, container, false);
        gallery = (GridView) rootView.findViewById(R.id.grid_carnivals);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void setData(JSONArray galleryArray) {
        gallery.setAdapter(new GalleryAdapter(getActivity(), galleryArray));
    }
}
