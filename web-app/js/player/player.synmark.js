/*
 * Define synmark class
 */
var Synmark = Base.extend({
	recording:null,
	outer_container:null,
	inner_container:null,
	selectedSynmark:null, //is a jquery object, not a dom object
	synmarks:null,//a list of synmark divs, jquery object
	synmarksData:null, //a list of synmark json data
	synmarkTags:null, //an array of tags
	synchronised:true,
	autoScroll:true,
	scrollOffset:-100,
	mySynmarksOnly:false,
	myTagsOnly:false,
	
	constructor:function(recording, outer_container, inner_container)
	{
		this.recording = recording;
		this.outer_container=outer_container;
		this.inner_container=inner_container;
	},
	showMsg:function(msg,type) //display the message, could be error, or success
	{
		var msg_div = $("#synmark_msg_div");
		if(type == "error")
		{
			msg_div.html("<div class='alert alert-error'><button class='close' data-dismiss='alert'>x</button>"+msg+"</div>");
		}
		else
		{
			msg_div.html("<div class='alert alert-success'><button class='close' data-dismiss='alert'>x</button>"+msg+"</div>");
		}
	},
	initSynmark:function()
	{
		//Yunjia: don't need to do that if it's read only and we have to remove the form if it's readonly
		if(recording.canCreateSynmark === "true")
		{
			
			//$("#synmark_create_div").hide();
			$("#synmark_st").mask("?99:99:99");
			$("#synmark_et").mask("?99:99:99");
			
			$("#synmark_st_time").click(function(){
				var currentPosition = multimedia.getPosition();
				//console.log("curpo:"+currentPosition);
				$("#synmark_st").val(milisecToString(currentPosition));
			});
			$("#synmark_st_add").click(function(){
				var oldTime = stringToMilisec($("#synmark_st").val());
				var newTime = oldTime+1000;
				$("#synmark_st").val(milisecToString(newTime));
			});
			$("#synmark_st_remove").click(function(){
				var oldTime = stringToMilisec($("#synmark_st").val());
				var newTime = oldTime-1000>0?oldTime-1000:0;
				$("#synmark_st").val(milisecToString(newTime));
			});
			$("#synmark_et_time").click(function(){
				var currentPosition = multimedia.getPosition();
				$("#synmark_et").val(milisecToString(currentPosition));
			});
			$("#synmark_et_add").click(function(){
				var oldTime = stringToMilisec($("#synmark_et").val());
				var newTime = oldTime+1000;
				$("#synmark_et").val(milisecToString(newTime));
			});
			$("#synmark_et_remove").click(function(){
				var oldTime = stringToMilisec($("#synmark_et").val());
				var newTime = oldTime-1000>0?oldTime-1000:0;
				$("#synmark_et").val(milisecToString(newTime));
			});
			//#######Init autocomplete for synmark tags
			/* Will be implemented later using bootstrap typeahead
			 * $( "#synmark_tags" ).bind( "keydown", function( event ) {
				if ( event.keyCode === $.ui.keyCode.TAB &&
						$( this ).data( "autocomplete" ).menu.active ) {
					event.preventDefault();
				}
			}).autocomplete({
				minLength: 0,
				source: function( request, response ) {
					// delegate back to autocomplete, but extract the last term
					var extractedLast = synmark.splitTags(request.term).pop();
					response( $.ui.autocomplete.filter(
						synmark.synmarkTags, extractedLast) );
				},
				focus: function() {
					// prevent value inserted on focus
					return false;
				},
				select: function( event, ui ) {
					var terms = synmark.splitTags(this.value);
					// remove the current input
					terms.pop();
					// add the selected item
					terms.push( ui.item.value );
					// add placeholder to get the comma-and-space at the end
					terms.push( "" );
					this.value = terms.join( ", " );
					return false;
				}
			});*/
			
			//Init tinyMCE
			$('#synmark_note').tinymce({
				script_url:g.resource({dir:'js/tiny_mce',file:'tiny_mce.js'}),
				mode : "textareas",
				width:"100%",
				plugins:"xhtmlxtras",
				//plugins : "autolink,lists,pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template,advlist,rdfa",
				valid_elements : "*[*]",
				theme:"advanced",
				theme_advanced_buttons1 : "bold,italic,underline,forecolor,backcolor",//,|,namespace,about,property,rdfGraph,rdfEnrich,stat,setting",
				theme_advanced_buttons2 : "",
		        theme_advanced_buttons3 : "",
		        theme_advanced_toolbar_location : "top",
		        theme_advanced_toolbar_align : "left",
		        theme_advanced_statusbar_location : "bottom",
		        theme_advanced_resizing : true,
		        theme_advanced_path : false,
		        force_p_newlines : false,
		        force_br_newlines : true,
		        forced_root_block : ''
			});

			//Init the ajax submitting of forms
			$('#synmark_form').submit(function(){
				var synmark_st = stringToMilisec($("#synmark_st").val());
				var synmark_et = stringToMilisec($("#synmark_et").val());
				if($("#synmark_id").val() != null && $("#synmark_id").val() !== undefined && $.trim($("#synmark_id").val()).length>0) //if id is not null, it means synmark updates 
				{
					var url = g.createLink({controller:"recording",action:"updateSynmarkAjax"});//, params:{synmark_id:$.trim($("#synmark_id").val())}});
					var synmarkData = synmark.getSynmarkData($("#synmark_id").val());
					var oldMiddle = (synmarkData.start + synmarkData.end)/2;
					if(recording.thumbnail != 'null' && recording.isVideo == 'true' && (cue.thumbnail == null || (oldMiddle<newCue.start || oldMiddle > newCue.end))) //need to regenerate the thumbnail
					{
						mmServiceClient.generateThumbnail(recording.url,recording.uuid, synmark_st, synmark_et, function(thumbnail_url, error){
							//We are not going to print out any error message here
							if(error == null)
							{
								$("#synmark_thumbnail").val(thumbnail_url);
							}
							synmark.updateSynmarkAjax(url, function(msg,error){
								if(error == null)
								{
									synmark.showMsg(msg,null);
									$("#synmark_create_div").hide(400);
									synmark.refresh();
								}
								else
								{
									synmark.showMsg(msg,"error");
								}
							});
						});
					}
					else
					{
						synmark.updateSynmarkAjax(url, function(msg,error){
							if(error == null)
							{
								synmark.showMsg(msg,null);
								$("#synmark_create_div").hide(400);
								synmark.refresh();
							}
							else
							{
								synmark.showMsg(msg,"error");
							}
						});
					}
				}
				else
				{
					var url = g.createLink({controller:"recording",action:"saveSynmarkAjax",params:{multimedia_id:recording.id}});
					if(recording.isVideo == 'true' && recording.thumbnail != 'null')
					{
						mmServiceClient.generateThumbnail(recording.url,recording.uuid, synmark_st, synmark_et, function(thumbnail_url, error){
							//We are not going to print out any error message here
							if(error == null)
							{
								$("#synmark_thumbnail").val(thumbnail_url);
							}
							synmark.updateSynmarkAjax(url, function(msg,error){
								if(error == null)
								{
									synmark.showMsg(msg,null);
									$("#synmark_create_div").hide(400);
									synmark.refresh();
								}
								else
								{
									synmark.showMsg(msg,"error");
								}
							});
						});
					}
					else
					{
						synmark.updateSynmarkAjax(url, function(msg,error){
							if(error == null)
							{
								synmark.showMsg(msg,null);
								$("#synmark_create_div").hide(400);
								synmark.refresh();
							}
							else
							{
								synmark.showMsg(msg,"error");
							}
						});
					}
				}
				return false;
			});
			$("#synmark_cancel").click(function(){
				$("#synmark_create_div").hide(400);
				//console.log("note:"+$("#synmark_note").val());
			});
			
			$("#add_synmark_btn").click(function(){
				var newTime = multimedia.getPosition();
				synmark.fillSynmarkForm(milisecToString(newTime),"","","","","","");
				$("#synmark_create_div").show(400);
			});
			
			$("#synmark_form").validate(
			{
				rules: {
				    synmark_st: {
					    required:true
				    },
				    synmark_et:{
						required:true
					},
					synmark_title:{
						required:false,
						maxlength:255
					},
					synmark_tags:{
						required:false,
						maxlength:255 //mysql text field, max length 65535
					},
					synmark_note:{
						required:false,
						maxlength:65535
					}
				 },
				highlight: function(label) {
					$(label).closest('.control-group').addClass('error');
				},
			});
		}
	},
	updateSynmarkAjax:function(url,callback)
	{
		$("#synmark_form").ajaxSubmit({
			url:url,
			//resetForm:true,
			type:'post',
			dataType:'json',
			beforeSend:function(event)
			{
				//Do nothing
			},
			success:function(data,textStatus, jqXHR, $form) //
			{
				//console.log("status:"+status);
				if(data.success) //status == 200
				{
					return callback(data.success.description,null);
				}
				else if(data.error)
				{
					return callback(data.error.description,"error");
				}
				
			},
			error:function(jqXHR,textStatus,errorThrown)
			{
				var resp =$.parseJSON(jqXHR.responseText);
				return callback(resp.error.descrption,"error");
			}
		});
	},
	fillSynmarkForm:function(synmark_st, synmark_et, synmark_title,synmark_tags, synmark_note, synmark_id,synmark_thumbnail)
	{
		$("#synmark_st").val(synmark_st);
		$("#synmark_et").val(synmark_et);
		$("#synmark_title").val(synmark_title);
		$("#synmark_tags").val(synmark_tags);
		//$("#synmark_note").val(synmark_note);
		//CKEDITOR.instances.synmark_note.setData(synmark_note);
		$("#synmark_note").val(synmark_note);
		$("#synmark_thumbnail").val(synmark_thumbnail);
		$("#synmark_id").val(synmark_id);
	},
	splitTags:function(val)
	{
		return val.split(/,\s*/);
	},
	sync:function(currentPosition)
	{
		//currentSynmark is a jquery object
		if(this.synmarks != null && this.synmarks.length>0)
		{
			var currentSynmark = this.getSynmark(currentPosition)
			//console.log("selectedSynmark:"+this.selectedSynmark);
			if(currentSynmark != this.selectedSynmark)
			{
				this.setSynmarkSelected(currentSynmark);
			}
		}
	},
	getSynmark:function(currentPosition)
	{
		var cs = null;
		for(var i=0;i<this.synmarks.length;i++)
		{
			var st = parseInt($(this.synmarks[i]).attr("date-time-st"));
			if(st>=currentPosition)
			{
				//if(cs==null)
					//cs = $(this.synmarks[i]);
				
				break;
			}
			else
				cs = $(this.synmarks[i]);
		}
		//console.log("date-time-st:"+cs.attr("date-time-st"));
		return cs;
	},
	getSynmarkData:function(synmark_id)
	{
		var synmarkData = null;
		$.each(this.synmarksData,function(i,s){
			if(s.id == synmark_id)
			{
				synmarkData = s;
				return;
			}
		});
		return synmarkData;
	},
	setSynmarkSelected:function(currentSynmark)
	{
		if(currentSynmark != null)
		{
			if(this.selectedSynmark != null)
				this.selectedSynmark.removeClass("synmark_selected");
			this.selectedSynmark = currentSynmark;
			currentSynmark.addClass("synmark_selected");
			if(this.autoScroll == true)
				this.inner_container.scrollTo(currentSynmark, 400, {offset:this.scrollOffset});
		}
	},
	clickSynmark:function(currentSynmark)
	{
		this.setSynmarkSelected(currentSynmark);
		//console.log("cur sy id:"+currentSynmark.attr("id"));
		multimedia.setPosition(parseInt(currentSynmark.attr("date-time-st")));
	},
	deleteSynmark:function(synmark_id)
	{
		var deleteSynmarkURL = g.createLink({controller:'recording', action:'deleteSynmarkAjax'});
		$.ajax({
			type:"POST",
			url:deleteSynmarkURL,
			data:{synmark_id:synmark_id},
			datatype:"json",
			success:function(data,textStatus,jqXHR)
			{
				if(data.success) //status == 200
				{
					synmark.showMsg(data.success.description,null);
				}
				else
				{
					//console.log("not success");
					synmark.showMsg(data.error.description,"error")
				}
				synmark.refresh();
			}
		})
	},
	getURI:function(synmark_id)
	{
		//This is the actual URI, but we need to write some code to redirect it to the player
		//return getHostLocation()+"/"+appPath+"/resource/synmark/"+synmark_id;
		
		//This is the playing URI, which could be shared easily.
		//console.log("getURI:"+resourceBaseURI+synmark_id);
		return resourceBaseURI+synmark_id;
	},
	//reset synmark form
	resetSynmarkForm:function()
	{
		$('#synmark_form').resetForm();
		//CKEDITOR.instances.synmark_note.setData("");
	},
	empty:function()
	{
		//empty the data
		this.synmarks = null; 
		this.synmarksData = null;
		this.synmarkTags = null;
		this.selectedSynmark = null; 
		//empty the inner container
		this.inner_container.empty();
	},
	refresh:function()
	{
		var synmark_list_div = this.inner_container;
		//empty the inner container first
		this.empty();
		this.synmarkTags = new Array();
		var getSynmarksURL = g.createLink({controller:'recording', action:'getSynmarksAjax'});
		$.ajax({
			   type: "GET",
			   url: getSynmarksURL,
			   data:{multimediaId:this.recording.id},
			   dataType: "json",
			   beforeSend:function(jqXHR, settings)
			   {
				   $("#synmark_loading_div").show();
			   },
			   success:function(data)
			   {
				   if(data == null || data.length == 0)
				   {
					   synmark_list_div.html("No Synmarks");
					   return;
				   }
				   
				   data = $(data).sort(sortSynmarkByStartTime);
				   $("#synmark_count_span").text("("+data.length+")");
				   synmark.synmarksData = data;
				   $.each(data, function(i,s){ //cannot use synmark as variable name as it has been used for the global variable
					   var isMySynmark = false;
					   var single_synmark_div = $("<div/>",{
						   mouseover:function(){$(this).addClass("synmark_highlight");},
						   mouseout:function(){$(this).removeClass("synmark_highlight");},
						   click:function(){
							   synmark.clickSynmark($(this));
						   },
						   id:"synmark_"+s.id
					   }).attr("date-time-st",s.start).attr("date-time-et",s.end?s.end:"").attr("synmark_id",s.id)
					   .addClass("single_synmark_div").appendTo(synmark_list_div);
					   //MicroData: add microdata related to synmark
					   mdHelper.setMediaObject(single_synmark_div, recording.isVideo == 'true'?true:false);
					   mdHelper.setItemid(single_synmark_div,attachFragmentToURI(resourceBaseURI+recording.id,getFragmentString(s.start,s.end)));
					   if(user.id == s.owner.id)
					   {
						   single_synmark_div.addClass("synmark_mine");
						   isMySynmark = true;
					   }
					   else
						   single_synmark_div.addClass("synmark_other");
					   
					   var synmark_title_span = $("<span/>",{
						   text:s.title?s.title:"No title"
					   }).addClass("synmark_title").appendTo(single_synmark_div);
					   
					   //MicroData: add synmark title
					   if(s.title !== undefined)
						   mdHelper.setItemprop(synmark_title_span,"name");
					  
					   var synmark_owner_span = $("<span/>",{
						   //Yunjia: We have to update UserData and add userName instead of firstName and lastName
						   text:"by "+s.owner.firstName
					   }).addClass("owner-info pull-right").insertAfter(synmark_title_span);
					   
					   //MicroData: set owner span
					   mdHelper.setItemprop(synmark_owner_span,"creator");
					   mdHelper.createItem(synmark_owner_span,"http://schema.org/Person");
					   mdHelper.setItemid(synmark_owner_span,userBaseURI+s.owner.id);
					   mdHelper.setMetaTag(mdHelper.createMetaTag(),"familyName",s.owner.lastName).appendTo(synmark_owner_span);
					   mdHelper.setMetaTag(mdHelper.createMetaTag(),"givenName",s.owner.firstName).appendTo(synmark_owner_span);
					   
					   var synmark_1_br = $("<br/>").insertAfter(synmark_owner_span);
					   var synmark_time_span = $("<span/>",{
						   text: milisecToString(s.start)+(s.end?(" to "+milisecToString(s.end)):"")
					   }).addClass("synmark_time").insertAfter(synmark_1_br);
					   
					   var synmark_btn_span = $("<span/>").insertAfter(synmark_time_span);
					   //Get synmark link button
					   var link_synmark_btn = $("<button/>",{
							  html: "<i class='icon-share'></i>"
						   }).attr("title","Share the URL of this synmark").attr("type","button").addClass("btn btn-mini");
					   link_synmark_btn.bind("click",{synmark_id:s.id},function(event){
						   $("#synmark_url_dialog .modal-body").html("<p>"+synmark.getURI(event.data.synmark_id)+"</p>");
						   $("#synmark_url_dialog").modal('show');
					   });
					   link_synmark_btn.appendTo(synmark_btn_span);
					   
					   //Edit and Delete synmark button
					   if(isMySynmark == true)
					   {
						   var edit_synmark_btn = $("<button/>",{
							  html: "<i class='icon-edit'></i>"
						   }).attr("title","Edit this synmark").attr("type","button").addClass("btn btn-mini");
						   edit_synmark_btn.bind("click",{synmark_id:s.id},function(event){
							   var synmark_id = event.data.synmark_id;
							   var synmarkData = null;
							   $.each(synmark.synmarksData,function(i,s){
									if(s.id == synmark_id)
									{
										synmarkData = s;
										return;
									}
								})
								
								//Open the edit widget
								if(synmarkData!=null)
								{
									var tags = "";
									if(synmarkData.tags && synmarkData.tags.length >0)
									{
										
										var separator ="";
										$.each(synmarkData.tags,function(j,t){
											tags+=separator+$.trim(t);
											separator=",";
										});
									}
									synmark.fillSynmarkForm(milisecToString(synmarkData.start),
											milisecToString(synmarkData.end),synmarkData.title,tags,synmarkData.note,synmarkData.id,
											synmarkData.thumbnail);
									
									$("html,body").animate({scrollTop:$("#synmarks_div").offset().top},200);
									$("#synmark_create_div").show(200);
								}
						   });
						   edit_synmark_btn.appendTo(synmark_btn_span);
						   
						   var delete_synmark_btn = $("<button/>",{
								  html: "<i class='icon-trash'></i>"
						   }).attr("title","Delete this synmark").attr("type","button").addClass("btn btn-mini");
						   delete_synmark_btn.bind("click",{synmark_id:s.id},function(event){
							   var synmark_id = event.data.synmark_id;
							   if(confirm("Do you want to delete this synmark?"))
							   {
								   synmark.deleteSynmark(synmark_id);
							   }
						   });
						   delete_synmark_btn.appendTo(synmark_btn_span);
					   } 
					   
					   if(s.note)
					   {
						   var synmark_note_p = $("<p/>",{
							   html:s.note
						   }).addClass("synmark_note").insertAfter(synmark_time_span);
						   //MicroData: add synmark note
						   mdHelper.setItemprop(synmark_note_p,"description");
					   }
					   
					   if(s.tags.length>0)
					   {
						   var synmark_tags_div = $("<div/>").addClass("synmark_tags_div").insertAfter(synmark_time_span);
						   //Yunjia: Add "tags" title to tags
						   //var synmark_tags_title_span=$("<span/>",{
							//  text:"tags"
						   //}).addClass("synmark_title_span").appendTo(synmark_tags_div);
						   
						   $.each(s.tags, function(j,tag){
							  var synmark_tag_span = $("<span/>",{
								  html:"<i class='icon-tag tag-item icon-white'></i>"+tag
							  }).addClass("badge badge-tag").appendTo(synmark_tags_div);
							  //MicroData: add keyword
							  mdHelper.setItemprop(synmark_tag_span,"keywords");
						   });
						   
						   //Init the tags, think about the tags of owner's only!
						   if((!synmark.myTagsOnly) || (synmark.myTagsOnly && user.id == s.owner.id))
						   {
							   $.each(s.tags, function(j,tag){
								   if($.inArray(tag.toLowerCase(),synmark.synmarkTags) == -1)
									   synmark.synmarkTags.push(tag);
							   });
						   }
						   synmark.synmarkTags.sort();
					   }
				   });
				   //Yunjia: we can setup a mode to float display the slides
				   //$("#slides_div").aqFloater({attach:"s", duration:0, opacity:.9,offsetX:0,offsetY:0});
				   //Init click menu
				  
				   synmark.synmarks = $(".single_synmark_div");
				   
				   //synmark.synmarks = new Array();
				   /*$.each(unsortedSynmarks,function(idx,itm)
				   {
					   synmark.synmarks.push(itm);
				   });*/
			   },
			   complete:function(jqXHR, textStatus)
			   {
				   $("#synmark_loading_div").hide();
			   }
		});
	}
});

var sortSynmarkByStartTime = function(a,b)
{
	return a.start > b.start?1:-1;
}