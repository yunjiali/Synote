<!-- set thumbnail url -->
<g:if test="${row.thumbnail?.size() >0 && row.risVideo==true}">
	<g:set var="thumbnail_src" value="${row.thumbnail}"/>
</g:if>
<g:elseif test="${row.risVideo == false}">
	<g:set var="thumbnail_src" value="${resource(dir: 'images', file: 'audio_default.png')}"/>
</g:elseif>
<g:else>
	<g:set var="thumbnail_src" value="${resource(dir: 'images', file: 'video_default.jpg')}"/>
</g:else>

<!-- set title depending on length -->
<g:if test="${row.title?.size() > 50}">
	<g:set var="s_title" value="${row.title?.substring(0,50)+'...'}"/>
</g:if>
<g:elseif test="${row.title?.size() > 0}">
	<g:set var="s_title" value="${row.title}"/>
</g:elseif>
<g:else>
	<g:set var="s_title" value="${null}"/>
</g:else>

<div class="synmark-row row">
	<div class="span2">
		<div style="position:relative;">
			<g:link controller='recording' action='replay' id="${row.rid}" fragment="${row.mf}" title="play synamrk">			
				<img class="thumbnail-img" src="${thumbnail_src}"/>
				<div style="position:absolute;z-index:1;left:0;bottom:0">
					<span class="label label-inverse label-duration">${row.start} to ${row.end}</span>
				</div>
			</g:link>
		</div>
	</div>
	<div class="span8">
	  	<h3 class="heading-inline">
		  	<g:if test="${s_title != null}">
		  		${s_title}
		  	</g:if>
		  	<g:else>
		  		<i>Title not available</i>
	  		</g:else>
	  	</h3>
	  	<br/>
	  	<span class="datetime-info">${row.date_created}</span><br/>
	  	<div style="padding: 5px 0px;">
	  		<p>${row.note?.encodeAsHTML()}</p>
	  	</div>
	  	<div>
	  		<g:each var="tag" in="${row.tags}">
	  			<span class="badge badge-tag"><i class="icon-tag tag-item icon-white"></i>${tag}</span>
	  		</g:each>
	  	</div>
	  	<div class="btn-group pull-right">
	 		<a href="#" data-toggle="dropdown" class="btn dropdown-toggle">
	  			Actions<span class="caret"></span></a>
	  		<ul class="dropdown-menu">
	  				<li><g:link controller="multimediaResource" action="replay" id="${row.id}">Details</g:link></li>
	  				<li><g:link controller="multimediaResource" action="replay" id="${row.id}">Edit</g:link>
	  				</li><li class="divider"></li>
	  				<li><g:link controller="recording" action="replay" id="${row.rid}" fragment="${row.mf}">Play it in Synote Player</g:link></li>
	  				<li><g:link controller="recording" action="print" id="${row.rid}" fragment="${row.mf }">Print Friendly Version</g:link></li>
	 		</ul>
	 	</div>
	 	<div class="pull-right" style="margin-right:10px;">
	 		<g:link class="btn btn-warning pull-right" controller="nerd" action="nerdsmk" id="${row.id}">Nerd it</g:link>
	 	</div>
	</div>
</div>