package in.amankumar110.chatapp.utils;

import android.os.Build;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateHelper {

    public static String getFormattedTime(Long time) {
        Date date = new Date(time);
        Calendar inputCal = Calendar.getInstance();
        inputCal.setTime(date);

        Calendar now = Calendar.getInstance();

        String formattedTime;
        if (isSameDay(inputCal, now)) {
            // Same day: Only show time
            formattedTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date);
        } else if (isSameYear(inputCal, now)) {
            // Same year: Show day and month + time
            formattedTime = new SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(date);
        } else {
            // Different year: Full date
            formattedTime = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(date);
        }

        Log.d("FormattedTime", formattedTime);
        return formattedTime;
    }

    private static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private static boolean isSameYear(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }
}
