
package com.techplicit.mycarnival.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BandsPojo implements Parcelable{

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("startDate")
    @Expose
    private String startDate;
    @SerializedName("endDate")
    @Expose
    private String endDate;
    @SerializedName("activeFlag")
    @Expose
    private Boolean activeFlag;

    protected BandsPojo(Parcel in) {
        name = in.readString();
        image = in.readString();
        startDate = in.readString();
        endDate = in.readString();
    }

    public static final Creator<BandsPojo> CREATOR = new Creator<BandsPojo>() {
        @Override
        public BandsPojo createFromParcel(Parcel in) {
            return new BandsPojo(in);
        }

        @Override
        public BandsPojo[] newArray(int size) {
            return new BandsPojo[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Boolean getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(image);
        dest.writeString(startDate);
        dest.writeString(endDate);
    }
}
