package com.techplicit.carnivaladmin.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.techplicit.carnivaladmin.R;
import com.techplicit.carnivalcommons.CommonSingleton;
import com.techplicit.carnivalcommons.ServiceHandler;
import com.techplicit.carnivalcommons.data.CarnivalsPojo;
import com.techplicit.carnivalcommons.adapters.CarnivalsListAdapter;
import com.techplicit.carnivalcommons.utils.Constants;
import com.techplicit.carnivalcommons.utils.UtilityCommon;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class CarnivalsListActivity extends AppCompatActivity
        implements Constants {

    private static final String TAG = CarnivalsListActivity.class.getName();

    private final int ALL_PERMISSION_CODE = 1001;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private static boolean isCreating = true;

    private static ListView carnivalsList;
    private TextView emptyText;
    private ProgressBar carnivalsProgress;
    private SharedPreferences sharedPreferences;
    private TextView mScreenTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hide the Title bar of this activity screen
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_carnivals_list);

        sharedPreferences = getSharedPreferences(PREFS_BAND_ADMIN, Context.MODE_PRIVATE);

        carnivalsList = (ListView) findViewById(R.id.list_carnivals);
        carnivalsProgress = (ProgressBar) findViewById(R.id.progress_carnivals_list);
        emptyText = (TextView) findViewById(R.id.emptyText);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/ftra_hv.ttf");
        emptyText.setVisibility(View.GONE);
        emptyText.setTypeface(face);

        mScreenTitle = (TextView) findViewById(R.id.title_home);
        mScreenTitle.setText(getString(R.string.app_name) +" v"+getString(R.string.version_num_admin));

        carnivalsProgress.setVisibility(View.VISIBLE);
        carnivalsProgress.getIndeterminateDrawable().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY);

        carnivalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<CarnivalsPojo> carnivalsPojoArrayList = CommonSingleton.getInstance().getCarnivalsPojoArrayList();
                if (carnivalsPojoArrayList != null && !carnivalsPojoArrayList.isEmpty()) {
                    CarnivalsPojo carnivalsPojo = (CarnivalsPojo) carnivalsPojoArrayList.get(position);
                    if (carnivalsPojo.getActiveFlag()) {
                        Intent intent = new Intent(CarnivalsListActivity.this, MainActivity.class);
                        intent.putExtra(SELECTED_CARNIVAL_NAME, carnivalsPojo.getName());
                        startActivity(intent);
                    }

                    /*SharedPreferences sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SELECTED_CARNIVAL_NAME, carnivalsPojo.getName());
                    editor.commit();*/
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

        checkAndRequestPermissions();

    }

    private boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();

        /*if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }*/

        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), ALL_PERMISSION_CODE);
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isCreating = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCreating = true;
        CommonSingleton.getInstance().clearCarnivalsData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    private void getCarnivalsList(TextView emptyText, ProgressBar carnivalsProgress) {
        if (UtilityCommon.isNetworkConnectionAvailable(CarnivalsListActivity.this)) {
            new GetAsync(CarnivalsListActivity.this, carnivalsProgress, emptyText).execute();
        } else {
            carnivalsProgress.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText("No Network ! Please Connect \n and Tap Here !!");
            UtilityCommon.displayNetworkFailDialog(CarnivalsListActivity.this, NETWORK_FAIL);
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
                UtilityCommon.displayNetworkFailDialog(mContext, ERROR);
            }

            if (jsonArray != null && jsonArray.length() > 0) {

                CommonSingleton.getInstance().setCarnivalsJsonResponse(jsonArray);

                if (CommonSingleton.getInstance().getCarnivalsJsonResponse() != null) {
                    quizModelArrayList = CommonSingleton.getInstance().getCarnivalsPojoArrayList();

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


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                //denied
                Toast.makeText(this, "Oops you denied the permission", Toast.LENGTH_LONG).show();
            } else {
                if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                    //allowed
                    Toast.makeText(this, "Permission granted ", Toast.LENGTH_LONG).show();
//                    getCarnivalsList(emptyText, carnivalsProgress);
                } else {
                    //set to never ask again
                    Log.e("set to never ask again", permission);
                    promptSettings();
                }
            }
        }
    }

    private void promptSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You have denied permissions");
        builder.setMessage("Please go to app settings and allow permissions");
        builder.setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                goToSettings();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void goToSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.parse("package:" + getPackageName());
        intent.setData(uri);
        startActivity(intent);
    }
}
