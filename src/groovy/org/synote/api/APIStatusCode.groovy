package org.synote.api

class APIStatusCode {
	/**
	* status code: To indicate the status of the return xml
	*/
   private final static int SUCCESS = 0
   private final static int AUTHENTICATION_FAILED = 1
   private final static int USER_API_KEY_INVALID = 2
   private final static int API_KEY_NOT_FOUND = 3 //when log out, we delete the user apikey, but if we cannot find it, we through out this error
   
   private final static int MMID_MISSING = 10
   private final static int MM_NOT_FOUND = 11
   private final static int MM_PERMISSION_DENIED = 12
   private final static int MM_CREATION_ERROR = 13
   
   private final static int SYNMARK_ID_MISSING=20
   private final static int SYNMARK_NOT_FOUND=21
   private final static int SYNMARK_UKNOWN_ERROR = 22
   private final static int SYNMARK_ST_MISSING=23
   private final static int SYNMARK_ST_INVALID=24
   private final static int SYNMARK_ET_INVALID=25
   private final static int SYNMARK_NEXT_INVALID=26
   private final static int SYNMARK_PERMISSION_DENIED = 27
   
   private final static int TRANSCRIPT_ID_MISSING = 40
   private final static int TRANSCRIPT_NOT_FOUND = 41
   private final static int TRANSCRIPT_PERMISSION_DENIED = 42
   private final static int TRANSCRIPT_WEBVTT_JSON_INVALID = 43 //the json objects for webvtt (WebVTTCueData) is not valid
   private final static int TRANSCRIPT_TRANSCRIPTS_MISSING = 44 //params.transcripts is missing
   private final static int TRANSCRIPT_WEBVTT_INVALID = 45 //The webvtt string is not valid
   private final static int TRANSCRIPT_DRAFT_NOT_FOUND = 46
   private final static int TRANSCRIPT_DRAFT_INVALID = 47 //The format of saved transcript draft is invalid
   private final static int TRANSCRIPT_SRT_JSON_INVALID = 48
   private final static int TRANSCRIPT_SRT_INVALID = 49
  
   private final static int PRESENTATION_ID_MISSING = 60
   private final static int PRESENTATION_NOT_FOUND = 61
   private final static int PRESENTATION_PERMISSION_DENIED = 62
   private final static int SLIDE_INDEX_MISSING = 63
   private final static int SLIDE_ID_MISSING = 64
   private final static int SLIDE_ST_MISSING = 65
   private final static int SLIDE_URL_MISSING = 66
   private final static int SLIDE_INDEX_INVALID = 67
   private final static int SLIDE_OLD_INDEX_MISSING = 68 //old slide index is missing
   private final static int SLIDE_ET_INVALID = 69 //end time is bigger than the start time
   private final static int SLIDE_UNKNOWN_ERROR =79
   
   
   private final static int RESOURCEANNOTATION_CREATEION_ERROR = 80 //cannot create annotation
   private final static int RESOURCEANNOTATION_NOT_FOUND = 81
   
   private final static int INTERNAL_ERROR = 99
   
   /**
    * Ussed to get the last exception in the session
    */
   private final static String API_AUTH_EXCEPTION = "API_AUTH_EXCEPTION"
}
