package com.techplicit.mycarnival;

import android.app.ProgressDialog;

import com.techplicit.mycarnival.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by pnaganjane001 on 23/12/15.
 */
public class FacebookLoginAndUserInfo implements Constants{

    private static final String TAG = FacebookLoginAndUserInfo.class.getName();
    private ProgressDialog pd;

    public static final String USER_MAP = "userHashmap";
    public static final String FRIEND_LIST = "friendList";


    HashMap<String, String> userHashmap;
    private ArrayList<HashMap<String, String>> friendList;

}
