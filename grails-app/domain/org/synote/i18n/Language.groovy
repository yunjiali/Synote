package org.synote.i18n

class Language {
	
	/*
	 * A Java locale used to represent a political, geographical, or cultural region that has a distinct language and customs for formatting:
		en_US = United States
		fr_FR = France
		de_DE = Germany
		it_IT = Italy
		es_ES = Spain
		pt_BR = Brazil
		zh_CN = China
		zh_TW = Taiwan
		ko_KR = Korea
		ja_JP = Japan
	 */
	String localeName
	/*
	 * Language component of the locale:
		en = English (US)
		fr = French
		de = German
		it = Italian
		es = Spanish
		pt = Brazilian Portuguese
		zh = Simplified Chinese
		zh = Traditional Chinese
		ko = Korean
		ja = Japanese
		
		To choose another language component, refer to the existing ISO codes.
	 */
	String language
	
    static constraints = {
		localeName(nullable:false, blank:false,unique:true)
		language(nullable:false, blank:false,unique:true)
	}
}
