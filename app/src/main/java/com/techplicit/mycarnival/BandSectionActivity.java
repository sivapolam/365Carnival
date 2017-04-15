package com.techplicit.mycarnival;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techplicit.mycarnival.adapters.ViewPagerAdapter;
import com.techplicit.mycarnival.ui.activities.BaseActivity;
import com.techplicit.mycarnival.ui.fragments.BandSectionAlphaSort;
import com.techplicit.mycarnival.ui.fragments.BandsSectionMyFavourites;
import com.techplicit.mycarnival.utils.Utility;

/**
 * Created by FuGenX-50 on 19-01-2017.
 */

public class BandSectionActivity extends BaseActivity {

    ViewPager vp_bands;
    private TabLayout tabLayout;
    private int[] tabIcons = {
            R.drawable.alpha_sort,
            R.drawable.fav,
    };
    String bandName;
    private static ImageView btnFetes, btnBands, btnBandLocation, btnBandUpdate, btnSmartUpdate, btnFriendFinder;

    BandsSectionMyFavourites bandsMyFavourites;
    BandSectionAlphaSort bandsAlphaSortFragment;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.bands_list_activity, frameLayout);
        sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
        bandName = getIntent().getStringExtra("bandName");
        if (!TextUtils.isEmpty(bandName)) {
            Utility.bandName = bandName;
        }

        mContext = BandSectionActivity.this;

        home_icon.setImageResource(R.drawable.home);
        titleHome.setVisibility(View.GONE);
        title.setText("Bands Section");
        title.setVisibility(View.VISIBLE);

        vp_bands = (ViewPager) findViewById(R.id.vp_bands);
        setupViewPager(vp_bands);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(vp_bands);

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
                IntentGenerator.startHomeActivity(BandSectionActivity.this);
                finish();
            }
        });

        btnFetes = (ImageView) findViewById(R.id.fetes_button_hs);
        btnFriendFinder = (ImageView) findViewById(R.id.friend_finder_button_hs);
        btnBands = (ImageView) findViewById(R.id.band_button_hs);
        btnBandLocation = (ImageView) findViewById(R.id.band_location_button_hs);
        btnBandUpdate = (ImageView) findViewById(R.id.band_update_button_hs);
        btnSmartUpdate = (ImageView) findViewById(R.id.smart_update_button_hs);

        btnFetes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startFetesActivity(BandSectionActivity.this);
            }
        });

        btnBands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startBandsListActivity(BandSectionActivity.this);
            }
        });

        btnBandLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startBankLocationUpdate(BandSectionActivity.this);
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
        btnFriendFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startFriendFinderActivity(BandSectionActivity.this, BandSectionActivity.class.getSimpleName());
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

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        bandsAlphaSortFragment = new BandSectionAlphaSort();
        bandsMyFavourites = new BandsSectionMyFavourites();
        adapter.addFragment(bandsAlphaSortFragment, "ALPHA SORT");
        adapter.addFragment(bandsMyFavourites, "MY FAVS");
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mContext = BandSectionActivity.this;
        boolean isSignedIn = sharedPreferences.getBoolean(IS_SIGNED_IN, false);

        if (isSignedIn) {
            btnFriendFinder.setVisibility(View.VISIBLE);
        } else {
            btnFriendFinder.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utility.bandName = null;
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
