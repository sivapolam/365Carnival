package com.techplicit.mycarnival.data;

import android.util.Log;

import com.techplicit.mycarnival.data.model.BandLocationPojo;
import com.techplicit.mycarnival.data.model.CarnivalsPojo;
import com.techplicit.mycarnival.data.model.FriendsLocationsPojo;
import com.techplicit.mycarnival.data.model.FriendsPojo;
import com.techplicit.mycarnival.data.model.SortedDistanceBandsPojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by pnaganjane001 on 17/12/15.
 */
public class CarnivalsSingleton {

    private static final String TAG = CarnivalsSingleton.class.getName();
    private static CarnivalsSingleton instance = null;
    private JSONArray mCarnivalsJsonResponse, mBandsJsonResponse,mBandsUpdatedJsonResponse;
    private ArrayList<CarnivalsPojo> mCarnivalsPojoArrayList;
    private ArrayList<BandLocationPojo> mBandsPojoArrayList;
    private ArrayList<SortedDistanceBandsPojo> mDistanceSortedBandsPojoArrayList;
    private ArrayList<FriendsPojo> friendsPojo;
    private ArrayList<FriendsLocationsPojo> friendsLocationsPojoArrayList;
    private String privacyData;
    boolean privacyStatus;

    private CarnivalsSingleton() {
    }

    public static CarnivalsSingleton getInstance() {
        if (instance == null) {
            synchronized (CarnivalsSingleton.class) {
                if (instance == null) {
                    instance = new CarnivalsSingleton();
                }
            }
        }
        return instance;
    }

    public boolean getPrivacyStatus() {
        return privacyStatus;
    }

    public void setPrivacyStatus(boolean privacyStatus) {
        this.privacyStatus = privacyStatus;
    }

    public String getPrivacyData() {
        return privacyData;
    }

    public void setPrivacyData(String privacyData) {
        this.privacyData = privacyData;
    }

    public JSONArray getCarnivalsJsonResponse() {
        return mCarnivalsJsonResponse;
    }

    public ArrayList<CarnivalsPojo> getCarnivalsPojoArrayList() {
        return mCarnivalsPojoArrayList;
    }

    public void setCarnivalsJsonResponse(JSONArray mJsonResponse) {
        this.mCarnivalsJsonResponse = mJsonResponse;
        mCarnivalsPojoArrayList = new ArrayList<CarnivalsPojo>();
        for (int i=0; i<mCarnivalsJsonResponse.length();i++ ){
            try {
                JSONObject jsonObject = (JSONObject) mCarnivalsJsonResponse.get(i);
                CarnivalsPojo pojo = new CarnivalsPojo(jsonObject);
                mCarnivalsPojoArrayList.add(pojo);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "JSON Exception");
            }
        }

    }

    public JSONArray getBandsJsonResponse() {
        return mCarnivalsJsonResponse;
    }

    public ArrayList<BandLocationPojo> getBandsPojoArrayList() {
        return mBandsPojoArrayList;
    }

    public ArrayList<SortedDistanceBandsPojo> getSortedDistanceBandsPojoArrayList() {
        return mDistanceSortedBandsPojoArrayList;
    }

    public void setDistanceSortedBandsPojoArrayList(ArrayList<SortedDistanceBandsPojo> mDistanceSortedBandsPojoArrayList) {
        this.mDistanceSortedBandsPojoArrayList = mDistanceSortedBandsPojoArrayList;
    }

    public ArrayList<FriendsPojo> getFriendsPojoArrayList() {
        return friendsPojo;
    }

    public void setFreindsPojoArrayList(ArrayList<FriendsPojo> freindsPojoArrayList) {
        this.friendsPojo = freindsPojoArrayList;
    }

    public ArrayList<FriendsLocationsPojo> getFriendsLocationsPojoArrayList() {
        return friendsLocationsPojoArrayList;
    }

    public void setFriendsLocationsPojoArrayList(ArrayList<FriendsLocationsPojo> friendsLocationsPojoArrayList) {
        this.friendsLocationsPojoArrayList = friendsLocationsPojoArrayList;
    }

    public void setBandsJsonResponse(JSONArray mJsonResponse) {
        this.mBandsJsonResponse = mJsonResponse;
        mBandsPojoArrayList = new ArrayList<BandLocationPojo>();
        for (int i=0; i<mBandsJsonResponse.length();i++ ){
            try {
                JSONObject jsonObject = (JSONObject) mBandsJsonResponse.get(i);
                BandLocationPojo pojo = new BandLocationPojo(jsonObject);
                mBandsPojoArrayList.add(pojo);


            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "JSON Exception");
            }
        }

    }

    public void setBandsUpdatedJsonResponse(JSONArray mJsonResponse) {
        this.mBandsUpdatedJsonResponse = mJsonResponse;
        mBandsPojoArrayList = new ArrayList<BandLocationPojo>();
        for (int i=0; i<mBandsJsonResponse.length();i++ ){
            try {
                JSONObject jsonObject = (JSONObject) mBandsJsonResponse.get(i);
                BandLocationPojo pojo = new BandLocationPojo(jsonObject);
                mBandsPojoArrayList.add(pojo);


            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "JSON Exception");
            }
        }

    }

    public void clear (){
        mBandsJsonResponse = null;
        mBandsPojoArrayList = null;
        mCarnivalsJsonResponse = null;
        mCarnivalsPojoArrayList = null;
    }

    public void clearCarnivalsData (){
        mCarnivalsJsonResponse = null;
        mCarnivalsPojoArrayList = null;
    }

    public void clearBandsData (){
        mBandsJsonResponse = null;
        mBandsPojoArrayList = null;
        mDistanceSortedBandsPojoArrayList = null;
    }

    public void clearFriendsData (){
        friendsPojo = null;
    }
}
