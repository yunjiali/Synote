<html>
<head>
<title><g:message code="org.synote.user.listTags.title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<g:urlMappings/>
<link rel="stylesheet" type="text/css" href="${resource(dir:'css/jquery',file:"jqcloud.css")}"/>
<style type="text/css">
.tag {
    background: none repeat scroll 0 0 #CDE69C;
    border: 1px solid #A5D24A;
    border-radius: 4px 4px 4px 4px;
    color: #638421;
    display: block;
    float: left;
    font-size: 1.2em;
    margin-bottom: 5px;
    margin-right: 5px;
    padding: 5px;
    text-decoration: none;
}
.tag a {
    text-decoration: none;
}
</style>
</head>
<body>
<div class="container">
	<div class="row">
		<div class="span2" id="user_nav_div">
			<g:render template="/common/userNav" model="['active':'tags']"/>
		</div>
		<div class="span10" id="user_content_div">
			<h2 class="heading-inline">My Tags</h2>
			<span id="recording_count_span" style="padding:5px" class="pull-right label label-info">${tags.size()} Tags</span>
			<hr/>
			<div class="span10">
				<g:each in="${tags}" status="i" var="tag">
					<span class="tag" style="font-size:1.2em;">
						<g:link controller="resSearch" action="search" params='[query:"${tag.text}"]'> <!-- Search my resource only! -->
							<i class="icon-tag tag-item icon-white"></i>${tag.text}(${tag.weight})</g:link>
					</span>
				</g:each>
			</div>
			<div class="span10">
				<br/>
				<g:render template="/common/tagCloud"/>
			</div>
		</div>
	</div>
</div>

</body>
</html>
