<html>
<head>
<title><g:message
	code="org.synote.resource.compound.multimediaResource.list.title" /></title>
<meta name="layout" content="main" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<script type="text/javascript" src="${resource(dir:'js/i18n',file:"grid.locale-en.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.jqGrid.min.js")}"></script>
<link rel="stylesheet" href="${resource(dir:'css/jquery',file:"ui.jqgrid.css")}" media="screen, projection" />

<script type="text/javascript">
	$(document).ready(function(){
		//var isLoggedIn = Boolean("${isLoggedIn}")
		var viewOpts = {caption:"Show recording details",width:500};
		//console.log("isLoggedIn"+isLoggedIn);
		var multimedia_list_url = g.createLink({controller:'multimediaResource',action:'listMultimediaAjax'});
		jQuery("#multimedia_list").jqGrid({ 
			url:multimedia_list_url, 
			datatype: "json", 
			jsonReader: { repeatitems: false },
			colNames:['Id','Title','Action','URL Address','Owner','Permission'], //Yunjia: Add create and update later, to do this , we have to change the database 
			colModel:[ {name:'id', index:'id', width:40}, 
						{name:'title',index:'title'},
						{name:'act', width:100, sortable:false},
						{name:'url',editable:true, editrules:{required:true, edithidden:true}, hidden:true, editoptions:{ dataInit: function(element) { $(element).attr("readonly", "readonly"); } }}, 
						{name:'owner_name',index:'owner_name',width:100}, 
						{name:'perm_name', index:'perm_val', width:60, align:"center"}
					],
			rowNum:10, 
			rowList:[10,30,50,100], 
			pager: '#multimedia_pager', 
			sortname: 'id', 
			viewrecords: true,
			emptyrecords:"No recording is found",
			width:870, 
			height:"auto",
			sortorder: "desc",
			hoverrows:false,
			gridComplete:function()
			{
				var ids = $("#multimedia_list").jqGrid('getDataIDs');
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
						id:"show_link_"+cl,
						title:"show details of this recording",
						//target:"_blank",
						text:"Details",
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
					var print_link_str = $('<div>').append($(print_link).clone()).remove().html();
					$("#multimedia_list").jqGrid('setRowData',ids[i],{act:play_link_str+show_link_str+print_link_str});
					//var position = $("#show_link_"+cl).position(); 
				}
			}
		}); 
		$("#multimedia_list").jqGrid('navGrid','#multimedia_pager',{edit:false,add:false,del:false,search:false,view:false},{},{},{},{},viewOpts);
	});
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
	<div class="span-22 prepend-1 append-1">
	<h1>Recording List</h1>
	<g:render template="/common/message" />
	<div>
		<table id="multimedia_list"></table>
		<div id="multimedia_pager"></div>
	</div>
</div>
</div>
</body>
</html>
