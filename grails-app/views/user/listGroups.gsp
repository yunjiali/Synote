<html>
<head>
<title><g:message code="org.synote.user.listGroups.title" /></title>
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
		$("#group_search_button").button({
			icons:{
				primary:"ui-icon-search"	
			},
			text:false
		});
		$("#group_search_button").click(function()
			{
				//console.log("clickbutton");
				var sText = $("#group_search_text").val();
				//console.log("stext:"+sText);
				$("#group_list_table").jqGrid("GridUnload");
				loadGroupList(sText);
			});
		$("#group_search_text").keyup(function(event){
  				if(event.keyCode == 13){
    			$("#group_search_button").click();
  			}
		});
		var searchText = "${params.text}";
		loadGroupList(searchText);
	});
	

function loadGroupList(searchText) 
{
	var viewOpts = {caption:"Show recording details",width:500};
	var group_list_url = g.createLink({controller:"user",action:"listGroupsAjax"});
	var caption = "My Groups";
	if(searchText!=null&&$.trim(searchText).length>0)
	{
		group_list_url += "?text="+searchText;
		caption += " (search results for term '"+(searchText.length>15?(searchText.substring(0,11)+"..."):searchText)+"')";
	}
	//console.log(group_list_url);
	$("#group_list_table").jqGrid({ 
		url:group_list_url, 
		datatype: "json",
		jsonReader: { repeatitems: false },
		colNames:['Id','Group Name','Action','Shared','Members','Recordings'], //Yunjia: 'Description','Members",'Recordings','Created','Updated', to do this , we have to change the database 
		colModel:[ {name:'id', index:'id', width:40}, 
					{name:'name',index:'title'}, 
					{name:'act', width:60,sortable:false},
					{name:'shared',index:'shared'},
					{name:'member_count',sortable:false},
					{name:'recording_count',sortable:false}
				],
		rowNum:10, 
		rowList:[10,30,50], 
		pager: '#group_pager', 
		sortname: 'id',
		width:950, 
		viewrecords: true, 
		emptyrecords:"You don't have any group yet.",
		height:"auto",
		sortorder: "desc",
		hoverrows:false,
		caption:caption,
		gridComplete:function()
		{
			var ids = $("#group_list_table").jqGrid('getDataIDs');
			for(var i=0;i<ids.length;i++)
			{
				var cl=ids[i]; //So this is the id
				var show_link =  $("<a/>",{
					href:g.createLink({controller:"userGroup",action:"show"})+"/"+cl,
					id:"show_group_link_"+cl,
					title:"show details of this group",
					text:"Details",
				}).addClass("jqGrid_action_link");
				
				var show_link_str = $('<div>').append($(show_link).clone()).remove().html();
				$("#group_list_table").jqGrid('setRowData',ids[i],{act:show_link_str});
				//$("#show_group_link_"+cl).bind('click',{rowId:cl}, function(event){
					//console.log("cl:"+event.data.rowId);
				//	$("#group_list_table").jqGrid('viewGridRow',event.data.rowId,viewOpts);
			//});
			}
		}
	}); 
	$("#group_list_table").jqGrid('navGrid','#group_pager',{edit:false,add:false,del:false,view:false,search:false},{},{},{},{},viewOpts);
}
</script>
</head>
<body>
<div class="span-24" id="user_nav">
	<g:render template="/common/userNav"/>
</div>
<div class="span-24" id="user_content">
	<h1><g:message code="org.synote.user.listGroups.title" /></h1>
	<g:render template="/common/message" />
	<div id="group_key_word_search" class="right" style="padding-bottom:5px;">
		<label for="text">Please Enter the Name of the Group:</label>
		<input type="textbox" name="group_search_text" id="group_search_text" value="${params.text}" style="font-size:1.1em;padding:0.4em;"/>
		<button id="group_search_button" title="search group">Search</button>
	</div>
	<hr/>
	<!-- Yunjia: need to change the card view to something better -->
	<div id="group_list_div_outer" class="span-24">
		<div id="group_list_div">
			<table id="group_list_table"></table>
			<div id="group_pager"></div>
		</div>
	</div>
</div>
</body>
</html>
