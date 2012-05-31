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
 * Connect to synote-multimedia-service and generate thumbnail picture at a specific time
 * params:
 * videourl: the url of the video
 * id: uuid of the recording
 * start: the start time
 * end: the end time
 * callback: the callback function when the response is successfully obtained
 */
SynoteMultimediaServiceClient.prototype.generateThumbnail = function(videourl, id, start, end, callback)
{
	var opts = {videourl:videourl, id:id};
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
		   data: {videourl:videourl}, 
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
		   data: {videourl:videourl}, 
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