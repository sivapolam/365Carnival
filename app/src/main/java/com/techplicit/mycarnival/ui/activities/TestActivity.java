package com.techplicit.mycarnival.ui.activities;

import android.os.Bundle;

import com.techplicit.mycarnival.R;

/**
 * Created by pnaganjane001 on 25/01/16.
 */
public class TestActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.fragment_about, frameLayout);

    }
}
