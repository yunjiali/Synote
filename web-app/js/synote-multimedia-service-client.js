/*
 * params:
 * url: the base url of the service, for example http://lslvm-yl2.ecs.soton.ac.uk:80
 */
function SynoteMultimediaServiceClient(url)
{
	this.url = url;
	this.generateThumbnailURL = url+"api/generateThumbnail";
	this.getMetadataURL = url +"api/getMetadata";
	this.getDurationURL = url +"api/getDuration";
	this.isVideoURL = url+"api/isVideo";
	this.getSubtitleListURL = url+"api/getSubtitleList";
	this.getSubtitleSRTURL = url+"api/getSubtitleSRT";
	this.nerdifySRTURL = url+"api/nerdifySRT";
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
			    var resp =$.parseJSON(jqXHR.responseText);
			    if(resp!=null)
			   		callback(null, resp.message);
			    else
			    	callback(null,jqXHR.textStatus);
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
			   return callback(null, resp.message);
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
			   return callback(null, resp.message);
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

/*
  get the available subtitles
* params:
* videourl: the url of the video
* callback (data,errorMsg)
 */
SynoteMultimediaServiceClient.prototype.getSubtitleList = function(videourl,callback)
{
	$.ajax({
		   type: "GET",
		   url: this.getSubtitleListURL,
		   data: {videourl:encodeURIComponent(videourl)}, 
		   timeout:60000, 
		   dataType: "json",
		   success:function(data,textStatus, jqXHR)
		   {
				callback(data, null);
				return;
		   },
		   error:function(jqXHR,textStatus,errorThrown)
		   {
			   var resp =$.parseJSON(jqXHR.responseText);
			   return callback(null, resp.message);
		   }
	});		
}

/*
  get the subtitles in synote json format
* params:
* subtitle: the url of the subtitle
* fmt: json or synote
* callback (data,errorMsg)
 */
SynoteMultimediaServiceClient.prototype.getSubtitleSRT = function(subtitleurl,fmt, callback)
{
	$.ajax({
		   type: "GET",
		   url: this.getSubtitleListSRTURL,
		   data: {subtitleurl:encodeURIComponent(subtitleurl),fmt:fmt}, 
		   timeout:60000, 
		   dataType: "json",
		   success:function(data,textStatus, jqXHR)
		   {
				callback(data, null);
				return;
		   },
		   error:function(jqXHR,textStatus,errorThrown)
		   {
			   var resp =$.parseJSON(jqXHR.responseText);
			   return callback(null, resp.message);
		   }
	});		
}

/*
  nerdify the susbtitle and get the ttl format of the annotations
* params:
* subtitle: the url of the subtitle
* fmt: json or synote
* callback (data,errorMsg)
*
* if fmt="ttl", you also need to provide:
* nm: the base namespace for the text, media and annotations used in the RDF model
* videourl: the url of the video
 */
SynoteMultimediaServiceClient.prototype.nerdifySRT = function(subtitleurl, fmt, nm, videourl, callback)
{
	var opts = {subtitleurl:subtitleurl,fmt:fmt};
	var dataType = "json"
	if(fmt === "ttl")
	{
		opts.nm = nm;
		opts.videourl = videourl;
	}
	$.ajax({
		   type: "GET",
		   url: this.nerdifySRTURL,
		   data: opts, 
		   timeout:60000, 
		   success:function(data,textStatus, jqXHR)
		   {
				callback(data, null);
				return;
		   },
		   error:function(jqXHR,textStatus,errorThrown)
		   {
			   var resp =$.parseJSON(jqXHR.responseText);
			   return callback(null, resp.message);
		   }
	});		
}