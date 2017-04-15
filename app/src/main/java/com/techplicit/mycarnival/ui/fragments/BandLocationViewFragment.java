package com.techplicit.mycarnival.ui.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.techplicit.mycarnival.GpsSettingsDialogListener;
import com.techplicit.mycarnival.OnBandLocationUpdateListener;
import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.data.CarnivalsSingleton;
import com.techplicit.mycarnival.data.ServiceHandler;
import com.techplicit.mycarnival.data.model.BandLocationPojo;
import com.techplicit.mycarnival.data.model.SortedDistanceBandsPojo;
import com.techplicit.mycarnival.ui.activities.BandTabsActivity;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.Utility;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by pnaganjane001 on 18/12/15.
 */
public class BandLocationViewFragment extends Fragment implements Constants, LocationListener, OnBandLocationUpdateListener {

    private static final String TAG = BandLocationViewFragment.class.getName();
    private static final int MY_PERMISSIONS_LOCATION = 1;
    private static GoogleMap mMap;
    private static HashMap<Marker, SortedDistanceBandsPojo> mMarkersHashMap;
    private static LatLngBounds.Builder builder;
    private static LatLngBounds bounds;
    private static Marker currentMarker;
    private static Marker marker;

    static List<Marker> markers;
    private static LatLng Leicester_Square;
    private static LatLngBounds boundsLatLng;
    private static CameraUpdate cu;
    private static LatLngBounds.Builder builder1;
    View v = null;

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
    private static ArrayList<Integer> updatedViewsPositionsList = new ArrayList<Integer>();
    private static SharedPreferences sharedPreferences;
    private boolean isLocationUpdated;
    private ProgressBar carnivalsProgress;
    private AlertDialog changePassDialog;
    private GpsSettingsDialogListener mCallbackListener;
    private static View mapView;
    public static int deviceWidth = 0, deviceHeight = 0;
    private ScheduledFuture<?> scheduledFuture;
    private PowerManager.WakeLock wakeLock;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (v == null) {
            v = inflater.inflate(R.layout.bands_view_fragment, container, false);
            mMarkersHashMap = new HashMap<Marker, SortedDistanceBandsPojo>();
        }

        setUpMap(getActivity());

        sharedPreferences = getActivity().getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
        isLocationUpdated = sharedPreferences.getBoolean(IS_VIEW_NEEDS_TO_LOAD, false);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_BANDS_VIEW_VISIBLE, true);
        editor.commit();

        deviceWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        deviceHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();

        deviceWidth = deviceWidth - ((deviceWidth * 10) / 100);
        deviceHeight = deviceHeight - ((deviceHeight * 10) / 100);

        try {
            startUpdates();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "startUpdates Exception--> " + e.toString());
        }

        Utility.preventLockScreen(getActivity());

        return v;
    }

    private void preventLockScreen() {
        PowerManager powerManager = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
        wakeLock.acquire();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            if (Utility.wakeLock != null) {
                Utility.wakeLock.release();
            }
        } catch (Exception e) {
            Log.e(TAG, "error wakelock--> " + e.toString());
        }

    }


    public void startUpdates() throws InterruptedException {

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

        Runnable r = new Runnable() {
            @Override
            public void run() {
                System.out.println("Hello");

                getBandsLocations();
            }
        };

        scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(r, 0, 1, TimeUnit.MINUTES);

    }

    public void getBandsLocations() {
        if (Utility.isNetworkConnectionAvailable(getActivity())) {

            new GetAsync(getActivity(), null).execute();

        } else {
            Utility.displayNetworkFailDialog(getActivity(), NETWORK_FAIL, "Success", "Successfully Invited !");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MapsInitializer.initialize(getActivity());

    }

    private static void setUpMap(Activity activtiy) {
        // Do a null check to confirm that we have not already instantiated the map.

        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((MapFragment) activtiy.getFragmentManager().findFragmentById(R.id.map_view)).getMap();


            try {
                mapView = activtiy.getFragmentManager().findFragmentById(R.id.map_view).getView();
            } catch (Exception e) {
                Log.e(TAG, "Error while initializing mapView");
            }


            // Check if we were successful in obtaining the map.

            if (mMap != null) {
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
                        marker.showInfoWindow();
                        return true;
                    }
                });
            } else {
                Toast.makeText(activtiy, "Unable to create Maps", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private static void plotMarkers(String lat, String lng, String name, Activity context) {

        if (CarnivalsSingleton.getInstance().getSortedDistanceBandsPojoArrayList() != null && CarnivalsSingleton.getInstance().getSortedDistanceBandsPojoArrayList().size() > 0) {

            if (mMap != null) {
                if (updatedViewsPositionsList != null && updatedViewsPositionsList.size() > 0) {

                    for (int i = 0; i < updatedViewsPositionsList.size(); i++) {
                        int bandPos = updatedViewsPositionsList.get(i);

                        if (markers != null && markers.size() > 0 && bandPos > -1 && mMap != null) {
                            Marker marker = markers.get(bandPos);
                            marker.remove();

                            SortedDistanceBandsPojo pojo = CarnivalsSingleton.getInstance().getSortedDistanceBandsPojoArrayList().get(bandPos);

                            Marker markeradd = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(pojo.getLatitude()), Double.parseDouble(pojo.getLongitude()))));

                            markers.set(bandPos, markeradd);
                            mMarkersHashMap.put(markeradd, pojo);

                        }
                    }

                } else {
                    mMap.clear();

                    markers = new ArrayList<Marker>();

                    for (int i = 0; i < CarnivalsSingleton.getInstance().getSortedDistanceBandsPojoArrayList().size(); i++) {

                        SortedDistanceBandsPojo pojo = CarnivalsSingleton.getInstance().getSortedDistanceBandsPojoArrayList().get(i);

                        // Create user marker with custom icon and other options
                        MarkerOptions markerOption = new MarkerOptions().position(new LatLng(Double.parseDouble(pojo.getLatitude()), Double.parseDouble(pojo.getLongitude())));
                        markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));

                        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(pojo.getLatitude()), Double.parseDouble(pojo.getLongitude())))); //...
                        markers.add(marker);

                        mMarkersHashMap.put(marker, pojo);

                        mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter(name, context));

                    }

                    builder1 = new LatLngBounds.Builder();

                    for (Marker marker1 : markers) {
                        builder1.include(marker1.getPosition());
                    }

                    bounds = builder1.build();
                    cu = CameraUpdateFactory.newLatLngBounds(bounds, 300, 300, 10);

                    mMap.animateCamera(cu);
                    mMap.setMyLocationEnabled(true);
                }


            }
        }
    }

    @Override
    public void onBandLocationUpdateListener(int position, Double lat, Double lng) {
        if (markers != null && markers.size() > 0 && position > -1 && mMap != null) {
            Marker marker = markers.get(position);
            marker.remove();
            Marker markeradd = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
            SortedDistanceBandsPojo pojo = CarnivalsSingleton.getInstance().getSortedDistanceBandsPojoArrayList().get(position);
            markers.set(position, markeradd);
            mMarkersHashMap.put(markeradd, pojo);

            if (builder1 != null && markeradd != null) {
                builder1.include(markeradd.getPosition());
                bounds = builder1.build();

                cu = CameraUpdateFactory.newLatLngBounds(bounds, 300, 300, 10);

            }

        }

    }

    public static class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private String mName;
        private Activity mContext;

        public MarkerInfoWindowAdapter(String name, Activity context) {
            mName = name;
            mContext = context;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View v = mContext.getLayoutInflater().inflate(R.layout.custom_marker_icon, null);

            ImageView markerIcon = (ImageView) v.findViewById(R.id.marker_icon);

            TextView markerLabel = (TextView) v.findViewById(R.id.marker_text);

            SortedDistanceBandsPojo bandsPojo = mMarkersHashMap.get(marker);

            markerLabel.setText(bandsPojo.getName());

            return v;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
            scheduledFuture = null;
        }

        if (mMap != null) {
            mMap = null;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_BANDS_VIEW_VISIBLE, false);
        editor.commit();


    }

    static class GetAsync extends AsyncTask<String, String, JSONArray> {

        ServiceHandler jsonParser = new ServiceHandler();

        private static final String BANDS_URL = Constants.BASE_URL + "getbandslocationoverview?carnival=";

        private Activity mContext;
        private String responseStatus;

        public GetAsync(Activity context, ProgressBar carnivalsProgress) {
            mContext = context;
            Log.e(TAG, "GetAsync called");

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONArray doInBackground(String... args) {

            try {

                SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
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
                if (CarnivalsSingleton.getInstance().getBandsPojoArrayList() != null) {
                    CarnivalsSingleton.getInstance().setBandsJsonResponse(jsonArray);

                    if (CarnivalsSingleton.getInstance().getBandsPojoArrayList() != null) {
                        quizModelArrayList = CarnivalsSingleton.getInstance().getBandsPojoArrayList();

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

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(IS_VIEW_NEEDS_TO_LOAD, false);
                        editor.commit();

                        plotMarkers(null, null, null, mContext);

                    }
                } else {
                    try {

                        CarnivalsSingleton.getInstance().setBandsJsonResponse(jsonArray);

                        if (CarnivalsSingleton.getInstance().getBandsPojoArrayList() != null) {
                            quizModelArrayList = CarnivalsSingleton.getInstance().getBandsPojoArrayList();

                            ArrayList<SortedDistanceBandsPojo> sortedDistanceBandsPojosList = new ArrayList<SortedDistanceBandsPojo>();

                            for (BandLocationPojo pojo : quizModelArrayList) {
                                for (int i = 0; i < quizModelArrayList.size(); i++) {
                                    SortedDistanceBandsPojo sortedDistanceBandsPojo = CarnivalsSingleton.getInstance().getSortedDistanceBandsPojoArrayList().get(i);
                                    if (sortedDistanceBandsPojo.getName().equalsIgnoreCase(pojo.getName())) {
                                        if (!sortedDistanceBandsPojo.getLatitude().equalsIgnoreCase(pojo.getLatitude())) {
                                            updatedViewsPositionsList.add(i);
                                        }
                                    }
                                }

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

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(IS_VIEW_NEEDS_TO_LOAD, false);
                            editor.commit();

                            plotMarkers(null, null, null, mContext);

                        }

                    } catch (Exception e) {

                    }
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
            locationManager.removeUpdates(BandLocationViewFragment.this);
        }
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

        isLocationUpdated = sharedPreferences.getBoolean(IS_VIEW_NEEDS_TO_LOAD, false);

        if (isLocationUpdated) {
            if (Utility.isNetworkConnectionAvailable(getActivity())) {
                new GetAsync(getActivity(), carnivalsProgress).execute();
            } else {
//                Utility.displayNetworkFailDialog(getActivity(), NETWORK_FAIL, "Success", "Successfully Invited !");
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATION: {
                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!isGPSEnabled && !isNetworkEnabled) {
//                        getLocation(getActivity());
                    }

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
        try {
            mCallbackListener = (BandTabsActivity) context;
        } catch (Exception e) {
            Log.e(TAG, BandTabsActivity.class.getName() + " has to implement " + GpsSettingsDialogListener.class.getName());
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

}
