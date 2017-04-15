package com.techplicit.mycarnival.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.techplicit.mycarnival.R;

import static com.techplicit.mycarnival.utils.Constants.IS_SIGNED_IN;
import static com.techplicit.mycarnival.utils.Constants.PREFS_CARNIVAL;

/**
 * Created by pnaganjane001 on 4/13/17.
 */

public class NavigationAdapter extends BaseAdapter{

    private static final String TAG = NavigationAdapter.class.getSimpleName();
    private final LayoutInflater mInflater;
    private final SharedPreferences mSharedPreferences;
    private Context mContext;
    private String[] mNavigationItems;

    public NavigationAdapter(Context context, String[] navigationItems) {
//        Log.e(TAG, "Constructor");
        mContext = context;
        mNavigationItems = navigationItems;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSharedPreferences = mContext.getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
//        Log.e(TAG, "Constructor end: " +mNavigationItems.length);
    }

    @Override
    public int getCount() {
        return mNavigationItems.length;
    }

    @Override
    public Object getItem(int position) {
        return mNavigationItems[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        Log.e(TAG, "Inside getView()");
        ViewHolder viewHolder = new ViewHolder();
        View view = mInflater.inflate(R.layout.navigation_list_item, parent, false);
        viewHolder.itemName = (TextView) view.findViewById(R.id.text_navigation_item);
        viewHolder.itemName.setText(mNavigationItems[position]);

        if (position == 3) {
            boolean isSignedIn = mSharedPreferences.getBoolean(IS_SIGNED_IN, false);
//            Log.e(TAG, "isSignedIn: "+isSignedIn);
            if (isSignedIn) {
                viewHolder.itemName.setText("Sign Out");
            } else {
                viewHolder.itemName.setText("Sign In");
            }
        }

        return view;
    }

    public class ViewHolder {
        TextView itemName;
    }

}
