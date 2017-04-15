package com.techplicit.mycarnival;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.techplicit.mycarnival.adapters.ViewPagerAdapter;
import com.techplicit.mycarnival.apipresenter.ApiResponsePresenter;
import com.techplicit.mycarnival.interfaces.IRequestInterface;
import com.techplicit.mycarnival.interfaces.IResponseInterface;
import com.techplicit.mycarnival.ui.activities.BaseActivity;
import com.techplicit.mycarnival.ui.fragments.BandExchange;
import com.techplicit.mycarnival.ui.fragments.BandGallery;
import com.techplicit.mycarnival.ui.fragments.BandInformation;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by FuGenX-50 on 19-01-2017.
 */

public class BandSectionInfoActivity extends BaseActivity implements IResponseInterface, Constants {

    ViewPager vp_bands;
    private TabLayout tabLayout;
    private int[] tabIcons = {
            R.drawable.info,
            R.drawable.gallery,
            R.drawable.exchange
    };
    String bandSectionName;
    private static String BAND_INFO = "band_info";
    private static ImageView btnFetes, btnBands, btnBandLocation, btnBandUpdate, btnSmartUpdate,btnFriendFinder;
    ApiResponsePresenter apiResponsePresenter;

    BandInformation bandInformation;
    BandGallery bandGallery;
    BandExchange bandExchange;
    JSONArray galleryArray;
    JSONObject jsonObject = null;
    SharedPreferences sharedPreferences ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.bands_list_activity, frameLayout);
        sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
        apiResponsePresenter = new ApiResponsePresenter(this);
        bandSectionName = getIntent().getStringExtra("bandSectionName");

        bandInformation = new BandInformation();
        bandGallery = new BandGallery();
        bandExchange = new BandExchange();

        mContext = BandSectionInfoActivity.this;

        if (!TextUtils.isEmpty(bandSectionName)) {
            Log.v("bandSectionName", "bandSectionName if" + bandSectionName);
            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, MODE_PRIVATE);
            try {
                apiResponsePresenter.callApi(Request.Method.GET, Constants.BASE_URL + "getbandsectioninfo?carnival=" + URLEncoder.encode(sharedPreferences.getString(SELECTED_CARNIVAL_NAME, ""), "UTF-8") + "&band=" + URLEncoder.encode(Utility.bandName, "UTF-8") + "&section=" + URLEncoder.encode(bandSectionName, "UTF-8"), null, BAND_INFO, IRequestInterface.REQUEST_TYPE_JSON_OBJECT);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            Log.v("bandSectionName", "bandSectionName else" + bandSectionName);
        }

        home_icon.setImageResource(R.drawable.home);
        titleHome.setVisibility(View.GONE);
        title.setText("Bands Info");
        title.setVisibility(View.VISIBLE);

        vp_bands = (ViewPager) findViewById(R.id.vp_bands);
        tabLayout = (TabLayout) findViewById(R.id.tabs);


        home_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startHomeActivity(BandSectionInfoActivity.this);
                finish();
            }
        });

        btnFriendFinder = (ImageView) findViewById(R.id.friend_finder_button_hs);
        btnFetes = (ImageView) findViewById(R.id.fetes_button_hs);
        btnBands = (ImageView) findViewById(R.id.band_button_hs);
        btnBandLocation = (ImageView) findViewById(R.id.band_location_button_hs);
        btnBandUpdate = (ImageView) findViewById(R.id.band_update_button_hs);
        btnSmartUpdate = (ImageView) findViewById(R.id.smart_update_button_hs);

        btnFetes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startFetesActivity(BandSectionInfoActivity.this);
            }
        });

        btnBands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startBandsListActivity(BandSectionInfoActivity.this);
            }
        });

        btnFriendFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startFriendFinderActivity(BandSectionInfoActivity.this, BandSectionInfoActivity.class.getSimpleName());
            }
        });

        btnBandLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startBankLocationUpdate(BandSectionInfoActivity.this);
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

        setupViewPager(vp_bands);
        tabLayout.setupWithViewPager(vp_bands);
        setupTabIcons();
        vp_bands.setOffscreenPageLimit(3);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(bandInformation, getResources().getStringArray(R.array.band_location_tabs_list)[5]);
        adapter.addFragment(bandGallery, getResources().getStringArray(R.array.band_location_tabs_list)[6]);
        adapter.addFragment(bandExchange, getResources().getStringArray(R.array.band_location_tabs_list)[7]);
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mContext = BandSectionInfoActivity.this;
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
        tabText1.setText(getResources().getStringArray(R.array.band_location_tabs_list)[5]);
        tabImage1.setImageResource(tabIcons[0]);
        tabLayout.getTabAt(0).setCustomView(tab1);

        LinearLayout tab2 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_layout, null);
        TextView tabText2 = (TextView) tab2.findViewById(R.id.tab_text);
        ImageView tabImage2 = (ImageView) tab2.findViewById(R.id.tab_image);
        tabText2.setText(getResources().getStringArray(R.array.band_location_tabs_list)[6]);
        tabImage2.setImageResource(tabIcons[1]);
        tabLayout.getTabAt(1).setCustomView(tab2);

        LinearLayout tab3 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_layout, null);
        TextView tabText3 = (TextView) tab3.findViewById(R.id.tab_text);
        ImageView tabImage3 = (ImageView) tab3.findViewById(R.id.tab_image);
        tabText3.setText(getResources().getStringArray(R.array.band_location_tabs_list)[7]);
        tabImage3.setImageResource(tabIcons[2]);
        tabLayout.getTabAt(2).setCustomView(tab3);
    }


    @Override
    public void onResponseSuccess(String resp, String req) {
        try {
            jsonObject = new JSONObject(resp);
            if (jsonObject.has("gallery")) {
                galleryArray = jsonObject.getJSONArray("gallery");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            bandInformation.setData(jsonObject);
            bandGallery.setData(galleryArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponseFailure(String req) {
        Toast.makeText(this, "Response Failed " + req, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onApiConnected(String req) {

    }
}
