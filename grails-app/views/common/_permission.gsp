<g:set var="name" value="${template_name?template_name:'perm'}" />
<g:set var="id" value="${template_id?template_id:'perm'}" />
<g:set var="title"
	value="${template_title?template_title:'Select permission'}" />
<g:set var="defaultPermission"
	value="${defaultPerm?defaultPerm:org.synote.permission.PermissionValue.findByName('ANNOTATE')}" />
<div class="controls">
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
			<label class="radio">
				<input type="radio" value="${permissionValue?.val}" checked="checked">
						${permissionValue?.name.trim().toLowerCase()}
			</label>
		</g:if>
		<g:else>
			<label class="radio">
				<input type="radio" value="${permissionValue?.val}">
						${permissionValue?.name.trim().toLowerCase()}
			</label>
		</g:else>
		<g:if test="${permissionValue?.val == 0}">
			<span class="permission-info">Only you can view</span>
		</g:if>
		<g:elseif test="${permissionValue?.val == 100}">
			<span class="permission-info">Every body can view, but they cannot make annotations</span>
		</g:elseif>
		<g:elseif test="${permissionValue?.val == 200}">
			<span class="permission-info">Every body can view and make annotations, but they cannot edit transripts and slides</span>
		</g:elseif>
		<g:else>
			<span class="permission-info">Every body can view, make annotations, edit transcript and slides</span>
		</g:else>
	</g:each>
	<label class="radio">
		
	</label>
</div>

