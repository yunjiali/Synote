package org.synote.utils

import groovy.sql.Sql
import org.synote.user.User
import org.synote.user.SecurityService
import org.synote.permission.PermissionValue
import org.synote.resource.compound.MultimediaResource
import org.synote.user.group.UserGroup

class DatabaseService {

    boolean transactional = true

	def dataSource
	def securityService
	def db
	def sessionFactory
	
    private getSqlInstance()
    {
    	if(!db)
		{
			db = new Sql(dataSource)
		}
		
		return db
	}
	
//##################################################################################
	/*
	 * List Multimedia
	 */
	//Yunjia: Add date_created last_updated, maybe user_perm_val to the view
	def listMultimedia(params) 
    {
		if(!params.offset)
			params.offset=0
		if(!params.max)
			params.max=10
	
		if(securityService.isLoggedIn())
		{
			def user = securityService.getLoggedUser()

			if(securityService.isNormalLoggedIn())
			{
				return listMultimediaNormalLoggedIn(params,user)
			}
			else
				return listMultimediaAdminLoggedIn(params,user)
		}
		else
			return listMultimediaNotLoggedIn(params)
    }
	
	def countMultimediaList(params)
	{
		if(securityService.isLoggedIn())
		{
			def user = securityService.getLoggedUser()
			
			if(securityService.isNormalLoggedIn())
			{
				return countMultimediaListNormalLoggedIn(params,user)
			}
			else
			{
				return countMultimediaListAdminLoggedIn(params,user)
			}
		}
		else
			return countMultimediaListNotLoggedIn(params)
	}
	
	/*
	 * Use the vw_multimedia_user_permissionvalue to get the multimedia list when
	 * user is not logged in
	 */
	def listMultimediaNotLoggedIn(params)
	{
		def dbInstance = getSqlInstance()
		def orderBy = ''
		if (!params.sort)
		{
			params.sort="id"
		}
		
		orderBy = "order by vw_mup.${params.sort}"
		if (params.order == 'desc')
			orderBy += ' desc'
		
		String query = 
				""" 
					select distinct * from vw_multimedia_user_permissionvalue as vw_mup   
					where public_perm_val>0
					${orderBy}
					limit ${params.max} offset ${params.offset}
				"""
		def result = dbInstance.rows(query)
		
		return result
	}
	
	def countMultimediaListNotLoggedIn(params)
	{
		def dbInstance = getSqlInstance()
		
		String query = 
				""" 
					select count(distinct vw_mup.id) as count 
					from vw_multimedia_user_permissionvalue as vw_mup   
					where public_perm_val>0
				"""
		def result = dbInstance.rows(query)
		
		return result["count"][0]
	}
	
	def listMultimediaNormalLoggedIn(params,user)
	{
		//default permission value for owner and admin
		def DEFAULT_PV_OWNER_AND_ADMIN = PermissionValue.findByName("WRITE")
		
		def dbInstance = getSqlInstance()
		def orderBy = ''
		if (!params.sort)
		{
			params.sort="id"
		}
		
		if(params.sort == "user_perm_val")
			orderBy = "order by ${params.sort}"
		else
			orderBy = "order by vw_mup.${params.sort}"
			
		//Add order type
		if (params.order == 'desc')
			orderBy += ' desc'
		
		//Find a better solution......
		String query = """
			
			
					SELECT DISTINCT vw_mup.*, MAX(mup.user_perm_val) AS user_perm_val
					FROM  vw_multimedia_user_permissionvalue AS vw_mup,
					(
						(
							select vw_mup2.id AS mup_id, ${user.id} AS user_id, ${DEFAULT_PV_OWNER_AND_ADMIN?.val} AS user_perm_val 
							from vw_multimedia_user_permissionvalue AS vw_mup2
							where vw_mup2.owner_id=${user.id}
						)
						UNION ALL
						(
						SELECT vw_mup1.id AS mup_id, ${user.id} AS user_id, public_perm_val AS user_perm_val 
						FROM vw_multimedia_user_permissionvalue AS vw_mup1
						WHERE vw_mup1.owner_id <> ${user.id}
						) 
						UNION ALL
						(
						SELECT vw_mugmp.resource_id AS mup_id, vw_mugmp.user_id as user_id, vw_mugmp.user_perm_val as user_perm_val
						FROM vw_multimedia_user_group_member_permission AS vw_mugmp
						)
					) AS mup
					WHERE user_id = ${user.id} AND vw_mup.id = mup_id AND mup.user_perm_val>0
					GROUP BY vw_mup.id
					${orderBy}
					limit ${params.max} offset ${params.offset}
		"""
		
		def result = dbInstance.rows(query)
		
		return result
	}
	
	def countMultimediaListNormalLoggedIn(params,user)
	{
		def dbInstance = getSqlInstance()
		def DEFAULT_PV_OWNER_AND_ADMIN = PermissionValue.findByName("WRITE")
		String query = """
			SELECT count(distinct mup.mup_id) as count
			FROM  vw_multimedia_user_permissionvalue AS vw_mup,
			(
				(
					select vw_mup2.id AS mup_id, ${user.id} AS user_id, ${DEFAULT_PV_OWNER_AND_ADMIN?.val} AS user_perm_val 
					from vw_multimedia_user_permissionvalue AS vw_mup2
					where vw_mup2.owner_id=${user.id}
				)
				UNION ALL
				(
					SELECT vw_mup1.id AS mup_id, ${user.id} AS user_id, public_perm_val AS user_perm_val 
					FROM vw_multimedia_user_permissionvalue AS vw_mup1
					WHERE vw_mup1.owner_id <> ${user.id}
				) 
				UNION ALL
				(
					SELECT vw_mugmp.resource_id AS mup_id, vw_mugmp.user_id as user_id, vw_mugmp.user_perm_val as user_perm_val
					FROM vw_multimedia_user_group_member_permission AS vw_mugmp
				)
			) AS mup
			WHERE user_id = ${user.id} AND vw_mup.id = mup_id AND mup.user_perm_val>0
		"""
		
		def result = dbInstance.rows(query)
		int count = result[0]["count"]
		return count 
	}
	
	def listMultimediaAdminLoggedIn(params,user)
	{
		//default permission value for owner and admin
		def DEFAULT_PV_OWNER_AND_ADMIN = PermissionValue.findByName("WRITE")
		
		def dbInstance = getSqlInstance()
		def orderBy = ''
		if (!params.sort)
		{
			params.sort="id"
		}
		
		orderBy = "order by ${params.sort}"
		
		if (params.order == 'desc')
			orderBy += ' desc'
		
		String query = """
				(
					select distinct vw_mup.*, ${DEFAULT_PV_OWNER_AND_ADMIN?.val} as user_perm_val
					from vw_multimedia_user_permissionvalue vw_mup
					${orderBy}
					limit ${params.max} offset ${params.offset}
				)
			"""
		
		def result = dbInstance.rows(query)
		return result
	}
	
	def countMultimediaListAdminLoggedIn(params,user)
	{
		return MultimediaResource.count()
	}
	
//##################################################################################
	/*
	 * List group
	 */
	def listGroup(params)
	{
		if(securityService.isLoggedIn())
		{
			def user = securityService.getLoggedUser()
			
			if(securityService.isNormalLoggedIn())
			{
				return listGroupNormalLoggedIn(params,user)
			}
			else
				return listGroupAdminLoggedIn(params,user)
		}
		else
			return listGroupNotLoggedIn(params)
	}
	
	def countGroupList(params)
	{
		if(securityService.isLoggedIn())
		{
			def user = securityService.getLoggedUser()
			
			if(securityService.isNormalLoggedIn())
			{
				return countGroupListNormalLoggedIn(params,user)
			}
			else
				return countGroupListAdminLoggedIn(params,user)
		}
		else
			return countGroupListNotLoggedIn(params)
	}
	
	def listGroupNotLoggedIn(params)
	{
		return UserGroup.findAllByShared(true, params)
	}
	
	def countGroupListNotLoggedIn(params)
	{
		UserGroup.countByShared(true)
	}
	
	def listGroupNormalLoggedIn(params,user)
	{
		
		def orderBy = ''
		if (params.sort)
		{
			orderBy = "order by {ug.${params.sort}}"
			if (params.order == 'desc')
				orderBy += ' desc'
		}
		
		def queryString = """
			select distinct {ug.*}
			from user_group {ug}, user_group_member m
			where ug.shared=true or ug.owner_id=:userId or (ug.id = m.group_id and m.user_id=:userId)
			${orderBy}"""
		
		def session = sessionFactory.getCurrentSession()
		def userGroupList = session.createSQLQuery(queryString)
				.addEntity('ug', UserGroup.class)
				.setLong('userId', user.id)
				.setFirstResult(params.offset ? params.offset.toInteger() : 0)
				.setMaxResults(params.max.toInteger())
				.list()
		
		return userGroupList
	}
	
	def countGroupListNormalLoggedIn(params,user)
	{
		def queryString = """
			select distinct {ug.*}
			from user_group {ug}, user_group_member m
			where ug.shared=true or ug.owner_id=:userId or (ug.id = m.group_id and m.user_id=:userId)
			"""
		def session = sessionFactory.getCurrentSession()
		def allResult = session.createSQLQuery(queryString)
				.addEntity('ug', UserGroup.class)
				.setLong('userId', user.id)
				.list()
		
		return allResult.size()
	}
	def listGroupAdminLoggedIn(params, user)
	{
		return UserGroup.list(params)
	}
	
	def countGroupListAdminLoggedIn(params,user)
	{	
		return UserGroup.count()
	}
	
	//##################################################################################
	/*
	 * List my synmarks
	 */
	def getMySynmarks(params,user)
	{
		def dbInstance = getSqlInstance()
		if(!params.offset)
			params.offset=0
		if(!params.max)
			params.max=10
		
			def orderBy = ''
		if (params.sort)
		{
			orderBy = "order by ${params.sort}"
			if (params.order == 'desc')
				orderBy += ' desc'
		}
		
		String query = 
					"""
					select distinct s.*, group_concat(distinct t.content separator ',') as tags 
					from resource as s inner join resource as t on t.parent_resource_id = s.id  
					where t.class='org.synote.resource.single.text.SynmarkTag' and s.owner_id=${user.id} group by s.id ${orderBy} 
					limit ${params.max} offset ${params.offset}
					"""
					
		//println "query:"+query
		def synmarkList = dbInstance.rows(query)
		return synmarkList
	}
	
	def getMySynmarksCount(params,user)
	{
		def dbInstance = getSqlInstance()
		String query = """
					select distinct s.*, group_concat(distinct t.content separator ',') as tags
					from resource as s inner join resource as t on t.parent_resource_id = s.id
					where t.class='org.synote.resource.single.text.SynmarkTag' and s.owner_id=${user.id}
					group by s.id
				"""
		def synmarkList = dbInstance.rows(query)
		return synmarkList.size()
	}
	
	def searchMySynmarks(params, user, text)
	{
		def dbInstance = getSqlInstance()
		if(!params.offset)
			params.offset=0
		if(!params.max)
			params.max=10
		def orderBy = ''
		if (params.sort)
		{
			orderBy = "order by synmark.${params.sort}"
			if (params.order == 'desc')
				orderBy += ' desc'
		}
		
		String query = """select synmark.*
							from (
								select distinct s.*, group_concat(distinct t.content separator ',') as tags
								from resource as s inner join resource as t on t.parent_resource_id = s.id
								where t.class='org.synote.resource.single.text.SynmarkTag' and s.owner_id=${user.id}
								group by s.id
							) as synmark
							where synmark.tags like "%${text}%" or synmark.title like "%${text}%"
							${orderBy}
							limit ${params.max} offset ${params.offset}
							"""
		
		def synmarkList = dbInstance.rows(query)
		return synmarkList
	}
	
	def searchMySynmarksCount(params,user,text)
	{
		def dbInstance = getSqlInstance()
		String query = """select synmark.*
							from (
								select distinct s.*, group_concat(distinct t.content separator ',') as tags
								from resource as s inner join resource as t on t.parent_resource_id = s.id
								where t.class='org.synote.resource.single.text.SynmarkTag' and s.owner_id=${user.id}
								group by s.id
							) as synmark
							where synmark.tags like "%${text}%" or synmark.title like "%${text}%"
							"""
		
		def synmarkList = dbInstance.rows(query)
		return synmarkList.size()
	}
}
