package com.techplicit.mycarnival.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.techplicit.mycarnival.IntentGenerator;
import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.adapters.FetesAdapter;
import com.techplicit.mycarnival.apipresenter.ApiResponsePresenter;
import com.techplicit.mycarnival.data.model.BandLocationPojo;
import com.techplicit.mycarnival.data.model.FeteDetailModel;
import com.techplicit.mycarnival.interfaces.IRequestInterface;
import com.techplicit.mycarnival.interfaces.IResponseInterface;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.Utility;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by pnaganjane001 on 18/12/15.
 */
public class FetesAlphaSortFragment extends Fragment implements Constants, IResponseInterface {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int MY_PERMISSIONS_LOCATION = 1;
    private static final String TAG = FetesAlphaSortFragment.class.getName();
    private static GridView carnivalsList;
    private static ArrayList<BandLocationPojo> quizModelArrayList;
    private static SharedPreferences sharedPreferences;
    private boolean isLocationUpdated;
    private ProgressBar carnivalsProgress;
    private TextView emptyText;

    ArrayList<FeteDetailModel> data = null;
    FetesAdapter fetesAdapter;

    ApiResponsePresenter apiResponsePresenter;
    private static final String LOAD_BANDS = "load_bands";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FetesAlphaSortFragment newInstance(int sectionNumber) {
        FetesAlphaSortFragment fragment = new FetesAlphaSortFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public FetesAlphaSortFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bands_ist, container, false);
        carnivalsList = (GridView) rootView.findViewById(R.id.grid_carnivals);
        carnivalsProgress = (ProgressBar) rootView.findViewById(R.id.progress_carnivals_list);
        emptyText = (TextView) rootView.findViewById(R.id.emptyText);
        carnivalsProgress.getIndeterminateDrawable().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY);
        emptyText.setVisibility(View.GONE);
        sharedPreferences = getActivity().getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
        isLocationUpdated = sharedPreferences.getBoolean(IS_ALPH_SORT_NEEDS_TO_LOAD, false);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ftra_hv.ttf");

        emptyText.setTypeface(face);
        apiResponsePresenter = new ApiResponsePresenter(this);

        loadBands();
        emptyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyText.setVisibility(View.GONE);
                loadBands();
            }
        });

        carnivalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IntentGenerator.startFeteDetailActivity(getActivity(), data.get(position));
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//            ((CarnivalsListActivity) activity).onSectionAttached(
//                    getArguments().getInt(ARG_SECTION_NUMBER));
    }

    private void loadBands() {
        if (Utility.isNetworkConnectionAvailable(getActivity())) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
            String selectedCarnivalName = sharedPreferences.getString(SELECTED_CARNIVAL_NAME, "");
            //http://www.mocky.io/v2/5880e2662500003a05c9ed29
            try {
                apiResponsePresenter.callApi(Request.Method.GET, Constants.BASE_URL + "getfetes?carnival=" + URLEncoder.encode(selectedCarnivalName, "UTF-8"), null, LOAD_BANDS, IRequestInterface.REQUEST_TYPE_JSON_GET_ARRAY);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            carnivalsProgress.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText("No Network ! Please Connect \n and Tap to Try Again !!");
//            Utility.displayNetworkFailDialog(getActivity(), NETWORK_FAIL, "Success", "Successfully Invited !");
        }
    }


    @Override
    public void onResponseSuccess(String resp, String req) {
        Log.v("Fetes", "Bands Succ " + resp + "," + req);
        if (!TextUtils.isEmpty(req) && req.equalsIgnoreCase(LOAD_BANDS)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<FeteDetailModel>>() {
            }.getType();
            data = (ArrayList<FeteDetailModel>) gson.fromJson(resp, listType);
            fetesAdapter = new FetesAdapter(getActivity(), data);
            carnivalsList.setAdapter(fetesAdapter);
            carnivalsProgress.setVisibility(View.GONE);
            emptyText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResponseFailure(String req) {
        Log.v("Bands", "Bands Fail " + req);
        if (!TextUtils.isEmpty(req) && req.equalsIgnoreCase(LOAD_BANDS)) {
            carnivalsProgress.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText("Response Failed...");
        }
    }

    @Override
    public void onApiConnected(String req) {

    }

    public void notifyAdapter() {
        if (fetesAdapter != null)
            fetesAdapter.notifyDataSetChanged();
    }
}
