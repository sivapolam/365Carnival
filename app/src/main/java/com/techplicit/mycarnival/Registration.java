package com.techplicit.mycarnival;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.techplicit.mycarnival.data.RegistrationAsyncTask;
import com.techplicit.mycarnival.ui.activities.BaseActivity;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pnaganjane001 on 25/12/15.
 */
public class Registration implements Constants {

    private static final String TAG = Registration.class.getName();

    private static String facebook_id, f_name, m_name, l_name, full_name, profile_image, accessToken, email_id;
    private static ProfileTracker profileTracker;
    private static ProgressDialog progressDialog;

    public static void register(final Activity activity, final NavigationDrawerFragment navigationDrawerFragment, CallbackManager callbackManager) {

        Log.e(TAG, "Sign In register--> called");
        //register callback object for facebook result
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            public JSONObject objectJson;

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG, "FB onSuccess called");
                Logger.addRecordToLog("SignIn onSuccess called");
                /*Profile profile = Profile.getCurrentProfile();
                if (profile != null) {
//                    facebook_id = profile.getId();
                    f_name = profile.getFirstName();
//                    m_name = profile.getMiddleName();
                    l_name = profile.getLastName();
//                    full_name = profile.getName();
                    profile_image = profile.getProfilePictureUri(400, 400).toString();
Log.e(TAG, "profile_imageeeeeeee--> "+profile_image);
//                    accessToken = AccessToken.getCurrentAccessToken().getToken();

                }*/


                if (loginResult.getRecentlyGrantedPermissions().contains("email")) {
                    Log.e(TAG, "email_iddddddd--> " + email_id);
                }


//                Log.e(TAG, " accessToken-->" + accessToken + "\nprofile_image--> " + profile_image);
                //Toast.makeText(FacebookLogin.this,"Wait...",Toast.LENGTH_SHORT).show();
                GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                if (object != null) {

                                    try {
                                        try {

                                            String name = object.optString("name");
                                            String[] namesArry = name.split(" ");

                                            StringBuilder builder = new StringBuilder();
                                            for (int i = 0; i < namesArry.length; i++) {

                                                if (i == namesArry.length - 1) {
                                                    l_name = namesArry[namesArry.length - 1];
                                                    break;
                                                }

                                                builder = builder.append(namesArry[i] + " ");
                                            }


                                            f_name = builder.toString();

                                            accessToken = AccessToken.getCurrentAccessToken().getToken();

                                            facebook_id = object.optString("id");

                                            profile_image = "https://graph.facebook.com/" + facebook_id + "/picture?height=400&width=400".trim();

                                            if (object.has("email")) {
                                                email_id = object.getString("email");

                                                SharedPreferences sharedPreferences = activity.getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString(Constants.EMAIL, email_id);
                                                editor.commit();

                                                Log.e(TAG, "FB email_id--> " + email_id);
                                                Log.e(TAG, "f_name--> " + f_name);
                                                Log.e(TAG, "l_name--> " + l_name);
                                                Log.e(TAG, "profile_image--> " + profile_image);
                                                Log.e(TAG, "accessToken--> " + accessToken);

                                                objectJson = new JSONObject();

                                                try {
                                                    objectJson.put("firstName", f_name);
                                                    objectJson.put("lastName", l_name);
                                                    objectJson.put("email", email_id);
                                                    objectJson.put("image", profile_image);
                                                    objectJson.put("userAccessToken", accessToken);

                                                    new RegistrationAsyncTask(activity, objectJson, navigationDrawerFragment).execute();
                                                    clear();

                                            /*if(sharedPreferences.getString(GCM_REG_ID, null)!=null){
                                                objectJson.put("gcmRegId", sharedPreferences.getString(GCM_REG_ID, null));
                                            }*/

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    Logger.addRecordToLog("FB registration e111 --> " + e.toString());
                                                }
                                            } else {

                                                dismissProgress(activity);
                                                SharedPreferences sharedPreferences = activity.getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString(Constants.EMAIL, "");
                                                editor.commit();
                                                Utility.displayNetworkFailDialog(activity, STATUS, "Failed", "Please check your Facebook Email id. It is invalid or hasn't been confirmed.");
                                            }


                                            Log.e(TAG, "FB objectJson--> " + objectJson);

                                            if (progressDialog != null) {
                                                progressDialog.dismiss();
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Logger.addRecordToLog("FB registration e222 --> " + e.toString());
                                        }

                                    } catch (Exception e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                        Logger.addRecordToLog("FB registration e --> " + e.toString());
                                    }

                                    Logger.addRecordToLog("FB email_id--> " + email_id);
                                    Logger.addRecordToLog("FB f_name--> " + f_name);
                                    Logger.addRecordToLog("FB l_name--> " + l_name);
                                    Logger.addRecordToLog("FB profile_image--> " + profile_image);
                                    Logger.addRecordToLog("FB accessToken--> " + accessToken + "\n\n");
                                    Logger.addRecordToLog("FB objectJson--> " + objectJson);


                                }
                            }

                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);

                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(activity, "Login Cancelled", Toast.LENGTH_SHORT).show();
//                progress.dismiss();
                Log.e(TAG, "onCancel called-->");
                Logger.addRecordToLog("SignIn onCancel called");
                try {
                    SharedPreferences sharedPreferences = activity.getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(IS_SIGNED_IN, false);
                    editor.commit();
                } catch (Exception e) {
                    Log.e(TAG, "onCancel-->" + e.toString());
                    Logger.addRecordToLog("SignIn onCancel error --> " + e.toString());
                }

                dismissProgress(activity);

/*
                if (activity instanceof MainActivity){
                    if (MainActivity.progressDialog!=null){
                        MainActivity.progressDialog.dismiss();
                    }
                }


                if (activity instanceof CarnivalsListActivity){
                    if (CarnivalsListActivity.progressDialog!=null){
                        CarnivalsListActivity.progressDialog.dismiss();
                    }
                }

                if (activity instanceof BandTabsActivity) {

                    if (BandTabsActivity.progressDialog!=null){
                        BandTabsActivity.progressDialog.dismiss();
                    }

                    BandTabsActivity bandTabsActivity = new BandTabsActivity();
                    bandTabsActivity.setupFriendFinderButton(activity);
                }

                if (activity instanceof UpdateBandLocationActivity) {
                    UpdateBandLocationActivity updateBandLocationActivity = new UpdateBandLocationActivity();
                    updateBandLocationActivity.setupFriendFinderButton(activity);

                    if (UpdateBandLocationActivity.progressDialog!=null){
                        UpdateBandLocationActivity.progressDialog.dismiss();
                    }
                }

                if (activity instanceof AboutUsActivity){
                    if (AboutUsActivity.progressDialog!=null){
                        AboutUsActivity.progressDialog.dismiss();
                    }
                }

                if (activity instanceof FriendFinderTabsActivity){
                    if (FriendFinderTabsActivity.progressDialog!=null){
                        FriendFinderTabsActivity.progressDialog.dismiss();
                    }
                }

//                if (activity instanceof SmartUpdateActivity){
                try{
                    if (SmartUpdateActivity.progressDialog!=null){
                        SmartUpdateActivity.progressDialog.dismiss();
                    }

                    SmartUpdateActivity smartUpdateActivity = new SmartUpdateActivity();
                    smartUpdateActivity.setupFriendFinderButton(activity);
                }catch (Exception e){
                    Log.e(TAG, "smartUpdateActivity error--"+e.toString());
                }*/


//                }

                profileTracker = null;
            }

            @Override
            public void onError(FacebookException error) {
                Logger.addRecordToLog("SignIn onError error --> " + error.toString());
                try {
                    SharedPreferences sharedPreferences = activity.getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(IS_SIGNED_IN, false);
                    editor.commit();
                } catch (Exception e) {
                    Log.e(TAG, "onError-->" + e.toString());
                    Logger.addRecordToLog("SignIn onError e --> " + e.toString());
                }

                Log.e(TAG, "onError error-->" + error.toString());

                Toast.makeText(activity, "Login Failed", Toast.LENGTH_SHORT).show();

                /*if (activity instanceof BaseActivity){
                    if (BaseActivity.progressDialog!=null){
                        BaseActivity.progressDialog.dismiss();
                    }
                }*/

                /*if (activity instanceof MainActivity){
                    if (MainActivity.progressDialog!=null){
                        MainActivity.progressDialog.dismiss();
                    }
                }


                if (activity instanceof CarnivalsListActivity){
                    if (CarnivalsListActivity.progressDialog!=null){
                        CarnivalsListActivity.progressDialog.dismiss();
                    }
                }

                if (activity instanceof BandTabsActivity) {

                    if (BandTabsActivity.progressDialog!=null){
                        BandTabsActivity.progressDialog.dismiss();
                    }

                    BandTabsActivity bandTabsActivity = new BandTabsActivity();
                    bandTabsActivity.setupFriendFinderButton(activity);
                }

                if (activity instanceof UpdateBandLocationActivity) {
                    UpdateBandLocationActivity updateBandLocationActivity = new UpdateBandLocationActivity();
                    updateBandLocationActivity.setupFriendFinderButton(activity);

                    if (UpdateBandLocationActivity.progressDialog!=null){
                        UpdateBandLocationActivity.progressDialog.dismiss();
                    }
                }


                if (activity instanceof AboutUsActivity){
                    if (AboutUsActivity.progressDialog!=null){
                        AboutUsActivity.progressDialog.dismiss();
                    }
                }

                if (activity instanceof FriendFinderTabsActivity){
                    if (FriendFinderTabsActivity.progressDialog!=null){
                        FriendFinderTabsActivity.progressDialog.dismiss();
                    }
                }*/

                dismissProgress(activity);
                profileTracker = null;

            }
        });
    }

    private static void login(final String email_id, final Activity activity, final NavigationDrawerFragment navigationDrawerFragment) {
        //Initialize the ProfileTracker and override its
        // onCurrentProfileChanged(...) method.
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                //Whenever the user profile is changed,
                //this method will be called.

                if (newProfile == null) {

                } else {

                    facebook_id = newProfile.getId();
                    f_name = newProfile.getFirstName();
                    l_name = newProfile.getLastName();
                    full_name = newProfile.getName();
                    profile_image = newProfile.getProfilePictureUri(400, 400).toString();

                    accessToken = AccessToken.getCurrentAccessToken().getToken();

                    Log.e(TAG, "facebook_id--> " + facebook_id);
                    Log.e(TAG, "f_name--> " + f_name);
                    Log.e(TAG, "l_name--> " + l_name);
                    Log.e(TAG, "full_name--> " + full_name);
                    Log.e(TAG, "profile_image--> " + profile_image);
                    Log.e(TAG, "accessToken--> " + accessToken);

                    JSONObject objectJson = new JSONObject();

                    try {
                        objectJson.put("firstName", f_name);
                        objectJson.put("lastName", l_name);
                        objectJson.put("email", email_id);
                        objectJson.put("image", profile_image);
                        objectJson.put("userAccessToken", accessToken);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.e(TAG, "FB objectJson--> " + objectJson);

                    new RegistrationAsyncTask(activity, objectJson, navigationDrawerFragment).execute();

                    profileTracker = null;
                }
            }
        };

        profileTracker.startTracking();
    }

    public static void clear() {
        facebook_id = null;
        f_name = null;
        m_name = null;
        l_name = null;
        full_name = null;
        profile_image = null;
        accessToken = null;
        email_id = null;
    }

    public static void dismissProgress(Activity activity) {

        if (activity instanceof BaseActivity) {
            if (BaseActivity.progressDialog != null) {
                BaseActivity.progressDialog.dismiss();
                BaseActivity.mNavigationDrawerFragment.changeNavigationList(activity);
            }
        }

       /* if (activity instanceof MainActivity){
            if (MainActivity.progressDialog!=null){
                MainActivity.progressDialog.dismiss();
            }
        }


        if (activity instanceof CarnivalsListActivity){
            if (CarnivalsListActivity.progressDialog!=null){
                CarnivalsListActivity.progressDialog.dismiss();
            }
        }

        if (activity instanceof BandTabsActivity) {

            if (BandTabsActivity.progressDialog!=null){
                BandTabsActivity.progressDialog.dismiss();
            }

            BandTabsActivity bandTabsActivity = new BandTabsActivity();
            bandTabsActivity.setupFriendFinderButton(activity);
        }

        if (activity instanceof UpdateBandLocationActivity) {
            UpdateBandLocationActivity updateBandLocationActivity = new UpdateBandLocationActivity();
            updateBandLocationActivity.setupFriendFinderButton(activity);

            if (UpdateBandLocationActivity.progressDialog!=null){
                UpdateBandLocationActivity.progressDialog.dismiss();
            }
        }

        if (activity instanceof AboutUsActivity){
            if (AboutUsActivity.progressDialog!=null){
                AboutUsActivity.progressDialog.dismiss();
            }
        }

        if (activity instanceof FriendFinderTabsActivity){
            if (FriendFinderTabsActivity.progressDialog!=null){
                FriendFinderTabsActivity.progressDialog.dismiss();
            }
        }

                if (activity instanceof SmartUpdateActivity) {
                    try {
                        if (SmartUpdateActivity.progressDialog != null) {
                            SmartUpdateActivity.progressDialog.dismiss();
                        }

                        SmartUpdateActivity smartUpdateActivity = new SmartUpdateActivity();
                        smartUpdateActivity.setupFriendFinderButton(activity);
                    } catch (Exception e) {
                        Log.e(TAG, "smartUpdateActivity error--" + e.toString());
                    }
                }*/

    }
}
