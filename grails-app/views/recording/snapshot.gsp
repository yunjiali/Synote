<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional with HTML5 microdata//EN" "xhtml1-transitional-with-html5-microdata.dtd">
<!-- this page is used for provide snapshot web page for google search -->
<%@ page import="org.synote.resource.compound.*"%>
<%@ page import="org.synote.annotation.synpoint.Synpoint"%>
<%@ page import="org.synote.user.User"%>
<%@ page import="java.text.SimpleDateFormat"%>
<html>
<head>
<title>${recording.title}</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="${resource(dir: 'css', file: 'print.css')}" />
<style type="text/css">
	.mediaObject
	{}
</style>

</head>
<body>
<!-- Get multimedia type, audio or video -->
<g:set var="mmType" value="${isVideo?'http://schema.org/VideoObject':'http://schema.org/AudioObject'}"/>
<div id="recording_content_div" id="recording_content_div" itemscope="itemscope" itemtype="${mmType}" 
			itemref="recording_owner_div" class="mediaObject">
	<g:if test="${encodingFormat}">
		<meta itemprop="encodingFormat" content="${encodingFormat}"/>
	</g:if>
	<h1 itemprop="name">${recording.title}</h1>
	<meta itemprop="contentURL" content="${recording.url?.url}"/>
	<meta itemprop="dateCreated" content="${new SimpleDateFormat("dd/MM/yyyy").format(recording.dateCreated)}"/>
	<meta itemprop="dateModified" content="${new SimpleDateFormat("dd/MM/yyyy").format(recording.lastUpdated)}"/>
	<div id="multimedia_player_div">
	</div>
</div>
<div id="recording_owner_div" itemprop="creator" itemscope="itemscope" itemtype="http://schema.org/Person" itemid="${g.getUserURI(recording.owner?.id.toString())}">
		by <g:link controller="user" action="show" id="${recording.owner?.id}" elementId="recording_owner_a" itemprop="name">
						${recording.owner?.userName}</g:link>
		<meta itemprop="familyName" content="${recording.owner?.firstName}"/>
		<meta itemprop="givenName" content="${recording.owner?.lastName}"/>
		<meta itemprop="email" content="${recording.owner?.email}"/>		
</div>
<g:set var="printStartTime" value="${true}" />
<g:each var="synpoint" in="${synpoints}">
	<g:if test="${synpoint.annotation.source.instanceOf(WebVTTResource)}">
		<g:set var="cue" value="${synpoint.annotation.source.cues?.find{it.cueIndex == synpoint.sourceStart}}" />
		<g:set var="text" value="${cue?.content}" />
		<div class="transcript mediaObject" 
			itemscope="itemscope" itemtype="${mmType}" itemid="${g.getResourceURIWithFragment(recording.id.toString(),synpoint)}">
			<g:if test="${printStartTime}">
				<span class="firstWord"><g:formatTime startTime="${synpoint.targetStart}" endTime="${synpoint.targetEnd}"/></span>
			</g:if>
			<span itemprop="transcript">${text.encodeAsHTML()}</span>
		</div>
		<g:printEndTime synpoint="${synpoint}" synpoints="${synpoints}" ends="${ends}" />
		<g:set var="printStartTime" value="${false}" />
	</g:if>
	<g:elseif test="${synpoint.annotation.source.instanceOf(PresentationResource)}">
		<g:set var="presentation" value="${synpoint.annotation.source}" />
		<g:set var="slide" value="${presentation.slides.find {slide -> slide.index == synpoint.sourceStart}}" />
		<div class="slide" class="mediaObject" 
			itemscope="itemscope" itemtype="${mmType}" itemid="${g.getResourceURIWithFragment(recording.id.toString(),synpoint)}">
			<meta itemprop="image" content="${slide?.url}"/>
			<div class="title">
				<g:formatTime startTime="${synpoint.targetStart}" endTime="${synpoint.targetEnd}" />
				<g:if test="${presentation.title}">
					${presentation.title} -
				</g:if> Slide ${slide.index + 1}
			</div>
			<div class="image">
				<img itemprop="image" itemscope="itemscope" itemtype="http://schema.org/ImageObject"  src="${slide?.url}" alt="Slide ${slide.index + 1}" />
			</div>
		</div>
		<g:set var="printStartTime" value="${true}" />
	</g:elseif>
	<g:elseif test="${synpoint.annotation.source.instanceOf(SynmarkResource)}">
		<g:set var="synmark" value="${synpoint.annotation.source}" />
		<div class="synmark mediaObject" itemscope="itemscope" itemtype="${mmType}" itemid="${g.getResourceURIWithFragment(recording.id.toString(),synpoint)}">
			<div class="title">
				<g:if test="${settings.timing}">
					<g:formatTime startTime="${synpoint.targetStart}" endTime="${synpoint.targetEnd}" />
				</g:if> 
				<g:if test="${settings.title}">
					<g:if test="${synmark.title}">
						<span itemprop="name">${synmark.title}</span>
					</g:if>
					<g:else>
						Synmark
					</g:else>
				</g:if>
			</div>
			<g:if test="${settings.note}">
				<div class="note" itemprop="description">
					${synmark.note?.content?.encodeAsHTML()}
				</div>
			</g:if> 
			<g:if test="${settings.tags}">
				<div class="tags">
					<g:each var="tag" in="${synmark.tags}">
						<span class="tag" itemprop="keywords">${tag?.content}</span>
					</g:each>
				</div>
			</g:if>
			<g:if test="${settings.owner}">
				<div class="owner" itemprop="creator" itemscope="itemscope" itemtype="http://schema.org/Person" itemid="${g.getUserURI(synmark.owner?.id.toString())}">
					by <g:link controller="user" action="show" id="${recording.owner?.id}" itemprop="name">
						${synmark.owner?.userName}</g:link>
					<meta itemprop="familyName" content="${synmark.owner?.firstName}"/>
					<meta itemprop="givenName" content="${synmark.owner?.lastName}"/>
					<meta itemprop="email" content="${synmark.owner?.email}"/>		
				</div>
			</g:if>
		</div>
		<g:set var="printStartTime" value="${true}" />
	</g:elseif>
</g:each>
</body>
</html>
