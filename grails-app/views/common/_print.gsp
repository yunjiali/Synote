<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
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
</head>
<body>

<!-- Get multimedia type, audio or video -->
<g:set var="mmType" value="${recording.isVideo?'http://schema.org/VideoObject':'http://schema.org/AudioObject'}"/>
<div id="recording_content_div" class="mediaObject">
	<g:if test="${encodingFormat}">
		<meta itemprop="encodingFormat" content="${encodingFormat}"/>
	</g:if>
	<h1 itemprop="name">${recording.title}</h1>
	<div id="multimedia_player_div">
	</div>
</div>
<div id="recording_owner_div">
		by <g:link controller="user" action="show" id="${recording.owner?.id}" elementId="recording_owner_a">
						${recording.owner?.userName}</g:link>
</div>
<g:each var="synpoint" in="${synpoints}">
	<g:set var="resourceURI" value="${syn.getResourceURIWithFragment([resourceId:recording.id.toString(),synpoint:synpoint])}"/>
	<g:if test="${synpoint.annotation.source.instanceOf(WebVTTResource)}">
		<g:set var="cue" value="${synpoint.annotation.source.cues?.find{it.cueIndex == synpoint.sourceStart}}" />
		<g:set var="text" value="${cue?.content}" />
		<div class="transcript mediaObject">
				<span class="firstWord">
					<a href="${resourceURI}" target="_blank"><syn:formatTime startTime="${synpoint.targetStart}" endTime="${synpoint.targetEnd}"/></a>
				</span>
			<span>${text.encodeAsHTML()}</span>
		</div>
		<syn:printEndTime synpoint="${synpoint}" synpoints="${synpoints}" ends="${ends}" />
	</g:if>
	<g:elseif test="${synpoint.annotation.source.instanceOf(PresentationResource)}">
		<g:set var="presentation" value="${synpoint.annotation.source}" />
		<g:set var="slide" value="${presentation.slides.find {slide -> slide.index == synpoint.sourceStart}}" />
		<div class="slide" class="mediaObject">
			<div class="title">
				<a href="${resourceURI}" target="_blank"><syn:formatTime startTime="${synpoint.targetStart}" endTime="${synpoint.targetEnd}" /></a>
				<g:if test="${presentation.title}">
					${presentation.title} -
				</g:if> Slide ${slide.index + 1}
			</div>
			<div class="image">
				<img src="${slide?.url}" alt="Slide ${slide.index + 1}" />
			</div>
		</div>
	</g:elseif>
	<g:elseif test="${synpoint.annotation.source.instanceOf(SynmarkResource)}">
		<g:set var="synmark" value="${synpoint.annotation.source}" />
		<div class="synmark mediaObject">
			<div class="title">
				<g:if test="${settings.timing}">
					<a href="${resourceURI}" target="_blank"><syn:formatTime startTime="${synpoint.targetStart}" endTime="${synpoint.targetEnd}" /></a>
				</g:if> 
				<g:if test="${settings.title}">
					<g:if test="${synmark.title}">
						<span>${synmark.title}</span>
					</g:if>
					<g:else>
						Synmark
					</g:else>
				</g:if>
			</div>
			<g:if test="${settings.note}">
				<div class="note">
					${synmark.note?.content?.encodeAsHTML()}
				</div>
			</g:if> 
			<g:if test="${settings.tags}">
				<div class="tags">
					<g:each var="tag" in="${synmark.tags}">
						<span class="tag">${tag?.content}</span>
					</g:each>
				</div>
			</g:if>
			<g:if test="${settings.owner}">
				<div class="owner">
					by <g:link controller="user" action="show" id="${recording.owner?.id}">
						${synmark.owner?.userName}</g:link>
				</div>
			</g:if>
		</div>
	</g:elseif>
</g:each>
</body>
</html>
