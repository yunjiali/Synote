package org.synote.user.group

import org.synote.utils.DatabaseService
import org.synote.user.SecurityService
import org.synote.utils.UtilsService
import org.synote.permission.PermissionValue
import org.synote.permission.ResourcePermission
import org.synote.user.User
import org.synote.resource.compound.MultimediaResource
import grails.converters.*
import groovy.json.*

class UserGroupService {

    static transactional = true

	def databaseService
	def securityService
	def utilsService
	
	/*
	 * Only list my groups
	 */
	def getMyGroupsAsJSON(params) {
		def sortIndex = params.sidx ?: 'id'
		def sortOrder  = params.sord ?: 'asc'
		if(!params.rows)
			params.rows ="10"
			
		def maxRows = Integer.valueOf(params.rows)
		if(!params.page)
			params.page ="1"
		def currentPage = Integer.valueOf(params.page) ?: 1
		def rowOffset = currentPage == 1 ? 0 : (currentPage - 1) * maxRows
		def user=securityService.getLoggedUser()
		def groupList = UserGroup.createCriteria().list(max:maxRows, offset:rowOffset){
			eq('owner',user)
			if(params.text?.trim()?.length()>0)
			{
				ilike("name","%${params.text}%",[ignoreCase:true])
			}
			order(sortIndex,sortOrder)
		}
		def totalRows = groupList.totalCount
		def numberOfPages = Math.ceil(totalRows/maxRows)
		def results = []
		groupList?.collect{ r->
			def group = UserGroup.get(r.id)
			def item = [
				id:r.id,
				//owner_name:r.owner.userName, Don't need owner_name, it's you!
				name:r.name,
				shared:r.shared,
				description:r.description,
				date_created: r.dateCreated,
				member_count: UserGroupMember.countByGroup(group)+1, //At least, there will be owner itself in the group
				recording_count:ResourcePermission.countByGroup(group),
			]
			results << item
		}
		def jqGridData = [rows:results, page:currentPage, records:totalRows, total:numberOfPages]
		return jqGridData
    }
	
	/*
	 * List the groups a user joined
	 */
	def getMyJoinedGroupsAsJSON(params)
	{
		
	}
	
	//List all the groups arranged by created date
	def getGroupsAsJSON(jqGridParams) {
		//Yunjia: there is some redundant programme here...
		def sortIndex = jqGridParams.sidx ?: 'dateCreated'
		def sortOrder  = jqGridParams.sord ?: 'asc'
		if(!jqGridParams.rows)
			jqGridParams.rows ="10"
		def maxRows = Integer.valueOf(jqGridParams.rows)
		if(!jqGridParams.page)
			jqGridParams.page ="1"
		def currentPage = Integer.valueOf(jqGridParams.page) ?: 1
		def rowOffset = currentPage == 1 ? 0 : (currentPage - 1) * maxRows
		
		def user = securityService.getLoggedUser()
		
		def gParams = utilsService.mapJQGridParamsToGrails(jqGridParams)
		
		def groupList = databaseService.listGroup(gParams)
		int count = databaseService.countGroupList(gParams)
				
		def numberOfPages = Math.ceil(count / maxRows)
		
		
		def results = []
		
		groupList?.collect{ r->
			
			def group = UserGroup.get(r.id)
			def item = [
				id:r.id,
				name:r.name,
				shared:r.shared,
				owner_name:r.owner.userName,
				member_count: UserGroupMember.countByGroup(group)+1, //At least, there will be owner itself in the group
				recording_count:ResourcePermission.countByGroup(group),
				date_created: r.dateCreated,
			]
			results << item
		}
		def jqGridData = [rows:results, page:currentPage, records:count, total:numberOfPages]
		return jqGridData
	}
	
	/*
	 * Get all the members of a group, use for getGroupMembersAjax in userGroup controller
	 */
	def getGroupMembersAsJSON(params, userGroup)
	{
		def sortIndex = params.sidx ?: 'id'
		def sortOrder  = params.sord ?: 'asc'
		if(!params.rows)
			params.rows ="10"
			
		def maxRows = Integer.valueOf(params.rows)
		if(!params.page)
			params.page ="1"
		def currentPage = Integer.valueOf(params.page) ?: 1
		def rowOffset = currentPage == 1 ? 0 : (currentPage - 1) * maxRows
		def user=securityService.getLoggedUser()
		def memberList = UserGroupMember.createCriteria().list(max:maxRows, offset:rowOffset){
			eq('group',userGroup)
			order(sortIndex,sortOrder).ignoreCase()
		}
		def totalRows = memberList.totalCount
		def numberOfPages = Math.ceil(totalRows/maxRows)
		def results = []
		memberList?.collect{ r->
			def member = r.user
			def item = [
				id:r.id,//Id here is the membershipid
				//owner_name:r.owner.userName, Don't need owner_name, it's you!
				userName:member.userName,
				firstName:member.firstName,
				lastName:member.lastName
			]
			results << item
		}
		def jqGridData = [rows:results, page:"${currentPage}", records:"${totalRows}", total:numberOfPages]
		return jqGridData
	}
	
	def getPossibleUsersAsJSON(params,userGroup)
	{
		//Yunjia: Need to remove Admin in the list
		def sortIndex = params.sidx ?: 'id'
		def sortOrder  = params.sord ?: 'asc'
		if(!params.rows)
			params.rows ="10"
			
		def maxRows = Integer.valueOf(params.rows)
		if(!params.page)
			params.page ="1"
		def currentPage = Integer.valueOf(params.page) ?: 1
		def rowOffset = currentPage == 1 ? 0 : (currentPage - 1) * maxRows
		
		def userList = User.createCriteria().list(max:maxRows, offset:rowOffset){
			or{
				ilike("userName","%${params.text}%")
				ilike("firstName","%${params.text}%")
				ilike("lastName","%${params.text}%")
			}
			order(sortIndex,sortOrder).ignoreCase()
		}
		def totalRows = userList.totalCount
		def numberOfPages = Math.ceil(totalRows/maxRows)
		def results = []
		userList?.collect{ r->
			def canAdd = false
			if(r.userName!="admin")//Yunjia: better use some other way to check if it's admin
			{
				if(UserGroupMember.findByUserAndGroup(r,userGroup) == null)
				{
					canAdd = true
				}
			}
			def item = [
				id:r.id,
				//owner_name:r.owner.userName, Don't need owner_name, it's you!
				userName:r.userName,
				firstName:r.firstName,
				lastName: r.lastName, //At least, there will be owner itself in the group
				canAdd:canAdd
			]
			results << item
		}
		def jqGridData = [rows:results, page:"${currentPage}", records:"${totalRows}", total:numberOfPages]
		return jqGridData
	}
	
	/*
	 * Get group recordings as json
	 */
	def getGroupRecordingsASJSON(params,userGroup)
	{
		def sortIndex = params.sidx ?: 'id'
		def sortOrder  = params.sord ?: 'asc'
		if(!params.rows)
			params.rows ="10"
			
		def maxRows = Integer.valueOf(params.rows)
		if(!params.page)
			params.page ="1"
		def currentPage = Integer.valueOf(params.page) ?: 1
		def rowOffset = currentPage == 1 ? 0 : (currentPage - 1) * maxRows
		
		def groupRecordingList = ResourcePermission.createCriteria().list(max:maxRows, offset:rowOffset){
			eq('group',userGroup)
			order(sortIndex,sortOrder).ignoreCase()
		}
		def totalRows = groupRecordingList.totalCount
		def numberOfPages = Math.ceil(totalRows/maxRows)
		def results = []
		groupRecordingList?.collect{ r->
			def res = r.resource
			def perm =r.perm
			def item = [
				id:r.id,  //This is ResourcePermission Id
				title:res.title,
				perm:perm.name,
				public_perm_val:perm.val,
			]
			results<<item
		}
		def jqGridData = [rows:results, page:"${currentPage}", records:"${totalRows}", total:numberOfPages]
		return jqGridData
	}
	
	def getPossibleRecordingsAsJSON(params,userGroup)
	{
		def sortIndex = params.sidx ?: 'id'
		def sortOrder  = params.sord ?: 'asc'
		if(!params.rows)
			params.rows ="10"
			
		def maxRows = Integer.valueOf(params.rows)
		if(!params.page)
			params.page ="1"
		def currentPage = Integer.valueOf(params.page) ?: 1
		def rowOffset = currentPage == 1 ? 0 : (currentPage - 1) * maxRows
		def user=securityService.getLoggedUser()
		def multimediaList = MultimediaResource.createCriteria().list(max:maxRows, offset:rowOffset){
			eq('owner',user)
			if(params.text?.trim()?.length()>0)
			{
				ilike("title","%${params.text}%")
			}
			order(sortIndex,sortOrder).ignoreCase()
		}
		def totalRows = multimediaList.totalCount
		def numberOfPages = Math.ceil(totalRows/maxRows)
		def results =[] 
		
		multimediaList?.collect{ r->
			def canAdd = false
			if(ResourcePermission.findByResourceAndGroup(r,userGroup) == null)
			{
				canAdd = true
			}
			def item = [
				id:r.id,
				//owner_name:r.owner.userName, Don't need owner_name, it's you!
				title:r.title,
				url:MultimediaResource.findById(r.id).url?.url,
				public_perm_name:r.perm?.name,
				perm: r.perm,
				date_created:r.dateCreated,
				last_updated:r.lastUpdated,
				canAdd:canAdd
			]
			results << item
		}
		def jqGridData = [rows:results, page:"${currentPage}", records:"${totalRows}", total:numberOfPages]
		return jqGridData
	}
}
