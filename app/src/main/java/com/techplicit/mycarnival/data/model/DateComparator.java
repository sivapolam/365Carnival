package com.techplicit.mycarnival.data.model;

import java.util.Date;

/**
 * Created by pnaganjane001 on 21/12/15.
 */
public class DateComparator implements Comparable<DateComparator> {

    private Date dateTime;

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date datetime) {
        this.dateTime = datetime;
    }

    @Override
    public int compareTo(DateComparator o) {
        if (getDateTime() == null || o.getDateTime() == null)
            return 0;
        return getDateTime().compareTo(o.getDateTime());
    }
}
