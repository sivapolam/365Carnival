package com.techplicit.mycarnival.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by pnaganjane001 on 18/12/15.
 */
public class BandInformation extends Fragment implements Constants {

    private static final String TAG = BandInformation.class.getName();
    private LinearLayout ll_bandinfo;


    TextView tv_info_band_name;
    TextView tv_info_sect_name;
    TextView tv_info_year_val;
    TextView tv_data_title;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_band_info, container, false);
        ll_bandinfo = (LinearLayout) rootView.findViewById(R.id.ll_bandinfo);

        tv_info_band_name = (TextView) rootView.findViewById(R.id.tv_info_band_name);
        tv_info_sect_name = (TextView) rootView.findViewById(R.id.tv_info_sect_name);
        tv_info_year_val = (TextView) rootView.findViewById(R.id.tv_info_year_val);
        tv_data_title = (TextView) rootView.findViewById(R.id.tv_data_title);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void setData(JSONObject jsonObjectM) throws JSONException {

        if (jsonObjectM != null) {
            tv_info_band_name.setText(Utility.bandName);
            tv_info_sect_name.setText(jsonObjectM.getString("name"));
            tv_info_year_val.setText(jsonObjectM.getString("year"));

            if (jsonObjectM.has("costumes")) {
                JSONArray jsonArray = jsonObjectM.getJSONArray("costumes");
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    addChild(jsonObject.getString("type"), null);

                    JSONArray lines = jsonObject.getJSONArray("lines");

                    for (int j = 0; j < lines.length(); j++) {

                        JSONObject linesObject = lines.getJSONObject(j);

                        addChild(linesObject.getString("lineType"), linesObject.getString("lineCost"));

                        addChild("INCLUDES", linesObject.getString("lineIncludes"));

                        JSONObject lineAddons = linesObject.getJSONObject("lineAddons");
                        Iterator keys = lineAddons.keys();
                        while (keys.hasNext()) {
                            // loop to get the dynamic key
                            String currentDynamicKey = (String) keys.next();
                            // get the value of the dynamic key
                            String currentDynamicValue = lineAddons.getString(currentDynamicKey);
                            // do something here with the value...

                            addChild(currentDynamicKey, currentDynamicValue);

                        }
                    }
                    addChild(null, null);
                }
            }
        }
    }

    private void addChild(String subTitle, String value) {
        View child = getActivity().getLayoutInflater().inflate(R.layout.item_band_info, null);

        TextView tv_band_info_type = (TextView) child.findViewById(R.id.tv_band_info_type);
        TextView tv_band_info_type_value = (TextView) child.findViewById(R.id.tv_band_info_type_value);
        View vi_line = (View) child.findViewById(R.id.vi_line);
        tv_band_info_type.setText(subTitle+" : ");

        if (subTitle == null && value == null) {
            vi_line.setVisibility(View.VISIBLE);
            tv_band_info_type.setVisibility(View.GONE);
            tv_band_info_type_value.setVisibility(View.GONE);
        } else {
            vi_line.setVisibility(View.GONE);
            if (value == null)
                tv_band_info_type_value.setVisibility(View.GONE);
            else {
                tv_band_info_type_value.setVisibility(View.VISIBLE);
                tv_band_info_type_value.setText(value);
            }
        }
        ll_bandinfo.addView(child);
    }

}
