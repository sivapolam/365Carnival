package com.techplicit.mycarnival.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.techplicit.mycarnival.IntentGenerator;
import com.techplicit.mycarnival.NavigationDrawerFragment;
import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.Registration;
import com.techplicit.mycarnival.utils.Constants;

/**
 * Created by pnaganjane001 on 27/12/15.
 */
public class AboutUsActivity extends BaseActivity
        implements Constants {

    private static final String TAG = AboutUsActivity.class.getName();
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    public static NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private FragmentManager fragmentManager;
    private Fragment currentFragment;
    private CallbackManager callbackManager;
    private boolean isSignedIn;
    private SharedPreferences sharedPreferences;
    public static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hide the Title bar of this activity screen
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.activity_carnivals_list);

        mContext = AboutUsActivity.this;

        getLayoutInflater().inflate(R.layout.fragment_about, frameLayout);

        sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);

        home_icon.setImageResource(R.drawable.home);
        titleHome.setVisibility(View.GONE);
        title.setText("About");
        title.setVisibility(View.VISIBLE);

        sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);

        TextView app_name = (TextView) findViewById(R.id.app_name);
        TextView text_version = (TextView)findViewById(R.id.text_version);
        TextView text_copy_rights1 = (TextView)findViewById(R.id.text_copy_rights1);
        TextView text_copy_rights2 = (TextView)findViewById(R.id.text_copy_rights2);

        Typeface face= Typeface.createFromAsset(getAssets(), "fonts/ftra_hv.ttf");

        app_name.setTypeface(face);
        text_version.setTypeface(face);
        text_copy_rights1.setTypeface(face);
        text_copy_rights2.setTypeface(face);

        home_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startHomeActivity(AboutUsActivity.this);
                finish();
            }
        });

        //for facebook
        FacebookSdk.sdkInitialize(AboutUsActivity.this);
        callbackManager = CallbackManager.Factory.create();

        Registration.register(AboutUsActivity.this, mNavigationDrawerFragment, callbackManager);

    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 final Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_about, container, false);
            TextView app_name = (TextView) rootView.findViewById(R.id.app_name);
            TextView text_version = (TextView)rootView.findViewById(R.id.text_version);
            TextView text_copy_rights1 = (TextView)rootView.findViewById(R.id.text_copy_rights1);
            TextView text_copy_rights2 = (TextView)rootView.findViewById(R.id.text_copy_rights2);

            Typeface face= Typeface.createFromAsset(getActivity().getAssets(), "fonts/ftra_hv.ttf");

            app_name.setTypeface(face);
            text_version.setTypeface(face);
            text_copy_rights1.setTypeface(face);
            text_copy_rights2.setTypeface(face);

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mContext = AboutUsActivity.this;
    }

}
