package at.fhs.smartsigncapture.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

// TODO: Auto-generated Javadoc

/**
 * The Class DateUtils.
 *
 * @author Martin Tiefengrabner
 * @date Feb 6, 2014
 */
public class DateUtils {

    public static final String JSON_ZERO_DATE = "0000-00-00 00:00:00";
    public static final SimpleDateFormat TITLE_DATE_FORMAT = new SimpleDateFormat("EEEE, d. MMMM yyyy");
    public static final SimpleDateFormat ACTION_BAR_DATE_FORMAT = new SimpleDateFormat("EEEE, d. MMMM yyyy");
    private static final SimpleDateFormat JSON_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * daysBetweenDates.
     *
     * @param fromDate the fromDate
     * @param toDate   the toDate
     * @return the int
     */
    public static int daysBetweenDates(Date fromDate, Date toDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fromDate);
        clearTime(cal);

        Date d1 = cal.getTime();

        cal.setTime(toDate);
        clearTime(cal);

        Date d2 = cal.getTime();

        long difference = d2.getTime() - d1.getTime();

        int days = (int) (difference / (1000 * 3600 * 24));

        if (days >= 0) {
            days++;
        }

        return days;
    }

    /**
     * Sets the time of a date to zero;
     *
     * @param cal Calendar the time should be removed from
     */
    public static void clearTime(Calendar cal) {
        cal.clear(Calendar.HOUR_OF_DAY);
        cal.clear(Calendar.HOUR);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
    }

    public static Date parseJSONDateString(String dateString) {
        Date result = null;

        if (!dateString.equalsIgnoreCase(JSON_ZERO_DATE)) {
            try {
                result = JSON_DATE_FORMAT.parse(dateString);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return result;
            }
        }
        return result;
    }

    public static String dateToJsonDateString(Date date) {
        return JSON_DATE_FORMAT.format(date);
    }

    public static Date stripTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    public static Date beginOfDay(Date day) {
        return stripTime(day);
    }

    public static Date endOfDay(Date day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(day);

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);

        return cal.getTime();
    }

    public static Date combineDateAndTime(Date date, Date time) {
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);

        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(time);

        dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
        dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
        dateCal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));
        dateCal.set(Calendar.MILLISECOND, timeCal.get(Calendar.MILLISECOND));

        return dateCal.getTime();
    }

    public static String formatElapsedTime(long elapsedSeconds) {
        int h = (int) (elapsedSeconds / 3600);
        int m = (int) ((elapsedSeconds / 60) % 60);

        return String.format("%02d:%02d", h, m);
    }

    /**
     * Format a date / time such that if the then is on the same day as now, it shows
     * just the time and if it's a different day, it shows just the date.
     * <p/>
     * <p>The parameters dateFormat and timeFormat should each be one of
     * {@link java.text.DateFormat#DEFAULT},
     * {@link java.text.DateFormat#FULL},
     * {@link java.text.DateFormat#LONG},
     * {@link java.text.DateFormat#MEDIUM}
     * or
     * {@link java.text.DateFormat#SHORT}
     *
     * @param then      the date to format
     * @param now       the base time
     * @param dateStyle how to format the date portion.
     * @param timeStyle how to format the time portion.
     */
    public static final CharSequence formatSameDayTime(long then, long now, int dateStyle, int timeStyle) {

        CharSequence result = "";

        Calendar thenCal = new GregorianCalendar();
        thenCal.setTimeInMillis(then);
        Date thenDate = thenCal.getTime();
        Calendar nowCal = new GregorianCalendar();
        nowCal.setTimeInMillis(now);

        java.text.DateFormat f;

        if (thenCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR)
                && thenCal.get(Calendar.MONTH) == nowCal.get(Calendar.MONTH)
                && thenCal.get(Calendar.DAY_OF_MONTH) == nowCal.get(Calendar.DAY_OF_MONTH)) {

            f = new SimpleDateFormat("HH:mm");
            result = f.format(thenDate);

        } else if (thenCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR)
                && thenCal.get(Calendar.MONTH) == nowCal.get(Calendar.MONTH)
                && thenCal.get(Calendar.WEEK_OF_YEAR) == nowCal.get(Calendar.WEEK_OF_YEAR)) {

            f = new SimpleDateFormat("EEE, HH:mm");
            String tmp = f.format(thenDate);
            int idx = tmp.lastIndexOf(".");
            result = tmp.substring(0, idx) + tmp.substring(idx + 1);


        } else if (thenCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR)) {
            f = new SimpleDateFormat("d. MMM, HH:mm ");
            String tmp = f.format(thenDate);
            int idx = tmp.lastIndexOf(".");
            result = tmp.substring(0, idx) + tmp.substring(idx + 1);


        } else {
            f = new SimpleDateFormat("d. MMM y, HH:mm");
            result = f.format(thenDate);
        }
        return result;
    }
}
