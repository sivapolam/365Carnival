package com.techplicit.mycarnival.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by FuGenX-50 on 20-01-2017.
 */

public class TicketTypes implements Parcelable{
    @SerializedName("Details")
    @Expose
    private String details;
    @SerializedName("Ticket")
    @Expose
    private String ticket;

    protected TicketTypes(Parcel in) {
        details = in.readString();
        ticket = in.readString();
    }

    public static final Creator<TicketTypes> CREATOR = new Creator<TicketTypes>() {
        @Override
        public TicketTypes createFromParcel(Parcel in) {
            return new TicketTypes(in);
        }

        @Override
        public TicketTypes[] newArray(int size) {
            return new TicketTypes[size];
        }
    };

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(details);
        dest.writeString(ticket);
    }
}
