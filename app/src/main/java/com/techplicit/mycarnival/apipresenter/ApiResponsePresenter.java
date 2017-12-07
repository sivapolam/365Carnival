package com.techplicit.mycarnival.apipresenter;

import android.util.Log;

import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.techplicit.mycarnival.MyApplication;
import com.techplicit.mycarnival.interfaces.IRequestInterface;
import com.techplicit.mycarnival.interfaces.IResponseInterface;
import com.techplicit.mycarnival.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by FuGenX-10 on 18-11-2016.
 */

public class ApiResponsePresenter implements IRequestInterface {

    private static final String LOG_TAG = ApiResponsePresenter.class.getSimpleName();
    public IResponseInterface iResponseInterface;

    public ApiResponsePresenter(IResponseInterface iResponseInterface) {
        this.iResponseInterface = iResponseInterface;
    }

    @Override
    public void callApi(int methodType, String Url, JSONObject paramObj, final String reqName, int requestType) {
        //on connection if need to start dialog
        iResponseInterface.onApiConnected(reqName);
//        try {
//            Url = URLEncoder.encode(Url, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        Log.v("Url", "Url " + Url);
        Log.v("Url", "Url paramObj " + paramObj);
        if (requestType == REQUEST_TYPE_JSON_OBJECT) {
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(methodType, Url, paramObj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.v("RESP" + reqName, response.toString());
                            if (response != null) {
                                try {
                                    iResponseInterface.onResponseSuccess(response.toString(), reqName);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    iResponseInterface.onResponseFailure(reqName);
                                }
                            }
                        }

                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(LOG_TAG, "obj onErrorResponse " + error.toString());
                    if (error instanceof NoConnectionError) {
                        iResponseInterface.onResponseFailure(reqName);
                    } else if (error instanceof TimeoutError) {
                        iResponseInterface.onResponseFailure(reqName);
                    } else if (error instanceof ParseError) {
                        iResponseInterface.onResponseFailure(Constants.PARSE_ERROR);
                    } else {
                        iResponseInterface.onResponseFailure(error.getLocalizedMessage());
                    }
                }
            });
            Log.e("Url", "Object jsonObjReq " + jsonObjReq);

            MyApplication.getInstance().addToRequestQueue(jsonObjReq, MyApplication.TAG_JSON_OBJ_REQ);
        } else if (requestType == REQUEST_TYPE_JSON_GET_ARRAY) {
            JsonArrayRequest jsonObjReq = new JsonArrayRequest(methodType, Url,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.v("RESP" + reqName, response.toString());

                            iResponseInterface.onResponseSuccess(response.toString(), reqName);
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(LOG_TAG, "error onErrorResponse " + error.toString());
                    if (error instanceof NoConnectionError) {
                        iResponseInterface.onResponseFailure(reqName);
                    } else if (error instanceof TimeoutError) {
                        iResponseInterface.onResponseFailure(reqName);
                    } else if (error instanceof ParseError) {
                        iResponseInterface.onResponseFailure(Constants.PARSE_ERROR);
                    } else {
                        iResponseInterface.onResponseFailure(error.getLocalizedMessage());
                    }
                }
            });
            Log.e("Url", "array jsonObjReq " + jsonObjReq);

            MyApplication.getInstance().addToRequestQueue(jsonObjReq, MyApplication.TAG_JSON_OBJ_REQ);
        }
    }

}
