package com.techplicit.mycarnival;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techplicit.mycarnival.adapters.ViewPagerAdapter;
import com.techplicit.mycarnival.ui.activities.BaseActivity;
import com.techplicit.mycarnival.ui.fragments.BandsAlphaSortFragment;
import com.techplicit.mycarnival.ui.fragments.BandsMyFavourites;

import de.greenrobot.event.EventBus;

/**
 * Created by FuGenX-50 on 19-01-2017.
 */

public class BandsActivity extends BaseActivity {

    private static final String LOG_TAG = BandsActivity.class.getSimpleName();
    ViewPager vp_bands;
    private TabLayout tabLayout;
    private int[] tabIcons = {
            R.drawable.alpha_sort,
            R.drawable.fav,
    };
    private static ImageView btnFetes, btnBands, btnBandLocation, btnBandUpdate, btnSmartUpdate,btnFriendFinder;

    BandsMyFavourites bandsMyFavourites;
    BandsAlphaSortFragment bandsAlphaSortFragment;

    SharedPreferences sharedPreferences ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.bands_list_activity, frameLayout);
        sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
        home_icon.setImageResource(R.drawable.home);
        titleHome.setVisibility(View.GONE);
        title.setText("Bands");
        title.setVisibility(View.VISIBLE);
        mContext = BandsActivity.this;
        vp_bands = (ViewPager) findViewById(R.id.vp_bands);

        setupViewPager(vp_bands);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(vp_bands);
        setupTabIcons();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                vp_bands.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 1) {
//                    bandsMyFavourites.notifyAdapter();
                } else {
                    bandsAlphaSortFragment.notifyAdapter();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        home_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startHomeActivity(BandsActivity.this);
                finish();
            }
        });

        btnFriendFinder = (ImageView) findViewById(R.id.friend_finder_button_hs);
        btnFetes = (ImageView) findViewById(R.id.fetes_button_hs);
        btnBands = (ImageView) findViewById(R.id.band_button_hs);
        btnBandLocation = (ImageView) findViewById(R.id.band_location_button_hs);
        btnBandUpdate = (ImageView) findViewById(R.id.band_update_button_hs);
        btnSmartUpdate = (ImageView) findViewById(R.id.smart_update_button_hs);

        btnBands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                IntentGenerator.startBandsListActivity(BandsActivity.this);
            }
        });

        btnFriendFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startFriendFinderActivity(BandsActivity.this, BandsActivity.class.getSimpleName());
            }
        });

        btnFetes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startFetesActivity(BandsActivity.this);
            }
        });

        btnBandLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startBankLocationUpdate(BandsActivity.this);
            }
        });

        btnBandUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*SharedPreferences.Editor editor = sharedPreferences.edit();
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

        ImageView backArrowCarnivalsList = (ImageView) findViewById(R.id.back_arrow_carnivals_list);
        backArrowCarnivalsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public static void setupFriendFinderButton(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);

        boolean isSignedIn = sharedPreferences.getBoolean(IS_SIGNED_IN, false);
        Log.e(LOG_TAG, "setupFriendFinderButton: isSignedIn--> "+isSignedIn);
        if (isSignedIn) {
            btnFriendFinder.setVisibility(View.VISIBLE);
        } else {
            btnFriendFinder.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    public void onEvent(boolean isSignedIn) {
        if (isSignedIn) {
            btnFriendFinder.setVisibility(View.VISIBLE);
        } else {
            btnFriendFinder.setVisibility(View.GONE);
        }
    }
        @Override
    protected void onResume() {
        super.onResume();
            mContext = BandsActivity.this;
        btnBands.setEnabled(false);

        boolean isSignedIn = sharedPreferences.getBoolean(IS_SIGNED_IN, false);

        if (isSignedIn){
            btnFriendFinder.setVisibility(View.VISIBLE);
        }else{
            btnFriendFinder.setVisibility(View.GONE);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        bandsMyFavourites = new BandsMyFavourites();
        bandsAlphaSortFragment = new BandsAlphaSortFragment();

        adapter.addFragment(bandsAlphaSortFragment, "ALPHA SORT");
        adapter.addFragment(bandsMyFavourites, "MY FAVS");

        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        LinearLayout tab1 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_layout, null);
        TextView tabText1 = (TextView) tab1.findViewById(R.id.tab_text);
        ImageView tabImage1 = (ImageView) tab1.findViewById(R.id.tab_image);
        tabText1.setText(getResources().getStringArray(R.array.band_location_tabs_list)[1]);
        tabImage1.setImageResource(tabIcons[0]);
        tabLayout.getTabAt(0).setCustomView(tab1);

        LinearLayout tab2 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_layout, null);
        TextView tabText2 = (TextView) tab2.findViewById(R.id.tab_text);
        ImageView tabImage2 = (ImageView) tab2.findViewById(R.id.tab_image);
        tabText2.setText(getResources().getStringArray(R.array.band_location_tabs_list)[2]);
        tabImage2.setImageResource(tabIcons[1]);
        tabLayout.getTabAt(1).setCustomView(tab2);
    }


}
