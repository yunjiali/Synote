<select name="role" id="role" title="select a role">
	<g:each var="role" in="${UserRole.values()}">
		<option value="${role}">
		${role}
		</option>
	</g:each>
</select>
