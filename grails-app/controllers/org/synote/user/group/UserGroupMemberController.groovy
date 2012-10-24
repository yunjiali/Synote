package org.synote.user.group

import grails.plugins.springsecurity.Secured

@Secured(['ROLE_ADMIN'])
class UserGroupMemberController {
	def scaffold = org.synote.user.group.UserGroupMember
}
