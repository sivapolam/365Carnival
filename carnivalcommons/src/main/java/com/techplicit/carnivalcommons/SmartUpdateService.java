package com.techplicit.carnivalcommons;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.techplicit.carnivalcommons.apipresenter.ApiResponsePresenter;
import com.techplicit.carnivalcommons.interfaces.IRequestInterface;
import com.techplicit.carnivalcommons.interfaces.IResponseInterface;
import com.techplicit.carnivalcommons.utils.Constants;
import com.techplicit.carnivalcommons.utils.Logger;
import com.techplicit.carnivalcommons.utils.UtilityCommon;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;

/**
 * Created by pnaganjane001 on 05/01/16.
 */
public class SmartUpdateService extends Service implements LocationListener, Constants, IResponseInterface {

    private final static String LOG_TAG = "SmartUpdateService";
    private static final String UPDATE_LOCATION = "UPDATE_LOCATION";

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


    public SmartUpdateService() {
        // Do nothing
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        Logger.addRecordToLog("Smart Update Started");

        Log.e(LOG_TAG, "onCreate");

//        dummyLatitude = 28.6139;
//        dummyLongitude = 77.2090;
//        dummyAddress = "delhi";

        sharedPreferences = getSharedPreferences(PREFS_BAND_ADMIN, Context.MODE_PRIVATE);
        carnivalName = sharedPreferences.getString(CARNIVAL_NAME_SMART_UPDATE, null);
        bandNameSelected = sharedPreferences.getString(BAND_NAME_SMART_UPDATE, null);
        bandLatitude = sharedPreferences.getString(SELECTED_BAND_LATITUDE, null);
        bandLongitude = sharedPreferences.getString(SELECTED_BAND_LONGITUDE, null);

        Log.e(LOG_TAG, "mBandNameSelected-->"+bandNameSelected);


        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_SMART_UPDATED, false);
        editor.apply();

        latitude = Double.parseDouble(bandLatitude);
        longitude = Double.parseDouble(bandLongitude);

        Log.e(LOG_TAG, "latitude-->" + latitude + " longitude-->" + longitude);

        minutesInterval = (sharedPreferences.getString(MINUTES_INTERVAL_SMART_UPDATE, null) != null)
                ? Integer.parseInt(sharedPreferences.getString(MINUTES_INTERVAL_SMART_UPDATE, null))
                : 0;
        updatesInterval = (sharedPreferences.getString(UPDATES_INTERVAL_SMART_UPDATE, null) != null)
                ? Integer.parseInt(sharedPreferences.getString(UPDATES_INTERVAL_SMART_UPDATE, null))
                : 0;

        Logger.addRecordToLog("MinutesInterval: "+minutesInterval+"\nupdatesInterval: "+updatesInterval);


        subscribeToLocationUpdates();

        try {
            startUpdates();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception--> " + e.toString(), e);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }


    public void startUpdates() {

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

        Runnable r = new Runnable() {
            @Override
            public void run() {
                System.out.println("Hello");

                if (UtilityCommon.isNetworkConnectionAvailable(getApplicationContext())) {
                    UtilityCommon.getCurrentLocationAddress(getApplicationContext(), latitude, longitude);

                    bandAddress = sharedPreferences.getString(LAST_KNOWN_ADDRESS, "");

                    Log.e(LOG_TAG, "bandAddress: "+bandAddress);

                    if (UtilityCommon.isStringValid(bandAddress)) {
                        Log.e(LOG_TAG, "makeBandLocationAPI");
                        makeBandLocationAPI();
                    } else {
                        Log.e(LOG_TAG, "Problem in Fetching Location");
//                        Toast.makeText(getApplicationContext(), "Problem in Fetching Location...", Toast.LENGTH_LONG).show();
                    }

                }

                updatesCount++;
                Logger.addRecordToLog("Current update is: "+updatesCount);
                Logger.addRecordToLog("Total No. of updates: "+updatesInterval);

                Log.e(LOG_TAG, "updatesCount: "+updatesCount+", updatesInterval: "+updatesInterval+", scheduledFuture:"+scheduledFuture);
                if (updatesCount == updatesInterval && scheduledFuture != null) {
                                Log.e(LOG_TAG, "Updates finished");
                    Logger.addRecordToLog("Updates finished");
                    scheduledFuture.cancel(false);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(IS_SMART_UPDATED, true);
                    editor.apply();
                    Toast.makeText(getApplicationContext(), "myCarnival: Smart Update Finished", Toast.LENGTH_LONG).show();

                    stopSelf();
                }

            }
        };

        scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(r, 0, minutesInterval, TimeUnit.MINUTES);

    }

    private void makeBandLocationAPI() {
        ApiResponsePresenter apiResponsePresenter = new ApiResponsePresenter(this);

        try {
            String updateLocationUrl = Constants.BASE_URL + "updatebandlocation?carnival="
                    + URLEncoder.encode(carnivalName, "UTF-8")
                    + "&band=" + URLEncoder.encode(bandNameSelected, "UTF-8")
                    + "&address=" + URLEncoder.encode(bandAddress, "UTF-8")
                    + "&latitude=" + latitude
                    + "&longitude=" + longitude;

            apiResponsePresenter.callApi(Request.Method.GET,
                    updateLocationUrl,
                    null,
                    UPDATE_LOCATION,
                    IRequestInterface.REQUEST_TYPE_JSON_OBJECT);
        } catch (UnsupportedEncodingException e) {
            Log.e(LOG_TAG, "exception: " + e.toString(), e);
        } catch (Exception e) {
            Log.e(LOG_TAG, "exception: " + e.toString(), e);
        }

    }

    public void onLocationChanged(Location loc) {
        this.latitude = loc.getLatitude();
        this.longitude = loc.getLongitude();
        Log.e(LOG_TAG, "lat: " + latitude + " Long: " + longitude);
    }

    public void onProviderEnabled(String s) {
    }

    public void onProviderDisabled(String s) {
    }

    public void onStatusChanged(String s, int i, Bundle b) {
    }

    public void subscribeToLocationUpdates() {
        Log.e(LOG_TAG, "subscribeToLocationUpdates");
        this.lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        this.lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 900, 0, this);
//        this.lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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


    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e(LOG_TAG, "onDestroy() Called");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_SMART_UPDATED, true);
        editor.apply();

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

    @Override
    public void onResponseSuccess(String resp, String req) {
        Log.e(LOG_TAG, "onResponseSuccess--> " + resp);

        if (resp != null) {
            try {
                JSONObject jsonObject = new JSONObject(resp);
                String updateResponse = jsonObject.optString("Status");
                if (updateResponse != null && updateResponse.equalsIgnoreCase("Success")) {
                    Toast.makeText(getApplicationContext(), "myCarnival: Smart Update Successful", Toast.LENGTH_LONG).show();

                    SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    Date date = new Date();

                    SharedPreferences sharedPreferences = getSharedPreferences(PREFS_BAND_ADMIN, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(LAST_UPDATE_ADMIN, ft.format(date));
                    editor.apply();
                    EventBus.getDefault().post(ft.format(date));
                    Logger.addRecordToLog("myCarnival: Smart Update Successful @"+ft.format(date));
                } else {
                    Toast.makeText(getApplicationContext(), "myCarnival: Smart Update Failed", Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                Log.e(LOG_TAG, "onResponseSuccess exception: "+e.toString(), e);
            }

        }

    }

    @Override
    public void onResponseFailure(String req) {
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();

        Logger.addRecordToLog("myCarnival: Smart Update Failed @"+ft.format(date));

        Log.e(LOG_TAG, "onResponseFailure--> " + req);
        Toast.makeText(getApplicationContext(), "myCarnival: Smart Update Failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onApiConnected(String req) {
        Log.e(LOG_TAG, "onApiConnected--> " + req);
    }


}
