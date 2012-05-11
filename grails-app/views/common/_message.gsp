<g:if test="${flash.error}">
	<div class="alert alert-error">
		<button class="close" data-dismiss="alert">×</button>
		${flash.error}
	</div>
</g:if>
<g:if test="${flash.info}">
	<div class="alert alert-info">
		<button class="close" data-dismiss="alert">×</button>
		${flash.info}
	</div>
</g:if>
<g:hasErrors bean="${bean}">
	<div class="errors"><button class="close" data-dismiss="alert">×</button><g:renderErrors bean="${bean}" as="list" /></div>
</g:hasErrors>
<g:if test="${flash.message}">
	<div class="alert alert-success">
		<button class="close" data-dismiss="alert">×</button>
		${flash.message}
	</div>
</g:if>
