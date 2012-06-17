<!-- set thumbnail url -->
<g:if test="${row.thumbnail?.size() >0 && multimedia.isVideo==true}">
	<g:set var="thumbnail_src" value="${row.thumbnail}"/>
</g:if>
<g:elseif test="${row.risVideo == false}">
	<g:set var="thumbnail_src" value="${resource(dir: 'images', file: 'audio_default.png')}"/>
</g:elseif>
<g:else>
	<g:set var="thumbnail_src" value="${resource(dir: 'images', file: 'video_default.jpg')}"/>
</g:else>

<div class="cue-row row">
	<div class="span2">
		<div style="position:relative;">
			<g:link controller='recording' action='replay' id="${multimedia.id}" fragment="${row.mf}" title="play transcript block">			
				<img class="thumbnail-img" src="${thumbnail_src}"/>
				<div style="position:absolute;z-index:1;left:0;bottom:0">
					<span class="label label-inverse label-duration">${row.start} to ${row.end}</span>
				</div>
			</g:link>
		</div>
	</div>
	<div class="span8">
		<g:if test="${row.speaker?.size()>0 }">
		<span><b>${row.speaker}</b></span>
	  	<br/>
	  	</g:if>
	  	<div style="padding: 5px 0px;">
	  		<p>${row.text?.encodeAsHTML()}</p>
	  	</div>
	  	<div class="btn-group pull-right">
	 		<a href="#" data-toggle="dropdown" class="btn dropdown-toggle">
	  			Actions<span class="caret"></span></a>
	  		<ul class="dropdown-menu">
	  				<li><g:link controller="recording" action="replay" id="${multimedia.id}" fragment="${row.mf}">Play it in Synote Player</g:link></li>
	  				<li><g:link controller="recording" action="print" id="${multimedia.id}" fragment="${row.mf}">Print Friendly Version</g:link></li>
	 		</ul>
	 	</div>
	 	<div class="pull-right" style="margin-right:10px;">
	 		<g:link class="btn btn-warning pull-right" controller="nerd" action="nerdcue" id="${row.id}">Nerd it</g:link>
	 	</div>
	</div>
</div>