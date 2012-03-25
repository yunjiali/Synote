package org.synote.permission

import org.synote.user.SecurityService
import org.synote.user.User
import org.synote.resource.Resource
import org.synote.resource.compound.*
import org.synote.user.group.UserGroup
import org.synote.annotation.ResourceAnnotation


class PermService {

    boolean transactional = true

	def sessionFactory
	def securityService
	
	/*
	 * Deprecated. Used in old Synote.
	 */
	PermissionValue getPerm(Resource resource, User user)
	{
		//No user logs, return public permission
		if (!user)
			return resource.perm
		
		if(isOwnerOrAdmin(resource.owner.id, user))
			return PermissionValue.findByName("WRITE")
		
		if(resource.perm?.val > 0)
			return PermissionValue.findByName("READ")
		
		def queryString = """
			select max(pv.val)
			from permission p, user_group g, user_group_member m, permission_value pv
			where
				p.resource_id = :resource_id and p.group_id = g.id and g.id = m.group_id and m.user_id = :user_id and pv.id=p.perm_id
			"""
		
		def session = sessionFactory.getCurrentSession()
		def result = session.createSQLQuery(queryString)
				.setLong('resource_id', resource.id)
				.setLong('user_id', user.id)
				.list()
		
		def perm = PermissionValue.findByVal((Integer) result[0])
		
		return (perm?.val >= resource.perm?.val) ? perm : resource.perm
	}
	/*
	 * If the permission of a resource is null, we will find the permission for the
	 * resource this resource annotates. This is permission inherit
	 */
	PermissionValue getPerm(Resource resource)
	{
		if(!securityService.isLoggedIn())
		{
			PermissionValue readVal = PermissionValue.findByName("READ")
			if(resource.perm!=null && resource.perm?.val >= readVal.val)
				return resource.perm
			else if(resource.perm!=null && resource.perm?.val < readVal.val)
				return PermissionValue.findByName("PRIVATE")
			else
				return readVal
		}
		/*	
		if(!resource.perm)
		{
			def annotation = ResourceAnnotation.findBySource(resource)
			if(!annotation)
			{
				return PermissionValue.findByName("PRIVATE")
			}
			
			def targetResource = annotation.target
			if(!targetResource)
			{
				return PermissionValue.findByName("PRIVATE")
			}
			
			return getPerm(targetResource)
		}
		*/
		
		if(securityService.isAdminLoggedIn())
			return PermissionValue.findByName("WRITE")
		
		def user = securityService.getLoggedUser()
		if(user.id == resource.owner?.id)
			return PermissionValue.findByName("WRITE")
			
		def publicPerm = resource.perm
		
		if(!publicPerm)
		{
			def annotation = ResourceAnnotation.findBySource(resource)
			if(!annotation)
			{
				return PermissionValue.findByName("PRIVATE")
			}
			
			def targetResource = annotation.target
			if(!targetResource)
			{
				return PermissionValue.findByName("PRIVATE")
			}
			
			return getPerm(targetResource)
		}
		
		//if(publicPerm?.val > 0)
		//	return PermissionValue.findByName("READ")
		
		def queryString = """
					select max(pv.val)
					from permission p, user_group g, user_group_member m, permission_value pv
					where
						p.resource_id = :resource_id and p.group_id = g.id and g.id = m.group_id and m.user_id = :user_id and pv.id=p.perm_id
					"""
		
		def session = sessionFactory.getCurrentSession()
		def result = session.createSQLQuery(queryString)
				.setLong('resource_id', resource.id)
				.setLong('user_id', user.id)
				.list()
		
		def perm = PermissionValue.findByVal((Integer) result[0])
		
		return (perm?.val >= publicPerm?.val) ? perm : publicPerm
	}
	
	/*
	 * Deprecated. Used in old Synote
	 */
	PermissionValue getPerm(Resource resource, UserGroup userGroup)
	{
		def queryString = """
			select max(pv.val)
			from permission p, permission_value pv
			where
				p.resource_id = :resource_id and p.group_id = :group_id and p.perm_id = pv.id
			"""
		
		def session = sessionFactory.getCurrentSession()
		def result = session.createSQLQuery(queryString)
				.setLong('resource_id', resource.id)
				.setLong('group_id', userGroup.id)
				.list()
		
		return PermissionValue.findByVal((Integer) result[0])
	}
	
	PermissionValue getUserGroupPerm(Resource resource, UserGroup userGroup)
	{
		def queryString = """
			select max(pv.val)
			from permission p, permission_value pv
			where
				p.resource_id = :resource_id and p.group_id = :group_id and p.perm_id = pv.id
			"""
		
		def session = sessionFactory.getCurrentSession()
		def result = session.createSQLQuery(queryString)
				.setLong('resource_id', resource.id)
				.setLong('group_id', userGroup.id)
				.list()
		
		return PermissionValue.findByVal((Integer) result[0])
	}
	
	def getMultimediaPerm(Resource resource, User user)
	{
		def perm
		if(user)
			perm = getPerm(resource,user)
		else
			perm = resource.perm
		return perm
	}
	
	//This method can be used to change the permission of all multimedia belonged to a user all at one time
	def changeAllMultimediaPermission(userName,perm)
	{
		def multimediaOwner = User.findByUserName(userName)
		if(!multimediaOwner)
		{
			log.error "Cannot find user ${userName}"
			return
		}
		def multimediaList = MultimediaResource.findAllByOwner(multimediaOwner)
		multimediaList.each{multimedia->
			multimedia.perm=perm
			if(!multimedia.save())
			{
				log.error "Save multimedia permission with id ${multimedia.id} error!"
				return
			}
			
			mutimedia.reindex()
		}
	}
	
	def isOwnerOrAdmin(owner, user)
	{
		if(securityService.isAdminLoggedIn())
			return true
			
		if (owner?.class == String)
			owner = owner.toLong()
		
		return user?.id == owner
	}
}
