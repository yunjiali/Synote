<div id="tabs-right">
    <ul>
        <li><g:link controller="user" action="showUserProfile" title="show user profile">
        	<img src="${resource(dir: 'images/skin', file: 'user_profile_16.png')}"/>&nbsp;User Profile&nbsp;</g:link></li>
        <li><g:link controller="user" action="listGroups">
        	<img src="${resource(dir: 'images/skin', file: 'user_group_16.png')}"/>&nbsp;My Groups&nbsp;</g:link></li>
        <li><g:link controller="user" action="listResources">
        	<img src="${resource(dir: 'images/skin', file: 'my_resources_16.png')}"/>&nbsp;My Resources&nbsp;</g:link></li>
        <li><g:link controller="user" action="listTags">
        	<img src="${resource(dir: 'images/skin', file: 'tag_16.png')}"/>&nbsp;My Tags&nbsp;</g:link></li>
        <g:twitterEnabled>
        <li><g:link controller="twitter" action="index"><img src="${resource(dir: 'images/skin', file: 'twitter_16.png')}"/>&nbsp;
        	Upload Tweets&nbsp;</g:link></li>
        </g:twitterEnabled>
        <g:ibmhtsEnabled>
        <li><g:link controller="iBMTransJob" action=index"><img src="${resource(dir: 'images/skin', file: 'document_16.png')}"/>&nbsp;
        	Transcript Jobs&nbsp;</g:link></li>
        </g:ibmhtsEnabled>
    </ul>
</div>
<script type="text/javascript">
$(document).ready(function(){
	 $("#tabs-right").wijmenu({
         orientation: 'horizontal'
     });

});
</script>