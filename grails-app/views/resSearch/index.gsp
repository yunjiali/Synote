<%@ page import="org.synote.search.resource.*"%>
<%@ page import="org.synote.resource.compound.*"%>

<html>
<head>
<title><g:message code="org.synote.search.resource.index.title" /></title>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta name="layout" content="main" />
<link rel="stylesheet" href="${resource(dir: 'css', file: 'search.css')}" />
<script type="text/javascript" src="${resource(dir:'js/jquery',file:'jquery.highlight.js')}"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$(".search-resource-type-li").removeClass("active");
		var resource_type_input = $(".form-search #search_resource_type");
		if(resource_type_input.val() == "" || resource_type_input.val() == 'all')
		{
			$("#search_resource_type_all").addClass("active");
		}
		else if(resource_type_input.val() == 'multimedia')
		{
			$("#search_resource_type_multimedia").addClass("active");
		}
		else if(resource_type_input.val() == 'synmark')
		{
			$("#search_resource_type_synmark").addClass("active");
		}
		else if(resource_type_input.val() == 'transcript')
		{
			$("#search_resource_type_transcript").addClass("active");
		}
		else
		{
			$("#search_resource_type_all").addClass("active");
		}

		$("#search_resource_type_all_a").click(function(){
			resource_type_input.val("all");
			$(".form-search").submit();
		});
		$("#search_resource_type_multimedia_a").click(function(){
			resource_type_input.val("multimedia");
			$(".form-search").submit();
		});
		$("#search_resource_type_synmark_a").click(function(){
			resource_type_input.val("synmark");
			$(".form-search").submit();
		});
		$("#search_resource_type_transcript_a").click(function(){
			resource_type_input.val("synmark");
			$(".form-search").submit();
		});

		//highlight search terms
		var search_term = $(".form-search #search_textbox").val();
		if(search_term != "")
		{
			$("#search_result_list_div").highlight(search_term,{className:'search-highlight'});
		}
	});
</script>
</head>
<body>
<div class="container">
	<div class="row">
		<div class="span2" id="user_nav_div">
			<h3>Resource Type</h3>
			<ul class="nav nav-pills nav-stacked">
				<li class="search-resource-type-li" id="search_resource_type_all" class="active"><a href="#" id="search_resource_type_all_a">All Type</a></li>
				<li class="search-resource-type-li" id="search_resource_type_multimedia"><a href="#" id="search_resource_type_multimedia_a">Multimedia</a></li>
				<li class="search-resource-type-li" id="search_resource_type_synmark"><a href="#" id="search_resource_type_synmark_a">Synmark</a></li>
				<li class="search-resource-type-li" id="search_resource_type_transcript"><a href="#" id="search_resource_type_transcript_a">Transcript</a></li>
			</ul>
		</div>
		<div class="span10" id="user_content_div">
			<h2 class="heading-inline"><g:message code="org.synote.search.resource.index.title" /></h2>
			<span id="recording_count_span" style="padding:5px" class="pull-right label label-info">Page ${searchResultList?.records >0?searchResultList?.page:0} of ${searchResultList?.records} results</span>
			<g:render template="/common/message" />
			<div>
				<div id="search_result_list_div">
					<g:if test="${searchResultList?.rows?.size() == 0}">
						<div class="nodata">No result is found for <strong>${params?.query?.encodeAsHTML()}</strong></div>
					</g:if>
					<g:each in="${searchResultList?.rows}" var="row">
						<g:if test="${row.perm_val > 0 }">
							<g:if test="${row.clazz=='multimedia'}">
								<g:render template="/common/recording" model="['row':row,'actionEnabled':true, 'viewTranscriptsEnabled':false,'viewSynmarksEnabled':false]"/>
							</g:if>
							<g:elseif test="${row.clazz=='synmark'}">
								<g:render template="/common/synmark" model="['row':row]"/>
							</g:elseif>
							<g:elseif test="${row.clazz=='webvttcue'}">
								<g:render template="/common/subtitle_block" model="['row':row]"/>
							</g:elseif>
						</g:if>
						<g:else>
							<div class="recording-row row">
							 	<div class="span2">
							 		<span>Private resource.</span>
							 	</div>
							</div>
						</g:else>
					</g:each>
				</div>
			</div>
			<div class="row" id="search_pagination">
				<g:render template="/common/pagination" 
					model="['query': params.query, 'currentPage':searchResultList?.page,'rows':params.rows,'ctrl':'resSearch', 'act':'index', 'total':searchResultList?.total]"/>
			</div>
		</div>
	</div>
</div>
</body>
</html>
