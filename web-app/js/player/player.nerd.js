//querying how many enetities for this recording in each NERD main category
//SELECT ?type (COUNT(distinct ?entity) as ?eCount)
//WHERE
//{
//  ?anno rdf:type oac:Annotation .
//   ?anno oac:hasTarget ?frag .
//   <http://linkeddata.synote.org/synote/resources/37175> ma:hasFragment ?frag.
//   ?anno oac:hasBody ?entity .
//   ?entity rdf:type ?type.
//}
//Group by ?type

//Query all named entities related to this recording
//SELECT ?entity ?label ?beginIndex ?endIndex ?type
//WHERE
//{
//   ?anno rdf:type oac:Annotation .
//   ?anno oac:hasTarget ?frag .
//   <http://linkeddata.synote.org/synote/resources/37175> ma:hasFragment ?frag.
//   ?anno oac:hasBody ?entity .
//   ?entity rdfs:label ?label.
//   ?anno str:beginIndex ?beginIndex .
//   ?anno str:endIndex ?endIndex .
//   ?entity rdf:type ?type
//}

function NerdClient(sparqlEndpoint,prefixString)
{
	this.sparqlEndpoint = sparqlEndpoint;
	this.queryPrefixString = prefixString;
	this.queryEntitesCountByCategory = this.queryPrefixString +
		" SELECT ?type (COUNT(distinct ?entity) as ?eCount)"+
		" WHERE"+
		" {"+
		"  ?anno rdf:type oac:Annotation ."+
		"   ?anno oac:hasTarget ?frag ."+
		"   <"+resourceBaseURI+recording.id+"> ma:hasFragment ?frag."+
		"   ?anno oac:hasBody ?entity ."+
		"   ?entity rdf:type ?type."+
		" }"+
		" Group by ?type";
		
	this.queryListNamedEntities = this.queryPrefixString +
		" SELECT ?entity ?label ?beginIndex ?endIndex ?type"+
		" WHERE"+
		" {"+
		"   ?anno rdf:type oac:Annotation ."+
		"   ?anno oac:hasTarget ?frag ."+
		"   <"+resourceBaseURI+recording.id+"> ma:hasFragment ?frag."+
		"   ?anno oac:hasBody ?entity ."+
		"   ?entity rdfs:label ?label."+
		"   ?anno str:beginIndex ?beginIndex ."+
		"   ?anno str:endIndex ?endIndex ."+
		"   ?entity rdf:type ?type"+
		" }"+
		" order by ?beginIndex";	    
}

//get entites count by category
NerdClient.prototype.getEntitesCountByCategory = function()
{
	$.ajax({
		   type: "GET",
		   url: this.sparqlEndpoint+"?query="+encodeURIComponent(this.queryEntitesCountByCategory),
		   timeout:60000, // the call will be queued on the server-side, so we need to set it for a longer time
		   dataType: "json",
		   //Yunjia: Add a beforeSend function to display the loading message
		   success:function(data,textStatus, jqXHR)
		   {
		   		var htmlStr = "<div class='well'>";
		   		var types = nerdClient.listNerdTypes();
		   		$.each(types,function(i,t){
		   			var count = 0;
		   			htmlStr+="<div class='nerd-line'>"
		   			for(var k=0;k<data.results.bindings.length;k++)
		   			{
						if(data.results.bindings[k].type!= null && data.results.bindings[k].type.value == t)
						{
							count = data.results.bindings[k].eCount.value; 
							break;
						}
		   			}
		   			
					var nerdType = t.split("#")[t.split("#").length-1]
					var highlightClass = nerdClient.getHighlightCSS(nerdType.toLowerCase());
					htmlStr += "<span class='nerd-label "+highlightClass+"'>"+nerdType+"</span>";
					htmlStr += "<span class='badge badge-info'>"+count+" entities <span>";
						
					htmlStr += "</div>";
		   		});
		   		//htmlStr+="<div class='nerd-line'><span class='nerd-label nerd-multiple'>Multiple Types</span>"
				htmlStr+="</div>";
				$("#nerd_category_list_div").html(htmlStr);
		   },
		   error:function(jqXHR,textStatus,errorThrown)
		   {
				$("#nerd_div_msg").text("error!");
				$("#nerd_div_msg").show();
		   },
		   complete:function(jqXHR,textStatus)
		   {
			   //Do nothing
		   }
	});		
}

//get all entities related to this recording
NerdClient.prototype.getNamedEntities = function()
{
	$.ajax({
		   type: "GET",
		   url: this.sparqlEndpoint+"?query="+encodeURIComponent(this.queryListNamedEntities),
		   timeout:60000, // the call will be queued on the server-side, so we need to set it for a longer time
		   dataType: "json",
		   //Yunjia: Add a beforeSend function to display the loading message
		   success:function(data,textStatus, jqXHR)
		   {
		   		if(transcript.transcriptsData != null && data.results.bindings.length >0)
		   		{
		   			/*
		   			var d = transcript.transcriptsData;
		   			var transcriptsData = $(d).sort(sortCueByStartTime); //defined in player.transcript.js
		   			var charCount = 0;
		   			var tIndex = 0;
		   			
		   			$.each(transcriptsData, function(i,t){
		   				var blockStartChar = 0;
		   				charCount+= t.cueText.length;
		   				var entityList = [];
		   				for(var k=0;k<data.results.bindings.length;k++)
		   				{
		   					var beginIndex = data.results.bindings[k].beginIndex.value;
		   					var endIndex  = data.results.bindings[k].endIndex.value;
		   					if(beginIndex >= blockStartChar && endIndex <=charCount)
		   					{
		   						var result = data.results.bindings[k];
		   						entityList.push(result);
		   					}
		   					
		   					if(beginIndex > charCount)
		   						break;
		   					else if(beginIndex < charCount)
		   						continue;
		   				}
		   				
		   				console.log("entityListlength:"+entityList.length);
		   				if(entityList.length >0)
		   				{
		   					var oldText = t.cueText;
		   					var newText = t.cueText;
		   					for(var j=entityList.length-1;j>=0;j--)
		   					{
		   						var result = entityList[j];
		   						//console.log("blockStartChar:"+blockStartChar);
		   						var offset = result.beginIndex.value - blockStartChar;
		   						//console.log("offset:"+offset);
		   						var startStr = oldText.substring(0,offset);
		   						//console.log("startStr:"+startStr);
		   						var nerdType = result.type.value.split("#")[result.type.value.split("#").length-1];
		   						var highlightClass = nerdClient.getHighlightCSS(nerdType.toLowerCase());
		   						
		   						var endStr = newText.substring(offset).replace(result.label.value,
		   							"<span class='nerd-label-small "+highlightClass+"'>"+result.label.value+"</span>");
		   						newText = startStr + endStr;
		   					}
		   					var transcript_content = $(".transcript_line[transcript_id='"+t.index+"'] div.transcript_line_content");
		   					transcript_content.html(newText);
		   				}
		   				
		   			});*/
		   			
		   			var d = transcript.transcriptsData;
		   			var transcriptsData = $(d).sort(sortCueByStartTime); //defined in player.transcript.js
		   			var charCount = transcriptsData[0].cueText.length;
		   			var tIndex = 0;
		   			
		   			var newText="";
		   			$.each(data.results.bindings, function(i,result){
		   				while(result.beginIndex.value > charCount)
		   				{
		   					newText = "";
		   					tIndex++;
		   					charCount += transcriptsData[tIndex].cueText.length+1;
		   				}
		   				
		   				if(newText == "")
		   				{
		   					newText = transcriptsData[tIndex].cueText;
		   				}
		   				//var blockStartChar = (charCount - transcriptsData[tIndex].cueText.length);
		   				
		   				var nerdType = result.type.value.split("#")[result.type.value.split("#").length-1];
		   				var highlightClass = nerdClient.getHighlightCSS(nerdType.toLowerCase());
		   				
		   				//console.log("blockstartchar:"+blockStartChar);
		   				//var offset = result.endIndex.value - blockStartChar;
		   				newText = newText.replace(result.label.value,
		   						"<span class='nerd-label-small "+highlightClass+"'>"+result.label.value+"</span>");
		   				console.log("entity:"+result.label.value);
		   				console.log("newText:"+newText);
		   				//console.log("beginIndex:"+result.beginIndex.value+"#"+result.endIndex.value);
		   				
		   				//var endStr = transcriptsData[tIndex].cueText.substring(offset);
		   				//newText = startStr+endStr;
		   				var transcript_content = $(".transcript_line[transcript_id='"+transcriptsData[tIndex].index+"'] div.transcript_line_content");
			   			transcript_content.html(newText);
		   			});
		   		}
		   },
		   error:function(jqXHR,textStatus,errorThrown)
		   {
				$("#nerd_div_msg").text("error!");
				$("#nerd_div_msg").show();
		   },
		   complete:function(jqXHR,textStatus)
		   {
			   
		   }
	});		
}

NerdClient.prototype.getHighlightCSS = function(type)
{
	switch(type)
	{
		case "thing":
	  		return "nerd-thing";
		case "person":
	  		return "nerd-person";
  		case "organisation":
  			return "nerd-organisation";
  		case "location":
  			return "nerd-location";
  		case "function":
  			return "nerd-function";
  		case "time":
  			return "nerd-time";
  		case "amount":
  			return "nerd-amount";
		default:
	  		return "";
	}
}

NerdClient.prototype.listNerdTypes = function()
{
	return ["http://nerd.eurecom.fr/ontology#Thing",
			"http://nerd.eurecom.fr/ontology#Person",
			"http://nerd.eurecom.fr/ontology#Organisation",
			"http://nerd.eurecom.fr/ontology#Location",
			"http://nerd.eurecom.fr/ontology#Function",
			"http://nerd.eurecom.fr/ontology#Time",
			"http://nerd.eurecom.fr/ontology#Amount"]
}
