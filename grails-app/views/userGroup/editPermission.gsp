<html>
<head>
<title><g:message
	code="org.synote.user.group.editPermission.title" /></title>
<meta name="layout" content="main" />
<script type="text/javascript" src="${resource(dir:'js/i18n',file:"grid.locale-en.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.jqGrid.min.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery/uniform',file:'uni-form.jquery.min.js')}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery/uniform',file:'uni-form-validation.jquery.min.js')}"></script>
<script type="text/javascript" src="${resource(dir: 'js/jquery', file: 'jquery.combobox.js')}"></script>
<link rel="stylesheet" href="${resource(dir:'css/jquery',file:"ui.jqgrid.css")}" media="screen, projection" />
<script type="text/javascript">
	//Yunjia: more work on this page
	//1. format the datatime created and updated
	//2. think about adding recording title to the synmarklist
	$(document).ready(function(){
		$("#resource_search_button").button({
			icons:{
				primary:"ui-icon-search"	
			},
			text:false
		});
		$("#group_show_back_button").button({
			icons:{
				primary:"ui-icon-carat-1-w"	
			},
		});
		
		$(".uniForm").uniform();
		$("input[type=text], input[type=password]").wijtextbox();
		$("#add_recording_permission_submit").button();
		$("#add_recording_permission_cancel").button();
		$(".combowrap select").combobox();
		
		//init the medal dialog
		$("#add_recording_dialog").wijdialog({
			captionButtons:{
				pin:{visible:false},
				refresh:{visible:false},
				toggle:{visible:false},
				close:{visible:true},
				minimize:{visible:false},
				maximize:{visible:false}
			},
	     	autoOpen: false,
	        height: 240,
	        width: 320,
	        modal: true
	    }); 
		$("#add_recording_dialog").wijdialog('close');
	    $("#add_recording_permission_cancel").click(function(){
	    	$("#add_recording_dialog").wijdialog('close');
		});
		$("#resource_search_button").click(function()
		{
			//console.log("clickbutton");
			var sText = $("#resource_search_text").val();
			//console.log("stext:"+sText);
			if(sText!=null&&$.trim(sText).length>0)
			{
				$("#multimedia_list_table").jqGrid("GridUnload");
				loadMultimediaResource(sText);
			}
		});
		$("#resource_search_text").keyup(function(event){
  				if(event.keyCode == 13){
    			$("#resource_search_button").click();
  			}
		});
		var searchText = "${params.text}";
		loadMultimediaResource(searchText);
		loadGroupRecording();
		//Yunjia: Later, we can add the groups you belong to
	});
	

function loadMultimediaResource(searchText) 
{
	var multimedia_list_url = g.createLink({controller:"userGroup",action:"listPossibleRecordingsAjax"});
	multimedia_list_url += "/"+"${userGroup?.id}";
	var caption = "My Recordings";
	if(searchText!=null&&$.trim(searchText).length>0)
	{
		multimedia_list_url += "?text="+searchText;
		caption += " (search results for term '"+(searchText.length>15?(searchText.substring(0,11)+"..."):searchText)+"')";
	}
	//console.log(multimedia_list_url);
	$("#multimedia_list_table").jqGrid({ 
		url:multimedia_list_url, 
		datatype: "json",
	//	data:"text=test", 
		jsonReader: { repeatitems: false },
		colNames:['Id','Title','Action','Public Permission','Created','Updated'], //Yunjia: Add create and update later, to do this , we have to change the database 
		colModel:[ {name:'id', index:'id', width:40}, 
					{name:'title',index:'title'}, 
					{name:'act', sortable:false, formatter:actListMultimediaFormatter, width:100},
					{name:'public_perm_name',index:'perm', width:70}, 
					{name:'date_created', index:'dateCreated', width:60, align:"center"},
					{name:'last_updated', index:'lastUpdated', width:60, align:"center"}
				],
		rowNum:10, 
		rowList:[10,30,50], 
		pager: '#multimedia_pager', 
		sortname: 'id',
		width:950, 
		viewrecords: true, 
		emptyrecords: "No recordings are found.",
		height:"auto",
		sortorder: "desc",
		hoverrows:false,
		gridComplete:function()
		{
			$("#multimedia_list_table .ui-jqgrid-titlebar").hide();	//Hide the caption
			$("#search_hint_div").hide();
			var ids = $("#multimedia_list_table").jqGrid('getDataIDs');
			for(var i=0;i<ids.length;i++)
			{
				var cl=ids[i]; //So this is the id
				$("#add_recording_a_"+cl).bind('click',{recordingId:cl},function(event){
					$("#add_recording_form_recording_id").val(event.data.recordingId);
					//$("#add_recording_form_text").val() //Yunjia: Add support for search text later, also in the controller
					$("#add_recording_dialog").wijdialog('open');
				});
			}
		}
	}); 
	$("#multimedia_list_table").jqGrid('navGrid','#multimedia_pager',{edit:false,add:false,del:false,view:true,search:false},{},{},{},{},{});
}

function loadGroupRecording() 
{
	var multimedia_list_url = g.createLink({controller:"userGroup",action:"listGroupRecordingsAjax"});
	
	//console.log(multimedia_list_url);
	$("#group_recording_list_table").jqGrid({ 
		url:multimedia_list_url+"/"+"${userGroup?.id}", 
		datatype: "json",
	//	data:"text=test", 
		jsonReader: { repeatitems: false },
		colNames:['Group Recording Id','Title','Group Permission','Action'], //Yunjia: Add create and update later, to do this , we have to change the database 
		colModel:[ {name:'id', index:'id', width:60}, 
					{name:'title',sortable:false}, //Yunjia title is not sortable 
					{name:'act',sortable:false, formatter:actGroupRecordingFormatter} ,
					{name:'perm',index:'perm', width:70}
					
				],
		rowNum:10, 
		rowList:[10,30,50], 
		pager: '#group_recording_pager', 
		sortname: 'id',
		width:950, 
		viewrecords: true, 
		emptyrecords:"No recordings for this group.",
		height:"auto",
		sortorder: "desc",
		hoverrows:false,
		gridComplete:function()
		{
			$("#group_recording_list_table .ui-jqgrid-titlebar").hide();	//Hide the caption
		}
	}); 
	$("#group_recording_list_table").jqGrid('navGrid','#multimedia_pager',{edit:false,add:false,del:false,view:true,search:false},{},{},{},{},{});
}

function actListMultimediaFormatter(cellvalue,options,rowObject)
{
	if(rowObject.canAdd == true)
		return "<span class='group'><a href='#' class='addRecording' id='add_recording_a_"+rowObject.id+"'>Add</a></span>";
	else
		return "Recording is already in the group" 
}

function actGroupRecordingFormatter(cellvalue,options,rowObject)
{
	
	return "<span class='group'><a href='"+g.createLink({controller:'userGroup',action:'deletePermission'})+"/"+
		rowObject.id+"' onclick='return confirm(\""+"Are you sure?"+"\");"+"' class='removeRecording'>Remove</a></span>";
}

</script>
</head>
<body>
<div class="span-24" id="user_nav">
	<g:render template="/common/userNav"/>
</div>
<div class="span-24" id="user_content">
	<div class="span-24">
		<h1><g:message code="org.synote.user.group.editPermission.title"/></h1>
		<g:render template="/common/message" />
	</div>
	<div id="user_key_word_search" class="span-24" style="padding-bottom:5px;">
		<div class="span-6 left">
			<g:link controller="userGroup" action="show" id="${userGroup?.id}" elementId="group_show_back_button">Back to Group Detail</g:link>
		</div>
		<div class="span-13 prepend-5 last right">
			<label for="text">Please enter the title of the recording:</label>
			<input type="textbox" name="resource_search_text" id="resource_search_text" value="${params.text}" style="font-size:1.1em;padding:0.4em;"/>
			<button id="resource_search_button" title="search key words">Search</button>
		</div>
	</div>
	<!-- Yunjia: Haven't implemented the filter function yet, we should be able to search by keywords -->
	<hr/>
	<!-- Yunjia: need to change the card view to something better -->
	<h2>Search Results</h2>
	<div id="search_hint_div">Please enter the key words to search recordings.</div>
	<div id="multimedia_list_div_outer" class="span-24">
		<div id="multimedia_list_div">
			<table id="multimedia_list_table"></table>
			<div id="multimedia_pager"></div>
		</div>
	</div>
	<h2>Group Recording List</h2>
	<div id="group_recording_list_div_outer" class="span-24">
		<div id="group_recording_list_div">
			<table id="group_recording_list_table"></table>
			<div id="group_recording_pager"></div>
		</div>
	</div>
	<div id="add_recording_dialog">
		<g:form controller="userGroup" action="savePermission" method="post" class="uniForm" id="${userGroup?.id}">
			<input type="hidden" id="add_recording_form_recording_id" name="recordingId" value="" />
			<!--  <input type="hidden" id="add_recording_form_text" name="text" value="" />-->
			<div class="ctrlHolder inlineLabels">
				<label for="perm">Select a permission:</label>
				<g:render template="/common/permission" model="[canPrivate:true, selectorClass:'selectInput medium']" />
				<p class="formHint">Select the permission for this recording. Default is ANNOTATE</p>
			</div>
			<div class="prepend-top append-bottom">
				<input id="add_recording_permission_submit" type="submit" value="Save" />
				<input id="add_recording_permission_cancel" type="reset" value="Cancel"/>
			</div>
		</g:form>
	</div>
</div>
</body>
</html>
