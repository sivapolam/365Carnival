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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.techplicit.mycarnival.GetFriendsLocationsListener;
import com.techplicit.mycarnival.GpsSettingsDialogListener;
import com.techplicit.mycarnival.PrivacyListener;
import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.data.CarnivalsSingleton;
import com.techplicit.mycarnival.data.ServiceHandler;
import com.techplicit.mycarnival.data.model.FriendsLocationsPojo;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by pnaganjane001 on 25/12/15.
 */
public class WhereAreMyFriends extends Fragment implements Constants, GetFriendsLocationsListener, LocationListener, PrivacyListener {
    private static final String TAG = WhereAreMyFriends.class.getName();
    View v;
    private static HashMap<Marker, FriendsLocationsPojo> mMarkersHashMap;
    private static GoogleMap mMap;
    private static View mapView;
    private ArrayList<FriendsLocationsPojo> friendsLocationsPojoArrayList;

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
    private static SharedPreferences sharedPreferences;
    private boolean isLocationUpdated;
    private ProgressBar carnivalsProgress;
    private AlertDialog changePassDialog;
    private GpsSettingsDialogListener mCallbackListener;

    public static int deviceWidth = 0, deviceHeight = 0;
    public static TextView textNoFriendsLocations;

    private static final int MY_PERMISSIONS_LOCATION = 1;
    private static RelativeLayout layout_privacy_alert;

    LatLngBounds.Builder builder1 = new LatLngBounds.Builder();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (v == null) {
            v = inflater.inflate(R.layout.fragment_where_are_my_friends, container, false);
            textNoFriendsLocations = (TextView) v.findViewById(R.id.text_no_friends_location);
            layout_privacy_alert = (RelativeLayout) v.findViewById(R.id.layout_privacy_alert);
            mMarkersHashMap = new HashMap<Marker, FriendsLocationsPojo>();
            ImageView back_Arrow = (ImageView) v.findViewById(R.id.back_arrow_carnivals_list);
            back_Arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });

            Button updateLocation = (Button) v.findViewById(R.id.update_my_location_btn);
            updateLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isPrivacyModeEnabled = sharedPreferences.getBoolean("isPrivacyModeEnabled", false);
                    if (isPrivacyModeEnabled) {
                        updateUserLocation(true, 0.0, 0.0);
                    } else {
//                        latitude = 36.778261;
//                        longitude = -119.417932;
                        if (latitude != 0.0 && longitude != 0.0) {
                            updateUserLocation(false, latitude, longitude);
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "Problem in getting your location", Toast.LENGTH_LONG).show();
                        }
                    }

                }
            });

        }

        sharedPreferences = getActivity().getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);

        setUpMap(getActivity());

        if (Utility.isNetworkConnectionAvailable(getActivity())) {
            getFriendsLocations(getActivity());
        } else {
            Utility.displayNetworkFailDialog(getActivity(), NETWORK_FAIL, "Success", "Successfully Invited !");
        }
        return v;
    }

    public void updateUserLocation(boolean privacyMode, Double lat, Double lng) {

        try {
            JSONObject object = new JSONObject();
            JSONObject objectLocation = new JSONObject();
            object.put("email", sharedPreferences.getString(EMAIL, null));
            objectLocation.put("lattitude", lat);
            objectLocation.put("longitude", lng);
            object.put("location", objectLocation);

            Log.e(TAG, "object location:"+object);

            new UpdateUserLocation(getActivity(), object).execute();

        } catch (Exception e) {
            Log.e(TAG, "UpdateUserLocation object error -> " + e.toString());
        }

    }

    private void setUpMap(Activity activtiy) {
        // Do a null check to confirm that we have not already instantiated the map.

        if (mMap != null) {
            mMap = null;
        }

        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((MapFragment) activtiy.getFragmentManager().findFragmentById(R.id.map_view)).getMap();
            mMap.setMyLocationEnabled(true);
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {

                }
            });

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

    private void plotMarkers(String lat, String lng, String name, Activity context) {

        List<Marker> markers = new ArrayList<Marker>();
        if (CarnivalsSingleton.getInstance().getFriendsLocationsPojoArrayList() != null && CarnivalsSingleton.getInstance().getFriendsLocationsPojoArrayList().size() > 0) {

            if (mMap != null) {
                mMap.clear();

                for (int i = 0; i < CarnivalsSingleton.getInstance().getFriendsLocationsPojoArrayList().size(); i++) {

                    FriendsLocationsPojo pojo = CarnivalsSingleton.getInstance().getFriendsLocationsPojoArrayList().get(i);

                    // Create user marker with custom icon and other options
//                    MarkerOptions markerOption = new MarkerOptions().position(new LatLng(pojo.getLatitude(), pojo.getLongitude()));

                    if (mMap != null && pojo != null) {

                        Log.e(TAG, "pojo.getLatitude(): "+pojo.getLatitude());
                        Log.e(TAG, "pojo.getLongitude(): "+pojo.getLongitude());

                        /*// create marker
                        MarkerOptions marker = new MarkerOptions().position(new LatLng(pojo.getLatitude(), pojo.getLongitude())).title(pojo.getfName()+" "+pojo.getlName());

                        // adding marker
                        mMap.addMarker(marker);*/


                        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(pojo.getLatitude(), pojo.getLongitude()))
                                .title(pojo.getfName() + " "+pojo.getlName()));
//                                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(pojo.getFriendImageBitmap(), 150, 150)))); //...
//                        marker.showInfoWindow();
                        markers.add(marker);

                        mMarkersHashMap.put(marker, pojo);

//                        mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter(name, context));
                    }

                }

                for (Marker marker1 : markers) {
                    builder1.include(marker1.getPosition());
                }

                if (latitude!=0.0 && longitude!=0.0) {
                    builder1.include(new LatLng(latitude, longitude));
                }

                LatLngBounds bounds = builder1.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 300, 300, 0);

                if (mMap != null) {
                    mMap.animateCamera(cu);
                    mMap.setMyLocationEnabled(true);
                }

            }
        }
    }


    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
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

            TextView markerLabel = (TextView) v.findViewById(R.id.marker_text);

            FriendsLocationsPojo friendsLocationsPojo = mMarkersHashMap.get(marker);

            markerLabel.setText(friendsLocationsPojo.getfName() + " " + friendsLocationsPojo.getlName());

            return v;
        }
    }
    public Bitmap resizeMapIcons(Bitmap imageBitmap, int width, int height) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMap != null) {
            mMap = null;
        }
    }

    public class GetFriendsLocationsAsync extends AsyncTask<String, String, String> {

        private final JSONObject object;
        ServiceHandler jsonParser = new ServiceHandler();
        private Activity mContext;
        private String responseStatus, inviteEmail;
        ProgressDialog pd;
        private String inviteStatus;

        public GetFriendsLocationsAsync(Activity context, ProgressBar carnivalsProgress, JSONObject object) {
            mContext = context;
            this.object = object;
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mContext);
            pd.setMessage("Loading Friends Locations");
            pd.show();
        }

        @Override
        protected String doInBackground(String... args) {
            friendsLocationsPojoArrayList = new ArrayList<FriendsLocationsPojo>();

            try {
                responseStatus = jsonParser.makePostRequest(
                        GET_FRIENDS_LOCATION_URL, object);
                Log.e(TAG, "FriendsLocations responseStatus: "+responseStatus);
                return responseStatus;
            } catch (Exception e) {
                e.printStackTrace();
                responseStatus = ERROR;
            }

            return null;
        }

        protected void onPostExecute(String responseStatus) {

            if (pd != null) {
                pd.dismiss();
            }

            try {
                if (responseStatus != null && !responseStatus.equals(ERROR)) {

                    JSONObject jsonObject = new JSONObject(responseStatus);
                    inviteStatus = jsonObject.optString(STATUS_INVITE);
                    if (inviteStatus != null) {

                        if (!inviteStatus.equalsIgnoreCase("success")) {
                            JSONArray jsonArray = jsonObject.optJSONArray(EXPLANATION_INVITE);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);

                                inviteStatus = Utility.getErrorMessage(object);

                                Utility.displayNetworkFailDialog(mContext, STATUS, "Failed", inviteStatus);

                            }
                        } else {
                            JSONArray jsonArray = jsonObject.optJSONArray("data");
                            Double lat = 0.0, lng = 0.0;
                            if (jsonArray != null && jsonArray.length() > 0) {
                                textNoFriendsLocations.setVisibility(View.GONE);
                                ArrayList<FriendsLocationsPojo> pojoArrayList = pojoArrayList = new ArrayList<FriendsLocationsPojo>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.optJSONObject(i);
                                    if (jsonArray.optJSONObject(i) != null) {
                                        if (object.has(JsonMap.LOCATION)) {
                                            JSONObject locationObject = object.getJSONObject(JsonMap.LOCATION);
                                            lat = locationObject.optDouble(JsonMap.LATITUDE);
                                            lng = locationObject.optDouble(JsonMap.LONGITUDE);

                                        } else {
                                            lat = 0.0;
                                            lng = 0.0;
                                        }

                                        try {
                                            if (lat != 0.0 && lng != 0.0) {
                                                FriendsLocationsPojo pojo = new FriendsLocationsPojo(jsonArray.optJSONObject(i));
                                                pojoArrayList.add(pojo);
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }


                                }

                                CarnivalsSingleton.getInstance().setFriendsLocationsPojoArrayList(pojoArrayList);

                                if (CarnivalsSingleton.getInstance().getFriendsLocationsPojoArrayList() != null && CarnivalsSingleton.getInstance().getFriendsLocationsPojoArrayList().size() > 0) {
//                                    new GetFriendsImages(mContext).execute();
                                    plotMarkers(null, null, null, mContext);

                                } else {
                                    textNoFriendsLocations.setVisibility(View.VISIBLE);
                                    if (mMap!=null){
                                        mMap.setMyLocationEnabled(true);

                                        if (latitude != 0.0 && longitude != 0.0) {
                                            LatLng latLng = new LatLng(latitude, longitude);

                                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 1);
                                            mMap.animateCamera(cameraUpdate);
                                        }

                                        if (mMap != null) {
                                            mMap.clear();
                                        }
                                    }


                                }


                            } else {
                                textNoFriendsLocations.setVisibility(View.VISIBLE);
                                if (mMap!=null){
                                    mMap.clear();
                                    mMap.setMyLocationEnabled(true);

                                    if (latitude != 0.0 && longitude != 0.0) {
                                        LatLng latLng = new LatLng(latitude, longitude);

                                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 1);
                                        mMap.animateCamera(cameraUpdate);
                                    }
                                }


                            }

                        }

                    }

                } else {
                    Utility.displayNetworkFailDialog(mContext, ERROR, "", "");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public void getFriendsLocations(Activity activity) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(FRIENDS_LIST_EMAIL, sharedPreferences.getString(EMAIL, null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new GetFriendsLocationsAsync(activity, null, jsonObject).execute();
    }


    @Override
    public void onFriendsLocationListener(Activity activity, JSONObject object) {

        getFriendsLocations(activity);


    }

    private interface JsonMap {
        String ID = "id";
        String NAME = "name";
        String IMAGE = "image";
        String START_DATE = "startDate";
        String END_DATE = "endDate";
        String ACTIVE_FLAG = "activeFlag";
        String ADDRESS = "lastUpdated";


        String LATITUDE = "lattitude";
        String LONGITUDE = "longitude";

        String LAST_UPDATED = "lastUpdated";

        String LOCATION = "location";

        String EMAIL = "email";
        String STATUS = "status";
        String FIRST_NAME = "firstName";
        String LAST_NAME = "lastName";
        String FRIEND_IMAGE = "image";

        String FRIEND_REQUEST_STATUS = "requestStatus";
        String DATA = "data";
    }

    class GetFriendsImages extends AsyncTask<Void, Void, Void> {

        Activity context;

        public GetFriendsImages(Activity mContext) {
            context = mContext;
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (CarnivalsSingleton.getInstance().getFriendsLocationsPojoArrayList() != null && CarnivalsSingleton.getInstance().getFriendsLocationsPojoArrayList().size() > 0) {
                for (int i = 0; i < CarnivalsSingleton.getInstance().getFriendsLocationsPojoArrayList().size(); i++) {

                    FriendsLocationsPojo pojo = CarnivalsSingleton.getInstance().getFriendsLocationsPojoArrayList().get(i);
                    //constants
                    URL url = null;
                    try {
                        url = new URL(pojo.getImage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (bitmap != null) {
                        pojo.setFriendImageBitmap(bitmap);
                    }

                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            plotMarkers(null, null, null, context);

        }
    }

    class UpdateUserLocation extends AsyncTask<String, String, String> implements Constants {

        ServiceHandler jsonParser = new ServiceHandler();

        private Activity mContext;
        private String responseStatus;
        private JSONObject object;
        private String inviteStatus;
        private ProgressDialog pd;

        public UpdateUserLocation(Activity context, JSONObject jsonObject) {
            mContext = context;
            object = jsonObject;
            sharedPreferences = mContext.getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
        }


        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mContext);
            pd.setMessage("Updating your location");
            pd.show();

        }

        @Override
        protected String doInBackground(String... args) {

            try {
                responseStatus = jsonParser.makePUTRequest(
                        UPDATE_USER_LOCATION_URL, object);
                return responseStatus;


            } catch (Exception e) {
                e.printStackTrace();
                responseStatus = ERROR;
                return responseStatus;
            }

        }

        protected void onPostExecute(String responseStatus) {

            if (pd != null) {
                pd.dismiss();
            }

            if (responseStatus != null) {

                if (responseStatus != null && !responseStatus.equals(ERROR)) {

                    try {
                        JSONObject jsonObject = new JSONObject(responseStatus);
                        inviteStatus = jsonObject.optString(STATUS_INVITE);
                        if (inviteStatus != null) {
                            if (!inviteStatus.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = jsonObject.optJSONArray(EXPLANATION_INVITE);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    inviteStatus = Utility.getErrorMessage(object);

                                    Utility.displayNetworkFailDialog(mContext, STATUS, "Error", inviteStatus);
                                }
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "Successfully Updated!", Toast.LENGTH_LONG).show();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    Utility.displayNetworkFailDialog(mContext, ERROR, "", "");
                }
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
            locationManager.removeUpdates(WhereAreMyFriends.this);
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            /*if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 16));
            }*/
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

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);

        String privacyMode = sharedPreferences.getString(USER_PRIVACY, null);

        if (privacyMode.equalsIgnoreCase("ON")) {
            layout_privacy_alert.setVisibility(View.VISIBLE);
        } else {
            layout_privacy_alert.setVisibility(View.GONE);
        }

        if (changePassDialog != null && changePassDialog.isShowing()) {
            changePassDialog.dismiss();
        }

        if (!isGPSEnabled && !isNetworkEnabled) {
            getLocation(getActivity());
        }

    }

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
    public void privacyListener(boolean status) {
        if (status) {
            layout_privacy_alert.setVisibility(View.VISIBLE);
        } else {
            layout_privacy_alert.setVisibility(View.GONE);
        }
    }

}
