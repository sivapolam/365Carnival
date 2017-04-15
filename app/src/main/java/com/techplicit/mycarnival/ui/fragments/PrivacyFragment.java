package com.techplicit.mycarnival.ui.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.data.CarnivalsSingleton;
import com.techplicit.mycarnival.data.ServiceHandler;
import com.techplicit.mycarnival.utils.Constants;

import org.json.JSONObject;

/**
 * Created by pnaganjane001 on 23/01/16.
 */
public class PrivacyFragment extends Fragment implements Constants{

    private static final String TAG = PrivacyFragment.class.getName();
    private WebView webview;
    String mimeType = "text/html";
    String encoding = "utf-8";
    private ProgressBar progress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.privacy_fragment, container,false);
        webview = (WebView)v.findViewById(R.id.webview);
        progress = (ProgressBar)v.findViewById(R.id.progress);

        if (CarnivalsSingleton.getInstance().getPrivacyData()!=null){
            webview.loadData(CarnivalsSingleton.getInstance().getPrivacyData(), mimeType, encoding);
        }else{
            new GetAsync(getActivity(), null).execute();
        }

        return v;
    }

    class GetAsync extends AsyncTask<String, String, String> {

        ServiceHandler jsonParser = new ServiceHandler();


        private Activity mContext;
        private String responseStatus;
        private String jsonData;

        public GetAsync(Activity context, ProgressBar carnivalsProgress) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... args) {

            try {
                responseStatus = jsonParser.makeHttpRequest(
                        PRIVACY_URL, "GET", null);
                return responseStatus;
            } catch (Exception e) {
                e.printStackTrace();
                responseStatus = ERROR;
            }

            return null;
        }

        protected void onPostExecute(String jsonArray) {

            if (progress!=null){
                progress.setVisibility(View.GONE);
            }

            try {

                if (jsonArray!=null){
                    JSONObject jsonObject = new JSONObject(jsonArray);
                    boolean status = jsonObject.optBoolean("success");
                    CarnivalsSingleton.getInstance().setPrivacyStatus(status);

                    if (status){
                        String content = jsonObject.optString("content");
                        if (content!=null){
                            CarnivalsSingleton.getInstance().setPrivacyData(content);
                        }else{
                            CarnivalsSingleton.getInstance().setPrivacyData("<h3>Bad Response! Please Try Again</h3><br/>");
                        }

                    }else{
                        String content = jsonObject.optString("error");
                        if (content!=null){
                            CarnivalsSingleton.getInstance().setPrivacyData("<h3>+"+content+"</h3><br/>");
                        }else{
                            CarnivalsSingleton.getInstance().setPrivacyData("<h3>Bad Response! Please Try Again</h3><br/>");
                        }
                    }

                }else{
                    CarnivalsSingleton.getInstance().setPrivacyData("<h3>Bad Response! Please Try Again</h3><br/>");
                }

                if (CarnivalsSingleton.getInstance().getPrivacyData()!=null){
                    webview.loadData(CarnivalsSingleton.getInstance().getPrivacyData(), mimeType, encoding);
                }else{
                    webview.loadData("<h3>Bad Response! Please Try Again</h3><br/>", mimeType, encoding);
                }

            } catch (Exception e) {
                e.printStackTrace();
                CarnivalsSingleton.getInstance().setPrivacyData("<h3>Bad Response! Please Try Again</h3><br/>");
                Log.d(TAG, "error---> "+e.toString());
            }

        }

    }

}
