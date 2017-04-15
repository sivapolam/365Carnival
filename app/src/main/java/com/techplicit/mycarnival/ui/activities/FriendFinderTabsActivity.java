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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.techplicit.mycarnival.GpsSettingsDialogListener;
import com.techplicit.mycarnival.IntentGenerator;
import com.techplicit.mycarnival.NavigationDrawerFragment;
import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.Registration;
import com.techplicit.mycarnival.adapters.BandsTabsPageAdapter;
import com.techplicit.mycarnival.adapters.ViewPagerAdapter;
import com.techplicit.mycarnival.data.CarnivalsSingleton;
import com.techplicit.mycarnival.ui.fragments.AboutFragment;
import com.techplicit.mycarnival.ui.fragments.ContactFragment;
import com.techplicit.mycarnival.ui.fragments.WhereAreMyFriends;
import com.techplicit.mycarnival.ui.fragments.WhoAreMyFriendsFragment;
import com.techplicit.mycarnival.utils.Constants;

/**
 * Created by pnaganjane001 on 18/12/15.
 */
public class FriendFinderTabsActivity extends BaseActivity
        implements ActionBar.TabListener, Constants, GpsSettingsDialogListener, android.location.LocationListener, AboutFragment.OnFragmentInteractionListener, ContactFragment.OnFragmentInteractionListener {


    private static final int MY_PERMISSIONS_LOCATION = 1;
    private static final String TAG = FriendFinderTabsActivity.class.getName();
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private ViewPager viewPager;
    private BandsTabsPageAdapter mAdapter;

    // Tab ICONS
    private int[] tabIcons = {
            R.drawable.ic_who_are_my_friends,
            R.drawable.ic_where_are_my_friends,
    };
    private TabLayout tabLayout;
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
    private static AlertDialog changePassDialog;
    private CallbackManager callbackManager;
    private boolean isSignedIn;
    private SharedPreferences sharedPreferences;

    Fragment whereAreMyFriendsFragment;
    public static ProgressDialog progressDialog;
    private boolean isCreating = true;
    private Activity previousContext;

    public FriendFinderTabsActivity() {
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_friends_list, frameLayout);
        isCreating = true;

        previousContext = mContext;
        mContext = FriendFinderTabsActivity.this;

        sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);

        home_icon.setImageResource(R.drawable.home);
        titleHome.setVisibility(View.GONE);
        title.setText("Friend Finder");
        title.setVisibility(View.VISIBLE);

        sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/ftra_hv.ttf");

        home_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startHomeActivity(FriendFinderTabsActivity.this);
                finish();
            }
        });

        whereAreMyFriendsFragment = new WhereAreMyFriends();

        home_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startHomeActivity(FriendFinderTabsActivity.this);
                finish();
            }
        });

        home_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startHomeActivity(FriendFinderTabsActivity.this);
                finish();
            }
        });

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        //for facebook
        FacebookSdk.sdkInitialize(FriendFinderTabsActivity.this);
        callbackManager = CallbackManager.Factory.create();

        Registration.register(FriendFinderTabsActivity.this, mNavigationDrawerFragment, callbackManager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void setupViewPager(ViewPager viewPager) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);

        String privacyMode = sharedPreferences.getString(USER_PRIVACY, null);


        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new WhoAreMyFriendsFragment(), getResources().getStringArray(R.array.friend_finder_tabs_list)[0]);

        adapter.addFragment(new WhereAreMyFriends(), getResources().getStringArray(R.array.friend_finder_tabs_list)[1]);

        viewPager.setAdapter(adapter);
    }


    private void setupTabIcons() {

        LinearLayout tab1 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_layout, null);
        TextView tabText1 = (TextView) tab1.findViewById(R.id.tab_text);
        ImageView tabImage1 = (ImageView) tab1.findViewById(R.id.tab_image);
        tabText1.setText(getResources().getStringArray(R.array.friend_finder_tabs_list)[0]);
        tabImage1.setImageResource(tabIcons[0]);
//        tabText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tab_favourite, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tab1);


        LinearLayout tab2 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_layout, null);
        TextView tabText2 = (TextView) tab2.findViewById(R.id.tab_text);
        ImageView tabImage2 = (ImageView) tab2.findViewById(R.id.tab_image);
        tabText2.setText(getResources().getStringArray(R.array.friend_finder_tabs_list)[1]);
        tabImage2.setImageResource(tabIcons[1]);
//        tabText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tab_favourite, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tab2);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContext = previousContext;
        isCreating = true;
        CarnivalsSingleton.getInstance().clearFriendsData();
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public void showSettingsAlert() {
        alertDialog = new AlertDialog.Builder(FriendFinderTabsActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                FriendFinderTabsActivity.this.startActivity(intent);
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

        isCreating = false;

        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        if (changePassDialog != null && changePassDialog.isShowing()) {
            changePassDialog.dismiss();
        }

        if (!isGPSEnabled && !isNetworkEnabled) {
//            getLocation(FriendFinderTabsActivity.this);
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
            Log.e("Siva", "getLocation called");
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
                Log.e("Siva", "no network");
            } else {
                Log.e("Siva", "network");
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    Log.e("Siva", "isNetworkEnabled");
                    if (ActivityCompat.checkSelfPermission(FriendFinderTabsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FriendFinderTabsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                        ActivityCompat.requestPermissions(FriendFinderTabsActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_LOCATION);

                        return null;
                    }

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
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
                    Log.e("Siva", "isGPSEnabled");
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
            if (ActivityCompat.checkSelfPermission(FriendFinderTabsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FriendFinderTabsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(FriendFinderTabsActivity.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                Log.e(TAG, "onRequestPermissionsResult");
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
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        switch (tab.getPosition()) {
            case 1:
                Log.e(TAG, "onTabSelected1");
                ft.add(android.R.id.content, whereAreMyFriendsFragment, null);
                break;
            case 0:
                Log.e(TAG, "onTabSelected0");
                break;
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        switch (tab.getPosition()) {
            case 1:
                Log.e(TAG, "onTabUNSelected1");
                ft.remove(whereAreMyFriendsFragment);
                break;
            case 0:
                Log.e(TAG, "onTabUNSelected0");
                break;

        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
