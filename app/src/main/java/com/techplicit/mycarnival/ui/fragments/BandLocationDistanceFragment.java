package com.techplicit.mycarnival.ui.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.techplicit.mycarnival.GpsSettingsDialogListener;
import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.adapters.BandLocationSortedGridAdapter;
import com.techplicit.mycarnival.data.CarnivalsSingleton;
import com.techplicit.mycarnival.data.ServiceHandler;
import com.techplicit.mycarnival.data.model.BandLocationPojo;
import com.techplicit.mycarnival.data.model.SortedDistanceBandsPojo;
import com.techplicit.mycarnival.ui.activities.BandTabsActivity;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.Utility;
import com.techplicit.mycarnival.utils.Utils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by pnaganjane001 on 18/12/15.
 */
public class BandLocationDistanceFragment extends Fragment implements Constants, android.location.LocationListener {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int MY_PERMISSIONS_LOCATION = 1;
    private static final String TAG = BandLocationDistanceFragment.class.getName();
    private static ListView carnivalsList;
    private ProgressDialog pDialog;
    private AlertDialog alertDialog;

    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;
    Location location; // location
    public static double latitude; // latitude
    public static double longitude; // longitude
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    // Declaring a Location Manager
    protected LocationManager locationManager;
    private static ArrayList<BandLocationPojo> quizModelArrayList;
    private static boolean isLocationUpdated = false;
    private ProgressBar carnivalsProgress;
    private static SharedPreferences sharedPreferences;
    private AlertDialog changePassDialog;
    private GpsSettingsDialogListener mCallbackListener;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static BandLocationDistanceFragment newInstance(int sectionNumber) {
        BandLocationDistanceFragment fragment = new BandLocationDistanceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public BandLocationDistanceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_band_loc_list, container, false);
        carnivalsList = (ListView) rootView.findViewById(R.id.list_carnivals);
        carnivalsProgress = (ProgressBar) rootView.findViewById(R.id.progress_carnivals_list);
        carnivalsProgress.getIndeterminateDrawable().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY);

        sharedPreferences = getActivity().getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
        isLocationUpdated = sharedPreferences.getBoolean(IS_DISATNCE_NEEDS_TO_LOAD, false);

        carnivalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<SortedDistanceBandsPojo> carnivalsPojoArrayList = CarnivalsSingleton.getInstance().getSortedDistanceBandsPojoArrayList();

                SortedDistanceBandsPojo carnivalsPojo = (SortedDistanceBandsPojo) carnivalsPojoArrayList.get(position);

                String bandName = carnivalsPojo.getName();
                String address = carnivalsPojo.getAddress();
                String latitude = carnivalsPojo.getLatitude();
                String longitude = carnivalsPojo.getLongitude();

                if (bandName != null && address != null && latitude != null && longitude != null) {
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

        if (Utility.isNetworkConnectionAvailable(getActivity())) {

//            if (isLocationUpdated){
//                new GetAsync(getActivity(), carnivalsProgress).execute();
//            }

            if (CarnivalsSingleton.getInstance().getBandsPojoArrayList() != null) {
                carnivalsProgress.setVisibility(View.GONE);

                if (CarnivalsSingleton.getInstance().getBandsPojoArrayList() != null) {
                    quizModelArrayList = CarnivalsSingleton.getInstance().getBandsPojoArrayList();

//                    CarnivalsSingleton.getInstance().setDistanceSortedBandsPojoArrayList(null);
                    ArrayList<SortedDistanceBandsPojo> sortedDistanceBandsPojosList = new ArrayList<SortedDistanceBandsPojo>();

                    for (BandLocationPojo pojo : quizModelArrayList) {
                        double distance = Utility.distance(latitude, longitude, Double.parseDouble(pojo.getLatitude()), Double.parseDouble(pojo.getLongitude()));

                        SortedDistanceBandsPojo pojoSorted = new SortedDistanceBandsPojo(null);
                        pojoSorted.setDistance(distance);

                        pojoSorted.setAddress(pojo.getAddress());
                        pojoSorted.setImage(pojo.getImage());
                        pojoSorted.setActiveFlag(pojo.getActiveFlag());
                        pojoSorted.setLastUpdated(pojo.getLastUpdated());
                        pojoSorted.setLatitude(pojo.getLatitude());
                        pojoSorted.setLongitude(pojo.getLongitude());
                        pojoSorted.setName(pojo.getName());
                        pojoSorted.setUpdates(pojo.getUpdates());

                        sortedDistanceBandsPojosList.add(pojoSorted);

                    }


                    Collections.sort(sortedDistanceBandsPojosList, new Comparator<SortedDistanceBandsPojo>() {
                        public int compare(SortedDistanceBandsPojo dc1, SortedDistanceBandsPojo dc2) {
                            return (int) (dc1.getDistance() - (dc2.getDistance()));
                        }
                    });

                    CarnivalsSingleton.getInstance().setDistanceSortedBandsPojoArrayList(sortedDistanceBandsPojosList);

                    carnivalsList.setAdapter(new BandLocationSortedGridAdapter(getActivity(), sortedDistanceBandsPojosList));
                    carnivalsProgress.setVisibility(View.GONE);
                }
            } else {
                new GetAsync(getActivity(), carnivalsProgress).execute();
            }


        } else {
            carnivalsProgress.setVisibility(View.VISIBLE);
            Utility.displayNetworkFailDialog(getActivity(), NETWORK_FAIL, "Success", "Successfully Invited !");

        }


        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbackListener = (BandTabsActivity) activity;
        } catch (Exception e) {
            Log.e(TAG, BandTabsActivity.class.getName() + " has to implement " + GpsSettingsDialogListener.class.getName());
        }
    }

    static class GetAsync extends AsyncTask<String, String, JSONArray> {

        ServiceHandler jsonParser = new ServiceHandler();
        private ProgressBar carnivalsProgress;

        private static final String BANDS_URL = Constants.BASE_URL + "getbandslocationoverview?carnival=";
        private Activity mContext;
        private String responseStatus;

        public GetAsync(Activity context, ProgressBar carnivalsProgress) {
            mContext = context;
            this.carnivalsProgress = carnivalsProgress;
        }

        @Override
        protected void onPreExecute() {
            this.carnivalsProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONArray doInBackground(String... args) {

            try {

                String selectedCarnivalName = sharedPreferences.getString(SELECTED_CARNIVAL_NAME, "");

                String selectedCarnivalNameTrimmed = null;

                if (selectedCarnivalName.contains(" & ")) {
                    selectedCarnivalNameTrimmed = selectedCarnivalName.replace(" & ", "+%26+").trim();
                } else if (selectedCarnivalName.contains(" ")) {
                    selectedCarnivalNameTrimmed = selectedCarnivalName.replace(" ", "%20").trim();
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
                Utility.displayNetworkFailDialog(mContext, ERROR, "Success", "Successfully Invited !");
            }

            if (jsonArray != null) {

                CarnivalsSingleton.getInstance().setBandsJsonResponse(jsonArray);

                if (CarnivalsSingleton.getInstance().getBandsPojoArrayList() != null) {
                    quizModelArrayList = CarnivalsSingleton.getInstance().getBandsPojoArrayList();

                    ArrayList<SortedDistanceBandsPojo> sortedDistanceBandsPojosList = new ArrayList<SortedDistanceBandsPojo>();
                    Utils.removeAllBandLocation();
                    for (BandLocationPojo pojo : quizModelArrayList) {
                        double distance = Utility.distance(latitude, longitude, Double.parseDouble(pojo.getLatitude()), Double.parseDouble(pojo.getLongitude()));

                        SortedDistanceBandsPojo pojoSorted = new SortedDistanceBandsPojo(null);
                        pojoSorted.setDistance(distance);
                        pojoSorted.setAddress(pojo.getAddress());
                        pojoSorted.setImage(pojo.getImage());
                        pojoSorted.setActiveFlag(pojo.getActiveFlag());
                        pojoSorted.setLastUpdated(pojo.getLastUpdated());
                        pojoSorted.setLatitude(pojo.getLatitude());
                        pojoSorted.setLongitude(pojo.getLongitude());
                        pojoSorted.setName(pojo.getName());
                        pojoSorted.setUpdates(pojo.getUpdates());
                        sortedDistanceBandsPojosList.add(pojoSorted);

                        if (Utils.isBandLocationFav(pojoSorted)) {
                            Utils.storeBandLocation(pojoSorted);
                        }

                    }


                    Collections.sort(sortedDistanceBandsPojosList, new Comparator<SortedDistanceBandsPojo>() {
                        public int compare(SortedDistanceBandsPojo dc1, SortedDistanceBandsPojo dc2) {
                            return (int) (dc1.getDistance() - (dc2.getDistance()));
                        }
                    });

                    CarnivalsSingleton.getInstance().setDistanceSortedBandsPojoArrayList(sortedDistanceBandsPojosList);

                    carnivalsList.setAdapter(new BandLocationSortedGridAdapter(mContext, sortedDistanceBandsPojosList));
                    carnivalsProgress.setVisibility(View.GONE);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(IS_DISATNCE_NEEDS_TO_LOAD, false);
                    editor.commit();

                }
            }

        }

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();

        }


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public Location getLocation(Activity activityContext) {
        try {

            locationManager = (LocationManager) activityContext
                    .getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled

            } else {

                this.canGetLocation = true;
                if (isNetworkEnabled) {

                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_LOCATION);

                        return null;
                    }

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {

                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Siva", "getlocation Error-- > " + e.toString());
        }

        return location;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void stopUsingGPS() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(BandLocationDistanceFragment.this);
        }
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getActivity().startActivity(intent);
                dialog.cancel();
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // Showing Alert Message
//        alertDialog.show();

        changePassDialog = alertDialog.create();

        try {
            changePassDialog.cancel();
        } catch (Exception e) {
            Log.e(TAG, "Problem with GPS Settings Alert --> " + e.toString());
        }

        changePassDialog.show();

    }


    @Override
    public void onResume() {
        super.onResume();

        if (changePassDialog != null && changePassDialog.isShowing()) {
            changePassDialog.dismiss();
        }


        if (!isGPSEnabled && !isNetworkEnabled) {
            getLocation(getActivity());
        }

        isLocationUpdated = sharedPreferences.getBoolean(IS_DISATNCE_NEEDS_TO_LOAD, false);

        if (isLocationUpdated) {
            new GetAsync(getActivity(), carnivalsProgress).execute();
        }

        try {
            if (Utility.wakeLock != null) {
                Utility.wakeLock.release();
            }
        } catch (Exception e) {
            Log.e(TAG, "wakeLock error--> " + e.toString());
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(this);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATION: {
                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        getLocation(getActivity());
    }


}
