<g:form class="form-search" name="searchableForm"  controller="resSearch" method="get" style="padding:19px 0px;min-height:20px;margin-bottom:0px;">
    <input type="text" class="span5" id="search_textbox" name="query" maxlength="255" value="${params?.query?.encodeAsHTML()}" />
    <input type="submit" class="btn" title="Search" value="Search" />
    <g:link style="color:#3366cc; margin-left:6px;" id="advanced_search_a" controller="resSearch" action="advancedSearch">Advanced Search</g:link>
		&nbsp;|&nbsp;
	<g:link style="color:#3366cc; " class="search" controller="resSearch" action="help" title="Search Help">Search Help</g:link>
</g:form>

<!-- 
<g:form class="uniform_aristo" name="searchableForm" controller="resSearch"
	method="get">
	<div class="span-18 margin-top:1em" style="padding-left:16px;">
		<input title="You can enter key words to find resources users and groups" style="font-size:1.1em;padding:0.4em;" id="search_textbox" type="text" name="query" size="40" maxlength="255" value="${params?.query?.encodeAsHTML()}" />
		<input id="search_button" type="submit" value="Search" title="search" />
		
	</div>
	<div class="span-13 prepend-1">
		<span style="font-weight:bold">Search for:</span>
		<input type="radio" name="starget" id="search_target_resources" value="resources" checked="${params.starget == 'resources'?true:false}"/>
		<label style="font-weight:normal" for="search_target_resources">Resources</label>
		<input type="radio" name="starget" id="search_target_users" value="users" checked=""${params.starget == 'users'?true:false}"/>
		<label style="font-weight:normal" for="search_target_users">Users</label>
		<input type="radio" name="starget" id="search_target_groups" value="groups" checked="${params.starget == 'groups'?true:false}"/>
		<label style="font-weight:normal" for="search_target_groups">Groups</label>
	</div>			
</g:form>-->