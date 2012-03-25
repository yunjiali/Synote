<%@ page import="org.synote.user.User"%>
<%@ page import="org.synote.resource.compound.*"%>
<%@ page import="org.synote.annotation.synpoint.Synpoint"%>
<%@ page import="org.synote.user.User"%>
<%@ page import="org.synote.user.User"%>
<html>
<head>
<title>${recording.title}</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="${resource(dir: 'css', file: 'print.css')}" />
<link rel="shortcut icon"
	href="${resource(dir: 'images', file: 'favicon.ico')}"
	type="image/x-icon" />
</head>
<body>
<g:isLoggedIn>
<div class="span-24" id="user_nav">
	<g:render template="/common/userNav"/>
</div>
</g:isLoggedIn>
<h1>
${recording.title}
</h1>
<g:set var="printStartTime" value="${true}" />
<g:each var="synpoint" in="${synpoints}">
	<g:if
		test="${synpoint.annotation.source instanceof TranscriptResource}">
		<g:set var="transcript"
			value="${synpoint.annotation.source.transcript}" />
		<g:set var="text"
			value="${transcript.content.substring(synpoint.sourceStart.intValue(), synpoint.sourceEnd.intValue() + 1)}" />
		<g:set var="text"
			value="${text.replace('\r\n', '<br/>').replace('\n', '<br/>')}" />
		<g:if test="${printStartTime}">
			<span class="firstWord"><g:formatTime
				startTime="${synpoint.targetStart}" /></span>
		</g:if>
		<span class="transcript">
		${text}
		</span>
		<g:printEndTime synpoint="${synpoint}" synpoints="${synpoints}"
			ends="${ends}" />
		<g:set var="printStartTime" value="${false}" />
	</g:if>
	<g:elseif
		test="${synpoint.annotation.source instanceof PresentationResource}">
		<g:set var="presentation" value="${synpoint.annotation.source}" />
		<g:set var="slide"
			value="${presentation.slides.find {slide -> slide.index == synpoint.sourceStart}}" />
		<div class="slide">
		<div class="title"><g:formatTime
			startTime="${synpoint.targetStart}" endTime="${synpoint.targetEnd}" />
		<g:if test="${presentation.title}">
			${presentation.title} -
					</g:if> Slide ${slide.index + 1}
		</div>
		<g:if test="${slideHeight}">
			<div class="image"><img src="${slide?.url}"
				alt="Slide ${slide.index + 1}" height="${slideHeight}" /></div>
		</g:if> <g:else>
			<div class="image"><img src="${slide?.url}"
				alt="Slide ${slide.index + 1}" /></div>
		</g:else></div>
		<g:set var="printStartTime" value="${true}" />
	</g:elseif>
	<g:elseif
		test="${synpoint.annotation.source instanceof SynmarkResource}">
		<g:set var="synmark" value="${synpoint.annotation.source}" />
		<div class="synmark">
		<div class="title"><g:if test="${settings.timing}">
			<g:formatTime startTime="${synpoint.targetStart}"
				endTime="${synpoint.targetEnd}" />
		</g:if> <g:if test="${settings.title}">
			<g:if test="${synmark.title}">
				${synmark.title}
			</g:if>
			<g:else>
							Synmark
						</g:else>
		</g:if> <g:if test="${settings.id}">
						(${synmark.id})
					</g:if></div>
		<g:if test="${settings.note}">
			<div class="note">
			${synmark.note.content}
			</div>
		</g:if> <g:if test="${settings.tags}">
			<div class="tags"><g:each var="tag" in="${synmark.tags}">
				${tag.content}
			</g:each></div>
		</g:if> <g:if test="${settings.owner}">
			<div class="owner"><g:formatOwner owner="${synmark.owner}" /></div>
		</g:if> <g:if test="${settings.next && synmark.next}">
			<div class="next">Next: (${synmark.next})</div>
		</g:if></div>
		<g:set var="printStartTime" value="${true}" />
	</g:elseif>
</g:each>
</body>
</html>
