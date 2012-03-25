package org.synote.user

import org.codehaus.groovy.grails.plugins.springsecurity.Secured
import org.synote.user.UserRole
import org.synote.resource.compound.*
import org.synote.resource.single.text.TagResource
import grails.converters.*
import org.synote.resource.ResourceService
import org.synote.user.group.UserGroup
import org.synote.user.group.UserGroupMember
import org.synote.user.group.UserGroupService

class UserController {

	def securityService
	def resourceService
	def userGroupService

	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def index = {
		//Do nothing
	}

	@Secured(['ROLE_ADMIN'])
	def list = {
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		//UserRole role = UserRole.findByAuthority("ROLE_NORMAL")

		def userInstanceList = User.createCriteria().list(params){
			authorities{ eq("authority","ROLE_NORMAL")	 }
		}
		[userInstanceList: userInstanceList, userInstanceTotal: User.count()]
	}

	@Secured(['ROLE_ADMIN'])
	def create = {
		def userInstance = new User()
		userInstance.properties = params
		return [userInstance: userInstance]
	}

	@Secured(['ROLE_ADMIN'])
	def save = {
		def userInstance = new User(params)
		if (userInstance.save(flush: true)) {
			flash.message = "${message(code: 'default.created.message', args: [message(code: 'user.label', default: 'User'), userInstance.id])}"
			redirect(action: "show", id: userInstance.id)
		}
		else {
			render(view: "create", model: [userInstance: userInstance])
		}
	}

	/*
	 * Used for linked data dereferencing
	 */
	def show = {
		def userInstance = User.get(params.id?.toLong())
		if (!userInstance || securityService.isAdmin(userInstance)) {
			flash.error = "Cannot find user"
			redirect(action: "index")
		}
		else {
			render (view:'show',model:[userInstance: userInstance])
		}
		return
	}
	
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def showUserProfile = {
		def userInstance = securityService.getLoggedUser()
		if (!userInstance) {
			flash.error = "Cannot find the logged in user."
			redirect(action: "index")
		}
		else {
			userInstance = User.get(userInstance.id)
			render (view:'showUserProfile',model:[userInstance: userInstance])
		}
		return
	}

	@Secured(['ROLE_ADMIN'])
	def edit = {
		def userInstance = User.get(params.id)
		if (!userInstance) {
			flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])}"
			redirect(action: "list")
		}
		else {
			return [userInstance: userInstance]
		}
	}

	@Secured(['ROLE_ADMIN'])
	def update = {
		def userInstance = User.get(params.id)
		if (userInstance) {
			if (params.version) {
				def version = params.version.toLong()
				if (userInstance.version > version) {

					userInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [
						message(code: 'user.label', default: 'User')]
					as Object[], "Another user has updated this User while you were editing")
					render(view: "edit", model: [userInstance: userInstance])
					return
				}
			}
			userInstance.properties = params
			if (!userInstance.hasErrors() && userInstance.save(flush: true)) {
				flash.message = "${message(code: 'default.updated.message', args: [message(code: 'user.label', default: 'User'), userInstance.id])}"
				redirect(action: "show", id: userInstance.id)
			}
			else {
				render(view: "edit", model: [userInstance: userInstance])
			}
		}
		else {
			flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])}"
			redirect(action: "list")
		}
	}

	@Secured(['ROLE_ADMIN'])
	def delete = {
		def userInstance = User.get(params.id)
		if (userInstance) {
			try {
				userInstance.delete(flush: true)
				flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'user.label', default: 'User'), params.id])}"
				redirect(action: "list")
			}
			catch (org.springframework.dao.DataIntegrityViolationException e) {
				flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'user.label', default: 'User'), params.id])}"
				redirect(action: "show", id: params.id)
			}
		}
		else {
			flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])}"
			redirect(action: "list")
		}
	}

	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def changePassword=
	{
		//Do nothing
	}

	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def handleChangePassword=
	{
		def user = securityService.getLoggedUser()
		user=User.get(user.id)

		if(!params.oldPassword)
		{
			flash.error ="Please input your old password"
			redirect(action: 'changePassword')
			return
		}

		if(!params.newPassword)
		{
			flash.error ="Please input your new password"
			redirect(action: 'changePassword')
			return
		}

		if(!params.confirmNewPassword)
		{
			flash.error="Please confirm your new password"
			redirect(action: 'changePassword')
			return
		}

		if(securityService.encodePassword(user.userName, params.oldPassword) != user.password)
		{
			flash.error="The old password is not correct!"
			redirect(action:'changePassword')
			return
		}

		if(!params.newPassword.equals(params.confirmNewPassword))
		{
			flash.error="The new passworld and new confirmed password do not match!"
			redirect(action:'changePassword')
			return
		}

		if (params.newPassword.size() > 0)
		{
			user.password = securityService.encodePassword(user.userName, params.newPassword)
			user.confirmedPassword = securityService.encodePassword(user.userName, params.confirmNewPassword)
		}

		//Because we are using Acegi. When you get UserDomain: authenticateService.userDomain(),
		//the user object is actually locked. So we need to use another way around
		if (user.hasErrors() || !user.merge(flush:true))
		{
			securityService.getLoggedUser().properties = user.peroperties
			render(view: 'changePassword', model: [user: user])
			return
		}

		flash.message = "Your password has been successfully changed."
		redirect(action:'index')
		return
	}

	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def editUserProfile =
	{
		def user = securityService.getLoggedUser()
		return [user: user]
	}

	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def handleEditUserProfile =
	{

		def user = securityService.getLoggedUser()
		user = User.get(user.id)

		user.properties = params

		if (user.hasErrors() || !user.merge())
		{
			securityService.getLoggedUser().properties = user.properties
			render(view: 'editUserProfile', model: [user: user])
			return
		}

		flash.message = "Your basic information has been successfuly updated"
		redirect(action: index)
		return
	}

	//Open list my group page
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def listGroups = {
		return [params:params]
	}
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def listGroupsAjax =
	{
		def groupList = userGroupService.getMyGroupsAsJSON(params)
		render groupList as JSON
		return
	}
	//List my tags
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def listTags = {

		//Yunjia: it's not valuable to display tag cloud for just one user because it's not very many
		//So we only return the tag
		def user = securityService.getLoggedUser()
		def tagList = [:]
		def resourcetagList = TagResource.findAllByOwner(user)
		resourcetagList.each{tag->
			String term = tag.content?.trim()
			if(term != null && term.length() > 0)
			{
				if(tagList.get(term) == null)
				{
					tagList.put(term, 1)
				}
				else
				{
					tagList[term] = ((int)tagList.get(term))+1
				}
			}
		}

		def tagArray = []

		tagList.sort().each{key,value->
			tagArray << ["text":key,"weight":value]
		}

		return [tags:tagArray]
	}
	
	//List all tags used by all users, return type: xml
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def listAllTags = {
	   
	   //Yunjia: it's not valuable to display tag cloud for just one user because it's not very many
	   //def user = securityService.getLoggedUser()
	   
	   def tagList = [:]
	   def resourcetagList = TagResource.list()
	   log.debug("tag list size:"+resourcetagList.size());
	   resourcetagList.each{tag->
		   String term = tag.content?.trim()
		   //log.debug("term:"+term)
		   if(term != null)
		   {
			   if(tagList.get(term) == null)
			   {
				   tagList.put(term, 1)
			   }
			   else
			   {
				   tagList[term] = ((int)tagList.get(term))+1
			   }
		   }
	   }
	   
	   log.debug("tag list size:"+tagList.size())
	   
	   def tagArray = []
	   tagList.each{key,value->
		   tagArray << ["text":key,"weight":value]
	   }
	  
	   /*return (contentType:"text/xml", encoding:"UTF-8")
	   {
			  synote(action:"listAllTags")
			  {
				   tags()
				   {
					   tagList.each{key,value->
						   tag{
							   text(key)
							   weight(value)
						   }
					   }
				   }
			   }
	   }*/
	   render tagArray as JSON
	   return
	}

	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def listResources = {
		return [params:params]	
	}
	
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def listMultimediaAjax = {
		def multimediaList = resourceService.getMyMultimediaAsJSON(params)
		render multimediaList as JSON
		return
	}
	
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def listSynmarkAjax = {
		def synmarkList = resourceService.getMySynmarksAsJSON(params)
		render synmarkList as JSON
		return	
	}
	
	
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def cancel =
	{ redirect(action:'index') }

	def termsAndConditions = {
		//Do nothing
	}

	def userToUser = {
		//Do nothing
	}

	def accessibility = {
		//Do nothing
	}
	
	def contact = {
		//Do nothing	
	}
	
	//Open the help page
	def help= {
		//Do nothing
	}
}
