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
public class HTTPUtils {

    public static final String BASE_URL = "http://10.112.209.206:8080/api/parckman/";
    public static final String GET_ZONES = "zones/";
    public static final String GET_ZONES_STATS = "zones/stats/";
    public static final String GET_LUGARES_FROM_ZONE = "sites?zone=";
}
