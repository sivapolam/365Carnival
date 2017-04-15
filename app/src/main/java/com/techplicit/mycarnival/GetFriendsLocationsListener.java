package com.techplicit.mycarnival;

import android.app.Activity;

import org.json.JSONObject;

/**
 * Created by pnaganjane001 on 29/12/15.
 */
public interface GetFriendsLocationsListener {

    void onFriendsLocationListener(Activity activity, JSONObject object);
}
