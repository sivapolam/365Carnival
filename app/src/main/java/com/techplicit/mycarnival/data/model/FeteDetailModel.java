package com.techplicit.mycarnival.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by FuGenX-50 on 20-01-2017.
 */

public class FeteDetailModel implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("venue")
    @Expose
    private String venue;
    @SerializedName("startDate")
    @Expose
    private String startDate;
    @SerializedName("endDate")
    @Expose
    private String endDate;
    @SerializedName("startTime")
    @Expose
    private String startTime;
    @SerializedName("endTime")
    @Expose
    private String endTime;
    @SerializedName("primaryContact")
    @Expose
    private String primaryContact;
    @SerializedName("secondaryContact")
    @Expose
    private String secondaryContact;
    @SerializedName("ticketTypes")
    @Expose
    private TicketTypes ticketTypes;
    @SerializedName("activeFlag")
    @Expose
    private Boolean activeFlag;

    protected FeteDetailModel(Parcel in) {
        name = in.readString();
        image = in.readString();
        venue = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        primaryContact = in.readString();
        secondaryContact = in.readString();
        ticketTypes = in.readParcelable(TicketTypes.class.getClassLoader());
    }

    public static final Creator<FeteDetailModel> CREATOR = new Creator<FeteDetailModel>() {
        @Override
        public FeteDetailModel createFromParcel(Parcel in) {
            return new FeteDetailModel(in);
        }

        @Override
        public FeteDetailModel[] newArray(int size) {
            return new FeteDetailModel[size];
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

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getPrimaryContact() {
        return primaryContact;
    }

    public void setPrimaryContact(String primaryContact) {
        this.primaryContact = primaryContact;
    }

    public String getSecondaryContact() {
        return secondaryContact;
    }

    public void setSecondaryContact(String secondaryContact) {
        this.secondaryContact = secondaryContact;
    }

    public TicketTypes getTicketTypes() {
        return ticketTypes;
    }

    public void setTicketTypes(TicketTypes ticketTypes) {
        this.ticketTypes = ticketTypes;
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
        dest.writeString(venue);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(primaryContact);
        dest.writeString(secondaryContact);
        dest.writeParcelable(ticketTypes, flags);
    }
}
