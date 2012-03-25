<html>
<head>
<title><g:message code="org.synote.user.group.list.title" /></title>
<meta name="layout" content="main" />
<script type="text/javascript" src="${resource(dir:'js/i18n',file:"grid.locale-en.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.jqGrid.min.js")}"></script>
<link rel="stylesheet" href="${resource(dir:'css/jquery',file:"ui.jqgrid.css")}" media="screen, projection" />
<script type="text/javascript">
	//Yunjia: more work on this page
	//1. format the datatime created and updated
	//2. think about adding recording title to the synmarklist
	$(document).ready(function(){
		loadGroupList();
	});
	

function loadGroupList() 
{
	var viewOpts = {caption:"Show recording details",width:500};
	var group_list_url = g.createLink({controller:"userGroup",action:"listGroupsAjax"});
	var caption = "Group List";
	//console.log(group_list_url);
	$("#group_list_table").jqGrid({ 
		url:group_list_url, 
		datatype: "json",
		jsonReader: { repeatitems: false },
		colNames:['Id','Group Name','Shared','owner', 'Members','Recordings','Action','Join'], //Yunjia: 'Description','Members",'Recordings','Created','Updated', to do this , we have to change the database 
		colModel:[ {name:'id', index:'id', width:40}, 
					{name:'name',index:'title'}, 
					{name:'shared',index:'shared'},
					{name:'owner_name', sortable:false},//Yunjia: owner is not sortable, I want to make it sortable
					{name:'member_count',sortable:false},
					{name:'recording_count',sortable:false},
					{name:'act', width:60,sortable:false},
					{name:'joined', sortable:false, formatter:joinFormatter}
				],
		rowNum:10, 
		rowList:[10,30,50], 
		pager: '#group_pager', 
		sortname: 'id',
		width:950, 
		viewrecords: true, 
		emptyrecords:"No user group is found",
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

function joinFormatter(cellvalue,options,rowObject)
{
	//<span class="group"><a class="join" value="Edit" /></span>
	if(cellvalue == false)
	{
		return "<span class='group'><a href='"+g.createLink({controller:'userGroup',action:'joinGroup'})+"/"+rowObject.id+"' class='join'>Join</a></span>";
	}
	else
	{
		return "You are in the group"
	}
}
</script>
</head>
<body>
<g:isLoggedIn>
	<g:set var="isLoggedIn" value="true" />
	<div class="span-24" id="user_nav">
		<g:render template="/common/userNav"/>
	</div>
</g:isLoggedIn>
<g:isNotLoggedIn>
	<g:set var="isLoggedIn" value="false" />
</g:isNotLoggedIn>
<div class="span-24" id="user_content">
	<h1>Group List</h1>
	<g:render template="/common/message" />
	<div class="span-24 prepend-top" id="user_content">
		<!-- Yunjia: need to change the card view to something better -->
		<div id="group_list_div_outer" class="span-24">
			<div id="group_list_div">
				<table id="group_list_table"></table>
				<div id="group_pager"></div>
			</div>
		</div>
	</div>
</div>
</body>
</html>
