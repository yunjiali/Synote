/*
 * Class for presentations
 */
var Presentation = Base.extend({
	recording:null,
	outer_container:null,
	inner_container:null,
	presentationId:null,
	slides:null,
	slidesData:null, //a list of slides in json format
	selectedSlide:null,
	synchronised:true,
	autoScroll:true,
	scrollOffset:2,
	editingEnabled:false,
	
	constructor:function(recording, outer_container, inner_container)
	{
		this.recording = recording;
		this.outer_container=outer_container;
		this.inner_container=inner_container;
	},
	initPresentation:function()
	{	
		//Start loading slides
		//this.refresh(this.outer_container,this.inner_container);
		 //Yunjia: we can setup a mode to float display the slides
		   //$("#slides_div").aqFloater({attach:"s", duration:0, opacity:.9,offsetX:0,offsetY:0});
		//var slides_div = this.outer_container;
		this.outer_container.remove("#container");
		this.outer_container.appendTo("#container_wrapper");
		
		//Init the control buttons on slides bar
		$("#slides_play_btn").bind('click',{},function(){
			multimedia.play();
		});
		$("#slides_pause_btn").bind('click',{},function(){
			multimedia.pause();
		});
		$("#slides_stop_btn").bind('click',{},function(){
			multimedia.stop();
		});
		$("#slides_rewind_btn").bind('click',{},function(){
			multimedia.rewind();
		});
		$("#slides_forward_btn").bind('click',{},function(){
			multimedia.forward();
		});
		
		//If can edit, init the add, edit and delete buttons
		if(recording.canEdit === "true")
		{
			$("#presentation_edit_dialog .uniForm").uniform();
			$("#presentation_edit_dialog .uniForm input[type=text]").wijtextbox();
			$("#slide_st").mask("?99:99:99");
			$("#slide_et").mask("?99:99:99");
			$("#slide_st_time").click(function(){
				var currentPosition = multimedia.getPosition();
				//console.log("curpo:"+currentPosition);
				$("#slide_st").val(milisecToString(currentPosition));
			});
			$("#slide_st_add").click(function(){
				var oldTime = stringToMilisec($("#slide_st").val());
				var newTime = oldTime+1000;
				$("#slide_st").val(milisecToString(newTime));
			});
			$("#slide_st_remove").click(function(){
				var oldTime = stringToMilisec($("#slide_st").val());
				var newTime = oldTime-1000>0?oldTime-1000:0;
				$("#slide_st").val(milisecToString(newTime));
			});
			$("#slide_et_time").click(function(){
				var currentPosition = multimedia.getPosition();
				$("#slide_et").val(milisecToString(currentPosition));
			});
			$("#slide_et_add").click(function(){
				var oldTime = stringToMilisec($("#slide_et").val());
				var newTime = oldTime+1000;
				$("#slide_et").val(milisecToString(newTime));
			});
			$("#slide_et_remove").click(function(){
				var oldTime = stringToMilisec($("#slide_et").val());
				var newTime = oldTime-1000>0?oldTime-1000:0;
				$("#slide_et").val(milisecToString(newTime));
			});
			$("#presentation_submit").button();
			$("#presentation_cancel").button().click(function(){
				$("#presentation_edit_dialog").wijdialog('close');
				$("#presentation_edit_dialog").wijdialog('destroy');//exit editing
			});
			//Yunjia: Can't use combobox because the dropdownlist keep on appearing behind the dialog
			//$("#presentation_edit_dialog select").combobox();
			$("#slides_add_btn").click(function(){
				var newTime = multimedia.getPosition();
				presentation.fillPresentationForm(milisecToString(newTime),"","","","","");
				presentation.showEditForm("Add Slide");
				return;
			});
			
			$("#slides_edit_btn").click(function(){
				//Add slide_id first
				if(presentation.selectedSlide != null)
				{
					presentation.showEditForm("Edit Slide");
					var slide_st = milisecToString(presentation.selectedSlide.attr("date-time-st"));
					var slide_index = parseInt(presentation.selectedSlide.attr("index"))+1;
					var slide_et;
					if(presentation.selectedSlide.attr("date-time-et") === "")
					{
						var next_slide = $("#image_container_div img[index='"+slide_index+"']");
						if(next_slide == null)
						{
							slide_et = slide_st;
						}
						else
						{
							slide_et =  milisecToString(next_slide.attr("date-time-st"));
						}
					}
					else
					{
						slide_et = milisecToString(presentation.selectedSlide.attr("date-time-et"));
					}
					
					
					var slide_url = presentation.selectedSlide.attr("src");
					var slide_id = presentation.selectedSlide.attr("slide_id");
					var old_index = slide_index;
					presentation.fillPresentationForm(slide_st,slide_et,slide_index,slide_url,slide_id,old_index);
					return;
				}
				else
				{
					alert("Please select a slide.");
					return;
				}
			});
			
			$("#slides_delete_btn").click(function(){
				if(presentation.selectedSlide == null)
				{
					alert("Please select a slide.");
					return;
				}
				else
				{
					var r = confirm("Are you sure?");
					if(r==true)
					{
						var deleteSlideURL = g.createLink({controller:'recording', action:'deleteSlideAjax'});
						$.ajax({
							type:"POST",
							url:deleteSlideURL,
							data:{multimedia_id:recording.id,slide_index:presentation.selectedSlide.attr("index"),presentation_id:presentation.presentationId},
							datatype:"text",
							success:function(data)
							{
								//console.log("data:"+data);
								var respJson = data;
								//console.log("status:"+status);
								if(respJson.success) //status == 200
								{
									//console.log("success"+respJson.success.description);
									presentation.showMsgDialog(respJson.success.description);
								}
								else if(respJson.error)
								{
									//console.log("not success");
									presentation.showMsgDialog("<span style='color:red'>"+respJson.error.description+"</span>");
								}
								//$(":wijmo-wijdialog").wijdialog("destroy").remove();
								presentation.refresh();
							}
						});
					}
					else
						return;
				}
			});
			
			$("#presentation_edit_form").submit(function(){
				var url;
				var params;
				if($("#slide_id").val() != null && $("#slide_id").val() !== undefined && $.trim($("#slide_id").val()).length>0) //if slide_id is not null, it means slide updates 
				{
					url = g.createLink({controller:"recording",action:"updateSlideAjax",params:{multimedia_id:recording.id,presentation_id:presentation.presentationId}});
				}
				else
				{
					if(presentation.presentationId == null)
						url = g.createLink({controller:"recording",action:"saveSlideAjax",params:{multimedia_id:recording.id}});
					else
						url = g.createLink({controller:"recording",action:"saveSlideAjax",params:{multimedia_id:recording.id,presentation_id:presentation.presentationId}});
					//;
				}
				
				$(this).ajaxSubmit({
					url:url,
					//resetForm:true,
					type:'post',
					dataType:'text',
					//data:params,
					beforeSend:function(event)
					{
						$("#presentation_dialog").wijdialog('destroy');
						
					},
					success:function(responseText, statusText, xhr, $form)
					{
						//var respText = ""; //this will be the string go to the dialog
						var respJson = $.parseJSON(responseText);
						//console.log("status:"+status);
						if(respJson.success) //status == 200
						{
							presentation.showMsgDialog(respJson.success.description);
							presentation.resetPresentationForm();
							$("#presentation_edit_dialog").wijdialog('close');
							presentation.refresh();
						}
						else if(respJson.error)
						{
							presentation.showMsgDialog("<span style='color:red'>"+respJson.error.description+"</span>");
							
						}
						
					}
				});
				
				return false;
			});
		}
	},
	resetPresentationForm:function()
	{
		$('#presentation_edit_form').resetForm();
	},
	fillPresentationForm:function(slide_st,slide_et,slide_index,slide_url,slide_id, old_index)
	{
		$("#slide_st").val(slide_st);
		$("#slide_et").val(slide_et);
		$("#slide_index").val(slide_index);
		$("#slide_url").val(slide_url);
		$("#old_index").val(old_index);
		$("#slide_id").val(slide_id);
	},
	showEditForm:function(title)
	{
		$("#presentation_edit_dialog").wijdialog('destroy');
		$("#presentation_edit_dialog").attr("title",title).wijdialog({
	        autoOpen: true,
	        height: 320,
	        width: 480,
	        modal: false,
	        position:"right center",
	        close:function(e){
	        	presentation.resetPresentationForm();
	        },
	        captionButtons: {
	            pin: { visible: false },
	            refresh: { visible: false },
	            toggle: { visible: false },
	            minimize: { visible: false },
	            maximize: { visible: false }
	        }
	    });
	},
	showMsgDialog:function(msg)
	{
		$("#presentation_dialog").html(msg);
		$("#presentation_dialog").wijdialog({
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
	},
	sync:function(currentPosition)
	{
		//currentSlide is a jquery object
		//console.log("sync slide");
		if(this.slides != null && this.slides.length>0)
		{
			var currentSlide = this.getSlide(currentPosition)
			//console.log("selectedSlide:"+this.selectedSlide);
			if(currentSlide != this.selectedSlide)
			{
				this.setSlideSelected(currentSlide);
			}
		}
	},
	getSlide:function(currentPosition)
	{
		var cs = null; //current slide
		for(var i=0;i<this.slides.length;i++)
		{
			var st= parseInt($(this.slides[i]).attr("date-time-st"));//st is slide start time
			if(st>=currentPosition)
			{
				break;
			}
			else
				cs= $(this.slides[i]);
		}
		//console.log("sl date-time-st:"+cs?cs.attr("date-time-st"):"nulll");
		return cs;
	},
	setSlideSelected:function(currentSlide)
	{
		if(currentSlide != null)
		{
			if(this.selectedSlide != null)
				this.selectedSlide.removeClass("slide_selected");
			this.selectedSlide = currentSlide;
			currentSlide.addClass("slide_selected");
			if(this.autoScroll)
			{
				var index = parseInt(currentSlide.attr("index"));
				var i = index-this.scrollOffset>0?index-this.scrollOffset:index;
				//console.log("i:"+i);
				$("#image_container_ul").data('jcarousel').scroll($.jcarousel.intval(index));
			}
				//this.inner_container.scrollTo(currentSlide,400, {offset:this.scrollOffset});
				
		}
	},
	clickSlide:function(currentSlide)
	{
		this.setSlideSelected(currentSlide);
		multimedia.setPosition(parseInt(currentSlide.attr("date-time-st")));
	},
	initEditing:function()
	{
		var index_length = 1;
		if(presentation.slides != null)
		{
			index_length = presentation.slides.length+1;
		}
		   
		$("#slide_index").empty();
		//console.log(index_length);
		for(var i=1;i<=index_length;i++)
		{
			var opt = $("<option/>").val(i).text(i);
			$("#slide_index").append(opt);
		}
	},
	refresh:function()
	{
		var image_container_div  = this.inner_container;
		var multimediaId = this.recording.id;
		image_container_div.empty();
		var getPresentationsURL = g.createLink({controller:'recording', action:'getPresentationsAjax'});
		//var slides_div = $("<div/>").attr("id","slides_div").appendTo("body");
		var slides_div = this.outer_container;
		var image_zoom_div = $("<div />").attr('id',"image_zoom_div").appendTo(slides_div);
		image_container_div.css({width:"100%"});
		var image_container_ul=$("<ul/>").attr('id',"image_container_ul").addClass("jcarousel-skin-tango").appendTo(image_container_div);
		var slides_button = $("#slides_collapse_expand_btn");
		var slides_count_span = $("#slides_count_span");
		$.ajax({
			   type: "GET",
			   url: getPresentationsURL,
			   data:{multimediaId:multimediaId},
			   dataType: "json",
			   //Yunjia: Add a beforeSend function to display the loading message
			   success:function(data)
			   {
				   if(!data || !data[0] || data[0].slides.length==0)
				   {
					   slides_button.removeClass("slides_div_collapse");
					   slides_button.addClass("slides_div_expand");
					   slides_count_span.html("No Presentation Slides");
					   if(recording.canEdit === "true")
					   {
						   presentation.initEditing();
						   //$("#presentation_edit_dialog select").combobox();
					   }
					   //image_container_div.hide();
					   //slides_div.remove("#container");
					   //slides_div.appendTo("#container_wrapper");
					   initFixedBottomSlider('25','bottom','0.75');
					   return;
				   }
				   presentation.presentationId = data[0].id;
				   slides_count_span.html(data[0].slides.length+" Presentation Slides");
				   $.each(data[0].slides, function(i,slide){
					   var single_slide_li = $("<li/>");
					   var slide_div = $("<div style='text-align:center;'/>").appendTo(single_slide_li);
					   var slide_time_span = $("<span style='color:yellow;'/>").text(milisecToString(slide.start)).appendTo(slide_div);
					   slide_div.append($("<br/>"));
					   var slide_thumb_img = $("<img>",{
						   src: slide.url,
						   alt: slide.start,
						   height:96,
						   width:128,
						   id:"slide_"+slide.id,
						   mouseover:function(){$(this).addClass("slide_hover");},
						   mouseout:function(){$(this).removeClass("slide_hover");},
						   click:function()
						   {
							   presentation.clickSlide($(this));
						   }
					   }).attr("date-time-st",slide.start).attr("date-time-et",slide.end?slide.end:"").attr("index",i+1).attr("slide_id",slide.id)
					   .addClass("slide_thumb_image");//date-time-et is not used
					   slide_thumb_img.bind("dblclick",function(){
						   //console.log("double click images");
							 //$("#image_zoom_div").wijdialog("destroy");attr("index",slide.index)
					         $("#image_zoom_div").wijdialog({ 
					        	 width: 640, 
					        	 height: 480, 
					        	 contentUrl: slide.url, 
					        	 autoOpen: true,
					        	 captionButtons:{
					     			pin:{visible:false},
					     			refresh:{visible:false},
					     			toggle:{visible:false}
					     			}
					     		});
						});
					   slide_div.append(slide_thumb_img);
					   //single_slide_li.append(slide_thumb_img);
					   single_slide_li.append(slide_div);
					   image_container_ul.append(single_slide_li);
					   //Microdata: add imageObject
					   mdHelper.setMediaObject(single_slide_li,factory.isAudio);
					   mdHelper.setItemid(single_slide_li,attachFragmentToURI(resourceBaseURI+recording.id,getFragmentString(slide.start,slide.end)));
					   mdHelper.setItemprop(slide_thumb_img,"image");
					   mdHelper.createItem(slide_thumb_img,"http://schema.org/ImageObject");
				   });
				   //On collapse and expand button click:
				   slides_button.click(function(){
					   //console.log("collapse button");
					   if(image_container_div.is(':hidden'))
					   {
						   image_container_div.show();
						   $(this).removeClass("slides_div_expand");
						   $(this).addClass("slides_div_collapse");
						   initFixedBottomSlider('170','bottom','0.75'); //a method in player.presentation.js
					   }
					   else
					   {
						   image_container_div.hide();
						   $(this).removeClass("slides_div_collapse");
						   $(this).addClass("slides_div_expand");
						   initFixedBottomSlider('25','bottom','0.75');
					   }
				   })
				   presentation.slides = $(".slide_thumb_image");
				   
				   //###########################init the index in edit form############################
				   if(recording.canEdit === "true")
				   {
					   presentation.initEditing();
					   //$("#presentation_edit_dialog select").combobox();
				   }
				   //slides_div.remove("#container");
				   //slides_div.appendTo("#container_wrapper");
				   initFixedBottomSlider('170','bottom','0.75');
				   //To make it width 100%				   
				   $("#image_container_ul").jcarousel();
			   }
		});	   
	}
});

//Function to make the slides float on the bottom
var initFixedBottomSlider = function(height,position,opacity)
{
	 	presentation.outer_container.meerkat({
			height: height,//128 for image div, 25 for top bar
			opacity:opacity,
			width: '100%',
			position: position,
			animationIn: 'slide'
		});
};