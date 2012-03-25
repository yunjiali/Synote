/*
 * Define synmark class
 */
var Synmark = Base.extend({
	recording:null,
	outer_container:null,
	inner_container:null,
	selectedSynmark:null, //is a jquery object, not a dom object
	synmarks:null,//a list of synmark divs, jquery object
	synmarksData:null, //a lit of synmark json data
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
	initSynmark:function()
	{
		//Yunjia: don't need to do that if it's read only and we have to remove the form if it's readonly
		if(recording.canCreateSynmark === "true")
		{
			$("#synmarks_div .uniForm").uniform();
			$("#synmarks_div .uniForm input[type=text]").wijtextbox();
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
			$( "#synmark_tags" ).bind( "keydown", function( event ) {
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
			});
			
			//Init tinyMCE
			$('#synmark_note').tinymce({
				script_url:g.resource({dir:'js/tiny_mce',file:'tiny_mce.js'}),
				mode : "textareas",
				width:"100%",
				plugins:"xhtmlxtras",
				//plugins : "autolink,lists,pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template,advlist,rdfa",
				valid_elements : "*[*]",
				theme:"advanced",
				theme_advanced_buttons1 : "bold,italic,underline,forecolor,backcolor,|,code",//,|,namespace,about,property,rdfGraph,rdfEnrich,stat,setting",
				theme_advanced_buttons2 : "",
		        theme_advanced_buttons3 : "",
		        theme_advanced_toolbar_location : "top",
		        theme_advanced_toolbar_align : "left",
		        theme_advanced_statusbar_location : "bottom",
		        theme_advanced_resizing : true,
			});

			//Init the CKeditor
			//$("#synmark_note").ckeditor();
			//Init the ajax submitting of forms
			$('#synmark_form').submit(function(){
				//copy the data from ckeditor to synmark textarea
				//$("#synmark_note").val(CKEDITOR.instances.synmark_note.getData());
				var url;
				if($("#synmark_id").val() != null && $("#synmark_id").val() !== undefined && $.trim($("#synmark_id").val()).length>0) //if id is not null, it means synmark updates 
				{
					url = g.createLink({controller:"recording",action:"updateSynmarkAjax"});//, params:{synmark_id:$.trim($("#synmark_id").val())}});
				}
				else
				{
					url = g.createLink({controller:"recording",action:"saveSynmarkAjax",params:{multimedia_id:recording.id}});
				}
				$(this).ajaxSubmit({
					url:url,
					//resetForm:true,
					type:'post',
					dataType:'text',
					target:"okMsg",
					beforeSend:function(event)
					{
						$("#synmark_create_errorMsg").hide();
						
					},
					success:function(responseText, statusText, xhr, $form)
					{
						//var respText = ""; //this will be the string go to the dialog
						var respJson = $.parseJSON(responseText);
						//console.log("status:"+status);
						if(respJson.success) //status == 200
						{
							$("#synmark_dialog").text(respJson.success.description);
							//$(":wijmo-wijdialog").wijdialog("destroy").remove();
							$("#synmark_dialog").wijdialog({
				                autoOpen: true,
				                height: 180,
				                width: 400,
				                modal: true,
				                buttons: {
				                    Ok: function () {
				                        $(this).wijdialog("close");
				                    }
				                },
				                captionButtons: {
				                    pin: { visible: false },
				                    refresh: { visible: false },
				                    toggle: { visible: false },
				                    minimize: { visible: false },
				                    maximize: { visible: false }
				                }
				            });
							synmark.resetSynmarkForm();
							$("#synmark_create_div").hide(400);
							synmark.refresh();
						}
						else if(respJson.error)
						{
							$("#synmark_create_errorMsg").text(respJson.error.description);
							$("#synmark_create_errorMsg").show();
							//$("#synmark_create_errorMsg").text(responseText);
						}
						
					}
				});
				
				return false;
			});
			$("#synmark_cancel").button().click(function(){
				$("#synmark_create_div").hide(400);
				//console.log("note:"+$("#synmark_note").val());
			});
			$("#synmark_submit").button();
			
			$("#add_synmark_img").click(function(){
				$("#synmark_create_errorMsg").hide();
				var newTime = multimedia.getPosition();
				synmark.fillSynmarkForm(milisecToString(newTime),"","","","","");
				$("#synmark_create_div").show(400);
			});
		}
		$("#export_synmark_img").click(function(){
			//Yunjia: We may later add some more export functions, such as my synmarks only, or whose synmark only, etc
			//And a better solution will be to use server side code to generate csv instead of here
			var cvs = synmark.exportAsCSV();
			if (navigator.appName != 'Microsoft Internet Explorer')
		    {
		        window.open('data:text/csv;charset=utf-8,' + escape(cvs));
		    }
		    else
		    {
		        var popup = window.open('','csv','');
		        popup.document.body.innerHTML = '<pre>' + cvs + '</pre>';
		    }
		});
		
		//Start loading synmarks
		//this.refresh();
	},
	fillSynmarkForm:function(synmark_st, synmark_et, synmark_title,synmark_tags, synmark_note, synmark_id)
	{
		$("#synmark_st").val(synmark_st);
		$("#synmark_et").val(synmark_et);
		$("#synmark_title").val(synmark_title);
		$("#synmark_tags").val(synmark_tags);
		//$("#synmark_note").val(synmark_note);
		//CKEDITOR.instances.synmark_note.setData(synmark_note);
		$("#synmark_note").val(synmark_note);
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
			datatype:"text",
			success:function(data)
			{
				//console.log("data:"+data);
				var respJson = data;
				//console.log("status:"+status);
				if(respJson.success) //status == 200
				{
					//console.log("success"+respJson.success.description);
					$("#synmark_dialog").text(respJson.success.description);
				}
				else if(respJson.error)
				{
					//console.log("not success");
					$("#synmark_dialog").html("<span style='color:red'>"+respJson.error.description+"</span>");
				}
				//$(":wijmo-wijdialog").wijdialog("destroy").remove();
				$("#synmark_dialog").wijdialog({
	                autoOpen: true,
	                height: 180,
	                width: 400,
	                modal: true,
	                buttons: {
	                    Ok: function () {
	                        $(this).wijdialog("close");
	                    }
	                },
	                captionButtons: {
	                    pin: { visible: false },
	                    refresh: { visible: false },
	                    toggle: { visible: false },
	                    minimize: { visible: false },
	                    maximize: { visible: false }
	                }
	            });
				synmark.refresh();
			}
		})
	},
	getURI:function(synmark_id)
	{
		//This is the actual URI, but we need to write some code to redirect it to the player
		//return getHostLocation()+"/"+appPath+"/resource/synmark/"+synmark_id;
		
		//This is the playing URI, which could be shared easily.
		return resourceBaseURI+synmark_id;
	},
	exportAsCSV:function()
	{
		var str = "";
		for(var i=0;i<this.synmarksData.length;i++)
		{
			var line='';
			var s = this.synmarksData[i];
			//console.log("sid:"+s.id);
			for(var key in s)
			{
				
				if(key.toLowerCase() != "class" && key.toLowerCase() != "owner") //We don't need the "class" in the cvs
				{
					if(line != '')
						line+=",";
					
					line +=s[key];
					//console.log("key:"+s[key]);
				}
			}
			str += line+'\r\n';
		}
		return str;
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
			   //Yunjia: Add a beforeSend function to display the loading message
			   //Yunjia: Also set the height before loading finished
			   success:function(data)
			   {
				   if(data == null || data.length == 0)
				   {
					   synmark_list_div.html("No Synmarks");
					   return;
				   }
				   synmark.synmarksData = data;
				   var synmark_list_div_height = window.screen.height; 
					   //(window.screen.height-180)>0?window.screen.height-180:window.screen.height;
				   synmark_list_div.css("max-height",synmark_list_div_height);
				   $.each(data, function(i,s){ //cannot use synmark as variable name as it has been used for the global variable
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
					   mdHelper.setMediaObject(single_synmark_div,factory.isAudio);
					   mdHelper.setItemid(single_synmark_div,attachFragmentToURI(resourceBaseURI+recording.id,getFragmentString(s.start,s.end)));
					   if(user.id == s.owner.id)
					   {
						   single_synmark_div.addClass("synmark_mine");
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
					   }).addClass("synmark_owner").insertAfter(synmark_title_span);
					   
					   //MicroData: set owner span
					   mdHelper.setItemprop(synmark_owner_span,"creator");
					   mdHelper.createItem(synmark_owner_span,"http://schema.org/Person");
					   mdHelper.setItemid(synmark_owner_span,userBaseURI+s.owner.id);
					   mdHelper.setMetaTag(mdHelper.createMetaTag(),"familyName",s.owner.lastName).appendTo(synmark_owner_span);
					   mdHelper.setMetaTag(mdHelper.createMetaTag(),"givenName",s.owner.firstName).appendTo(synmark_owner_span);
					   
					   var synmark_1_br = $("<br/>").insertAfter(synmark_owner_span);
					   var synmark_time_span = $("<span/>",{
						   text: "position: "+milisecToString(s.start)+(s.end?(" to "+milisecToString(s.end)):"")
					   }).addClass("synmark_time").insertAfter(synmark_1_br);
					   
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
								  text:tag
							  }).addClass("tag").appendTo(synmark_tags_div);
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
						   
						   var synmark_tag_title_span = $("<span/>",{
								  text:"tags: "
							  }).addClass("synmark_tag_title_span").appendTo(synmark_tags_div);
						   
					   }
				   });
				   //Yunjia: we can setup a mode to float display the slides
				   //$("#slides_div").aqFloater({attach:"s", duration:0, opacity:.9,offsetX:0,offsetY:0});
				   //Init click menu
				   initSynmarkOwnerClickMenu(".synmark_mine");//method in method in player.synmark.click.menu.js
				   initSynmarkOtherClickMenu(".synmark_other");
				   synmark.synmarks = $(".single_synmark_div");
					   //Yunjia: Well, I don't think we need to sort, currently I didn't find any problem
				   //var unsortedSynmarks = $(".single_synmark_div");
				   /*unsortedSynmarks.sort(function(a,b)
				   {
					   var sta = parseInt($(a).attr("date-time-st"));
					   var stb = parseInt($(b).attr("data-time-st"));
					   return (sta<stb)?-1:(sta>stb)?1:0;
				   });*/
				   
				   
				   //synmark.synmarks = new Array();
				   /*$.each(unsortedSynmarks,function(idx,itm)
				   {
					   synmark.synmarks.push(itm);
				   });*/
			   }
		});
	}
});