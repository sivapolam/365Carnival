package com.techplicit.mycarnival;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.techplicit.mycarnival.data.ServiceHandler;
import com.techplicit.mycarnival.ui.activities.BandTabsActivity;
import com.techplicit.mycarnival.ui.activities.SmartUpdateActivity;
import com.techplicit.mycarnival.ui.activities.UpdateBandLocationActivity;
import com.techplicit.mycarnival.ui.fragments.BandLocationViewFragment;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;

/**
 * Created by pnaganjane001 on 05/01/16.
 */
public class SmartUpdateService extends Service implements LocationListener, Constants {

    private final static String TAG = "SmartUpdateService";
    private static AlertDialog alertDialog;
    private SharedPreferences sharedPreferences;
    LocationManager lm;
    private boolean isGPSEnabled, isNetworkEnabled;
    private double latitude, longitude, dummyLatitude, dummyLongitude;
    private String bandLongitude, dummyAddress;
    private String bandLatitude;
    private String bandAddress;
    private int updatesCount, minutesInterval, updatesInterval;
    private ScheduledFuture<?> scheduledFuture;
    private String carnivalName;
    private String bandNameSelected;

    OnBandLocationUpdateListener onBandLocationUpdateListener;

    public SmartUpdateService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");

        onBandLocationUpdateListener = (OnBandLocationUpdateListener) new BandLocationViewFragment();


//        dummyLatitude = 28.6139;
//        dummyLongitude = 77.2090;
//        dummyAddress = "delhi";

        sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
        carnivalName = sharedPreferences.getString(CARNIVAL_NAME_SMART_UPDATE, null);
        bandNameSelected = sharedPreferences.getString(BAND_NAME_SMART_UPDATE, null);
        bandLatitude = sharedPreferences.getString(SELECTED_BAND_LATITUDE, null);
        bandLongitude = sharedPreferences.getString(SELECTED_BAND_LONGITUDE, null);


        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_SMART_UPDATED, false);
        editor.commit();
//        setupSmartUpdateButton(false);

        latitude = Double.parseDouble(bandLatitude);
        longitude = Double.parseDouble(bandLongitude);

        Log.e(TAG, "latitude-->" + latitude + " longitude-->" + longitude);

        minutesInterval = (sharedPreferences.getString(MINUTES_INTERVAL_SMART_UPDATE, null) != null) ? Integer.parseInt(sharedPreferences.getString(MINUTES_INTERVAL_SMART_UPDATE, null)) : 0;
        updatesInterval = (sharedPreferences.getString(UPDATES_INTERVAL_SMART_UPDATE, null) != null) ? Integer.parseInt(sharedPreferences.getString(UPDATES_INTERVAL_SMART_UPDATE, null)) : 0;

        subscribeToLocationUpdates();

        try {
            startUpdates();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "startUpdates Exception--> " + e.toString());
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    void setupSmartUpdateButton(boolean status) {
        try {
            Log.e(TAG, "BandTabsActivity");
            BandTabsActivity bandTabsActivity = new BandTabsActivity();
            bandTabsActivity.setupSmartUpdateButton(null, status);

        } catch (Exception e) {
            Log.e(TAG, "e-=--> " + e.toString());
        }

        try {
            UpdateBandLocationActivity.setupSmartUpdateButton(null, status);

        } catch (Exception e) {
            Log.e(TAG, "e-=--> " + e.toString());
        }

        try {
            SmartUpdateActivity.setupSmartUpdateButton(null, status);

        } catch (Exception e) {
            Log.e(TAG, "e-=--> " + e.toString());
        }

    }

    public void startUpdates() throws InterruptedException {

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

        Runnable r = new Runnable() {
            @Override
            public void run() {
                System.out.println("Hello");


                if (Utility.isNetworkConnectionAvailable(getApplicationContext())) {
//                    displayNetworkFailDialog(getApplicationContext());
                    getLocationDetails(latitude, longitude);
                } else {
//
                    /*// getting GPS status
                    isGPSEnabled = lm
                            .isProviderEnabled(LocationManager.GPS_PROVIDER);
                    Log.e(TAG, "getLocation 4");

                    // getting network status
                    isNetworkEnabled = lm
                            .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                    Log.e(TAG, "getLocation 5");

                    if (!isGPSEnabled && !isNetworkEnabled) {
                        // no network provider is enabled
                        showSettingsAlert(getApplicationContext());
//                Log.e("Siva", "no network");
                    }*/

//                    getLocationDetails(latitude, longitude);


                }

                updatesCount++;
                if (updatesCount == updatesInterval && scheduledFuture != null) {
                    scheduledFuture.cancel(false);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(IS_SMART_UPDATED, true);
                    editor.commit();
//                    setupSmartUpdateButton(true);

                    stopSelf();

                }

            }
        };

        scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(r, 0, minutesInterval, TimeUnit.MINUTES);

//        Thread.sleep(1000);
//        scheduledFuture.cancel(false);
    }

    public void onLocationChanged(Location loc) {
        Log.e(TAG, loc.toString());
        Log.e(TAG, "" + loc.getLatitude());

        this.latitude = loc.getLatitude();
        this.longitude = loc.getLongitude();

    }

    public void onProviderEnabled(String s) {
    }

    public void onProviderDisabled(String s) {
    }

    public void onStatusChanged(String s, int i, Bundle b) {
    }

    public void getLocationDetails(double latitude, double longitude) {
        Log.e(TAG, "getLocationDetails");
        if (latitude != 0.0 && longitude != 0.0) {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = null;
            try {
//                                    Log.e(TAG, "convert Lat and Lon-->" + latitude + "-" + longitude);
                addresses = geocoder.getFromLocation(this.latitude, this.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Failed to convert Address from Latitude and Longitude");
            }

            if (addresses != null && addresses.size() > 0) {
                StringBuilder builder = new StringBuilder();

                if (addresses.get(0).getAddressLine(0) != null) {
                    builder.append(addresses.get(0).getAddressLine(0) + ", ");
                }

                if (addresses.get(0).getLocality() != null) {
                    builder.append(addresses.get(0).getLocality() + ", ");
                }

                if (addresses.get(0).getAdminArea() != null) {
                    builder.append(addresses.get(0).getAdminArea() + ", ");
                }

                if (addresses.get(0).getCountryName() != null) {
                    builder.append(addresses.get(0).getCountryName() + ", ");
                }

                if (addresses.get(0).getPostalCode() != null) {
                    builder.append(addresses.get(0).getPostalCode());
                }

                if (addresses.get(0).getLongitude() != 0) {
                    bandLongitude = "" + addresses.get(0).getLongitude();
                }

                if (addresses.get(0).getLatitude() != 0) {
                    bandLatitude = "" + addresses.get(0).getLatitude();
                }

                bandAddress = builder.toString().replace("?", "");

                Log.e(TAG, "bandAddress --> " + bandAddress);

                Log.e(TAG, "bandNameSelected-------> " + bandNameSelected);
                if (bandNameSelected != null && bandNameSelected.equalsIgnoreCase("Yourself")) {
                    Log.e(TAG, "Yourself-------> ");
                    try {
                        JSONObject object = new JSONObject();
                        JSONObject objectLocation = new JSONObject();
                        object.put("email", sharedPreferences.getString(EMAIL, null));
                        objectLocation.put("lattitude", bandLatitude);
                        objectLocation.put("longitude", bandLongitude);
                        object.put("location", objectLocation);

                        new UpdateUserLocation(getApplicationContext(), object).execute();

                    } catch (Exception e) {
                        Log.e(TAG, "UpdateUserLocation object error -> " + e.toString());
                    }
                } else {
                    Log.e(TAG, "GetAsync-------> ");
                    if (carnivalName != null && bandNameSelected != null && !bandLatitude.equals("0.0") && !bandLongitude.equals("0.0") && bandAddress != null) {
                        new GetAsync().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), "Problem in fetching current Location!", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "Problem in fetching current Location!", Toast.LENGTH_LONG).show();
            }


        } else {
            Toast.makeText(getApplicationContext(), "Problem in fetching current Location!", Toast.LENGTH_LONG).show();
        }

    }

    public void getLocation() {
        Log.e(TAG, "getLocation called");

//        subscribeToLocationUpdates();

        this.lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Log.e(TAG, "getLocation 3");

        // getting GPS status
        isGPSEnabled = lm
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.e(TAG, "getLocation 4");

        // getting network status
        isNetworkEnabled = lm
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.e(TAG, "getLocation 5");

        if (!isGPSEnabled && !isNetworkEnabled) {
            // no network provider is enabled
            showSettingsAlert(getApplicationContext());
//                Log.e("Siva", "no network");
        }

        Log.e(TAG, "getLocation 1");

        this.lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        Log.e(TAG, "getLocation 2");
//        this.lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e(TAG, "getLocation ended");

            return;
        }


    }

    public void subscribeToLocationUpdates() {
        Log.e(TAG, "subscribeToLocationUpdates");
        this.lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        this.lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//        this.lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


    }


    public static void displayNetworkFailDialog(Context context) {
        Log.e(TAG, "displayNetworkFailDialog");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setMessage(context.getResources().getString(R.string.network_fail_message));
        alertDialogBuilder.setTitle(context.getResources().getString(R.string.network_status));


        final AlertDialog.Builder ok = alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (alertDialog != null) {
                    alertDialog.dismiss();

                }
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public static void showSettingsAlert(final Context context) {
        Log.e(TAG, "showSettingsAlert called");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        alertDialogBuilder.setTitle("GPS is settings");


        final AlertDialog.Builder settings = alertDialogBuilder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);

                if (alertDialog != null) {
                    alertDialog.dismiss();

                }
            }
        });

        final AlertDialog.Builder cancel = alertDialogBuilder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                if (alertDialog != null) {
                    alertDialog.dismiss();

                }
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e(TAG, "onDestroy() Called");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_SMART_UPDATED, true);
        editor.commit();

        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }

        if (lm != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            lm.removeUpdates(this);
        }

    }

    public class GetAsync extends AsyncTask<String, String, String> {

        ServiceHandler jsonParser = new ServiceHandler();

        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";

        private Activity mContext;
        private ProgressDialog progressDialog;
        private String responseStatus;
        private String response;


        @Override
        protected String doInBackground(String... args) {

            try {

                String updateLocationUrl = Constants.BASE_URL + "updatebandlocation?carnival=" + URLEncoder.encode(carnivalName, "UTF-8") +
                        "&band=" + URLEncoder.encode(bandNameSelected, "UTF-8") + "&address=" + URLEncoder.encode(bandAddress, "UTF-8") + "&latitude=" + bandLatitude + "&longitude=" + bandLongitude;

//                String updateLocationUrl = Constants.BASE_URL + "updatebandlocation?carnival=" + carnivalName +
//                        "&band=" + bandNameSelected + "&address=" + dummyAddress.replace(" ", "%20") + "&latitude=" + dummyLatitude + "&longitude=" + dummyLongitude;

//
//                    String updateLocationUrl = Constants.BASE_URL + "updatebandlocation?carnival=" + carnivalName +
//                            "&band=" + bandNameSelected + "&address=Hyderabad&latitude=17.3700&longitude=78.4800";

                /*if (updateLocationUrl.contains(" & ") || updateLocationUrl.contains(" ")) {
                    updateLocationUrl = updateLocationUrl.replace(" & ", "+%26+").replace(" ", "%20").trim();
                }*/

                Log.e("Siva", "updateLocationUrl--> " + updateLocationUrl);

                response = jsonParser.makeHttpRequest(
                        updateLocationUrl, "GET", null);

                if (response != null && !response.equalsIgnoreCase(ERROR)) {
                    JSONObject jsonObject = new JSONObject(response);
                    responseStatus = (String) jsonObject.get(STATUS);
                }

            } catch (Exception e) {
                e.printStackTrace();
                responseStatus = ERROR;
            }

            return responseStatus;
        }

        protected void onPostExecute(String response) {


            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
            int pos = sharedPreferences.getInt(CARNIVAL_POSITION_SMART_UPDATE, 0);
            Log.e(TAG, "pos--> " + pos);
            Log.e(TAG, "IS_BANDS_VIEW_VISIBLE--> " + sharedPreferences.getBoolean(IS_BANDS_VIEW_VISIBLE, false));

            SharedPreferences.Editor editor = sharedPreferences.edit();

            if (response != null && !response.equalsIgnoreCase(ERROR)) {

                if (response != null && response.equalsIgnoreCase("Success")) {
                    if (sharedPreferences.getBoolean(IS_BANDS_VIEW_VISIBLE, false)) {
                        onBandLocationUpdateListener.onBandLocationUpdateListener(pos, latitude, longitude);
//                        onBandLocationUpdateListener.onBandLocationUpdateListener(pos, dummyLatitude, dummyLongitude);
                    }

                    Toast.makeText(getApplicationContext(), "myCarnival: Smart Update Successful", Toast.LENGTH_LONG).show();
//                    displayUpdateLocationStatus(mContext, "Success");
//                    editor.putBoolean(IS_LOCATION_UPDATED, true);
//                    editor.putBoolean(IS_ALPH_SORT_NEEDS_TO_LOAD, true);
//                    editor.putBoolean(IS_DISATNCE_NEEDS_TO_LOAD, true);
//                    editor.putBoolean(IS_FAVS_NEEDS_TO_LOAD, true);
//                    editor.putBoolean(IS_VIEW_NEEDS_TO_LOAD, true);
                } else {
                    Toast.makeText(getApplicationContext(), "myCarnival: Smart Update Failed", Toast.LENGTH_LONG).show();
//                    displayUpdateLocationStatus(mContext, "Fail");
//                    editor.putBoolean(IS_LOCATION_UPDATED, false);
//                    editor.putBoolean(IS_ALPH_SORT_NEEDS_TO_LOAD, false);
//                    editor.putBoolean(IS_DISATNCE_NEEDS_TO_LOAD, false);
//                    editor.putBoolean(IS_FAVS_NEEDS_TO_LOAD, false);
//                    editor.putBoolean(IS_VIEW_NEEDS_TO_LOAD, false);
                }

//                editor.putString(SELECTED_BAND_NAME, null);
//                editor.putString(SELECTED_BAND_ADDRESS, null);
//                editor.putString(SELECTED_BAND_LATITUDE, null);
//                editor.putString(SELECTED_BAND_LONGITUDE, null);
//                editor.putString(UPDATE_LOCATION_FROM, null);
//                editor.commit();

            } else {
                Toast.makeText(getApplicationContext(), "myCarnival: Smart Update Failed", Toast.LENGTH_LONG).show();
//                Utility.displayNetworkFailDialog(mContext, ERROR, "Success", "Successfully Invited !");
            }

        }

    }

    class UpdateUserLocation extends AsyncTask<String, String, String> implements Constants {

        ServiceHandler jsonParser = new ServiceHandler();

        private Context mContext;
        private String responseStatus;
        private JSONObject object;
        private String inviteStatus;
        private ProgressDialog pd;

        public UpdateUserLocation(Context context, JSONObject jsonObject) {
            mContext = context;
            object = jsonObject;
            sharedPreferences = mContext.getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
            Log.d("Siva", "UpdateUserLocation---> ");
        }


        @Override
        protected void onPreExecute() {
//            Log.d("Siva", "onPreExecute---> ");
//            pd = new ProgressDialog(mContext);
//            pd.setMessage("Updating your location");
//            pd.show();

        }

        @Override
        protected String doInBackground(String... args) {

            try {
//                Log.d("Siva", "UpdateUserLocation doInBackground---> ");
//                Log.d("Siva", "object---> " + object.toString());

                responseStatus = jsonParser.makePUTRequest(
                        UPDATE_USER_LOCATION_URL, object);

//                Log.d("Siva", "UPDATE_USER_LOCATION_URL---> " + UPDATE_USER_LOCATION_URL);
                Log.d("Siva", "responseStatus---> " + responseStatus);

                return responseStatus;


            } catch (Exception e) {
                e.printStackTrace();
                responseStatus = ERROR;
                return responseStatus;
            }

//            return null;
        }

        protected void onPostExecute(String responseStatus) {

//            if (pd != null) {
//                pd.dismiss();
//            }

            Log.d("Siva", "onPostExecute responseStatus---> " + responseStatus);

            if (responseStatus != null) {

                if (responseStatus != null && !responseStatus.equals(ERROR)) {

                    try {
                        JSONObject jsonObject = new JSONObject(responseStatus);
                        inviteStatus = jsonObject.optString(STATUS_INVITE);
                        if (inviteStatus != null) {
//                            Log.d("Siva", "UPDATE_USER_LOCATION_URL---> " + inviteStatus);
                            if (!inviteStatus.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = jsonObject.optJSONArray(EXPLANATION_INVITE);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

//                                    Log.e("Siva", "object)-->" + object.toString());
//                                    Log.e("Siva", "object.optString(ERR004)-->" + object.optString(ERR004));

                                    inviteStatus = Utility.getErrorMessage(object);

//                                    Utility.displayNetworkFailDialog(mContext, STATUS, "Error", inviteStatus);
//                                    Log.d("Siva", "inviteStatus2---> " + inviteStatus);
                                }
                            } else {
//                                Utility.displayNetworkFailDialog(mContext, STATUS, "Success", "Successfully Updated!");
                                SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                Date date = new Date();
                                SharedPreferences sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(LAST_UPDATE, ft.format(date));
                                editor.commit();
                                EventBus.getDefault().post(ft.format(date));
                                Toast.makeText(getApplicationContext(), "myCarnival: Smart Update Success", Toast.LENGTH_LONG).show();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
//                    Utility.displayNetworkFailDialog(mContext, ERROR, "", "");
                    Toast.makeText(getApplicationContext(), "myCarnival: Smart Update Failed", Toast.LENGTH_LONG).show();
                }

                /*if (responseStatus.equals(ERROR)){
                    Utility.displayNetworkFailDialog(mContext, ERROR, "", "");
                }else{
                    if (responseStatus.equalsIgnoreCase(SUCCESS)){


                    }else{
                        Utility.displayNetworkFailDialog(mContext, STATUS, "Failed", responseStatus);
                    }

                }*/

            }
        }

    }


}
