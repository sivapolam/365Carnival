package com.techplicit.mycarnival.ui.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.techplicit.carnivalcommons.utils.BandsDateFormatsConverter;
import com.techplicit.carnivalcommons.utils.UtilityCommon;
import com.techplicit.mycarnival.IntentGenerator;
import com.techplicit.mycarnival.NavigationDrawerFragment;
import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.data.CarnivalsSingleton;
import com.techplicit.mycarnival.data.ServiceHandler;
import com.techplicit.mycarnival.data.model.BandLocationPojo;
import com.techplicit.mycarnival.ui.fragments.AboutFragment;
import com.techplicit.mycarnival.ui.fragments.ContactFragment;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.Utility;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;


public class SmartUpdateActivity extends BaseActivity
        implements Constants, android.location.LocationListener, AboutFragment.OnFragmentInteractionListener, ContactFragment.OnFragmentInteractionListener {

    private static final int MIN_VALUE = 1;
    private static final int MY_PERMISSIONS_LOCATION = 101;
    private static final String LOG_TAG = SmartUpdateActivity.class.getName();
    private static NumberPicker bandsPicker;
    private static String mDurationStr;
    private static int mDurationValue = 1;
    private static String selectedPickerValue;
    private static TextView selectBandText;
    private static TextView updateLocation;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    public static NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private static String bandNameSelected = "Yourself", bandAddress, bandLatitude, bandLongitude, carnivalName, from;


    private static GoogleMap mMap;


    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    static boolean canGetLocation = false, isGPSDialogShowing = false;
    Location location; // location
    public static double latitude; // latitude
    public static double longitude; // longitude
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    // Declaring a Location Manager
    protected LocationManager locationManager;
    private static ArrayList<BandLocationPojo> quizModelArrayList;
    private static SharedPreferences sharedPreferences;
    private AlertDialog.Builder alertDialog;
    private AlertDialog changePassDialog;

    private FragmentManager fragmentManager;
    private Fragment currentFragment;
    public static boolean isSignedIn;
    private CallbackManager callbackManager;
    private static ImageView btnFriendFinder, btnSmartUpdate;
    public static ProgressDialog progressDialog;

    private static boolean isCreating = true;

    private static ImageView btnFetes, btnBands, btnBandLocation, btnBandUpdate;
    private ImageView updateLocationBtn;

    String[] minutesArray = new String[]{
            "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20"
    };

    String[] minutesArray1 = new String[]{
            "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "999"
    };
    private Spinner timeSpinner, updatesSpinner;
    private ScheduledFuture<?> scheduledFuture;

    private String getLastUpdateTime() {
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        Calendar c = Calendar.getInstance();
        String currentDate = ft.format(c.getTime());
        String lastAccessOn = sharedPreferences.getString(LAST_UPDATE, "");
        Date d1 = new Date();
        Date d2 = new Date();

        try {
            d1 = ft.parse(currentDate);
            d2 = ft.parse(lastAccessOn);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Exception: "+e.toString(), e);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception: "+e.toString(), e);
        }

        // in milliseconds
        long diff = d1.getTime() - d2.getTime();

        return BandsDateFormatsConverter.printDateDifferenceInUIWithDefinedFormat(diff);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.fragment_smart_update, frameLayout);
        sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
        mContext = SmartUpdateActivity.this;

        home_icon.setImageResource(R.drawable.home);
        titleHome.setVisibility(View.GONE);
        title.setText("Smart Update");

        setSubTitle();

        try {
            startUpdates();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        title.setVisibility(View.VISIBLE);

        home_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startHomeActivity(SmartUpdateActivity.this);
                finish();
            }
        });


        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_GPS_DIALOG_SHOWING, false);
        editor.commit();

        if (!Utility.isNetworkConnectionAvailable(SmartUpdateActivity.this)) {
            Utility.displayNetworkFailDialog(SmartUpdateActivity.this, NETWORK_FAIL, "Success", "Successfully Invited !");
        }

        RelativeLayout layout_select_band = (RelativeLayout) findViewById(R.id.layout_select_band);

        layout_select_band.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                displayDurationDialog(SmartUpdateActivity.this);
            }
        });

        timeSpinner = (Spinner) findViewById(R.id.spinner_time);
        updatesSpinner = (Spinner) findViewById(R.id.spinner_update);


        RelativeLayout layout_select_time = (RelativeLayout) findViewById(R.id.layout_select_time);

        layout_select_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeSpinner.performClick();
            }
        });

        RelativeLayout layout_update_select = (RelativeLayout) findViewById(R.id.layout_update_select);

        layout_update_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatesSpinner.performClick();
            }
        });

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SmartUpdateActivity.this, R.layout.spinner_layout_smart_update, minutesArray);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_smart_update);

        // attaching data adapter to spinner
        timeSpinner.setAdapter(dataAdapter);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(SmartUpdateActivity.this, R.layout.spinner_layout_smart_update, minutesArray1);

        // Drop down layout style - list view with radio button
        dataAdapter1.setDropDownViewResource(R.layout.spinner_dropdown_smart_update);


        updatesSpinner.setAdapter(dataAdapter1);

        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String minutes = minutesArray[position];
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(MINUTES_INTERVAL_SMART_UPDATE, minutes);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                String minutes = minutesArray[0];
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(MINUTES_INTERVAL_SMART_UPDATE, minutes);
                editor.commit();
            }

        });

        updatesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String updateIntervals = minutesArray1[position];
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(UPDATES_INTERVAL_SMART_UPDATE, updateIntervals);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                String updateIntervals = minutesArray1[0];
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(UPDATES_INTERVAL_SMART_UPDATE, updateIntervals);
                editor.commit();
            }

        });


        btnFetes = (ImageView) findViewById(R.id.fetes_button_hs);
        btnBands = (ImageView) findViewById(R.id.band_button_hs);
        btnBandLocation = (ImageView) findViewById(R.id.band_location_button_hs);
        btnBandUpdate = (ImageView) findViewById(R.id.band_update_button_hs);
        btnSmartUpdate = (ImageView) findViewById(R.id.smart_update_button_hs);
        updateLocationBtn = (ImageView) findViewById(R.id.start_smart_update);
        btnFriendFinder = (ImageView) findViewById(R.id.friend_finder_button_hs);

        btnSmartUpdate.setEnabled(false);

        updateLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
                String from = sharedPreferences.getString(UPDATE_LOCATION_FROM, null);
                carnivalName = sharedPreferences.getString(SELECTED_CARNIVAL_NAME, null);

                boolean isSmartUpdated = sharedPreferences.getBoolean(IS_SMART_UPDATED, true);
                if (isSmartUpdated) {
                    if (!selectBandText.getText().toString().trim().equalsIgnoreCase(getResources().getString(R.string.select_band))) {
                        if (!Utility.isNetworkConnectionAvailable(SmartUpdateActivity.this)) {
                            Utility.displayNetworkFailDialog(SmartUpdateActivity.this, NETWORK_FAIL, "Success", "Successfully Invited !");
                        } else {

                            if (latitude != 0.0 && longitude != 0.0) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(CARNIVAL_NAME_SMART_UPDATE, carnivalName);
                                editor.putString(BAND_NAME_SMART_UPDATE, bandNameSelected);

                                editor.putString(SELECTED_BAND_LATITUDE, "" + latitude);
                                editor.putString(SELECTED_BAND_LONGITUDE, "" + longitude);
                                editor.commit();

//                                if (bandNameSelected.equalsIgnoreCase("Yourself")) {
                                    if (isSignedIn = sharedPreferences.getBoolean(IS_SIGNED_IN, false)) {
                                        IntentGenerator.startSmartUpdateService(getApplicationContext());
                                        Toast.makeText(SmartUpdateActivity.this, "We will update locations for you", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(SmartUpdateActivity.this, "Please Login", Toast.LENGTH_LONG).show();
                                    }
                                /*} else {

                                    final ArrayList<BandLocationPojo> carnivalsPojoArrayList = CarnivalsSingleton.getInstance().getBandsPojoArrayList();
                                    final ArrayList<String> listBands = new ArrayList<String>();

                                    if (sharedPreferences.getBoolean(IS_IN_TRUCK_MODE, false)) {
                                        if (carnivalsPojoArrayList != null && carnivalsPojoArrayList.size() > 0) {
                                            for (int i = 0; i < carnivalsPojoArrayList.size(); i++) {
                                                BandLocationPojo carnivalsPojo = (BandLocationPojo) carnivalsPojoArrayList.get(i);
                                                listBands.add(carnivalsPojo.getName());
                                            }
                                        }
                                    }

                                    if (listBands != null && listBands.size() > 0) {
                                        int pos = listBands.indexOf(bandNameSelected);
                                        SharedPreferences.Editor editor1 = sharedPreferences.edit();
                                        editor.putInt(CARNIVAL_POSITION_SMART_UPDATE, pos);
                                        editor.commit();
                                    }

                                    IntentGenerator.startSmartUpdateService(getApplicationContext());
                                    Toast.makeText(SmartUpdateActivity.this, "We will update locations for you", Toast.LENGTH_LONG).show();
                                }*/

                            } else {
                                Toast.makeText(SmartUpdateActivity.this, "Problem in fetching current Location!", Toast.LENGTH_LONG).show();
                            }

                        }
                    } else {
                        Toast.makeText(SmartUpdateActivity.this, "Please select Band!", Toast.LENGTH_LONG).show();
                    }
                }


            }
        });

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/ftra_hv.ttf");
/*

        btnFetes.setTypeface(face);
        btnSmartUpdate.setTypeface(face);
        btnBandUpdate.setTypeface(face);
        btnBandLocation.setTypeface(face);
        btnBands.setTypeface(face);
        btnFriendFinder.setTypeface(face);
*/

        setupFriendFinderButton(SmartUpdateActivity.this);

        btnFriendFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startFriendFinderActivity(SmartUpdateActivity.this, SmartUpdateActivity.class.getSimpleName());
            }
        });

        btnBands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startBandsListActivity(SmartUpdateActivity.this);
            }
        });

        btnFetes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startFetesActivity(SmartUpdateActivity.this);
            }
        });

        btnBandLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startBankLocationUpdate(SmartUpdateActivity.this);
            }
        });

        btnBandUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*SharedPreferences sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(UPDATE_LOCATION_FROM, FROM_BAND_UPDATE_BUTTON);
                editor.commit();

                IntentGenerator.startUpdateBandLocation(SmartUpdateActivity.this, -1, null);*/
            }
        });

        btnSmartUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                IntentGenerator.startUpdateBandLocation(getApplicationContext(), -1, null);
            }
        });

        ImageView backArrowCarnivalsList = (ImageView) findViewById(R.id.back_arrow_carnivals_list);
        backArrowCarnivalsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        MapsInitializer.initialize(SmartUpdateActivity.this);

        setUpMap();

        if (bandNameSelected != null) {
//                getBandsDetails(bandNameSelected, getActivity());
        }

        selectBandText = (TextView) findViewById(R.id.text_select_band);
        updateLocation = (TextView) findViewById(R.id.update_location);

//        if (from != null && from.equalsIgnoreCase(FROM_BAND_UPDATE_BUTTON)) {
            selectBandText.setText("Yourself");
            bandNameSelected = "Yourself";
        /*} else if (from != null && from.equalsIgnoreCase(FROM_BANDS_LIST)) {
            selectBandText.setText(bandNameSelected);
        }*/

//        bandNameSelected = "Yourself";

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
        from = sharedPreferences.getString(UPDATE_LOCATION_FROM, null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
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

    public static void setupSmartUpdateButton(Activity activity, boolean status) {

        if (status) {
            btnSmartUpdate.setVisibility(View.VISIBLE);
        } else {
            btnSmartUpdate.setVisibility(View.GONE);
        }

    }

    private void setUpMap() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

            // Check if we were successful in obtaining the map.

            if (mMap != null) {
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        marker.showInfoWindow();
                        return true;
                    }
                });
            } else {
                Toast.makeText(SmartUpdateActivity.this, "Unable to create Maps", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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
        private static ListView carnivalsList;
        private ProgressDialog pDialog;
        private AlertDialog alertDialog;

        private static ImageView btnFetes, btnBands, btnBandLocation, btnBandUpdate;
        private ImageView updateLocationBtn;

        String[] minutesArray = new String[]{
                "1", "2", "3", "4", "5",
                "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15",
                "16", "17", "18", "19", "20"
        };

        String[] minutesArray1 = new String[]{
                "1", "2", "3", "4", "5",
                "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15",
                "16", "17", "18", "19", "20", "999"
        };

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
            View rootView = inflater.inflate(R.layout.fragment_smart_update, container, false);

            if (!Utility.isNetworkConnectionAvailable(getActivity())) {
                Utility.displayNetworkFailDialog(getActivity(), NETWORK_FAIL, "Success", "Successfully Invited !");
            }

            RelativeLayout layout_select_band = (RelativeLayout) rootView.findViewById(R.id.layout_select_band);

            layout_select_band.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    displayDurationDialog(getActivity());
                }
            });

            Spinner timeSpinner = (Spinner) rootView.findViewById(R.id.spinner_time);
            Spinner updatesSpinner = (Spinner) rootView.findViewById(R.id.spinner_update);

            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_layout_smart_update, minutesArray);

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_smart_update);

            // attaching data adapter to spinner
            timeSpinner.setAdapter(dataAdapter);

            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(getActivity(), R.layout.spinner_layout_smart_update, minutesArray1);

            // Drop down layout style - list view with radio button
            dataAdapter1.setDropDownViewResource(R.layout.spinner_dropdown_smart_update);


            updatesSpinner.setAdapter(dataAdapter1);

            timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    String minutes = minutesArray[position];
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(MINUTES_INTERVAL_SMART_UPDATE, minutes);
                    editor.commit();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    String minutes = minutesArray[0];
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(MINUTES_INTERVAL_SMART_UPDATE, minutes);
                    editor.commit();
                }

            });

            updatesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    String updateIntervals = minutesArray1[position];
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(UPDATES_INTERVAL_SMART_UPDATE, updateIntervals);
                    editor.commit();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    String updateIntervals = minutesArray1[0];
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(UPDATES_INTERVAL_SMART_UPDATE, updateIntervals);
                    editor.commit();
                }

            });

            btnFetes = (ImageView) rootView.findViewById(R.id.fetes_button_hs);
            btnBands = (ImageView) rootView.findViewById(R.id.band_button_hs);
            btnBandLocation = (ImageView) rootView.findViewById(R.id.band_location_button_hs);
            btnBandUpdate = (ImageView) rootView.findViewById(R.id.band_update_button_hs);
            btnSmartUpdate = (ImageView) rootView.findViewById(R.id.smart_update_button_hs);
            updateLocationBtn = (ImageView) rootView.findViewById(R.id.start_smart_update);
            btnFriendFinder = (ImageView) rootView.findViewById(R.id.friend_finder_button_hs);

            btnSmartUpdate.setEnabled(false);

            updateLocationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isSmartUpdated = sharedPreferences.getBoolean(IS_SMART_UPDATED, true);

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
                    String from = sharedPreferences.getString(UPDATE_LOCATION_FROM, null);
                    carnivalName = sharedPreferences.getString(SELECTED_CARNIVAL_NAME, null);
                    if (isSmartUpdated) {
                        if (!selectBandText.getText().toString().trim().equalsIgnoreCase(getActivity().getResources().getString(R.string.select_band))) {

                            if (!Utility.isNetworkConnectionAvailable(getActivity())) {
                                Utility.displayNetworkFailDialog(getActivity(), NETWORK_FAIL, "Success", "Successfully Invited !");
                            } else {

                                if (latitude != 0.0 && longitude != 0.0) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(CARNIVAL_NAME_SMART_UPDATE, carnivalName);
                                    editor.putString(BAND_NAME_SMART_UPDATE, bandNameSelected);

                                    editor.putString(SELECTED_BAND_LATITUDE, "" + latitude);
                                    editor.putString(SELECTED_BAND_LONGITUDE, "" + longitude);
                                    editor.commit();

                                    if (bandNameSelected.equalsIgnoreCase("Yourself")) {
                                        if (isSignedIn = sharedPreferences.getBoolean(IS_SIGNED_IN, false)) {
                                            IntentGenerator.startSmartUpdateService(getActivity().getApplicationContext());
                                            Toast.makeText(getActivity(), "We will update locations for you", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getActivity(), "Please Login", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        IntentGenerator.startSmartUpdateService(getActivity().getApplicationContext());
                                        Toast.makeText(getActivity(), "We will update locations for you", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(getActivity(), "Problem in fetching current Location!", Toast.LENGTH_LONG).show();
                                }

                            }

                        } else {
                            Toast.makeText(getActivity(), "Please select Band!", Toast.LENGTH_LONG).show();
                        }
                    }
                }

            });

            Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ftra_hv.ttf");

            /*btnFetes.setTypeface(face);
            btnSmartUpdate.setTypeface(face);
            btnBandUpdate.setTypeface(face);
            btnBandLocation.setTypeface(face);
            btnBands.setTypeface(face);
            btnFriendFinder.setTypeface(face);*/

            setupFriendFinderButton(getActivity());

            btnFriendFinder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentGenerator.startFriendFinderActivity(getActivity(), SmartUpdateActivity.class.getSimpleName());
                }
            });

            btnFetes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    IntentGenerator.startFriendFinderActivity(getActivity());
                }
            });

            btnBands.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            btnBandLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });

            btnBandUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(UPDATE_LOCATION_FROM, FROM_BAND_UPDATE_BUTTON);
                    editor.commit();

                    IntentGenerator.startUpdateBandLocation(getActivity(), -1, null);*/
                }
            });

            btnSmartUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                IntentGenerator.startUpdateBandLocation(getApplicationContext(), -1, null);
                }
            });

            ImageView backArrowCarnivalsList = (ImageView) rootView.findViewById(R.id.back_arrow_carnivals_list);
            backArrowCarnivalsList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });

            MapsInitializer.initialize(getActivity());

            setUpMap();

            if (bandNameSelected != null) {
//                getBandsDetails(bandNameSelected, getActivity());
            }

            selectBandText = (TextView) rootView.findViewById(R.id.text_select_band);
            updateLocation = (TextView) rootView.findViewById(R.id.update_location);

            if (from != null && from.equalsIgnoreCase(FROM_BAND_UPDATE_BUTTON)) {
                selectBandText.setText("Yourself");
                bandNameSelected = "Yourself";
            } else if (from != null && from.equalsIgnoreCase(FROM_BANDS_LIST)) {
                selectBandText.setText(bandNameSelected);
            }

            bandNameSelected = "Yourself";

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            btnSmartUpdate.setEnabled(false);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }

        private void setUpMap() {
            // Do a null check to confirm that we have not already instantiated the map.
            if (mMap == null) {
                // Try to obtain the map from the SupportMapFragment.
                mMap = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map)).getMap();

                // Check if we were successful in obtaining the map.

                if (mMap != null) {
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            marker.showInfoWindow();
                            return true;
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Unable to create Maps", Toast.LENGTH_SHORT).show();
                }
            }

        }


        public static class GetAsync extends AsyncTask<String, String, String> {

            ServiceHandler jsonParser = new ServiceHandler();

            private static final String TAG_SUCCESS = "success";
            private static final String TAG_MESSAGE = "message";

            private Activity mContext;
            private ProgressDialog progressDialog;
            private String responseStatus;
            private String response;

            public GetAsync(Activity context) {
                mContext = context;
            }

            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(mContext);
                progressDialog.setMessage("Please Wait! Updating Band location. ");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... args) {

                try {

                    String updateLocationUrl = Constants.BASE_URL + "updatebandlocation?carnival=" + URLEncoder.encode(carnivalName, "UTF-8") +
                            "&band=" + URLEncoder.encode(bandNameSelected, "UTF-8") + "&address=" + URLEncoder.encode(bandAddress, "UTF-8") + "&latitude=" + URLEncoder.encode(bandLatitude, "UTF-8") + "&longitude=" + URLEncoder.encode(bandLongitude, "UTF-8");

                    /*if (updateLocationUrl.contains(" & ") || updateLocationUrl.contains(" ")) {
                        updateLocationUrl = updateLocationUrl.replace(" & ", "+%26+").replace(" ", "%20").trim();
                    }*/

                    response = jsonParser.makeHttpRequest(
                            updateLocationUrl, "GET", null);

                    if (response != null && !response.equalsIgnoreCase(ERROR)) {
                        JSONObject jsonObject = new JSONObject(response);
                        responseStatus = (String) jsonObject.get(STATUS);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    responseStatus = ERROR;
                }

                return responseStatus;
            }

            protected void onPostExecute(String response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (response != null && !response.equalsIgnoreCase(ERROR)) {

                    if (response != null && response.equalsIgnoreCase("Success")) {
                        displayUpdateLocationStatus(mContext, "Success");
                        editor.putBoolean(IS_LOCATION_UPDATED, true);
                        editor.putBoolean(IS_ALPH_SORT_NEEDS_TO_LOAD, true);
                        editor.putBoolean(IS_DISATNCE_NEEDS_TO_LOAD, true);
                        editor.putBoolean(IS_FAVS_NEEDS_TO_LOAD, true);
                        editor.putBoolean(IS_VIEW_NEEDS_TO_LOAD, true);
                    } else {
                        displayUpdateLocationStatus(mContext, "Fail");
                        editor.putBoolean(IS_LOCATION_UPDATED, false);
                        editor.putBoolean(IS_ALPH_SORT_NEEDS_TO_LOAD, false);
                        editor.putBoolean(IS_DISATNCE_NEEDS_TO_LOAD, false);
                        editor.putBoolean(IS_FAVS_NEEDS_TO_LOAD, false);
                        editor.putBoolean(IS_VIEW_NEEDS_TO_LOAD, false);
                    }

                    editor.putString(SELECTED_BAND_NAME, null);
                    editor.putString(SELECTED_BAND_ADDRESS, null);
                    editor.putString(SELECTED_BAND_LATITUDE, null);
                    editor.putString(SELECTED_BAND_LONGITUDE, null);
                    editor.putString(UPDATE_LOCATION_FROM, null);
                    editor.commit();

                } else {
                    Utility.displayNetworkFailDialog(mContext, ERROR, "Success", "Successfully Invited !");
                }

            }

        }


    }

    private static void displayUpdateLocationDialog(final Activity context, String fromBandUpdateButton) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_location);
//        dialog.setCancelable(false);

        selectBandText = (TextView) dialog.findViewById(R.id.text_select_band);
        updateLocation = (TextView) dialog.findViewById(R.id.update_location);
        ImageView selectBandImage = (ImageView) dialog.findViewById(R.id.image_select_band);

        RelativeLayout layout_select_band = (RelativeLayout) dialog.findViewById(R.id.layout_select_band);
        if (fromBandUpdateButton != null && fromBandUpdateButton.equalsIgnoreCase(FROM_BAND_UPDATE_BUTTON)) {
            selectBandText.setText(context.getResources().getString(R.string.select_band));
        } else if (bandNameSelected != null) {
            selectBandText.setText(bandNameSelected);
        }


        layout_select_band.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                displayDurationDialog(context);
            }
        });

        dialog.show();

    }

    private static void displayUpdateLocationStatus(final Activity context, String status) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.update_location_status);
//        dialog.setCancelable(false);

        TextView status_title = (TextView) dialog.findViewById(R.id.status_title);
        TextView status_message = (TextView) dialog.findViewById(R.id.status_message);
        TextView ok_text = (TextView) dialog.findViewById(R.id.ok_text);

        if (status != null && status.equalsIgnoreCase("Success")) {
            status_title.setText("" + context.getResources().getString(R.string.success_title));
            status_message.setText("" + context.getResources().getString(R.string.status_message_update_success));
        } else {
            status_title.setText("" + context.getResources().getString(R.string.success_title));
            status_message.setText("" + context.getResources().getString(R.string.status_message_update_fail));
        }

        ok_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private static void getBandsDetails(String bandName, Activity context) {
        final ArrayList<BandLocationPojo> carnivalsPojoArrayList = CarnivalsSingleton.getInstance().getBandsPojoArrayList();

        for (int i = 0; i < carnivalsPojoArrayList.size(); i++) {

            BandLocationPojo carnivalsPojo = (BandLocationPojo) carnivalsPojoArrayList.get(i);
            if (carnivalsPojo.getName().contains(bandName)) {
                bandNameSelected = carnivalsPojo.getName();
                bandAddress = carnivalsPojo.getAddress();
                bandLatitude = carnivalsPojo.getLatitude();
                bandLongitude = carnivalsPojo.getLongitude();

                return;
            }

        }


    }


    public static void displayDurationDialog(final Activity context) {

        final Dialog mDurationDialog = new Dialog(context);
        mDurationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDurationDialog.setContentView(R.layout.dialog_band_picker);

        bandsPicker = (NumberPicker) mDurationDialog.findViewById(R.id.band_picker);
        TextView selectPicker = (TextView) mDurationDialog.findViewById(R.id.select_band_picker);
        TextView cancelPicker = (TextView) mDurationDialog.findViewById(R.id.cancel_band_picker);

        bandsPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        UtilityCommon.setNumberPickerTextColor(context, bandsPicker, context.getResources().getColor(R.color.red));

        final ArrayList<BandLocationPojo> carnivalsPojoArrayList = CarnivalsSingleton.getInstance().getBandsPojoArrayList();
        final ArrayList<String> listBands = new ArrayList<String>();
        listBands.add("Yourself");

        if (sharedPreferences.getBoolean(IS_IN_TRUCK_MODE, false)) {
            if (carnivalsPojoArrayList != null && carnivalsPojoArrayList.size() > 0) {
                for (int i = 0; i < carnivalsPojoArrayList.size(); i++) {
                    BandLocationPojo carnivalsPojo = (BandLocationPojo) carnivalsPojoArrayList.get(i);
                    listBands.add(carnivalsPojo.getName());
                }
            }
        }

        selectPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDurationDialog.dismiss();

                bandNameSelected = "" + listBands.get(mDurationValue - 1);
                if (bandNameSelected != null) {
                    selectBandText.setText(bandNameSelected);
//                    getBandsDetails(bandNameSelected, context);
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

        String[] textDurationValues = new String[listBands.size()];
        textDurationValues = listBands.toArray(textDurationValues);

        bandsPicker.setMinValue(MIN_VALUE);
        bandsPicker.setMaxValue(listBands.size());
        bandsPicker.setWrapSelectorWheel(false);
        bandsPicker.setDisplayedValues(textDurationValues);
        bandsPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                bandsPicker.getContentDescription();
                mDurationValue = newVal;
                bandNameSelected = listBands.get(newVal - 1);
                if (bandNameSelected != null) {
                    selectBandText.setText(bandNameSelected);
                }
            }
        });

        mDurationDialog.show();
    }

    private static void setUpdateLocationSpinnerItems() {

    }


    private static void plotMarkers(String lat, String lng, String name, Activity context) {
        // Create user marker with custom icon and other options
        MarkerOptions markerOption = new MarkerOptions().position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
        markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.app_icon));

        Marker currentMarker = mMap.addMarker(markerOption);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)), 15));
        mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter(name, context));
    }

    public static class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private String mName;
        private Activity mContext;

        public MarkerInfoWindowAdapter(String name, Activity context) {
            mName = name;
            mContext = context;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View v = mContext.getLayoutInflater().inflate(R.layout.custom_marker_icon, null);

            ImageView markerIcon = (ImageView) v.findViewById(R.id.marker_icon);

            TextView markerLabel = (TextView) v.findViewById(R.id.marker_text);

            markerLabel.setText(mName);

            return v;
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
                    if (ActivityCompat.checkSelfPermission(SmartUpdateActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SmartUpdateActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                        ActivityCompat.requestPermissions(SmartUpdateActivity.this,
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
            if (ActivityCompat.checkSelfPermission(SmartUpdateActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SmartUpdateActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(SmartUpdateActivity.this);
        }
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public void showSettingsAlert() {
        alertDialog = new AlertDialog.Builder(SmartUpdateActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                SmartUpdateActivity.this.startActivity(intent);
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
            Log.e(LOG_TAG, "Problem with GPS Settings Alert --> " + e.toString());
        }

        changePassDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean isLoggedIn = sharedPreferences.getBoolean(IS_SIGNED_IN, false);
        if (isLoggedIn) {
            btnFriendFinder.setVisibility(View.VISIBLE);
        } else {
            btnFriendFinder.setVisibility(View.GONE);
        }

        isCreating = false;
        if (changePassDialog != null && changePassDialog.isShowing()) {
            changePassDialog.dismiss();
        }

        if (!isGPSEnabled && !isNetworkEnabled) {
            getLocation(SmartUpdateActivity.this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isGPSDialogShowing = false;

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
    protected void onStop() {
        super.onStop();
        isGPSDialogShowing = true;
        EventBus.getDefault().unregister(this);
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

    // This method will be called when a JustAnotherEvent is posted

    public void onEvent(String timeStamp) {
        setSubTitle();

        try {
            startUpdates();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        sub_title.setText("Last Update : " + sharedPreferences.getString(LAST_UPDATE, ""));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCreating = true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            setupFriendFinderButton(SmartUpdateActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error in onRestart()--> " + e.toString());
        }

    }

    public void startUpdates() throws InterruptedException {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

        Runnable r = new Runnable() {
            @Override
            public void run() {

                setSubTitle();
            }
        };

        scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(r, 0, 1, TimeUnit.MINUTES);
    }

    private void setSubTitle() {
        if (sharedPreferences.getString(LAST_UPDATE, null) != null) {
            String lastUpdateTime = getLastUpdateTime();
            if (UtilityCommon.isStringValid(lastUpdateTime)) {
                sub_title.setText("Last Update : " + lastUpdateTime);
                sub_title.setVisibility(View.VISIBLE);
            }
        }
    }


}
