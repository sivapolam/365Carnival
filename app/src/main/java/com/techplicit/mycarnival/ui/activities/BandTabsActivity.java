package com.techplicit.mycarnival.ui.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techplicit.mycarnival.GpsSettingsDialogListener;
import com.techplicit.mycarnival.IntentGenerator;
import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.adapters.BandsTabsPageAdapter;
import com.techplicit.mycarnival.adapters.ViewPagerAdapter;
import com.techplicit.mycarnival.data.CarnivalsSingleton;
import com.techplicit.mycarnival.ui.fragments.BandLocationAlphaSortFragment;
import com.techplicit.mycarnival.ui.fragments.BandLocationDistanceFragment;
import com.techplicit.mycarnival.ui.fragments.BandLocationMyFavourites;
import com.techplicit.mycarnival.ui.fragments.BandLocationViewFragment;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.Utility;

/**
 * Created by pnaganjane001 on 18/12/15.
 */
public class BandTabsActivity extends BaseActivity
        implements android.app.ActionBar.TabListener, Constants, GpsSettingsDialogListener, android.location.LocationListener {


    private static final int MY_PERMISSIONS_LOCATION = 1;
    private static final String TAG = BandTabsActivity.class.getName();
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    private CharSequence mTitle;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private ViewPager viewPager;
    private BandsTabsPageAdapter mAdapter;

    // Tab titles
    private String[] tabs = {"Top Rated", "Games", "Movies"};
    private int[] tabIcons = {
            R.drawable.distance_ic,
            R.drawable.alpha_sort,
            R.drawable.fav,
            R.drawable.view
    };
    private TabLayout tabLayout;
    private static ImageView btnFetes, btnBands, btnBandLocation, btnBandUpdate, btnSmartUpdate, btnFriendFinder;
    private int bandPosition;
    private String bandName;

    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    // Declaring a Location Manager
    protected LocationManager locationManager;
    private AlertDialog.Builder alertDialog;
    private AlertDialog changePassDialog;

    public static SharedPreferences sharedPreferences;
    public static ProgressDialog progressDialog;

    public BandTabsActivity() {
    }

    private static boolean isCreating = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isCreating = true;

        mContext = BandTabsActivity.this;

        sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);

        getLayoutInflater().inflate(R.layout.activity_bands_list, frameLayout);

        sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);

        home_icon.setImageResource(R.drawable.home);
        titleHome.setVisibility(View.GONE);
        title.setText("Band Location");
        title.setVisibility(View.VISIBLE);

        sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/ftra_hv.ttf");

        home_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startHomeActivity(BandTabsActivity.this);
                finish();
            }
        });

        btnFetes = (ImageView) findViewById(R.id.fetes_button_hs);
        btnBands = (ImageView) findViewById(R.id.band_button_hs);
        btnBandLocation = (ImageView) findViewById(R.id.band_location_button_hs);
        btnBandUpdate = (ImageView) findViewById(R.id.band_update_button_hs);
        btnSmartUpdate = (ImageView) findViewById(R.id.smart_update_button_hs);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        ImageView backArrowCarnivalsList = (ImageView) findViewById(R.id.back_arrow_carnivals_list);
        backArrowCarnivalsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnFriendFinder = (ImageView) findViewById(R.id.friend_finder_button_hs);
        /*btnFetes.setTypeface(face);
        btnSmartUpdate.setTypeface(face);
        btnBandUpdate.setTypeface(face);
        btnBandLocation.setTypeface(face);
        btnBands.setTypeface(face);
        btnFriendFinder.setTypeface(face);
*/
        btnBandLocation.setEnabled(false);


        btnFriendFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startFriendFinderActivity(BandTabsActivity.this, BandTabsActivity.class.getSimpleName());
            }
        });

        boolean status = sharedPreferences.getBoolean(IS_IN_TRUCK_MODE, false);

        setupFriendFinderButton(BandTabsActivity.this);


        btnBands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startBandsListActivity(BandTabsActivity.this);
            }
        });

        btnFetes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startFetesActivity(BandTabsActivity.this);
            }
        });

        btnBandLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent bandIntent = new Intent(BandTabsActivity.this, BandTabsActivity.class);
//                startActivity(bandIntent);
            }
        });

        btnBandUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*SharedPreferences sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(UPDATE_LOCATION_FROM, FROM_BAND_UPDATE_BUTTON);
                editor.commit();
                IntentGenerator.startUpdateBandLocation(getApplicationContext(), -1, null);*/
            }
        });

        btnSmartUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startSmartUpdateActivity(getApplicationContext(), -1, null);
            }
        });
    }


    public static void setupFriendFinderButton(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);

        boolean isSignedIn = sharedPreferences.getBoolean(IS_SIGNED_IN, false);

        if (isSignedIn) {
            btnFriendFinder.setVisibility(View.VISIBLE);
        } else {
            btnFriendFinder.setVisibility(View.GONE);
        }

    }

    public void setupSmartUpdateButton(Activity activity, boolean status) {

        if (status) {
            btnSmartUpdate.setVisibility(View.VISIBLE);
        } else {
            btnSmartUpdate.setVisibility(View.GONE);
        }

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BandLocationDistanceFragment(), "DISTANCE");
        adapter.addFragment(new BandLocationAlphaSortFragment(), "ALPHA SORT");
        adapter.addFragment(new BandLocationMyFavourites(), "MY FAVS");
        adapter.addFragment(new BandLocationViewFragment(), "VIEWS");
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        LinearLayout tab1 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_layout, null);
        TextView tabText1 = (TextView) tab1.findViewById(R.id.tab_text);
        ImageView tabImage1 = (ImageView) tab1.findViewById(R.id.tab_image);
        tabText1.setText(getResources().getStringArray(R.array.band_location_tabs_list)[0]);
        tabImage1.setImageResource(tabIcons[0]);
        tabLayout.getTabAt(0).setCustomView(tab1);

        LinearLayout tab2 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_layout, null);
        TextView tabText2 = (TextView) tab2.findViewById(R.id.tab_text);
        ImageView tabImage2 = (ImageView) tab2.findViewById(R.id.tab_image);
        tabText2.setText(getResources().getStringArray(R.array.band_location_tabs_list)[1]);
        tabImage2.setImageResource(tabIcons[1]);
        tabLayout.getTabAt(1).setCustomView(tab2);

        LinearLayout tab3 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_layout, null);
        TextView tabText3 = (TextView) tab3.findViewById(R.id.tab_text);
        ImageView tabImage3 = (ImageView) tab3.findViewById(R.id.tab_image);
        tabText3.setText(getResources().getStringArray(R.array.band_location_tabs_list)[2]);
        tabImage3.setImageResource(tabIcons[2]);
        tabLayout.getTabAt(2).setCustomView(tab3);

        LinearLayout tab4 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_layout, null);
        TextView tabText = (TextView) tab4.findViewById(R.id.tab_text);
        ImageView tabImage = (ImageView) tab4.findViewById(R.id.tab_image);
        tabText.setText(getResources().getStringArray(R.array.band_location_tabs_list)[3]);
        tabImage.setImageResource(tabIcons[3]);
        tabLayout.getTabAt(3).setCustomView(tab4);

    }

    @Override
    public void onTabSelected(android.app.ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(android.app.ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(android.app.ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCreating = true;
        CarnivalsSingleton.getInstance().clearBandsData();
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public void showSettingsAlert() {
        alertDialog = new AlertDialog.Builder(BandTabsActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                BandTabsActivity.this.startActivity(intent);
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

        changePassDialog = alertDialog.create();

        try {
            changePassDialog.cancel();
        } catch (Exception e) {
            Log.e(TAG, "Problem with GPS Settings Alert --> " + e.toString());
        }

        changePassDialog.show();
    }

    @Override
    public void showSettingsDialog(boolean status) {
        if (changePassDialog != null && changePassDialog.isShowing()) {
            changePassDialog.dismiss();
        }
        showSettingsAlert();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mContext = BandTabsActivity.this;
        btnBandLocation.setEnabled(false);

        isCreating = false;

        boolean isLoggedIn = sharedPreferences.getBoolean(IS_SIGNED_IN, false);
        if (isLoggedIn) {
            btnFriendFinder.setVisibility(View.VISIBLE);
        } else {
            btnFriendFinder.setVisibility(View.GONE);
        }


        if (changePassDialog != null && changePassDialog.isShowing()) {
            changePassDialog.dismiss();
        }

        try {
            mNavigationDrawerFragment.changeNavigationList(BandTabsActivity.this);
        } catch (Exception e) {
            Log.e(TAG, "onResume error--> " + e.toString());
        }

        if (!isGPSEnabled && !isNetworkEnabled) {
            getLocation(BandTabsActivity.this);
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
                showSettingsAlert();
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(BandTabsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BandTabsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                        ActivityCompat.requestPermissions(BandTabsActivity.this,
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
                        Log.d("GPS Enabled", "GPS Enabled");
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
            if (ActivityCompat.checkSelfPermission(BandTabsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BandTabsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(BandTabsActivity.this);
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
    protected void onPause() {
        super.onPause();
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

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            try {
                mNavigationDrawerFragment.changeNavigationList(BandTabsActivity.this);
            } catch (Exception e) {
                Log.e(TAG, "onResume error--> " + e.toString());
            }

            setupFriendFinderButton(BandTabsActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in onRestart()--> " + e.toString());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            if (Utility.wakeLock != null) {
                Utility.wakeLock.release();
            }
        } catch (Exception e) {
            Log.e(TAG, "wakeLock error--> " + e.toString());
        }

    }
}
