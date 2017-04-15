package com.techplicit.mycarnival.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * Created by FuGenX-50 on 30-01-2017.
 */

public class FavouritesPojo implements Parcelable {

    HashMap<String,BandsPojo> stringBandsPojoHashMap = new HashMap<>();
    HashMap<String,BandSectionPojo> stringBandSectionPojoHashMap= new HashMap<>();
    HashMap<String,SortedDistanceBandsPojo> stringBandLocationPojoHashMap= new HashMap<>();
    HashMap<String,FeteDetailModel> stringFeteDetailModelHashMap= new HashMap<>();

    public FavouritesPojo() {
    }

    public FavouritesPojo(Parcel in) {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FavouritesPojo> CREATOR = new Creator<FavouritesPojo>() {
        @Override
        public FavouritesPojo createFromParcel(Parcel in) {
            return new FavouritesPojo(in);
        }

        @Override
        public FavouritesPojo[] newArray(int size) {
            return new FavouritesPojo[size];
        }
    };

    public HashMap<String, BandsPojo> getStringBandsPojoHashMap() {
        return stringBandsPojoHashMap;
    }

    public void setStringBandsPojoHashMap(HashMap<String, BandsPojo> stringBandsPojoHashMap) {
        this.stringBandsPojoHashMap = stringBandsPojoHashMap;
    }

    public HashMap<String, BandSectionPojo> getStringBandSectionPojoHashMap() {
        return stringBandSectionPojoHashMap;
    }

    public void setStringBandSectionPojoHashMap(HashMap<String, BandSectionPojo> stringBandSectionPojoHashMap) {
        this.stringBandSectionPojoHashMap = stringBandSectionPojoHashMap;
    }

    public HashMap<String, SortedDistanceBandsPojo> getStringBandLocationPojoHashMap() {
        return stringBandLocationPojoHashMap;
    }

    public void setStringBandLocationPojoHashMap(HashMap<String, SortedDistanceBandsPojo> stringBandLocationPojoHashMap) {
        this.stringBandLocationPojoHashMap = stringBandLocationPojoHashMap;
    }

    public HashMap<String, FeteDetailModel> getStringFeteDetailModelHashMap() {
        return stringFeteDetailModelHashMap;
    }

    public void setStringFeteDetailModelHashMap(HashMap<String, FeteDetailModel> stringFeteDetailModelHashMap) {
        this.stringFeteDetailModelHashMap = stringFeteDetailModelHashMap;
    }
}
