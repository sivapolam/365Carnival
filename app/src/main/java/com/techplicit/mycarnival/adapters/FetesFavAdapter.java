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
import com.techplicit.mycarnival.apipresenter.ApiResponsePresenter;
import com.techplicit.mycarnival.data.model.FeteDetailModel;
import com.techplicit.mycarnival.interfaces.IResponseInterface;
import com.techplicit.mycarnival.interfaces.NoDataInterface;
import com.techplicit.mycarnival.ui.fragments.FetesMyFavourites;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.ImageLoader;
import com.techplicit.mycarnival.utils.Utils;

import java.util.ArrayList;


/**
 * Created by pnaganjane001 on 14/11/15.
 */
public class FetesFavAdapter extends BaseAdapter implements Constants, IResponseInterface {

    private Context context;

    LayoutInflater inflater;
    private ArrayList<FeteDetailModel> bandsPojoArrayList;
    ImageLoader imageLoader;
    ApiResponsePresenter apiResponsePresenter;
    String FAV_REQUEST = "fav_request";
    NoDataInterface noDataInterface;

    public FetesFavAdapter(Context context, FetesMyFavourites fetesMyFavourites, ArrayList<FeteDetailModel> carnivalsPojoArrayList) {
        this.context = context;
        this.bandsPojoArrayList = carnivalsPojoArrayList;
        noDataInterface = (NoDataInterface) fetesMyFavourites;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(context);
        apiResponsePresenter = new ApiResponsePresenter(this);
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
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

        if (Utils.isBandFeteDetailFav(bandsPojoArrayList.get(position)))
            holder.iv_band_fav.setImageResource(R.drawable.fav);
        else
            holder.iv_band_fav.setImageResource(R.drawable.fav_white);

        holder.iv_band_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Utils.removeBandFeteDetail(bandsPojoArrayList.get(position));
                bandsPojoArrayList.remove(position);
                holder.iv_band_fav.setImageResource(R.drawable.fav_white);

                if (bandsPojoArrayList.size() == 0)
                    noDataInterface.noData();
            }
        });

//        holder.iv_band_fav.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
//                String email = sharedPreferences.getString(Constants.EMAIL, "");
//                if (TextUtils.isEmpty(email))
//                    Toast.makeText(context, "Please Login", Toast.LENGTH_LONG).show();
//                else {
//                    JSONObject jsonObject = new JSONObject();
//                    try {
//                        jsonObject.put("email", email);
//                        jsonObject.put("carnival", sharedPreferences.getString(Constants.SELECTED_CARNIVAL_NAME, ""));
//                        jsonObject.put("fete", bandsPojo.getName());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    apiResponsePresenter.callApi(Request.Method.POST, Constants.COMMON_USER_SERVICE_URL + "addfavouritefete", jsonObject, FAV_REQUEST, IRequestInterface.REQUEST_TYPE_JSON_OBJECT);
//                }
//            }
//        });

        return rootView;
    }

    @Override
    public void onResponseSuccess(String resp, String req) {

    }

    @Override
    public void onResponseFailure(String req) {

    }

    @Override
    public void onApiConnected(String req) {

    }

    public class ViewHolder {
        ImageView iv_band_image;
        ImageView iv_band_fav;
        TextView tv_band_name;
    }
}
