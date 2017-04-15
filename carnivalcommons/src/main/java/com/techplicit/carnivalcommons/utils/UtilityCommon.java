package com.techplicit.carnivalcommons.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.techplicit.carnivalcommons.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.techplicit.carnivalcommons.utils.Constants.LAST_KNOWN_ADDRESS;
import static com.techplicit.carnivalcommons.utils.Constants.PREFS_BAND_ADMIN;

/**
 * Utility Class
 */

public class UtilityCommon {

    private static final String LOG_TAG = UtilityCommon.class.getSimpleName();
    private static ProgressDialog progressDialog;

    /**
     * Set Number picker text color
     *
     * @param activity - Activity context
     * @param numberPicker - Number Picker object
     * @param color - color
     * @return
     */
    public static boolean setNumberPickerTextColor(Activity activity, NumberPicker numberPicker, int color) {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(activity.getResources().getColor(R.color.white));
                    ((EditText) child).setTextColor(activity.getResources().getColor(R.color.white));
                    numberPicker.invalidate();

                    Field[] pickerFields = NumberPicker.class.getDeclaredFields();
                    for (Field pf : pickerFields) {
                        if (pf.getName().equals("mSelectionDivider")) {
                            pf.setAccessible(true);
                            try {
                                ColorDrawable colorDrawable = new ColorDrawable(activity.getResources().getColor(R.color.red));
                                pf.set(numberPicker, colorDrawable);
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            } catch (Resources.NotFoundException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }

                    return true;
                } catch (NoSuchFieldException e) {
                    Log.w("NumberPickerTextColor1", e);
                } catch (IllegalAccessException e) {
                    Log.w("NumberPickerTextColor2", e);
                } catch (IllegalArgumentException e) {
                    Log.w("NumberPickerTextColor3", e);
                }
            }
        }
        return false;
    }


    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static void showProgressDialog(Context context, String message) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public static void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    // added as an instance method to an Activity
    public static boolean isNetworkConnectionAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }

    public static void displayNetworkFailDialog(Context context, String type) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        if (type.equalsIgnoreCase(Constants.NETWORK_FAIL)) {
            alertDialogBuilder.setMessage(context.getResources().getString(R.string.network_fail_message));
            alertDialogBuilder.setTitle(context.getResources().getString(R.string.network_status));
        } else {
            alertDialogBuilder.setMessage(context.getResources().getString(R.string.error_message));
            alertDialogBuilder.setTitle(context.getResources().getString(R.string.error_status));
        }

        alertDialogBuilder.setPositiveButton("Ok", null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public static AlertDialog showSettingsAlert(final Activity activity) {
        AlertDialog.Builder alertDialogSettings = new AlertDialog.Builder(activity);
        // Setting Dialog Title
        alertDialogSettings.setTitle("Enable GPS");
        // Setting Dialog Message
        alertDialogSettings.setMessage("Problem in Fetching Location... \nGPS is not enabled. Do you want to go to settings menu?");
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

        AlertDialog changePassDialog = alertDialogSettings.create();

        try {
            changePassDialog.dismiss();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Problem with GPS Settings Alert --> " + e.toString(), e);
        }

        changePassDialog.show();

        return changePassDialog;
    }

    public static boolean isStringValid(String string) {
        if (string != null && !string.isEmpty() && !string.equalsIgnoreCase("null")) {
            return true;
        }
        return false;
    }

    public static String getCurrentLocationAddress(Context context, double latitude, double longitude) {
        String bandAddress = null;
        if (latitude != 0.0 && longitude != 0.0) {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            if (geocoder != null) {
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "exception: "+e.toString(), e);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "exception: "+e.toString(), e);
                }

                StringBuilder builder = new StringBuilder();

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    if (address.getAddressLine(0) != null) {
                        builder.append(address.getAddressLine(0)).append(", ");
                    }

                    if (address.getLocality() != null) {
                        builder.append(address.getLocality()).append(", ");
                    }

                    if (address.getAdminArea() != null) {
                        builder.append(address.getAdminArea()).append(", ");
                    }

                    if (address.getCountryName() != null) {
                        builder.append(address.getCountryName()).append(", ");
                    }

                    if (address.getPostalCode() != null) {
                        builder.append(address.getPostalCode());
                    }
                }

                bandAddress = builder.toString().replace("?", "");
                Log.d(LOG_TAG, "Address is: "+bandAddress);

                SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_BAND_ADMIN, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(LAST_KNOWN_ADDRESS, bandAddress);
                editor.apply();
            }

        }

        return bandAddress;

    }


    public static String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month - 1];
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {

        }
    }

}
