package com.techplicit.mycarnival.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.techplicit.mycarnival.CarnivalsListActivity;
import com.techplicit.mycarnival.MainActivity;
import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.ui.activities.AboutUsActivity;
import com.techplicit.mycarnival.ui.activities.BandTabsActivity;
import com.techplicit.mycarnival.ui.activities.BaseActivity;
import com.techplicit.mycarnival.ui.activities.SmartUpdateActivity;
import com.techplicit.mycarnival.ui.activities.UpdateBandLocationActivity;
import com.techplicit.mycarnival.utils.Constants;

/**
 * Created by pnaganjane001 on 22/01/16.
 */
public class TruckVerificationFragment extends DialogFragment implements Constants {

    private static final String TAG = TruckVerificationFragment.class.getName();
    private SharedPreferences sharedPreferences;
    private String from;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.truck_verification_dialog, null, false);

        sharedPreferences = getActivity().getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);

        getDialog().setTitle("Verify");

        final EditText et_verification_code = (EditText)v.findViewById(R.id.et_verification_code);
        Button btn_verify = (Button)v.findViewById(R.id.btn_verify);

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(et_verification_code.getText().toString())){
                    if (et_verification_code.getText().toString().equalsIgnoreCase("2016a")){
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(IS_IN_TRUCK_MODE, true);
                        editor.commit();

                        try{
                            if(getActivity() instanceof BaseActivity){
                                BaseActivity.mNavigationDrawerFragment.changeNavigationList(getActivity());
                            }

                        }catch (Exception e){
                            Log.e(TAG, "e-=--> "+e.toString());
                        }

                        try{
                            if(getActivity() instanceof BandTabsActivity){
                                BandTabsActivity.mNavigationDrawerFragment.changeNavigationList(getActivity());
                            }


                        }catch (Exception e){
                            Log.e(TAG, "e-=--> "+e.toString());
                        }

                        try{
                            if(getActivity() instanceof UpdateBandLocationActivity){
                                UpdateBandLocationActivity.mNavigationDrawerFragment.changeNavigationList(getActivity());
                            }

                        }catch (Exception e){
                            Log.e(TAG, "e-=--> "+e.toString());
                        }

                        try{
                            if(getActivity() instanceof SmartUpdateActivity){
                                SmartUpdateActivity.mNavigationDrawerFragment.changeNavigationList(getActivity());
                                SmartUpdateActivity.displayDurationDialog(getActivity());
                            }

                        }catch (Exception e){
                            Log.e(TAG, "e-=--> "+e.toString());
                        }

                        try{
                            if(getActivity() instanceof MainActivity){
                                MainActivity.mNavigationDrawerFragment.changeNavigationList(getActivity());
                            }

                        }catch (Exception e){
                            Log.e(TAG, "e-=--> "+e.toString());
                        }

                        try{
                            if(getActivity() instanceof AboutUsActivity){
                                AboutUsActivity.mNavigationDrawerFragment.changeNavigationList(getActivity());
                            }

                        }catch (Exception e){
                            Log.e(TAG, "e-=--> "+e.toString());
                        }

                        try{
                            if(getActivity() instanceof CarnivalsListActivity){
                                CarnivalsListActivity.mNavigationDrawerFragment.changeNavigationList(getActivity());
                            }

                        }catch (Exception e){
                            Log.e(TAG, "e-=--> "+e.toString());
                        }

                        Toast.makeText(getActivity().getApplicationContext(), "Successful", Toast.LENGTH_LONG).show();
                        getDialog().dismiss();
                    }else{
                            Toast.makeText(getActivity().getApplicationContext(), "Password Mismatched", Toast.LENGTH_LONG).show();
                    }
                }else{
                    et_verification_code.setError("Enter Password");
                }
            }
        });

        return v;
    }
}
