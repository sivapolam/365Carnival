package com.techplicit.mycarnival;

import android.app.Activity;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by pnaganjane001 on 28/12/15.
 */
public interface Listener {

    void onFriendAcceptedListener(Activity context, String userEmail, ListView listView, TextView textView);
}
