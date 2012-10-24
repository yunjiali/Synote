package org.synote.user

import org.synote.user.UserRole
//import org.synote.user.admin.Requestmap
import grails.plugins.springsecurity.Secured

/**
 * UserRole Controller.
 */
@Secured(['ROLE_ADMIN'])
class UserRoleController {

	// the delete, save and update actions only accept POST requests
	static Map allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

	def authenticateService

	def index = {
		redirect action: list, params: params
	}

	/**
	 * Display the list authority page.
	 */
	def list = {
		if (!params.max) {
			params.max = 10
		}
		[authorityList: UserRole.list(params)]
	}

	/**
	 * Display the show authority page.
	 */
	def show = {
		def authority = UserRole.get(params.id)
		if (!authority) {
			flash.message = "UserRole not found with id $params.id"
			redirect action: list
			return
		}

		[authority: authority]
	}

	/**
	 * Delete an authority.
	 */
	def delete = {
		def authority = UserRole.get(params.id)
		if (!authority) {
			flash.message = "UserRole not found with id $params.id"
			redirect action: list
			return
		}

		springSecurityService.deleteRole(authority)

		flash.message = "UserRole $params.id deleted."
		redirect action: list
	}

	/**
	 * Display the edit authority page.
	 */
	def edit = {
		def authority = UserRole.get(params.id)
		if (!authority) {
			flash.message = "UserRole not found with id $params.id"
			redirect action: list
			return
		}

		[authority: authority]
	}

	/**
	 * Authority update action.
	 */
	def update = {

		def authority = UserRole.get(params.id)
		if (!authority) {
			flash.message = "UserRole not found with id $params.id"
			redirect action: edit, id: params.id
			return
		}

		long version = params.version.toLong()
		if (authority.version > version) {
			authority.errors.rejectValue 'version', 'authority.optimistic.locking.failure',
				'Another user has updated this UserRole while you were editing.'
			render view: 'edit', model: [authority: authority]
			return
		}

		if (springSecurityService.updateRole(authority, params)) {
			springSecurityService.clearCachedRequestmaps()
			redirect action: show, id: authority.id
		}
		else {
			render view: 'edit', model: [authority: authority]
		}
	}

	/**
	 * Display the create new authority page.
	 */
	def create = {
		[authority: new UserRole()]
	}

	/**
	 * Save a new authority.
	 */
	def save = {

		def authority = new UserRole()
		authority.properties = params
		if (authority.save()) {
			redirect action: show, id: authority.id
		}
		else {
			render view: 'create', model: [authority: authority]
		}
	}
}
