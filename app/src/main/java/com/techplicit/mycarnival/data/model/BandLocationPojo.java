package com.techplicit.mycarnival.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pnaganjane001 on 17/12/15.
 */
public class BandLocationPojo implements Parcelable{

    private String name, image, address, latitude, longitude, updates, lastUpdated;
    private JSONObject locationObject;
    private boolean activeFlag;


    public BandLocationPojo(JSONObject jsonObject) {
        this.name = jsonObject.optString(JsonMap.NAME);
        this.image = jsonObject.optString(JsonMap.IMAGE);
        this.activeFlag = jsonObject.optBoolean(JsonMap.ACTIVE_FLAG);

        try {
            this.locationObject = jsonObject.getJSONObject(JsonMap.LOCATION);


            this.address = locationObject.optString(JsonMap.ADDRESS);
            this.latitude = locationObject.optString(JsonMap.LATITUDE);
            this.longitude = locationObject.optString(JsonMap.LONGITUDE);
            this.updates = locationObject.optString(JsonMap.UPDATES);
            this.lastUpdated = locationObject.optString(JsonMap.LAST_UPDATED);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected BandLocationPojo(Parcel in) {
        name = in.readString();
        image = in.readString();
        address = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        updates = in.readString();
        lastUpdated = in.readString();
        activeFlag = in.readByte() != 0;
    }

    public static final Creator<BandLocationPojo> CREATOR = new Creator<BandLocationPojo>() {
        @Override
        public BandLocationPojo createFromParcel(Parcel in) {
            return new BandLocationPojo(in);
        }

        @Override
        public BandLocationPojo[] newArray(int size) {
            return new BandLocationPojo[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

    public String getAddress() {
        return address;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getUpdates() {
        return updates;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public JSONObject getLocationObject() {
        return locationObject;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(image);
        dest.writeString(address);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(updates);
        dest.writeString(lastUpdated);
        dest.writeByte((byte) (activeFlag ? 1 : 0));
    }


    private interface JsonMap{
        String ID = "id";
        String NAME = "name";
        String IMAGE = "image";
        String START_DATE = "startDate";
        String END_DATE = "endDate";
        String ACTIVE_FLAG = "activeFlag";
        String ADDRESS = "address";
        String LATITUDE = "latitude";
        String LONGITUDE = "longitude";
        String UPDATES = "updated";
        String LAST_UPDATED = "lastUpdated";
        String LOCATION = "location";
    }
}
