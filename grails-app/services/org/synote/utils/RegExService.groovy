package org.synote.utils

import java.util.regex.Matcher
import java.util.regex.Pattern

class RegExService {
	
	//Regular expression service
	boolean transactional = true
	
	//userid+guid regular expression
	private static Pattern userGuidRegEx = ~/userid=(\{){0,1}[0-9a-fA-F]{8}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{12}(\}){0,1}/
	private static Pattern iBMTransJobUrlRegEx = ~ /\b(https?|ftp|file|mms):\/\/[-A-Za-z0-9+&@#\/%?=~_|!:,.;]*\.((wav)|(wmv)|(avi)|(mp3)|(mov))\z/
	//(A|aV|vI|i)(M|mO|oV|v)(M|mP|p3)]/
	private static Pattern urlRegEx = ~ /\b(https?|ftp|file|mms):\/\/[-A-Za-z0-9+&@#\/%?=~_|!:,.;]*[-A-Za-z0-9+&@#\/%=~_|]/
	private static Pattern ipAddrRegEx = ~/\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\b/
	private static Pattern positionIntegerRegEx = ~/[0-9]+/
	private static Pattern unacceptedSearchTermRegEx = ~/^(\W*)|(.*\*)$/
	//University of Southampton and localhost
	private static Pattern allowedIPv4AddressRegEx = ~/\b((152)\.(78)\.\d{1,3}\.\d{1,3})|(127\.0\.0\.1)\b/
	private static Pattern allowedIPv6AddressRegEx = null 
	//private static Pattern youtubeURLRegEx = ~ /(youtu\.be\/|youtube\.com\/(watch\?(.*&)?v=|(embed|v)\/))([^\?&"'>]+)/
	
	
	def isUserGuid(String guidStr)
	{
		
	}
	
	def isPositionInteger(String integerStr)
	{
		return positionIntegerRegEx.matcher(integerStr).matches()
	}
	
	def isAllowedIPv4Address(String ipAddress)
	{
		return allowedIPv4AddressRegEx.matcher(ipAddress).matches()
	}
	
	//We don't consider ipv6 address at the moment, so this method will always
	//return true
	def isAllowedIPv6AddressRegEx(String ipAddress)
	{
		return true
	}
	
	def getPositionIntegerRegEx()
	{
		return positionintegerRegEx
	}
	
	def getUserGuidRegEx()
	{
		return userGuidRegEx
	}
	
	def isIBMTransJobUrl(String url)
	{
		return iBMTransJobUrlRegEx.matcher(url.toLowerCase()).matches()
	}
	
	def getIBMTransJobUrlRegEx()
	{
		return iBMTransJobUrlfRegEx
	}
	
	def isUrl(String url)
	{
		return urlRegEx.matcher(url.toLowerCase()).matches()
	}
	def getUrlREgEx()
	{
		return urlRegEx
	}
	
	def trimXml(String xml)
	{
		return xml.trim().replaceFirst("^([\\W]+)<","<");
	}
	
	def isUnacceptedSearchTerm(String query)
	{
		return unacceptedSearchTermRegEx.matcher(query).matches()
	}
	
	def getVideoIDfromYouTubeURL(String url)
	{
		def vid = null
		
		Pattern p = Pattern.compile("http.*\\?v=([a-zA-Z0-9_\\-]+)(?:&.)*")
			
		Matcher m = p.matcher(url)
		 
		if (m.matches())
		{
			vid = m.group(1)
		}
		
		return vid
	}
}
