<g:set var="name" value="${template_name?template_name:'perm'}" />
<g:set var="id" value="${template_id?template_id:'perm'}" />
<g:set var="title"
	value="${template_title?template_title:'Select permission'}" />
<g:set var="defaultPermission"
	value="${defaultPerm?defaultPerm:org.synote.permission.PermissionValue.findByName('ANNOTATE')}" />
<div id="combowrap_{id}_div" class="combowrap">
<select name="${name}" id="${id}" title="Select permission" class="${selectorClass}">
	<g:if test="${canPrivate}">
		<g:set var="permissionValues"
			value="${org.synote.permission.PermissionValue.list()}" />
	</g:if>
	<g:else>
		<g:set var="permissionValues"
			value="${org.synote.permission.PermissionValue.findAllByValGreaterThan(0)}" />
	</g:else>
	<g:each var="permissionValue" in="${permissionValues}">
		<g:if test="${permissionValue?.val.equals(defaultPermission?.val)}">
			<option value="${permissionValue?.val}" selected="selected">
			${permissionValue?.name.trim()}
			</option>
		</g:if>
		<g:else>
			<option value="${permissionValue?.val}">
			${permissionValue?.name.trim()}
			</option>
		</g:else>
	</g:each>
</select>
</div>

