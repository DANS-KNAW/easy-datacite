package nl.knaw.dans.easy.web.template.dates;

import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.EasyWicketApplication;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class EasyDate
{
	public static String toDateString(DateTime date)
	{
		String format = EasyWicketApplication.getProperty(EasyResources.DATE_FORMAY_KEY);
		return DateTimeFormat.forPattern(format).print(date);
	}

	public static String toDateTimeString(DateTime date)
	{
		String format = EasyWicketApplication.getProperty(EasyResources.DATETIME_FORMAY_KEY);
		return DateTimeFormat.forPattern(format).print(date);
	}
}
