package com.techplicit.mycarnival.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.techplicit.mycarnival.NavigationDrawerFragment;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pnaganjane001 on 28/12/15.
 */
class FriendAckTask extends AsyncTask<String, String, String> implements Constants {
    private SharedPreferences sharedPreferences;
    private boolean isResponseSucceed;
    ServiceHandler jsonParser = new ServiceHandler();

    private Activity mContext;
    private String responseStatus;
    private String jsonData;
    private boolean isSignedIn;
    private JSONObject object;
    NavigationDrawerFragment navigationDrawerFragment;
    private String inviteStatus;

    public FriendAckTask(Activity context, JSONObject jsonObject) {
        mContext = context;
        object = jsonObject;
        sharedPreferences = mContext.getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);

    }

    @Override
    protected void onPreExecute() {
//            this.carnivalsProgress.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... args) {

        try {
            responseStatus = jsonParser.makePostRequest(
                    REQUEST_ACKNOWLEDGE_URL, object);

            return responseStatus;
        } catch (Exception e) {
            e.printStackTrace();
            responseStatus = ERROR;
            return responseStatus;
        }

//            return null;
    }

    protected void onPostExecute(String responseStatus) {

        if (responseStatus!=null){

            if (responseStatus!=null && !responseStatus.equals(ERROR)){

                try{
                    JSONObject jsonObject = new JSONObject(responseStatus);
                    inviteStatus = jsonObject.optString(STATUS_INVITE);
                    if(inviteStatus!=null){
                        if (!inviteStatus.equalsIgnoreCase("success")){
                            JSONArray jsonArray = jsonObject.optJSONArray(EXPLANATION_INVITE);
                            for (int i = 0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);

                                if (object.has(ERR003)){
                                    inviteStatus = object.optString(ERR003);
                                }

                                if (object.has(ERR004)){
                                    inviteStatus = object.optString(ERR004);
                                }

                                if (object.has(ERR005)){
                                    inviteStatus = object.optString(ERR005);
                                }

                                if (object.has(ERR006)){
                                    inviteStatus = object.optString(ERR006);
                                }

                                if (object.has(ERR007)){
                                    inviteStatus = object.optString(ERR007);
                                }

                                if (object.has(ERR008)){
                                    inviteStatus = object.optString(ERR008);
                                }

                                if (object.has(ERR009)){
                                    inviteStatus = object.optString(ERR009);
                                }

                                if (object.has(ERR010)){
                                    inviteStatus = object.optString(ERR010);
                                }

                                if (object.has(ERR011)){
                                    inviteStatus = object.optString(ERR011);
                                }

                                Utility.displayNetworkFailDialog(mContext, STATUS, "Error", inviteStatus);

                            }
                        }else{
                            Utility.displayNetworkFailDialog(mContext, STATUS, "Success", "Successfully Invited !");

                            JSONObject jsonObject1 = new JSONObject();
                            if (sharedPreferences.getString(EMAIL, null)!=null && sharedPreferences.getString(ACCESS_TOKEN, null)!=null){
                                try {
                                    jsonObject1.put(FRIENDS_LIST_EMAIL, sharedPreferences.getString(EMAIL, null));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }


            }else{
                Utility.displayNetworkFailDialog(mContext, ERROR, "", "");
            }

        }
    }

}