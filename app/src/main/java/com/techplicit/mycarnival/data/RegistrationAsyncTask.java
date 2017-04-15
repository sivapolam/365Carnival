package com.techplicit.mycarnival.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.techplicit.mycarnival.BandSectionActivity;
import com.techplicit.mycarnival.BandSectionInfoActivity;
import com.techplicit.mycarnival.BandsActivity;
import com.techplicit.mycarnival.FetesActivity;
import com.techplicit.mycarnival.Logger;
import com.techplicit.mycarnival.NavigationDrawerFragment;
import com.techplicit.mycarnival.ui.activities.BandTabsActivity;
import com.techplicit.mycarnival.ui.activities.BaseActivity;
import com.techplicit.mycarnival.ui.activities.FriendFinderTabsActivity;
import com.techplicit.mycarnival.ui.activities.SmartUpdateActivity;
import com.techplicit.mycarnival.ui.activities.UpdateBandLocationActivity;
import com.techplicit.mycarnival.ui.fragments.WhoAreMyFriendsFragment;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * Created by pnaganjane001 on 25/12/15.
 */
public class RegistrationAsyncTask extends AsyncTask<String, String, String> implements Constants {
    private static final String LOG_TAG = RegistrationAsyncTask.class.getName();

    private SharedPreferences sharedPreferences;
    ServiceHandler jsonParser = new ServiceHandler();

    private Activity mContext;
    private String responseStatus;
    private JSONObject object;
    NavigationDrawerFragment navigationDrawerFragment;
    private String inviteStatus;
    public RegistrationAsyncTask(Activity context, JSONObject jsonObject, NavigationDrawerFragment mNavigationDrawerFragment) {
        mContext = context;
        object = jsonObject;
        sharedPreferences = mContext.getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
        navigationDrawerFragment = mNavigationDrawerFragment;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... args) {

        try {

            responseStatus = jsonParser.makePostRequest(
                    REGISTRATION_URL, object);

                return responseStatus;

        } catch (Exception e) {
            Logger.addRecordToLog("FB regAsync e111 --> "+e.toString());
            e.printStackTrace();
            responseStatus = ERROR;
        }

        return null;
    }

    protected void onPostExecute(String responseStatus) {


        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (responseStatus != null && !responseStatus.equals(ERROR)) {

            try {
                JSONObject jsonObject = new JSONObject(responseStatus);
                inviteStatus = jsonObject.optString(STATUS_INVITE);
                Logger.addRecordToLog("FB regAsync inviteStatus--> "+inviteStatus);

                if (inviteStatus != null) {

                    if (!inviteStatus.equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.optJSONArray(EXPLANATION_INVITE);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            inviteStatus = Utility.getErrorMessage(object);

                            Utility.displayNetworkFailDialog(mContext, STATUS, "Failed", inviteStatus);
                        }

                        Toast.makeText(mContext, "SignIn Failed!", Toast.LENGTH_LONG).show();
                        editor.putBoolean(IS_SIGNED_IN, false);

                    } else {
                        Toast.makeText(mContext, "Signed In Successfully!", Toast.LENGTH_LONG).show();

                        editor.putBoolean(IS_SIGNED_IN, true);

                        JSONObject jsonObject1 = new JSONObject(responseStatus);

                        editor.putString(REGISTRATION_STATUS, jsonObject1.optString(REGISTRATION_STATUS));

                        JSONObject dataObject = jsonObject.optJSONObject(DATA);

                        JSONObject locationObject = null;

                        if (dataObject.has(LOCATION)){
                            locationObject = jsonObject.optJSONObject(LOCATION);
                        }

                        if (locationObject!=null && locationObject.has(USER_LATITUDE)){
                            editor.putString(USER_LATITUDE, locationObject.optString(USER_LATITUDE));
                        }

                        if (locationObject!=null && locationObject.has(USER_LONGITUDE)){
                            editor.putString(USER_LONGITUDE, locationObject.optString(USER_LONGITUDE));
                        }

                        if (locationObject!=null && locationObject.has(USER_LAST_UPDATED)){
                            editor.putString(USER_LAST_UPDATED, locationObject.optString(USER_LAST_UPDATED));
                        }

                        editor.putString(FIRST_NAME, dataObject.optString(FIRST_NAME));
                        editor.putString(LAST_NAME, dataObject.optString(LAST_NAME));
                        editor.putString(EMAIL, dataObject.optString(EMAIL));
                        editor.putString(IMAGE, dataObject.optString(IMAGE));
                        editor.putString(ACCESS_TOKEN, dataObject.optString(ACCESS_TOKEN));
                        editor.putString(USER_ID, dataObject.optString(USER_ID));
                        editor.putString(USER_STATUS, dataObject.optString(USER_STATUS));
                        editor.putString(LOGIN_MEDIUM, dataObject.optString(LOGIN_MEDIUM));
                        editor.putString(USER_PRIVACY, dataObject.optString(USER_PRIVACY));
                        editor.commit();

                    }

                }
            } catch (Exception e) {
                Logger.addRecordToLog("FB regAsync e --> "+e.toString());
                e.printStackTrace();
            }

        } else {
            Logger.addRecordToLog("FB regAsync error--> "+ERROR);

            Utility.displayNetworkFailDialog(mContext, ERROR, "", "");
        }

        if (mContext instanceof BaseActivity){
            if (BaseActivity.progressDialog!=null){
                BaseActivity.progressDialog.dismiss();
                BaseActivity.mNavigationDrawerFragment.changeNavigationList(mContext);
            }
        }

        if (mContext instanceof FetesActivity) {
            Log.e(LOG_TAG, "BandsActivity");

            FetesActivity.setupFriendFinderButton(mContext);
        }

        if (mContext instanceof BandSectionInfoActivity) {
            Log.e(LOG_TAG, "BandsActivity");

            BandSectionInfoActivity.setupFriendFinderButton(mContext);
        }

        if (mContext instanceof BandSectionActivity) {
            Log.e(LOG_TAG, "BandsActivity");

            BandSectionActivity.setupFriendFinderButton(mContext);
        }

        if (mContext instanceof BandsActivity) {
            Log.e(LOG_TAG, "BandsActivity");
            BandsActivity.setupFriendFinderButton(mContext);
        }

        if (mContext instanceof BandTabsActivity) {
            Log.e(LOG_TAG, "BandsActivity");
            BandTabsActivity bandTabsActivity = new BandTabsActivity();
            bandTabsActivity.setupFriendFinderButton(mContext);
        }

        if (mContext instanceof UpdateBandLocationActivity) {
            UpdateBandLocationActivity updateBandLocationActivity = new UpdateBandLocationActivity();
            updateBandLocationActivity.setupFriendFinderButton(mContext);
        }

        if (mContext instanceof SmartUpdateActivity) {
            SmartUpdateActivity updateBandLocationActivity = new SmartUpdateActivity();
            updateBandLocationActivity.setupFriendFinderButton(mContext);
        }

        if (mContext instanceof FriendFinderTabsActivity) {
            WhoAreMyFriendsFragment updateBandLocationActivity = new WhoAreMyFriendsFragment();
            updateBandLocationActivity.setupFriendFinderButton(mContext);
        }

        EventBus.getDefault().post(true);
    }

}


