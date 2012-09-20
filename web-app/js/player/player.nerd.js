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
		" SELECT ?type (COUNT(?entity) as ?eCount)"+
		" WHERE"+
		" {"+
		"  ?anno rdf:type oac:Annotation ."+
		"   ?anno oac:hasTarget ?frag ."+
		"   <"+resourceBaseURI+recording.id+"> ma:hasFragment ?frag."+
		"   ?anno oac:hasBody ?entity ."+
		"   ?entity rdf:type ?type."+
		"   ?type rdfs:label 'nerdType'."+
		" }"+
		" Group by ?type";
	this.queryReviewOptional = user.id !== -1?"OPTIONAL{"+
							"?anno review:hasReview ?rev."+
							"?rev review:reviewer <"+userBaseURI+user.id+">;"+
							"review:rating ?rating.}":""; //if not logged, don't display the review data
							
	this.queryListNamedEntities = this.queryPrefixString +
		" SELECT Distinct ?idex ?entity ?label ?beginIndex ?endIndex ?type ?start ?end ?rating "+
		" WHERE"+
		" {"+
		"   ?anno rdf:type oac:Annotation ."+
		"   ?anno dc:identifier ?idex ."+
		"   ?anno oac:hasTarget ?frag ."+
		"   <"+resourceBaseURI+recording.id+"> ma:hasFragment ?frag."+
		"   ?frag nsa:temporalStart ?start."+
		"   ?frag nsa:temporalEnd ?end."+
		"   ?anno oac:hasBody ?entity ."+
		//"   ?entity rdfs:label ?label."+
		"   ?string str:sourceString ?label."+
		"   ?anno opmv:wasDerivedFrom ?string."+
		"   ?string str:beginIndex ?beginIndex ."+
		"   ?string str:endIndex ?endIndex ."+
		"   ?entity rdf:type ?type ."+
		"   ?type rdfs:label 'nerdType' ."+
		this.queryReviewOptional+
		" }"+
		" order by ?beginIndex";
	
	this.defaultDBpediaSparqlEndpoint = "http://dbpedia.org/sparql";
	this.defaultDBpediaGraphURI = "http://dbpedia.org";
	this.defaultSparqlResultsFormat ="application/sparql-results+json";
	
	this.queryDBpediaPerson = "";
	this.queryDBpediaLocation = "";
	this.queryDBpediaOrganisation ="";	    
}

NerdClient.prototype.getDBpediaQueryString = function(nerdType,uri)
{
	switch(nerdType)
	{
		case "thing":
	  		return this.getQueryDBpediaThing(uri);
		case "person":
	  		return this.getQueryDBpediaPerson(uri);
  		case "organization":
  			return this.getQueryDBpediaOrganisation(uri);
  		case "location":
  			return this.getQueryDBpediaLocation(uri);
		default:
	  		return this.getQueryDBpediaThing(uri);
	}
}

NerdClient.prototype.getQueryDBpediaThing = function(uri)
{
	return "select distinct ?label ?abstract ?depiction"+ 
		" where {"+
		" 	{"+
		"    <"+uri+"> rdfs:label ?label."+
		"    <"+uri+"> dbpedia-owl:abstract ?abstract."+
		"    <"+uri+"> foaf:depiction ?depiction."+
		"    FILTER (langMatches( lang(?label), 'en') && langMatches(lang(?abstract),'en'))"+
		" 	}"+
		" }";	
}

NerdClient.prototype.getQueryDBpediaPerson = function(uri)
{
	return "select distinct ?label ?abstract ?depiction ?birthdate ?profession"+ 
		" where {"+
		" 	{"+
		"    <"+uri+"> rdfs:label ?label."+
		"    <"+uri+"> dbpedia-owl:abstract ?abstract."+
		"    <"+uri+"> foaf:depiction ?depiction."+
		"    optional {<"+uri+"> dbpedia-owl:birthDate ?birthdate.}"+
		"    optional {<"+uri+"> dbpedia-owl:profession ?profession.}"+
		"    FILTER (langMatches( lang(?label), 'en') && langMatches(lang(?abstract),'en'))"+
		" 	}"+
		" }";	
}

NerdClient.prototype.getQueryDBpediaOrganisation = function(uri)
{
	return "select distinct ?label ?abstract ?formationYear ?numberOfStaff ?homepage"+ 
		" where {"+
		" 	{"+
		"    <"+uri+"> rdfs:label ?label."+
		"    <"+uri+"> dbpedia-owl:abstract ?abstract."+
		"    optional {<"+uri+"> dbpedia-owl:formationYear ?formationYear.}"+
		"    optional {<"+uri+"> dbpedia-owl:numberOfStaff ?numberOfStaff.}"+
		"    optional {<"+uri+"> foaf:homepage ?homepage.}"+
		"    FILTER (langMatches( lang(?label), 'en') && langMatches(lang(?abstract),'en'))"+
		" 	}"+
		" }";	
}

NerdClient.prototype.getQueryDBpediaLocation = function(uri)
{
	return "select distinct ?label ?abstract ?latitude ?longitude"+ 
		" where {"+
		" 	{"+
		"    <"+uri+"> rdfs:label ?label."+
		"    <"+uri+"> dbpedia-owl:abstract ?abstract."+
		"    optional {<"+uri+"> geo:lat ?latitude.}"+
		"    optional {<"+uri+"> geo:long ?longitude.}"+
		"    FILTER (langMatches( lang(?label), 'en') && langMatches(lang(?abstract),'en'))"+
		" 	}"+
		" }";	
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
		   		var types = nerdClient.listNerdTypes();
		   		$.each(types,function(i,t){
		   			var count = 0;
		   			for(var k=0;k<data.results.bindings.length;k++)
		   			{
						if(data.results.bindings[k].type!= null && data.results.bindings[k].type.value == t)
						{
							count = data.results.bindings[k].eCount.value; 
							break;
						}
		   			}
					var nerdType = t.split("#")[t.split("#").length-1];
					var nerd_div = $("#nerd_"+nerdType.toLowerCase()+"_count_div");
					var highlightClass = nerdClient.getHighlightCSS(nerdType.toLowerCase());
					var type_span = $("<span/>",{
						text: nerdType+ " ("+count+" entities)"
					}).addClass("nerd-label "+highlightClass);
					type_span.appendTo(nerd_div);
					
					//initialise the table
					var nerd_table = $("#nerd_"+nerdType.toLowerCase()+"_table");
					var row_count = parseInt(count / 2);
					if(count%2!==0)
					{
						row_count++;
					}
					
					for(var r=0;r<=row_count;r++)
					{
						var row_tr = $("<tr/>").appendTo(nerd_table);
						//Add an empty class, when a named entity is filled in this td, we will remove this class
						var first_td = $("<td/>").addClass("empty").appendTo(row_tr);
						var second_td = $("<td/>").addClass("empty").appendTo(row_tr);
					}
		   		});
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
		   			var d = transcript.transcriptsData;
		   			var transcriptsData = $(d).sort(sortCueByStartTime); //defined in player.transcript.js
		   			var charCount = transcriptsData[0].cueText.length;
		   			var tIndex = 0;
		   			
		   			var newText="";
		   			$.each(data.results.bindings, function(i,result){
		   				//highlight it in transcript
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
		   				//console.log("entity:"+result.label.value);
		   				//console.log("newText:"+newText);
		   				//console.log("beginIndex:"+result.beginIndex.value+"#"+result.endIndex.value);
		   				
		   				//var endStr = transcriptsData[tIndex].cueText.substring(offset);
		   				//newText = startStr+endStr;
		   				var transcript_content = $(".transcript_line[transcript_id='"+transcriptsData[tIndex].index+"'] div.transcript_line_content");
			   			transcript_content.html(newText);
			   			
			   			//list them in NERD widget
			   			var nerd_table = $("#nerd_"+nerdType.toLowerCase()+"_table");
			   			//var entity_div = $("<div/>").appendTo(nerd_div);
			   			
			   			var entity_span = $("<span/>").addClass("nerd-entity");
			   			entity_span.bind('click',{result:result},function(event){
			   				var start = parseInt(result.start.value*1000,10);
			   				var end = parseInt(result.end.value*1000,10);
			   				multimedia.setPosition(start);
			   			});
			   			var entity_a = $("<a/>",{text:result.label.value}).appendTo(entity_span);
			   			nerdClient.displayDisambiguation(entity_span,result.entity.value,result.label.value,nerdType);
				   		
				   		var ne_td = nerd_table.find(".empty:first").append(entity_span).removeClass("empty");
				   		//append the external link
				   		if(result.entity.type === 'uri')
				   		{
				   			var external_a = $("<a/>",{title:result.label.value});
				   			external_a.html("<i class='icon-link-small'></i>");
				   			external_a.attr("href",result.entity.value);
				   			external_a.attr("target","_blank");
				   			external_a.appendTo(ne_td);
				   		} 
				   		//Add review buttons
				   		if(user.id !== -1)
			   			{
				   			if(result.rating === undefined)
				   			{
				   				var entity_rev_div = $("<div/>",{id:"approve_"+result.idex.value}).addClass("btn-group btn-group-nerd").appendTo(ne_td);
				   				entity_rev_div.html(
				   					'<button class="btn btn-mini approve" title="approve" data-loading-text="Sending Review..."'+ 
				   					'id="btn_approve_'+result.idex.value+'"><i class="icon-thumbs-up"></i></button>'+
									'<button class="btn btn-mini approve" title="reject" id="btn_reject_'+result.idex.value+'"><i class="icon-thumbs-down"></i></button>'
				   				);
				   			}
				   			else if(result.rating.value === "0")
				   			{
				   				var entity_rev_div = $("<div/>",{id:"approve_"+result.idex.value}).addClass("btn-group btn-group-nerd").appendTo(ne_td);
				   				entity_rev_div.html(
				   					'<button class="btn btn-mini approve" title="approve" data-loading-text="Sending Review..."'+ 
				   					'id="btn_approve_'+result.idex.value+'"><i class="icon-thumbs-up"></i></button>'+
									'<button class="btn btn-mini approve btn-danger" disabled="disabled" title="reject" id="btn_reject_'+result.idex.value+'"><i class="icon-thumbs-down"></i></button>'
				   				);
				   			}
				   			else if(result.rating.value === "1")
				   			{
				   				var entity_rev_div = $("<div/>",{id:"approve_"+result.idex.value}).addClass("btn-group btn-group-nerd").appendTo(ne_td);
				   				entity_rev_div.html(
				   					'<button class="btn btn-mini btn-success approve" disabled="disabled" title="approve" data-loading-text="Sending Review..."'+ 
				   					'id="btn_approve_'+result.idex.value+'"><i class="icon-thumbs-up"></i></button>'+
									'<button class="btn btn-mini approve" title="reject" id="btn_reject_'+result.idex.value+'"><i class="icon-thumbs-down"></i></button>'
				   				);
				   			}
				   		}
		   			});
		   		}
		   		
		   		nerdClient.regReviewEvent();
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

/*
 * Display the Disambiguation information in a tooltip according to different NERD categories
 * tooltip_span: the span (or other element) you want to display the tooltip
 * url: the url of the named entity
 * nerdType: type of the nerd, such as person, organization, etc.
 */
NerdClient.prototype.displayDisambiguation = function(tooltip_span, url, title, nerdType)
{
	//check if within dbpedia domain
	if(url.indexOf("http://dbpedia.org/") ===0 || url.indexOf("http://www.dbpedia.org/") ===0)
	{
		var queryURL = this.defaultDBpediaSparqlEndpoint+"?default-graph-uri="+encodeURIComponent(this.defaultDBpediaGraphURI)
					+"&format="+encodeURIComponent(this.defaultSparqlResultsFormat)
					+"&query="+encodeURIComponent(this.getDBpediaQueryString(nerdType.toLowerCase(),url));
		$.ajax({
			url:queryURL,
			dataType:'jsonp',
			jsonp:'callback',
			beforeSend:function(jqXHR,settings)
			{
				tooltip_span.addClass("nerd-disambiguation nerd-disambiguation-loading");
			},
			success:function(data){
				//console.log("success");
				var htmlStr = "<dl>";
				$.each(data.results.bindings,function(i,result){
					for(var key in result)
					{
						htmlStr += "<dt>"+key+"</dt>";
						if(key === "depiction")
						{
							htmlStr += "<img src='"+result[key].value+"' alt='"+result.label.value+"' />";
						}
						else
							htmlStr += "<dd>"+(result[key].value.length > 128?(result[key].value.substring(0,127)+"..."):result[key].value)+"</dd>";
					}
				});
				htmlStr += "</dl>";
				tooltip_span.attr("data-content",htmlStr);
				tooltip_span.attr("data-original-title",title);
				tooltip_span.popover({html:true});
			},
			complete:function(jqXHR,textStatus)
			{
				tooltip_span.removeClass("nerd-disambiguation-loading");
			}
		});
	}
}

NerdClient.prototype.regReviewEvent = function()
{
	$(".approve").click(function(){
		var review_url = g.createLink({controller:'nerd',action:'saveReviewAjax'});
		var rating = 0;
		var idex = "";
		if($(this).attr("title") == "approve")
		{
			rating = 1;
			idex = $(this).attr("id").replace("btn_approve_",""); //get the extraction id
		}
		else
			idex = $(this).attr("id").replace("btn_reject_","");

		$.ajax({
			   type: "POST",
			   url: review_url,
			   data: {rating:rating,idex:idex}, //default language is English
			   timeout:60000, // the call will be queued on the server-side, so we need to set it for a longer time
			   dataType: "json",
			   //Yunjia: Add a beforeSend function to display the loading message
			   beforeSend:function(jqXHR, settings)
			   {
					//disable the button
					$("#btn_approve_"+idex).attr("disabled","disabled");
					$("#btn_reject_"+idex).attr("disabled","disabled");
			   },
			   success:function(data,textStatus, jqXHR)
			   {
				   	if(data.error !== undefined)
					{
						//Show error messages
						$("#nerd_div_msg").html("<span class='alert alert-error'>"+data.error.description+"</span>");
				   		$("#nerd_div_msg").show();
						$("#btn_approve_"+idex).removeAttr("disabled");
						$("#btn_reject_"+idex).removeAttr("disabled");
					}
				   	else
					{
						if(rating==0)
						{
							$("#btn_approve_"+idex).removeClass("btn-success").removeAttr("disabled");
				   			$("#btn_reject_"+idex).addClass("btn-danger").attr("disabled","disabled");
						}
						else
						{
							$("#btn_approve_"+idex).addClass("btn-success").attr("disabled","disabled");
							$("#btn_reject_"+idex).removeClass("btn-danger").removeAttr("disabled");
						}

						return false;
					}
			   },
			   error:function(jqXHR,textStatus,errorThrown)
			   {
				   	$("#nerd_div_msg").html("<span class='alert alert-error'>"+data.error.description+"</span>");
				   	$("#nerd_div_msg").show();
				   	$("#btn_approve_"+idex).removeAttr("disabled");
					$("#btn_reject_"+idex).removeAttr("disabled");
			   },
			   complete:function(jqXHR,textStatus)
			   {
					//Do nothing
			   }
		});	
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
  		case "organization":
  			return "nerd-organization";
  		case "location":
  			return "nerd-location";
  		case "function":
  			return "nerd-function";
  		case "product":
  			return "nerd-product";
  		case "event":
  			return "nerd-event";
  		case "time":
  			return "nerd-time";
  		case "amount":
  			return "nerd-amount";
		default:
	  		return "nerd-thing";
	}
}

NerdClient.prototype.listNerdTypes = function()
{
	return ["http://nerd.eurecom.fr/ontology#Thing",
			"http://nerd.eurecom.fr/ontology#Person",
			"http://nerd.eurecom.fr/ontology#Organization",
			"http://nerd.eurecom.fr/ontology#Product",
			"http://nerd.eurecom.fr/ontology#Event",
			"http://nerd.eurecom.fr/ontology#Location",
			"http://nerd.eurecom.fr/ontology#Function",
			"http://nerd.eurecom.fr/ontology#Time",
			"http://nerd.eurecom.fr/ontology#Amount"]
}
//To add a new nerd type:
//change listNerdTypes function add the URI of the new types
//change the getHighlightCSS function
//add new css class to nerd.css
//add div in subpreview.gsp