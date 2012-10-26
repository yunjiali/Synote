<g:form class="form-search" name="searchableForm"  controller="resSearch" method="get" style="padding:19px 0px;min-height:20px;margin-bottom:0px;">
    <input type="text" class="span5" id="search_textbox" name="query" maxlength="255" value="${params?.query?.encodeAsHTML()}" />
    <input type="hidden" id="search_resource_type" name="type" value="${params?.type}"/>
    <input type="submit" class="btn" title="Search" value="Search" />
    <!-- 
    <g:link style="color:#3366cc; margin-left:6px;" id="advanced_search_a" controller="resSearch" action="advancedSearch">Advanced Search</g:link>
		&nbsp;|&nbsp;
	<g:link style="color:#3366cc; " class="search" controller="resSearch" action="help" title="Search Help">Search Help</g:link>
	-->
</g:form>