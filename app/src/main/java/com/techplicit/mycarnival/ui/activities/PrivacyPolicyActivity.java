package com.techplicit.mycarnival.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.facebook.CallbackManager;
import com.techplicit.mycarnival.IntentGenerator;
import com.techplicit.mycarnival.NavigationDrawerFragment;
import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.data.CarnivalsSingleton;
import com.techplicit.mycarnival.data.ServiceHandler;
import com.techplicit.mycarnival.ui.fragments.AboutFragment;
import com.techplicit.mycarnival.ui.fragments.ContactFragment;
import com.techplicit.mycarnival.utils.Constants;

import org.json.JSONObject;

/**
 * Created by pnaganjane001 on 27/12/15.
 */
public class PrivacyPolicyActivity extends BaseActivity
        implements Constants, AboutFragment.OnFragmentInteractionListener, ContactFragment.OnFragmentInteractionListener {

    private static final String TAG = PrivacyPolicyActivity.class.getName();
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    public static NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private FragmentManager fragmentManager;
    private Fragment currentFragment;
    private CallbackManager callbackManager;
    private boolean isSignedIn;
    private SharedPreferences sharedPreferences;
    public static ProgressDialog progressDialog;

    private static boolean isCreating = true;
    private WebView webview;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hide the Title bar of this activity screen
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.privacy_fragment, frameLayout);

        mContext = PrivacyPolicyActivity.this;

        if (mNavigationDrawerFragment.mDrawerListView != null) {
            mNavigationDrawerFragment.mDrawerListView.setItemChecked(1, true);
        }

        sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);

        home_icon.setImageResource(R.drawable.home);
        titleHome.setVisibility(View.GONE);
        title.setText("Privacy Policy");
        title.setVisibility(View.VISIBLE);

        home_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startHomeActivity(PrivacyPolicyActivity.this);
                finish();
            }
        });

        webview = (WebView)findViewById(R.id.webview);
        progress = (ProgressBar)findViewById(R.id.progress);

        String mimeType = "text/html";
        String encoding = "utf-8";

        if (CarnivalsSingleton.getInstance().getPrivacyData()!=null){
            webview.loadData(CarnivalsSingleton.getInstance().getPrivacyData(), mimeType, encoding);
        }else{
            new GetAsync(PrivacyPolicyActivity.this, null, webview).execute();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mContext = PrivacyPolicyActivity.this;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static ListView carnivalsList;
        private ProgressDialog pDialog;
        private AlertDialog alertDialog;

        private WebView webview;

        private ProgressBar progress;
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 final Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.privacy_fragment, container, false);

            webview = (WebView)rootView.findViewById(R.id.webview);
            progress = (ProgressBar)rootView.findViewById(R.id.progress);

            String mimeType = "text/html";
            String encoding = "utf-8";

            if (CarnivalsSingleton.getInstance().getPrivacyData()!=null){
                webview.loadData(CarnivalsSingleton.getInstance().getPrivacyData(), mimeType, encoding);
            }else{
                new GetAsync(getActivity(), null, webview).execute();
            }


            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }


        static class GetAsync extends AsyncTask<String, String, String> {

            ServiceHandler jsonParser = new ServiceHandler();


            private Activity mContext;
            private String responseStatus;
            private String jsonData;
            private ProgressDialog pd;
            WebView webView;

            public GetAsync(Activity context, ProgressBar carnivalsProgress, WebView webview) {
                mContext = context;
                this.webView = webview;
            }

            @Override
            protected void onPreExecute() {
                pd = new ProgressDialog(mContext);
                pd.show();
                pd.dismiss();

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

                if (pd!=null){
                    pd.dismiss();
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

                    String mimeType = "text/html";
                    String encoding = "utf-8";

                    if (CarnivalsSingleton.getInstance().getPrivacyData()!=null){
                        webView.loadData(CarnivalsSingleton.getInstance().getPrivacyData(), mimeType, encoding);
                    }else{
                        webView.loadData("<h3>Bad Response! Please Try Again</h3><br/>", mimeType, encoding);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    CarnivalsSingleton.getInstance().setPrivacyData("<h3>Bad Response! Please Try Again</h3><br/>");
                }

            }

        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCreating = true;
    }

    @Override
    public void onBackPressed() {
    }

    class GetAsync extends AsyncTask<String, String, String> {

        ServiceHandler jsonParser = new ServiceHandler();


        private Activity mContext;
        private String responseStatus;
        private String jsonData;
        private ProgressDialog pd;
        WebView webView;

        public GetAsync(Activity context, ProgressBar carnivalsProgress, WebView webview) {
            mContext = context;
            this.webView = webview;
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mContext);
            pd.setMessage("Loading Content");
            pd.show();
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

            if (pd!=null){
                pd.dismiss();
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

                String mimeType = "text/html";
                String encoding = "utf-8";

                if (CarnivalsSingleton.getInstance().getPrivacyData()!=null){
                    webView.loadData(CarnivalsSingleton.getInstance().getPrivacyData(), mimeType, encoding);
                }else{
                    webView.loadData("<h3>Bad Response! Please Try Again</h3><br/>", mimeType, encoding);
                }

            } catch (Exception e) {
                e.printStackTrace();
                CarnivalsSingleton.getInstance().setPrivacyData("<h3>Bad Response! Please Try Again</h3><br/>");
            }

        }

    }


}
