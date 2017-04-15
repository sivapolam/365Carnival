package com.techplicit.mycarnival.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.techplicit.mycarnival.BandSectionActivity;
import com.techplicit.mycarnival.BandSectionInfoActivity;
import com.techplicit.mycarnival.BandsActivity;
import com.techplicit.mycarnival.FetesActivity;
import com.techplicit.mycarnival.IntentGenerator;
import com.techplicit.mycarnival.NavigationDrawerFragment;
import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.Registration;
import com.techplicit.mycarnival.ui.fragments.WhoAreMyFriendsFragment;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.Utility;

import java.util.Arrays;

import de.greenrobot.event.EventBus;

/**
 * Created by pnaganjane001 on 25/01/16.
 */
public class BaseActivity extends AppCompatActivity implements Constants, NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String LOG_TAG = BaseActivity.class.getName();
    private SharedPreferences sharedPreferences;
    public static NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CallbackManager callbackManager;
    public FrameLayout frameLayout;
    private boolean isSignedIn;
    public static ProgressDialog progressDialog;
    public TextView titleHome;
    public TextView title;
    public TextView sub_title;
    public ImageView home_icon;
    public static Activity mContext;
    private FrameLayout layout_navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hide the Title bar of this activity screen
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        layout_navigation = (FrameLayout)findViewById(R.id.layout_navigation);
        sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
        frameLayout = (FrameLayout) findViewById(R.id.container);

        mTitle = getTitle();

        Toolbar toolbar = setupToolBar();

        home_icon = (ImageView) findViewById(R.id.home_icon);
        title = (TextView) findViewById(R.id.title);
        titleHome = (TextView) findViewById(R.id.title_home);
        sub_title = (TextView) findViewById(R.id.sub_title);
        title.setText("About");
        title.setVisibility(View.GONE);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        }; // Drawer Toggle Object Made
        drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        mDrawerToggle.setDrawerIndicatorEnabled(false);

        //for facebook
        FacebookSdk.sdkInitialize(BaseActivity.this);
        callbackManager = CallbackManager.Factory.create();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private Toolbar setupToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_drawer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setTitle("");
        return toolbar;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        switch (position) {

            case 0:
                closeDrawer();
                mTitle = getString(R.string.title_section1);

                startActivity(new Intent(BaseActivity.this, AboutUsActivity.class));

                if (mNavigationDrawerFragment.mDrawerListView != null) {
                    mNavigationDrawerFragment.mDrawerListView.setItemChecked(0, true);
                }

                break;
            case 1:
                closeDrawer();
                if (Utility.isNetworkConnectionAvailable(BaseActivity.this)) {
                    IntentGenerator.startPrivacyPolicyActivity(getApplicationContext());
                } else {
                    Utility.displayNetworkFailDialog(BaseActivity.this, NETWORK_FAIL, "Success", "Successfully Invited !");
                }

                if (mNavigationDrawerFragment.mDrawerListView != null) {
                    mNavigationDrawerFragment.mDrawerListView.setItemChecked(1, true);
                }

                break;
            case 2:
                closeDrawer();
                IntentGenerator.startContactEmails(BaseActivity.this);
                if (mNavigationDrawerFragment.mDrawerListView != null) {
                    mNavigationDrawerFragment.mDrawerListView.setItemChecked(2, true);
                }

                break;

            /*case 3:
                closeDrawer();
                if (mNavigationDrawerFragment.mDrawerListView != null) {
                    mNavigationDrawerFragment.mDrawerListView.setItemChecked(3, true);
                }

                if (sharedPreferences.getBoolean(IS_IN_TRUCK_MODE, false)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(IS_IN_TRUCK_MODE, false);
                    editor.commit();
                    mNavigationDrawerFragment.changeNavigationList(BaseActivity.this);
                    Toast.makeText(getApplicationContext(), "Truck On", Toast.LENGTH_LONG).show();
                } else {
                    TruckVerificationFragment fragment = new TruckVerificationFragment();
                    fragment.show(getSupportFragmentManager(), "TruckVerificationFragment");
                }


                break;*/

            case 3:

                closeDrawer();

                isSignedIn = sharedPreferences.getBoolean(IS_SIGNED_IN, false);

                if (isSignedIn) {
                    EventBus.getDefault().post(false);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(IS_SIGNED_IN, false);
                    editor.putString(FIRST_NAME, null);
                    editor.putString(LAST_NAME, null);
                    editor.putString(EMAIL, null);
                    editor.putString(IMAGE, null);
                    editor.putString(ACCESS_TOKEN, null);
                    editor.putString(USER_ID, null);
                    editor.putString(USER_STATUS, null);
                    editor.putString(LOGIN_MEDIUM, null);
                    editor.putBoolean("isPrivacyModeEnabled", false);
                    editor.commit();

                    try {
                        IntentGenerator.stopSmartUpdateService(getApplicationContext());
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "IntentGenerator.stopSmartUpdateService --> " + e.toString());
                    }

                    Toast.makeText(getApplicationContext(), "Sign Out Successfully!", Toast.LENGTH_LONG).show();

                    Log.e(LOG_TAG, "mContext: "+mContext);

                    try {
                        if (mContext instanceof FetesActivity) {
                            Log.e(LOG_TAG, "FetesActivity");

                            FetesActivity.setupFriendFinderButton(mContext);
                        }

                        if (mContext instanceof BandSectionInfoActivity) {
                            Log.e(LOG_TAG, "BandSectionInfoActivity");

                            BandSectionInfoActivity.setupFriendFinderButton(mContext);
                        }

                        if (mContext instanceof BandSectionActivity) {
                            Log.e(LOG_TAG, "BandSectionActivity");

                            BandSectionActivity.setupFriendFinderButton(mContext);
                        }

                        if (mContext instanceof BandsActivity) {
                            Log.e(LOG_TAG, "BandsActivity");
                            BandsActivity.setupFriendFinderButton(mContext);
                        }

                        if (mContext instanceof BandTabsActivity) {
                            Log.e(LOG_TAG, "BandTabsActivity");

                            BandTabsActivity.setupFriendFinderButton(mContext);
                        }

                        if (mContext instanceof UpdateBandLocationActivity) {
                            Log.e(LOG_TAG, "UpdateBandLocationActivity");

                            UpdateBandLocationActivity.setupFriendFinderButton(mContext);
                        }

                        if (mContext instanceof SmartUpdateActivity) {
                            Log.e(LOG_TAG, "SmartUpdateActivity");

                            SmartUpdateActivity.setupFriendFinderButton(mContext);
                        }

                        if (mContext instanceof FriendFinderTabsActivity) {
                            Log.e(LOG_TAG, "FriendFinderTabsActivity");

                            WhoAreMyFriendsFragment.setupFriendFinderButton(mContext);
                            FriendFinderTabsActivity.mContext.finish();
                        }

                    } catch (Exception e) {
                        Log.e(LOG_TAG, "setupFriendFinderButton error--> " + e.toString());
                    }
                    mNavigationDrawerFragment.changeNavigationList(BaseActivity.this);


                } else {

                    if (Utility.isNetworkConnectionAvailable(BaseActivity.this)) {
                        progressDialog = new ProgressDialog(BaseActivity.this);
                        progressDialog.setMessage("Please Wait");
                        progressDialog.show();
                        progressDialog.setCancelable(false);


                        if (AccessToken.getCurrentAccessToken() != null) {
                            LoginManager.getInstance().logOut();
                        }

                        LoginManager.getInstance().logInWithReadPermissions(BaseActivity.this, Arrays.asList("public_profile", "user_friends", "email"));

                    } else {
                        Utility.displayNetworkFailDialog(BaseActivity.this, NETWORK_FAIL, "Success", "Successfully Invited !");
                    }
                }

                if (mNavigationDrawerFragment.mDrawerListView != null) {
                    mNavigationDrawerFragment.mDrawerListView.setItemChecked(4, true);
                }

                break;
        }
    }

    private void closeDrawer() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawerLayout.closeDrawer(Gravity.RIGHT);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.home_menu, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.right_menu_icon) {

            if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                drawerLayout.closeDrawer(Gravity.RIGHT);
            } else {
                drawerLayout.openDrawer(Gravity.RIGHT);
            }

            /*try {
                if (mNavigationDrawerFragment != null) {
                    Log.e(LOG_TAG, "mNavigationDrawerFragment");
                    mNavigationDrawerFragment.changeNavigationList(BaseActivity.this);
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "onResume-->" + e.toString());
            }*/

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(LOG_TAG, "onResume mContext: "+mContext);
        mNavigationDrawerFragment = new NavigationDrawerFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.layout_navigation, mNavigationDrawerFragment);
        ft.commit();

//        mNavigationDrawerFragment = (NavigationDrawerFragment)
//                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        Registration.register(BaseActivity.this, mNavigationDrawerFragment, callbackManager);

        try {
            if (mNavigationDrawerFragment != null) {
                Log.e(LOG_TAG, "mNavigationDrawerFragment");
                mNavigationDrawerFragment.changeNavigationList(BaseActivity.this);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "onResume-->" + e.toString());
        }

        /*try {
            if (mNavigationDrawerFragment != null) {
                Log.e(LOG_TAG, "mNavigationDrawerFragment");
                mNavigationDrawerFragment.changeNavigationList(BaseActivity.this);

                *//*boolean isSignedIn = sharedPreferences.getBoolean(IS_SIGNED_IN, false);
                Log.e(LOG_TAG, "isSignedIn:"+isSignedIn);
                if (!isSignedIn) {
                    onNavigationDrawerItemSelected(3);
                }*//*
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "onResume-->" + e.toString());
        }*/
    }

    public void setFooterButtonsSelector() {

    }
}
