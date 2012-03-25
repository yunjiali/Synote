package org.synote.user.group

import org.codehaus.groovy.grails.plugins.springsecurity.Secured
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
	static Map allowedMethods = [save: 'POST', update: 'POST', delete: 'POST', savePermission:'POST']
	
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
		//Do nothing
	}
	
	/*
	 * List all the groups
	 */
	def listGroupsAjax = {
		def groupList = userGroupService.getGroupsAsJSON(params)
		render groupList as JSON
		return
	}
	/*
	 * List all the current group memebers
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
	
	def show = {
		
		def userGroup = UserGroup.get(params.id)
		
		if (!userGroup)
		{
			flash.error = "Group with id ${params.id} not found"
			redirect(action: list)
			return
		}
		
		def canJoinGroup = false //As a normal use, you can join the group
		def isMember = false // As a normal user, you are a member of the group and you can remove yourself from the group
		def isOwnerOrAdmin = false //As the owner or admin, you can edit the group information
		//As the owner or admin, you can add a new member to the group
		//As the owner or admin, you can add a new recording with the permission different from public to this group
		
		def user = securityService.getLoggedUser()
		if(user)
		{
			if(securityService.isOwnerOrAdmin(userGroup.owner.id))
			{
				isOwnerOrAdmin = true	//We can only add group owner's recordings
			}
			else if(UserGroupMember.findByGroupAndUser(userGroup,user) != null)//the logged in user is a member of the group
			{
				isMember = true
			}
			else //the logged in user is not the owner and is not a member of the group yet
			{
				canJoinGroup = true
			}
		}
		else
		{
			//Not logged in and the group is not shared
			if(!userGroup.shared)
			{
				flash.error = "Permission denied - cannot show group with id ${params.id}."
				redirect(action: list)
				return
			}
		}
		
		def memberCount = UserGroupMember.countByGroup(userGroup)+1
		def recordingCount = ResourcePermission.countByGroup(userGroup)
		return [userGroup: userGroup, canJoinGroup:canJoinGroup, isMember:isMember, isOwnerOrAdmin:isOwnerOrAdmin, 
			memberCount:memberCount,recordingCount:recordingCount]
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
		redirect(action: list)
		return
	}
	/*
	 * Open create.gsp for userGroup
	 */
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def create = {	
		def userGroup = new UserGroup(params)
		return [userGroup: userGroup]
	}
	
	/*
	 * save newly created user group
	 */
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def save = {
		def userGroup = new UserGroup(params)
		
		if (securityService.isAdminLoggedIn())
		{
			def owner = User.findByUserName(params.ownerUserName)
			if (owner)
			userGroup.owner = owner
			else
			userGroup.errors.rejectValue('owner', null, "User ${params.ownerUserName} not found")
		}
		else
		{
			userGroup.owner = securityService.getLoggedUser()
		}
		
		if(userGroup.hasErrors() || !userGroup.merge(flush:true))
		{
			render(view: 'create', model: [userGroup: userGroup, ownerUserName: params.ownerUserName])
			return
		}
		
		def newGroup = UserGroup.findByNameAndOwner(userGroup.name, userGroup.owner)
		
		flash.message = "Group ${userGroup.name} was successfully created"
		redirect(action: show, id: newGroup.id)
	}
	
	/*
	 * open edit usergroup page
	 */
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def edit = {
		
		def userGroup = UserGroup.get(params.id)
		
		if (!userGroup)
		{
			flash.error = "Group with id ${params.id} not found"
			redirect(controller:'user', action: 'listGroups')
			return
		}
		
		if (!securityService.isOwnerOrAdmin(userGroup.owner.id))
		{
			flash.error = "Permission denied - cannot edit group with id ${params.id}"
			redirect(controller:'user', action: 'listGroups')
			return
		}
		
		return [userGroup: userGroup, ownerUserName: userGroup.owner.userName]
	}
	
	//Delete the code that admin can change the owner
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def update = {
		
		def userGroup = UserGroup.get(params.id)
		def owner= securityService.getLoggedUser();
		
		if (!userGroup)
		{
			flash.error = "Group with id ${params.id} not found"
			redirect(controller:'user', action: 'listGroups')
			return
		}
		
		//If current logged in user is not the owner
		if (!securityService.isOwnerOrAdmin(userGroup.owner.id))
		{
			flash.error = "Permission denied - cannot update group with id ${params.id}"
			redirect(controller:'user', action: 'listGroups')
			return
		}
		
		if(!params.name)
		{
			flash.error = "Please input the group name."
			render(controller:'userGroup', view: 'edit', model: [userGroup: userGroup, ownerUserName: params.ownerUserName])
			return
		}
		
		def oldGroup = UserGroup.findByNameAndOwner(params.name, owner)
		
		if(oldGroup)
		{
			if(oldGroup.id != userGroup.id)
			{
				flash.error = "The owner ${owner} has already got a group with name ${params.name}. Please select another name."
				render(controller:'userGroup', view: 'edit', model: [userGroup: userGroup, ownerUserName: params.ownerUserName])
				return
			}
		}
		
		userGroup.properties = params
		
		if(userGroup.hasErrors() || !userGroup.save())
		{
			render(controller:'userGroup', view: 'edit', model: [userGroup: userGroup, ownerUserName: params.ownerUserName])
			return
		}
		
		flash.message = "Group ${userGroup.name} was successfully updated"
		redirect(controller:'user', action: 'listGroup')
		return
	}
	
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def delete = {
		def owner = securityService.getLoggedUser()
		def userGroup = UserGroup.get(params.id)
		
		if (!userGroup)
		{
			flash.error = "Group with id ${params.id} not found"
			redirect(controller:'user', action: 'listGroup')
			return
		}
		
		//if the logged in user is not the owner or admin
		if (!securityService.isOwnerOrAdmin(userGroup.owner.id))
		{
			flash.error = "Permission denied - cannot delete group with id ${params.id}"
			redirect(controller:'user', action: 'listGroup')
			return
		}
		
		def name = userGroup.name
		
		userGroup.delete()
		flash.message = "Group ${name} was successfully deleted"
		redirect(controller:'user', action: 'listGroup')
		return
	}
	
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
	
	/*
	 * Edit group member page. You can add memebers, remove members on this page
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
	def deleteMember = {
		if (!securityService.isLoggedIn())
		{
			flash.message = "User login required"
			redirect(controller: 'login', action: 'auth')
			return
		}
		
		def member = UserGroupMember.get(params.id)
		
		if (!member)
		{
			flash.error = "Member with id ${params.id} not found"
			redirect(action: list)
			return
		}
		
		if (!securityService.isOwnerOrAdmin(member.group.owner.id))
		{
			flash.error = "Permission denied - cannot delete member with id ${params.id}"
			redirect(action: list)
			return
		}
		
		def userName = member.user.userName
		def group = member.group
		
		member.delete()
		
		flash.message = "Member ${userName} was successfully removed from Group ${group.name}"
		redirect(action: 'editMember', id: group.id, params:params)
		return
	}

	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def savePermission = {
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
			flash.error = "Permission denied - cannot add permission to group with id ${params.id}"
			redirect(action: list)
			return
		}
		
		def resource = MultimediaResource.findById(params.recordingId)
		if(!resource)
		{
			flash.error ="Cannot find resource"
			redirect(action: 'editPermission', id:userGroup.id, model:[params:params])
			return
		}
		
		def perm = params.perm ? PermissionValue.findByVal(params.perm) : PermissionValue.findByVal(0)
		if(!perm)
		{
			flash.error = "The permission is not specified"
			redirect(action: 'editPermission', id:userGroup.id, model:[params:params])
			return
		}
		
		//check if the logged in user is the resource owner
		if (!securityService.isOwnerOrAdmin(resource.owner.id))
		{	
			flash.error = "Permission denied - cannot add recording '${resource.title}' to group ${userGroup.name}"
			redirect(action: 'editPermission', id:userGroup.id, model:[params:params])
			return
		}
		
		def permission = new ResourcePermission(group: userGroup, resource: resource, perm: perm)
		if(permission.validate()) {
		// do something with user
		}
		else {
			permission.errors.allErrors.each {
				println it
			}
		}
		if(permission.hasErrors() || !permission.save())
		{	
			flash.error = "Cannot add recording '${resource.title}' to group ${userGroup.name}"
			redirect(action: 'editPermission', id:userGroup.id, model:[params:params])
			return
		}
		
		flash.message = "Recording '${resource.title}' was successfully added to group ${userGroup.name}"
		redirect(action: 'editPermission', id:userGroup.id, model:[params:params])
		return
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
	
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def deletePermission = {
		
		def permission = ResourcePermission.get(params.id)
		
		if (!permission)
		{
			flash.error = "Permission with id ${params.id} not found"
			redirect(action: list)
			return
		}
		
		def userGroup = permission.group
		
		if (!securityService.isOwnerOrAdmin(userGroup.owner.id))
		{
			flash.error = "Permission denied - cannot edit the recordings in this group"
			redirect(action: list)
			return
		}
		
		def title = permission.resource.title
		def group = permission.group.id
		
		permission.delete()
		
		flash.message = "Recording ${title} was successfully removed from Group ${userGroup.name}"
		redirect(action: 'editPermission', id: userGroup.id, params:params)
		return
	}
}

