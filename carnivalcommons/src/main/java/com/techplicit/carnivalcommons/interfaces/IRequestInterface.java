package com.techplicit.carnivalcommons.interfaces;

import org.json.JSONObject;

/**
 * Created by FuGenX-10 on 29-11-2016.
 */

public interface IRequestInterface {

    int REQUEST_TYPE_JSON_OBJECT=1;
    int REQUEST_TYPE_JSON_ARRAY=2;
    int REQUEST_TYPE_JSON_GET_ARRAY=3;
    int REQUEST_TYPE_ASYNC_JSON_ARRAY=4;

    void callApi(int methodType, String Url, JSONObject paramObj, final String reqName, int requestType);
}
