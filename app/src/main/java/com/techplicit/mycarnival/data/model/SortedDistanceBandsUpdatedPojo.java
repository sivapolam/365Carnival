package com.techplicit.mycarnival.data.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pnaganjane001 on 17/12/15.
 */
public class SortedDistanceBandsUpdatedPojo implements Comparable<SortedDistanceBandsUpdatedPojo>{

    private String name, image, address, latitude, longitude, updates, lastUpdated;
    private JSONObject locationObject;
    private boolean activeFlag;

    private double distance;


    public SortedDistanceBandsUpdatedPojo(JSONObject jsonObject) {
        if (jsonObject!=null){
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

    }

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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setUpdates(String updates) {
        this.updates = updates;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setLocationObject(JSONObject locationObject) {
        this.locationObject = locationObject;
    }

    @Override
    public int compareTo(SortedDistanceBandsUpdatedPojo another) {
        return (int)(getDistance()-another.getDistance());
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
