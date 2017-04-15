package com.techplicit.mycarnival.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.data.model.BandSectionPojo;
import com.techplicit.mycarnival.interfaces.NoDataInterface;
import com.techplicit.mycarnival.ui.fragments.BandsSectionMyFavourites;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.ImageLoader;
import com.techplicit.mycarnival.utils.Utils;

import java.util.ArrayList;


/**
 * Created by pnaganjane001 on 14/11/15.
 */
public class BandSectionFavAdapter extends BaseAdapter implements Constants {

    private Context context;

    LayoutInflater inflater;
    private ArrayList<BandSectionPojo> bandsPojoArrayList;
    ImageLoader imageLoader;
    NoDataInterface noDataInterface;

    public BandSectionFavAdapter(Context context, BandsSectionMyFavourites bandsSectionMyFavourites, ArrayList<BandSectionPojo> carnivalsPojoArrayList) {
        this.context = context;
        this.bandsPojoArrayList = carnivalsPojoArrayList;
        this.noDataInterface = (NoDataInterface) bandsSectionMyFavourites;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(context);
    }

    @Override
    public int getCount() {
        return this.bandsPojoArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder = new ViewHolder();
        final View rootView;
        rootView = inflater.inflate(R.layout.bands_item, null);
        holder.iv_band_image = (ImageView) rootView.findViewById(R.id.iv_band_image);
        holder.iv_band_fav = (ImageView) rootView.findViewById(R.id.iv_band_fav);
        holder.tv_band_name = (TextView) rootView.findViewById(R.id.tv_band_name);

        holder.tv_band_name.setText("" + bandsPojoArrayList.get(position).getName());

        rootView.setId(position);

        if (bandsPojoArrayList.get(position).getImage() != null) {
            Picasso.with(context).load(bandsPojoArrayList.get(position).getImage()).into(holder.iv_band_image);
//            imageLoader.DisplayImage(bandsPojoArrayList.get(position).getImage(), holder.iv_band_image);
        }

        if (Utils.isBandSecFav(bandsPojoArrayList.get(position)))
            holder.iv_band_fav.setImageResource(R.drawable.fav);
        else
            holder.iv_band_fav.setImageResource(R.drawable.fav_white);

        holder.iv_band_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.removeBandSection(bandsPojoArrayList.get(position));
                bandsPojoArrayList.remove(position);
                notifyDataSetChanged();
                if (bandsPojoArrayList.size() == 0) {
                    noDataInterface.noData();
                }
            }
        });

        return rootView;
    }

    public class ViewHolder {
        ImageView iv_band_image;
        ImageView iv_band_fav;
        TextView tv_band_name;
    }
}
