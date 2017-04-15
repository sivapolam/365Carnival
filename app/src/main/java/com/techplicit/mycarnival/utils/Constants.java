package com.techplicit.mycarnival.utils;

/**
 * Created by pnaganjane001 on 17/12/15.
 */
public interface Constants {
    String ERROR = "ERROR";
    String SUCCESS = "success";
    String NETWORK_FAIL = "NETWORK_FAIL";
    String STATUS = "Status";

    String POSITION = "POSITION";
    String BAND_NAME = "BAND_NAME";

    String SELECTED_CARNIVAL_NAME = "SELECTED_CARNIVAL_NAME";
    String SELECTED_BAND_NAME = "SELECTED_BAND_NAME";
    String SELECTED_BAND_ADDRESS = "SELECTED_BAND_ADDRESS";
    String SELECTED_BAND_LATITUDE = "SELECTED_BAND_LATITUDE";
    String SELECTED_BAND_LONGITUDE = "SELECTED_BAND_LONGITUDE";

    String UPDATE_LOCATION_FROM = "UPDATE_LOCATION_FROM";
    String FROM_BAND_UPDATE_BUTTON = "FROM_BAND_UPDATE_BUTTON";
    String FROM_BANDS_LIST = "FROM_BANDS_LIST";

    String PREFS_CARNIVAL = "PREFS_CARNIVAL";
    String GCM_REG_ID = "GCM_REG_ID";

    String LATITUDE_SMART_UPDATE = "LATITUDE_SMART_UPDATE";
    String LONGITUDE_SMART_UPDATE = "LONGITUDE_SMART_UPDATE";
    String BAND_ADDRESS_SMART_UPDATE = "BAND_ADDRESS_SMART_UPDATE";
    String CARNIVAL_NAME_SMART_UPDATE = "CARNIVAL_NAME_SMART_UPDATE";
    String CARNIVAL_POSITION_SMART_UPDATE = "CARNIVAL_POSITION_SMART_UPDATE";
    String BAND_NAME_SMART_UPDATE = "BAND_NAME_SMART_UPDATE";
    String MINUTES_INTERVAL_SMART_UPDATE = "MINUTES_INTERVAL_SMART_UPDATE";
    String UPDATES_INTERVAL_SMART_UPDATE = "UPDATES_INTERVAL_SMART_UPDATE";

    String IS_LOCATION_UPDATED = "IS_LOCATION_UPDATED";

    String IS_DISATNCE_NEEDS_TO_LOAD = "IS_DISATNCE_NEEDS_TO_LOAD";
    String IS_ALPH_SORT_NEEDS_TO_LOAD = "IS_ALPH_SORT_NEEDS_TO_LOAD";
    String IS_FAVS_NEEDS_TO_LOAD = "IS_FAVS_NEEDS_TO_LOAD";
    String IS_VIEW_NEEDS_TO_LOAD = "IS_VIEW_NEEDS_TO_LOAD";
    String IS_BANDS_VIEW_VISIBLE = "IS_BANDS_VIEW_VISIBLE";

    String IS_GPS_DIALOG_SHOWING = "IS_GPS_DIALOG_SHOWING";

    String KEY_HASH_VALUE = "KEY_HASH_VALUE";

    /*String BASE_URL = "http://52.89.123.186/carnival/service/";
    String REGISTRATION_URL = "http://52.33.80.237/carnival/user/fb-login";
    String INVITE_FRIEND_URL = "http://52.33.80.237/carnival/user/friend-request";
    String FRIENDs_LIST_URL = "http://52.33.80.237/carnival/user/friends";
    String REQUEST_ACKNOWLEDGE_URL = "http://52.33.80.237/carnival/user/ack-request";
    String GET_FRIENDS_LOCATION_URL = "http://52.33.80.237/carnival/user/friends/location";
    String UPDATE_USER_LOCATION_URL = "http://52.33.80.237/carnival/user/location";
    String UPDATE_USER_PRIVACY_URL = "http://52.33.80.237/carnival/user/privacy";
    String PRIVACY_URL = "http://www.iubenda.com/api/privacy-policy/7778591";*/

    String COMMON_URL = "http://34.209.73.148:8080/carnival/";
//    String COMMON_URL = "http://52.39.152.174/carnival/";
    String COMMON_USER_SERVICE_URL = "http://52.39.152.174/userservice/";
    String BASE_URL = COMMON_URL + "service/";
    String REGISTRATION_URL = COMMON_URL + "user/fb-login";
    String INVITE_FRIEND_URL = COMMON_URL + "user/friend-request";
    String FRIENDs_LIST_URL = COMMON_URL + "user/friends";
    String REQUEST_ACKNOWLEDGE_URL = COMMON_URL + "user/ack-request";
    String GET_FRIENDS_LOCATION_URL = COMMON_URL + "user/friends/location";
    String UPDATE_USER_LOCATION_URL = COMMON_URL + "user/location";
    String UPDATE_USER_PRIVACY_URL = COMMON_URL + "user/privacy";
    String PRIVACY_URL = "http://www.iubenda.com/api/privacy-policy/7778591";

    //Registration POST Keys
    String FIRST_NAME = "firstName";
    String LAST_NAME = "lastName";
    String EMAIL = "email";
    String IMAGE = "image";
    String ACCESS_TOKEN = "userAccessToken";

    String USER_ID = "id";
    String LOGIN_MEDIUM = "loginMedium";
    String REGISTRATION_STATUS = "status";
    String USER_STATUS = "status";
    String USER_PRIVACY = "privacy";
    String USER_LATITUDE = "lattitude";
    String USER_LONGITUDE = "longitude";
    String USER_LAST_UPDATED = "lastUpdated";

    String USER_EMAIL_INVITE = "userEmail";
    String FRIEND_EMAIL_INVITE = "friendEmail";
    String USER_TOKEN_INVITE = "userToken";

    String ACK_USER_EMAIL = "userEmail";
    String ACK_FRIEND_EMAIL = "friendEmail";
    String ACK_ACTION = "action";

    String FRIENDS_LIST_EMAIL = "email";

    String STATUS_INVITE = "status";
    String EXPLANATION_INVITE = "explanation";

    String ERR000 = "ERR000";
    String ERR001 = "ERR001";
    String ERR002 = "ERR002";
    String ERR003 = "ERR003";
    String ERR004 = "ERR004";
    String ERR005 = "ERR005";
    String ERR006 = "ERR006";
    String ERR007 = "ERR007";
    String ERR008 = "ERR008";
    String ERR009 = "ERR009";
    String ERR010 = "ERR010";
    String ERR011 = "ERR011";
    String ERR012 = "ERR012";

    String DATA = "data";
    String LOCATION = "location";

    String IS_SIGNED_IN = "IS_SIGNED_IN";
    String IS_IN_TRUCK_MODE = "IS_IN_TRUCK_MODE";
    String IS_SMART_UPDATED = "IS_SMART_UPDATED";
    String LAST_UPDATE = "LAST_UPDATE";

}
