<%@ page import="org.springframework.util.ClassUtils"%>
<%@ page import="org.synote.search.resource.*"%>
<%@ page import="org.synote.resource.compound.*"%>
<%@ page
	import="org.codehaus.groovy.grails.plugins.searchable.SearchableUtils"%>
<%@ page
	import="org.codehaus.groovy.grails.plugins.searchable.lucene.LuceneUtils"%>
<%@ page
	import="org.codehaus.groovy.grails.plugins.searchable.util.StringQueryUtils"%>
<html>
<head>
<title><g:message code="org.synote.search.resource.index.title" /></title>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta name="layout" content="main" />
<link rel="stylesheet"
	href="${resource(dir: 'css', file: 'search.css')}" />
<script type="text/javascript">
			var focusQueryInput = function() {
					document.getElementById("q").focus();
			}
	</script>

</head>
<body onload="focusQueryInput();">
<g:set var="haveQuery" value="${params.query?.trim()}" />
<g:set var="resultsCount"
	value="${resourceSearchResult?.results.size()}" />
<g:if test="${resultsCount > 0}">
	<g:set var="results"
		value="${resourceSearchResult?.results.getAt(new IntRange(resourceSearchResult.offset, (resourceSearchResult.offset+resourceSearchResult.max>resultsCount) ? resultsCount-1: resourceSearchResult.offset+resourceSearchResult.max-1))}" />
</g:if>

<div id="cnt">
<div id="ssb"><g:if test="${resultsCount > 0}">
	<p>Recordings ${resourceSearchResult?.offset+1} - ${(resourceSearchResult.offset+resourceSearchResult.max>resultsCount) ? resultsCount: resourceSearchResult.offset+resourceSearchResult.max}
	of ${resultsCount} for <b>
	${params.query?.trim()}
	</b></p>
</g:if></div>

<g:if test="${haveQuery && resultsCount == 0 && !parseException}">
	<div id="nor">
	<p>Nothing matched your query - <strong>
	${params.query}
	</strong></p>
	<p>You may not have the permission to see the resource. Please go
	back and choose other keywords.</p>
	</div>
</g:if> <g:if test="${parseException}">
	<p>Your query - <strong>
	${params.query}
	</strong> - is not valid.</p>
	<p>Suggestions:</p>
	<ul>
		<li>Choose an alternative term to search.</li>
		<g:if test="${LuceneUtils.queryHasSpecialCharacters(params.query)}">
			<li>Remove special characters like <strong>" - [ ]</strong>,
			before searching
			</li>
			<li>Escape special characters like <strong>" - [ ]</strong> with
			<strong>\</strong>
			</li>
		</g:if>
		<li>You may not have the permission to see the resource.</li>
	</ul>
</g:if> <g:if test="${resultsCount > 0}">
	<div id="res" class="med">
	<h1><g:message code="org.synote.search.resource.index.h1" /></h1>
	<g:if test="${resourceSearchResult?.suggestedQuery}">
		<span class="spell" style="color: red">Did you mean:</span>
		<span id="sug"><i>resourceSearchResult?.suggestedQuery</i></span>
		<br />
	</g:if>
	<ol>
		<g:each var="result" in="${results}" status="index">
			<li>
			<div id="mulwr">
			<h2><g:link target="_blank" controller="resSearch"
				action="locateMultimedia" id="${result?.id}" params="[qr:params.qr]">
				<g:if
					test="${result.recording['MultimediaResource'] && result.highlights['MultimediaResource'].head().getAt('title')}">
					${result.highlights["MultimediaResource"].head().getAt("title")}
				</g:if>
				<g:else>
					${MultimediaResource.get(result.id).title}
				</g:else>
			</g:link></h2>
			</div>

			<div id="synwr"><g:if
				test="${result.recording['SynmarkResource']}">
				<g:each var="synmarkMap"
					in="${result.highlights['SynmarkResource']}" status="s">
					<g:set var="synmark"
						value="${result.recording['SynmarkResource'].getAt(s)}" />
					<div class="synr"><img
						src="${createLinkTo(dir: 'images/skin', file: 'bookmark.png')}">
					<h3 style="display: inline;"><g:link target="_blank"
						controller="resSearch" action="locateSynmark" params="[qr:params.qr]"
						id="${result.recording['SynmarkResource'].getAt(s).id}"
						title="play this synmark">
						<g:if test="${synmarkMap.getAt('title')}">
							${synmarkMap.getAt("title")}
						</g:if>
						<g:elseif test="${synmark.title}">
							${synmark.title}
						</g:elseif>
						<g:else>
															Untitled Synmark
														</g:else>
					</g:link></h3>
					<g:if test="${synmarkMap.getAt('tags')}">
						<div class="stags">Tags:${synmarkMap.getAt('tags')}
						</div>
					</g:if> <g:elseif test="${synmark.tags}">
						<div class="stags">Tags:${synmark.TagsToString()}
						</div>
					</g:elseif> <g:if test="${synmarkMap.getAt('note')}">
						<div class="snote">Note:${synmarkMap.getAt('note')}
						</div>
					</g:if> <g:elseif test="${synmark.note && synmark.note.content}">
						<div class="snote">Note:${synmark.note.content}
						</div>
					</g:elseif></div>
				</g:each>
				<!--
										<div id="synti">
											<span class="mr">More Synmark results from this recording...</span>
										</div>
										-->
			</g:if> <g:elseif
				test="${!params?.advanced || (params?.synmark && params?.advanced)}">
				<div class="synno">No synmark in this recording matches the
				query.</div>
			</g:elseif></div>
			<g:if test="${result.recording['TranscriptResource']}">
				<div id="trawr"><g:each var="transcriptMap"
					in="${result.highlights['TranscriptResource']}" status="t">
					<g:if test="${transcriptMap.getAt('content')}">
						<div class="trar"><g:set var="transStrs"
							value="${transcriptMap.getAt('content').split('\\.\\.\\.')}" />
						<g:set var="trans"
							value="${result.recording['TranscriptResource'].getAt(t)}" /> <g:each
							var="transStr" in="${transStrs}" status="tr">
							<div class="transtx"><g:link controller="resSearch" action="locateTranscript"
								params="[content:transStr,id:trans.id, qr:params.qr]" target="_blank"
								title="play this transcript">
								<img
									src="${createLinkTo(dir: 'images/skin', file: 'icon_play.png')}">
							</g:link> ${transStr}
							</div>
						</g:each> <!--
													<span class="mr">More transcript</span>
													--></div>
					</g:if>
				</g:each></div>
			</g:if> <g:elseif
				test="${!params?.advanced || (params?.transcript && params?.advanced)}">
				<div class="trano">No transcript in this recording matches the
				query.</div>
			</g:elseif></li>
		</g:each>
	</ol>
	</div>
</g:if>
<div class="paging"><g:if test="${resultsCount > 0}">
	<g:set var="totalPages"
		value="${Math.ceil(resultsCount / resourceSearchResult.max)}" />
	<g:if test="${totalPages == 1}">
		<span>1</span>
	</g:if>
	<g:else>
		<g:paginate next="Next" prev="Prev" controller="resSearch" action="index"
			params="[query: params.query, qr:params.qr]" total="${resultsCount}"
			max="resourceSearchResult.max" />
	</g:else>
</g:if></div>
</div>
</div>
</body>
</html>
