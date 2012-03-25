package org.synote.player.client;

public class Utils
{
	public static enum Browser
	{
		//Rabbit: Actually, Google Chrome and Safari share the same render engine called AppleWebKit
		IE, FIREFOX, GoogleChrome, Safari, UNKNOWN
	}

	public static enum Platform
	{
		WINDOWS, MAC, UNKNOWN
	}

	private static final String WMPFORMATREGEX = ".+\\.((avi)|(mpg)|(mpeg)|(wav)|(wmv)|(mp3)|(asf)|(asx)|(wax)|(wm)|(wvx)|(m1v)|(mp2)|(mpa)|(mpe)|(mid)|(vmi)|(ivf)|(au)|(snd)|(vob)|(wma))";
	private static final String QTFORMATREGEXCOMPENSATE = ".+\\.((mov)|(mp4))";

	public static native String getLoggedInUserId()
	/*-{
		return $wnd.getLoggedInUserId();
	}-*/;

	public static Browser getBrowser()
	{
		String userAgent = getUserAgent().toLowerCase();

		if (userAgent.indexOf("firefox") != -1)
			return Browser.FIREFOX;
		else if (userAgent.indexOf("msie") != -1)
			return Browser.IE;
		else if (userAgent.indexOf("chrome") != -1)
			return Browser.GoogleChrome;
		else if (userAgent.indexOf("safari") != -1)
			return Browser.Safari;
		else
			return Browser.UNKNOWN;
	}

	public static Platform getPlatform()
	{
		String platform = getPlatformString().toLowerCase();
		//Logger.debug(platform);
		if(platform.indexOf("win") != -1)
		{
			return Platform.WINDOWS;
		}
		else if(platform.indexOf("mac") != -1)
		{
			return Platform.MAC;
		}
		else
		{
			return Platform.UNKNOWN;
		}
	}

	public static boolean canPlayByWMP(String url)
	{
		//Logger.debug("url:"+url);
		return url.toLowerCase().matches(WMPFORMATREGEX);
	}

	public static boolean canPlayByQT(String url) //With flip4mac plugin
	{
		if(!canPlayByWMP(url))
			return url.toLowerCase().matches(QTFORMATREGEXCOMPENSATE);
		else
			return true;
	}

	public static native String getLoggedInUserName()
	/*-{
		return $wnd.getLoggedInUserName();
	}-*/;

	public static native void redirectLocation(String url)
	/*-{
		$wnd.redirectLocation(url);
	}-*/;

	public static native void openNewWindow(String url, String windowName)
	/*-{
		$wnd.openNewWindow(url,windowName);
	}-*/;

	public static native String getMultimediaURL()
	/*-{
		return $wnd.getMultimediaURL();
	}-*/;

	public static native String getMultimediaId()
	/*-{
		return $wnd.getMultimediaId();
	}-*/;

	public static native String getMultimediaOwerId()
	/*-{
		return $wnd.getMultimediaOwnerId();
	}-*/;

	public static native String generateRedirectUrl(String url)
	/*-{
		return $wnd.generateRedirectUrl(url);
	}-*/;

	public static native boolean isGuest()
	/*-{
		return $wnd.isGuest();
	}-*/;

	public static native String getStartPosition()
	/*-{
		return $wnd.getStartPosition();
	}-*/;

	private static native String getUserAgent()
	/*-{
		return navigator.userAgent;
	}-*/;

	private static native String getPlatformString()
	/*-{
		return navigator.platform;
	}-*/;
}
