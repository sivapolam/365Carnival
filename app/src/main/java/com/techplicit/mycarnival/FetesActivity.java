package com.techplicit.mycarnival;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techplicit.mycarnival.adapters.ViewPagerAdapter;
import com.techplicit.mycarnival.ui.activities.BaseActivity;
import com.techplicit.mycarnival.ui.fragments.FetesAlphaSortFragment;
import com.techplicit.mycarnival.ui.fragments.FetesDateFragment;
import com.techplicit.mycarnival.ui.fragments.FetesMyFavourites;

/**
 * Created by FuGenX-50 on 19-01-2017.
 */

public class FetesActivity extends BaseActivity {

    ViewPager vp_fetes;
    private TabLayout tabLayout;
    private int[] tabIcons = {
            R.drawable.date,
            R.drawable.alpha_sort,
            R.drawable.fav
    };
    private static ImageView btnFetes, btnBands, btnBandLocation, btnBandUpdate, btnSmartUpdate, btnFriendFinder;


    FetesDateFragment fetesDateFragment;
    FetesAlphaSortFragment fetesAlphaSortFragment;
    FetesMyFavourites fetesMyFavourites;
    SharedPreferences sharedPreferences ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.fetes_list_activity, frameLayout);

        sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);

        mContext = FetesActivity.this;

        home_icon.setImageResource(R.drawable.home);
        titleHome.setVisibility(View.GONE);
        title.setText("Fetes");
        title.setVisibility(View.VISIBLE);

        vp_fetes = (ViewPager) findViewById(R.id.vp_fetes);
        vp_fetes.setOffscreenPageLimit(3);
        setupViewPager(vp_fetes);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(vp_fetes);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(vp_fetes);
        setupTabIcons();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp_fetes.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    fetesDateFragment.notifyAdapter();
                } else if (tab.getPosition() == 1) {
                    fetesAlphaSortFragment.notifyAdapter();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        btnFetes = (ImageView) findViewById(R.id.fetes_button_hs);
        btnFriendFinder = (ImageView) findViewById(R.id.friend_finder_button_hs);
        btnBands = (ImageView) findViewById(R.id.band_button_hs);
        btnBandLocation = (ImageView) findViewById(R.id.band_location_button_hs);
        btnBandUpdate = (ImageView) findViewById(R.id.band_update_button_hs);
        btnSmartUpdate = (ImageView) findViewById(R.id.smart_update_button_hs);

        btnFetes.setEnabled(false);
        home_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startHomeActivity(FetesActivity.this);
                finish();
            }
        });

        btnFriendFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startFriendFinderActivity(FetesActivity.this, FetesActivity.class.getSimpleName());
            }
        });

        btnBands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startBandsListActivity(FetesActivity.this);
            }
        });

        btnFetes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                IntentGenerator.startFetesActivity(FetesActivity.this);
            }
        });

        btnBandLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startBankLocationUpdate(FetesActivity.this);
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

    @Override
    protected void onResume() {
        mContext = FetesActivity.this;
        super.onResume();
        btnFetes.setEnabled(false);

        if (mNavigationDrawerFragment != null) {
            mNavigationDrawerFragment.changeNavigationList(FetesActivity.this);
        }

        boolean isSignedIn = sharedPreferences.getBoolean(IS_SIGNED_IN, false);

        if (isSignedIn){
            btnFriendFinder.setVisibility(View.VISIBLE);
        }else{
            btnFriendFinder.setVisibility(View.GONE);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        fetesDateFragment = new FetesDateFragment();
        fetesAlphaSortFragment = new FetesAlphaSortFragment();
        fetesMyFavourites = new FetesMyFavourites();

        adapter.addFragment(fetesDateFragment, "Date");
        adapter.addFragment(fetesAlphaSortFragment, "ALPHA SORT");
        adapter.addFragment(fetesMyFavourites, "MY FAVS");

        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {

        LinearLayout tab1 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_layout, null);
        TextView tabText1 = (TextView) tab1.findViewById(R.id.tab_text);
        ImageView tabImage1 = (ImageView) tab1.findViewById(R.id.tab_image);
        tabText1.setText(getResources().getStringArray(R.array.band_location_tabs_list)[4]);
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
}
