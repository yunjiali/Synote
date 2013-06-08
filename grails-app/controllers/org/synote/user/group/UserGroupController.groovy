package org.synote.user.group

import grails.plugins.springsecurity.Secured
import org.synote.user.*
import org.synote.user.group.*
import org.synote.permission.*
import org.synote.resource.compound.MultimediaResource
import org.synote.utils.DatabaseService
import org.synote.user.group.UserGroupService
import grails.converters.*
import groovy.json.*

class UserGroupController {
	//TODO: Many errors about multimediaresource and permissions here, user login page has changed
	//static Map allowedMethods = [save: 'POST', update: 'POST', delete: 'POST', savePermission:'POST']
	
	def securityService
	def sessionFactory
	def databaseService
	def userGroupService
	
	def index = {
		redirect(action: list, params: params)
	}
	
	/*
	 * Open list.gsp page for usergroup
	 */
	def list = {
		def groupList = userGroupService.getGroupsAsJSON(params)
		return [groupList:groupList, params:params]
	}
	
	
	
	def show = {
		
		def userGroup = UserGroup.get(params.id)
		
		if (!userGroup)
		{
			flash.error = "Group with id ${params.id} not found"
			redirect(action: list)
			return
		}
		
		def isMember = false // As a normal user, you are a member of the group and you can remove yourself from the group
		def isOwnerOrAdmin = false //As the owner or admin, you can edit the group information
		def canShowFigures = false
		
		def user = securityService.getLoggedUser()
		if(user)
		{
			if(securityService.isOwnerOrAdmin(userGroup.owner.id))
			{
				isOwnerOrAdmin = true	//We can only add group owner's recordings
				isMember = true
				canShowFigures = true
			}
			else if(UserGroupMember.findByGroupAndUser(userGroup,user) != null)//the logged in user is a member of the group
			{
				isMember = true
				canShowFigures = true
			}
			else if(userGroup.shared == true)//the logged in user is not the owner and is not a member of the group yet
			{
				canShowFigures = true
			}
		}
		else
		{
			//Not logged in and the group is not shared
			if(userGroup.shared == true)
			{
				canShowFigures = true
			}
		}
		
		def members = null
		def recordings = null
		if(canShowFigures == true)
		{
			members = UserGroupMember.findAllByGroup(userGroup)
			recordings = ResourcePermission.findAllByGroup(userGroup)
		}
		return [userGroup: userGroup, canShowFigures:canShowFigures, isMember:isMember, isOwnerOrAdmin:isOwnerOrAdmin, 
			members: members,recordings: recordings]
	}
	
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def joinGroup = {
		//return to show page
		def user=securityService.getLoggedUser()
		if (!user)
		{
			flash.message = "User login required..."
			redirect(controller: 'login', action: 'auth')
			return
		}
		def userGroup = UserGroup.get(params.id)
		if (!userGroup)
		{
			flash.error = "Group with id ${params.id} not found"
			redirect(action: list)
			return
		}
		if(!userGroup.shared) //You cannot join a not-shared group
		{
			flash.error = "Permission denied - cannot show group with id ${params.id}."
			redirect(action: list)
			return
		}
		
		if (UserGroupMember.findByUserAndGroup(user, userGroup))
		{
			flash.error = "User ${user.userName} is already member of group ${userGroup.name}"
			//render(view: 'createMember', model: [userGroup: userGroup, memberUserName: params.memberUserName, memberUserNameError: 'errors'])
			redirect(action:show,id:params.id)
			return
		}
		
		def member = new UserGroupMember(user: user, group: userGroup)
		
		if(member.hasErrors() || !member.save())
		{
			//render(view: 'createMember', model: [userGroup: userGroup, memberUserName: params.memberUserName, memberUserNameError: 'errors'])
			redirect(action:show,id:params.id)
			return
		}
		
		flash.message = "You have successfully joined group ${userGroup.name}"
		redirect(action: show, id: userGroup.id)
		return
	}
	
	/*
	 * User remove him or herself from the group, not used for owner of the group to remove user
	 */
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def removeFromGroup = {
		
		def user=securityService.getLoggedUser()
		if (!user)
		{
			flash.message = "User login required..."
			redirect(controller: 'login', action: 'auth')
			return
		}
		def userGroup = UserGroup.get(params.id)
		if (!userGroup)
		{
			flash.error = "Group with id ${params.id} not found"
			redirect(action: list)
			return
		}
		//The group doesn't need to be shared
		def member = UserGroupMember.findByUserAndGroup(user,userGroup)
		if (!member)
		{
			flash.error = "You are not in the group."
			redirect(action: list)
			return
		}
		
		//The you doesn't need to be the owner of the group
		
		def userName = user.userName
		def group = userGroup.id
		
		member.delete()
		
		//return to list page
		flash.message = "You have left group ${userGroup.name}"
		redirect(controller:'user', action: 'listGroups')
		return
	}
	
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def delete = {
		def owner = securityService.getLoggedUser()
		def userGroup = UserGroup.get(params.id)
		
		if (!userGroup)
		{
			flash.error = "Group with id ${params.id} not found"
			redirect(controller:'user', action: 'listGroups')
			return
		}
		
		//if the logged in user is not the owner or admin
		if (!securityService.isOwnerOrAdmin(userGroup.owner.id))
		{
			flash.error = "Permission denied - cannot delete group with id ${params.id}"
			response.status = 400
			render view:'/error/400'
			return
		}
		
		def name = userGroup.name
		
		userGroup.delete()
		flash.message = "Group ${name} was successfully deleted"
		redirect(controller:'user', action: 'listGroups')
		return
	}
	
	/*Deprecated*/
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def saveMember = {
		if (!securityService.isLoggedIn())
		{
			flash.message = "User login required"
			redirect(controller: 'login', action: 'auth')
			return
		}
		
		def userGroup = UserGroup.get(params.id)
		
		if (!userGroup)
		{
			flash.error = "Group with id ${params.id} not found"
			redirect(action: list)
			return
		}
		
		if (!securityService.isOwnerOrAdmin(userGroup.owner.id))
		{
			flash.error = "Permission denied - cannot add member into group with id ${params.id}"
			redirect(action: list)
			return
		}
		
		def user = User.findById(params.userId)
		params.remove("userId")
		
		if (!user || securityService.isAdmin(user))
		{
			flash.error = "User ${params.memberUserName} not found"
			//render(view: 'createMember', model: [userGroup: userGroup, memberUserName: params.memberUserName, memberUserNameError: 'errors'])
			redirect(action:'editMember',id:params.id,params:params)
			return
		}
		
		if (UserGroupMember.findByUserAndGroup(user, userGroup))
		{
			flash.error = "User ${user.userName} is already member of group ${userGroup.name}"
			//render(view: 'createMember', model: [userGroup: userGroup, memberUserName: params.memberUserName, memberUserNameError: 'errors'])
			redirect(action:'editMember',id:params.id,params:params)
			return
		}
		
		def member = new UserGroupMember(user: user, group: userGroup)
		
		if(member.hasErrors() || !member.save())
		{
			//render(view: 'createMember', model: [userGroup: userGroup, memberUserName: params.memberUserName, memberUserNameError: 'errors'])
			flash.error = "Cannot add User ${user.userName} is to group ${userGroup.name}"
			redirect(action:editMember,id:params.id,params:params)
			return
		}
		
		flash.message = "User ${user.userName} was successfully added into group ${userGroup.name}"
		redirect(action: 'editMember', id: userGroup.id, params:params)
		return
	}
	
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def deleteMember = {
		if (!securityService.isLoggedIn())
		{
			flash.message = "User login required"
			redirect(controller: 'login', action: 'auth')
			return
		}
		
		def member = UserGroupMember.get(params.id?.toLong())
		
		if (!member)
		{
			flash.error = "Member with id ${params.id} not found"
			response.status = 400
			render view:'/error/400'
			return
		}
		
		if (!securityService.isOwnerOrAdmin(member.group.owner.id))
		{
			flash.error = "Permission denied - cannot delete member with id ${params.id}"
			response.status = 400
			render view:'/error/400'
			return
		}
		
		def userName = member.user.userName
		def group = member.group
		
		member.delete()
		
		flash.message = "Member ${userName} was successfully removed from Group ${group.name}"
		redirect(action: 'show', id: group.id, params:params)
		return
	}
	
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def deletePermission = {
		
		def permission = ResourcePermission.get(params.id?.toLong())
		
		if (!permission)
		{
			flash.error = "Permission with id ${params.id} not found"
			response.status = 400
			render 
		}
		
		def userGroup = permission.group
		
		if (!securityService.isOwnerOrAdmin(userGroup.owner.id))
		{
			flash.error = "Permission denied - cannot edit the recordings in this group"
			response.status = 400
			render 
		}
		
		def title = permission.resource.title
		def group = permission.group.id
		
		permission.delete()
		
		flash.message = "Recording ${title} was successfully removed from Group ${userGroup.name}"
		redirect(action: 'show', id: userGroup.id, params:params)
		return
	}
	
	/*------- Deprecated ---------------*/
	
	/*
	 * Deprecated
	 */
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def editMember = {
		def userGroup = UserGroup.get(params.id)
		
		if (!userGroup)
		{
			flash.error = "Group with id ${params.id} not found"
			redirect(action: list)
			return
		}
		
		def user = securityService.getLoggedUser()
		if (!user)
		{
			flash.message = "User login required"
			redirect(controller: 'login', action: 'auth')
			return
		}
		
		if (!securityService.isOwnerOrAdmin(userGroup.owner.id))
		{
			flash.error = "Permission denied - cannot edit the members in this group"
			redirect(action: list)
			return
		}
		
		return [userGroup: userGroup]
	}
	
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def editPermission = {
		def userGroup = UserGroup.get(params.id)
		
		if (!userGroup)
		{
			flash.error = "Group with id ${params.id} not found"
			redirect(action: list)
			return
		}
		
		def user = securityService.getLoggedUser()
		if (!user)
		{
			flash.message = "User login required"
			redirect(controller: 'login', action: 'auth')
			return
		}
		
		if (!securityService.isOwnerOrAdmin(userGroup.owner.id))
		{
			flash.error = "Permission denied - cannot edit the members in this group"
			redirect(action: list)
			return
		}
		
		return [userGroup: userGroup]
	}
	
	/*
	 * List all the current group memebers
	 *  DEPRECATED
	 */
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def listGroupMembersAjax = {
		def userGroup = UserGroup.get(params.id)
		if (!userGroup)
		{
			flash.error = "Group with id ${params.id} not found"
			redirect(action: list)
			return
		}
		
		def user = securityService.getLoggedUser()
		if (!user)
		{
			flash.message = "User login required"
			redirect(controller: 'login', action: 'auth')
			return
		}
		
		if (!securityService.isOwnerOrAdmin(userGroup.owner.id))
		{
			flash.error = "Permission denied - cannot edit the members in this group"
			redirect(action: list)
			return
		}
		
		def groupMemberList = userGroupService.getGroupMembersAsJSON(params,userGroup)
		render groupMemberList as JSON
		return
	}
	
	/*
	 * List all the possible users that could join the group
	 * List users according to the "text" param
	 * DEPRECATED
	 */
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def listPossibleUsersAjax = {
		def userGroup = UserGroup.get(params.id)
		if (!userGroup)
		{
			flash.error = "Group with id ${params.id} not found"
			redirect(action: list)
			return
		}
		def user = securityService.getLoggedUser()
		if (!user)
		{
			flash.message = "User login required"
			redirect(controller: 'login', action: 'auth')
			return
		}
		
		if (!securityService.isOwnerOrAdmin(userGroup.owner.id))
		{
			flash.error = "Permission denied - cannot edit the members in this group"
			redirect(action: list)
			return
		}
		
		def searchText =  params.text
		if(searchText==null || searchText.trim().size()==0)
		{
			render "Please provide the search text"
			return
		}
		def userList = userGroupService.getPossibleUsersAsJSON(params,userGroup)
		render userList as JSON
		return
	}
	
	/*
	 * List all the possible recordings that could be added into the group
	 */
	def listPossibleRecordingsAjax = {
		def userGroup = UserGroup.get(params.id)
		if (!userGroup)
		{
			flash.error = "Group with id ${params.id} not found"
			redirect(action: list)
			return
		}
		def user = securityService.getLoggedUser()
		if (!user)
		{
			flash.message = "User login required"
			redirect(controller: 'login', action: 'auth')
			return
		}
		
		if (!securityService.isOwnerOrAdmin(userGroup.owner.id))
		{
			flash.error = "Permission denied - cannot edit the members in this group"
			redirect(action: list)
			return
		}
		
		def recordingList = userGroupService.getPossibleRecordingsAsJSON(params,userGroup)
		render recordingList as JSON
		return
	}
	/*
	 * List group recordings, used for editPermissions
	 */
	def listGroupRecordingsAjax = {
		def userGroup = UserGroup.get(params.id)
		if (!userGroup)
		{
			flash.error = "Group with id ${params.id} not found"
			redirect(action: list)
			return
		}
		def user = securityService.getLoggedUser()
		if (!user)
		{
			flash.message = "User login required"
			redirect(controller: 'login', action: 'auth')
			return
		}
		
		if (!securityService.isOwnerOrAdmin(userGroup.owner.id))
		{
			flash.error = "Permission denied - cannot edit the members in this group"
			redirect(action: list)
			return
		}
		
		def recordingList = userGroupService.getGroupRecordingsASJSON(params,userGroup)
		render recordingList as JSON
		return
	}
}

