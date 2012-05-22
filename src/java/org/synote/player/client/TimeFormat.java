package org.synote.player.client;

public class TimeFormat
{
	private static TimeFormat instance;

	private TimeFormat()
	{
		// Singleton.
	}

	public int parse(String string)
	{
		if (string == null || string.length() == 0)
			return 0;

		// implement regexp validation

		String seconds = null;
		int index = string.lastIndexOf(":");
		if (index != -1)
		{
			seconds = string.substring(index + 1);
			string = string.substring(0, index);
		}
		else
		{
			seconds = string;
			string = null;
		}

		String minutes = null;
		index = (string != null) ? string.lastIndexOf(":") : -1;
		if (index != -1)
		{
			minutes = string.substring(index + 1);
			string = string.substring(0, index);
		}
		else
		{
			minutes = string;
			string = null;
		}

		String hours = string;

		int time = 0;
		if (seconds != null && seconds.length() != 0)
			time += Integer.parseInt(seconds);

		if (minutes != null && minutes.length() != 0)
			time += Integer.parseInt(minutes) * 60;

		if (hours != null && hours.length() != 0)
			time += Integer.parseInt(hours) * 3600;

		return time * 1000;
	}

	private String format(int number)
	{
		return (number < 10) ? "0" + Integer.toString(number) : Integer.toString(number);
	}

	//Format hh:mm:ss
	public String toString(int time)
	{
		time = time / 1000;
		int hours = time / 3600;
		time -= hours * 3600;
		int minutes = time / 60;
		time -= minutes * 60;
		int seconds = time;

		if (hours == 0 && minutes == 0)
			return "00:00:" + format(seconds);
		else if (hours == 0)
			return "00:"+format(minutes) + ":" + format(seconds);
		else
			return format(hours) + ":" + format(minutes) + ":" + format(seconds);
	}
	
	//This is SRT format
	public String toSRTTimeString(int time)
	{
		int miliseconds = time % 1000;
		String milisecStr = String.valueOf(miliseconds);
		if(miliseconds < 10)
			milisecStr ="00"+milisecStr;
		else if(miliseconds < 100)
			milisecStr="0"+milisecStr;
		//Yunjia: is it valid to have 00:30,0 instead of 00:30,000? if not, we need to add something to miliseconds
		return toString(time)+","+milisecStr;
	}

	public String toSRTTimeString(Integer time)
	{
		return (time != null) ? toSRTTimeString(time.intValue()) : null;
	}
	
	//This is WebVTT format [hh...:]mm:ss.msmsms --> [hh...:]mm:ss.msmsms
	public String toWebVTTTimeString(int time)
	{
		int miliseconds = time % 1000;
		String milisecStr = String.valueOf(miliseconds);
		if(miliseconds < 10)
			milisecStr ="00"+milisecStr;
		else if(miliseconds < 100)
			milisecStr="0"+milisecStr;
		//Yunjia: is it valid to have 00:30,0 instead of 00:30,000? if not, we need to add something to miliseconds
		return toString(time)+"."+milisecStr;
	}
	/*
	 * given a WebVTT time format string, return the milliseconds
	 */
	public int getWebVTTFormatTime(String timeStr)
	{
		   //SRT time format: hh:mm:ss.mmm
		   int time = -1;
		   String[] hhmmssmmm = timeStr.trim().split("\\.",2);
		   //println "timestr:"+timeStr
		   //println "size:"+hhmmssmmm.length
		   String mmm = "0";
		   if (hhmmssmmm.length > 2)
			   return -1;
		   
		   if(hhmmssmmm.length ==2)
		   {
			   mmm = hhmmssmmm[1];
		   }
		   
		   String[] hhmmss = hhmmssmmm[0].split(":");
		   if(hhmmss.length == 3) //we have all hh mm and ss
		   {
				   
				   try
				   {
					   //println hhmmss[0]+":"+hhmmss[1]+":"+hhmmss[2]+","+mmm
					   time = 0;
					   time = time + Integer.parseInt(hhmmss[0].trim())*3600;
					   time = time + Integer.parseInt(hhmmss[1].trim())*60;
					   time = time + Integer.parseInt(hhmmss[2].trim());
					   time = time * 1000 + Integer.parseInt(mmm.trim());
					   return time;
				   }
				   catch(NumberFormatException nfe)
				   {
					   //Logger.debug("1 return -1");
					   return -1;
				   }
		   }
		   else if(hhmmss.length==2) //we only have mm and ss
		   {
			   try
			   {
				   //println hhmmss[0]+":"+hhmmss[1]+":"+hhmmss[2]+","+mmm
				   time = 0;
				   time = time + Integer.parseInt(hhmmss[0].trim())*60;
				   time = time + Integer.parseInt(hhmmss[1].trim());
				   time = time * 1000 + Integer.parseInt(mmm.trim());
				   return time;
			   }
			   catch(NumberFormatException nfe)
			   {
				   //Logger.debug("1 return -1");
				   return -1;
			   }   
		   }
		   else if(hhmmss.length ==1)
		   {
			   try
			   {
				   //println hhmmss[0]+":"+hhmmss[1]+":"+hhmmss[2]+","+mmm
				   time = 0;
				   time = time + Integer.parseInt(hhmmss[0].trim());
				   time = time * 1000 + Integer.parseInt(mmm.trim());
				   return time;
			   }
			   catch(NumberFormatException nfe)
			   {
				   //Logger.debug("1 return -1");
				   return -1;
			   }  
		   }
		   else
		   {
				   //Logger.debug("2 return -1");
				   return -1;
		   }
	   }
	
	public int getSRTFormatTime(String timeStr)
	   {
		   //SRT time format: hh:mm:ss,mmm
		   int time = -1;
		   String[] hhmmssmmm = timeStr.trim().split(",");
		   //println "timestr:"+timeStr
		   //println "size:"+hhmmssmmm.length
		   if(hhmmssmmm.length ==2)
		   {
			   String[] hhmmss = hhmmssmmm[0].split(":");
			   if(hhmmss.length == 3)
			   {
				   String mmm = hhmmssmmm[1];
				   try
				   {
					   //println hhmmss[0]+":"+hhmmss[1]+":"+hhmmss[2]+","+mmm
					   time = 0;
					   time = time + Integer.parseInt(hhmmss[0].trim())*3600;
					   time = time + Integer.parseInt(hhmmss[1].trim())*60;
					   time = time + Integer.parseInt(hhmmss[2].trim());
					   time = time * 1000 + Integer.parseInt(mmm.trim());
					   return time;
				   }
				   catch(NumberFormatException nfe)
				   {
					   //Logger.debug("1 return -1");
					   return -1;
				   }
			   }
			   else
			   {
				   //Logger.debug("2 return -1");
				   return -1;
			   }

		   }
		   else
		   {
			   //Logger.debug("3 return -1");
			   return -1;
		   }
	   }

	public String toWebVTTTimeString(Integer time)
	{
		return (time != null) ? toWebVTTTimeString(time.intValue()) : null;
	}
	
	public String toWebVTTTimeString(String time)
	{
		return (time != null && time.length()>0) ? toWebVTTTimeString(Integer.parseInt(time)) : null;
	}

	public String toSRTTimeString(String time)
	{
		return (time != null && time.length() > 0) ? toSRTTimeString(Integer.parseInt(time)) : null;
	}
	
	public String toString(Integer time)
	{
		return (time != null) ? toString(time.intValue()) : null;
	}
	
	public String toString(Long time)
	{
		return (time != null) ? toString(time.intValue()) : null;
	}

	public String toString(String time)
	{
		return (time != null && time.length() > 0) ? toString(Integer.parseInt(time)) : null;
	}

	public static TimeFormat getInstance()
	{
		if (instance == null)
			instance = new TimeFormat();

		return instance;
	}
}
