package com.techplicit.mycarnival.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.techplicit.carnivalcommons.utils.UtilityCommon;
import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.data.model.SortedDistanceBandsPojo;
import com.techplicit.mycarnival.utils.BandsDateFormatsConverter;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by pnaganjane001 on 14/11/15.
 */
public class BandLocationSortedGridAdapter extends BaseAdapter implements Constants {

    private Context context;

    LayoutInflater inflater;
    private ArrayList<SortedDistanceBandsPojo> bandsPojoArrayList;
    ImageLoader imageLoader;
    private SortedDistanceBandsPojo bandsPojo;

    public BandLocationSortedGridAdapter(Context context, ArrayList<SortedDistanceBandsPojo> carnivalsPojoArrayList) {
        this.context = context;
        this.bandsPojoArrayList = carnivalsPojoArrayList;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        rootView = inflater.inflate(R.layout.band_location_list_item, null);
        holder.galleryImage = (ImageView)rootView.findViewById(R.id.image_band);

        holder.titleBand = (TextView)rootView.findViewById(R.id.title_band);
        holder.subTitleBand = (TextView)rootView.findViewById(R.id.sub_title_band);
        holder.timeBand = (TextView)rootView.findViewById(R.id.time_band);
        holder.updatesBand = (TextView)rootView.findViewById(R.id.updates_band);

        bandsPojo = (SortedDistanceBandsPojo)this.bandsPojoArrayList.get(position);

        if (bandsPojo.getLastUpdated() != null) {
            String lastAccessOn = UtilityCommon.getDate(Long.valueOf(bandsPojo.getLastUpdated()), "MM/dd/yyyy HH:mm:ss");


            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Calendar c = Calendar.getInstance();
            String currentDate = format.format(c.getTime());

            Date d1 = new Date();
            Date d2 = null;

            try {
                d1 = format.parse(currentDate);
                d2 = format.parse(lastAccessOn);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // in milliseconds
            long diff = d1.getTime() - d2.getTime();

            String lastUpdateStatus = BandsDateFormatsConverter.printDateDifferenceInUIWithDefinedFormat(diff);

            if (lastUpdateStatus!=null && !lastUpdateStatus.equalsIgnoreCase("")){
                holder.timeBand.setText("Updated: "+lastUpdateStatus);
            }
        }


        holder.titleBand.setText(bandsPojo.getName());
        if (bandsPojo.getAddress() != null && !bandsPojo.getAddress().isEmpty()) {
            holder.subTitleBand.setText(bandsPojo.getAddress());
        } else {
            holder.subTitleBand.setText("Location not updated");
        }
        holder.updatesBand.setText(bandsPojo.getUpdates() + " UPDATES");

        rootView.setId(position);
        if (bandsPojo.getImage()!=null){
            Picasso.with(context).load(bandsPojo.getImage()).into(holder.galleryImage);

//            imageLoader.DisplayImage(bandsPojo.getImage(), holder.galleryImage);
        }

        return rootView;
    }

    public class ViewHolder{
        ImageView galleryImage;
        TextView titleBand;
        TextView subTitleBand;
        TextView timeBand;
        TextView updatesBand;
    }
}
