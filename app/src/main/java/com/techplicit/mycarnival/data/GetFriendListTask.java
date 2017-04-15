package com.techplicit.mycarnival.data;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import com.techplicit.mycarnival.data.model.FriendsPojo;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by pnaganjane001 on 28/12/15.
 */
class GetFriendListAsync extends AsyncTask<String, String, String> implements Constants {

    private static final String TAG = GetFriendListAsync.class.getName();
    private final JSONObject object;
    ServiceHandler jsonParser = new ServiceHandler();

    private Activity mContext;
    private String responseStatus, inviteEmail;
    private ArrayList<FriendsPojo> friendsPojoArrayList;
    ProgressDialog pd;
    private String inviteStatus;

    public GetFriendListAsync(Activity context, ProgressBar carnivalsProgress, JSONObject object) {
        mContext = context;
//            this.carnivalsProgress = carnivalsProgress;
        this.object = object;
        Log.e(TAG, "GetFriendsListAsync object--> " + object);
    }

    @Override
    protected void onPreExecute() {
//            this.carnivalsProgress.setVisibility(View.VISIBLE);
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Friends List");
        pd.show();
    }

    @Override
    protected String doInBackground(String... args) {

        friendsPojoArrayList = new ArrayList<FriendsPojo>();

        try {
            responseStatus = jsonParser.makePostRequest(
                    FRIENDs_LIST_URL, object);
            return responseStatus;

        } catch (Exception e) {
            e.printStackTrace();
            responseStatus = ERROR;
        }

        return null;
    }

    protected void onPostExecute(String responseStatus) {

        if (pd != null) {
            pd.dismiss();
        }

        try {
            if (responseStatus!=null && !responseStatus.equals(ERROR)){

                JSONObject jsonObject = new JSONObject(responseStatus);
                inviteStatus = jsonObject.optString(STATUS_INVITE);
                if(inviteStatus!=null){

                    if (!inviteStatus.equalsIgnoreCase("success")){
                        JSONArray jsonArray = jsonObject.optJSONArray(EXPLANATION_INVITE);
                        for (int i = 0; i<jsonArray.length();i++){
                            JSONObject object = jsonArray.getJSONObject(i);

                            if (object.has(ERR003)){
                                inviteStatus = object.optString(ERR003);
                            }else if (object.has(ERR004)){
                                inviteStatus = object.optString(ERR004);
                            }else if (object.has(ERR005)){
                                inviteStatus = object.optString(ERR005);
                            }else if (object.has(ERR006)){
                                inviteStatus = object.optString(ERR006);
                            }else if (object.has(ERR007)){
                                inviteStatus = object.optString(ERR007);
                            }

                            Utility.displayNetworkFailDialog(mContext, STATUS, "Failed", inviteStatus);

                        }
                    }else{
                        JSONArray jsonArray = jsonObject.optJSONArray("data");
                        if (jsonArray!=null && jsonArray.length()>0){
                            for (int i = 0; i<jsonArray.length();i++){
                                FriendsPojo pojo = new FriendsPojo(jsonArray.optJSONObject(i));
                                friendsPojoArrayList.add(pojo);

                                CarnivalsSingleton.getInstance().setFreindsPojoArrayList(friendsPojoArrayList);

                            }
                        }else{
//                            textEmpty.setVisibility(View.VISIBLE);
//                                Utility.displayNetworkFailDialog(mContext, "empty_friends", "", "");
                        }

                    }

                }

            }else{
                Utility.displayNetworkFailDialog(mContext, ERROR, "", "");
//                    return responseStatus;
            }

            if(CarnivalsSingleton.getInstance().getFriendsPojoArrayList()!=null && CarnivalsSingleton.getInstance().getFriendsPojoArrayList().size() > 0){
//                textEmpty.setVisibility(View.GONE);
            }else{
//                textEmpty.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}