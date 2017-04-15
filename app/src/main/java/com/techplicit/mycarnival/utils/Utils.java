package com.techplicit.mycarnival.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.techplicit.mycarnival.MyApplication;
import com.techplicit.mycarnival.data.model.BandSectionPojo;
import com.techplicit.mycarnival.data.model.BandsPojo;
import com.techplicit.mycarnival.data.model.FavouritesPojo;
import com.techplicit.mycarnival.data.model.FeteDetailModel;
import com.techplicit.mycarnival.data.model.SortedDistanceBandsPojo;

import java.io.InputStream;
import java.io.OutputStream;

public class Utils implements Constants {

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {

        }
    }

    public static void storeFavourites(FavouritesPojo favouritsPojo) {
        SharedPreferences settings;
        settings =  MyApplication.getInstance().getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(favouritsPojo);
        editor.putString("favouritsPojo", json);
        editor.commit();
    }

    public static FavouritesPojo getFavourites() {
        SharedPreferences settings = MyApplication.getInstance().getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = settings.getString("favouritsPojo", null);
        FavouritesPojo obj = gson.fromJson(json, FavouritesPojo.class);
        return obj;
    }

    public static void storeBand(BandsPojo bandsPojo) {
        FavouritesPojo favouritesPojo = getFavourites();
        favouritesPojo.getStringBandsPojoHashMap().put(bandsPojo.getName(), bandsPojo);
        storeFavourites(favouritesPojo);
    }

    public static void removeBand(BandsPojo bandsPojo) {
        FavouritesPojo favouritesPojo = getFavourites();
        Log.v("Size Before","Before "+favouritesPojo.getStringBandsPojoHashMap().size());
        favouritesPojo.getStringBandsPojoHashMap().remove(bandsPojo.getName());
        Log.v("Size Before","After "+favouritesPojo.getStringBandsPojoHashMap().size());
        storeFavourites(favouritesPojo);
    }

    public static boolean isBandFav(BandsPojo bandsPojo) {
        FavouritesPojo favouritesPojo = getFavourites();
        return favouritesPojo.getStringBandsPojoHashMap().containsKey(bandsPojo.getName());
    }

    public static void storeBandSection(BandSectionPojo bandSectionPojo) {
        FavouritesPojo favouritesPojo = getFavourites();
        favouritesPojo.getStringBandSectionPojoHashMap().put(bandSectionPojo.getName(), bandSectionPojo);
        storeFavourites(favouritesPojo);
    }

    public static void removeBandSection(BandSectionPojo bandSectionPojo) {
        FavouritesPojo favouritesPojo = getFavourites();
        favouritesPojo.getStringBandSectionPojoHashMap().remove(bandSectionPojo.getName());
        storeFavourites(favouritesPojo);
    }

    public static boolean isBandSecFav(BandSectionPojo bandSectionPojo) {
        FavouritesPojo favouritesPojo = getFavourites();
        return favouritesPojo.getStringBandSectionPojoHashMap().containsKey(bandSectionPojo.getName());
    }

    public static void storeBandLocation(SortedDistanceBandsPojo bandLocationPojo) {
        FavouritesPojo favouritesPojo = getFavourites();
        favouritesPojo.getStringBandLocationPojoHashMap().put(bandLocationPojo.getName(), bandLocationPojo);
        storeFavourites(favouritesPojo);
    }

    public static void removeBandLocation(SortedDistanceBandsPojo bandLocationPojo) {
        FavouritesPojo favouritesPojo = getFavourites();
        favouritesPojo.getStringBandLocationPojoHashMap().remove(bandLocationPojo.getName());
        storeFavourites(favouritesPojo);
    }

    public static boolean isBandLocationFav(SortedDistanceBandsPojo bandLocationPojo) {
        FavouritesPojo favouritesPojo = getFavourites();
        return favouritesPojo.getStringBandsPojoHashMap().containsKey(bandLocationPojo.getName());
    }

    public static void removeAllBandLocation() {
        FavouritesPojo favouritesPojo = getFavourites();
        favouritesPojo.getStringBandLocationPojoHashMap().clear();
        storeFavourites(favouritesPojo);
    }

    public static void storeBandFeteDetail(FeteDetailModel feteDetailModel) {
        FavouritesPojo favouritesPojo = getFavourites();
        favouritesPojo.getStringFeteDetailModelHashMap().put(feteDetailModel.getName(), feteDetailModel);
        storeFavourites(favouritesPojo);
    }

    public static void removeBandFeteDetail(FeteDetailModel feteDetailModel) {
        FavouritesPojo favouritesPojo = getFavourites();
        favouritesPojo.getStringFeteDetailModelHashMap().remove(feteDetailModel.getName());
        storeFavourites(favouritesPojo);
    }

    public static boolean isBandFeteDetailFav(FeteDetailModel feteDetailModel) {
        FavouritesPojo favouritesPojo = getFavourites();
        return favouritesPojo.getStringFeteDetailModelHashMap().containsKey(feteDetailModel.getName());
    }

}