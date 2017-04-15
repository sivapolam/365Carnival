package com.techplicit.carnivaladmin.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.techplicit.carnivaladmin.R;
import com.techplicit.carnivaladmin.interfaces.IFragmentListener;
import com.techplicit.carnivaladmin.ui.activity.MainActivity;
import com.techplicit.carnivalcommons.apipresenter.ApiResponsePresenter;
import com.techplicit.carnivalcommons.interfaces.IRequestInterface;
import com.techplicit.carnivalcommons.interfaces.IResponseInterface;
import com.techplicit.carnivalcommons.utils.Constants;
import com.techplicit.carnivalcommons.utils.UtilityCommon;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by pnaganjane001 on 3/25/17.
 */

public class BandUpdateFragment extends Fragment implements IResponseInterface, IFragmentListener {

    private static final String LOG_TAG = BandUpdateFragment.class.getSimpleName();
    private static final String UPDATE_LOCATION = "update_location";
    private static int mDurationValue = 1;
    private static final int MIN_VALUE = 1;

    private NumberPicker bandsPicker;
    private String mCarnivalName;
    private String mBandNameSelected;
    private TextView mSelectBandText, mUpdateLocationText;
    private MainActivity mActivity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_band_update, container, false);

        initViews(rootView);

        return rootView;
    }

    private void initViews(View rootView) {
        mActivity = (MainActivity)getActivity();
        mBandNameSelected = mActivity.mBandNameSelected;
        RelativeLayout layout_select_band = (RelativeLayout) rootView.findViewById(R.id.layout_select_band);
        mSelectBandText = (TextView) rootView.findViewById(R.id.text_select_band);
        mUpdateLocationText = (TextView) rootView.findViewById(R.id.update_location);
        ImageView updateLocationBtn = (ImageView) rootView.findViewById(R.id.update_location_btn);

        layout_select_band.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.displayDurationDialog(getActivity(), MainActivity.BAND_UPDATE);
            }
        });

        updateLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLocation();
            }
        });

    }

    private void updateLocation() {
        if (!mSelectBandText.getText().toString().trim().equalsIgnoreCase(getActivity().getResources().getString(R.string.select_band))) {
            if (!UtilityCommon.isNetworkConnectionAvailable(getActivity())) {
                UtilityCommon.displayNetworkFailDialog(getActivity(), Constants.NETWORK_FAIL);
            } else if (mActivity.isLocationAvailable()){
                mActivity.mBandAddress = UtilityCommon.getCurrentLocationAddress(getActivity(),
                        mActivity.mLatitude, mActivity.mLongitude);
                makeBandLocationAPI();
            } else {
                //TODO: Use SnackBar
                Toast.makeText(getActivity(), "Problem in Fetching Location...", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), "Please select Band!", Toast.LENGTH_LONG).show();
        }
    }

    private void makeBandLocationAPI() {
        ApiResponsePresenter apiResponsePresenter = new ApiResponsePresenter(this);

        UtilityCommon.showProgressDialog(getActivity(), getString(R.string.loading_bands));

        try {
            String updateLocationUrl = Constants.BASE_URL + "updatebandlocation?carnival="
                    + URLEncoder.encode(mActivity.selectedCarnivalName, "UTF-8")
                    + "&band=" + URLEncoder.encode(mBandNameSelected, "UTF-8")
                    + "&address=" + URLEncoder.encode(mActivity.mBandAddress, "UTF-8")
                    + "&latitude=" + mActivity.mLatitude
                    + "&longitude=" + mActivity.mLongitude;

            apiResponsePresenter.callApi(Request.Method.GET,
                    updateLocationUrl,
                    null,
                    UPDATE_LOCATION,
                    IRequestInterface.REQUEST_TYPE_JSON_OBJECT);
        } catch (UnsupportedEncodingException e) {
            Log.e(LOG_TAG, "exception: "+e.toString(), e);
        } catch (Exception e) {
            Log.e(LOG_TAG, "exception: "+e.toString(), e);
        }
        
    }

    @Override
    public void onResponseSuccess(String resp, String req) {
        Log.d(LOG_TAG, "onResponseSuccess--> "+resp);
    }

    @Override
    public void onResponseFailure(String req) {
        Log.d(LOG_TAG, "onResponseFailure--> req: "+req);
    }

    @Override
    public void onApiConnected(String req) {
        Log.d(LOG_TAG, "onApiConnected--> req"+req);
    }

    @Override
    public void updateSelectedBand(String message) {
        mSelectBandText.setText(message);
    }
}
