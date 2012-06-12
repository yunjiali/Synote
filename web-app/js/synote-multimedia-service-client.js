/*
 * params:
 * url: the base url of the service, for example http://lslvm-yl2.ecs.soton.ac.uk:8000
 */
function SynoteMultimediaServiceClient(url)
{
	this.url = url;
	this.generateThumbnailURL = url+"api/generateThumbnail";
	this.getMetadataURL = url +"api/getMetadata";
	this.getDurationURL = url +"api/getDuration";
	this.isVideoURL = url+"api/isVideo";
}

/*
 * Connect to synote-multimedia-service and get metadata for a video or audio resource
 * params:
 * videourl: the url of the video
 * callback (data,errorMsg): the callback function when the response is successfully obtained
 */
SynoteMultimediaServiceClient.prototype.getMetadata = function(videourl,callback)
{
	$.ajax({
		   type: "GET",
		   url: this.getMetadataURL,
		   data: {videourl:encodeURIComponent(videourl)}, 
		   timeout:60000, 
		   dataType: "json",
		   success:function(data,textStatus, jqXHR)
		   {
				if(data != null)
				{
					callback(data, null);
					return;
				}
				else
				{
					callback(null, data.message);
					return;
				}
		   },
		   error:function(jqXHR,textStatus,errorThrown)
		   {
			   //console.log(jqXHR);
			   
			   var resp =$.parseJSON(jqXHR.responseText);
			   callback(null, resp.message);
			   return;
		   }
	});		
}

/*
 * Connect to synote-multimedia-service and generate thumbnail picture at a specific time
 * params:
 * videourl: the url of the video
 * id: uuid of the recording
 * start: the start time
 * end: the end time
 * callback (thumbnail_url,errorMsg): the callback function when the response is successfully obtained
 */
SynoteMultimediaServiceClient.prototype.generateThumbnail = function(videourl, id, start, end, callback)
{
	var opts = {videourl:encodeURIComponent(videourl), id:id};
	if(start != null)
	{
		opts.start = start;
	}
	if(end != null)
	{
		opts.end =end;
	}
	$.ajax({
		   type: "GET",
		   url: this.generateThumbnailURL,
		   data: opts, 
		   timeout:60000, 
		   dataType: "json",
		   success:function(data,textStatus, jqXHR)
		   {
				if(data.thumbnail_url != null)
				{
					callback(data.thumbnail_url, null);
					return;
				}
				else
				{
					callback(null, data.message);
					return;
				}
					
		   },
		   error:function(jqXHR,textStatus,errorThrown)
		   {
			   var resp =$.parseJSON(jqXHR.responseText);
			   callback(null, resp.message);
			   return;
		   }
	});	
}

/*
 * Get the duration of a recording
 * params:
 * videourl: the url of the video
 * callback: callback method
 * 
 * response:
 * duration in milliseconds if successful, if not an error message will be returned.
 */
SynoteMultimediaServiceClient.prototype.getDuration = function(videourl, callback)
{
	$.ajax({
		   type: "GET",
		   url: this.getDurationURL,
		   data: {videourl:encodeURIComponent(videourl)}, 
		   timeout:60000, 
		   dataType: "json",
		   success:function(data,textStatus, jqXHR)
		   {
				if(data.duration != null)
				{
					callback(data.duration, null);
					return;
				}
				else
				{
					callback(null, data.message);
					return;
				}
		   },
		   error:function(jqXHR,textStatus,errorThrown)
		   {
			   console.log(jqXHR);
			   
			   var resp =$.parseJSON(jqXHR.responseText);
			   callback(null, resp.message);
			   return;
		   }
	});		
}

SynoteMultimediaServiceClient.prototype.isVideo = function(videourl,callback)
{
	$.ajax({
		   type: "GET",
		   url: this.isVideoURL,
		   data: {videourl:encodeURIComponent(videourl)}, 
		   timeout:60000, 
		   dataType: "json",
		   success:function(data,textStatus, jqXHR)
		   {
				if(data.isVideo != null)
				{
					callback(data.isVideo, null);
					return;
				}
				else
				{
					callback(null, data.message);
					return;
				}
		   },
		   error:function(jqXHR,textStatus,errorThrown)
		   {
			   var resp =$.parseJSON(jqXHR.responseText);
			   callback(null, resp.message);
			   return;
		   }
	});		
}

/*Use these information to decide if it is a video*/

SynoteMultimediaServiceClient.prototype.flash_audio_list = new Array("mp3","aac","m4a","ogg","wav");
SynoteMultimediaServiceClient.prototype.flash_video_list = new Array("mp4","mov","f4v","flv","3gp","3g2","ogv","webm");
SynoteMultimediaServiceClient.prototype.flash_youtube_list = new Array("www.youtube.com","youtube.be","youtu.be");
SynoteMultimediaServiceClient.prototype.flash_protocol_list = new Array("rtmp");

//For silverlight player
SynoteMultimediaServiceClient.prototype.sl_audio_list=new Array("wma","mp3");
SynoteMultimediaServiceClient.prototype.sl_video_list=new Array("wmv");
SynoteMultimediaServiceClient.prototype.sl_protocol_list = new Array("mms","rtsp","rstpt");

//For windows media player
SynoteMultimediaServiceClient.prototype.wmp_audio_list = new Array("wma","mp3","wav","mid","midi");
SynoteMultimediaServiceClient.prototype.wmp_video_list = new Array("avi","wmv","mpg","mpeg","m1v","mp2","mpa");
SynoteMultimediaServiceClient.prototype.wmp_protocol_list = new Array("mms","rtsp","rstpt");

//For all accepted common things
SynoteMultimediaServiceClient.prototype.all_protocol_list = new Array("http","https");

SynoteMultimediaServiceClient.prototype.playerType = {"flash":0,"silverlight":1,"wmp":2,"html5native":3,"Unknown":99};
SynoteMultimediaServiceClient.prototype.browserType = {"ie":0,"firefox":10,"safari":20,"googlechrome":30,"opera":40,"unknown":99};
SynoteMultimediaServiceClient.prototype.platformType = {"windows":0,"linux":10,"mac":20,"unknown":99};