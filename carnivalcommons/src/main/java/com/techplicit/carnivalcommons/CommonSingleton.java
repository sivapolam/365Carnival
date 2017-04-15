package com.techplicit.carnivalcommons;

import android.util.Log;

import com.techplicit.carnivalcommons.data.BandLocationPojo;
import com.techplicit.carnivalcommons.data.CarnivalsPojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by pnaganjane001 on 17/12/15.
 */
public class CommonSingleton {

    private static final String TAG = CommonSingleton.class.getName();
    private static CommonSingleton instance = null;
    private JSONArray mCarnivalsJsonResponse, mBandsJsonResponse,mBandsUpdatedJsonResponse;
    private ArrayList<CarnivalsPojo> mCarnivalsPojoArrayList;
    private ArrayList<BandLocationPojo> mBandsPojoArrayList;
    private String privacyData;
    boolean privacyStatus;

    private CommonSingleton() {
    }

    public static CommonSingleton getInstance() {
        if (instance == null) {
            synchronized (CommonSingleton.class) {
                if (instance == null) {
                    instance = new CommonSingleton();
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

    /*public void setBandsUpdatedJsonResponse(JSONArray mJsonResponse) {
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
*/
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
    }

}
