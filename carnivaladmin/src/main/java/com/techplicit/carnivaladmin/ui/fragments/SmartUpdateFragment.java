package com.techplicit.carnivaladmin.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.techplicit.carnivaladmin.R;
import com.techplicit.carnivaladmin.interfaces.IFragmentListener;
import com.techplicit.carnivaladmin.ui.activity.MainActivity;
import com.techplicit.carnivalcommons.SmartUpdateService;
import com.techplicit.carnivalcommons.data.BandLocationPojo;
import com.techplicit.carnivalcommons.utils.BandsDateFormatsConverter;
import com.techplicit.carnivalcommons.utils.Constants;
import com.techplicit.carnivalcommons.utils.UtilityCommon;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import de.greenrobot.event.EventBus;

/**
 * Created by pnaganjane001 on 3/25/17.
 */

public class SmartUpdateFragment extends Fragment implements IFragmentListener, Constants {

    private static final String LOG_TAG = SmartUpdateFragment.class.getSimpleName();
    private static final String UPDATE_LOCATION = "update_location";

    private MainActivity mActivity;
    private Spinner timeSpinner;
    private Spinner updatesSpinner;
    private TextView mSelectBandText;
    private TextView updateLocation;
    private String bandNameSelected;

    String[] minutesArray = new String[]{
            "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20"
    };

    String[] minutesArray999 = new String[]{
            "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "999"
    };
    private ImageView updateLocationBtn;
    private String mBandNameSelected;
    private ScheduledFuture<?> scheduledFuture;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_smart_update, container, false);

        mActivity = (MainActivity) getActivity();
        mBandNameSelected = mActivity.mBandNameSelected;

        initViews(rootView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        onEvent(null);
    }

    private void initViews(View rootView) {
        RelativeLayout layout_select_band = (RelativeLayout) rootView.findViewById(R.id.layout_select_band);
        timeSpinner = (Spinner) rootView.findViewById(R.id.spinner_time);
        updatesSpinner = (Spinner) rootView.findViewById(R.id.spinner_update);

        mSelectBandText = (TextView) rootView.findViewById(R.id.text_select_band);
        updateLocation = (TextView) rootView.findViewById(R.id.update_location);
        updateLocationBtn = (ImageView) rootView.findViewById(R.id.start_smart_update);

//        mSelectBandText.setText("Yourself");
//        bandNameSelected = "Yourself";

        /*try {
            startUpdates();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        layout_select_band.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.displayDurationDialog(getActivity(), MainActivity.SMART_UPDATE);
            }
        });

        RelativeLayout layout_select_time = (RelativeLayout) rootView.findViewById(R.id.layout_select_time);

        layout_select_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeSpinner.performClick();
            }
        });

        RelativeLayout layout_update_select = (RelativeLayout) rootView.findViewById(R.id.layout_update_select);

        layout_update_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatesSpinner.performClick();
            }
        });

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_layout_smart_update, minutesArray);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_smart_update);

        // attaching data adapter to spinner
        timeSpinner.setAdapter(dataAdapter);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(getActivity(), R.layout.spinner_layout_smart_update, minutesArray999);

        // Drop down layout style - list view with radio button
        dataAdapter1.setDropDownViewResource(R.layout.spinner_dropdown_smart_update);


        updatesSpinner.setAdapter(dataAdapter1);

        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String minutes = minutesArray[position];
                saveUpdateIntervalToPrefs(MINUTES_INTERVAL_SMART_UPDATE, minutes);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                String minutes = minutesArray[0];
                saveUpdateIntervalToPrefs(MINUTES_INTERVAL_SMART_UPDATE, minutes);
            }

        });

        updatesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String updateIntervals = minutesArray999[position];
                saveUpdateIntervalToPrefs(UPDATES_INTERVAL_SMART_UPDATE, updateIntervals);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                String updateIntervals = minutesArray999[0];
                saveUpdateIntervalToPrefs(UPDATES_INTERVAL_SMART_UPDATE, updateIntervals);
            }

        });

        updateLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSmartUpdated = mActivity.mSharedPreferences.getBoolean(IS_SMART_UPDATED, true);
                if (isSmartUpdated) {
                    if (!mSelectBandText.getText().toString().trim().equalsIgnoreCase(getActivity().getResources().getString(R.string.select_band))) {
                        if (!UtilityCommon.isNetworkConnectionAvailable(getActivity())) {
                            UtilityCommon.displayNetworkFailDialog(getActivity(), NETWORK_FAIL);
                        } else if (mActivity.isLocationAvailable()) {
                            SharedPreferences.Editor editor = mActivity.mSharedPreferences.edit();
                            Log.e(LOG_TAG, "mBandNameSelected-->"+mBandNameSelected);
                            editor.putString(CARNIVAL_NAME_SMART_UPDATE, mActivity.selectedCarnivalName);
                            editor.putString(BAND_NAME_SMART_UPDATE, mBandNameSelected);
                            editor.putString(SELECTED_BAND_LATITUDE, Double.toString(mActivity.mLatitude));
                            editor.putString(SELECTED_BAND_LONGITUDE, Double.toString(mActivity.mLongitude));

                            List<String> listBands = mActivity.mListBands;
                            if (listBands != null && !listBands.isEmpty()) {
                                int pos = listBands.indexOf(mBandNameSelected);
                                editor.putInt(CARNIVAL_POSITION_SMART_UPDATE, pos);
                            }

                            editor.apply();

                            startSmartUpdateService(getActivity());
                            Toast.makeText(getActivity(), "We will update locations for you", Toast.LENGTH_LONG).show();

                        } else {
                            mActivity.getCurrentLocation(mActivity);
                            if (ActivityCompat.checkSelfPermission(getActivity(),
                                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(getActivity(), "Problem in Fetching Location... Try Again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "Please select Band!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Previous Updates are in progress...", Toast.LENGTH_LONG).show();
                }

            }
        });

    }


    private String getLastUpdateTime() {
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        Calendar c = Calendar.getInstance();
        String currentDate = ft.format(c.getTime());
        String lastAccessOn = mActivity.mSharedPreferences.getString(LAST_UPDATE_ADMIN, "");
        Date d1 = new Date();
        Date d2 = new Date();

        Log.e(LOG_TAG, "currentDate: "+currentDate);
        Log.e(LOG_TAG, "lastAccessOn: "+lastAccessOn);
        try {
            d1 = ft.parse(currentDate);
            d2 = ft.parse(lastAccessOn);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Exception: "+e.toString(), e);
            Log.e(LOG_TAG, "Exception: "+e.toString(), e);
        } catch (Exception e) {
        }

        // in milliseconds
        long diff = d1.getTime() - d2.getTime();

        return BandsDateFormatsConverter.printDateDifferenceInUIWithDefinedFormat(diff);
    }

    /**
     * Save Smart update intervals to Shared Preferences
     *
     * @param type  - Minutes/Updates
     * @param value - No. of intervals/minutes
     */
    private void saveUpdateIntervalToPrefs(String type, String value) {
        SharedPreferences.Editor editor = mActivity.mSharedPreferences.edit();
        editor.putString(type, value);
        editor.apply();
    }

    @Override
    public void updateSelectedBand(String message) {
        mSelectBandText.setText(message);
        mBandNameSelected = message;

        BandLocationPojo bandLocationPojo = mActivity.mBandLocationPojoList.get(mActivity.mSelectedBandPosition);
        Log.e(LOG_TAG, "bandLocationPojo.getLastUpdated()-->"+bandLocationPojo.getLastUpdated());
        if (bandLocationPojo != null && bandLocationPojo.getLastUpdated() != null) {
            String lastAccessOn = UtilityCommon.getDate(Long.valueOf(bandLocationPojo.getLastUpdated()), "dd-MM-yyyy HH:mm:ss");
            SharedPreferences.Editor editor = mActivity.mSharedPreferences.edit();
            editor.putString(LAST_UPDATE_ADMIN, lastAccessOn);
            editor.apply();

            String lastUpdateTime = getLastUpdateTime();
            Log.e(LOG_TAG, "lastUpdateTime: "+lastUpdateTime);
            if (UtilityCommon.isStringValid(lastUpdateTime)) {
                Log.e(LOG_TAG, "lastUpdateTime: Valid");
                mActivity.mScreenSubTitle.setText("Last Update : " + lastUpdateTime);
                mActivity.mScreenSubTitle.setVisibility(View.VISIBLE);
            }
        }

    }


    private void startSmartUpdateService(Context context) {
        Intent smartUpdateService = new Intent(context, SmartUpdateService.class);
        try {
            context.stopService(smartUpdateService);
        } catch (Exception e) {
            Log.e(LOG_TAG, "error: " + e.toString(), e);
        }

        context.startService(smartUpdateService);
    }

    public void onEvent(String timeStamp) {
        Log.e(LOG_TAG, "onEvent called");
        setSubTitle();
/*
        try {
            startUpdates();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    public void startUpdates() throws InterruptedException {

        Handler handler = new Handler();
        handler.postDelayed
                (new Runnable() {

                    @Override
                    public void run() {
                        Log.e(LOG_TAG, " Handler run()");
                        setSubTitle();
                    }
                }, 60000);

        /*ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

        Runnable r = new Runnable() {
            @Override
            public void run() {
                Log.e(LOG_TAG, " scheduledFuture run()");
                setSubTitle();
            }
        };

        scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(r, 0, 1, TimeUnit.MINUTES);*/
    }

    private void setSubTitle() {
        if (mActivity.mBandNameSelected != null
            && mActivity.mSharedPreferences.getString(LAST_UPDATE_ADMIN, null) != null) {
            String lastUpdateTime = getLastUpdateTime();
            Log.e(LOG_TAG, "lastUpdateTime: "+lastUpdateTime);
            if (UtilityCommon.isStringValid(lastUpdateTime)) {
                Log.e(LOG_TAG, "lastUpdateTime: Valid");
                mActivity.mScreenSubTitle.setText("Last Update : " + lastUpdateTime);
                mActivity.mScreenSubTitle.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
    }
}
