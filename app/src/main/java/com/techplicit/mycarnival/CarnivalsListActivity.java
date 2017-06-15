package com.techplicit.mycarnival;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.techplicit.mycarnival.adapters.CarnivalsListAdapter;
import com.techplicit.mycarnival.data.CarnivalsSingleton;
import com.techplicit.mycarnival.data.ServiceHandler;
import com.techplicit.mycarnival.data.model.CarnivalsPojo;
import com.techplicit.mycarnival.ui.activities.BandTabsActivity;
import com.techplicit.mycarnival.ui.activities.BaseActivity;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.Utility;

import org.json.JSONArray;

import java.util.ArrayList;


public class CarnivalsListActivity extends BaseActivity
        implements Constants {

    private static final String TAG = CarnivalsListActivity.class.getName();
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private static boolean isCreating = true;

    private static ListView carnivalsList;
    private TextView emptyText;
    private ProgressBar carnivalsProgress;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hide the Title bar of this activity screen
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        isCreating = true;
        Utility.bandName = null;
        getLayoutInflater().inflate(R.layout.fragment_carnivals_list, frameLayout);

        mContext = CarnivalsListActivity.this;

        sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);

        home_icon.setImageResource(R.drawable.home);
        titleHome.setVisibility(View.GONE);
        title.setText("Carnivals");
        title.setVisibility(View.VISIBLE);

        home_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startHomeActivity(CarnivalsListActivity.this);
                finish();
            }
        });

        carnivalsList = (ListView) findViewById(R.id.list_carnivals);
        carnivalsProgress = (ProgressBar) findViewById(R.id.progress_carnivals_list);
        emptyText = (TextView) findViewById(R.id.emptyText);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/ftra_hv.ttf");
        emptyText.setVisibility(View.GONE);
        emptyText.setTypeface(face);

        carnivalsProgress.setVisibility(View.VISIBLE);
        carnivalsProgress.getIndeterminateDrawable().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY);

        carnivalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<CarnivalsPojo> carnivalsPojoArrayList = CarnivalsSingleton.getInstance().getCarnivalsPojoArrayList();
                if (carnivalsPojoArrayList != null) {
                    CarnivalsPojo carnivalsPojo = (CarnivalsPojo) carnivalsPojoArrayList.get(position);
                    if (carnivalsPojo.getActiveFlag()) {
                        IntentGenerator.startFetesActivity(CarnivalsListActivity.this);
                    }

                    SharedPreferences sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SELECTED_CARNIVAL_NAME, carnivalsPojo.getName());
                    editor.commit();
                }
            }
        });

        emptyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carnivalsProgress.setVisibility(View.VISIBLE);
                emptyText.setVisibility(View.GONE);
                getCarnivalsList(emptyText, carnivalsProgress);
            }
        });

        getCarnivalsList(emptyText, carnivalsProgress);

        ImageView backArrowCarnivalsList = (ImageView) findViewById(R.id.back_arrow_carnivals_list);
        backArrowCarnivalsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
        private TextView emptyText;
        private ProgressBar carnivalsProgress;

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
            View rootView = inflater.inflate(R.layout.fragment_carnivals_list, container, false);
            carnivalsList = (ListView) rootView.findViewById(R.id.list_carnivals);
            carnivalsProgress = (ProgressBar) rootView.findViewById(R.id.progress_carnivals_list);
            emptyText = (TextView) rootView.findViewById(R.id.emptyText);
            Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ftra_hv.ttf");
            emptyText.setVisibility(View.GONE);
            emptyText.setTypeface(face);

            carnivalsProgress.setVisibility(View.VISIBLE);
            carnivalsProgress.getIndeterminateDrawable().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY);

            carnivalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ArrayList<CarnivalsPojo> carnivalsPojoArrayList = CarnivalsSingleton.getInstance().getCarnivalsPojoArrayList();
                    if (carnivalsPojoArrayList != null) {
                        CarnivalsPojo carnivalsPojo = (CarnivalsPojo) carnivalsPojoArrayList.get(position);
                        if (carnivalsPojo.getActiveFlag()) {
                            Intent bandIntent = new Intent(getActivity(), BandTabsActivity.class);
                            startActivity(bandIntent);
                        }

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_CARNIVAL, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(SELECTED_CARNIVAL_NAME, carnivalsPojo.getName());
                        editor.commit();
                    }


                }
            });

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mContext = CarnivalsListActivity.this;
        isCreating = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCreating = true;
        CarnivalsSingleton.getInstance().clearCarnivalsData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    private void getCarnivalsList(TextView emptyText, ProgressBar carnivalsProgress) {
        if (Utility.isNetworkConnectionAvailable(CarnivalsListActivity.this)) {
            new GetAsync(CarnivalsListActivity.this, carnivalsProgress, emptyText).execute();
        } else {
            carnivalsProgress.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText("No Network ! Please Connect \n and Tap Here !!");
            Utility.displayNetworkFailDialog(CarnivalsListActivity.this, NETWORK_FAIL, "Success", "Successfully Invited !");
            isCreating = false;
        }
    }

    static class GetAsync extends AsyncTask<String, String, JSONArray> {

        private boolean isResponseSucceed;
        ServiceHandler jsonParser = new ServiceHandler();

        private ProgressDialog pDialog;
        private ProgressBar carnivalsProgress;

        private static final String CARNIVALS_URL = Constants.BASE_URL + "getcarnivals";

        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";
        private ArrayList<CarnivalsPojo> quizModelArrayList;

        private Activity mContext;
        private String responseStatus;
        private String jsonData;
        TextView emptyText;

        public GetAsync(Activity context, ProgressBar carnivalsProgress, TextView emptyText) {
            mContext = context;
            this.carnivalsProgress = carnivalsProgress;
            this.emptyText = emptyText;
        }

        @Override
        protected void onPreExecute() {
            this.carnivalsProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONArray doInBackground(String... args) {

            try {

                responseStatus = jsonParser.makeHttpRequest(
                        CARNIVALS_URL, "GET", null);
                Log.e(TAG, "responseStatus-->"+responseStatus);
                if (responseStatus != null && !responseStatus.equalsIgnoreCase(ERROR)) {
                    JSONArray jsonArray = null;

                    jsonArray = new JSONArray(responseStatus);

                    if (jsonArray != null) {
                        return jsonArray;
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
                responseStatus = ERROR;
            }

            return null;
        }

        protected void onPostExecute(JSONArray jsonArray) {

            if (responseStatus.equalsIgnoreCase(ERROR)) {
                Utility.displayNetworkFailDialog(mContext, ERROR, "Success", "Successfully Invited !");
            }

            if (jsonArray != null && jsonArray.length() > 0) {

                CarnivalsSingleton.getInstance().setCarnivalsJsonResponse(jsonArray);

                if (CarnivalsSingleton.getInstance().getCarnivalsJsonResponse() != null) {
                    quizModelArrayList = CarnivalsSingleton.getInstance().getCarnivalsPojoArrayList();

                    carnivalsList.setAdapter(new CarnivalsListAdapter(mContext, quizModelArrayList));
                    this.carnivalsProgress.setVisibility(View.GONE);
                    this.emptyText.setVisibility(View.GONE);
                }
            } else {
                this.carnivalsProgress.setVisibility(View.GONE);
                this.emptyText.setVisibility(View.VISIBLE);
                this.emptyText.setText("No Carnivals Available");
            }

            isCreating = false;
        }

    }


}
