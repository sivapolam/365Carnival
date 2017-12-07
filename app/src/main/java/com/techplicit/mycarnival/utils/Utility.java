package com.techplicit.mycarnival.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.techplicit.mycarnival.MainActivity;
import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.ui.activities.FriendFinderTabsActivity;

import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by pnaganjane001 on 17/12/15.
 */
public class Utility implements Constants {

    private static final String TAG = Utility.class.getName();
    private static AlertDialog alertDialog;
    private static AlertDialog.Builder alertDialogSettings;
    public static AlertDialog changePassDialog;
    public static PowerManager.WakeLock wakeLock;
    private CameraUpdate cameraUpdate;
    private static Calendar calendar = Calendar.getInstance();
    public static String bandName=null;
    private static String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    // added as an instance method to an Activity
    public static boolean isNetworkConnectionAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }

    public static String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month - 1];
    }

    public static void displayNetworkFailDialog(final Activity context, final String type, String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        if (type.equalsIgnoreCase(STATUS)) {
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setTitle(title);

            final AlertDialog.Builder ok = alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }
                }
            });

        } else {
            if (type.equalsIgnoreCase(NETWORK_FAIL)) {
                alertDialogBuilder.setMessage(context.getResources().getString(R.string.network_fail_message));
                alertDialogBuilder.setTitle(context.getResources().getString(R.string.network_status));
            } else if (type.equalsIgnoreCase(ERROR)) {
                alertDialogBuilder.setMessage(context.getResources().getString(R.string.error_message));
                alertDialogBuilder.setTitle(context.getResources().getString(R.string.error_status));
            }

            final AlertDialog.Builder ok = alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    if (alertDialog != null) {
                        alertDialog.dismiss();
                        if (type.equalsIgnoreCase(ERROR) && (context instanceof FriendFinderTabsActivity || context instanceof MainActivity)) {

                        } else {

                            context.finish();
                        }

                        if (type.equalsIgnoreCase(NETWORK_FAIL) && context instanceof MainActivity) {

                        } else {

                            context.finish();
                        }

                    }
                }
            });

        }

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public static double calculateLatLangDistances(double lat1, double long1, double lat2, double long2) {

        Log.e("calculate", "lat1--> " + lat1 + " long1--> " + long1);
        Log.e("calculate", "lat2--> " + lat2 + " long2--> " + long2);

        Location locationA = new Location("point A");
        locationA.setLatitude(lat1);
        locationA.setLongitude(long1);

        Location locationB = new Location("point B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(long2);

        double d = locationA.distanceTo(locationB);

        return locationA.distanceTo(locationB);
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        Log.e(TAG, "latitude1--> " + lat1 + "longitude1--> " + lon1);
        Log.e(TAG, "latitude2--> " + lat2 + "longitude2--> " + lon2);

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
//        if (unit == "K") {
        dist = dist * 1.609344;
//        } else if (unit == "N") {
//            dist = dist * 0.8684;
//        }

        Log.e(TAG, "dist--> " + dist);

        return (dist);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }


    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public static AlertDialog showSettingsAlert(final Activity activity) {
        alertDialogSettings = new AlertDialog.Builder(activity);
        // Setting Dialog Title
        alertDialogSettings.setTitle("GPS is settings");
        // Setting Dialog Message
        alertDialogSettings.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        // On pressing Settings button
        alertDialogSettings.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(intent);
                dialog.cancel();
            }
        });

        // on pressing cancel button
        alertDialogSettings.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // Showing Alert Message

        changePassDialog = alertDialogSettings.create();

        try {
            changePassDialog.dismiss();
        } catch (Exception e) {
            Log.e(TAG, "Problem with GPS Settings Alert --> " + e.toString());
        }

        changePassDialog.show();

        /*if (!isGPSDialogShowing){
            alertDialog.show();
            isGPSDialogShowing = true;
        }*/


        return changePassDialog;
    }

    /*public static void clearAppCache(Context activity){

        if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
            ((ActivityManager)activity.getSystemService(Context.ACTIVITY_SERVICE))
                    .clearApplicationUserData(); // note: it has a return value!
        } else {
            // use old hacky way, which can be removed
            // once minSdkVersion goes above 19 in a few years.
        }
    }*/


    public static String getErrorMessage(JSONObject object) {

        String inviteStatus = null;


        if (object.has(ERR000)) {
            inviteStatus = object.optString(ERR000);
        }

        if (object.has(ERR001)) {
            inviteStatus = object.optString(ERR001);
        }

        if (object.has(ERR002)) {
            inviteStatus = object.optString(ERR002);
        }

        if (object.has(ERR003)) {
            inviteStatus = object.optString(ERR003);
        }

        if (object.has(ERR004)) {
            inviteStatus = object.optString(ERR004);
        }

        if (object.has(ERR005)) {
            inviteStatus = object.optString(ERR005);
        }

        if (object.has(ERR006)) {
            inviteStatus = object.optString(ERR006);
        }

        if (object.has(ERR007)) {
            inviteStatus = object.optString(ERR007);
        }

        if (object.has(ERR008)) {
            inviteStatus = object.optString(ERR008);
        }

        if (object.has(ERR009)) {
            inviteStatus = object.optString(ERR009);
        }

        if (object.has(ERR010)) {
            inviteStatus = object.optString(ERR010);
        }

        if (object.has(ERR011)) {
            inviteStatus = object.optString(ERR011);
        }

        if (object.has(ERR012)) {
            inviteStatus = object.optString(ERR012);
        }

        return inviteStatus;
    }


    public static void preventLockScreen(Activity context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
        wakeLock.acquire();
    }

    public static String convertDateFormate(String date, String inputFormat, String outputFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(inputFormat);
        Date testDate = null;
        try {
            testDate = sdf.parse(date);
            SimpleDateFormat formatter = new SimpleDateFormat(outputFormat);
            return formatter.format(testDate);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static String convertMilliSecsToDate(String milliSecs) {
        if (TextUtils.isEmpty(milliSecs))
            return " ";
        else {
            long milliSeconds = Long.parseLong(milliSecs);
            calendar.setTimeInMillis(milliSeconds);
            int mYear = calendar.get(Calendar.YEAR);
            int mMonth = calendar.get(Calendar.MONTH);
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
            return mDay + "-" + monthNames[mMonth] + "-" + mYear;
        }
    }

    public static void showAlertDialog(final Activity activityContext, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activityContext);
        alertDialogBuilder.setMessage(message);

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    activityContext.finish();
                }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


}
