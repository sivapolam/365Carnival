package com.techplicit.mycarnival.data.model;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pnaganjane001 on 17/12/15.
 */
public class FriendsLocationsPojo {

    private String name;
    private String image;
    private long lastUpdated;
    private double latitude;
    private double longitude;
    private String statusMsg;
    private String email;
    private String fName;
    private String lName;
    private JSONObject locationObject;
    private boolean activeFlag;
    private Bitmap friendImageBitmap;

    public Bitmap getFriendImageBitmap() {
        return friendImageBitmap;
    }

    public void setFriendImageBitmap(Bitmap friendImageBitmap) {
        this.friendImageBitmap = friendImageBitmap;
    }

    public FriendsLocationsPojo(JSONObject object) {

        if (object!=null){
            this.email = object.optString(JsonMap.EMAIL);
            this.fName = object.optString(JsonMap.FIRST_NAME);
            this.lName = object.optString(JsonMap.LAST_NAME);
            this.image = object.optString(JsonMap.FRIEND_IMAGE);

            try {

                if (object.has(JsonMap.LOCATION)){
                    this.locationObject = object.getJSONObject(JsonMap.LOCATION);

                    this.lastUpdated = locationObject.optLong(JsonMap.LAST_UPDATED);
                    this.latitude = locationObject.optDouble(JsonMap.LATITUDE);
                    this.longitude = locationObject.optDouble(JsonMap.LONGITUDE);

                }else{
                    this.latitude = 0.0;
                    this.longitude = 0.0;
                }

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

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public JSONObject getLocationObject() {
        return locationObject;
    }

    public String getEmail() {
        return email;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    private interface JsonMap{
        String ID = "id";
        String NAME = "name";
        String IMAGE = "image";
        String START_DATE = "startDate";
        String END_DATE = "endDate";
        String ACTIVE_FLAG = "activeFlag";
        String ADDRESS = "lastUpdated";


        String LATITUDE = "lattitude";
        String LONGITUDE = "longitude";

        String LAST_UPDATED = "lastUpdated";

        String LOCATION = "location";

        String EMAIL = "email";
        String STATUS = "status";
        String FIRST_NAME = "firstName";
        String LAST_NAME = "lastName";
        String FRIEND_IMAGE = "image";

        String FRIEND_REQUEST_STATUS = "requestStatus";
        String DATA = "data";
    }
}
