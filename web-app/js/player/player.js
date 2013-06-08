var transcript,
	synmark,
	presentation;

var SynotePlayer = function(recording, user)
{
	this.recording = recording;
	this.user = user;
	this.options = {
		mfURI:recording.url, 
		autoStart:true, 
		height: 320, 
		width:480,
		success:function(mediaElement,domObject,p){
			mediaElement.addEventListener('timeupdate', function(e){
	            var currentPosition = mediaElement.currentTime;
	            
	            if(synmark.synchronised == true)
					synmark.sync(currentPosition);
				if(transcript.synchronised == true)
					transcript.sync(currentPosition);
				if(presentation.synchronised == true)
					presentation.sync(currentPosition);
	        });
		}
	};
}

SynotePlayer.prototype.init=function(player_div, callback)
{
	var player = player_div.smfplayer(this.options);
	if(player === undefined)
	{
		return callback("The video/audio resource cannot be played in Synote Player.",null);
	}
	transcript = new Transcript(recording,$("transcripts_div"),$("#transcripts_content_div"));
	transcript.initTranscript();
	synmark = new Synmark(recording,$("#synmarks_div"),$("#synmark_list_div"));
	synmark.initSynmark();
	presentation = new Presentation(recording,$("#slides_div"),$("#image_container_div"));
	presentation.initPresentation();
	synmark.refresh();
	transcript.refresh();
	presentation.refresh();
	return callback(null, player);
}