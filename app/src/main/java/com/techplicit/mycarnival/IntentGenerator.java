package com.techplicit.mycarnival;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.techplicit.mycarnival.data.model.FeteDetailModel;
import com.techplicit.mycarnival.ui.activities.AboutUsActivity;
import com.techplicit.mycarnival.ui.activities.BandTabsActivity;
import com.techplicit.mycarnival.ui.activities.FriendFinderTabsActivity;
import com.techplicit.mycarnival.ui.activities.PrivacyPolicyActivity;
import com.techplicit.mycarnival.ui.activities.SmartUpdateActivity;
import com.techplicit.mycarnival.ui.activities.UpdateBandLocationActivity;
import com.techplicit.mycarnival.utils.Constants;

/**
 * Created by pnaganjane001 on 19/12/15.
 */
public class IntentGenerator implements Constants {


    private static final String TAG = IntentGenerator.class.getName();
    private static Intent smartUpdateService;

    public static void startUpdateBandLocation(Context context, int position, String bandName) {
        Intent i = new Intent(context, UpdateBandLocationActivity.class);
        i.putExtra(POSITION, position);
        i.putExtra(BAND_NAME, bandName);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public static void startBankLocationUpdate(Context context) {
        Intent bandIntent = new Intent(context, BandTabsActivity.class);
        context.startActivity(bandIntent);
    }

    public static void startSmartUpdateActivity(Context context, int position, String bandName) {
        Intent i = new Intent(context, SmartUpdateActivity.class);
        i.putExtra(POSITION, position);
        i.putExtra(BAND_NAME, bandName);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public static void startBandsListActivity(Context context) {
        Intent bandIntent = new Intent(context, BandsActivity.class);
        bandIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(bandIntent);
    }

    public static void startFetesActivity(Context context) {
        Intent bandIntent = new Intent(context, FetesActivity.class);
        bandIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(bandIntent);
    }

    public static void startFeteDetailActivity(Context context, FeteDetailModel feteDetailModel) {
        Intent bandIntent = new Intent(context, FeteDetailActivity.class);
        bandIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.v("feteDetailModel", "feteDetailModel intent " + feteDetailModel);
        bandIntent.putExtra("feteDetailModel", feteDetailModel);
        context.startActivity(bandIntent);
    }

    public static void startFullImageActivity(Context context, String imageUrl) {
        Intent bandIntent = new Intent(context, ImageDialogActivity.class);
        bandIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        bandIntent.putExtra("imageUrl", imageUrl);
        context.startActivity(bandIntent);
    }

    public static void startBandSection(Context context, String bandName) {
        Intent bandIntent = new Intent(context, BandSectionActivity.class);
        bandIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        bandIntent.putExtra("bandName", bandName);
        context.startActivity(bandIntent);
    }

    public static void startBandSectionInfo(Context context, String bandName) {
        Intent bandIntent = new Intent(context, BandSectionInfoActivity.class);
        bandIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        bandIntent.putExtra("bandSectionName", bandName);
        context.startActivity(bandIntent);
    }

    public static void startHomeActivity(Context context) {
        Intent bandIntent = new Intent(context, MainActivity.class);
        bandIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        bandIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(bandIntent);
    }

    public static void startFriendFinderActivity(Context context, String from) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
        boolean isSignedIn = sharedPreferences.getBoolean(IS_SIGNED_IN, false);
        if (isSignedIn) {
            Intent bandIntent = new Intent(context, FriendFinderTabsActivity.class);
            bandIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            bandIntent.putExtra("FROM", from);
            context.startActivity(bandIntent);
        } else {
            Toast.makeText(context, "Please Login", Toast.LENGTH_LONG).show();
        }
    }

    public static void startAboutUsActivity(Context context) {
        Intent bandIntent = new Intent(context, AboutUsActivity.class);
        bandIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        bandIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(bandIntent);
    }

    public static void startPrivacyPolicyActivity(Context context) {
        Intent bandIntent = new Intent(context, PrivacyPolicyActivity.class);
        bandIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        bandIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(bandIntent);
    }

    public static void startSmartUpdateService(Context context) {
        smartUpdateService = new Intent(context, SmartUpdateService.class);
        try {
            context.stopService(smartUpdateService);
        } catch (Exception e) {
            Log.e(TAG, "startSmartUpdateService --> error: " + e.toString());
        }

        context.startService(smartUpdateService);
    }

    public static void stopSmartUpdateService(Context context) {
        if (smartUpdateService != null) {
            try {
                context.stopService(smartUpdateService);
            } catch (Exception e) {
                Log.e(TAG, "stopSmartUpdateService --> error: " + e.toString());
            }
        }
    }

    public static void startContactEmails(Activity context) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getResources().getString(R.string.email)});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.app_name) + " " + context.getResources().getString(R.string.version_name));
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));

    }

}
