
package com.techplicit.mycarnival.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BandSectionPojo implements Parcelable{

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("year")
    @Expose
    private Integer year;
    @SerializedName("image")
    @Expose
    private String image;

    protected BandSectionPojo(Parcel in) {
        name = in.readString();
        image = in.readString();
    }

    public static final Creator<BandSectionPojo> CREATOR = new Creator<BandSectionPojo>() {
        @Override
        public BandSectionPojo createFromParcel(Parcel in) {
            return new BandSectionPojo(in);
        }

        @Override
        public BandSectionPojo[] newArray(int size) {
            return new BandSectionPojo[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(image);
    }
}
