<html>
<head>
<title><g:message code="org.synote.user.listResources.title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<script type="text/javascript" src="${resource(dir:'js/i18n',file:"grid.locale-en.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.jqGrid.min.js")}"></script>
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
		$("#resource_search_button").click(function()
			{
				//console.log("clickbutton");
				var sText = $("#resource_search_text").val();
				//console.log("stext:"+sText);
				$("#multimedia_list_table").jqGrid("GridUnload");
				loadMultimediaResource(sText);
				$("#synmark_list_table").jqGrid("GridUnload");
				loadSynmarkResource(sText);
			});
		$("#resource_search_text").keyup(function(event){
  				if(event.keyCode == 13){
    			$("#resource_search_button").click();
  			}
		});
		var searchText = "${params.text}";
		loadMultimediaResource(searchText);
		loadSynmarkResource(searchText);
		
		//Yunjia: Later, we can add the groups you belong to
	});
	

function loadMultimediaResource(searchText) 
{
	var viewOpts = {caption:"Show recording details",width:500};
	var multimedia_list_url = g.createLink({controller:"user",action:"listMultimediaAjax"});
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
		colNames:['Id','Title','Action','URL Address','Public Permission','Created','Updated'], //Yunjia: Add create and update later, to do this , we have to change the database 
		colModel:[ {name:'id', index:'id', width:40}, 
					{name:'title',index:'title'}, 
					{name:'act', width:120,sortable:false},
					{name:'url',editable:true, editrules:{required:true, edithidden:true}, hidden:true, editoptions:{ dataInit: function(element) { $(element).attr("readonly", "readonly"); } }}, 
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
		emptyrecords:"No recording is found",
		height:"auto",
		sortorder: "desc",
		hoverrows:false,
		caption:caption,
		gridComplete:function()
		{
			var ids = $("#multimedia_list_table").jqGrid('getDataIDs');
			for(var i=0;i<ids.length;i++)
			{
				var cl=ids[i]; //So this is the id
				var play_url = g.createLink({controller:'recording',action:'replay',params:{id:cl}});
				var play_link = $("<a/>",{
					href:play_url,
					title:"play this recording",
					target:"_blank",
					text:"Play"
				}).addClass("jqGrid_action_link");
				var show_url = g.createLink({controller:'multimediaResource', action:'show',params:{id:cl}})
				var show_link =  $("<a/>",{
					href:show_url,
					id:"show_multimedia_link_"+cl,
					title:"show details of this recording",
					text:"Details",
				}).addClass("jqGrid_action_link");
				var edit_url = g.createLink({controller:'multimediaResource', action:'edit',params:{id:cl}})
				var edit_link =  $("<a/>",{
					href:edit_url,
					id:"edit_multimedia_link_"+cl,
					title:"Edit this recording",
					text:"Edit",
				}).addClass("jqGrid_action_link");
				
				var print_url = g.createLink({controller:'recording', action:'print',params:{id:cl}})
				var print_link =  $("<a/>",{
					href:print_url,
					title:"Show print friendly version",
					target:"_blank",
					text:"Print Friendly"
				}).addClass("jqGrid_action_link");
				var play_link_str = $('<div>').append($(play_link).clone()).remove().html();
				var show_link_str = $('<div>').append($(show_link).clone()).remove().html();
				var edit_link_str = $('<div>').append($(edit_link).clone()).remove().html();
				var print_link_str = $('<div>').append($(print_link).clone()).remove().html();
				$("#multimedia_list_table").jqGrid('setRowData',ids[i],{act:play_link_str+show_link_str+edit_link_str+print_link_str});
			}
		}
	}); 
	$("#multimedia_list_table").jqGrid('navGrid','#multimedia_pager',{edit:false,add:false,del:false,view:false,search:false},{},{},{},{},viewOpts);
}
function loadSynmarkResource(searchText) 
{
	//Yunjia: Add start and end time for synmark later
	var viewOpts = {caption:"Show synnmark details",width:500};
	var synmark_list_url = g.createLink({controller:"user",action:"listSynmarkAjax"});
	var caption = "My Synmarks";
	if(searchText!=null&&$.trim(searchText).length>0)
	{
		synmark_list_url += "?text="+searchText;
		caption += " (search results for term '"+(searchText.length>15?(searchText.substring(0,11)+"..."):searchText)+"')";
	}
	//console.log(synmark_list_url);
	$("#synmark_list_table").jqGrid({ 
		url:synmark_list_url, 
		datatype: "json", 
		jsonReader: { repeatitems: false },
		colNames:['Id','Title','Action','Tags','Note','Recording Title','Created','Updated'], //Yunjia: Add create and update later, to do this , we have to change the database 
		colModel:[ {name:'id', index:'id', width:40}, 
					{name:'title',index:'title'}, 
					{name:'act', width:60,sortable:false},
					{name:'tags',index:'tags', width:100},
					{name:'note',editable:true, editrules:{required:true, edithidden:true}, hidden:true, editoptions:{ dataInit: function(element) { $(element).attr("readonly", "readonly"); } }},
					{name:'rtitle',editable:true, editrules:{required:true, edithidden:true}, hidden:true, editoptions:{ dataInit: function(element) { $(element).attr("readonly", "readonly"); } }},  
					{name:'date_created', index:'date_Created', width:60, align:"center"},
					{name:'last_updated', index:'last_Updated', width:60, align:"center"}
				],
		rowNum:10, 
		rowList:[10,30,50], 
		pager: '#synmark_pager', 
		sortname: 'id', 
		viewrecords: true,
		emptyrecords:"No Synmark is found.",
		width:950, 
		height:"auto",
		sortorder: "desc",
		hoverrows:false,
		caption:caption,
		gridComplete:function()
		{
			var ids = $("#synmark_list_table").jqGrid('getDataIDs');
			for(var i=0;i<ids.length;i++)
			{
				var cl=ids[i]; //So this is the id
				var play_url = g.createLink({controller:'linkedData',action:'resources',params:{id:cl}});
				var play_link = $("<a/>",{
					href:play_url,
					title:"play this recording",
					target:"_blank",
					text:"Play"
				}).addClass("jqGrid_action_link");
				//var show_url = g.createLink({controller:'user', action:'showSynmark',params:{id:cl}})
				var show_link =  $("<a/>",{
					href:"#synmark_list_table",
					id:"show_synmark_link_"+cl,
					title:"show details of this recording",
					text:"Details"
				}).addClass("jqGrid_action_link");
				var play_link_str = $('<div>').append($(play_link).clone()).remove().html();
				var show_link_str = $('<div>').append($(show_link).clone()).remove().html();
				$("#synmark_list_table").jqGrid('setRowData',ids[i],{act:play_link_str+show_link_str});
				$("#show_synmark_link_"+cl).bind('click',{rowId:cl}, function(event){
						//console.log("cl:"+event.data.rowId);
						$("#synmark_list_table").jqGrid('viewGridRow',event.data.rowId,viewOpts);
				});
			}
		}
	}); 
	$("#synmark_list_table").jqGrid('navGrid','#synmark_pager',{edit:false,add:false,del:false,view:true,search:false},{},{},{},{},viewOpts);
}
</script>
</head>
<body>
<div class="span-24" id="user_nav">
	<g:render template="/common/userNav"/>
</div>
<div class="span-24" id="user_content">
	<div class="span-24">
		<h1><g:message code="org.synote.user.listResources.title"/></h1>
		<g:render template="/common/message" />
		<!-- Yunjia: Haven't implemented the filter function yet, we should be able to search by keywords -->
		<div id="resource_key_word_search" class="right" style="padding-bottom:5px;">
			<label for="text">Please Enter Keywords:</label>
			<input type="textbox" name="resource_search_text" id="resource_search_text" value="${params.text}" style="font-size:1.1em;padding:0.4em;"/>
			<button id="resource_search_button" title="search key words">Search</button>
		</div>
		<hr/>
		<!-- Yunjia: need to change the card view to something better -->
		<div id="multimedia_list_div_outer" class="span-24">
			<div id="multimedia_list_div">
				<table id="multimedia_list_table"></table>
				<div id="multimedia_pager"></div>
			</div>
		</div>
		<div id="synmark_list_div_outer" class="span-24 prepend-top">
			<div id="synmark_list_div">
				<table id="synmark_list_table"></table>
				<div id="synmark_pager"></div>
			</div>
		</div>
	</div>
</div>

</body>
</html>
