<g:if test="${flash.error}">
	<div class="alert alert-error">
	${flash.error}
	</div>
</g:if>
<g:if test="${flash.info}">
	<div class="alert alert-info">
	${flash.info}
	</div>
</g:if>
<g:hasErrors bean="${bean}">
	<div class="errors"><g:renderErrors bean="${bean}" as="list" /></div>
</g:hasErrors>
<g:if test="${flash.message}">
	<div class="alert alert-success">
	${flash.message}
	</div>
</g:if>
