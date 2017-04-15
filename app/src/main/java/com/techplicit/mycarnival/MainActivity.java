package com.techplicit.mycarnival;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.techplicit.mycarnival.data.model.FavouritesPojo;
import com.techplicit.mycarnival.ui.activities.BaseActivity;
import com.techplicit.mycarnival.ui.activities.FriendFinderTabsActivity;
import com.techplicit.mycarnival.ui.fragments.AboutFragment;
import com.techplicit.mycarnival.ui.fragments.ContactFragment;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.Utils;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity
        implements AboutFragment.OnFragmentInteractionListener, ContactFragment.OnFragmentInteractionListener, Constants {

    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int NOTIFY_ME_ID = 1000;
    //Permision code that will be checked in the method onRequestPermissionsResult
    private int STORAGE_PERMISSION_CODE = 23;
    private int LOCATION_PERMISSION_CODE = 25;
    private final int ALL_PERMISSION_CODE = 28;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    public static NavigationDrawerFragment mNavigationDrawerFragment;

    private static SharedPreferences sharedPreferences;

    private GoogleCloudMessaging gcm;
    private String regid;
    public static ProgressDialog progressDialog;
    private boolean isCreating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isCreating = true;
        getLayoutInflater().inflate(R.layout.fragment_main, frameLayout);
        if (Utils.getFavourites() == null) {
            FavouritesPojo favouritesPojo = new FavouritesPojo();
            Utils.storeFavourites(favouritesPojo);
        }
        mContext = MainActivity.this;

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo("com.techplicit.mycarnival", PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }

        printKeyHash();

        sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);

        titleHome.setVisibility(View.VISIBLE);
        titleHome.setText(getResources().getString(R.string.app_name));
        title.setVisibility(View.GONE);

        Button buttonCarnivalHome = (Button) findViewById(R.id.button_carnival_home);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/ftra_hv.ttf");
        buttonCarnivalHome.setTypeface(face);

        buttonCarnivalHome.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {
                                                      startActivity(new Intent(MainActivity.this, CarnivalsListActivity.class));
                                                  }
                                              }
        );


        if (Build.VERSION.SDK_INT < 23) {
            //Do not need to check the permission
        } else {
            checkAndRequestPermissions();
        }
    }

    private void printKeyHash(){
        Log.d(LOG_TAG, "printKeyHash");
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.techplicit.mycarnival",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d(LOG_TAG, "HashKey: "+Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(LOG_TAG, e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.d(LOG_TAG, e.toString());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isCreating = false;
    }

    private boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), ALL_PERMISSION_CODE);
            return false;
        }
        return true;
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                //denied
                Toast.makeText(this, "Oops you denied the permission", Toast.LENGTH_LONG).show();
            } else {
                if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                    //allowed
                    Toast.makeText(this, "Permission granted ", Toast.LENGTH_LONG).show();
                } else {
                    //set to never ask again
                    Log.e("set to never ask again", permission);
                    promptSettings();
                }
            }
        }

//        switch (requestCode) {
//            case ALL_PERMISSION_CODE :
//            {
//                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    // permissions granted.
//                    Toast.makeText(this, "Permission granted ", Toast.LENGTH_LONG).show();
//                } else {
//                    // no permissions granted.
//                    Toast.makeText(this, "Oops you denied the permission", Toast.LENGTH_LONG).show();
//                }
//                return;
//            }
//        }


        //Checking the request code of our request
//        if (requestCode == STORAGE_PERMISSION_CODE) {
//
//            //If permission is granted
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                //Displaying a toast
//                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
//            } else {
//                //Displaying another toast if permission is not granted
//                Toast.makeText(this, "Oops you just denied the storage permission", Toast.LENGTH_LONG).show();
//            }
//            if (!isLocationAllowed()) {
//                requestLocationPermission();
//            }
//        } else if (requestCode == LOCATION_PERMISSION_CODE) {
//            //If permission is granted
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                //Displaying a toast
//                Toast.makeText(this, "Permission granted now you can get location", Toast.LENGTH_LONG).show();
//            } else {
//                //Displaying another toast if permission is not granted
//                Toast.makeText(this, "Oops you just denied the location permission", Toast.LENGTH_LONG).show();
//            }
//        }
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

    private Toolbar setupToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        toolbar.setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setTitle("");
        return toolbar;
    }

    private void getKeyHashValue() {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                editor.putString(KEY_HASH_VALUE, Base64.encodeToString(md.digest(), Base64.DEFAULT).toString());
                editor.commit();
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(LOG_TAG, "Exception-> " + e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e(LOG_TAG, "Exception-> " + e.toString());
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
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            Button buttonCarnivalHome = (Button) rootView.findViewById(R.id.button_carnival_home);

            Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ftra_hv.ttf");
            buttonCarnivalHome.setTypeface(face);

            buttonCarnivalHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), CarnivalsListActivity.class));
                }
            });

            return rootView;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mContext = MainActivity.this;
        isCreating = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
//            mNavigationDrawerFragment.changeNavigationList(MainActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error in onRestart()--> " + e.toString());
        }
    }

    public void getRegId() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }

                    regid = gcm.register(getResources().getString(R.string.gcm_project_id));
                    msg = "Device registered, registration ID=" + regid;
                    Log.i("GCM", msg);

                } catch (IOException ex) {
                    regid = "Error :" + ex.getMessage();
                }
                return regid;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.e(LOG_TAG, "getRegId msg--> " + msg);

                if (msg != null && !msg.equalsIgnoreCase("")) {
                    Log.e(LOG_TAG, "getRegId regid-->" + msg);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(GCM_REG_ID, msg);
                    editor.commit();
                }

            }
        }.execute(null, null, null);
    }

    private void sendNotification(Context c) {
        // Use NotificationCompat.Builder to set up our notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        //icon appears in device notification bar and right hand corner of notification
        builder.setSmallIcon(R.drawable.app_icon);

        // This intent is fired when notification is clicked
        Intent intent = new Intent(MainActivity.this, FriendFinderTabsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Set the intent that will fire when the user taps the notification.
        builder.setContentIntent(pendingIntent);

        // Large icon appears on the left of the notification
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.app_icon));

        // Content title, which appears in large type at the top of the notification
        builder.setContentTitle("Notifications Title");

        // Content text, which appears in smaller text below the title
        builder.setContentText("Your notification content here.");

        // The subtext, which appears under the text on newer devices.
        // This will show-up in the devices with Android 4.2 and above only
        builder.setSubText("Tap to view documentation about notifications.");

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Will display the notification in the notification bar
        notificationManager.notify(100, builder.build());
    }


}
