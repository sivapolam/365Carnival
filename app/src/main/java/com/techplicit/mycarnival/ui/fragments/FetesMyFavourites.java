package com.techplicit.mycarnival.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.techplicit.mycarnival.IntentGenerator;
import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.adapters.FetesFavAdapter;
import com.techplicit.mycarnival.data.model.FavouritesPojo;
import com.techplicit.mycarnival.data.model.FeteDetailModel;
import com.techplicit.mycarnival.interfaces.NoDataInterface;
import com.techplicit.mycarnival.utils.Utils;

import java.util.ArrayList;

/**
 * Created by pnaganjane001 on 18/12/15.
 */
public class FetesMyFavourites extends Fragment implements NoDataInterface {

    private static final String TAG = BandsMyFavourites.class.getName();
    private static GridView carnivalsList = null;
    private ProgressBar carnivalsProgress;
    private TextView emptyText;
    private FetesFavAdapter bandsAdapter;

    ArrayList<FeteDetailModel> data = new ArrayList<>();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        FavouritesPojo favouritesPojo = Utils.getFavourites();
        if (favouritesPojo != null && favouritesPojo.getStringFeteDetailModelHashMap() != null && favouritesPojo.getStringFeteDetailModelHashMap().values() != null) {
            data.clear();
            data.addAll(favouritesPojo.getStringFeteDetailModelHashMap().values());
            if (carnivalsList != null) {
                onResume();
            }
        } else {

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bands_ist, container, false);
        carnivalsList = (GridView) rootView.findViewById(R.id.grid_carnivals);
        carnivalsProgress = (ProgressBar) rootView.findViewById(R.id.progress_carnivals_list);
        emptyText = (TextView) rootView.findViewById(R.id.emptyText);
        bandsAdapter = new FetesFavAdapter(getActivity(), this, data);
        carnivalsList.setAdapter(bandsAdapter);

        carnivalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    IntentGenerator.startFeteDetailActivity(getActivity(), data.get(position));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (data != null && data.size() > 0) {
                bandsAdapter.notifyDataSetChanged();
                carnivalsProgress.setVisibility(View.GONE);
                carnivalsList.setVisibility(View.VISIBLE);
                emptyText.setVisibility(View.GONE);
            } else {
                noData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void noData() {
        if (carnivalsProgress != null) {
            carnivalsProgress.setVisibility(View.GONE);
            carnivalsList.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText("No Fetes");
        }
    }
}
