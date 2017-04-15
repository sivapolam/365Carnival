package com.techplicit.carnivalcommons.interfaces;

/**
 * Created by FuGenX-10 on 22-11-2016.
 */

public interface IResponseInterface {

    void onResponseSuccess(String resp, String req);

    void onResponseFailure(String req);

    void onApiConnected(String req);

}
