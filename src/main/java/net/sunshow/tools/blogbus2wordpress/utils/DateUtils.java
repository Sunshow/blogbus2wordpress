package net.sunshow.tools.blogbus2wordpress.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
	private static final Logger logger = LoggerFactory.getLogger(DateUtils.class.getName());
	
	public static final String DATETIME = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE = "yyyy-MM-dd";
		
	public static String formatDate(Date date) {
		return formatDate(date, DATE);
	}
	
	public static String formatDateTime(Date date) {
		return formatDate(date, DATETIME);
	}

	public static String formatDate(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        return DateFormatUtils.format(date, pattern, Locale.CHINA);
	}
	
	public static String formatDate(String dateStr, String srcPattern, String desPattern) {
		Date date = parseDate(dateStr, srcPattern);
		if (date == null) {
			return null;
		}
		return formatDate(date, desPattern);
	}

	public static Date parseDate(String dateStr) {
		return parseDate(dateStr, DATE);
	}
	public static Date parseLongDate(String dateStr) {
		return parseDate(dateStr, new String[] {
                DATETIME,
                "yyyy-MM-dd HH:mm:ss.SSS",
        });
	}

	public static Date parseDate(String dateStr, String pattern) {
		return parseDate(StringUtils.trim(dateStr), new String[]{pattern});
	}

    public static Date parseDate(String dateStr, String[] patterns) {
        if (dateStr == null) {
            return null;
        }
        try {
            return org.apache.commons.lang.time.DateUtils.parseDateStrictly(dateStr, patterns);
        } catch (ParseException e) {
            logger.error("日期转换错误, dateStr={}, pattern={}", dateStr, StringUtils.join(patterns, ","));
            logger.error(e.getMessage(), e);
            return null;
        }
    }

	public static boolean test(String dateStr, String pattern) {
		return test(dateStr, new String[]{pattern});
	}

    public static boolean test(String dateStr, String[] patterns) {
        if (dateStr == null) {
            return false;
        }
        try {
            org.apache.commons.lang.time.DateUtils.parseDateStrictly(dateStr, patterns);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
	
	/**
	 * 两个时间相隔天数 time1-time2
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static long diffDays(Date time1, Date time2){
        if (time1 == null || time2 == null) {
            return 0;
        }
        return (time1.getTime() - time2.getTime()) / 1000 / 60 / 60 / 24;
	}

    // >= start && < end
    private static boolean springFestivalCheck = false;
    public static Date springFestivalStartDate = parseDate("2014-01-30");
    public static Date springFestivalEndDate = parseDate("2014-02-06");

    public static boolean isDuringSpringFestival() {
        return isDuringSpringFestival(new Date());
    }

    public static boolean isDuringSpringFestival(Date date) {
        if (!springFestivalCheck) {
            return false;
        }
        if (date == null) {
            return isDuringSpringFestival();
        }
        if (date.before(springFestivalStartDate) || date.after(springFestivalEndDate)) {
            return false;
        }

        return true;
    }

    // >= start && < end
    private static boolean worldCupCheck = true;
    public static Date worldCupStartDate = parseDate("2014-06-13");
    public static Date worldCupEndDate = parseDate("2014-07-15");

    public static boolean isDuringWorldCup() {
        return isDuringWorldCup(new Date());
    }

    public static boolean isDuringWorldCup(Date date) {
        if (!worldCupCheck) {
            return false;
        }
        if (date == null) {
            return isDuringWorldCup();
        }
        if (date.before(worldCupStartDate) || date.after(worldCupEndDate)) {
            return false;
        }

        return true;
    }
}
