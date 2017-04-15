package com.techplicit.mycarnival.ui.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapsInitializer;
import com.techplicit.mycarnival.GetFriendsLocationsListener;
import com.techplicit.mycarnival.GpsSettingsDialogListener;
import com.techplicit.mycarnival.IntentGenerator;
import com.techplicit.mycarnival.Listener;
import com.techplicit.mycarnival.PrivacyListener;
import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.adapters.FriendsListAdapter;
import com.techplicit.mycarnival.data.CarnivalsSingleton;
import com.techplicit.mycarnival.data.ServiceHandler;
import com.techplicit.mycarnival.data.model.BandLocationPojo;
import com.techplicit.mycarnival.data.model.FriendsPojo;
import com.techplicit.mycarnival.ui.activities.BaseActivity;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by pnaganjane001 on 25/12/15.
 */
public class WhoAreMyFriendsFragment extends Fragment implements Constants, Listener, LocationListener {

    private static final String TAG = WhoAreMyFriendsFragment.class.getName();
    private Button addFriends;
    private Switch privacySwitch;
    private boolean isPrivacyModeEnabled;
    private String inviteEmail;
    private ListView listFriends;
    private SharedPreferences sharedPreferences;
    private FriendsListAdapter friendsListAdapter;
    private TextView textEmpty;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

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

    private boolean isLocationUpdated;
    private ProgressBar carnivalsProgress;
    private AlertDialog changePassDialog;
    private GpsSettingsDialogListener mCallbackListener;
    private static View mapView;
    public static int deviceWidth = 0, deviceHeight = 0;
    private static final int MY_PERMISSIONS_LOCATION = 1;
    private GetFriendsLocationsListener mCallbackLocationListener;
    private String privacyMode;
    private static ImageView btnFetes, btnBands, btnBandLocation, btnBandUpdate, btnSmartUpdate;
    private static ImageView btnFriendFinder;
    private PrivacyListener privacyListener;
    private static boolean isLoaded;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_who_are_my_friends, container, false);

            BaseActivity.mContext = getActivity();

        sharedPreferences = getActivity().getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);

        privacyMode = sharedPreferences.getString(USER_PRIVACY, null);

        addFriends = (Button) v.findViewById(R.id.search_friends_btn);
        privacySwitch = (Switch) v.findViewById(R.id.privacy_mode_switch);
        listFriends = (ListView) v.findViewById(R.id.list_friends);
        textEmpty = (TextView) v.findViewById(R.id.text_empty);


        btnFetes = (ImageView) v.findViewById(R.id.fetes_button_hs);
        btnBands = (ImageView) v.findViewById(R.id.band_button_hs);
        btnBandLocation = (ImageView) v.findViewById(R.id.band_location_button_hs);
        btnBandUpdate = (ImageView) v.findViewById(R.id.band_update_button_hs);
        btnSmartUpdate = (ImageView)v. findViewById(R.id.smart_update_button_hs);
        btnFriendFinder = (ImageView) v.findViewById(R.id.friend_finder_button_hs);

        btnFriendFinder.setEnabled(false);

        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ftra_hv.ttf");

        /*btnFetes.setTypeface(face);
        btnSmartUpdate.setTypeface(face);
        btnBandUpdate.setTypeface(face);
        btnBandLocation.setTypeface(face);
        btnBands.setTypeface(face);

        btnFriendFinder.setTypeface(face);*/

        boolean isSignedIn = sharedPreferences.getBoolean(IS_SIGNED_IN, false);

        if (isSignedIn){
            btnFriendFinder.setVisibility(View.VISIBLE);
        }else{
            btnFriendFinder.setVisibility(View.GONE);
        }

        btnFriendFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                IntentGenerator.startFriendFinderActivity(getActivity());
            }
        });

        btnBands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startBandsListActivity(getActivity());
            }
        });

        btnFetes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startFetesActivity(getActivity());
            }
        });

        btnBandLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startBankLocationUpdate(getActivity());
            }
        });

        btnBandUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(UPDATE_LOCATION_FROM, FROM_BAND_UPDATE_BUTTON);
                editor.commit();
                IntentGenerator.startUpdateBandLocation(getActivity(), -1, null);*/
            }
        });

        btnSmartUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startSmartUpdateActivity(getActivity(), -1, null);
            }
        });


        try{
            privacyListener= (PrivacyListener)new WhereAreMyFriends();
        }catch (Exception e){
            Log.e(TAG, "mCallbackListener--> "+e.toString());
        }

        if (privacyMode!=null && privacyMode.equalsIgnoreCase("ON")){
            privacySwitch.setChecked(true);
        }else{
            privacySwitch.setChecked(false);
        }

        ImageView back_Arrow = (ImageView)v.findViewById(R.id.back_arrow_carnivals_list);
        back_Arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        addFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAddFriendDialog(getActivity());
            }
        });

        //set the switch to ON

        //attach a listener to check for changes in state
        privacySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    isPrivacyModeEnabled = true;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(USER_PRIVACY, "ON");
                    editor.commit();

                    updateUserLocation(isPrivacyModeEnabled, "ON");

                } else {
                    isPrivacyModeEnabled = false;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(USER_PRIVACY, "OFF");
                    editor.commit();

                        updateUserLocation(isPrivacyModeEnabled, "OFF");

                }

                privacyListener.privacyListener(isChecked);
            }
        });

        //check the current state before we display the screen
        if (privacySwitch.isChecked()) {
            isPrivacyModeEnabled = true;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(USER_PRIVACY, "ON");
            editor.commit();
        } else {
            isPrivacyModeEnabled = false;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(USER_PRIVACY, "OFF");
            editor.commit();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(FRIENDS_LIST_EMAIL, sharedPreferences.getString(EMAIL, null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (Utility.isNetworkConnectionAvailable(getActivity())) {
            new GetFriendsListAsync(getActivity(), null, jsonObject).execute();
        }

        return v;
    }

    public static void setupFriendFinderButton(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);

        boolean isSignedIn = sharedPreferences.getBoolean(IS_SIGNED_IN, false);

        if (isSignedIn){
            btnFriendFinder.setVisibility(View.VISIBLE);
        }else{
            btnFriendFinder.setVisibility(View.GONE);
        }
    }

    public void updateUserLocation(boolean privacyMode, String status) {
        try {
            JSONObject object = new JSONObject();
            JSONObject objectLocation = new JSONObject();
            object.put("email", sharedPreferences.getString(EMAIL, null));
            object.put("privacy", status);

            new UpdateUserLocation(getActivity(), object).execute();

        } catch (Exception e) {
            Log.e(TAG, "UpdateUserLocation object error -> " + e.toString());
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MapsInitializer.initialize(getActivity());
    }

    private void displayAddFriendDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity);
//        dialog.setTitle(R.string.add_friend);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.invite_friends_dialog);
        dialog.setCancelable(true);

        final EditText editText = (EditText) dialog.findViewById(R.id.email_id_invite);
        Button inviteButton = (Button) dialog.findViewById(R.id.invite_btn);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel_btn);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(editText.getText())) {

                    if (editText.getText().toString().trim().matches(emailPattern)) {
                        inviteEmail = editText.getText().toString();
                        JSONObject jsonObject = new JSONObject();
                        if (sharedPreferences.getString(EMAIL, null) != null && sharedPreferences.getString(ACCESS_TOKEN, null) != null) {
                            try {
                                jsonObject.put(USER_EMAIL_INVITE, sharedPreferences.getString(EMAIL, null).trim());
                                jsonObject.put(FRIEND_EMAIL_INVITE, inviteEmail.trim());
                                if (Utility.isNetworkConnectionAvailable(getActivity())) {
                                    new InviteFriendTask(getActivity(), jsonObject).execute();
                                }else{
                                    Utility.displayNetworkFailDialog(getActivity(), NETWORK_FAIL, "Success", "Successfully Invited !");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        dialog.dismiss();
                    } else {
                        editText.setError("Enter Valid Email Id");
                    }

                } else {
                    editText.setError("Please Enter Friend Email Id");
                }

            }
        });

        dialog.show();
    }

    @Override
    public void onFriendAcceptedListener(Activity context, String userEmail, ListView listView, TextView textView) {
        JSONObject jsonObject = new JSONObject();
        try {
            listFriends = listView;
            textEmpty = textView;

            if (userEmail != null && !userEmail.equalsIgnoreCase("")) {
                jsonObject.put(FRIENDS_LIST_EMAIL, userEmail);
                if (Utility.isNetworkConnectionAvailable(context)) {
                    new GetFriendsListAsync(context, null, jsonObject).execute();
                }else{
                    Utility.displayNetworkFailDialog(getActivity(), NETWORK_FAIL, "Success", "Successfully Invited !");
                }
            }

            try{
                mCallbackLocationListener = (GetFriendsLocationsListener)new WhereAreMyFriends();
            }catch (Exception e){
                Log.e(TAG, "mCallbackLocationListener--> "+e.toString());
            }

            mCallbackLocationListener.onFriendsLocationListener(context, null);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class GetFriendsListAsync extends AsyncTask<String, String, String> {

        private final JSONObject object;
        ServiceHandler jsonParser = new ServiceHandler();
        private Activity mContext;
        private String responseStatus, inviteEmail;
        private ArrayList<FriendsPojo> friendsPojoArrayList;
        ProgressDialog pd;
        private String inviteStatus;

        public GetFriendsListAsync(Activity context, ProgressBar carnivalsProgress, JSONObject object) {
            mContext = context;
            this.object = object;
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mContext);
            pd.setMessage("Loading Friends List");
            pd.show();
        }

        @Override
        protected String doInBackground(String... args) {
            friendsPojoArrayList = new ArrayList<FriendsPojo>();

            try {
                responseStatus = jsonParser.makePostRequest(
                        FRIENDs_LIST_URL, object);
                Log.e(TAG, "FRIENDs_LIST_URL: "+FRIENDs_LIST_URL);
                Log.e(TAG, "FRIENDs_LIST object: "+object);
                Log.e(TAG, "FRIENDs_LIST responseStatus: "+responseStatus);
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
                            if (jsonArray != null && jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    FriendsPojo pojo = new FriendsPojo(jsonArray.optJSONObject(i));
                                    friendsPojoArrayList.add(pojo);

                                    CarnivalsSingleton.getInstance().setFreindsPojoArrayList(friendsPojoArrayList);
                                    listFriends.setAdapter(new FriendsListAdapter(mContext, friendsPojoArrayList, listFriends, textEmpty));
                                }
                            } else {
                                textEmpty.setVisibility(View.VISIBLE);
                            }

                        }

                    }

                } else {
                    Utility.displayNetworkFailDialog(mContext, ERROR, "", "");
                    textEmpty.setVisibility(View.VISIBLE);
                    textEmpty.setText("Bad Response!");
                }

                if (CarnivalsSingleton.getInstance().getFriendsPojoArrayList() != null && CarnivalsSingleton.getInstance().getFriendsPojoArrayList().size() > 0) {
                    textEmpty.setVisibility(View.GONE);
                } else {
                    textEmpty.setVisibility(View.VISIBLE);
                }

            } catch (Exception e) {
                textEmpty.setVisibility(View.VISIBLE);
                textEmpty.setText("Bad Response!");
                e.printStackTrace();
            }


        }

    }


    class InviteFriendTask extends AsyncTask<String, String, String> implements Constants {
        private SharedPreferences sharedPreferences;
        ServiceHandler jsonParser = new ServiceHandler();
        private Activity mContext;
        private String responseStatus;
        private JSONObject object;
        private String inviteStatus;
        private ProgressDialog pd;

        public InviteFriendTask(Activity context, JSONObject jsonObject) {
            mContext = context;
            object = jsonObject;
            sharedPreferences = mContext.getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
        }


        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mContext);
            pd.setMessage("Sending Invite");
            pd.show();

        }

        @Override
        protected String doInBackground(String... args) {

            try {
                responseStatus = jsonParser.makePostRequest(
                        INVITE_FRIEND_URL, object);
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
                                Utility.displayNetworkFailDialog(mContext, STATUS, "Success", "Successfully Invited !");

                                JSONObject jsonObject1 = new JSONObject();
                                if (sharedPreferences.getString(EMAIL, null) != null && sharedPreferences.getString(ACCESS_TOKEN, null) != null) {
                                    try {
                                        jsonObject1.put(FRIENDS_LIST_EMAIL, sharedPreferences.getString(EMAIL, null));
                                        if (Utility.isNetworkConnectionAvailable(getActivity())) {
                                            new GetFriendsListAsync(mContext, null, jsonObject1).execute();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
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
                showSettingsAlert();
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
            locationManager.removeUpdates(WhoAreMyFriendsFragment.this);
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

    @Override
    public void onResume() {
        super.onResume();
        btnFriendFinder.setEnabled(false);

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
        changePassDialog = alertDialog.create();

        try {
            changePassDialog.cancel();
        } catch (Exception e) {
            Log.e(TAG, "Problem with GPS Settings Alert --> " + e.toString());
        }

        changePassDialog.show();
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
            pd.setMessage("Updating your status");
            pd.show();

        }

        @Override
        protected String doInBackground(String... args) {

            try {
                responseStatus = jsonParser.makePUTRequest(
                        UPDATE_USER_PRIVACY_URL, object);
                Log.e(TAG, "UPDATE_USER_PRIVACY_URL : "+UPDATE_USER_PRIVACY_URL);
                Log.e(TAG, "object : "+object);
                Log.e(TAG, "UPDATE_USER_PRIVACY responseStatus: "+responseStatus);
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


}
