<html>
<head>
<title><g:message code="org.synote.user.group.editMember.title" /></title>
<meta name="layout" content="main" />
<script type="text/javascript" src="${resource(dir:'js/i18n',file:"grid.locale-en.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.jqGrid.min.js")}"></script>
<link rel="stylesheet" href="${resource(dir:'css/jquery',file:"ui.jqgrid.css")}" media="screen, projection" />
<style type="text/css">
	.group_member_table_cell
	{
		border:0px;
	}
</style>
<script type="text/javascript">
	//Yunjia: more work on this page
	//1. format the datatime created and updated
	//2. think about adding recording title to the synmarklist
	$(document).ready(function(){
		$("#user_search_button").button({
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
		$("#user_search_button").click(function()
		{
			//console.log("clickbutton");
			var sText = $("#user_search_text").val();
			//console.log("stext:"+sText);
			if(sText!=null&&$.trim(sText).length>0)
			{
				$("#user_search_list_table").jqGrid("GridUnload");
				loadUserSearchList(sText);
			}
		});
		$("#user_search_text").keyup(function(event){
  				if(event.keyCode == 13){
    			$("#user_search_button").click();
  			}
		});
		var searchText = $("#user_search_text").val();
		if(searchText!=null&&$.trim(searchText).length>0)
		{
			loadUserSearchList(searchText);
		}
		loadGroupMemberList(); //Not the search result list
		//Hide the search_hint_div
	});
	
function loadUserSearchList(searchText)
{
	var user_search_list_url = g.createLink({controller:"userGroup",action:"listPossibleUsersAjax"});
	user_search_list_url += "/"+"${userGroup?.id}"+"?text="+searchText;
	//console.log(group_list_url);
	$("#user_search_list_table").jqGrid({ 
		url:user_search_list_url, 
		datatype: "json",
		jsonReader: { repeatitems: false },
		colNames:['Id','User Name','First Name','Last Name','Action'],
		colModel:[ {name:'id', index:'id', width:40,classes:"group_member_table_cell"}, //id is user id
					{name:'userName',index:'userName',classes:"group_member_table_cell"}, 
					{name:'firstName',index:'firstName',classes:"group_member_table_cell"},
					{name:'lastName',index:'lastName',classes:"group_member_table_cell"},
					{name:'act',sortable:false,formatter:actUserSearchFormatter,classes:"group_member_table_cell"}
				],
		rowNum:10, 
		rowList:[10,30,50], 
		pager: '#user_search_pager', 
		sortname: 'id',
		width:950, 
		viewrecords: true, 
		emptyrecords:"No user is found",
		height:"auto",
		sortorder: "desc",
		hoverrows:false,
		gridComplete:function()
		{
			$("#group_list_table .ui-jqgrid-titlebar").hide();	//Hide the caption
			$("#search_hint_div").hide();
		}
	}); 
	$("#user_search_list_table").jqGrid('navGrid','#user_search_pager',{edit:false,add:false,del:false,view:false,search:false},{},{},{},{},{});
}
function loadGroupMemberList() 
{
	//var viewOpts = {caption:"Show recording details",width:500};
	var group_member_list_url = g.createLink({controller:"userGroup",action:"listGroupMembersAjax"});
	//console.log(group_member_list_url);
	//var caption = "My Groups";
	//console.log(group_list_url);
	$("#group_member_list_table").jqGrid({ 
		url:group_member_list_url+"/"+"${userGroup?.id}", 
		datatype: "json",
		jsonReader: { repeatitems: false },
		colNames:['Member Id','User Name','First Name','Last Name','Action'],
		colModel:[ {name:'id', index:'id', width:40,classes:"group_member_table_cell"}, //id is the member id, not user id or group id
					{name:'userName',index:'userName',classes:"group_member_table_cell"}, 
					{name:'firstName',index:'firstName',classes:"group_member_table_cell"},
					{name:'lastName',index:'lastName',classes:"group_member_table_cell"},
					{name:'act',sortable:false,formatter:actGroupMemberFormatter,classes:"group_member_table_cell"}
				],
		rowNum:10, 
		rowList:[10,30,50], 
		pager: '#group_member_pager', 
		sortname: 'id',
		width:950, 
		viewrecords: true, 
		emptyrecords:"There is no member in the group",
		height:"auto",
		sortorder: "desc",
		hoverrows:false,
		gridComplete:function()
		{
			//Hide the caption
			$("#group_member_list_table .ui-jqgrid-titlebar").hide();
		}
	}); 
	$("#group_member_list_table").jqGrid('navGrid','#group_pager',{edit:false,add:false,del:false,view:false,search:false},{},{},{},{},{});
}

function actUserSearchFormatter(cellvalue,options,rowObject)
{
	if(rowObject.canAdd == true)
		return "<span class='group'><a href='"+g.createLink({controller:'userGroup',action:'saveMember'})+"/"+"${userGroup.id}"+
			"?userId="+rowObject.id+"&text="+$("#user_search_text").val()+"' onclick='return confirm(\""+"Are you sure?"+"\");'class='join'>Add</a></span>";
	else
		return "User has already in the group"
				
}


function actGroupMemberFormatter(cellvalue,options,rowObject)
{
	return "<span class='group'><a href='"+g.createLink({controller:'userGroup',action:'deleteMember'})+"/"+rowObject.id+
		"' onclick='return confirm(\""+"Are you sure?"+"\");' class='removeUser'>Remove</a></span>";
}
</script>
</head>
<body>
<div class="span-24" id="user_nav">
	<g:render template="/common/userNav"/>
</div>
<div class="span-24" id="user_content">
	<div class="span-24">
		<h1><g:message code="org.synote.user.group.editMember.title"/> </h1>
		<g:render template="/common/message" /> 
	</div>
	<div id="user_key_word_search" class="span-24" style="padding-bottom:5px;">
		<div class="span-6 left">
			<g:link controller="userGroup" action="show" id="${userGroup.id}" elementId="group_show_back_button">Back to Group Detail</g:link>
		</div>
		<div class="span-14 prepend-4 last right">
			<label for="text">Please enter the user name, first or last name:</label>
			<input type="textbox" name="group_search_text" id="user_search_text" value="${params.text}" style="font-size:1.1em;padding:0.4em;"/>
			<button id="user_search_button" title="search user">Search</button>
		</div>
	</div>
	<hr/>
	<h2>Search Results</h2>
	<div id="user_search_list_div_outer" class="span-24">
		<div id="user_search_list_div">
			<div id="search_hint_div">Please enter the key words to search users and add them into this group.</div>
			<table id="user_search_list_table"></table>
			<div id="user_search_pager"></div>
		</div>
	</div>
	<h2>Group Member List</h2>
	<div id="group_member_list_div_outer" class="span-24">
		<div id="group_member_list_div">
			<table id="group_member_list_table"></table>
			<div id="group_member_pager"></div>
		</div>
	</div>
</div>
</body>
</html>
