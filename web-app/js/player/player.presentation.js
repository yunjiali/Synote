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
		
		//If can edit, init the add, edit and delete buttons
		if(recording.canEdit === "true")
		{
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
			$("#presentation_cancel").click(function(){
				$("#presentation_edit_form").resetForm();
				$("#presentation_edit_div").hide(400);
			});
			//Yunjia: Can't use combobox because the dropdownlist keep on appearing behind the dialog
			//$("#presentation_edit_dialog select").combobox();
			$("#slides_add_btn").click(function(){
				var newTime = multimedia.getPosition();
				presentation.fillPresentationForm(milisecToString(newTime),"","","","","");
				$("#presentation_edit_div").show(400);
				return;
			});
			
			$("#slides_edit_btn").click(function(){
				//Add slide_id first
				if(presentation.selectedSlide != null)
				{
					$("#presentation_edit_div").show(400);
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
								if(data.success) //status == 200
								{
									//console.log("success"+respJson.success.description);
									presentation.showMsg(data.success.description,null);
								}
								else if(data.error)
								{
									//console.log("not success");
									presentation.showMsg(data.error.description,"error");
								}
								presentation.refresh();
							},
							error:function(jqXHR,textStatus,errorThrown)
							{
								var resp =$.parseJSON(jqXHR.responseText);
								presentation.showMsg(resp.error.description,"error");
								return;
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
					dataType:'json',
					//data:params,
					success:function(data, statusText, xhr, $form)
					{
						if(data.success) //status == 200
						{
							presentation.showMsg(respJson.success.description,null);
							presentation.resetPresentationForm();
							presentation.refresh();
						}
						else if(data.error)
						{
							presentation.showMsg(data.error.description);
						}
						
					},
					error:function(jqXHR,textStatus,errorThrown)
					{
						var resp =$.parseJSON(jqXHR.responseText);
						presentation.showMsg(resp.error.description,"error");
						return;
					}
				});
				
				return false;
			});
			
			$("#presentation_edit_form").validate(
			{
				rules: {
				    slides_st: {
					    required:true
				    },
				    slides_et:{
						required:true
					},
					slide_index:{
						required:true,
					},
					slide_url:{
						required:true,
						maxlength:255,
						url:true
					}
				 },
				highlight: function(label) {
					$(label).closest('.control-group').addClass('error');
				},
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
	showMsg:function(msg,error)
	{
		var msg_div = $("#slides_msg_div");
		if(type == "error")
		{
			msg_div.html("<div class='alert alert-error'><button class='close' data-dismiss='alert'>x</button>"+msg+"</div>");
		}
		else
		{
			msg_div.html("<div class='alert alert-success'><button class='close' data-dismiss='alert'>x</button>"+msg+"</div>");
		}
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
			}
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
	showMsg:function(msg,type)
	{
		var msg_div = $("#slides_msg_div");
		if(type == "error")
		{
			msg_div.html("<div class='alert alert-error'><button class='close' data-dismiss='alert'>x</button>"+msg+"</div>");
		}
		else
		{
			msg_div.html("<div class='alert alert-success'><button class='close' data-dismiss='alert'>x</button>"+msg+"</div>");
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
		var image_container_ul=$("<ul/>").attr('id',"image_container_ul").addClass("thumbnails").appendTo(image_container_div);
		$.ajax({
			   type: "GET",
			   url: getPresentationsURL,
			   data:{multimediaId:multimediaId},
			   dataType: "json",
			   beforeSend:function(jqXHR, settings)
			   {
				   $("#slides_loading_div").show();
			   },
			   success:function(data)
			   {
				   if(!data || !data[0] || data[0].slides.length==0)
				   {
					   image_container_div.html("No Presentation Slides");
					   $("#slides_count_span").text("(0)");
					   if(recording.canEdit === "true")
					   {
						   presentation.initEditing();
					   }
					   return;
				   }
				   presentation.presentationId = data[0].id;
				   $("#slides_count_span").text("("+data[0].slides.length+")");
				   $.each(data[0].slides, function(i,slide){
					   var single_slide_li = $("<li/>").addClass("span-thumbnail");
					   var slide_a = $("<div/>").addClass("thumbnail");
					   //var slide_time_span = $("<span style='color:yellow;'/>").text(milisecToString(slide.start)).appendTo(slide_div);
					   var slide_thumb_img = $("<img>",{
						   src: slide.url,
						   alt: slide.start,
						   id:"slide_"+slide.id,
						   //height:165,
						   //width:220,
						   mouseover:function(){$(this).addClass("slide_hover");},
						   mouseout:function(){$(this).removeClass("slide_hover");},
						   click:function()
						   {
							   presentation.clickSlide($(this));
						   }
					   }).attr("date-time-st",slide.start).attr("date-time-et",slide.end?slide.end:"").attr("index",i+1).attr("slide_id",slide.id)
					   .addClass("slide_thumb_image");//date-time-et is not used
					   slide_thumb_img.bind("dblclick",function(){
					         
						});
					   slide_a.append(slide_thumb_img);
					   //single_slide_li.append(slide_thumb_img);
					   single_slide_li.append(slide_a);
					   image_container_ul.append(single_slide_li);
					   //Microdata: add imageObject
					   mdHelper.setMediaObject(single_slide_li,recording.isVideo == 'true'?true:false);
					   mdHelper.setItemid(single_slide_li,attachFragmentToURI(resourceBaseURI+recording.id,getFragmentString(slide.start,slide.end)));
					   mdHelper.setItemprop(slide_thumb_img,"image");
					   mdHelper.createItem(slide_thumb_img,"http://schema.org/ImageObject");
				   });
				   
				   presentation.slides = $(".slide_thumb_image");
				   
				   //###########################init the index in edit form############################
				   if(recording.canEdit === "true")
				   {
					   presentation.initEditing();
				   }
			   },
			   complete:function(jqXHR, textStatus)
			   {
				   $("#slides_loading_div").hide();
			   }
		});	   
	}
});
