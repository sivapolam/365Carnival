package com.techplicit.carnivaladmin.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.techplicit.carnivaladmin.R;
import com.techplicit.carnivaladmin.interfaces.IFragmentListener;
import com.techplicit.carnivaladmin.ui.fragments.BandUpdateFragment;
import com.techplicit.carnivaladmin.ui.fragments.SmartUpdateFragment;
import com.techplicit.carnivalcommons.CommonSingleton;
import com.techplicit.carnivalcommons.apipresenter.ApiResponsePresenter;
import com.techplicit.carnivalcommons.data.BandLocationPojo;
import com.techplicit.carnivalcommons.interfaces.IRequestInterface;
import com.techplicit.carnivalcommons.interfaces.IResponseInterface;
import com.techplicit.carnivalcommons.utils.BandsDateFormatsConverter;
import com.techplicit.carnivalcommons.utils.Constants;
import com.techplicit.carnivalcommons.utils.UtilityCommon;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements IResponseInterface, android.location.LocationListener, Constants {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String CARNIVAL_NAME = "Tridand - Tabago";
    private static final String BAND_INFO = "band_info";

    public static final String BAND_UPDATE = "BAND_UPDATE";
    public static final String SMART_UPDATE = "SMART_UPDATE";

    /* Variables used for Location listener updates */
    private static final int MY_PERMISSIONS_LOCATION = 123;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // flag for GPS status
    public boolean isGPSEnabled = false;
    // flag for network status
    public boolean isNetworkEnabled = false;
    // flag for GPS status
    static boolean canGetLocation = false, isGPSDialogShowing = false;
    private LocationManager locationManager;
    private Location mLocation;

    /* Variables used for Duration picker */
    private static int mDurationValue = 1;
    private static final int MIN_VALUE = 1;
    private NumberPicker bandsPicker;

    private TextView mScreenTitle;
    public TextView mScreenSubTitle;
    public String mBandNameSelected, mBandAddress;
    public ApiResponsePresenter mApiResponsePresenter;
    public List<BandLocationPojo> mBandLocationPojoList;
    public double mLatitude, mLongitude;
    private IFragmentListener iFragmentListener;
    public SharedPreferences mSharedPreferences;
    public List<String> mListBands;
    private Button mBandUpdateButton;
    private Button mSmartUpdateButton;
    public String selectedCarnivalName;
    public int mSelectedBandPosition = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hide the Title bar of this activity screen
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferences = getSharedPreferences(PREFS_BAND_ADMIN, Context.MODE_PRIVATE);
        mApiResponsePresenter = new ApiResponsePresenter(MainActivity.this);

        if (getIntent()!=null) {
            selectedCarnivalName = getIntent().getStringExtra(SELECTED_CARNIVAL_NAME);
        }

        loadBandsInfo();
        getCurrentLocation(MainActivity.this);
        initViews();
        loadFragment(new SmartUpdateFragment());
//        setButtonStatus(false, true);
        try {
            startUpdates();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "exception startUpdates: "+e.toString(), e);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        removeLocationUpdates();
    }

    public Location getCurrentLocation(Activity activityContext) {
        Log.e(LOG_TAG, "getCurrentLocation");
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
                UtilityCommon.showSettingsAlert(this);
            } else {

                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_LOCATION);

                    return null;
                }

//                Log.e("Siva", "network");
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    Log.e(LOG_TAG, "NetworkEnabled");
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (mLocation != null) {
                            mLatitude = mLocation.getLatitude();
                            mLongitude = mLocation.getLongitude();
                            Log.d(LOG_TAG, "Network Lat:"+mLatitude+", Long:"+mLongitude);
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    Log.e(LOG_TAG, "GPS Enabled");
                    if (mLocation == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (mLocation != null) {
                                mLatitude = mLocation.getLatitude();
                                mLongitude = mLocation.getLongitude();
                                Log.d(LOG_TAG, "GPS Lat:"+mLatitude+", Long:"+mLongitude);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "getlocation Error-- > " + e.toString(), e);
        }

        return mLocation;
    }

    private void loadBandsInfo() {

        Log.e(LOG_TAG, "selectedCarnivalName: "+selectedCarnivalName);
        if (!UtilityCommon.isNetworkConnectionAvailable(MainActivity.this)) {
            UtilityCommon.displayNetworkFailDialog(MainActivity.this, Constants.NETWORK_FAIL);
        } else if (UtilityCommon.isStringValid(selectedCarnivalName)){
            UtilityCommon.showProgressDialog(MainActivity.this, getString(R.string.loading_bands));
            try {
                mApiResponsePresenter.callApi(Request.Method.GET,
                        Constants.BASE_URL + "getbandslocationoverview?carnival=" + URLEncoder.encode(selectedCarnivalName, "UTF-8"),
                        null,
                        BAND_INFO,
                        IRequestInterface.REQUEST_TYPE_JSON_GET_ARRAY);
            } catch (Exception e) {
                Log.e(LOG_TAG, "exception: " + e.toString(), e);
            }
        } else {

        }

    }

    private void initViews() {
        mScreenTitle = (TextView) findViewById(R.id.title_home);
        mScreenSubTitle = (TextView) findViewById(R.id.sub_title);
        mBandUpdateButton = (Button) findViewById(R.id.band_update_button);
        mSmartUpdateButton = (Button) findViewById(R.id.smart_update_button);

        ImageView homeIcon = (ImageView)findViewById(R.id.home_icon);
        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView backArrow = (ImageView)findViewById(R.id.back_arrow_carnivals_list);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        updateToolBar(getString(R.string.app_name) +" v"+getString(R.string.version_num_admin));
        updateToolBar("Smart Update");

        mBandUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateToolBar(getString(R.string.band_update_title));
                loadFragment(new BandUpdateFragment());
                setButtonStatus(false, true);
            }
        });

        mSmartUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateToolBar(getString(R.string.smart_update_title));
                loadFragment(new SmartUpdateFragment());
                setButtonStatus(true, false);

            }
        });

    }

    private void setButtonStatus(boolean bandUpdateStatus, boolean smartUpdateStatus) {
        mBandUpdateButton.setEnabled(bandUpdateStatus);
        mSmartUpdateButton.setEnabled(smartUpdateStatus);
    }

    private void updateToolBar(String title) {
        mScreenTitle.setText(title);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.layout_container, fragment);
        fragmentTransaction.commit();

        if (fragment instanceof IFragmentListener) {
            iFragmentListener = (IFragmentListener) fragment;
        }
    }

    public String getSelectedBandName() {
        return mBandNameSelected;
    }

    /*
        API Response callback methods
     */

    @Override
    public void onResponseSuccess(String resp, String req) {
        UtilityCommon.hideProgressDialog();
        Log.d(LOG_TAG, "onResponseSuccess: " + resp + " --- " + req);

        if (!TextUtils.isEmpty(req) && req.equalsIgnoreCase(BAND_INFO)) {

            try {
                CommonSingleton.getInstance().setBandsJsonResponse(new JSONArray(resp));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "exception: "+e.toString(), e);
            }

            mBandLocationPojoList = CommonSingleton.getInstance().getBandsPojoArrayList();

            /*Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<BandLocationPojo>>() {
            }.getType();
            mBandLocationPojoList = (ArrayList<BandLocationPojo>) gson.fromJson(resp, listType);*/
        }
    }

    @Override
    public void onResponseFailure(String req) {
        UtilityCommon.hideProgressDialog();
        Log.d(LOG_TAG, "onResponseFailure: " + req);
        UtilityCommon.displayNetworkFailDialog(MainActivity.this, ERROR);
    }

    @Override
    public void onApiConnected(String req) {
        Log.d(LOG_TAG, "onApiConnected: " + req);
    }


    public void displayDurationDialog(final Activity context, String screenType) {
        final Dialog mDurationDialog = new Dialog(context);
        mDurationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDurationDialog.setContentView(R.layout.dialog_band_picker);

        bandsPicker = (NumberPicker) mDurationDialog.findViewById(R.id.band_picker);
        TextView selectPicker = (TextView) mDurationDialog.findViewById(R.id.select_band_picker);
        TextView cancelPicker = (TextView) mDurationDialog.findViewById(R.id.cancel_band_picker);

        bandsPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        UtilityCommon.setNumberPickerTextColor(context, bandsPicker, context.getResources().getColor(R.color.red));

        mListBands = new ArrayList<>();
        if (mBandLocationPojoList != null && !mBandLocationPojoList.isEmpty()) {
            for (BandLocationPojo bandLocationPojo : mBandLocationPojoList) {
                mListBands.add(bandLocationPojo.getName());
            }
        }

        /*if (screenType.equalsIgnoreCase(SMART_UPDATE)) {
            mListBands.add("Yourself");
        }*/

        if (!mListBands.isEmpty()) {
            selectPicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDurationDialog.dismiss();
                    mSelectedBandPosition = mDurationValue - 1;
                    mBandNameSelected = "" + mListBands.get(mDurationValue - 1);
                    if (mBandNameSelected != null) {
                        iFragmentListener.updateSelectedBand(mBandNameSelected);
                    }

                    mDurationValue = 1;
                }
            });

            cancelPicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDurationDialog.dismiss();

                }
            });

            String[] textDurationValues = new String[mListBands.size()];
            textDurationValues = mListBands.toArray(textDurationValues);

            bandsPicker.setMinValue(MIN_VALUE);
            bandsPicker.setMaxValue(mListBands.size());
            bandsPicker.setWrapSelectorWheel(false);
            bandsPicker.setDisplayedValues(textDurationValues);
            bandsPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    bandsPicker.getContentDescription();
                    mDurationValue = newVal;
                    mSelectedBandPosition = mDurationValue - 1;
                    mBandNameSelected = mListBands.get(newVal - 1);
                    if (mBandNameSelected != null) {
                        iFragmentListener.updateSelectedBand(mBandNameSelected);
                    }

                }
            });

            mDurationDialog.show();
        } else {
            loadBandsInfo();
        }

    }



    /**
     * Location Listener callback methods
     */

    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOG_TAG, "onLocationChanged location:"+location);

        if (location != null) {
            this.mLatitude = location.getLatitude();
            this.mLongitude = location.getLongitude();
            Log.d(LOG_TAG, "onLocationChanged Lat:"+mLatitude+", Long:"+mLongitude);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(LOG_TAG, "onStatusChanged--> provider:"+provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(LOG_TAG, "onProviderEnabled--> provider:"+provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(LOG_TAG, "onProviderDisabled--> provider:"+provider);
    }

    /**
     * Check Whether Current Location available or not
     *
     * @return true if mLocation available
     */
    public boolean isLocationAvailable() {
        Log.e(LOG_TAG, "isLocationAvailable()"+mLatitude+", "+mLatitude);
        if (mLatitude != 0.0 && mLongitude != 0.0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(LOG_TAG, "onRequestPermissionsResult()");

        if (requestCode == MY_PERMISSIONS_LOCATION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.e(LOG_TAG, "PERMISSION_GRANTED");
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Log.e(LOG_TAG, "isGPSEnabled:"+ isGPSEnabled);

                    /*if (isGPSEnabled) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    } else {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    }*/

                    getCurrentLocation(MainActivity.this);
                }


        } else {
            isGPSEnabled = false;
            isNetworkEnabled = false;

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Toast.makeText(this, "Oops you denied the permission", Toast.LENGTH_LONG).show();
            } else {
                promptSettings();
            }

        }

    }

    private void promptSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You have denied permissions");
        builder.setMessage("Please go to app settings and allow permissions");
        builder.setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                goToSettings();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void goToSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.parse("package:" + getPackageName());
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeLocationUpdates();
    }

    private void removeLocationUpdates() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    @Override
    protected void onRestart() {
        super.onRestart();
        getCurrentLocation(MainActivity.this);
    }

    public void startUpdates() throws InterruptedException {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Log.e(LOG_TAG, " TimerTask run()");
                        setSubTitle();
                    }
                });
            }
        }, 0, 60000);

    }


    private void setSubTitle() {
        if (mBandNameSelected != null
                && mSharedPreferences.getString(LAST_UPDATE_ADMIN, null) != null) {
            String lastUpdateTime = getLastUpdateTime();
            Log.e(LOG_TAG, "lastUpdateTime: "+lastUpdateTime);
            if (UtilityCommon.isStringValid(lastUpdateTime)) {
                Log.e(LOG_TAG, "lastUpdateTime: Valid");
                mScreenSubTitle.setText("Last Update : " + lastUpdateTime);
                mScreenSubTitle.setVisibility(View.VISIBLE);
            }
        }
    }



    private String getLastUpdateTime() {
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        Calendar c = Calendar.getInstance();
        String currentDate = ft.format(c.getTime());
        String lastAccessOn = mSharedPreferences.getString(LAST_UPDATE_ADMIN, "");
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

}
