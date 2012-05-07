<ul class="nav nav-pills nav-stacked">
	<li class="nav-header">Menu</li>
	<g:if test="${active == 'user_profile'}">
    	<li class="active">
    </g:if>
    <g:else>
    	<li>
    </g:else><g:link controller="user" action="showUserProfile" title="show user profile">
    	<i class="icon-user-profile-small"></i>User Profile</g:link></li>
    <g:if test="${active == 'group'}">
    	<li class="active">
    </g:if>
    <g:else>
    	<li>
    </g:else><g:link controller="user" action="listGroups">
    	<i class="icon-group-small"></i>My Groups</g:link></li>
    <g:if test="${active == 'recordings'}">
    	<li class="active">
    </g:if>
    <g:else>
    	<li>
    </g:else><g:link controller="user" action="listResources">
    	<i class="icon-recordings-small"></i>My Recordings</g:link></li>
    <g:if test="${active == 'synmarks'}">
    	<li class="active">
    </g:if>
    <g:else>
    	<li>
    </g:else><g:link controller="user" action="listResources">
    	<i class="icon-synmarks-small"></i>My Synmarks</g:link></li>
    <g:if test="${active == 'tags'}">
    	<li class="active">
    </g:if>
    <g:else>
    	<li>
    </g:else><g:link controller="user" action="listTags">
    	<i class="icon-tag-c-small"></i>My Tags</g:link></li>
    <g:twitterEnabled>
    <g:if test="${active == 'tweets'}">
    	<li class="active">
    </g:if>
    <g:else>
    	<li>
    </g:else><g:link controller="twitter" action="index"><i class="icon-twitter-small"></i>
    	Upload Tweets</g:link></li>
    </g:twitterEnabled>
    <g:ibmhtsEnabled>
    <g:if test="${active == 'transjobs'}">
    	<li class="active">
    </g:if>
    <g:else>
    	<li>
    </g:else><g:link controller="iBMTransJob" action=index"><i class="icon-document-small"></i>
    	Transcript Jobs</g:link></li>
    </g:ibmhtsEnabled>
</ul>
