<html>
<head>
<title><g:message code="org.synote.user.listTags.title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<link rel="stylesheet" type="text/css" href="${resource(dir:'css/jquery',file:"jqcloud.css")}" media="screen,projection"/>
<script type="text/javascript">
	$(document).ready(function(){
		
		$("#user_tag_div").wijexpander({allowExpand:true});
	});
</script>
</head>
<body>
<div class="span-24" id="user_nav">
	<g:render template="/common/userNav"/>
</div>
<div class="span-24" id="user_content">
	<h1><g:message code="org.synote.user.listTags.title" /></h1>
	<div id="user_tag_div" class="prepend-top">
		<h2>The tags I use</h2>
		<div>
			<g:each in="${tags}" status="i" var="tag">
				<span class="tag">
					<g:link controller="user" action="listSynmarks" params='[text:"${tag.text}"]'>${tag.text}(${tag.weight})</g:link>
				</span>
			</g:each>
		</div>
	</div>
	<div class="span-24 prepend-top append-bottom">
		<g:render template="/common/tagCloud"/>
	</div>
</div>

</body>
</html>
