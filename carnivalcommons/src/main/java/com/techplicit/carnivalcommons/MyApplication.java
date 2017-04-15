package com.techplicit.carnivalcommons;

import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.File;

/**
 * Created by pnaganjane001 on 27/12/15.
 */
public class MyApplication extends MultiDexApplication {

    private static MyApplication instance;
    private RequestQueue mRequestQueue;
    public static final String TAG_JSON_OBJ_REQ = "json_obj_req";
    public static final String TAG = MyApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        if (instance == null) {
            instance = MyApplication.this;
        }

    }

    public static MyApplication getInstance(){
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }


    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if(appDir.exists()){
            String[] children = appDir.list();
            for(String s : children){
                if(!s.equals("lib")){
                    if (!s.equals("shared_prefs")){
                        deleteDir(new File(appDir, s));
                        Log.i("TAG", "File /data/data/APP_PACKAGE/" + s + " DELETED");
                    }

                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

}
