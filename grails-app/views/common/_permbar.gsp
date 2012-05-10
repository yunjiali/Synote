<g:if test="${perm_val == 0}">
	<span class="badge label-important">${perm_name.toLowerCase()}</span>
</g:if>
<g:elseif test="${perm_val == 100}">
	<span class="badge label-warning">${perm_name.toLowerCase()}</span>
</g:elseif>
<g:elseif test="${perm_val == 200}">
	<span class="badge label-success">${perm_name.toLowerCase()}</span>
</g:elseif>
<g:elseif test="${perm_val == 300}">
	<span class="badge label-inverse">${perm_name.toLowerCase()}</span>
</g:elseif>