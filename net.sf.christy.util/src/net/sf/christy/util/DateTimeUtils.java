/**
 * 
 */
package net.sf.christy.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author noah
 * 
 */
public class DateTimeUtils
{
	public static SimpleDateFormat UTC_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");

	public static SimpleDateFormat NEW_UTC_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	static
	{
		UTC_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT+0"));
		NEW_UTC_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	public static String utcFormat(long datetime)
	{
		return UTC_FORMAT.format(new Date(datetime));
	}
	
	public static String newutcFormat(long datetime)
	{
		return NEW_UTC_FORMAT.format(new Date(datetime));
	}
	
	public static Date utcParse(String strDate) throws ParseException
	{
		return UTC_FORMAT.parse(strDate);
	}
	
	public static Date newutcParse(String strDate) throws ParseException
	{
		return NEW_UTC_FORMAT.parse(strDate);
	}
}
