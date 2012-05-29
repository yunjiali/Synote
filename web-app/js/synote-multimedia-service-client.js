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
	this.getIsVideoURL = url+"api/isVideo";
}

/*
 * Connect to synote-multimedia-service and generate thumbnail picture at a specific time
 * params:
 * url: the url of the video
 * start: the start time
 * end: the end time
 * callback: the callback function when the response is successfully obtained
 */
SynoteMultimediaServiceClient.prototype.generateThumbnail = function(videourl, id, start, end, callback)
{
	
}

SynoteMultimediaServiceClient.prototype.getDuration = function(videourl, callback)
{
	console.log("video url"+videourl);
	$.ajax({
		   type: "GET",
		   url: this.getDurationURL,
		   data: {videourl:videourl}, 
		   timeout:60000, 
		   dataType: "json",
		   success:function(data,textStatus, jqXHR)
		   {
				if(data.duration != null)
					callback(data.duration, null);
				else
				{
					callback(null, data.message);
				}
		   },
		   error:function(jqXHR,textStatus,errorThrown)
		   {
			   var resp =$.parseJSON(jqXHR.responseText);
			   callback(null, resp.message);
		   }
	});		
}