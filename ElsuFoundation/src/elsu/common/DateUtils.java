package elsu.common;

import java.util.*;
import java.text.*;

/**
 *
 * @author SSDhaliwal
 */
public class DateUtils {

    static public java.util.Date getDate() {
        return new java.util.Date(Calendar.getInstance().getTimeInMillis());
    }

    static public java.sql.Timestamp getTimeStamp() {
        return new java.sql.Timestamp(DateUtils.getDate().getTime());
    }

    // routines to convert regular Date to different formats
    // to convert a date from String, you have to parse it
    // to convert a date from Date, you have to format it
    static public String convertDate2String(java.util.Date pDate) {
        return convertDate2String(pDate, "MM/dd/yyyy");
    }

    static public String convertDate2String(java.util.Date pDate, String pFormat) {
        SimpleDateFormat sdf2;

        // set defaults for data format conversions
        // if empty, set to default format
        if (StringUtils.IsNull(pFormat)) {
            sdf2 = new SimpleDateFormat("MM/dd/yyyy");
        } else {
            sdf2 = new SimpleDateFormat(pFormat);
        }

        // try to format the date
        try {
            return sdf2.format(pDate);
        } catch (Exception exi){
            return null;
        }
    }

    static public String convertDate2String(String pDate, String pFmtFrom,
            String pFmtTo) {
        SimpleDateFormat sdf2;
        SimpleDateFormat sdf3;

        // set defaults for data format conversions
        // if empty, set to default format
        if (StringUtils.IsNull(pFmtFrom)) {
            sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            sdf2 = new SimpleDateFormat(pFmtFrom);
        }

        // if empty, set to default format
        if (StringUtils.IsNull(pFmtTo)) {
            sdf3 = new SimpleDateFormat("MM/dd/yyyy");
        } else {
            sdf3 = new SimpleDateFormat(pFmtTo);
        }

        // try to format the date
        try {
            return sdf3.format(sdf2.parse(pDate));
        } catch (Exception exi){
            return null;
        }
    }

    // pre-coded to remove duplication of code
    static public String convertDate2SQLDateString(java.util.Date pDate) {
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");
        String nd = sdf2.format(pDate);

        return convertDate2String(nd, "MM/dd/yyyy", "yyyy-MM-dd");
    }

    static public String convertDate2SQLDateString(String pDate) {
        return convertDate2String(pDate, "MM/dd/yyyy", "yyyy-MM-dd");
    }

    // pre-coded to remove duplicate in code
    static public java.util.Date convertString2Date(String pDate) {
        return convertString2Date(pDate, "MM/dd/yyyy");
    }

    static public java.util.Date convertString2Date(String pDate, String pFormat) {
        SimpleDateFormat sdf2;

        // set defaults for data format conversions
        // if empty, set to default format
        if (StringUtils.IsNull(pFormat)) {
            sdf2 = new SimpleDateFormat("MM/dd/yyyy");
        } else {
            sdf2 = new SimpleDateFormat(pFormat);
        }

        // try to format the date
        try {
            return sdf2.parse(pDate);
        } catch (Exception exi){
            return null;
        }
    }

    // pre-coded to remove duplication of code
    static public java.sql.Date convertDate2SQLDate(java.util.Date pDate) {
        return convertDate2SQLDate(pDate, null);
    }

    static public java.sql.Date convertDate2SQLDate(java.util.Date pDate,
            String pFormat) {
        SimpleDateFormat sdf2;
        String nd;

        // set defaults for data format conversions
        // if empty, set to default format
        if (StringUtils.IsNull(pFormat)) {
            sdf2 = new SimpleDateFormat("MM/dd/yyyy");
        } else {
            sdf2 = new SimpleDateFormat(pFormat);
        }

        // do the initial conversion
        nd = sdf2.format(pDate);

        // try to format the date by parsing first, and then converting the
        // timestamp
        // to sql date format
        try {
            return java.sql.Date.valueOf(sdf2.format(sdf2.parse(nd)));
        } catch (Exception exi){
            return null;
        }
    }

    static public java.sql.Time convertDate2SQLTime(String pTime) {
        return convertDate2SQLTime(pTime, null);
    }

    static public java.sql.Time convertDate2SQLTime(String pTime, String pFormat) {
        SimpleDateFormat sdf2;
        SimpleDateFormat sdf;

        // if empty, set to default format
        if (StringUtils.IsNull(pFormat)) {
            sdf2 = new SimpleDateFormat("HH:mm:ss");
        } else {
            sdf2 = new SimpleDateFormat(pFormat);
        }
        sdf = new SimpleDateFormat("HH:mm:ss");

        // try to format the date by parsing first, and then converting the
        // timestamp
        // to sql date format
        try {
            return java.sql.Time.valueOf(sdf.format(sdf2.parse(pTime)));
        } catch (Exception exi){
            return null;
        }
    }

    static public java.sql.Date convertDate2SQLDate(String pDate) {
        return convertDate2SQLDate(pDate, null);
    }

    static public java.sql.Date convertDate2SQLDate(String pDate, String pFormat) {
        SimpleDateFormat sdf2;
        SimpleDateFormat sdf;

        // if empty, set to default format
        if (StringUtils.IsNull(pFormat)) {
            sdf2 = new SimpleDateFormat("MM/dd/yyyy");
        } else {
            sdf2 = new SimpleDateFormat(pFormat);
        }
        sdf = new SimpleDateFormat("yyyy-MM-dd");

        // try to format the date by parsing first, and then converting the
        // timestamp
        // to sql date format
        try {
            return java.sql.Date.valueOf(sdf.format(sdf2.parse(pDate)));
        } catch (Exception exi){
            return null;
        }
    }

    static public java.sql.Timestamp convertDate2SQLTimestamp(String pDate) {
        return convertDate2SQLTimestamp(pDate, null);
    }

    static public java.sql.Timestamp convertDate2SQLTimestamp(String pDate,
            String pFormat) {
        SimpleDateFormat sdf2;
        SimpleDateFormat sdf;

        // if empty, set to default format
        if (StringUtils.IsNull(pFormat)) {
            sdf2 = new SimpleDateFormat("MM/dd/yyyy H:m:s");
        } else {
            sdf2 = new SimpleDateFormat(pFormat);
        }
        sdf = new SimpleDateFormat("yyyy-MM-dd H:m:s");

        // try to format the date by parsing first, and then converting the
        // timestamp
        // to sql date format
        try {
            return java.sql.Timestamp.valueOf(sdf.format(sdf2.parse(pDate)));
        } catch (Exception exi){
            return null;
        }
    }
    
    // 20170114 - added method to convert time in milliseonds to java date
    static public java.util.Date convertMilliseconds2Date(long pMilliSeconds) {
    	Calendar local = Calendar.getInstance();
    	local.setTimeInMillis(pMilliSeconds);
    	
    	return local.getTime();
    }
}
