package com.techplicit.mycarnival.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.adapters.BandLocationGridAdapter;
import com.techplicit.mycarnival.data.CarnivalsSingleton;
import com.techplicit.mycarnival.data.ServiceHandler;
import com.techplicit.mycarnival.data.model.BandLocationPojo;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.Utility;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by pnaganjane001 on 18/12/15.
 */
public class BandLocationAlphaSortFragment extends Fragment implements Constants{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int MY_PERMISSIONS_LOCATION = 1;
    private static final String TAG = BandLocationAlphaSortFragment.class.getName();
    private static ListView carnivalsList;
    private static ArrayList<BandLocationPojo> quizModelArrayList;
    private static SharedPreferences sharedPreferences;
    private boolean isLocationUpdated;
    private ProgressBar carnivalsProgress;
    private TextView emptyText;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static BandLocationAlphaSortFragment newInstance(int sectionNumber) {
        BandLocationAlphaSortFragment fragment = new BandLocationAlphaSortFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public BandLocationAlphaSortFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_band_loc_list, container, false);
        carnivalsList = (ListView) rootView.findViewById(R.id.list_carnivals);
        carnivalsProgress = (ProgressBar) rootView.findViewById(R.id.progress_carnivals_list);
        emptyText = (TextView) rootView.findViewById(R.id.emptyText);
        carnivalsProgress.getIndeterminateDrawable().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY);
        emptyText.setVisibility(View.GONE);
        sharedPreferences = getActivity().getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
        isLocationUpdated = sharedPreferences.getBoolean(IS_ALPH_SORT_NEEDS_TO_LOAD, false);
        Typeface face= Typeface.createFromAsset(getActivity().getAssets(), "fonts/ftra_hv.ttf");

        emptyText.setTypeface(face);
        loadBandsData(emptyText, carnivalsProgress);

        emptyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyText.setVisibility(View.GONE);
                loadBandsData(emptyText, carnivalsProgress);
            }
        });

        carnivalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<BandLocationPojo> carnivalsPojoArrayList = CarnivalsSingleton.getInstance().getBandsPojoArrayList();
                BandLocationPojo carnivalsPojo = (BandLocationPojo) carnivalsPojoArrayList.get(position);

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
                }
            }
        });



        return rootView;
    }

    private void loadBandsData(TextView emptyText, ProgressBar carnivalsProgress) {
        if (Utility.isNetworkConnectionAvailable(getActivity())) {

            if (CarnivalsSingleton.getInstance().getBandsPojoArrayList()!=null){
                carnivalsProgress.setVisibility(View.GONE);
                if (CarnivalsSingleton.getInstance().getBandsPojoArrayList()!=null){
                    quizModelArrayList = CarnivalsSingleton.getInstance().getBandsPojoArrayList();

                    carnivalsList.setAdapter(new BandLocationGridAdapter(getActivity(),quizModelArrayList));
                    carnivalsProgress.setVisibility(View.GONE);
                    emptyText.setVisibility(View.GONE);
                }
            }else{
                new GetAsync(getActivity(), carnivalsProgress, emptyText).execute();
            }

        } else {
            carnivalsProgress.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText("No Network ! Please Connect \n and Tap to Try Again !!");

//            Utility.displayNetworkFailDialog(getActivity(), NETWORK_FAIL, "Success", "Successfully Invited !");

        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//            ((CarnivalsListActivity) activity).onSectionAttached(
//                    getArguments().getInt(ARG_SECTION_NUMBER));
    }


    static class GetAsync extends AsyncTask<String, String, JSONArray> {

        ServiceHandler jsonParser = new ServiceHandler();
        private ProgressBar carnivalsProgress;

        private static final String BANDS_URL = Constants.BASE_URL + "getbandslocationoverview?carnival=";

        private Activity mContext;
        private String responseStatus;
        private String selectedCarnivalNameTrimmed;
        private TextView emptyText;
        public GetAsync(Activity context, ProgressBar carnivalsProgress, TextView emptyText) {
            mContext = context;
            this.carnivalsProgress = carnivalsProgress;
            this.emptyText = emptyText;
        }

        @Override
        protected void onPreExecute() {
            this.carnivalsProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONArray doInBackground(String... args) {

            try {

                SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
                String selectedCarnivalName = sharedPreferences.getString(SELECTED_CARNIVAL_NAME, "");


                if (selectedCarnivalName.contains(" & ") || selectedCarnivalName.contains(" ")) {
                    selectedCarnivalNameTrimmed = selectedCarnivalName.replace(" & ", "+%26+").replace(" ", "%20").trim();
                }

                responseStatus = jsonParser.makeHttpRequest(
                        BANDS_URL + selectedCarnivalNameTrimmed, "GET", null);


                if (responseStatus != null && !responseStatus.equalsIgnoreCase(ERROR)) {
                    JSONArray jsonArray = null;

                    jsonArray = new JSONArray(responseStatus);

                    if (jsonArray != null) {

                        return jsonArray;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                responseStatus = ERROR;
            }

            return null;
        }

        protected void onPostExecute(JSONArray jsonArray) {

            if (responseStatus.equalsIgnoreCase(ERROR)) {
//                Utility.displayNetworkFailDialog(mContext, ERROR, "Success", "Successfully Invited !");
                this.carnivalsProgress.setVisibility(View.GONE);
                emptyText.setVisibility(View.VISIBLE);
                emptyText.setText(""+mContext.getResources().getString(R.string.error_message)+"\n Tap to try Again!!");
            }

            if (jsonArray != null) {

                CarnivalsSingleton.getInstance().setBandsJsonResponse(jsonArray);

                if (CarnivalsSingleton.getInstance().getBandsJsonResponse() != null) {
                    quizModelArrayList = CarnivalsSingleton.getInstance().getBandsPojoArrayList();

                    carnivalsList.setAdapter(new BandLocationGridAdapter(mContext, quizModelArrayList));
                    this.carnivalsProgress.setVisibility(View.GONE);
                    emptyText.setVisibility(View.GONE);
                }
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(IS_ALPH_SORT_NEEDS_TO_LOAD, false);
            editor.commit();

        }

    }


    @Override
    public void onResume() {
        super.onResume();

        isLocationUpdated = sharedPreferences.getBoolean(IS_ALPH_SORT_NEEDS_TO_LOAD, false);

        if (isLocationUpdated){
            if (Utility.isNetworkConnectionAvailable(getActivity())) {
                new GetAsync(getActivity(), carnivalsProgress, emptyText).execute();
            }else{
//                Utility.displayNetworkFailDialog(getActivity(), NETWORK_FAIL, "Success", "Successfully Invited !");
            }
        }

        try {
            if (Utility.wakeLock!=null){
                Utility.wakeLock.release();
            }
        }catch (Exception e){
            Log.e(TAG, "wakeLock error--> " + e.toString());
        }

    }

}
