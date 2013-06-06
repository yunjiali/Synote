<html>
<head>
<title><g:message code="org.synote.user.group.show.title" /></title>
<meta name="layout" content="main" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<div class="container">
	<div class="row">
		<div class="span12" id="user_content_div">
			<h2 class="heading-inline"><g:message code="org.synote.user.group.show.title" /></h2>
			<hr/>
			<g:render template="/common/message" />
			<g:if test="${ isMember == false && isOwnerOrAdmin == false}">
			<div class="pull-right">
				<g:link class="btn btn-primary btn-large" action="joinGroup" title="Join this group"><i class="icon-plus icon-white"></i>Join this group</g:link>
			</div>
			</g:if>
			<dl>
				<dt>Group Name</dt>
				<dd>${userGroup.name}</dd>
				<dt>Group Owner</dt>
				<dd>${userGroup.owner?.userName}</dd>
				<dt>Public</dt>
				<dd>${userGroup.shared==true?'Yes':'No'}</dd>
				<dt>Description</dt>
				<dd>${userGroup.description}</dd>
				<dt>Date Created</dt>
				<dd>${userGroup.dateCreated.format("dd-mm-yyyy")}</dd>
			</dl>
			<h3>Group Members and Recordings</h3>
			<div class="accordion" id="usergroup_accordion">  
	            <div class="accordion-group">  
	              <div class="accordion-heading">  
	                <a class="accordion-toggle" data-toggle="collapse" data-parent="#usergroup_accordion" href="#members_collapse">  
	                  	Members (${members.size()+1}) 
	                </a>
	              </div>  
	              <div id="members_collapse" class="accordion-body collapse" style="height: 0px; ">  
	                <div class="accordion-inner">  
	                  Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.  
	                </div>  
	              </div>  
	            </div>  
	            <div class="accordion-group">  
	              <div class="accordion-heading">  
	                <a class="accordion-toggle" data-toggle="collapse" data-parent="#usergroup_accordion" href="#recordings_collapse">  
	                 	Recordings (${recordings.size()})
	                </a>  
	              </div>  
	              <div id="recordings_collapse" class="accordion-body collapse">  
	                <div class="accordion-inner">  
	                  Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.  
	                </div>  
	              </div>  
	            </div>   
          	</div>  <!-- /usergroup_accordion -->
		</div>
	</div>
</div><!-- /container -->
</body>
</html>
