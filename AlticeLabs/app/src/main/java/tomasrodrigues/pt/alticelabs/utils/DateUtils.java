package tomasrodrigues.pt.alticelabs.utils;

import com.google.common.base.Strings;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by tomas on 21/02/2018.
 */
public class DateUtils {

    static final String DATEFORMAT = "yyyyMMddHHmmss";
    public static final int SECONDS_PERIOD = 5;
    public static final int MILI_TO_SECOND = 1000;

    public static String convertDateToString(Date date) {
        if (date == null) {
            return "";
        }

        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm");
        return dateFormat.format(date);
    }

    public static long convertDateStringToLong(String date) {
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        try {
            return format.parse(date).getTime();
        } catch (Exception e) {
        }
        return -1;
    }

    public static String convertDateToStringArqos(Date date) {
        if (date == null) {
            return "";
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String format = formatter.format(date);
        return Strings.nullToEmpty(format);
    }

    public static String convertDateToHoursMinutes(Date date) {
        if (date == null) {
            return "";
        }

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String format = formatter.format(date);
        return Strings.nullToEmpty(format);
    }

    public static String convertDateToStringAlarms(Date date) {
        if (date == null) {
            return "";
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = formatter.format(date);
        return Strings.nullToEmpty(format);
    }

    public static String convertDateToStringCDRs(Date date) {
        if (date == null) {
            return "";
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
        String format = formatter.format(date);
        return Strings.nullToEmpty(format);
    }

    public static Date maxDate() {
        final Calendar cld = Calendar.getInstance();
        cld.set(Calendar.YEAR, 280000000);
        return cld.getTime();
    }

    public static Date GetUTCdatetimeAsDate()
    {
        //note: does not check for null
        return StringDateToDate(GetUTCdatetimeAsString());
    }

    public static String GetUTCdatetimeAsString()
    {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }

    public static Date StringDateToDate(String StrDate)
    {
        Date dateToReturn = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);

        try
        {
            dateToReturn = (Date) dateFormat.parse(StrDate);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return dateToReturn;
    }
}
