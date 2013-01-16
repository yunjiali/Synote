/*
 * A javascript DailyMotion response parser to get what we want
 */
function DailyMotionParser()
{
	//Do nothing
}

/*
 * params:
 * data: json data response from DailyMotion API
 * callback (thumbnail_url,errorMsg)
 */
DailyMotionParser.prototype.getThumbnail = function(data,callback)
{
	if(data == null)
	{
		return callback(null, "Response data is empty.");
	}
	
	if(data.thumbnail_medium_url === undefined)
		return callback(null,"Cannot get the default thumbnail picture from DailyMotion.");
		
	return callback(data.thumbnail_medium_url,null);
}

/*
 * params:
 * data: json data response from DailyMotion API
 * callback (duration in milli-seconds,errorMsg)
 */
DailyMotionParser.prototype.getDuration = function(data,callback)
{
	if(data == null)
	{
		return callback(null, "Response data is empty.");
	}
	
	if(data.duration === undefined)
		return callback(null,"Cannot get the duration of the resource.");
	var duration = parseInt(data.duration);
	return callback(duration*1000,null);
}

/*
 * params:
 * data: json data response from DailyMotion API
 * callback (title,errorMsg)
 */
DailyMotionParser.prototype.getTitle = function(data,callback)
{
	if(data == null)
	{
		return callback(null, "Response data is empty.");
	}
	
	if(data.title === undefined)
		return callback(null,"Cannot get the title of the resource.");
	var title = data.title;
	return callback(title,null);
}

/*
 * params:
 * data: json data response from DailyMotion API
 * callback (description,errorMsg)
 */
DailyMotionParser.prototype.getDescription = function(data,callback)
{
	if(data == null)
	{
		return callback(null, "Response data is empty.");
	}
	
	if(data.description === undefined)
		return callback(null,"Cannot get the description of the resource");
	var description = data.description;
	return callback(description,null);
}

/*
 * Get keywords, separated by comma
 * params:
 * data: json data response from DailyMotion API
 * callback (keywords,errorMsg)
 */
DailyMotionParser.prototype.getKeywords = function(data,callback)
{
	if(data == null)
	{
		return callback(null, "Response data is empty.");
	}
	
	if(data.tags === undefined)
		return callback(null,"Cannot get the keywords of the resource");
	var keywords = data.tags;
	return callback(keywords,null);
}