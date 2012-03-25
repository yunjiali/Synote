<html>
<head>
<title><g:message code="org.synote.search.resource.help.title" /></title>
<meta name="layout" content="main" />
<link rel="stylesheet"
	href="${createLinkTo(dir: 'css', file: 'search.css')}" />
</head>
<body>
<div class="nav"><span class="menuButton"><a class="home"
	href="${createLinkTo(dir: '')}">Home</a></span></div>
<div class="body"><g:render template="/common/message" />
<div>
<h1 id="HelpTitle"><g:message
	code="org.synote.search.resource.help.title" /></h1>
<div id="minitoc-area">
<ul class="minitoc">
	<li><a href="#Overview">Overview</a></li>
	<li><a href="#Escaping Special Characters">Query Syntax Error
	and Special Characters</a></li>
	<li><a href="#Terms">Terms</a></li>
	<li><a href="#Fields">Fields</a></li>
	<li><a href="#Term Modifiers">Term Modifiers - Widcard
	Searches, Fuzzy Searches, Proximity Searches, Range Searches, Boosting
	a Term </a> <!--				<ul class="minitoc">
						<li>
							<a href="#Wildcard Searches">Wildcard Searches</a>
						</li>
						<li>
							<a href="#Fuzzy Searches">Fuzzy Searches</a>
						</li>
						<li>
							<a href="#Proximity Searches">Proximity Searches</a>
						</li>
						<li>
							<a href="#Range Searches">Range Searches</a>
						</li>
						<li>
							<a href="#Boosting a Term">Boosting a Term</a>
						</li>
					</ul>
--></li>
	<li><a href="#Boolean operators">Boolean Operators - OR, AND,
	+, NOT, -</a></li>
	<li><a href="#Grouping">Grouping</a></li>
	<li><a href="#Field Grouping">Field Grouping</a></li>
</ul>
</div>

<a name="N10013"></a><a name="Overview"></a>
<h2 class="boxed">Overview</h2>
<div class="section">
<p>This page provides the basic guide to the syntax of Synote search
engine.</p>
</div>

<a name="N10180"></a><a name="Escaping Special Characters"></a>
<h2 class="boxed">Query Syntax Error and Special Characters</h2>
<div class="section">
<p>The following several queries will return a syntax error:
<p>(1) A query containing no word(s), but only special characters or
other none word characters
<p>(2) A query contains word(s), but ends with a * character</p>
<p>The current list of special characters is</p>
<p>+ - &amp;&amp; || ! ( ) { } [ ] ^ " ~ * ? : \</p>
<p>You can use special charaters in <a href="#Wildcard Searches">wildcard
search</a></p>
<p>We DO NOT recommend you to search these special characters. To
escape these character use the \ before the character.</p>
</div>

<a name="N10032"></a><a name="Terms"></a>
<h2 class="boxed">Terms</h2>
<div class="section">
<p>A query is broken up into terms and operators. There are two
types of terms: Single Terms and Phrases. A Single Term is a single
word, for example "test". Phrase is a group of words surrounded by
double quotes, like "hi there". Multiple terms can be combined together
with Boolean operators to form a more complex query (see below).</p>
</div>

<a name="N10048"></a><a name="Fields"></a>
<h2 class="boxed">Fields</h2>
<div class="section">
<p>When performing a search you can either specify a field, or use
the default field. The field names and default field is implementation
specific. In our case text is the default field and it's indicator is
not required. You can search any field by typing the field name followed
by a colon ":" and then the term you are looking for.</p>
<p>If you want to find the document entitled "INFO1016 CECIL", you
can enter:</p>
<g:link controller="search" action="index"
	params='[query:/title:"INFO1016 CECIL"/]'>title:"INFO1016 CECIL"</g:link>
<p>Note: The field is only valid for the term that it directly
precedes, so the query <g:link controller="search" action="index"
	params="[query:'title:INFO1016 CECIL']">title:INFO1016 CECIL</g:link>
will only find "INFO1016" in the title field. It will find "CECIL" in
the default field (in this case the text field).</p>
</div>

<a name="N1006D"></a><a name="Term Modifiers"></a>
<h2 class="boxed">Term Modifiers</h2>
<div class="section">
<p>It is possible to modify query terms to get a wide range of
searching options.</p>

<a name="N10076"></a><a name="Wildcard Searches"></a>
<h3 class="boxed">Wildcard Searches</h3>
<p>Single and multiple character wildcard search is supported within
single terms (not within phrase queries). To perform a single character
wildcard search use the "?" symbol. The single character wildcard search
looks for terms that match that with the single character replaced. For
example, to search for "text" or "test" you can use the search:</p>
<g:link controller="search" action="index" params="[query:'te?t']">te?t</g:link>
<p>To perform a multiple character wildcard search use the "*"
symbol. Multiple character wildcard searches looks for 0 or more
characters. For example, to search for test, tests or tester, you can
use the search:</p>
<g:link controller="search" action="index" params="[query:'test*']">test*</g:link>
<p>You can also use the wildcard searches in the middle of a term or
as the first character of a search.</p>
<g:link controller="search" action="index" params="[query:'te*t']">te*t</g:link>
<p><g:link controller="search" action="index"
	params="[query:'?est']">?est</g:link></p>

<a name="N1009B"></a><a name="Fuzzy Searches"></a>
<h3 class="boxed">Fuzzy Searches</h3>
<p>To do a fuzzy search use the tilde, "~", symbol at the end of a
Single word Term. For example to search for a term similar in spelling
to "macro" use the fuzzy search:</p>
<g:link controller="search" action="index" params="[query:'macro~']">macro~</g:link>
<p>This search will except the term "macro" find terms like "macros"
and "micro".</p>

<a name="N100B4"></a><a name="Proximity Searches"></a>
<h3 class="boxed">Proximity Searches</h3>
<p>You can also find words that are within a specific distance away.
To do a proximity search use the tilde, "~", symbol at the end of a
Phrase. For example to search for a "unique" and "application" within 5
words of each other in a document use the search:</p>
<g:link controller="search" action="index"
	params='[query:/"unique application"~5/]'>"unique application"~5</g:link>

<a name="N100C1"></a><a name="Range Searches"></a>
<h3 class="boxed">Range Searches</h3>
<p>Range Queries allow one to match documents whose field(s) values
are between the lower and upper bound specified by the Range Query.
Range Queries can be inclusive or exclusive of the upper and lower
bounds. Sorting is done lexicographically. For example</p>
<g:link controller="search" action="index"
	params="[query:'title:[Info1016 TO Info6002]']">title:[Info1016 TO Info6002]</g:link>
<p>will find all documents whose titles are between Info1016 and
Info6002, including Info1016 and Info6002. Inclusive range queries are
denoted by square brackets. Exclusive range queries are denoted by curly
brackets.</p>

<a name="N100DA"></a><a name="Boosting a Term"></a>
<h3 class="boxed">Boosting a Term</h3>
<p>To boost a relevance of a term use the caret, "^", symbol with a
boost factor (a number) at the end of the term you are searching. The
higher the boost factor, the more relevant the term will be.</p>
<p>Boosting allows you to control the relevance of a document by
boosting its term. For example, if you are searching for</p>
<g:link controller="search" action="index"
	params="[query:'web multimedia']">web multimedia</g:link>
<p>and you want the term "multimedia" to be more relevant, boost it
using the ^ symbol along with the boost factor next to the term. You
would type:</p>
<g:link controller="search" action="index"
	params="[query:'web multimedia^4']">web multimedia^4</g:link>
<p>This will make documents with the term multimedia appear more
relevant. You can also boost Phrase Terms as in the example:</p>
<g:link controller="search" action="index"
	params='[query:/"Second Life"^4 "Dragon NaturallySpeaking"/]'>"Second Life"^4 "Dragon NaturallySpeaking"</g:link>
<p>By default, the boost factor is 1.</p>
</div>

<a name="N100FA"></a><a name="Boolean operators"></a>
<h2 class="boxed">Boolean Operators</h2>
<div class="section">
<p>Boolean operators allow terms to be combined through logic
operators AND, "+", OR, NOT and "-" (must be ALL CAPS).</p>
<a name="N10103"></a><a name="OR"></a>
<h3 class="boxed">OR</h3>
<p>The OR operator (or the symbol ||) links two terms and finds a
matching document if either of the terms exist in a document.</p>
<p>To search for documents that contain either "annotating
multimedia" or just "multimedia" use the query:</p>
<g:link controller="search" action="index"
	params='[query:/"annotating multimedia" multimedia/]'>"annotating multimedia" multimedia</g:link>
<p>or</p>
<g:link controller="search" action="index"
	params='[query:/"annotating multimedia" OR multimedia/]'>"annotating multimedia" OR multimedia</g:link>
<a name="N10116"></a><a name="AND"></a>
<h3 class="boxed">AND</h3>
<p>The AND operator (or the symbol &amp;&amp) matches documents
where both terms exist anywhere in the text of a single document.</p>
<p>To search for documents that contain "annotating multimedia" and
"University of Southampton " use the query:</p>
<g:link controller="search" action="index"
	params='[query:/"Second Life" AND "Dragon NaturallySpeaking"/]'>"Second Life" AND "Dragon NaturallySpeaking"</g:link>
<a name="N10126"></a><a name="+"></a>
<h3 class="boxed">+</h3>
<p>The "+" or required operator requires that the term after the "+"
symbol exist somewhere in the field of a single document.</p>
<p>To search for documents that must contain "life" and may contain
"dragon" use the query:</p>
<g:link controller="search" action="index"
	params="[query:'+life dragon']">+life dragon</g:link> <a name="N10136"></a><a
	name="NOT"></a>
<h3 class="boxed">NOT</h3>
<p>The NOT operator (or the symbol !) excludes documents that
contain the term after NOT.</p>
<p>To search for documents that contain "Second Life" but not
"Dragon NaturallySpeaking" use the query:</p>
<g:link controller="search" action="index"
	params='[query:/"Second Life" NOT "Dragon NaturallySpeaking"/]'>"Second Life" NOT "Dragon NaturallySpeaking"</g:link>
<p>Note: The NOT operator cannot be used with just one term. For
example, the following search will return no results:</p>
<g:link controller="search" action="index"
	params='[query:/NOT "Second Life"/]'>NOT "Second Life"</g:link> <a
	name="N1014C"></a><a name="-"></a>
<h3 class="boxed">-</h3>
<p>The "-" or prohibit operator excludes documents that contain the
term after the "-" symbol.</p>
<p>To search for documents that contain "Second Life" but not
"Dragon NaturallySpeaking" use the query:</p>
<g:link controller="search" action="index"
	params='[query:/"Second Life" -"Dragon NaturallySpeaking"/]'>"Second Life" -"Dragon NaturallySpeaking"</g:link>
</div>

<a name="N1015D"></a><a name="Grouping"></a>
<h2 class="boxed">Grouping</h2>
<div class="section">
<p>It is possible to use parentheses to group clauses to form sub
queries. This can be very useful if you want to control the boolean
logic for a query.</p>
<p>To search for either "run" or "compile" and "cecil" use the
query:</p>
<g:link controller="search" action="index"
	params="[query:'(run OR compile) AND cecil']">(run OR compile) AND cecil</g:link>
<p>This eliminates any confusion and makes sure that "cecil" must
exist and either term "run" or "compile" may exist.</p>
</div>

<a name="N10170"></a><a name="Field Grouping"></a>
<h2 class="boxed">Field Grouping</h2>
<div class="section">
<p>It Is possible to use parentheses to group multiple clauses to a
single field.</p>
<p>To search for a title that contains both the word "video" and the
phrase "Professional Issues" use the query:</p>
<g:link controller="search" action="index"
	params='[query:/title:(+video +"Professional Issues")/]'>title:(+video +"Professional Issues")</g:link>
</div>
</div>
<!--+
		|end content
		+--></div>
</body>
</html>
