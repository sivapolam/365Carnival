package com.techplicit.mycarnival.utils;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by pnaganjane001 on 20/12/15.
 */
public class BandsDateFormatsConverter {

    public static String printDateDifferenceInUIWithDefinedFormat(
            long durationFromPreviousAccess) {
        String formattedDuration = new String();

        long years = TimeUnit.MILLISECONDS.toDays(durationFromPreviousAccess) / 365;
        long weeks = TimeUnit.MILLISECONDS.toDays(durationFromPreviousAccess) / 7;
        long days = TimeUnit.MILLISECONDS.toDays(durationFromPreviousAccess);
        long hours = TimeUnit.MILLISECONDS.toHours(durationFromPreviousAccess);
        long mins = TimeUnit.MILLISECONDS.toMinutes(durationFromPreviousAccess);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationFromPreviousAccess);

        long months = getNumberOfMonths(durationFromPreviousAccess);

        long absoluteSeconds = durationFromPreviousAccess / 1000 % 60;
        long absoluteMinutes = durationFromPreviousAccess / (60 * 1000) % 60;

        if (years != 0) {
            formattedDuration += years + " year(s)";
        } else if (weeks != 0) {
            if(weeks > 4)
                formattedDuration += months + " month(s)";
            else
                formattedDuration += weeks + " week(s)";
        } else if (days != 0) {
            formattedDuration += days + " day(s)";
        } else if(hours != 0) {
            formattedDuration = hours +" hour(s)";

//            formattedDuration += format(hours) + ":" + format(absoluteMinutes) + ":" + format(absoluteSeconds);

        } else if (mins != 0) {
//            formattedDuration =  "1 minute";
            formattedDuration += /*"00:" + */format(absoluteMinutes)/* + ":" + format(absoluteSeconds)*/ +" min(s)";
        } /*else if (seconds != 0) {
//            formattedDuration =  "1 minute";
            formattedDuration += "00:00:" + format(absoluteSeconds);
        } */else {
            formattedDuration =  "00 min(s)";
//            formattedDuration += " while";
        }

        formattedDuration += " ago";
        System.out.println("Accessed " + formattedDuration);

        return formattedDuration;
    }

    private static String format(long value) {
        String result;
        if(value < 10)
            result = "0" + value;
        else
            result = "" + value;
        return result;
    }

    private static long getNumberOfMonths(long duration) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(duration);
        int months = c.get(Calendar.MONTH);
        return months;
    }

}
