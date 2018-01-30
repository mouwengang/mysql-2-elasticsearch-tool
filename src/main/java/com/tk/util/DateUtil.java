package com.tk.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * <p>
 *   日期工具类
 * </p>
 * @author  : longfei04@taikanglife.com
 * @version : 2016年3月23日 下午3:45:19
 * 
 */
public class DateUtil {
	
	public static String toHourMinuteSecond(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }
	
	public static String toYearMonthDay(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }
	
	public static Date parseDate(String exp, String strDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(exp);
        Date date = null;
        try {
            date = simpleDateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}
