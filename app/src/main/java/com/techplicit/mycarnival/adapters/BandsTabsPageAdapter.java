package com.techplicit.mycarnival.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.techplicit.mycarnival.ui.fragments.BandLocationAlphaSortFragment;
import com.techplicit.mycarnival.ui.fragments.BandLocationDistanceFragment;
import com.techplicit.mycarnival.ui.fragments.BandLocationMyFavourites;
import com.techplicit.mycarnival.ui.fragments.BandLocationViewFragment;

/**
 * Created by pnaganjane001 on 18/12/15.
 */
public class BandsTabsPageAdapter extends FragmentPagerAdapter {

    public BandsTabsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                return new BandLocationDistanceFragment();
            case 1:
                return new BandLocationAlphaSortFragment();
            case 2:
                return new BandLocationMyFavourites();
            case 3:
                return new BandLocationViewFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

}
