package com.techplicit.carnivalcommons.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.techplicit.carnivalcommons.R;
import com.techplicit.carnivalcommons.data.CarnivalsPojo;
import com.techplicit.carnivalcommons.utils.Constants;
import com.techplicit.carnivalcommons.utils.ImageLoader;
import com.techplicit.carnivalcommons.utils.UtilityCommon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by pnaganjane001 on 14/11/15.
 */
public class CarnivalsListAdapter extends BaseAdapter implements Constants {

    private Context context;

    LayoutInflater inflater;
    private ArrayList<CarnivalsPojo> carnivalsPojoArrayList;
    ImageLoader imageLoader;
    private CarnivalsPojo carnivalsPojo;
    private String[] strArr;
    private String startMonth;
    private String[] endArr;
    private String endMonth;

    public CarnivalsListAdapter(Context context, ArrayList<CarnivalsPojo> carnivalsPojoArrayList) {
        this.context = context;
        this.carnivalsPojoArrayList = carnivalsPojoArrayList;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(context);

        Collections.sort(this.carnivalsPojoArrayList, new Comparator<CarnivalsPojo>() {
            public int compare(CarnivalsPojo dc1, CarnivalsPojo dc2) {
                return dc1.getDateTime().compareTo(dc2.getDateTime());
            }
        });

    }

    @Override
    public int getCount() {
        return this.carnivalsPojoArrayList.size();
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
        rootView = inflater.inflate(R.layout.carnivals_list_item, null);
        holder.galleryImage = (com.github.siyamed.shapeimageview.RoundedImageView)rootView.findViewById(R.id.image_carnival);

        holder.titleCarnival = (TextView)rootView.findViewById(R.id.title_carnival_list);
        holder.dateCarnival = (TextView)rootView.findViewById(R.id.date_carnival_list);

        carnivalsPojo = (CarnivalsPojo)this.carnivalsPojoArrayList.get(position);

        String startDate = UtilityCommon.getDate(Long.valueOf(carnivalsPojo.getStartDate()), "dd/MM/yyyy");

        if (startDate!=null && startDate.contains("/")){
            strArr = startDate.split("/");
            startMonth = UtilityCommon.getMonth(Integer.valueOf(strArr[1]));
        }

        String endDate = UtilityCommon.getDate(Long.valueOf(carnivalsPojo.getEndDate()), "dd/MM/yyyy");

        if (endDate!=null && endDate.contains("/")){
            endArr = endDate.split("/");
            endMonth = UtilityCommon.getMonth(Integer.valueOf(endArr[1]));
        }

        if (startMonth.equalsIgnoreCase(endMonth)){
            holder.dateCarnival.setText(startMonth +" "+strArr[0]+" - "+endArr[0]);
        }else{
            holder.dateCarnival.setText(startMonth +" "+strArr[0]+" - "+endMonth+" "+endArr[0]);
        }

        holder.titleCarnival.setText(carnivalsPojo.getName());

        rootView.setId(position);
        Bitmap bitmap = null;
        if (carnivalsPojo.getImage()!=null){
            Picasso.with(context).load(carnivalsPojo.getImage()).into(holder.galleryImage);

//            bitmap = imageLoader.DisplayImage(carnivalsPojo.getImage(), holder.galleryImage);
        }

        /*if (bitmap!=null){
            Bitmap circularBitmap = RoundedCornersImage.getRoundedCornerBitmap(bitmap);
            holder.galleryImage.setImageBitmap(circularBitmap);
        }*/

        return rootView;
    }

    public class ViewHolder{
        com.github.siyamed.shapeimageview.RoundedImageView galleryImage;
        TextView titleCarnival;
        TextView dateCarnival;
    }
}
