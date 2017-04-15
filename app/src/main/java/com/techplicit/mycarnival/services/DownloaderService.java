package com.techplicit.mycarnival.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.techplicit.mycarnival.utils.Constants;

/**
 * Created by pnaganjane001 on 20/01/16.
 */
public class DownloaderService extends IntentService implements Constants{
    private static final String TAG = DownloaderService.class.getName();

    public DownloaderService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG,"onHandleIntent Called");


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy Called");
    }
}
