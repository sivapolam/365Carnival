package com.techplicit.mycarnival.data.model;

import org.json.JSONObject;

/**
 * Created by pnaganjane001 on 17/12/15.
 */
public class FriendsPojo {

    private String name, image, address, latitude, longitude, statusMsg, email, fName, lName, privacy;
    private JSONObject locationObject;
    private long lastUpdated;
    private boolean activeFlag;


    public String getPrivacy() {
        return privacy;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public boolean isActiveFlag() {
        return activeFlag;
    }

    public FriendsPojo(JSONObject object) {

        if (object!=null){
            this.email = object.optString(JsonMap.EMAIL);
            this.fName = object.optString(JsonMap.FIRST_NAME);
            this.lName = object.optString(JsonMap.LAST_NAME);
            this.image = object.optString(JsonMap.FRIEND_IMAGE);
            this.statusMsg = object.optString(JsonMap.FRIEND_REQUEST_STATUS);

            this.privacy = object.optString(JsonMap.PRIVACY);
            this.locationObject = object.optJSONObject(JsonMap.LOCATION);
            if (locationObject != null) {
                this.lastUpdated = locationObject.optLong(JsonMap.LAST_UPDATED);
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
        String ADDRESS = "address";
        String LATITUDE = "latitude";
        String LONGITUDE = "longitude";
        String UPDATES = "updated";

        String LAST_UPDATED = "lastUpdated";

        String LOCATION = "location";

        String EMAIL = "email";
        String STATUS = "status";
        String FIRST_NAME = "firstName";
        String LAST_NAME = "lastName";
        String FRIEND_IMAGE = "image";
        String FRIEND_REQUEST_STATUS = "requestStatus";
        String PRIVACY = "privacy";
        String DATA = "data";
    }
}
