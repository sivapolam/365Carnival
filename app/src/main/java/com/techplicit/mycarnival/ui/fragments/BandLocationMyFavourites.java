package com.techplicit.mycarnival.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.adapters.BandLocationSortedGridAdapter;
import com.techplicit.mycarnival.data.model.FavouritesPojo;
import com.techplicit.mycarnival.data.model.SortedDistanceBandsPojo;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.Utils;

import java.util.ArrayList;

/**
 * Created by pnaganjane001 on 18/12/15.
 */
public class BandLocationMyFavourites extends Fragment implements Constants {

    private static final String TAG = BandLocationMyFavourites.class.getName();
    private static ListView carnivalsList;
    private ProgressBar carnivalsProgress;
    private TextView emptyText;
    ArrayList<SortedDistanceBandsPojo> sortedDistanceBandsPojosList = new ArrayList<SortedDistanceBandsPojo>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_band_location_ist, container, false);
        carnivalsList = (ListView) rootView.findViewById(R.id.grid_carnivals);
        carnivalsProgress = (ProgressBar) rootView.findViewById(R.id.progress_carnivals_list);
        emptyText = (TextView) rootView.findViewById(R.id.emptyText);
        FavouritesPojo favouritesPojo = Utils.getFavourites();
        if (favouritesPojo != null && favouritesPojo.getStringBandLocationPojoHashMap() != null && favouritesPojo.getStringBandLocationPojoHashMap().values() != null) {
            sortedDistanceBandsPojosList.clear();
            sortedDistanceBandsPojosList.addAll(favouritesPojo.getStringBandLocationPojoHashMap().values());
        }

        if (sortedDistanceBandsPojosList.size() > 0) {
            carnivalsList.setAdapter(new BandLocationSortedGridAdapter(getActivity(), sortedDistanceBandsPojosList));
            carnivalsProgress.setVisibility(View.GONE);
            carnivalsList.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        } else {
            carnivalsProgress.setVisibility(View.GONE);
            carnivalsList.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText("No Bands");
        }


        carnivalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ArrayList<SortedDistanceBandsPojo> carnivalsPojoArrayList = CarnivalsSingleton.getInstance().getSortedDistanceBandsPojoArrayList();

                SortedDistanceBandsPojo carnivalsPojo = (SortedDistanceBandsPojo) sortedDistanceBandsPojosList.get(position);

                String bandName = carnivalsPojo.getName();
                String address = carnivalsPojo.getAddress();
                String latitude = carnivalsPojo.getLatitude();
                String longitude = carnivalsPojo.getLongitude();

                if (bandName != null && address != null && latitude != null && longitude != null) {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SELECTED_BAND_NAME, bandName);
                    editor.putString(SELECTED_BAND_ADDRESS, address);
                    editor.putString(SELECTED_BAND_LATITUDE, latitude);
                    editor.putString(SELECTED_BAND_LONGITUDE, longitude);
                    editor.putString(UPDATE_LOCATION_FROM, FROM_BANDS_LIST);
                    editor.commit();
                }

                if (carnivalsPojo.getActiveFlag()) {
//                    IntentGenerator.startUpdateBandLocation(getActivity().getApplicationContext(), position, carnivalsPojo.getName());
                } else {

                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
